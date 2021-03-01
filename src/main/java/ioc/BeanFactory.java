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
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: zerocoder
 * @Description: IOC对象工厂
 * @Date: 2020/9/13 18:16
 */
@Data
public class BeanFactory {

    private final BeanDefinitionRegistry beanDefinitionRegistry;

    private final Map<String, Object> objectPool;

    private final List<BaseBeanInitInterceptor> interceptors;

    public BeanFactory(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
        objectPool = new ConcurrentHashMap<>();
        interceptors = new ArrayList<>();
        interceptors.add(new AspectInterceptor(this));
    }

    /**
     * 构造对象
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
            List<BeanProperties> properties = beanDefinition.getProperties();
            String className = beanDefinition.getClassName();
            Class<T> beanClass = (Class<T>) Class.forName(className);
            // 多实例
            if (beanDefinition.getScop().equals(BeanScope.PROTOTYPE.getDesc())) {
                bean = ReflectUtil.newInstance(beanClass);
                initProperties(bean, properties);
            }
            // 单例
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
                initProperties(bean, properties);
                objectPool.put(className, bean);
            }
            return bean;
        } catch (Exception e) {
            throw e;
        }
    }



    private <T> void initProperties(T bean, List<BeanProperties> properties) throws Exception {
        for (BeanProperties beanProperties: properties) {
            String fieldName = beanProperties.getName();
            Field field = ReflectUtil.getField(bean.getClass(), fieldName);
            if (ArrayUtil.contains(Constant.types, field.getType())){
                ReflectUtil.setFieldValue(bean, fieldName, beanProperties.getValue());
            }else {
                ReflectUtil.setFieldValue(bean, fieldName, getBean(beanProperties.getRef()));
            }
        }
    }

    private boolean needProxy(String beanId) {
        return beanDefinitionRegistry.isNeedProxy(beanId);
    }
}
