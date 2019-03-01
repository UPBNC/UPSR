package cn.org.upbnc.enumtype;

public enum TunnelServiceClassEnum {
    AF1(1, "af1"),
    AF2(2, "af2"),
    AF3(3, "af3"),
    EF(6,"ef");


    private int code;
    private String name;

    TunnelServiceClassEnum(int code ,String name){
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
