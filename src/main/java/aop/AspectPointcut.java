package aop;

import lombok.Data;

/**
 * @Author: zerocoder
 * @Description: 切点
 * @Date: 2021/2/28 14:57
 */
@Data
public class AspectPointcut {

    private String id;

    private String expression;

}
