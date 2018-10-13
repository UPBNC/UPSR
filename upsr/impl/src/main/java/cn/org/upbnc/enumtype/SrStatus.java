package cn.org.upbnc.enumtype;

public enum SrStatus {
    ENABLED(1,"1"),
    DISENABLED(2,"2");
    private int code;
    private String name;

    SrStatus(int code , String name){
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
