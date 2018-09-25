package cn.org.upbnc.util.netconf;

public class L3vpnIf {
    private String ifName;
    private String ipv4Addr;
    private String subnetMask;

    public String getIfName() {
        return ifName;
    }

    public void setIfName(String ifName) {
        this.ifName = ifName;
    }

    public String getIpv4Addr() {
        return ipv4Addr;
    }

    public void setIpv4Addr(String ipv4Addr) {
        this.ipv4Addr = ipv4Addr;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }
}
