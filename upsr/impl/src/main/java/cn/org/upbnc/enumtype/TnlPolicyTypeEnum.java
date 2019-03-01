package cn.org.upbnc.enumtype;

public enum  TnlPolicyTypeEnum {
    Invalid(0,"invalid"),
    TnlBinding(1,"tnlBinding"),
    TnlSelectSeq(2,"tnlSelectSeq");

    private int code;
    private String name;

    TnlPolicyTypeEnum(int code , String name){
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
