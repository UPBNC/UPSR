package cn.org.upbnc.util.netconf;

public class SBfdCfgSession {
    private String sessName;
    private String minTxInt;
    private String minRxInt;
    private String linkType;
    private String tunnelName;
    private String createType;
    private String localDiscr;
    private String remoteDiscr;

    public String getSessName() {
        return sessName;
    }

    public void setSessName(String sessName) {
        this.sessName = sessName;
    }

    public String getTunnelName() {
        return tunnelName;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }

    public String getMinRxInt() {
        return minRxInt;
    }

    public void setMinRxInt(String minRxInt) {
        this.minRxInt = minRxInt;
    }

    public String getMinTxInt() {
        return minTxInt;
    }

    public void setMinTxInt(String minTxInt) {
        this.minTxInt = minTxInt;
    }

    public String getCreateType() {
        return createType;
    }

    public void setCreateType(String createType) {
        this.createType = createType;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getLocalDiscr() {
        return localDiscr;
    }

    public void setLocalDiscr(String localDiscr) {
        this.localDiscr = localDiscr;
    }

    public String getRemoteDiscr() {
        return remoteDiscr;
    }

    public void setRemoteDiscr(String remoteDiscr) {
        this.remoteDiscr = remoteDiscr;
    }
}
