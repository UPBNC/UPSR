package cn.org.upbnc.enumtype;

public enum ServiceStatusEnum {
    INIT(0,"Init"),
    READY(1,"Finish"),
    STARTING(2,"Starting");


    private int code;
    private String name;

    ServiceStatusEnum(int code , String name){
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
