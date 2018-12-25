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
                             List<NetworkSeg> networkSegList) {
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
}
