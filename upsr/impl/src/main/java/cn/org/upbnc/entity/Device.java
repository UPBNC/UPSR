/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;

import cn.org.upbnc.enumtype.DeviceTypeEnum;

import java.util.ArrayList;
import java.util.List;

public class Device {
    // Base property
    private Integer id;
    private String name;
    private String deviceName;
    private String sysName;
    private String routerId;
    private Integer deviceType;
    private Integer dataCenter;
    private Address address;

    private NetConf netConf;

    //private SRInfo srInfo;
    private Integer minNodeSID;
    private Integer maxNodeSID;

    private Integer srStatus;
    private Integer bgpAS;
    private Integer ospfId;

    private List<DeviceInterface> deviceInterfaceList;
    private LoopBack loopBack;
    private List<VPNInstance> vpnInstanceList;
    private List<Tunnel> tunnelList;
    private List<Prefix> prefixList;

    private DeviceTypeEnum deviceTypeEnum;
    private BgpDevice bgpDevice;

    public Device() {
        this.id = 0;
        this.name = null;
        this.deviceName = null;
        this.sysName = null;
        this.routerId = null;
        this.deviceType = 0;
        this.dataCenter = 0;
        this.address = null;
        this.netConf = null;
        this.minNodeSID = 0;
        this.maxNodeSID = 0;
        this.srStatus = 0;
        this.bgpAS = 0;
        this.ospfId = 0;
        this.deviceInterfaceList = new ArrayList<DeviceInterface>();
        this.loopBack = null;
        this.vpnInstanceList = new ArrayList<VPNInstance>();
        this.tunnelList = new ArrayList<Tunnel>();
        this.prefixList = new ArrayList<Prefix>();
        this.bgpDevice = null;
        this.deviceTypeEnum = DeviceTypeEnum.UNDEFINED;
    }

    public Device(Integer id,
                  String name,
                  String deviceName,
                  String sysName,
                  String routerId,
                  Integer deviceType,
                  Integer dataCenter,
                  Address address,
                  NetConf netConf,
                  Integer minNodeSID,
                  Integer maxNodeSID,
                  Integer srStatus,
                  Integer bgpAS,
                  Integer ospfId,
                  List<DeviceInterface> deviceInterfaceList,
                  LoopBack loopBack,
                  List<VPNInstance> vpnInstanceList,
                  List<Tunnel> tunnelList,
                  List<Prefix> prefixList,
                  BgpDevice bgpDevice,
                  DeviceTypeEnum deviceTypeEnum) {
        this.id = id;
        this.name = name;
        this.deviceName = deviceName;
        this.sysName = sysName;
        this.routerId = routerId;
        this.deviceType = deviceType;
        this.dataCenter = dataCenter;
        this.address = address;
        this.netConf = netConf;
        this.minNodeSID = minNodeSID;
        this.maxNodeSID = maxNodeSID;
        this.srStatus = srStatus;
        this.bgpAS = bgpAS;
        this.ospfId = ospfId;
        this.deviceInterfaceList = new ArrayList<DeviceInterface>();
        this.deviceInterfaceList.addAll(deviceInterfaceList);
//        this.deviceInterfaceList = deviceInterfaceList; //?
        this.loopBack = loopBack;
        this.vpnInstanceList = new ArrayList<VPNInstance>();
        this.vpnInstanceList.addAll(vpnInstanceList);
//        this.vpnInstanceList = vpnInstanceList;//?
        this.tunnelList = new ArrayList<Tunnel>();
        this.tunnelList.addAll(tunnelList);
//        this.tunnelList = tunnelList;//?
        this.prefixList = new ArrayList<Prefix>();
        this.prefixList.addAll(prefixList);
        this.bgpDevice = bgpDevice;
        this.deviceTypeEnum = deviceTypeEnum;
    }
    public Device(String routerId, String deviceName, NetConf netConf)
    {
        this.routerId = routerId;
        this.deviceName = deviceName;
        this.netConf = netConf;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getRouterId() {
        return routerId;
    }

    public void setRouterId(String routerId) {
        this.routerId = routerId;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }


    public Integer getDataCenter() {
        return dataCenter;
    }

    public void setDataCenter(Integer dataCenter) {
        this.dataCenter = dataCenter;
    }


    public NetConf getNetConf() {
        return netConf;
    }

    public void setNetConf(NetConf netConf) {
        this.netConf = netConf;
    }


    public Integer getMinNodeSID() {
        return minNodeSID;
    }

    public void setMinNodeSID(Integer minNodeSID) {
        this.minNodeSID = minNodeSID;
    }

    public Integer getMaxNodeSID() {
        return maxNodeSID;
    }

    public void setMaxNodeSID(Integer maxNodeSID) {
        this.maxNodeSID = maxNodeSID;
    }


    public Integer getSrStatus() {
        return srStatus;
    }

    public void setSrStatus(Integer srStatus) {
        this.srStatus = srStatus;
    }


    public Integer getBgpAS() {
        return bgpAS;
    }

    public void setBgpAS(Integer bgpAS) {
        this.bgpAS = bgpAS;
    }


    public Integer getOspfId() {
        return ospfId;
    }

    public void setOspfId(Integer ospfId) {
        this.ospfId = ospfId;
    }

    public List<DeviceInterface> getDeviceInterfaceList() {
        return deviceInterfaceList;
    }

    public void setDeviceInterfaceList(List<DeviceInterface> deviceInterfaceList) {
        this.deviceInterfaceList.clear();
        this.deviceInterfaceList.addAll(deviceInterfaceList);
    }


    public LoopBack getLoopBack() {
        return loopBack;
    }

    public void setLoopBack(LoopBack loopBack) {
        this.loopBack = loopBack;
    }

    public List<VPNInstance> getVpnInstanceList() {
        return vpnInstanceList;
    }

    public void setVpnInstanceList(List<VPNInstance> vpnInstanceList) {
        this.vpnInstanceList.clear();
        this.vpnInstanceList.addAll(vpnInstanceList);
    }

    public List<Tunnel> getTunnelList() {
        return tunnelList;
    }

    public void setTunnelList(List<Tunnel> tunnelList) {
        this.tunnelList.clear();
        this.tunnelList.addAll(tunnelList);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Prefix> getPrefixList() {
        return prefixList;
    }

    public void setPrefixList(List<Prefix> prefixList) {
        this.prefixList.clear();
        this.prefixList.addAll(prefixList);
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void addDeviceInterface(DeviceInterface deviceInterface){
        this.deviceInterfaceList.add(deviceInterface);
    }

    public void setDeviceTypeEnum(DeviceTypeEnum deviceTypeEnum){
        this.deviceTypeEnum = deviceTypeEnum;
    }

    public DeviceTypeEnum getDeviceTypeEnum() {
        return deviceTypeEnum;
    }

    public void setBgpDevice(BgpDevice bgpDevice) {
        this.bgpDevice = bgpDevice;
    }

    public BgpDevice getBgpDevice() {
        return bgpDevice;
    }
}
