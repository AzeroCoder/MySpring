package ioc.definition;

import lombok.Data;

/**
 * @Author: zerocoder
 * @Description: 构造参数
 * @Date: 2020/9/13 16:32
 */
@Data
public class ConstructorArg<T> {
    // 参数名
    private String name;
    // 参数类型
    private Class<T> type;
    // 参数值
    private T value;
    // 参数索引
    private int index;
}
