package cn.org.upbnc.enumtype.error;

public enum ErrorCodeEnum {
    SRUNDEF(-1,-1,"Undefined"),
    SR0000(0,0,"OK"),
    SR0001(1,1,"xxx"),
    SRO002(2,2,"zz");

    private int eCode;
    private int uCode;
    private String uDesc;

    ErrorCodeEnum(int eCode,int uCode,String uDesc) {
        this.uCode = uCode;
        this.eCode = eCode;
        this.uDesc = uDesc;
    }

    public int geteCode() {
        return eCode;
    }

    public int getuCode() {
        return uCode;
    }

    public String getuDesc() {
        return uDesc;
    }
}
