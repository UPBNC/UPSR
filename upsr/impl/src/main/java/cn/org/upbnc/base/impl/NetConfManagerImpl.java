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
    public static Map<String, NetConf> netconfMap = new HashMap<>();

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
        String key = netConf.getRouterID();
        if (netconfClientMap.containsKey(key)) {
            if (netconfClientMap.get(key).isFlag()) {
                netConfMap.get(key).setStatus(NetConfStatusEnum.Unknown);
                netconfClientMap.get(key).setFlag(false);
                if (netconfClientMap.get(key).clientSession.isUp()) {
                    netconfClientMap.get(key).clientSession.close();
                }
            }
        }
        try {
            netconfClient = netconfController.createClient(netConf.getIp().getAddress(), netConf.getPort(), netConf.getIp().getAddress(), netConf.getUser(), netConf.getPassword());
        } catch (Exception e) {
            LOG.info(" e.getCause :" + e.getCause());
            String cause = String.valueOf(e.getCause());
            if (cause.contains("Unable to create")) {
                netConf.setStatus(NetConfStatusEnum.IporPortError);
            }
            if (cause.contains("Timeout")) {
                netConf.setStatus(NetConfStatusEnum.UserNameOrPasswordError);
            }
        }
        if (netconfClient.label.equals(netConf.getIp().getAddress())) {
            if (netconfClient == null) {
                LOG.info("ip:" + netConf.getIp().getAddress() + "  port: " + netConf.getPort() + " userName: " + netConf.getUser() + "  password : " + netConf.getPassword());
            } else {
                flag = checkRouterId(netConf.getDevice().getRouterId(), netconfClient);
                if (flag) {
                    if (SessionListener.sessionList.contains(netconfClient.getSessionId())) {
                        netconfClient.setFlag(true);
                        netConf.setStatus(NetConfStatusEnum.Connected);
                        netconf.setStatus(NetConfStatusEnum.Connected);
                    }
                    netconfClientMap.put(key, netconfClient);
                } else {
                    netConf.setStatus(NetConfStatusEnum.RouterIdNotMatchIp);
                    netconfClient.clientSession.close();
                }
            }
        }
            if (flag) {
            netConfMap.put(key, netConf);
        }
        netconfMap.put(netConf.getRouterID(), netConf);
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
            if (!(NetConfStatusEnum.Connected.equals(netConfMap.get(key).getStatus()))) {
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
    public synchronized NetconfClient getNetconClient(String routerID) {
        NetconfClient netconfClient = null;
        if (netconfClientMap.containsKey(routerID)) {
            if (!netconfClientMap.get(routerID).isFlag()) {
                reconnect(routerID);
            }
            netconfClient = netconfClientMap.get(routerID);
        }
        return netconfClient;
    }

    private synchronized void reconnect(String routerID) {
        NetConf netConf;
        boolean flag = false;
        if (netConfMap.containsKey(routerID) && netconfClientMap.containsKey(routerID)) {
            netConf = netConfMap.get(routerID);
            try {
                netconfClient = netconfController.createClient(netConf.getIp().getAddress(), netConf.getPort(), netConf.getIp().getAddress(), netConf.getUser(), netConf.getPassword());
            } catch (Exception e) {
                e.printStackTrace();
                ///
            }
            if (netconfClient == null) {
                LOG.info("ip:" + netConf.getIp().getAddress() + "  port: " + netConf.getPort() + " userName: " + netConf.getUser() + "  password : " + netConf.getPassword());
            } else {
                flag = checkRouterId(netConf.getDevice().getRouterId(), netconfClient);
                if (flag) {
                    if (SessionListener.sessionList.contains(netconfClient.getSessionId())) {
                        netconfClient.setFlag(true);
                        netConf.setStatus(NetConfStatusEnum.Connected);
                        netConfMap.put(routerID, netConf);
                    }
                    netconfClientMap.put(routerID, netconfClient);
                } else {
                    netconfClient.clientSession.close();
                }

            }
        }
    }

    @Override
    public synchronized NetConf getDevice(String routerID) {
        NetConf netConf = new NetConf();
        if (netconfMap.containsKey(routerID)) {
            if (netConfMap.containsKey(routerID) && netconfClientMap.containsKey(routerID)) {
                if (!(NetConfStatusEnum.Connected.equals(netConfMap.get(routerID).getStatus()))) {
                    reconnect(routerID);
                }
                netConf = netConfMap.get(routerID);
            } else {
                netConf = netconfMap.get(routerID);
            }
            if (netConfMap.containsKey(routerID)) {
                if (NetConfStatusEnum.Disconnected.equals(netConfMap.get(routerID).getStatus())) {
                    reconnect(routerID);
                }
                netConf = netConfMap.get(routerID);
            }
        }
        return netConf;
    }

    @Override
    public synchronized void deleteDevice(NetConf netConf) {
        if (netconfClientMap.containsKey(netConf.getRouterID())) {
            netconfClientMap.get(netConf.getRouterID()).clientSession.close();
            netconfClientMap.remove(netConf.getRouterID());
        }
        if (netConfMap.containsKey(netConf.getRouterID())) {
            netConfMap.remove(netConf.getRouterID());
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
//                netConfMap.get(key).setStatus(NetConfStatusEnum.Disconnected);
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
//                    netConfMap.get(key).setStatus(NetConfStatusEnum.Disconnected);
                    if (netconfClientMap.containsKey(key)) {
                        netconfClientMap.get(key).setFlag(false);
                        if (netconfClientMap.get(key).clientSession.isUp()) {
                            netconfClientMap.get(key).clientSession.close();
                        }
                    }
                    return;
                }
            }

        }
    }
}
