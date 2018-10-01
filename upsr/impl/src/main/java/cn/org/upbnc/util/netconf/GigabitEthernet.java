package cn.org.upbnc.util.netconf;

public class GigabitEthernet {
    private String ifName;
    private String ifIndex;
    private String ifPhyType;
    private String ifNumber;
    private String vrfName;
    private String ifClass;
    private String ifOperStatus;
    private String ifPhyStatus;
    private String ifLinkStatus;
    private String ifOperMac;
    private String ifIpAddr;
    private String subnetMask;

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

    public String getIfOperMac() {
        return ifOperMac;
    }

    public void setIfOperMac(String ifOperMac) {
        this.ifOperMac = ifOperMac;
    }

    public String getIfName() {
        return ifName;
    }

    public void setIfName(String ifName) {
        this.ifName = ifName;
    }

    public String getIfIndex() {
        return ifIndex;
    }

    public void setIfIndex(String ifIndex) {
        this.ifIndex = ifIndex;
    }

    public String getIfPhyType() {
        return ifPhyType;
    }

    public void setIfPhyType(String ifPhyType) {
        this.ifPhyType = ifPhyType;
    }

    public String getIfNumber() {
        return ifNumber;
    }

    public void setIfNumber(String ifNumber) {
        this.ifNumber = ifNumber;
    }

    public String getVrfName() {
        return vrfName;
    }

    public void setVrfName(String vrfName) {
        this.vrfName = vrfName;
    }

    public String getIfClass() {
        return ifClass;
    }

    public void setIfClass(String ifClass) {
        this.ifClass = ifClass;
    }

    public String getIfOperStatus() {
        return ifOperStatus;
    }

    public void setIfOperStatus(String ifOperStatus) {
        this.ifOperStatus = ifOperStatus;
    }

    public String getIfPhyStatus() {
        return ifPhyStatus;
    }

    public void setIfPhyStatus(String ifPhyStatus) {
        this.ifPhyStatus = ifPhyStatus;
    }

    public String getIfLinkStatus() {
        return ifLinkStatus;
    }

    public void setIfLinkStatus(String ifLinkStatus) {
        this.ifLinkStatus = ifLinkStatus;
    }
}
