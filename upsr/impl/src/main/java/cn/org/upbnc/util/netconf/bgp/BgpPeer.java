package cn.org.upbnc.util.netconf.bgp;

public class BgpPeer {
    private String peerAddr;
    private String remoteAs;

    public void setRemoteAs(String remoteAs) {
        this.remoteAs = remoteAs;
    }

    public void setPeerAddr(String peerAddr) {
        this.peerAddr = peerAddr;
    }

    public String getRemoteAs() {
        return remoteAs;
    }

    public String getPeerAddr() {
        return peerAddr;
    }
    public BgpPeer(String peerAddr, String remoteAs){
        this.peerAddr=peerAddr;
        this.remoteAs=remoteAs;
    }
}
