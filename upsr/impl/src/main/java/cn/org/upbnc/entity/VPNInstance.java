/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;
import java.util.ArrayList;
import java.util.List;
public class VPNInstance {
    private Integer id;
    private Device device;
    private List<DeviceInterface> deviceInterfaceList;
    private String vpnName;
    private String businessRegion;
    private String routerId;
    private String rd;
    private String importRT;
    private String exportRT;
    private Integer peerAS;
    private Address peerIP;
    private Integer routeSelectDelay;
    private Integer importDirectRouteEnable;
    private List<NetworkSeg> networkSegList;

    public VPNInstance() {
        this.id = 0;
        this.device = null;
        this.deviceInterfaceList = new ArrayList<DeviceInterface>();
        this.vpnName = null;
        this.businessRegion = null;
        this.routerId = null;
        this.rd = null;
        this.importRT = null;
        this.exportRT = null;
        this.peerAS = null;
        this.peerIP = null;
        this.routeSelectDelay = 0;
        this.importDirectRouteEnable = 0;
        this.networkSegList = new ArrayList<NetworkSeg>();
    }

    public VPNInstance(Integer id,
                       Device device,
                       List<DeviceInterface> deviceInterfaceList,
                       String vpnName,
                       String routerId,
                       String businessRegion,
                       String rd,
                       String importRT,
                       String exportRT,
                       Integer peerAS,
                       Address peerIP,
                       Integer routeSelectDelay,
                       Integer importDirectRouteEnable,
                       List<NetworkSeg> networkSegList) {
        this.id = id;
        this.device = device;
        this.deviceInterfaceList = new ArrayList<DeviceInterface>();
        if(null  != deviceInterfaceList) {
            this.deviceInterfaceList.addAll(deviceInterfaceList);
        }
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
        this.networkSegList = new ArrayList<NetworkSeg>();
        if(null != networkSegList) {
            this.networkSegList.addAll(networkSegList);
        }
    }

    public VPNInstance(String routerId, String vpnName) {
        this.vpnName = vpnName;
        this.routerId = routerId;
        this.deviceInterfaceList = new ArrayList<DeviceInterface>();
        this.networkSegList = new ArrayList<NetworkSeg>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    public List<DeviceInterface> getDeviceInterfaceList() {
        return deviceInterfaceList;
    }

    public void setDeviceInterfaceList(List<DeviceInterface> deviceInterfaceList) {
        this.deviceInterfaceList.clear();
        this.deviceInterfaceList.addAll(deviceInterfaceList);
    }

    public void setVpnName(String vpnName) {
        this.vpnName = vpnName;
    }

    public String getVpnName() {
        return vpnName;
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

    public Address getPeerIP() {
        return peerIP;
    }

    public void setPeerIP(Address peerIP) {
        this.peerIP = peerIP;
    }

    public Integer getPeerAS() {
        return peerAS;
    }

    public void setPeerAS(Integer peerAS) {
        this.peerAS = peerAS;
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

    public List<NetworkSeg> getNetworkSegList() {
        return networkSegList;
    }

    public void setNetworkSegList(List<NetworkSeg> networkSegList) {
        this.networkSegList.clear();
        this.networkSegList.addAll(networkSegList);
    }

    public String getRouterId() {
        return routerId;
    }

    public void setRouterId(String routerId) {
        this.routerId = routerId;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}
