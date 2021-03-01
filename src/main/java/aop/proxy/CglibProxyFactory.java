package aop.proxy;

import ioc.BeanDefinitionRegistry;
import net.sf.cglib.proxy.Enhancer;

/**
 * @Author: zerocoder
 * @Description: CGLIB代理
 * @Date: 2021/2/28 16:33
 */

public class CglibProxyFactory {
    public static <T> T createProxy(T target, String beanId, BeanDefinitionRegistry registry) {
        // JDK动态代理，是根据 目标对象的接口去生成一个实现类。

        // Cglib增强工具类
        // Cglib动态代理。是根据目标对象本身，生成一个子类。
        Enhancer enhancer = new Enhancer();
        // 设置目标对象的类型
        enhancer.setSuperclass(target.getClass());
        // 设置用来拦截目标方法的接口
        enhancer.setCallback(new CglibMethodInterceptor(beanId, registry));
        // create生成代理对象
        return (T) enhancer.create();
    }
}
