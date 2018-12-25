package cn.org.upbnc.enumtype;

public enum CodeEnum {
    SUCCESS("1"), ERROR("0");
    private String name;

    CodeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
