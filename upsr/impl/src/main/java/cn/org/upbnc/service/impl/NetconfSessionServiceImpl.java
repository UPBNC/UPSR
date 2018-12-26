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
import cn.org.upbnc.base.IniSectionManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.entity.Address;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.NetConf;
import cn.org.upbnc.enumtype.AddressTypeEnum;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.NetConfStatusEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.service.NetconfSessionService;
import cn.org.upbnc.service.entity.NetconfSession;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.xml.HostNameXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class NetconfSessionServiceImpl implements NetconfSessionService {
    private static final Logger LOG = LoggerFactory.getLogger(NetconfSessionServiceImpl.class);
    private static NetconfSessionService ourInstance = new NetconfSessionServiceImpl();
    private BaseInterface baseInterface;
    private NetConfManager netConfManager;
    private DeviceManager deviceManager;
    private IniSectionManager iniSectionManager;
    private String disConnected = "未连接";
    private String connected = "已连接";
    private int netconfSession_seq_min = 1;
    private int netconfSession_seq_max = 100;

    public static NetconfSessionService getInstance() {
        if (null == ourInstance) {
            ourInstance = new NetconfSessionServiceImpl();
        }
        return ourInstance;
    }

    private NetconfSessionServiceImpl() {
        this.baseInterface = null;
        this.netConfManager = null;
        this.deviceManager = null;
        this.iniSectionManager = null;
    }

    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        if (null == baseInterface) {
            return false;
        }
        try {
            this.baseInterface = baseInterface;
            this.netConfManager = baseInterface.getNetConfManager();
            this.deviceManager = baseInterface.getDeviceManager();
            this.iniSectionManager = baseInterface.getIniSectionManager();
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
        return true;
    }


    @Override
    public Map<String, Object> updateNetconfSession(NetconfSession netconfSession) {
        String routerId = netconfSession.getRouterId();
        String deviceName = netconfSession.getDeviceName();
        String deviceDesc = netconfSession.getDeviceDesc();
        String deviceType = netconfSession.getDeviceType();
        String deviceIP = netconfSession.getDeviceIP();
        Integer devicePort = netconfSession.getDevicePort();
        String userName = netconfSession.getUserName();
        String userPassword = netconfSession.getUserPassword();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        resultMap.put(ResponseEnum.BODY.getName(), false);
        resultMap.put(ResponseEnum.MESSAGE.getName(), "error");
        NetConf netconf = null;
        Device device = this.deviceManager.getDevice(routerId);
        if (null != device) {
            device.setDeviceName(deviceName);
            device.setDataCenter(deviceDesc);
            device.setDeviceType(deviceType);

            netconf = device.getNetConf();
            if ((null != netconf) && ((true != deviceIP.equals(netconf.getIp().getAddress())) || (true != devicePort.equals(netconf.getPort()))
                    || (true != userName.equals(netconf.getUser())) || (true != userPassword.equals(netconf.getPassword())))) {
                netconf.setUser(userName);
                if (true != userPassword.equals("")) {
                    netconf.setPassword(userPassword);
                }
                netconf.setPort(devicePort);
                netconf.setIp(new Address(deviceIP, AddressTypeEnum.V4));

            }
            if (null == netconf) {
                netconf = new NetConf(deviceIP, devicePort, userName, userPassword);
                device.setNetConf(netconf);
            }

        } else {
            netconf = new NetConf(deviceIP, devicePort, userName, userPassword);
            device = this.deviceManager.addDevice(deviceName, routerId);
            if ((null == device)) {
                return resultMap;
            }
            device.setDataCenter(deviceDesc);
            device.setDeviceType(deviceType);
            device.setNetConf(netconf);
        }
        this.netConfManager.addDevice(netconf);
        saveNetconfSession(routerId, deviceName, deviceDesc, deviceType, deviceIP, devicePort, userName, userPassword);

        if ((null != netconf) && (null != netconf.getIp())) {
            NetConf netconfStat = this.netConfManager.getDevice(netconf.getIp().getAddress());
            String connStatus = (NetConfStatusEnum.Connected == netconfStat.getStatus()) ? "" +
                    "" +
                    "" : disConnected;
            netconf.setStatus(netconfStat.getStatus());
            if (NetConfStatusEnum.Connected == netconfStat.getStatus()) {
                NetconfClient netconfClient = this.netConfManager.getNetconClient(netconf.getIp().getAddress());
                String sendMsg = HostNameXml.getHostNameXml();
                LOG.info("get sendMsg= " + sendMsg + "\n");
                String result = netconfController.sendMessage(netconfClient, sendMsg);
                LOG.info("get result=" + result + "\n");
                String sysName = HostNameXml.getHostNameFromXml(result);
                device.setSysName(sysName);
            }
        }
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(), true);
        resultMap.put(ResponseEnum.MESSAGE.getName(), "success");
        return resultMap;
    }

    @Override
    public Map<String, Object> delNetconfSession(String routerId) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        resultMap.put(ResponseEnum.BODY.getName(), false);
        resultMap.put(ResponseEnum.MESSAGE.getName(), "error");
        LOG.info("routerId = {}", new Object[]{routerId});
        Device device = this.deviceManager.getDevice(routerId);
        if (null != device) {
            if (null != device.getNetConf()) {
                this.netConfManager.deleteDevice(device.getNetConf());
            }
            this.deviceManager.delDevice(routerId);
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
            resultMap.put(ResponseEnum.BODY.getName(), true);
            resultMap.put(ResponseEnum.MESSAGE.getName(), "success");
            return resultMap;
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getNetconfSession(String routerId) {
        Map<String, Object> resultMap = new HashMap<>();
        LOG.info("routerId = {}", new Object[]{routerId});
        NetconfSession netconfSession = null;
        Device device = this.deviceManager.getDevice(routerId);
        if (null != device) {
            netconfSession = new NetconfSession();
            netconfSession.setRouterId(device.getRouterId());
            netconfSession.setDeviceName(device.getDeviceName());
            netconfSession.setDeviceDesc(device.getDataCenter());
            netconfSession.setDeviceType(device.getDeviceType());
            netconfSession.setSysName(device.getSysName());
            if (null != device.getNetConf()) {

                netconfSession.setDeviceIP(device.getNetConf().getIp().getAddress());
                netconfSession.setDevicePort(device.getNetConf().getPort());
                netconfSession.setUserName(device.getNetConf().getUser());
                String connStatus = disConnected;
                //get status from device for avoiding connect break in use

                NetConf netconf = device.getNetConf(); // can't be null

                long start = System.currentTimeMillis();
                NetConf device_netconf = this.netConfManager.getDevice(netconf.getIp().getAddress());
                long end = System.currentTimeMillis();
                long timelong = end - start;
                LOG.info("getDevice time long is " + timelong);
                if ((null != netconf.getIp()) && (null != device_netconf)) {
                    NetConfStatusEnum netConfStatus = device_netconf.getStatus();
                    connStatus = (NetConfStatusEnum.Connected == netConfStatus) ? connected : disConnected;
                    netconf.setStatus(netConfStatus);
                }
                netconfSession.setStatus(connStatus);
            }
        }
        resultMap.put(ResponseEnum.CODE.getName(), "1");
        resultMap.put(ResponseEnum.MESSAGE.getName(), "ok");
        resultMap.put(ResponseEnum.BODY.getName(), netconfSession);
        return resultMap;
    }

    @Override
    public Map<String, Object> getNetconfSession() {
        Map<String, Object> resultMap = new HashMap<>();
        NetconfSession netconfSession = null;
        List<NetconfSession> netconfSessionList = null;
        List<Device> deviceList = this.deviceManager.getDeviceList();
        if ((null == deviceList) || (0 == deviceList.size())) {
            return null;
        }
        netconfSessionList = new LinkedList<NetconfSession>();
        for (Device device : deviceList) {
            netconfSession = (NetconfSession) getNetconfSession(device.getRouterId()).get(ResponseEnum.BODY.getName());
            if (null != netconfSession) {
                netconfSessionList.add(netconfSession);
            }
        }
        resultMap.put(ResponseEnum.CODE.getName(), "1");
        resultMap.put(ResponseEnum.MESSAGE.getName(), "ok");
        resultMap.put(ResponseEnum.BODY.getName(), netconfSessionList);
        return resultMap;
    }

    @Override
    public String toString() {
        return "NetconfSessionServiceImpl{" +
                "baseInterface=" + baseInterface +
                ", netConfManager=" + netConfManager +
                ", deviceManager=" + deviceManager +
                '}';
    }

    private boolean saveNetconfSession(String routerId, String deviceName, String deviceDesc, String deviceType, String deviceIP, Integer devicePort, String userName, String userPassword) {
        int seq = 0;
        for (seq = netconfSession_seq_min; seq < netconfSession_seq_max; seq++) {
            String sectionName = "netconfSession_" + seq;
            String value = this.iniSectionManager.getValue(sectionName, "routerId", null);
            if ((null != value) && (false == value.equals(routerId))) {
                continue;
            }

            this.iniSectionManager.setValue(sectionName, "routerId", routerId);
            this.iniSectionManager.setValue(sectionName, "deviceName", deviceName);
            this.iniSectionManager.setValue(sectionName, "centerName", deviceDesc);
            this.iniSectionManager.setValue(sectionName, "deviceType", deviceType);
            this.iniSectionManager.setValue(sectionName, "sshIP", deviceIP);
            this.iniSectionManager.setValue(sectionName, "sshPort", devicePort.toString());
            this.iniSectionManager.setValue(sectionName, "userName", userName);
            if (true != userPassword.equals("")) {
                this.iniSectionManager.setValue(sectionName, "passWord", userPassword);
            }
            break;
        }
        this.iniSectionManager.storeFile();
        return true;
    }

    public boolean recoverNetconfSession() {
        int seq = 0;
        String sectionName = null;
        String routerId = null, deviceName = null, deviceDesc = null, deviceType = null;
        String deviceIP = null, devicePort = null;
        String userName = null, passWord = null;
        NetconfSession netconfSession;
        for (seq = netconfSession_seq_min; seq < netconfSession_seq_max; seq++) {
            sectionName = "netconfSession_" + seq;
            routerId = this.iniSectionManager.getValue(sectionName, "routerId", null);
            if (null == routerId) {
                break;
            }
            deviceName = this.iniSectionManager.getValue(sectionName, "deviceName", null);
            deviceDesc = this.iniSectionManager.getValue(sectionName, "centerName", null);
            deviceType = this.iniSectionManager.getValue(sectionName, "deviceType", "PE");
            deviceIP = this.iniSectionManager.getValue(sectionName, "sshIP", null);
            devicePort = this.iniSectionManager.getValue(sectionName, "sshPort", null);
            userName = this.iniSectionManager.getValue(sectionName, "userName", null);
            passWord = this.iniSectionManager.getValue(sectionName, "passWord", null);
            netconfSession = new NetconfSession(routerId, deviceName, deviceDesc, deviceType, deviceIP, Integer.parseInt(devicePort), userName, passWord);
            updateNetconfSession(netconfSession);
        }
        return true;
    }
}
