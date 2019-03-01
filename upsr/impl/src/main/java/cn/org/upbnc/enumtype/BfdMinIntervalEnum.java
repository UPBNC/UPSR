package cn.org.upbnc.enumtype;

public enum BfdMinIntervalEnum {
    TunnelMinTX(1, "300"),
    TunnelMinRX(2, "300"),
    LSPMinTX(3, "200"),
    LSPMinRX(4,"200");


    private int code;
    private String name;

    BfdMinIntervalEnum(int code ,String name){
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
