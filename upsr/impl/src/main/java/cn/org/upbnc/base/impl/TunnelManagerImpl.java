/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.TunnelManager;
import cn.org.upbnc.entity.Tunnel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TunnelManagerImpl implements TunnelManager {
    private static TunnelManager instance = null;
    private static Map<String, Map<String, Tunnel>> tunnelMap = new ConcurrentHashMap<>();

    private TunnelManagerImpl() {
    }

    public static TunnelManager getInstance() {
        if (null == instance) {
            instance = new TunnelManagerImpl();
        }
        return instance;
    }

    @Override
    public Tunnel createTunnel(Tunnel tunnel) {
        Map<String, Tunnel> map;
        if (tunnelMap.containsKey(tunnel.getDevice().getRouterId())) {
            tunnelMap.get(tunnel.getDevice().getRouterId()).put(tunnel.getTunnelName(), tunnel);
        } else {
            map = new ConcurrentHashMap<>();
            map.put(tunnel.getTunnelName(), tunnel);
            tunnelMap.put(tunnel.getDevice().getRouterId(), map);

        }
        return tunnel;
    }

    @Override
    public Tunnel updateTunnel(Tunnel tunnel) {
        if (tunnel == null) {
            return null;
        }
        if (tunnelMap.containsKey(tunnel.getDevice().getRouterId())) {
            tunnelMap.get(tunnel.getDevice().getRouterId()).put(tunnel.getTunnelName(), tunnel);
        } else {
            Map<String, Tunnel> map = new ConcurrentHashMap<>();
            map.put(tunnel.getTunnelName(), tunnel);
            tunnelMap.put(tunnel.getDevice().getRouterId(), map);
        }
        return tunnel;
    }

    @Override
    public List<Tunnel> getTunnel(String routerId, String name) {
        List<Tunnel> tunnelList = new ArrayList<>();
        if (tunnelMap.containsKey(routerId)) {
            if (null == name || "".equals(name)) {
                Collection<Tunnel> collection = tunnelMap.get(routerId).values();
                tunnelList = new ArrayList<Tunnel>(collection);
            } else {
                if (tunnelMap.get(routerId).containsKey(name)) {
                    tunnelList.add(tunnelMap.get(routerId).get(name));
                }
            }
        }
        return tunnelList;
    }

    @Override
    public List<Tunnel> getTunnels() {
        List<Tunnel> tunnelList = new ArrayList<>();
        for (String key : tunnelMap.keySet()) {
            for (String keytunnel : tunnelMap.get(key).keySet()) {
                tunnelList.add(tunnelMap.get(key).get(keytunnel));
            }
        }
        return tunnelList;
    }

    @Override
    public void emptyTunnelsByRouterId(String routerId) {
        tunnelMap.put(routerId, new ConcurrentHashMap());
    }

    @Override
    public boolean deleteTunnel(String routerId, String name) {
        boolean flag = false;
        if (tunnelMap.containsKey(routerId)) {
            if (tunnelMap.get(routerId).containsKey(name)) {
                tunnelMap.get(routerId).remove(name);
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean checkTunnelNameAndId(String routerId, String tunnelName, String tunnelId) {
        boolean flag = false;
        if (tunnelMap.containsKey(routerId)) {
            for (String tunnelKey : tunnelMap.get(routerId).keySet()) {
                if (tunnelKey.equals(tunnelName) || tunnelId.equals(tunnelMap.get(routerId).get(tunnelKey).getTunnelId())) {
                    flag = true;
                }
            }
        }
        return flag;
    }
}
