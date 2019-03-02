package cn.org.upbnc.enumtype;

public enum  LabelTypeEnum {
    ADJACENCY(1,"adjacency"),
    PREFIX(2,"prefix");

    private int code;
    private String name;

    LabelTypeEnum(int code ,String name){
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
