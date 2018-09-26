package cn.org.upbnc.util.netconf;

import java.util.List;

public class L3vpnInstance {
    private String vrfName;
    private String vrfDescription;
    private String vrfRD;
    private String vrfRTValue;
    private List<L3vpnIf> l3vpnIfs;

    public String getVrfName() {
        return vrfName;
    }

    public void setVrfName(String vrfName) {
        this.vrfName = vrfName;
    }

    public String getVrfDescription() {
        return vrfDescription;
    }

    public void setVrfDescription(String vrfDescription) {
        this.vrfDescription = vrfDescription;
    }

    public String getVrfRD() {
        return vrfRD;
    }

    public void setVrfRD(String vrfRD) {
        this.vrfRD = vrfRD;
    }

    public String getVrfRTValue() {
        return vrfRTValue;
    }

    public void setVrfRTValue(String vrfRTValue) {
        this.vrfRTValue = vrfRTValue;
    }

    public List<L3vpnIf> getL3vpnIfs() {
        return l3vpnIfs;
    }

    public void setL3vpnIfs(List<L3vpnIf> l3vpnIfs) {
        this.l3vpnIfs = l3vpnIfs;
    }
}
