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
import cn.org.upbnc.entity.NetConf;
import cn.org.upbnc.enumtype.AddressTypeEnum;
import cn.org.upbnc.enumtype.NetConfStatusEnum;
import cn.org.upbnc.service.NetconfSessionService;
import cn.org.upbnc.service.entity.NetconfSession;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.xml.HostNameXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class NetconfSessionServiceImpl implements NetconfSessionService{
    private static final Logger LOG = LoggerFactory.getLogger(NetconfSessionServiceImpl.class);
    private static NetconfSessionService ourInstance = new NetconfSessionServiceImpl();
    private BaseInterface  baseInterface;
    private NetConfManager netConfManager;
    private DeviceManager deviceManager;
    public static NetconfSessionService getInstance() {
        if(null == ourInstance)
        {
            ourInstance = new NetconfSessionServiceImpl();
        }
        return ourInstance;
    }

    private NetconfSessionServiceImpl() {
        this.baseInterface = null;
        this.netConfManager = null;
        this.deviceManager = null;

    }

    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        if(null == baseInterface){
            return false;
        }
        try{
            this.baseInterface = baseInterface;
            this.netConfManager = baseInterface.getNetConfManager();
            this.deviceManager = baseInterface.getDeviceManager();
        }
        catch (Exception e)
        {
            LOG.info(e.getMessage());
        }
        return true;
    }


    @Override
    public boolean updateNetconfSession(String routerId, String deviceName, String deviceDesc, String deviceType, String deviceIP, Integer devicePort, String userName, String userPassword) {
        if((null == routerId)||routerId.isEmpty()
                ||(null == deviceName)||deviceName.isEmpty()
                ||(null == deviceIP)||deviceIP.isEmpty()||(0 == devicePort)){
            return false;
        }
        NetConf netconf = null;
        Device device = this.deviceManager.getDevice(routerId);
        if(null != device)
        {
            device.setDeviceName(deviceName);
            device.setDataCenter(deviceDesc);
            device.setDeviceType(deviceType);

            netconf = device.getNetConf();
            if((null != netconf)&&((deviceIP != netconf.getIp().getAddress())||(devicePort != netconf.getPort())
                    ||(userName != netconf.getUser())||(userPassword != netconf.getPassword())))
            {
                netconf.setUser(userName);
                if(true != userPassword.equals("")) {
                    netconf.setPassword(userPassword);
                }
                netconf.setPort(devicePort);
                netconf.setIp(new Address(deviceIP, AddressTypeEnum.V4));

            }
            if(null == netconf) {
                netconf = new NetConf(deviceIP, devicePort, userName, userPassword);
                device.setNetConf(netconf);
            }
            this.netConfManager.addDevice(netconf);

        }
        else
        {
            netconf = new NetConf(deviceIP, devicePort, userName, userPassword);
            device = this.deviceManager.addDevice(deviceName,routerId);
            if((null == netconf)||(null == device)) {
                return false;
            }
            device.setDataCenter(deviceDesc);
            device.setDeviceType(deviceType);
            device.setNetConf(netconf);
            this.netConfManager.addDevice(netconf);
        }
        if((null != netconf) &&(null != netconf.getIp())) {
            NetConf netconfStat = this.netConfManager.getDevice(netconf.getIp().getAddress());
            String connStatus = (NetConfStatusEnum.Connected == netconfStat.getStatus()) ? "connected" :"connecting" ;
            netconf.setStatus(netconfStat.getStatus());
            if(NetConfStatusEnum.Connected == netconfStat.getStatus()) {
                NetconfClient netconfClient = this.netConfManager.getNetconClient(netconf.getIp().getAddress());
                String sendMsg = HostNameXml.getHostNameXml();
                LOG.info("get sendMsg= "+ sendMsg + "\n");
                String result = netconfController.sendMessage(netconfClient, sendMsg);
                LOG.info("get result="+result + "\n");
                String sysName = HostNameXml.getHostNameFromXml(result);
                device.setSysName(sysName);
            }

        }
        return true;
    }

    @Override
    public boolean delNetconfSession(String routerId) {
        if((null == routerId)||routerId.isEmpty()){
            return false;
        }
        LOG.info("routerId = {}", new Object[]{routerId});
        Device device = this.deviceManager.getDevice(routerId);
        if(null != device)
        {
            this.netConfManager.deleteDevice(device.getNetConf());
            this.deviceManager.delDevice(routerId);
            return true;
        }
        return false;
    }

    @Override
    public NetconfSession getNetconfSession(String routerId) {
        if((null == routerId)||routerId.isEmpty()){
            return null;
        }
        LOG.info("routerId = {}", new Object[]{routerId});
        NetconfSession netconfSession = null;
        Device device = this.deviceManager.getDevice(routerId);
        if(null != device)
        {
            netconfSession = new NetconfSession(device.getRouterId(), device.getDeviceName(), device.getDataCenter(),device.getDeviceType(),device.getSysName(), device.getNetConf().getIp().getAddress(),
                    device.getNetConf().getPort(), device.getNetConf().getUser());
            String connStatus = "connecting";
            if(null != device.getNetConf()) {
                connStatus = (NetConfStatusEnum.Connected == device.getNetConf().getStatus()) ? "connected" : "connecting";
            }
            netconfSession.setStatus(connStatus);
        }
        return netconfSession;
    }

    @Override
    public List<NetconfSession> getNetconfSession() {
        NetconfSession netconfSession = null;
        List<NetconfSession> netconfSessionList = null;
        List<Device>   deviceList = this.deviceManager.getDeviceList();
        if((null == deviceList)||(0 == deviceList.size())) {
            return  null;
        }
        netconfSessionList = new LinkedList<NetconfSession>();
        for (Device device:deviceList) {
            netconfSession = getNetconfSession(device.getRouterId());
            if(null != netconfSession)
            {
                netconfSessionList.add(netconfSession);
            }
        }
        return netconfSessionList;
    }

    @Override
    public String toString() {
        return "NetconfSessionServiceImpl{" +
                "baseInterface=" + baseInterface +
                ", netConfManager=" + netConfManager +
                ", deviceManager=" + deviceManager +
                '}';
    }
}
