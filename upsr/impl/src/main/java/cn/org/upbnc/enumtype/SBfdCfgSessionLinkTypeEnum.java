package cn.org.upbnc.enumtype;

public enum SBfdCfgSessionLinkTypeEnum {
    Tunnel(3,"TE_TUNNEL"),
    Master(4,"TE_LSP");

    private int code;
    private String name;

    SBfdCfgSessionLinkTypeEnum(int code , String name){
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
