package aop;

import lombok.Data;

/**
 * @Author: zerocoder
 * @Description: 切面
 * @Date: 2021/2/28 14:58
 */
@Data
public class AspectDefinition {

    private String name;

    private String beforeMethod;

    private String beforePoint;

    private String aroundMethod;

    private String aroundPoint;

    private String afterMethod;

    private String afterPoint;
}
