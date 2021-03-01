import ioc.BeanInitializer;

/**
 * @Author: zerocoder
 * @Description:
 * @Date: 2021/2/28 11:10
 */

public class Boostrap {

    private static BeanInitializer beanLoader;

    public static <T> void start(T obj){
        beanLoader = new BeanInitializer(obj);
    }

    public static  <T> T  getBean(String id){
        return (T) beanLoader.getBean(id);
    }
}
