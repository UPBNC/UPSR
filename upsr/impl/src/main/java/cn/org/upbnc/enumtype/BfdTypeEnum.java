package cn.org.upbnc.enumtype;

public enum BfdTypeEnum {
    Empty(0,"Empty",""),
    Dynamic(1,"Dynamic","1"),
    Static(2,"Static","2"),
    Tunnel(3,"Tunnel","3"),
    Master(4,"Master","4"),
    Backup(5,"Backup","5");

    private int code;
    private String name;
    private String ui;

    public static final BfdTypeEnum[] values = BfdTypeEnum.values();

    BfdTypeEnum(int code , String name, String ui){
        this.code = code;
        this.name = name;
        this.ui = ui;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getUi(){
        return ui;
    }

    public static BfdTypeEnum valueOf(int i) {
        return values[i];
    }
}
