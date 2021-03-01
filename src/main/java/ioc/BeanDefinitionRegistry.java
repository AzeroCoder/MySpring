package ioc;

import aop.AspectDefinition;
import aop.AspectPointcut;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import enums.AspectScope;
import ioc.definition.BeanDefinition;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zerocoder
 * @Description: bean注册中心
 * @Date: 2020/9/13 16:19
 */
@Data
public class BeanDefinitionRegistry {

    /**
     * bean的名字和定义
     */
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    /**
     * 切点
     */
    private List<AspectPointcut> pointcutDefinitioList = new ArrayList<>();

    /**
     * 切点对应的 类及其方法
     */
    private Map<String, List<Method>> pointObjectMap = new HashMap<>();

    private Map<String, String> pointClassPointcut = new HashMap<>();
    /**
     * 切面
     */
    private List<AspectDefinition> aspectDefinitions = new ArrayList<>();

    /**
     * 切面对应的 切点及其方法
     */
    private Map<String, AspectDefinition> beforeAspects = new HashMap<>();

    private Map<String, AspectDefinition> aroundAspects = new HashMap<>();

    private Map<String, AspectDefinition> afterAspects = new HashMap<>();

    public void addAspect(AspectDefinition definition){
        aspectDefinitions.add(definition);
    }

    public void addPointcut(AspectPointcut pointcut){
        pointcutDefinitioList.add(pointcut);
    }

    public void register(String beanName, BeanDefinition beanDefinition) {
        if (isExists(beanName)) {
            return;
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    public void remove(String beanName) {
        if (!isExists(beanName)) {
            return;
        }
        beanDefinitionMap.remove(beanName);
    }


    public boolean isExists(String name) {
        return beanDefinitionMap.containsKey(name);
    }

    public BeanDefinition getBeanDefinition(String name) {
        if (!isExists(name)) {
            return null;
        }
        return beanDefinitionMap.get(name);
    }

    /**
     * 装载需要被代理的类
     * @throws ClassNotFoundException
     */
    public void parsePointcut() throws ClassNotFoundException {
        for (AspectPointcut pointcut : pointcutDefinitioList) {
            String[] expressions = StrUtil.subBetweenAll(pointcut.getExpression(), "[", "]");
            String packageName = expressions[0];
            String className = expressions[1];
            String methodName = expressions[2];
            Class clazz = Class.forName(packageName + "." + className);
            Method method = ReflectUtil.getMethod(clazz, methodName);
            List<Method> methods = pointObjectMap.getOrDefault(clazz.getName(), new ArrayList<>());
            methods.add(method);
            pointObjectMap.put(clazz.getName(), methods);
            pointClassPointcut.put(clazz.getName(), pointcut.getId());
        }
    }

    /**
     * 装载切点与切面
     */
    public void parseAspect(){
        for (AspectDefinition definition: aspectDefinitions) {
            if (StrUtil.isNotEmpty(definition.getBeforePoint())){
                beforeAspects.put(definition.getBeforePoint(), definition);
            }
            if (StrUtil.isNotEmpty(definition.getAroundPoint())){
                aroundAspects.put(definition.getAroundPoint(), definition);
            }
            if (StrUtil.isNotEmpty(definition.getAfterPoint())){
                afterAspects.put(definition.getAfterPoint(), definition);
            }
        }
    }

    /**
     * 是否需要动态代理
     * @param beanId
     * @return
     */
    public boolean isNeedProxy(String beanId){
        return isNeedProxy(beanId, null);
    }

    /**
     * 是否需要动态代理
     * @param beanId
     * @return
     */
    public boolean isNeedProxy(String beanId, String methodName){
        BeanDefinition beanDefinition = getBeanDefinition(beanId);
        String beanClass = beanDefinition.getClassName();
        boolean hasClass = pointObjectMap.containsKey(beanClass);
        if (!hasClass){
            return false;
        }
        if (StrUtil.isEmpty(methodName)){
            return true;
        }
        List<Method> methods = pointObjectMap.get(beanClass);
        for (Method method: methods) {
            if (method.getName().equals(methodName)){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * 获取切面对应的方法
     * @param aspectScope
     * @param className
     * @return
     */
    public AspectDefinition getAspectDefinition(AspectScope aspectScope, String className){
        String pointcutId = pointClassPointcut.get(className);
        if (aspectScope.getDesc().equals("before")){
            return beforeAspects.get(pointcutId);
        }
        else if (aspectScope.getDesc().equals("around")){
            return aroundAspects.get(pointcutId);
        }
        else if (aspectScope.getDesc().equals("after")){
            return afterAspects.get(pointcutId);
        }else {
            return null;
        }
    }
}
