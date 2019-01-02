package cn.org.upbnc.util.netconf;

public class SSrTeTunnel {
    private String tunnelName;
    private String mplsTunnelEgressLSRId;
    private String mplsTunnelIndex;
    private String mplsTeTunnelSetupPriority;
    private String holdPriority;
    private String mplsTunnelBandwidth;
    private String mplsTeTunnelBfdMinTx;
    private String mplsTeTunnelBfdMinnRx;
    private String pathType;
    private String explicitPathName;
    private String unNumIfName;
    private String addrCfgType;

    public String getTunnelName() {
        return tunnelName;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }

    public String getMplsTunnelEgressLSRId() {
        return mplsTunnelEgressLSRId;
    }

    public void setMplsTunnelEgressLSRId(String mplsTunnelEgressLSRId) {
        this.mplsTunnelEgressLSRId = mplsTunnelEgressLSRId;
    }

    public String getMplsTunnelIndex() {
        return mplsTunnelIndex;
    }

    public void setMplsTunnelIndex(String mplsTunnelIndex) {
        this.mplsTunnelIndex = mplsTunnelIndex;
    }

    public String getMplsTeTunnelSetupPriority() {
        return mplsTeTunnelSetupPriority;
    }

    public void setMplsTeTunnelSetupPriority(String mplsTeTunnelSetupPriority) {
        this.mplsTeTunnelSetupPriority = mplsTeTunnelSetupPriority;
    }

    public String getHoldPriority() {
        return holdPriority;
    }

    public void setHoldPriority(String holdPriority) {
        this.holdPriority = holdPriority;
    }

    public String getMplsTunnelBandwidth() {
        return mplsTunnelBandwidth;
    }

    public void setMplsTunnelBandwidth(String mplsTunnelBandwidth) {
        this.mplsTunnelBandwidth = mplsTunnelBandwidth;
    }

    public String getMplsTeTunnelBfdMinTx() {
        return mplsTeTunnelBfdMinTx;
    }

    public void setMplsTeTunnelBfdMinTx(String mplsTeTunnelBfdMinTx) {
        this.mplsTeTunnelBfdMinTx = mplsTeTunnelBfdMinTx;
    }

    public String getMplsTeTunnelBfdMinnRx() {
        return mplsTeTunnelBfdMinnRx;
    }

    public void setMplsTeTunnelBfdMinnRx(String mplsTeTunnelBfdMinnRx) {
        this.mplsTeTunnelBfdMinnRx = mplsTeTunnelBfdMinnRx;
    }

    public String getPathType() {
        return pathType;
    }

    public void setPathType(String pathType) {
        this.pathType = pathType;
    }

    public String getExplicitPathName() {
        return explicitPathName;
    }

    public void setExplicitPathName(String explicitPathName) {
        this.explicitPathName = explicitPathName;
    }

    public String getUnNumIfName() {
        return unNumIfName;
    }

    public void setUnNumIfName(String unNumIfName) {
        this.unNumIfName = unNumIfName;
    }

    public String getAddrCfgType() {
        return addrCfgType;
    }

    public void setAddrCfgType(String addrCfgType) {
        this.addrCfgType = addrCfgType;
    }
}
