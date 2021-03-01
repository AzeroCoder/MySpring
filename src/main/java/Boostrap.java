import ioc.BeanInitializer;

/**
 * @Author: zerocoder
 * @Description:
 * @Date: 2021/2/28 11:10
 */

public class Boostrap {

    private static BeanInitializer beanLoader;

    public static void start(){
        beanLoader = new BeanInitializer();
    }

    public static  <T> T  getBean(String id){
        return beanLoader.getBean(id);
    }
}
