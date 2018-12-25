package cn.org.upbnc.enumtype;

public enum ResponseEnum {
    CODE("code"), MESSAGE("message"), BODY("body");
    private String name;

    ResponseEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
