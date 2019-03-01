package cn.org.upbnc.entity.TunnelPolicy;

import java.util.ArrayList;
import java.util.List;

public class TpNexthop {//Rule for binding a TE tunnel to a destination address, so that the VPN traffic destined for
    // that destination address can be transmitted over the TE tunnel.

    private String tnlPolicyName;

    private String nexthopIPaddr;//KEY   Destination IP address to be bound to a tunnel

    private boolean downSwitch;//Enable tunnel switching. After this option is selected, if the bound TE tunnel is
    // unavailable, the system will select an available tunnel in the order of conventional LSPs, CR-LSPs, and
    // Local_IFNET tunnels.

    private boolean ignoreDestCheck;//Do not check whether the destination address of the TE tunnel matches the
    // destination address specified in the tunnel policy

    private boolean isIncludeLdp;//Is loadbalance with LDP

    private List<String> tpTunnels;//List of tunnels(tunnelName) available for an application,min-elements  "1";
    // max-elements  "16"

    public TpNexthop() {
        this.tnlPolicyName=null;
        this.nexthopIPaddr = null;
        this.downSwitch = false;
        this.ignoreDestCheck = false;
        this.isIncludeLdp = false;
        this.tpTunnels = new ArrayList<String>();
    }

    public TpNexthop(String tnlPolicyName,String nexthopIPaddr, boolean downSwitch, boolean ignoreDestCheck, boolean isIncludeLdp,
                     List<String> tpTunnels) {
        this.tnlPolicyName=tnlPolicyName;
        this.nexthopIPaddr = nexthopIPaddr;
        this.downSwitch = downSwitch;
        this.ignoreDestCheck = ignoreDestCheck;
        this.isIncludeLdp = isIncludeLdp;
        this.tpTunnels = tpTunnels;
    }

    public String getTnlPolicyName() {
        return tnlPolicyName;
    }

    public void setTnlPolicyName(String tnlPolicyName) {
        this.tnlPolicyName = tnlPolicyName;
    }

    public String getNexthopIPaddr() {
        return nexthopIPaddr;
    }

    public void setNexthopIPaddr(String nexthopIPaddr) {
        this.nexthopIPaddr = nexthopIPaddr;
    }

    public boolean isDownSwitch() {
        return downSwitch;
    }

    public void setDownSwitch(boolean downSwitch) {
        this.downSwitch = downSwitch;
    }

    public boolean isIgnoreDestCheck() {
        return ignoreDestCheck;
    }

    public void setIgnoreDestCheck(boolean ignoreDestCheck) {
        this.ignoreDestCheck = ignoreDestCheck;
    }

    public boolean isIncludeLdp() {
        return isIncludeLdp;
    }

    public void setIncludeLdp(boolean includeLdp) {
        isIncludeLdp = includeLdp;
    }

    public List<String> getTpTunnels() {
        return tpTunnels;
    }

    public void setTpTunnels(List<String> tpTunnels) {
        this.tpTunnels = tpTunnels;
    }

    public void addTpTunnels(String tpTunnels){this.tpTunnels.add(tpTunnels);}

}
