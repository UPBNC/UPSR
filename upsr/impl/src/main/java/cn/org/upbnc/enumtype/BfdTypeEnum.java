package cn.org.upbnc.enumtype;

public enum BfdTypeEnum {
    Dynamic(1,"Dynamic"),
    Static(2,"Static"),
    Tunnel(3,"Tunnel"),
    Master(4,"Master"),
    Backup(5,"Backup");

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
