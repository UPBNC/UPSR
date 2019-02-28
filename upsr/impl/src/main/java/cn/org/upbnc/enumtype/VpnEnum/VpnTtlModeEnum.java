package cn.org.upbnc.enumtype.VpnEnum;

public enum VpnTtlModeEnum {
    PIPE(1,"pipe"),
    UNIFORM(2,"uniform");
    private int code;
    private String name;

    VpnTtlModeEnum(int code , String name){
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
