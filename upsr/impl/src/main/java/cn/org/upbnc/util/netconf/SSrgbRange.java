package cn.org.upbnc.util.netconf;

public class SSrgbRange {
    String srgbBegin;
    String srgbEnd;

    @Override
    public String toString() {
        return "SSrgbRange{" +
                "srgbBegin='" + srgbBegin + '\'' +
                ", srgbEnd='" + srgbEnd + '\'' +
                '}';
    }

    public String getSrgbBegin() {
        return srgbBegin;
    }

    public void setSrgbBegin(String srgbBegin) {
        this.srgbBegin = srgbBegin;
    }

    public String getSrgbEnd() {
        return srgbEnd;
    }

    public void setSrgbEnd(String srgbEnd) {
        this.srgbEnd = srgbEnd;
    }
}
