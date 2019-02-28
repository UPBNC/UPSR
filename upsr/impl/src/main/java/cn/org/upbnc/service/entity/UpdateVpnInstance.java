package cn.org.upbnc.service.entity;

import cn.org.upbnc.entity.Address;
import cn.org.upbnc.entity.DeviceInterface;
import cn.org.upbnc.entity.NetworkSeg;

import java.util.List;

public class UpdateVpnInstance {
    private String vpnName;
    private String routerId;
    private String businessRegion;
    private String rd;
    private String importRT;
    private String exportRT;
    private Integer peerAS;
    private Address peerIP;
    private Integer routeSelectDelay;
    private Integer importDirectRouteEnable;
    private List<DeviceInterface> deviceInterfaceList;
    private List<NetworkSeg> networkSegList;
    private String tunnelPolicy;
    private String vpnFrr;
    private String applyLabel;
    private String ttlMode;
    private String routerImportPolicy;
    private String routerExportPolicy;
    private String ebgpPreference;
    private String ibgpPreference;
    private String localPreference;
    private String advertiseCommunity;
    private String note;


    public UpdateVpnInstance() {
    }

    public UpdateVpnInstance(String vpnName,
                             String routerId,
                             String businessRegion,
                             String rd,
                             String importRT,
                             String exportRT,
                             Integer peerAS,
                             Address peerIP,
                             Integer routeSelectDelay,
                             Integer importDirectRouteEnable,
                             List<DeviceInterface> deviceInterfaceList,
                             List<NetworkSeg> networkSegList,
                             String tunnelPolicy,
                             String vpnFrr,
                             String applyLabel,
                             String ttlMode,
                             String routerImportPolicy,
                             String routerExportPolicy,
                             String ebgpPreference,
                             String ibgpPreference,
                             String localPreference,
                             String advertiseCommunity,
                             String note) {
        this.vpnName = vpnName;
        this.routerId = routerId;
        this.businessRegion = businessRegion;
        this.rd = rd;
        this.importRT = importRT;
        this.exportRT = exportRT;
        this.peerAS = peerAS;
        this.peerIP = peerIP;
        this.routeSelectDelay = routeSelectDelay;
        this.importDirectRouteEnable = importDirectRouteEnable;
        this.deviceInterfaceList = deviceInterfaceList;
        this.networkSegList = networkSegList;
        this.tunnelPolicy=tunnelPolicy;
        this.vpnFrr = vpnFrr;
        this.applyLabel = applyLabel;
        this.ttlMode = ttlMode;
        this.routerImportPolicy = routerImportPolicy;
        this.routerExportPolicy = routerExportPolicy;
        this.ebgpPreference = ebgpPreference;
        this.ibgpPreference = ibgpPreference;
        this.localPreference = localPreference;
        this.advertiseCommunity = advertiseCommunity;
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getVpnName() {
        return vpnName;
    }

    public void setVpnName(String vpnName) {
        this.vpnName = vpnName;
    }

    public String getRouterId() {
        return routerId;
    }

    public void setRouterId(String routerId) {
        this.routerId = routerId;
    }

    public String getBusinessRegion() {
        return businessRegion;
    }

    public void setBusinessRegion(String businessRegion) {
        this.businessRegion = businessRegion;
    }

    public String getRd() {
        return rd;
    }

    public void setRd(String rd) {
        this.rd = rd;
    }

    public String getImportRT() {
        return importRT;
    }

    public void setImportRT(String importRT) {
        this.importRT = importRT;
    }

    public String getExportRT() {
        return exportRT;
    }

    public void setExportRT(String exportRT) {
        this.exportRT = exportRT;
    }

    public Integer getPeerAS() {
        return peerAS;
    }

    public void setPeerAS(Integer peerAS) {
        this.peerAS = peerAS;
    }

    public Address getPeerIP() {
        return peerIP;
    }

    public void setPeerIP(Address peerIP) {
        this.peerIP = peerIP;
    }

    public Integer getRouteSelectDelay() {
        return routeSelectDelay;
    }

    public void setRouteSelectDelay(Integer routeSelectDelay) {
        this.routeSelectDelay = routeSelectDelay;
    }

    public Integer getImportDirectRouteEnable() {
        return importDirectRouteEnable;
    }

    public void setImportDirectRouteEnable(Integer importDirectRouteEnable) {
        this.importDirectRouteEnable = importDirectRouteEnable;
    }

    public List<DeviceInterface> getDeviceInterfaceList() {
        return deviceInterfaceList;
    }

    public void setDeviceInterfaceList(List<DeviceInterface> deviceInterfaceList) {
        this.deviceInterfaceList = deviceInterfaceList;
    }

    public List<NetworkSeg> getNetworkSegList() {
        return networkSegList;
    }

    public void setNetworkSegList(List<NetworkSeg> networkSegList) {
        this.networkSegList = networkSegList;
    }

    public String getTunnelPolicy() {
        return tunnelPolicy;
    }

    public void setTunnelPolicy(String tunnelPolicy) {
        this.tunnelPolicy = tunnelPolicy;
    }

    public String getVpnFrr() {
        return vpnFrr;
    }

    public void setVpnFrr(String vpnFrr) {
        this.vpnFrr = vpnFrr;
    }

    public String getApplyLabel() {
        return applyLabel;
    }

    public void setApplyLabel(String applyLabel) {
        this.applyLabel = applyLabel;
    }

    public String getTtlMode() {
        return ttlMode;
    }

    public void setTtlMode(String ttlMode) {
        this.ttlMode = ttlMode;
    }

    public String getRouterImportPolicy() {
        return routerImportPolicy;
    }

    public void setRouterImportPolicy(String routerImportPolicy) {
        this.routerImportPolicy = routerImportPolicy;
    }

    public String getRouterExportPolicy() {
        return routerExportPolicy;
    }

    public void setRouterExportPolicy(String routerExportPolicy) {
        this.routerExportPolicy = routerExportPolicy;
    }

    public String getEbgpPreference() {
        return ebgpPreference;
    }

    public void setEbgpPreference(String ebgpPreference) {
        this.ebgpPreference = ebgpPreference;
    }

    public String getIbgpPreference() {
        return ibgpPreference;
    }

    public void setIbgpPreference(String ibgpPreference) {
        this.ibgpPreference = ibgpPreference;
    }

    public String getLocalPreference() {
        return localPreference;
    }

    public void setLocalPreference(String localPreference) {
        this.localPreference = localPreference;
    }

    public String getAdvertiseCommunity() {
        return advertiseCommunity;
    }

    public void setAdvertiseCommunity(String advertiseCommunity) {
        this.advertiseCommunity = advertiseCommunity;
    }
}
