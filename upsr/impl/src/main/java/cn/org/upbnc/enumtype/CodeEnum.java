package cn.org.upbnc.enumtype;

public enum CodeEnum {
    SUCCESS("1","success"), ERROR("0","error");
    private String name;
    private String message;

    CodeEnum(String name,String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
