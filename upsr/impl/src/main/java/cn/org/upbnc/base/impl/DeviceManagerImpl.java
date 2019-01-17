/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.entity.BgpDevice;
import cn.org.upbnc.entity.BgpDeviceInterface;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.DeviceInterface;
import cn.org.upbnc.enumtype.DeviceTypeEnum;

import java.util.*;

public class DeviceManagerImpl implements DeviceManager {
    private static DeviceManager instance = null;
    private List<Device> deviceList;

    private DeviceManagerImpl() {
        this.deviceList = Collections.synchronizedList(new ArrayList<Device>());
        return;
    }
    public static DeviceManager getInstance() {
        if(null == instance) {
            instance = new DeviceManagerImpl();
        }
        return instance;
    }

    @Override
    public Device addDevice(String deviceName, String routerId) {
        Device device = null;
        if(null != deviceName && null != routerId) {
            device = this.getDevice(routerId);
            if(null == device){
               device = new Device();
               // Device is create by man
               device.setDeviceTypeEnum(DeviceTypeEnum.SELFDEFIND);
               device.setDeviceName(deviceName);
               device.setRouterId(routerId);
               this.deviceList.add(device);
            }
        }
        return device;
    }

    @Override
    public Device addDevice(Device device) {
        if(null != device) {
            if(null == this.getDevice(device.getRouterId())) {
                this.deviceList.add(device);
            }else {
                return null;
            }
        }
        return device;
    }

    @Override
    public Device getDevice(String routerId) {
        if(null != routerId) {
            Iterator<Device> deviceIterator = this.deviceList.iterator();
            while(deviceIterator.hasNext()){
                Device device = deviceIterator.next();
                if(routerId.equals(device.getRouterId())){
                    return device;
                }
            }
        }
        return null;
    }

    @Override
    public Device getDeviceByDeviceName(String deviceName) {
        if(null != deviceName) {
            Iterator<Device> deviceIterator = this.deviceList.iterator();
            while(deviceIterator.hasNext()){
                Device device = deviceIterator.next();
                if(deviceName.equals(device.getDeviceName())){
                    return device;
                }
            }
        }
        return null;
    }

    @Override
    public Device getDeviceByNetconfIP(String deviceIP) {
        if(null != deviceIP) {
            Iterator<Device> deviceIterator = this.deviceList.iterator();
            while(deviceIterator.hasNext()){
                Device device = deviceIterator.next();
                if(null != device.getNetConf()) {
                    if (deviceIP.equals(device.getNetConf().getIp().getAddress())) {
                        return device;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<Device> getDeviceList(){
        return this.deviceList;
    }

    @Override
    public boolean delDevice(String routerId) {
        if(null != routerId) {
            Iterator<Device> deviceIterator = this.deviceList.iterator();
            while(deviceIterator.hasNext()){
                Device device = deviceIterator.next();
                if(routerId.equals(device.getRouterId())){
                    deviceIterator.remove();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean delDeviceByDeviceName(String deviceName) {
        if(null != deviceName) {
            Iterator<Device> deviceIterator = this.deviceList.iterator();
            while(deviceIterator.hasNext()){
                Device device = deviceIterator.next();
                if(deviceName.equals(device.getDeviceName())){
                    deviceIterator.remove();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean delDeviceByNetconfIP(String deviceIP) {
        if(null != deviceIP) {
            Iterator<Device> deviceIterator = this.deviceList.iterator();
            while(deviceIterator.hasNext()){
                Device device = deviceIterator.next();
                if(null != device.getNetConf()) {
                    if (deviceIP.equals(device.getNetConf().getIp().getAddress())) {
                        deviceIterator.remove();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<Device> updateDeviceListByBgpDeviceList(List<BgpDevice> bgpDeviceList){
        // 清空Device的Bgp状态
        Iterator<Device> deviceIterator = this.deviceList.iterator();
        while(deviceIterator.hasNext()){
            deviceIterator.next().setBgpDevice(null);
        }

        // 当 BGP 为空，不处理
        if(null == bgpDeviceList || bgpDeviceList.isEmpty()){
            return this.deviceList;
        }

        Iterator<BgpDevice> bgpDeviceIterator = bgpDeviceList.iterator();
        while(bgpDeviceIterator.hasNext()){
            BgpDevice bgpDevice = bgpDeviceIterator.next();
            //查找device
            Device device = this.getDevice(bgpDevice.getRouterId());
            if(null == device){// 创建新的device
                device = this.updateDevice(device,bgpDevice);
                this.deviceList.add(device);
            }else {// 更新 device
                device = this.updateDevice(device,bgpDevice);
            }
        }

        return this.deviceList;
    }

    @Override
    public Device getDeviceByNodeLabelValue(int labelVal) {
        Iterator<Device> deviceIterator = this.deviceList.iterator();
        while(deviceIterator.hasNext()){
            Device device = deviceIterator.next();
            if ((labelVal - device.getMinNodeSID().intValue()) == device.getNodeLabel().getValue()) {
                return device;
            }
        }
        return null;
    }

    // 更新Device
    private Device updateDevice(Device device,BgpDevice bgpDevice){
        Device ret = null;
        if(device == null){
            ret= new Device();
        }else{
            ret = device;
        }
        ret.setPrefixList(bgpDevice.getPrefixList());
        ret.setAddress(bgpDevice.getAddress());
        ret.setDeviceTypeEnum(bgpDevice.getDeviceTypeEnum());
        ret.setName(bgpDevice.getName());
        ret.setRouterId(bgpDevice.getRouterId());

        //添加端口
        List<DeviceInterface> deviceInterfaces = ret.getDeviceInterfaceList();
        this.updateDeviceInterface(ret,deviceInterfaces,bgpDevice.getBgpDeviceInterfaceList());

        //添加Bgp Device
        ret.setBgpDevice(bgpDevice);
        return ret;
    }

    private List<DeviceInterface> updateDeviceInterface(Device device,List<DeviceInterface> deviceInterfaces, List<BgpDeviceInterface> bgpDeviceInterfaces){
        //清空当前的BgpStatus
        Iterator<DeviceInterface> deviceInterfaceIterator = deviceInterfaces.iterator();
        while(deviceInterfaceIterator.hasNext()){
            DeviceInterface deviceInterface = deviceInterfaceIterator.next();
            deviceInterface.setBgpStatus(0);
        }

        // 如果Bgp没有接口上报，直接返回
        if(null == bgpDeviceInterfaces|| bgpDeviceInterfaces.isEmpty()){
            return deviceInterfaces;
        }

        // 遍历BgpDeviceInterface
        Iterator<BgpDeviceInterface> bgpDeviceInterfaceIterator = bgpDeviceInterfaces.iterator();
        while(bgpDeviceInterfaceIterator.hasNext()){
            BgpDeviceInterface bgpDeviceInterface = bgpDeviceInterfaceIterator.next();
            //查找是否存在了端口
            DeviceInterface temp = this.findDeviceInterface(deviceInterfaces,bgpDeviceInterface);
            // 创造端口
            if(null == temp) {
                temp = this.updateDeviceInterface(temp, bgpDeviceInterface);
                // 添加Device对象
                temp.setDevice(device);
                // 添加进入列表
                deviceInterfaces.add(temp);
            }else{ // 更新端口信息（不添加进入列表）
                temp = this.updateDeviceInterface(temp, bgpDeviceInterface);
                // 添加Device对象
                temp.setDevice(device);
            }
        }

        return deviceInterfaces;
    }

    private DeviceInterface findDeviceInterface(List<DeviceInterface> deviceInterfaceList,BgpDeviceInterface bgpDeviceInterface){
       Iterator<DeviceInterface> deviceInterfaceIterator = deviceInterfaceList.iterator();
       while(deviceInterfaceIterator.hasNext()) {
           DeviceInterface deviceInterface = deviceInterfaceIterator.next();
           if (deviceInterface.getIp() != null) {
               if (bgpDeviceInterface.getIp().getAddress().equals(deviceInterface.getIp().getAddress())) {
                   return deviceInterface;
               }
           }
       }
       return null;
    }

    private DeviceInterface updateDeviceInterface(DeviceInterface deviceInterface,BgpDeviceInterface bgpDeviceInterface){
        DeviceInterface ret = null;
        if( null == deviceInterface) {
            ret = new DeviceInterface();
        }else {
            ret = deviceInterface;
        }

        ret.setIp(bgpDeviceInterface.getIp());
        ret.setId(bgpDeviceInterface.getId());
        ret.setBgpName(bgpDeviceInterface.getName());
        ret.setBgpDeviceName(bgpDeviceInterface.getBgpDeviceName());
        ret.setBgpStatus(1);
        return ret;
    }
}
