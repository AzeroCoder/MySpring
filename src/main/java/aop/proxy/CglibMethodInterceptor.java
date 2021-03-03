package aop.proxy;

import aop.definition.AspectDefinition;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import enums.AspectScope;
import ioc.BeanDefinitionRegistry;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @Author: zerocoder
 * @Description:
 * @Date: 2021/2/28 16:34
 */

public class CglibMethodInterceptor implements MethodInterceptor {

    private BeanDefinitionRegistry registry;

    private String beanId;

    public CglibMethodInterceptor(String beanId, BeanDefinitionRegistry registry) {
        this.registry = registry;
        this.beanId = beanId;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Object result = null;
        String className = o.getClass().getName();
        className = StrUtil.subBefore(className, "$$", false);
//        String className  = AopTargetUtils.getCglibProxyTargetObject(o).getClass().getSimpleName();
        boolean needProxy = registry.isNeedProxy(beanId, method.getName());
        try {
            if (needProxy){
                // 前置通知
                AspectDefinition aspectDefinition = registry.getAspectDefinition(AspectScope.BEFORE, className);
                if (aspectDefinition != null){
                    String definitionName = aspectDefinition.getName();
                    String beforeMethod = aspectDefinition.getBeforeMethod();
                    Object beforeInstance = ReflectUtil.newInstance(definitionName);
                    ReflectUtil.invoke(beforeInstance, beforeMethod);
                }
            }
            // 调用目标方法
            result = methodProxy.invokeSuper(o, objects);
        }finally {
            if (needProxy){
                // 后置通知
                AspectDefinition aspectDefinition = registry.getAspectDefinition(AspectScope.AFTER, className);
                if (aspectDefinition != null){
                    String definitionName = aspectDefinition.getName();
                    String afterMethod = aspectDefinition.getAfterMethod();
                    Object beforeInstance = ReflectUtil.newInstance(definitionName);
                    ReflectUtil.invoke(beforeInstance, afterMethod);
                }
            }
        }
        return result;
    }
}
