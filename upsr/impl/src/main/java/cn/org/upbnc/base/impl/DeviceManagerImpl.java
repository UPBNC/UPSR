/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.enumtype.DeviceTypeEnum;

import java.util.*;

public class DeviceManagerImpl implements DeviceManager {
    private static DeviceManager instance = null;
    private Map<String,Device> mapInstance;
    private List<Device> deviceList;

    private DeviceManagerImpl() {
        this.mapInstance = new HashMap<String,Device>();
        this.deviceList = new ArrayList<Device>();
        return;
    }
    public static DeviceManager getInstance() {
        if(null == instance) {
            instance = new DeviceManagerImpl();
        }
        return instance;
    }

    @Override
    public Device addDevice(String name, String routerId) {
        Device device = null;
        if(null != name && null != routerId) {
            device = this.getDevice(routerId);
            if(null == device){
               device = new Device();
               // Device is create by man
               device.setDeviceTypeEnum(DeviceTypeEnum.SELFDEFIND);
               device.setDeviceName(name);
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
            if(deviceIterator.hasNext()){
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
            if(deviceIterator.hasNext()){
                Device device = deviceIterator.next();
                if(deviceName.equals(device.getDeviceName())){
                    return device;
                }
            }
        }
        return null;
//        Device device = null;
//        Map.Entry entry = null;
//        if(null == deviceName){
//            return null;
//        }
//        Iterator iter = mapInstance.entrySet().iterator();
//        while(iter.hasNext())
//        {
//            entry = (Map.Entry) iter.next();
//            device = (Device)entry.getValue();
//            if(true == deviceName.equals(device.getDeviceName()))
//            {
//                return device;
//            }
//        }
//        return null;
    }

    @Override
    public Device getDeviceByNetconfIP(String deviceIP) {
        if(null != deviceIP) {
            Iterator<Device> deviceIterator = this.deviceList.iterator();
            if(deviceIterator.hasNext()){
                Device device = deviceIterator.next();
                if(null != device.getNetConf()) {
                    if (deviceIP.equals(device.getNetConf().getIp().getAddress())) {
                        return device;
                    }
                }
            }
        }
        return null;
//        Device device = null;
//        Map.Entry entry = null;
//        if(null == deviceIP){
//            return null;
//        }
//        Iterator iter = mapInstance.entrySet().iterator();
//        while(iter.hasNext())
//        {
//            entry = (Map.Entry) iter.next();
//            device = (Device)entry.getValue();
//            if(true == deviceIP.equals(device.getNetConf().getIp().getAddress()))
//            {
//                return device;
//            }
//        }
//        return null;
    }

    @Override
    public List<Device> getDeviceList(){
        return this.deviceList;
    }

    @Override
    public boolean delDevice(String routerId) {
        if(null != routerId) {
            Iterator<Device> deviceIterator = this.deviceList.iterator();
            if(deviceIterator.hasNext()){
                Device device = deviceIterator.next();
                if(routerId.equals(device.getRouterId())){
                    deviceIterator.remove();
                    return true;
                }
            }
        }
        return false;
//        Device device = null;
//        Map.Entry entry = null;
//        if(null == routerId){
//            return false;
//        }
//        Iterator iter = mapInstance.entrySet().iterator();
//        while(iter.hasNext())
//        {
//            entry = (Map.Entry) iter.next();
//            device = (Device)entry.getValue();
//            if(true == routerId.equals(device.getRouterId()))
//            {
//                iter.remove();
//                return true;
//            }
//        }
//        return false;
    }

    @Override
    public boolean delDeviceByDeviceName(String deviceName) {
        if(null != deviceName) {
            Iterator<Device> deviceIterator = this.deviceList.iterator();
            if(deviceIterator.hasNext()){
                Device device = deviceIterator.next();
                if(deviceName.equals(device.getDeviceName())){
                    deviceIterator.remove();
                    return true;
                }
            }
        }
        return false;
//        Device device = null;
//        Map.Entry entry = null;
//        if(null == deviceName){
//            return false;
//        }
//        Iterator iter = mapInstance.entrySet().iterator();
//        while(iter.hasNext())
//        {
//            entry = (Map.Entry) iter.next();
//            device = (Device)entry.getValue();
//            if(true == deviceName.equals(device.getDeviceName()))
//            {
//                iter.remove();
//                return true;
//            }
//        }
//        return false;
    }

    @Override
    public boolean delDeviceByNetconfIP(String deviceIP) {
        if(null != deviceIP) {
            Iterator<Device> deviceIterator = this.deviceList.iterator();
            if(deviceIterator.hasNext()){
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
//        Device device = null;
//        Map.Entry entry = null;
//        if(null == deviceIP){
//            return false;
//        }
//        Iterator iter = mapInstance.entrySet().iterator();
//        while(iter.hasNext())
//        {
//            entry = (Map.Entry) iter.next();
//            device = (Device)entry.getValue();
//            if(true == deviceIP.equals(device.getNetConf().getIp().getAddress()))
//            {
//                iter.remove();
//                return true;
//            }
//        }
//        return true;
    }


}
