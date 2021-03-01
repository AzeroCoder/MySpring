package ioc;

import aop.proxy.CglibProxyFactory;
import aop.proxy.JdkProxyFactory;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import interceptor.AspectInterceptor;
import interceptor.BaseBeanInitInterceptor;
import ioc.definition.BeanDefinition;
import enums.BeanScope;
import ioc.definition.BeanProperties;
import lombok.Data;
import util.Constant;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: zerocoder
 * @Description: IOC对象工厂
 * @Date: 2020/9/13 18:16
 */
@Data
public class BeanFactory {

    private final BeanDefinitionRegistry beanDefinitionRegistry;
    private final BeanInitializer initializer;

    // 一级缓存
    private final Map<String, Object> objectPool;

    // 二级缓存（半成品，避免bean没有加载）
    private final Map<String, Object> earlyObjectPool;

    // 三级缓存（加载过的所有bean，避免循环依赖）
    private List<String> objectTags;

    private final List<BaseBeanInitInterceptor> interceptors;

    public BeanFactory(BeanInitializer beanInitializer) {
        initializer = beanInitializer;
        this.beanDefinitionRegistry = beanInitializer.getRegistry();
        objectPool = new ConcurrentHashMap<>();
        earlyObjectPool = new ConcurrentHashMap<>();
        objectTags = new ArrayList<>();
        interceptors = new ArrayList<>();
        interceptors.add(new AspectInterceptor(this));
    }

    /**
     * 构造对象
     * 如果是第一次加载，并且是多实例，就创建个新的，否则的话可以从池里拿
     * 如果是创建的时候，依赖的bean没有加载，那么先放入二级缓存中，待之后修复
     * 如果是创建的时候，依赖的bean有循环引用
     *
     * @param id
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T getBean(String id) throws Exception {
        try {
            T bean = null;
            BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(id);
            // 先不加载属性
            if (beanDefinition == null) {
                return null;
            }
            // 避免循环依赖
            if (objectTags.contains(id)) {
                return null;
            }
            objectTags.add(id);

            List<BeanProperties> properties = beanDefinition.getProperties();
            String className = beanDefinition.getClassName();
            if (isPresent(className)) {
                bean = (T) initializer.getContextObj();
                // 加载成功
                if (initProperties(bean, properties)) {
                    objectPool.put(className, bean);
                }
            } else {
                Class<T> beanClass = (Class<T>) Class.forName(className);
                // 多实例
                if (beanDefinition.getScop().equals(BeanScope.PROTOTYPE.getDesc())) {
                    bean = ReflectUtil.newInstance(beanClass);
                    initProperties(bean, properties);
                }
                // 单例
                // 第二次加载：直接加载属性吧
                else if (earlyObjectPool.containsKey(className)) {
                    bean = (T) earlyObjectPool.get(className);
                    initProperties(bean, properties);
                    earlyObjectPool.remove(className);
                    objectPool.put(className, bean);
                }
                // 第一次加载
                else if (objectPool.containsKey(className)) {
                    bean = (T) objectPool.get(className);
                } else {
                    bean = ReflectUtil.newInstance(beanClass);
                    // 动态代理
                    if (needProxy(id)) {
                        T proxy;
                        if (beanClass.isInterface()) {
                            proxy = JdkProxyFactory.createProxy(bean, id, beanDefinitionRegistry);
                        } else {
                            proxy = CglibProxyFactory.createProxy(bean, id, beanDefinitionRegistry);
                        }
                        bean = proxy;
                    }
                    // 加载成功
                    if (initProperties(bean, properties)) {
                        objectPool.put(className, bean);
                    }
                }
            }

            return bean;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 加载属性
     *
     * @param bean
     * @param properties
     * @param <T>
     * @return
     * @throws Exception
     */
    private <T> boolean initProperties(T bean, List<BeanProperties> properties) throws Exception {
        for (BeanProperties beanProperties : properties) {
            String fieldName = beanProperties.getName();
            Object fieldValue = ReflectUtil.getFieldValue(bean, fieldName);
            // 第一次加载已经赋值了
//            if (fieldValue != null){
//                continue;
//            }
            Field field = ReflectUtil.getField(bean.getClass(), fieldName);
            if (ArrayUtil.contains(Constant.types, field.getType())) {
                ReflectUtil.setFieldValue(bean, fieldName, beanProperties.getValue());
            } else {
                Object refBean = getBean(beanProperties.getRef());
                if (refBean == null) {
                    earlyObjectPool.put(bean.getClass().getName(), bean);
                    return false;
                }
                ReflectUtil.setFieldValue(bean, fieldName, refBean);
            }
        }
        return true;
    }

    /**
     * 是否需要代理
     *
     * @param beanId
     * @return
     */
    private boolean needProxy(String beanId) {
        return beanDefinitionRegistry.isNeedProxy(beanId);
    }

    public void clearTags() {
        objectTags.clear();
    }


    private boolean isPresent(String className) {
        Object contextObj = initializer.getContextObj();
        String contextName = contextObj.getClass().getName();
        return className.equals(contextName);
    }
}
