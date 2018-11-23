package cn.org.upbnc.enumtype;

public enum AddressTypeEnum {
    V4(4,"V4 Address"),
    V6(6,"V6 Address"),
    MAC(8,"Mac Address");


    private int code;
    private String name;

    AddressTypeEnum(int code ,String name){
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
