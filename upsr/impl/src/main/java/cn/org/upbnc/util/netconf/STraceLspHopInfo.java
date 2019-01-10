package cn.org.upbnc.util.netconf;

public class STraceLspHopInfo {
    String hopIndex;
    String dsIpAddr;
    String downStreamIpAddr;
    String type;

    public String getHopIndex() {
        return hopIndex;
    }

    public void setHopIndex(String hopIndex) {
        this.hopIndex = hopIndex;
    }

    public String getDsIpAddr() {
        return dsIpAddr;
    }

    public void setDsIpAddr(String dsIpAddr) {
        this.dsIpAddr = dsIpAddr;
    }

    public String getDownStreamIpAddr() {
        return downStreamIpAddr;
    }

    public void setDownStreamIpAddr(String downStreamIpAddr) {
        this.downStreamIpAddr = downStreamIpAddr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
