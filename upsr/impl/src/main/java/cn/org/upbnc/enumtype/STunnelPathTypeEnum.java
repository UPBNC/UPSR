package cn.org.upbnc.enumtype;

public enum STunnelPathTypeEnum {
    Primary(1,"primary"),
    HotStandby(2,"hotStandby");

    private int code;
    private String name;

    STunnelPathTypeEnum(int code , String name){
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
