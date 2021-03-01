package enums;

/**
 * @Author: zerocoder
 * @Description: 切面位置
 * @Date: 2021/2/28 18:00
 */

public enum  AspectScope {
    BEFORE("before"),
    AROUND("around"),
    AFTER("after");

    private String desc;

    AspectScope(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
