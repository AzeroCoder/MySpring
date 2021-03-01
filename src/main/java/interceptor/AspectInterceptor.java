package interceptor;

import ioc.BeanDefinitionRegistry;
import ioc.BeanFactory;
import ioc.definition.BeanDefinition;

/**
 * @Author: zerocoder
 * @Description: 切面拦截器
 * @Date: 2021/2/28 14:34
 */

public class AspectInterceptor implements BaseBeanInitInterceptor {

    private BeanFactory beanFactory;

    public AspectInterceptor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void process(String beanId) {
    }


}
