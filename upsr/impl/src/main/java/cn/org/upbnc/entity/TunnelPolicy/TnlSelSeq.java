package cn.org.upbnc.entity.TunnelPolicy;

public class TnlSelSeq {//Sequence in which different types of tunnels are selected. If the value is INVALID, no tunnel type has been configured

    private String tnlPolicyName;

    private int loadBalanceNum;//range "1..64;default "1";Sequence in which different types of tunnels are selected. The available tunnel types are CR-LSP, and LSP. LSP tunnels refer to LDP LSP tunnels here

    private String selTnlType1;

    private String selTnlType2;

    private String selTnlType3;

    private String selTnlType4;

    private String selTnlType5;

    private boolean unmix;
    public TnlSelSeq() {
        tnlPolicyName=null;
        loadBalanceNum=1;
        selTnlType1="invaild";
        selTnlType2="invaild";
        selTnlType3="invaild";
        selTnlType4="invaild";
        selTnlType5="invaild";
        unmix=false;
    }

    public TnlSelSeq(String tnlPolicyName,int loadBalanceNum, String selTnlType1, String selTnlType2,
                     String selTnlType3,
                           String selTnlType4, String selTnlType5, boolean unmix) {
        this.tnlPolicyName=tnlPolicyName;
        this.loadBalanceNum = loadBalanceNum;
        this.selTnlType1 = selTnlType1;
        this.selTnlType2 = selTnlType2;
        this.selTnlType3 = selTnlType3;
        this.selTnlType4 = selTnlType4;
        this.selTnlType5 = selTnlType5;
        this.unmix = unmix;
    }

    public String getTnlPolicyName() {
        return tnlPolicyName;
    }

    public void setTnlPolicyName(String tnlPolicyName) {
        this.tnlPolicyName = tnlPolicyName;
    }

    public int getLoadBalanceNum() {
        return loadBalanceNum;
    }

    public void setLoadBalanceNum(int loadBalanceNum) {
        this.loadBalanceNum = loadBalanceNum;
    }

    public String getSelTnlType1() {
        return selTnlType1;
    }

    public void setSelTnlType1(String selTnlType1) {
        this.selTnlType1 = selTnlType1;
    }

    public String getSelTnlType2() {
        return selTnlType2;
    }

    public void setSelTnlType2(String selTnlType2) {
        this.selTnlType2 = selTnlType2;
    }

    public String getSelTnlType3() {
        return selTnlType3;
    }

    public void setSelTnlType3(String selTnlType3) {
        this.selTnlType3 = selTnlType3;
    }

    public String getSelTnlType4() {
        return selTnlType4;
    }

    public void setSelTnlType4(String selTnlType4) {
        this.selTnlType4 = selTnlType4;
    }

    public String getSelTnlType5() {
        return selTnlType5;
    }

    public void setSelTnlType5(String selTnlType5) {
        this.selTnlType5 = selTnlType5;
    }

    public boolean isUnmix() {
        return unmix;
    }

    public void setUnmix(boolean unmix) {
        this.unmix = unmix;
    }
}
