/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.entity.Address;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.DeviceInterface;
import cn.org.upbnc.enumtype.AddressTypeEnum;
import cn.org.upbnc.enumtype.NetConfStatusEnum;
import cn.org.upbnc.enumtype.SrStatusEnum;
import cn.org.upbnc.service.InterfaceService;
import cn.org.upbnc.service.entity.DevInterfaceInfo;
import cn.org.upbnc.util.netconf.GigabitEthernet;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.xml.VpnXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class InterfaceServiceImpl implements InterfaceService{
    private static final Logger LOG = LoggerFactory.getLogger(NetconfSessionServiceImpl.class);
    public static final String INTERFACE_TYPE_GIGABITETHERNET = "GigabitEthernet";
    public static final String INTERFACE_TYPE_ETHTRUNK  = "Eth-Trunk";
    private static InterfaceService ourInstance = new InterfaceServiceImpl();
    private BaseInterface  baseInterface = null;
    private NetConfManager netConfManager = null;
    private DeviceManager deviceManager = null;
    public static InterfaceService getInstance() {
        if(null == ourInstance)
        {
            ourInstance = new InterfaceServiceImpl();
        }
        return ourInstance;
    }

    private InterfaceServiceImpl() {
        this.baseInterface = null;
        this.netConfManager = null;
        this.deviceManager = null;
    }
    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        if(null == baseInterface) {
            return false;
        }
        try {
            this.baseInterface = baseInterface;
            if (null != this.baseInterface) {
                this.netConfManager = this.baseInterface.getNetConfManager();
                this.deviceManager = this.baseInterface.getDeviceManager();
            }
        }
        catch (Exception e)
        {
            LOG.info(e.getMessage());
        }
        return true;
    }

    @Override
    public List<DevInterfaceInfo> getInterfaceList(String routerId) {
        String ifnetStatus = null;
        String srStatus = null;

        if((null == routerId)||(routerId.isEmpty()))
        {
            return null;
        }
        Device device = this.deviceManager.getDevice(routerId);
        if(null == device) {
            return null;
        }
        DevInterfaceInfo devInterfaceInfo = null;
        List<DevInterfaceInfo> devInterfaceInfos = new LinkedList<DevInterfaceInfo>();
        List<DeviceInterface> deviceInterfaceList = device.getDeviceInterfaceList();
        if(null != deviceInterfaceList)
        {
            for(DeviceInterface deviceInterface:deviceInterfaceList) {
                srStatus = (SrStatusEnum.ENABLED.getName() == deviceInterface.getSrStatus()) ? "up" :"down";
                devInterfaceInfo = new DevInterfaceInfo(deviceInterface.getName(),
                        null,null,null,null,
                        ifnetStatus, srStatus,
                        null,null,null);
                if(null != deviceInterface.getIp()) {
                    devInterfaceInfo.setIfnetIP(deviceInterface.getIp().getAddress());
                }
                if(null != deviceInterface.getMask()) {
                    devInterfaceInfo.setIfnetMask(deviceInterface.getMask().getAddress());
                }
                if(null != deviceInterface.getMac().getAddress())
                {
                    devInterfaceInfo.setIfnetMac(deviceInterface.getMac().getAddress());
                }
                if(null != deviceInterface.getVpn()) {
                    devInterfaceInfo.setVpnName(deviceInterface.getVpn().getVpnName());
                }
                if(null != deviceInterface.getAdjLabel())
                {
                    devInterfaceInfo.setAdjLabel(deviceInterface.getAdjLabel());
                }
                if(null!=deviceInterface.getIfPhyStatus()){
                    devInterfaceInfo.setIfnetStatus(deviceInterface.getIfPhyStatus());
                }
                if(null!=deviceInterface.getIfOperStatus()){
                    devInterfaceInfo.setRunningStatus(deviceInterface.getIfOperStatus());
                }
                if(null!=deviceInterface.getIfLinkStatus()){
                    devInterfaceInfo.setLinkStatus(deviceInterface.getIfLinkStatus());
                }
                devInterfaceInfos.add(devInterfaceInfo);
            }
           return devInterfaceInfos;
        }
 
        return null;
    }

    @Override
    public List<DevInterfaceInfo> getInterfaceListFromDevice(String routerId) {
        if((null == routerId)||(routerId.isEmpty()))
        {
            return null;
        }
        Device device = this.deviceManager.getDevice(routerId);
        if((null == device)||(null == device.getNetConf())) {
            return null;
        }
        DevInterfaceInfo devInterfaceInfo = null;
        List<DevInterfaceInfo> devInterfaceInfos = new LinkedList<DevInterfaceInfo>();
        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getRouterID());

        LOG.info("enter getInterfaceListFromDevice");
        String gigabitEthernetMsg = VpnXml.getInterfacesXml(InterfaceServiceImpl.INTERFACE_TYPE_GIGABITETHERNET);
        LOG.info("gigabitEthernetMsg : " + gigabitEthernetMsg);
        String gigabitEthernetResult = netconfController.sendMessage(netconfClient, gigabitEthernetMsg);
        LOG.info("gigabitEthernetResult : " + gigabitEthernetResult);
        List<GigabitEthernet> gigabitEthernets = VpnXml.getGigabitEthernetsFromXml(gigabitEthernetResult);
        String ethTrunkMsg = VpnXml.getInterfacesXml(InterfaceServiceImpl.INTERFACE_TYPE_ETHTRUNK);
        LOG.info("ethTrunkMsg : " + ethTrunkMsg);
        String ethTrunkResult = netconfController.sendMessage(netconfClient, ethTrunkMsg);
        LOG.info("ethTrunkResult : " + ethTrunkResult);
        List<GigabitEthernet> ethTrunks = VpnXml.getGigabitEthernetsFromXml(ethTrunkResult);
        for (GigabitEthernet gigabitEthernet:gigabitEthernets) {
            devInterfaceInfo = new DevInterfaceInfo(gigabitEthernet.getIfName(),gigabitEthernet.getIfIpAddr(),
                    gigabitEthernet.getSubnetMask(),gigabitEthernet.getIfOperMac(),gigabitEthernet.getVrfName(),gigabitEthernet.getIfPhyStatus(),
                    null, gigabitEthernet.getIfLinkStatus(),gigabitEthernet.getIfOperStatus(),gigabitEthernet.getIfTrunkName());
            devInterfaceInfos.add(devInterfaceInfo);
        }
        for (GigabitEthernet gigabitEthernet:ethTrunks) {
            devInterfaceInfo = new DevInterfaceInfo(gigabitEthernet.getIfName(),gigabitEthernet.getIfIpAddr(),
                    gigabitEthernet.getSubnetMask(),gigabitEthernet.getIfOperMac(),gigabitEthernet.getVrfName(),gigabitEthernet.getIfPhyStatus(),
                    null, gigabitEthernet.getIfLinkStatus(),gigabitEthernet.getIfOperStatus(),gigabitEthernet.getIfTrunkName());
            devInterfaceInfos.add(devInterfaceInfo);
        }
        return devInterfaceInfos;
    }


    @Override
    public String toString() {
        return "InterfaceServiceImpl{" +
                "baseInterface=" + baseInterface +
                ", netConfManager=" + netConfManager +
                ", deviceManager=" + deviceManager +
                '}';
    }
    public boolean updateDeviceInterface (DeviceInterface deviceInterface , DevInterfaceInfo devInterfaceInfo) {

        if((null == deviceInterface)||(null == devInterfaceInfo)) {
            return false;
        }
        deviceInterface.setName(devInterfaceInfo.getIfnetName());
        if(null != devInterfaceInfo.getIfnetIP()) {
            deviceInterface.setIp(new Address(devInterfaceInfo.getIfnetIP(), AddressTypeEnum.V4));
        }
        if(null != devInterfaceInfo.getIfnetMask()) {
            deviceInterface.setMask(new Address(devInterfaceInfo.getIfnetMask(), AddressTypeEnum.V4));
        }
        if(null != devInterfaceInfo.getIfnetMac()) {
            deviceInterface.setMac(new Address(devInterfaceInfo.getIfnetMac(), AddressTypeEnum.MAC));
        }
        if((null != deviceInterface.getVpn())&&(null != devInterfaceInfo.getVpnName())&&(true != deviceInterface.getVpn().getVpnName().equals(devInterfaceInfo.getVpnName()))) {
            //deviceInterface.setVpn();
        }
        if(null!=devInterfaceInfo.getIfnetStatus()){
            deviceInterface.setIfPhyStatus(devInterfaceInfo.getIfnetStatus());
        }
        if(null!=devInterfaceInfo.getLinkStatus()){
            deviceInterface.setIfLinkStatus(devInterfaceInfo.getLinkStatus());
        }
        if(null!=devInterfaceInfo.getRunningStatus()){
            deviceInterface.setIfOperStatus(devInterfaceInfo.getRunningStatus());
        }

        deviceInterface.setRefreshFlag(true);
        return true;

    }
    @Override
    public boolean syncInterfaceConf() {
        if(null != this.deviceManager) {
            for (Device device:this.deviceManager.getDeviceList()) {
                syncInterfaceConf(device.getRouterId());
            }
        }
        return true;
    }
    public boolean syncInterfaceConf(String routerId){
        if(null==routerId||routerId.equals("")){
            LOG.info("syncInterfaceConf failed,routerId is null or empty ");
            return false;
        }
        Device device=deviceManager.getDevice(routerId);
        if((null==device.getNetConf())||(device.getNetConf().getStatus()!= NetConfStatusEnum.Connected)) {
            LOG.info("Can not connect device by Netconf , status is Disconnected,which device routerId=" + device.getRouterId());
            return false;
        }
        List<DeviceInterface> deviceInterfaceList = device.getDeviceInterfaceList();
        List<DevInterfaceInfo> deviceInterfaceInfoList = getInterfaceListFromDevice(device.getRouterId());
        if(null == deviceInterfaceInfoList) {
            LOG.info("syncInterfaceConf failed,can't get interface list from device , deviceInterfaceInfoList is null");
            return false;
        }
        //1 memory interface set invalid
        for (DeviceInterface deviceInterface:deviceInterfaceList)
        {
            deviceInterface.setRefreshFlag(false);
        }
        //2 compare memory interface and device interface
        //3 add new device interface from device to memory
        for (DevInterfaceInfo deviceInterfaceInfo:deviceInterfaceInfoList ) {
            boolean compareFlag = false;

            for (DeviceInterface deviceInterface:deviceInterfaceList) {
                if(null == deviceInterfaceInfo.getIfnetIP()) { // some interface on device ip is null
                    break;  //for create deviceInterface
                }
                if(true == deviceInterfaceInfo.getIfnetIP().equals(deviceInterface.getIp().getAddress())){ //// deviceInterface.getIp() can't be null
                    compareFlag = true;
                    updateDeviceInterface(deviceInterface, deviceInterfaceInfo);
                    break;
                }
            }
            if(false == compareFlag) {
                DeviceInterface devInterface = new DeviceInterface(0, device, device.getDeviceName(),null,null,
                        null, deviceInterfaceInfo.getIfnetName(),deviceInterfaceInfo.getRunningStatus(),deviceInterfaceInfo.getIfnetStatus(),
                        deviceInterfaceInfo.getLinkStatus(), new Address(deviceInterfaceInfo.getIfnetIP(),AddressTypeEnum.V4),
                        new Address(deviceInterfaceInfo.getIfnetMask(), AddressTypeEnum.V4),
                        new Address(deviceInterfaceInfo.getIfnetMac(),AddressTypeEnum.V4),
                        null, null, null,deviceInterfaceInfo.getTrunkName());
                devInterface.setRefreshFlag(true);
                deviceInterfaceList.add(devInterface);
            }
        }
        //4 delete invalid memory interface
        DeviceInterface deviceInterfaceGet = null;
        Iterator<DeviceInterface> iter = deviceInterfaceList.iterator();
        while(iter.hasNext())
        {
            deviceInterfaceGet = iter.next();
            if(false == deviceInterfaceGet.isRefreshFlag()) {
                iter.remove();
            }
        }
        return true;
    }
}
