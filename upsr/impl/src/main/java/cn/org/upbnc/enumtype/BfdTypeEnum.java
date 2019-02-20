package cn.org.upbnc.enumtype;

public enum BfdTypeEnum {
    Dynamic(0,"Dynamic"),
    Tunnel(1,"Tunnel"),
    Master(2,"Master"),
    Backup(3,"Backup");

    private int code;
    private String name;

    BfdTypeEnum(int code , String name){
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
