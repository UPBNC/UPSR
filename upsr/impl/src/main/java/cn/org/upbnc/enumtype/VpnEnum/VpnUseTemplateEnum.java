package cn.org.upbnc.enumtype.VpnEnum;

public enum VpnUseTemplateEnum {
    ENABLE(1,"1"),
    DISABLE(2,"2");
    private int code;
    private String name;

    VpnUseTemplateEnum(int code , String name){
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
