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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetConfManagerImpl implements NetConfManager {

    private static NetConfManager instance = null;
    public static Map<String, NetconfClient> netconfClientMap = new HashMap<>();
    private NetconfDevice netconfController;
    private NetconfClient netconfClient;
    public static Map<String, NetConf> netConfMap = new HashMap<>();

    private NetConfManagerImpl() {
        netconfController = new NetconfDevice();
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
    public NetConf addDevice(NetConf netConf) {
        netConfMap.put(netConf.getIp().getAddress(), netConf);
        boolean connect = true;
        if (netconfClientMap.containsKey(netConf.getIp())) {
            if (netconfClientMap.get(netConf.getIp()).isFlag()) {
                connect = false;
            }
        }
        if (connect) {
            netconfClient = netconfController.createClient(netConf.getIp().getAddress(), netConf.getPort(), netConf.getIp().getAddress(), netConf.getUser(), netConf.getPassword());
            if (SessionListener.sessionList.contains(netconfClient.getSessionId())) {
                netconfClient.setFlag(true);
                netConf.setStatus(NetConfStatusEnum.Connected);
            }
            netconfClientMap.put(netConf.getIp().getAddress(), netconfClient);
        }
        return netConf;
    }

    @Override
    public List<NetConf> getDevices() {
        for (String key : netConfMap.keySet()) {
            if (NetConfStatusEnum.Disconnect.equals(netConfMap.get(key).getStatus())) {
                reconnect(key);
            }
        }
        return (List<NetConf>) netConfMap.values();
    }

    @Override
    public List<NetconfClient> getNetconClients() {
        for (String key : netconfClientMap.keySet()) {
            if (!netconfClientMap.get(key).isFlag()) {
                reconnect(key);
            }
        }
        return (List<NetconfClient>) netconfClientMap.values();
    }

    @Override
    public NetconfClient getNetconClient(String ip) {
        NetconfClient netconfClient = null;
        if (netconfClientMap.containsKey(ip)) {
            if (!netconfClientMap.get(ip).isFlag()) {
                reconnect(ip);
            }
            netconfClient = netconfClientMap.get(ip);
        }
        return netconfClient;
    }

    private void reconnect(String ip) {
        NetConf netConf;
        if (netConfMap.containsKey(ip)) {
            netConf = netConfMap.get(ip);
            netconfClient = netconfController.createClient(netConf.getIp().getAddress(), netConf.getPort(), netConf.getIp().getAddress(), netConf.getUser(), netConf.getPassword());
            if (SessionListener.sessionList.contains(netconfClient.getSessionId())) {
                netconfClient.setFlag(true);
                netConf.setStatus(NetConfStatusEnum.Connected);
                netConfMap.put(ip, netConf);
            }
            netconfClientMap.put(ip, netconfClient);
        }
    }

    @Override
    public NetConf getDevice(String ip) {
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
    public void deleteDevice(NetConf netConf) {
        if (netconfClientMap.containsKey(netConf.getIp().getAddress())) {
            netconfClientMap.get(netConf.getIp().getAddress()).clientSession.close();
            netconfClientMap.remove(netConf.getIp().getAddress());
        }
        if (netConfMap.containsKey(netConf.getIp().getAddress())) {
            netConfMap.remove(netConf.getIp().getAddress());
        }
    }
}
