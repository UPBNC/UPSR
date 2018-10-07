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
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.DeviceInterface;
import cn.org.upbnc.service.InterfaceService;
import cn.org.upbnc.service.entity.DevInterfaceInfo;
import cn.org.upbnc.util.netconf.GigabitEthernet;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.xml.VpnXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class InterfaceServiceImpl implements InterfaceService{
    private static final Logger LOG = LoggerFactory.getLogger(NetconfSessionServiceImpl.class);
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
        List<DeviceInterface> deviceInterfaceList = device.getDeviceInterfaceList();
        if(null != deviceInterfaceList)
        {
            for(DeviceInterface deviceInterface:deviceInterfaceList) {
                devInterfaceInfo = new DevInterfaceInfo(deviceInterface.getName(),
                        null,null,null,null,
                        deviceInterface.getStatus(),deviceInterface.getSrStatus(),
                        null,null);
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
        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());

        LOG.info("enter getInterfaceListFromDevice");
        String sendMsg = VpnXml.getGigabitEthernetsXml();
        LOG.info("get sendMsg={}", new Object[]{sendMsg});
        String result = netconfController.sendMessage(netconfClient, sendMsg);
        LOG.info("get result={}", new Object[]{result});
        List<GigabitEthernet> gigabitEthernets = VpnXml.getGigabitEthernetsFromXml(result);
        if(null != gigabitEthernets)
        {
            for (GigabitEthernet gigabitEthernet:gigabitEthernets) {
                devInterfaceInfo = new DevInterfaceInfo(gigabitEthernet.getIfName(),gigabitEthernet.getIfIpAddr(),
                        gigabitEthernet.getSubnetMask(),gigabitEthernet.getIfOperMac(),gigabitEthernet.getVrfName(),null,
                        null, null,null);
                LOG.info("gigabitEthernet getIfName={} getIfIpAddr={} " +
                        "getSubnetMask={} getIfOperMac={} getVrfName={} ",
                        new Object[]{gigabitEthernet.getIfName(), gigabitEthernet.getIfIpAddr(),
                                gigabitEthernet.getSubnetMask(), gigabitEthernet.getIfOperMac(),
                                gigabitEthernet.getVrfName()});
                devInterfaceInfos.add(devInterfaceInfo);
            }
            return devInterfaceInfos;
        }
        return null;
    }

    @Override
    public String syncInterfaceConf() {
        return null;
    }
}
