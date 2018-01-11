package cn.org.upbnc.enumtype;

public enum CfgTypeEnum {
    SR_LABEL(1,"1"),
    SR_TUNNEL(2,"2"),
    VPN(3,"3");

    private int code;
    private String cfgType;

    CfgTypeEnum(int code , String cfgType){
        this.code = code;
        this.cfgType = cfgType;
    }

    public int getCode() {
        return code;
    }

    public String getCfgType() {
        return cfgType;
    }
}
