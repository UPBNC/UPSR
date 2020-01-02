package cn.org.upbnc.util.netconf;

import java.util.List;

public class SSrTeTunnel {
    private String tunnelName;
    private String tunnelDesc;
    private String mplsTunnelEgressLSRId;
    private String mplsTunnelIndex;
    private String mplsTeTunnelSetupPriority = "7";
    private String holdPriority = "7";
    private String mplsTunnelBandwidth;
    private String mplsTeTunnelBfdMinTx;
    private String mplsTeTunnelBfdMinnRx;
    private String mplsTeTunnelBfdDetectMultiplier;
    private String mplsTeTunnelBfdEnable;
    private List<SSrTeTunnelPath> srTeTunnelPaths;
    private STunnelServiceClass mplsteServiceClass;
    private String unNumIfName = "LoopBack0";
    private String addrCfgType = "unnumbered";

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

    public String getMplsTeTunnelBfdDetectMultiplier() {
        return mplsTeTunnelBfdDetectMultiplier;
    }

    public void setMplsTeTunnelBfdDetectMultiplier(String mplsTeTunnelBfdDetectMultiplier) {
        this.mplsTeTunnelBfdDetectMultiplier = mplsTeTunnelBfdDetectMultiplier;
    }

    public List<SSrTeTunnelPath> getSrTeTunnelPaths() {
        return srTeTunnelPaths;
    }

    public void setSrTeTunnelPaths(List<SSrTeTunnelPath> srTeTunnelPaths) {
        this.srTeTunnelPaths = srTeTunnelPaths;
    }

    public STunnelServiceClass getMplsteServiceClass() {
        return mplsteServiceClass;
    }

    public void setMplsteServiceClass(STunnelServiceClass mplsteServiceClass) {
        this.mplsteServiceClass = mplsteServiceClass;
    }

    public String getMplsTeTunnelBfdEnable() {
        return mplsTeTunnelBfdEnable;
    }

    public void setMplsTeTunnelBfdEnable(String mplsTeTunnelBfdEnable) {
        this.mplsTeTunnelBfdEnable = mplsTeTunnelBfdEnable;
    }

    public String getTunnelDesc() {
        return tunnelDesc;
    }

    public void setTunnelDesc(String tunnelDesc) {
        this.tunnelDesc = tunnelDesc;
    }

    @Override
    public String toString() {
        return "SSrTeTunnel{" +
                "tunnelName='" + tunnelName + '\'' +
                ", tunnelDesc='" + tunnelDesc + '\'' +
                ", mplsTunnelEgressLSRId='" + mplsTunnelEgressLSRId + '\'' +
                ", mplsTunnelIndex='" + mplsTunnelIndex + '\'' +
                ", mplsTeTunnelSetupPriority='" + mplsTeTunnelSetupPriority + '\'' +
                ", holdPriority='" + holdPriority + '\'' +
                ", mplsTunnelBandwidth='" + mplsTunnelBandwidth + '\'' +
                ", mplsTeTunnelBfdMinTx='" + mplsTeTunnelBfdMinTx + '\'' +
                ", mplsTeTunnelBfdMinnRx='" + mplsTeTunnelBfdMinnRx + '\'' +
                ", mplsTeTunnelBfdDetectMultiplier='" + mplsTeTunnelBfdDetectMultiplier + '\'' +
                ", mplsTeTunnelBfdEnable='" + mplsTeTunnelBfdEnable + '\'' +
                ", srTeTunnelPaths=" + srTeTunnelPaths +
                ", mplsteServiceClass=" + mplsteServiceClass +
                ", unNumIfName='" + unNumIfName + '\'' +
                ", addrCfgType='" + addrCfgType + '\'' +
                '}';
    }
}
