package annotation;

import java.lang.annotation.*;

/**
 * @Author: zerocoder
 * @Description: 注入对象
 * @Date: 2021/2/28 22:55
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Inject {
    String name() default "";
}
