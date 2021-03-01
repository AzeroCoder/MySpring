package ioc.definition;

import lombok.Data;

/**
 * @Author: zerocoder
 * @Description: 属性
 * @Date: 2020/9/13 16:42
 */
@Data
public class BeanProperties {
    private String name;
    private String value;
    private String ref;
}
