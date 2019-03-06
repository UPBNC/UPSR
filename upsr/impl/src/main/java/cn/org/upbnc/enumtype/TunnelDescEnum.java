package cn.org.upbnc.enumtype;

public enum TunnelDescEnum {
    End(1,"Create_By_UPSR"),
    TunnelBegin(2,"Tunnel"),
    VPNBegin(3,"VPN");

    private int code;
    private String name;

    TunnelDescEnum(int code ,String name){
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
