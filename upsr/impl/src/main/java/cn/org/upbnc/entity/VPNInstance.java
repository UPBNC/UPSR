/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private boolean RefreshFlag;
    private String note;
    private String importRoutePolicyName;
    private String importTunnelPolicyName;
    private String exportRoutePolicyName;
    private String ebgpPreference;
    private String ibgpPreference;
    private String localPreference;


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
        this.importDirectRouteEnable = 2;
        this.networkSegList = new ArrayList<NetworkSeg>();
        this.RefreshFlag = false;
        this.note=null;
        this.importRoutePolicyName=null;
        this.importTunnelPolicyName=null;
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
        if (null != deviceInterfaceList) {
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
        if (null != networkSegList) {
            this.networkSegList.addAll(networkSegList);
        }
        RefreshFlag = false;
    }

    public VPNInstance(String routerId, String vpnName) {
        this.vpnName = vpnName;
        this.routerId = routerId;
        this.deviceInterfaceList = new ArrayList<DeviceInterface>();
        this.networkSegList = new ArrayList<NetworkSeg>();
        this.importDirectRouteEnable = 2;
        RefreshFlag = false;
    }

    public String getExportRoutePolicyName() {
        return exportRoutePolicyName;
    }

    public void setExportRoutePolicyName(String exportRoutePolicyName) {
        this.exportRoutePolicyName = exportRoutePolicyName;
    }

    public String getImportRoutePolicyName() {
        return importRoutePolicyName;
    }

    public void setImportRoutePolicyName(String importRoutePolicyName) {
        this.importRoutePolicyName = importRoutePolicyName;
    }

    public String getImportTunnelPolicyName() {
        return importTunnelPolicyName;
    }

    public void setImportTunnelPolicyName(String importTunnelPolicyName) {
        this.importTunnelPolicyName = importTunnelPolicyName;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public boolean isRefreshFlag() {
        return RefreshFlag;
    }

    public void setRefreshFlag(boolean refreshFlag) {
        RefreshFlag = refreshFlag;
    }

    public boolean isIfmBinded(String ifmName) {
        if (null == ifmName) {
            return false;
        }
        for (DeviceInterface deviceInterface : deviceInterfaceList) {
            if (deviceInterface.getName().equals(ifmName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public Map<String, Boolean> compareVpnInfo(String vpnName,
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

        Map<String, Boolean> compMap = new HashMap<String, Boolean>();
        boolean isRdChanged = false;
        boolean isRtChanged = false;
        boolean isIfmChanged = false;
        boolean isEbgpChanged = false;
        if (!rd.equals(this.rd)) {
            isRdChanged = true;
            isRtChanged = true;
            isEbgpChanged = true;
        } else {
            if (!exportRT.equals(this.exportRT)) {
                isRtChanged = true;
            }
            if (!compareEbgpInfoIsEqual(peerAS, peerIP, routeSelectDelay, importDirectRouteEnable, networkSegList)) {
                isEbgpChanged = true;
            }
        }
        if (!compareDeviceInterfaceListInfoIsEqual(deviceInterfaceList)) {
            isIfmChanged = true;
        }

        compMap.put("isRdChanged", isRdChanged);
        compMap.put("isRtChanged", isRtChanged);
        compMap.put("isIfmChanged", isIfmChanged);
        compMap.put("isEbgpChanged", isEbgpChanged);
        return compMap;
    }

    public boolean compareEbgpInfoIsEqual(Integer peerAS,
                                          Address peerIP,
                                          Integer routeSelectDelay,
                                          Integer importDirectRouteEnable,
                                          List<NetworkSeg> networkSegList) {
        if ((peerAS != null && this.peerAS == null) || (peerAS == null && this.peerAS != null)) {
            return false;
        }
        if ((peerAS != null && this.peerAS != null)) {
            if (!peerAS.equals(this.peerAS)) {
                return false;
            }
        }
        if ((peerIP != null && this.peerIP == null) || (peerIP == null && this.peerIP != null)) {
            return false;
        }
        if (peerIP != null && this.peerIP != null) {
            if (!peerIP.getAddress().equals(this.peerIP.getAddress())) {
                return false;
            }
        }

        if ((importDirectRouteEnable != null && this.importDirectRouteEnable == null) || (importDirectRouteEnable == null && this.importDirectRouteEnable != null)) {
            return false;
        }

        if ((importDirectRouteEnable != null && this.importDirectRouteEnable != null)) {
            if (!importDirectRouteEnable.equals(this.importDirectRouteEnable)) {
                return false;
            }
        }

        if (!compareNetworkSegListInfoIsEqual(networkSegList)) {
            return false;
        }
        return true;
    }

    public boolean compareNetworkSegListInfoIsEqual(List<NetworkSeg> networkSegList) {
        if (networkSegList == null && this.networkSegList == null) {
            return true;
        }
        if (networkSegList == null || this.networkSegList == null) {
            return false;
        }
        if (networkSegList.size() != this.networkSegList.size()) {
            return false;
        }
        for (NetworkSeg seg1 : networkSegList) {
            if (networkSegListContainNetworkSegList(this.networkSegList, seg1)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean networkSegListContainNetworkSegList(List<NetworkSeg> networkSegList, NetworkSeg seg1) {
        for (NetworkSeg seg2 : networkSegList) {
            if (compareNetworkSegInfoIsEqual(seg1, seg2)) {
                return true;
            }
        }
        return false;
    }

    private boolean compareNetworkSegInfoIsEqual(NetworkSeg seg1, NetworkSeg seg2) {

        if ((seg1.getAddress() != null && seg2.getAddress() == null) || (seg1.getAddress() == null && seg2.getAddress() != null)) {
            return false;
        }
        if (seg1.getAddress() != null && seg2.getAddress() != null) {
            if (!seg1.getAddress().getAddress().equals(seg2.getAddress().getAddress())) {
                return false;
            }
        }
        if ((seg1.getMask() != null && seg2.getMask() == null) || (seg1.getMask() == null && seg2.getMask() != null)) {
            return false;
        }
        if (seg1.getMask() != null && seg2.getMask() != null) {
            if (!seg1.getMask().getAddress().equals(seg2.getMask().getAddress())) {
                return false;
            }
        }
        return true;
    }


    public boolean compareDeviceInterfaceListInfoIsEqual(List<DeviceInterface> deviceInterfaceList) {
        if (deviceInterfaceList == null && this.deviceInterfaceList == null) {
            return true;
        }
        if (deviceInterfaceList == null || this.deviceInterfaceList == null) {
            return false;
        }
        if (deviceInterfaceList.size() != this.deviceInterfaceList.size()) {
            return false;
        }
        for (DeviceInterface d1 : deviceInterfaceList) {
            if (deviceInterfaceListContainDeviceInterface(this.deviceInterfaceList, d1)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean deviceInterfaceListContainDeviceInterface(List<DeviceInterface> deviceInterfaceList, DeviceInterface d1) {
        for (DeviceInterface d2 : deviceInterfaceList) {
            if (compareDeviceInterfaceInfoIsEqual(d1, d2)) {
                return true;
            }
        }
        return false;
    }

    private boolean compareDeviceInterfaceInfoIsEqual(DeviceInterface d1, DeviceInterface d2) {
        if (!d1.getName().equals(d2.getName())) {
            return false;
        }
        if ((d1.getIp() != null && d2.getIp() == null) || (d1.getIp() == null && d2.getIp() != null)) {
            return false;
        }
        if (d1.getIp() != null && d2.getIp() != null) {
            if (!d1.getIp().getAddress().equals(d2.getIp().getAddress())) {
                return false;
            }
        }
        if ((d1.getMask() != null && d2.getMask() == null) || (d1.getMask() == null && d2.getMask() != null)) {
            return false;
        }

        if (d1.getMask() != null && d2.getMask() != null) {
            if (!d1.getMask().getAddress().equals(d2.getMask().getAddress())) {
                return false;
            }
        }
        return true;
    }

    public boolean ebgpIsNull() {
        if ((null == peerAS || peerAS.equals(0)) && (null == peerIP || peerIP.getAddress().equals("")) &&
                (null == importDirectRouteEnable || importDirectRouteEnable.equals(2)) && (null == networkSegList || networkSegList.size() == 0)) {
            return true;
        }
        return false;
    }

}
