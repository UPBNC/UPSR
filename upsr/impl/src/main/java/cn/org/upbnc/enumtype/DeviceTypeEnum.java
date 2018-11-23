package cn.org.upbnc.enumtype;

public enum DeviceTypeEnum {
    UNDEFINED(0,"未定义"),
    ROUTER(1,"路由器"),
    OTHER(10,"其他");

    private int code;
    private String name;

    DeviceTypeEnum(int code ,String name){
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
