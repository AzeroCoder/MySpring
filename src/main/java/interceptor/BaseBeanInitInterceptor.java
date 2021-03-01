package interceptor;

/**
 * @Author: zerocoder
 * @Description: 拦截器
 * @Date: 2021/2/28 14:33
 */

public interface BaseBeanInitInterceptor {
    void process(String beanId);
}
