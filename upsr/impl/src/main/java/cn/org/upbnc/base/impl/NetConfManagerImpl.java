/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.entity.NetConf;
import cn.org.upbnc.enumtype.NetConfStatusEnum;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.NetconfDevice;
import cn.org.upbnc.util.netconf.SessionListener;
import cn.org.upbnc.util.xml.RouterIdXml;
import org.opendaylight.controller.config.util.xml.XmlUtil;
import org.opendaylight.netconf.api.NetconfMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetConfManagerImpl implements NetConfManager {
    private static final Logger LOG = LoggerFactory.getLogger(NetConfManagerImpl.class);
    private static NetConfManager instance = null;
    public static Map<String, NetconfClient> netconfClientMap = new HashMap<>();
    public static NetconfDevice netconfController = new NetconfDevice();
    private NetconfClient netconfClient;
    public static Map<String, NetConf> netConfMap = new HashMap<>();

    private NetConfManagerImpl() {
        if (null == netconfController) {
            netconfController = new NetconfDevice();
        }
    }


    public static NetConfManager getInstance() {
        if (null == instance) {
            instance = new NetConfManagerImpl();
        }
        return instance;
    }

    @Override
    public NetConf createNetConfConnect() {
        return new NetConf();
    }

    @Override
    public synchronized NetConf addDevice(NetConf netConf) {
        boolean flag = false;
        NetConf netconf = new NetConf();
        String key = netConf.getIp().getAddress();
        if (netconfClientMap.containsKey(netConf.getIp().getAddress())) {
            if (netconfClientMap.get(netConf.getIp().getAddress()).isFlag()) {
                netConfMap.get(key).setStatus(NetConfStatusEnum.Disconnect);
                netconfClientMap.get(key).setFlag(false);
                if (netconfClientMap.get(key).clientSession.isUp()) {
                    netconfClientMap.get(key).clientSession.close();
                }
            }
        }
        netconfClient = netconfController.createClient(netConf.getIp().getAddress(), netConf.getPort(), netConf.getIp().getAddress(), netConf.getUser(), netConf.getPassword());
        if (netconfClient == null) {
            LOG.info("ip:" + netConf.getIp().getAddress() + "  port: " + netConf.getPort() + " userName: " + netConf.getUser());
        } else {
            if (null == netConf.getDevice()) {
                flag = true;
                if (SessionListener.sessionList.contains(netconfClient.getSessionId())) {
                    netconfClient.setFlag(true);
                    netConf.setStatus(NetConfStatusEnum.Connected);
                    netconf.setStatus(NetConfStatusEnum.Connected);
                }
                netconfClientMap.put(netConf.getIp().getAddress(), netconfClient);
            } else {
                flag = checkRouterId(netConf.getDevice().getRouterId(), netconfClient);
                if (flag) {
                    if (SessionListener.sessionList.contains(netconfClient.getSessionId())) {
                        netconfClient.setFlag(true);
                        netConf.setStatus(NetConfStatusEnum.Connected);
                        netconf.setStatus(NetConfStatusEnum.Connected);
                    }
                    netconfClientMap.put(netConf.getIp().getAddress(), netconfClient);
                } else {
                    netconfClient.clientSession.close();
                }
            }
        }
        if (flag) {
            netConfMap.put(netConf.getIp().getAddress(), netConf);
        }
        return netconf;
    }

    private boolean checkRouterId(String routerId, NetconfClient netconfClient) {
        boolean flag = false;
        String sendMsg = RouterIdXml.getRouterIdXml();
        LOG.info("sendMsg={}", new Object[]{sendMsg});
        String result;
        String getRouterId;
        try {
            Document doc = XmlUtil.readXmlToDocument(sendMsg);
            NetconfMessage message = netconfClient.sendMessage(new NetconfMessage(doc));
            result = XmlUtil.toString(message.getDocument());
            LOG.info("result :" + result);
            getRouterId = RouterIdXml.getRouterIdFromXml(result);
            LOG.info("getRouterId :" + getRouterId);
            if (getRouterId.equals(routerId)) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    @Override
    public synchronized List<NetConf> getDevices() {
        for (String key : netConfMap.keySet()) {
            if (NetConfStatusEnum.Disconnect.equals(netConfMap.get(key).getStatus())) {
                reconnect(key);
            }
        }
        return (List<NetConf>) netConfMap.values();
    }

    @Override
    public synchronized List<NetconfClient> getNetconClients() {
        for (String key : netconfClientMap.keySet()) {
            if (!netconfClientMap.get(key).isFlag()) {
                reconnect(key);
            }
        }
        return (List<NetconfClient>) netconfClientMap.values();
    }

    @Override
    public synchronized NetconfClient getNetconClient(String ip) {
        NetconfClient netconfClient = null;
        if (netconfClientMap.containsKey(ip)) {
            if (!netconfClientMap.get(ip).isFlag()) {
                reconnect(ip);
            }
            netconfClient = netconfClientMap.get(ip);
        }
        return netconfClient;
    }

    private synchronized void reconnect(String ip) {
        NetConf netConf;
        boolean flag = false;
        if (netConfMap.containsKey(ip)) {
            netConf = netConfMap.get(ip);
            netconfClient = netconfController.createClient(netConf.getIp().getAddress(), netConf.getPort(), netConf.getIp().getAddress(), netConf.getUser(), netConf.getPassword());
            if (netconfClient == null) {
                LOG.info("ip:" + netConf.getIp().getAddress() + "  port: " + "netConf.getPort()" + " userName: " + netConf.getUser() + "  password : " + netConf.getPassword());
            } else {
                if (null == netConf.getDevice()) {
                    if (SessionListener.sessionList.contains(netconfClient.getSessionId())) {
                        netconfClient.setFlag(true);
                        netConf.setStatus(NetConfStatusEnum.Connected);
                        netConfMap.put(ip, netConf);
                    }
                    netconfClientMap.put(ip, netconfClient);
                } else {
                    flag = checkRouterId(netConf.getDevice().getRouterId(), netconfClient);
                    if (flag) {
                        if (SessionListener.sessionList.contains(netconfClient.getSessionId())) {
                            netconfClient.setFlag(true);
                            netConf.setStatus(NetConfStatusEnum.Connected);
                            netConfMap.put(ip, netConf);
                        }
                        netconfClientMap.put(ip, netconfClient);
                    } else {
                        netconfClient.clientSession.close();
                    }
                }

            }
        }
    }

    @Override
    public synchronized NetConf getDevice(String ip) {
        NetConf netConf = new NetConf();
        if (netConfMap.containsKey(ip)) {
            if (NetConfStatusEnum.Disconnect.equals(netConfMap.get(ip).getStatus())) {
                reconnect(ip);
            }
            netConf = netConfMap.get(ip);
        }
        return netConf;
    }

    @Override
    public synchronized void deleteDevice(NetConf netConf) {
        if (netconfClientMap.containsKey(netConf.getIp().getAddress())) {
            netconfClientMap.get(netConf.getIp().getAddress()).clientSession.close();
            netconfClientMap.remove(netConf.getIp().getAddress());
        }
        if (netConfMap.containsKey(netConf.getIp().getAddress())) {
            netConfMap.remove(netConf.getIp().getAddress());
        }
    }

    @Override
    public void close() {
        for (String key : netconfClientMap.keySet()) {
            netconfClientMap.get(key).clientSession.close();
        }
    }

    @Override
    public void closeNetconfByRouterId(String routerId) {
        for (String key : netConfMap.keySet()) {
            if (null == netConfMap.get(key).getDevice()) {
                netConfMap.get(key).setStatus(NetConfStatusEnum.Disconnect);
//                netconfClientMap.get(key).setFlag(false);
//                LOG.info("netConfMap(before) size is : " + netConfMap.size());
//                LOG.info("netConfMap(after) size is : " + netConfMap.size());
//                if (netconfClientMap.get(key).clientSession.isUp()) {
//                    LOG.info("clientSession is up ");
//                    netconfClientMap.get(key).clientSession.close();
//                }
                return;
            } else {
                if (netConfMap.get(key).getDevice().getRouterId().equals(routerId)) {
                    netConfMap.get(key).setStatus(NetConfStatusEnum.Disconnect);
                    netconfClientMap.get(key).setFlag(false);
                    if (netconfClientMap.get(key).clientSession.isUp()) {
                        netconfClientMap.get(key).clientSession.close();
                    }
                    return;
                }
            }

        }
    }
}
