package cn.org.upbnc.enumtype;

public enum TunnelServiceClassEnum {
    Empty(0,""),
    DEF(1,"default"),
    BE(2,"be"),
    AF1(3, "af1"),
    AF2(4, "af2"),
    AF3(5, "af3"),
    AF4(6, "af4"),
    EF(7,"ef"),
    CS7(8, "cs6"),
    CS6(9, "cs7");


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

    public static Integer nameToCode(String name) {
        if (AF1.name.equals(name)) {
            return AF1.code;
        } else if (AF2.name.equals(name)) {
            return AF2.code;
        } else if (AF3.name.equals(name)){
            return AF3.code;
        } else if (EF.name.equals(name)) {
            return EF.code;
        } else {
            return Empty.code;
        }
    }
}
