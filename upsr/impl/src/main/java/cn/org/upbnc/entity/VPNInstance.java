/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;
import cn.org.upbnc.util.netconf.L3vpnIf;
import cn.org.upbnc.util.netconf.L3vpnInstance;
import cn.org.upbnc.util.netconf.bgp.BgpVrf;

import java.util.*;

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
        RefreshFlag=false;
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
        RefreshFlag=false;
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

    public boolean isRefreshFlag() {
        return RefreshFlag;
    }

    public void setRefreshFlag(boolean refreshFlag) {
        RefreshFlag = refreshFlag;
    }

    public boolean isIfmBinded(String ifmName){
        if(null==ifmName){
            return false;
        }
        for(DeviceInterface deviceInterface:deviceInterfaceList){
            if(deviceInterface.getName().equals(ifmName)){
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

        Map<String, Boolean> compMap=new HashMap<String,Boolean>();
        boolean isRdChanged=false;
        boolean isRtChanged=false;
        boolean isIfmChanged=false;
        boolean isEbgpChanged=false;
        if(!rd.equals(this.rd)){
            isRdChanged=true;
            isRtChanged=true;
            isEbgpChanged=true;
        }else{
            if(!exportRT.equals(this.exportRT)){
                isRtChanged=true;
            }
            if(!compareEbgpInfoIsEqual(peerAS,peerIP,routeSelectDelay,importDirectRouteEnable,networkSegList)){
                isEbgpChanged=true;
            }
        }
        if(!compareDeviceInterfaceListInfoIsEqual(deviceInterfaceList)){
            isIfmChanged=true;
        }

        compMap.put("isRdChanged",isRdChanged);
        compMap.put("isRtChanged",isRtChanged);
        compMap.put("isIfmChanged",isIfmChanged);
        compMap.put("isEbgpChanged",isEbgpChanged);
        return compMap;
    }

    public boolean compareEbgpInfoIsEqual(Integer peerAS,
                                 Address peerIP,
                                 Integer routeSelectDelay,
                                 Integer importDirectRouteEnable,
                                 List<NetworkSeg> networkSegList){
        if(peerAS==this.peerAS&&peerIP.getAddress().equals(this.peerIP.getAddress())&&importDirectRouteEnable==this.importDirectRouteEnable){
            if(compareNetworkSegListInfoIsEqual(networkSegList)){
                return true;
            }
        }
        return false;
    }

    public boolean compareNetworkSegListInfoIsEqual(List<NetworkSeg> networkSegList){
        if(networkSegList==null&&this.networkSegList==null){
            return  true;
        }
        if(networkSegList.size()!=this.networkSegList.size()){
            return false;
        }
        for (Object object : networkSegList) {
            if (!this.networkSegList.contains(object))
                return false;
        }
        return true;
    }

    public boolean compareDeviceInterfaceListInfoIsEqual(List<DeviceInterface> deviceInterfaceList){
        if(deviceInterfaceList==null&&this.deviceInterfaceList==null){
            return  true;
        }
        if(deviceInterfaceList.size()!=this.deviceInterfaceList.size()){
            return false;
        }
        for (Object object : deviceInterfaceList) {
            if (!this.deviceInterfaceList.contains(object))
                return false;
        }
        return true;
    }

}
