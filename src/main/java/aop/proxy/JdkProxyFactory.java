package aop.proxy;

import ioc.BeanDefinitionRegistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @Author: zerocoder
 * @Description: JDK动态代理
 * @Date: 2021/2/28 16:31
 */

public class JdkProxyFactory {
    public static <T> T createProxy(T target, String beanId, BeanDefinitionRegistry registry) {
        // 给目标对象创建代理
        return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(), new JdkInvocationHandler(target, beanId, registry));
    }
}
