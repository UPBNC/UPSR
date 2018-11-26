package cn.org.upbnc.enumtype;

public enum TopoStatusEnum {
    INIT(0,"Init"),
    FINISH(1,"Finish"),
    UPDATING(2,"Updating"),
    UPDATED(3,"Updated");

    private int code;
    private String name;

    TopoStatusEnum(int code ,String name){
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
