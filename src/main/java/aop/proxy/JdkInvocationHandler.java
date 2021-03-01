package aop.proxy;

import aop.AspectDefinition;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import enums.AspectScope;
import ioc.BeanDefinitionRegistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Author: zerocoder
 * @Description:
 * @Date: 2021/2/28 16:31
 */

public class JdkInvocationHandler <T> implements InvocationHandler {

    private BeanDefinitionRegistry registry;

    private T target;

    private String beanId;

    public JdkInvocationHandler(T target, String beanId, BeanDefinitionRegistry registry) {
        this.registry = registry;
        this.target = target;
        this.beanId = beanId;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        String className = target.getClass().getName();
        className = StrUtil.subBefore(className, "$$", false);

//        String className  = AopUtils.getTargetClass(proxy).getName();
        boolean needProxy = registry.isNeedProxy(beanId, method.getName());
        try {
            // 前置通知
            if (needProxy){
                AspectDefinition aspectDefinition = registry.getAspectDefinition(AspectScope.BEFORE, className);
                if (aspectDefinition != null){
                    String definitionName = aspectDefinition.getName();
                    String beforeMethod = aspectDefinition.getBeforeMethod();
                    Object beforeInstance = ReflectUtil.newInstance(definitionName);
                    ReflectUtil.invoke(beforeInstance, beforeMethod);
                }
            }
            // 调用目标方法
            result = method.invoke(target, args);
        }finally {
            // 后置通知
            if (needProxy){
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
//    @Override
//    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        Object result = null;
//        try {
//            try {
//                LogUtil.logBefore(method.getName(), args);
//                // 调用目标方法
//                result = method.invoke(target, args);
//            } finally {
//                // 后置通知
//                LogUtil.logAfter(method.getName(), args);
//            }
//            // 返回通知
//            LogUtil.logAfterReturning(method.getName(), result);
//
//        } catch (Throwable e) {
//            ogUtil.logAfterThrowing(method.getName(), e);
//            throw e;
//        }
//
//        return result;
//    }
}
