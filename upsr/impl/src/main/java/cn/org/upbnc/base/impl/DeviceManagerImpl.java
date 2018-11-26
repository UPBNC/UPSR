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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DeviceManagerImpl implements DeviceManager {
    private static DeviceManager instance = null;
    private Map<String,Device> mapInstance;

    private DeviceManagerImpl() {
        this.mapInstance = new HashMap<String,Device>();
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
            device = new Device();
            this.mapInstance.put(routerId, device);
        }
        return device;
    }

    @Override
    public Device addDevice(Device device) {
        if(null == device) {
            return null;
        }
        return addDevice(device.getDeviceName(), device.getRouterId());
    }

    @Override
    public Device getDevice(String routerId) {
        Device device = null;
        if(null != routerId) {
            device = this.mapInstance.get(routerId);
        }
        return device;
    }

    @Override
    public Device getDeviceByName(String deviceName) {
        Device device = null;
        Map.Entry entry = null;
        if(null == deviceName){
            return null;
        }
        Iterator iter = mapInstance.entrySet().iterator();
        while(iter.hasNext())
        {
            entry = (Map.Entry) iter.next();
            device = (Device)entry.getValue();
            if(true == deviceName.equals(device.getDeviceName()))
            {
                return device;
            }
        }
        return null;
    }

    @Override
    public Device getDeviceByIP(String deviceIP) {
        Device device = null;
        Map.Entry entry = null;
        if(null == deviceIP){
            return null;
        }
        Iterator iter = mapInstance.entrySet().iterator();
        while(iter.hasNext())
        {
            entry = (Map.Entry) iter.next();
            device = (Device)entry.getValue();
            if(true == deviceIP.equals(device.getNetConf().getIp().getAddress()))
            {
                return device;
            }
        }
        return null;
    }

    @Override
    public boolean delDevice(String routerId) {
        Device device = null;
        Map.Entry entry = null;
        if(null == routerId){
            return false;
        }
        Iterator iter = mapInstance.entrySet().iterator();
        while(iter.hasNext())
        {
            entry = (Map.Entry) iter.next();
            device = (Device)entry.getValue();
            if(true == routerId.equals(device.getRouterId()))
            {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delDeviceByName(String deviceName) {
        Device device = null;
        Map.Entry entry = null;
        if(null == deviceName){
            return false;
        }
        Iterator iter = mapInstance.entrySet().iterator();
        while(iter.hasNext())
        {
            entry = (Map.Entry) iter.next();
            device = (Device)entry.getValue();
            if(true == deviceName.equals(device.getDeviceName()))
            {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delDeviceByIP(String deviceIP) {
        Device device = null;
        Map.Entry entry = null;
        if(null == deviceIP){
            return false;
        }
        Iterator iter = mapInstance.entrySet().iterator();
        while(iter.hasNext())
        {
            entry = (Map.Entry) iter.next();
            device = (Device)entry.getValue();
            if(true == deviceIP.equals(device.getNetConf().getIp().getAddress()))
            {
                iter.remove();
                return true;
            }
        }
        return true;
    }
}
