package cn.org.upbnc.enumtype;

public enum  LabelTypeEnum {
    Empty(0,""),
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

    public static Integer nameToCode(String name) {
        if (ADJACENCY.name.equals(name)) {
            return new Integer(ADJACENCY.code);
        } else if (PREFIX.name.equals(name)) {
            return new Integer(PREFIX.code);
        } else {
            return new Integer(Empty.code);
        }
    }
}
