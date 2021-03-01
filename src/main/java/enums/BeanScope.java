package enums;

public enum BeanScope {
    SINGLTON("singleton"),
    PROTOTYPE("prototype");

    private String desc;

    BeanScope(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
