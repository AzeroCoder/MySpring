package aspect;

/**
 * @Author: zerocoder
 * @Description:
 * @Date: 2021/2/28 17:33
 */

public class LogAspect {
    public static void before(){
        System.out.println("before-----打印日志");
    }
    public static void after(){
        System.out.println("after-----打印日志");
    }
}
