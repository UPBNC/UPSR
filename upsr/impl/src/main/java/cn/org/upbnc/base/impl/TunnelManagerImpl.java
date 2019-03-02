/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.TunnelManager;
import cn.org.upbnc.entity.BfdSession;
import cn.org.upbnc.entity.Tunnel;
import cn.org.upbnc.enumtype.BfdTypeEnum;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.SSrTeTunnel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TunnelManagerImpl implements TunnelManager {
    private static TunnelManager instance = null;
    private Map<String, Map<String, Tunnel>> tunnelMap;
    private Map<Integer, BfdSession> bfdSessionMap;
    private final int MAX_LOCAL= 65535;

    private TunnelManagerImpl() {
        this.tunnelMap = new ConcurrentHashMap<>();
        this.bfdSessionMap = new HashMap<Integer, BfdSession>();
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

    @Override
    public boolean addBfdSession(BfdSession bfdSession){
        this.bfdSessionMap.put(Integer.parseInt(bfdSession.getDiscriminatorLocal()),bfdSession);
        return true;
    }

    @Override
    public boolean deleteBfdSession(String name){
        Iterator<BfdSession> iterator = this.bfdSessionMap.values().iterator();
        while (iterator.hasNext()){
            BfdSession bfdSession = iterator.next();
            if(bfdSession.getBfdName().equals(name)){
                this.bfdSessionMap.remove(Integer.parseInt(bfdSession.getDiscriminatorLocal()));
            }
        }
        return true;
    }

    @Override
    public boolean isBfdDiscriminatorLocal(Integer local){
        return this.bfdSessionMap.containsKey(local);
    }

    @Override
    public Integer getBfdDiscriminatorLocal(){
        Integer i = 1;
        while(i<MAX_LOCAL){
            if(!this.bfdSessionMap.containsKey(i)){
                return i;
            }
            i = i + 1;
        }
        return null;
    }

    @Override
    public boolean isBfdDiscriminatorLocalUsed(int i){
        if(this.bfdSessionMap.containsKey(i)){
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean createTunnels(List<Tunnel> tunnels, NetconfClient netconfClient){
        boolean isCreate = false;

        if(null != tunnels && !tunnels.isEmpty()){
            isCreate = this.createTunnelListToDevice(tunnels,netconfClient);

            if(isCreate){
                for(Tunnel t : tunnels){
                    Map<String, Tunnel> map = this.tunnelMap.get(t.getDevice().getRouterId());
                    if( null == map) {
                        map = new ConcurrentHashMap<>();
                        this.tunnelMap.put(t.getDevice().getRouterId(),map);
                    }
                    map.put(t.getTunnelName(), t);
                }
            }
        }

        return isCreate;
    }

    private boolean createTunnelListToDevice(List<Tunnel> tunnels, NetconfClient netconfClient){
        List<SSrTeTunnel> srTeTunnels = new ArrayList<>();
        for(Tunnel t : tunnels){
            srTeTunnels.add(this.tunnelToSSrTeTunnel(t));
        }

        return true;
    }

    private SSrTeTunnel tunnelToSSrTeTunnel(Tunnel tunnel){
        SSrTeTunnel ret = new SSrTeTunnel();

        ret.setTunnelName(tunnel.getTunnelName());
        ret.setMplsTunnelEgressLSRId(tunnel.getEgressLSRId());
        ret.setMplsTunnelIndex(tunnel.getTunnelId());
        ret.setMplsTunnelBandwidth(tunnel.getBandWidth());

        if(tunnel.getBfdType() == BfdTypeEnum.Dynamic.getCode()) {

            ret.setMplsTeTunnelBfdMinTx(tunnel.getDynamicBfd().getMinSendTime());
            ret.setMplsTeTunnelBfdMinnRx(tunnel.getDynamicBfd().getMinRecvTime());
            ret.setMplsTeTunnelBfdDetectMultiplier(tunnel.getDynamicBfd().getMultiplier());
        }


        return ret;
    }
}
