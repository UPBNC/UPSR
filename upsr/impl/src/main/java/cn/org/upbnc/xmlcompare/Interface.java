package cn.org.upbnc.xmlcompare;

public class Interface {
    String ifName;
    String addrCfgType;
    String unNumIfName;
    String ifIpAddr;
    String subnetMask;
    String addrType;

    @Override
    public String toString() {
        return "Interface{" +
                "ifName='" + ifName + '\'' +
                ", addrCfgType='" + addrCfgType + '\'' +
                ", unNumIfName='" + unNumIfName + '\'' +
                ", ifIpAddr='" + ifIpAddr + '\'' +
                ", subnetMask='" + subnetMask + '\'' +
                ", addrType='" + addrType + '\'' +
                '}';
    }

    public String getIfName() {
        return ifName;
    }

    public void setIfName(String ifName) {
        this.ifName = ifName;
    }

    public String getAddrCfgType() {
        return addrCfgType;
    }

    public void setAddrCfgType(String addrCfgType) {
        this.addrCfgType = addrCfgType;
    }

    public String getUnNumIfName() {
        return unNumIfName;
    }

    public void setUnNumIfName(String unNumIfName) {
        this.unNumIfName = unNumIfName;
    }

    public String getIfIpAddr() {
        return ifIpAddr;
    }

    public void setIfIpAddr(String ifIpAddr) {
        this.ifIpAddr = ifIpAddr;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }

    public String getAddrType() {
        return addrType;
    }

    public void setAddrType(String addrType) {
        this.addrType = addrType;
    }
}
