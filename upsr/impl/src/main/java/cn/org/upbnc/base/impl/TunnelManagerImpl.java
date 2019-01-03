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

public class TunnelManagerImpl implements TunnelManager {
    private static TunnelManager instance = null;
    private static List<Tunnel> tunnels = new ArrayList<>();
    private static Map<String, Map<String, Tunnel>> tunnelMap = new HashMap<>();

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
            map = new HashMap<>();
            map.put(tunnel.getTunnelName(), tunnel);
            tunnelMap.put(tunnel.getDevice().getRouterId(), map);

        }
        return tunnel;
    }

    @Override
    public Tunnel updateTunnel(Tunnel tunnel) {
        return null;
    }

    @Override
    public List<Tunnel> getTunnel(String routerId, String name) {
        List<Tunnel> tunnelList = new ArrayList<>();
        if (null == name || "".equals(name)) {
            if (tunnelMap.containsKey(routerId)) {
                Collection<Tunnel> collection = tunnelMap.get(routerId).values();
                tunnelList = new ArrayList<Tunnel>(collection);
            }
        } else {
            if (tunnelMap.containsKey(routerId)) {
                if (tunnelMap.get(routerId).containsKey(name)) {
                    tunnelList.add(tunnelMap.get(routerId).get(name));
                }
            }

        }
        return tunnelList;
    }

    @Override
    public Map<String, List<Tunnel>> getTunnels() {
        Map<String, List<Tunnel>> maps = new HashMap<>();
        List<Tunnel> tunnelList;
        for (String key : tunnelMap.keySet()) {
            Collection<Tunnel> collection = tunnelMap.get(key).values();
            tunnelList = new ArrayList<Tunnel>(collection);
            maps.put(key, tunnelList);
        }
        return maps;
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
}
