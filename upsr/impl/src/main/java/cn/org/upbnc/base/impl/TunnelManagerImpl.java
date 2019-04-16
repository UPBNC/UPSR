/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.TunnelManager;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.enumtype.BfdTypeEnum;
import cn.org.upbnc.enumtype.LabelTypeEnum;
import cn.org.upbnc.enumtype.SBfdCfgSessionLinkTypeEnum;
import cn.org.upbnc.enumtype.STunnelPathTypeEnum;
import cn.org.upbnc.util.netconf.*;
import cn.org.upbnc.util.xml.BfdCfgSessionXml;
import cn.org.upbnc.util.xml.CheckXml;
import cn.org.upbnc.util.xml.ExplicitPathXml;
import cn.org.upbnc.util.xml.SrTeTunnelXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class TunnelManagerImpl implements TunnelManager {
    private static final Logger LOG = LoggerFactory.getLogger(TunnelManager.class);
    private static TunnelManager instance = null;
    private Map<String, Map<String, Tunnel>> tunnelMap;
    private Map<String, Map<Integer, BfdSession>> bfdSessionMap;
    private final int MAX_BFD_LOCAL = 65535;
    private final String Link = "Link";
    private final String Linkback = "Linkback";

    private TunnelManagerImpl() {
        this.tunnelMap = new ConcurrentHashMap<>();
        this.bfdSessionMap = new ConcurrentHashMap<String, Map<Integer, BfdSession>>();
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
    public boolean isTunnelNameAndIdUsed(String routerId, String tunnelName, String tunnelId) {
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
    public boolean isBfdDiscriminatorLocal(Integer local) {
        return this.bfdSessionMap.containsKey(local);
    }

    @Override
    public Integer getBfdDiscriminatorLocal() {
        Integer i = 1;
        while (i < MAX_BFD_LOCAL) {
            if (!this.bfdSessionMap.containsKey(i)) {
                return i;
            }
            i = i + 1;
        }
        return null;
    }

    @Override
    public boolean isBfdDiscriminatorLocalUsed(String routerId, Integer i) {
        if (this.bfdSessionMap.containsKey(routerId)) {
            Map<Integer, BfdSession> integerBfdSessionMap = this.bfdSessionMap.get(routerId);
            if (integerBfdSessionMap != null &&
                    integerBfdSessionMap.containsKey(i)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean createTunnels(List<Tunnel> tunnels, String routerId, NetconfClient netconfClient) {
        boolean isCreate = false;
        Map<String, Tunnel> map = this.tunnelMap.get(routerId);
        Map<Integer, BfdSession> bfdMap = this.bfdSessionMap.get(routerId);
        if (null != tunnels && !tunnels.isEmpty() && null != routerId) {

            isCreate = this.createTunnelListTotalToDevice(tunnels, netconfClient);
//            isCreate = this.createTunnelListTotalToDevice(tunnels,netconfClient);
            if (isCreate) {
                if (null == map) {
                    map = new ConcurrentHashMap<>();
                    this.tunnelMap.put(routerId, map);
                }

                if (null == bfdMap) {
                    bfdMap = new ConcurrentHashMap<>();
                    this.bfdSessionMap.put(routerId, bfdMap);
                }

                for (Tunnel t : tunnels) {
                    // add tunnel to map
                    map.put(t.getTunnelName(), t);

                    // add bfd to map
                    if (null != t.getTunnelBfd()) {
                        bfdMap.put(Integer.parseInt(t.getTunnelBfd().getDiscriminatorLocal()), t.getTunnelBfd());
                    }

                    if (null != t.getMasterBfd()) {
                        bfdMap.put(Integer.parseInt(t.getMasterBfd().getDiscriminatorLocal()), t.getMasterBfd());
                    }
                }
            }
        }

        return isCreate;
    }

    @Override
    public boolean deleteTunnels(List<String> tunnels, String routerId, NetconfClient netconfClient) {
        boolean isDelete = false;

        if (null != tunnels && !tunnels.isEmpty() && null != routerId) {
            isDelete = this.deleteTunnelsFromDeviceByNameList(tunnels, routerId, netconfClient);
        }

        return isDelete;
    }

    @Override
    public Map<String, Tunnel> syncTunnelsConf(String routerId, NetconfClient netconfClient) {
        Map<String, Tunnel> ret = null;
        if (null != routerId) {
            // get s bfds from device
            List<SBfdCfgSession> sBfdCfgSessions = this.getBfdSessionsFromDeviceByRouterId(netconfClient);

            // get s tunnels from device
            List<SSrTeTunnel> sSrTeTunnels = this.getTunnelListFromDeviceByRouterId(netconfClient);

            // get s explicit paths from device
            List<SExplicitPath> sExplicitPaths = this.getExplicitPathsFromDeviceByRouterId(netconfClient);

            // get bfds from s bfds
            Map<Integer, BfdSession> bfdSessionMap = this.getBfdSessionFromSBfdCfgSessions(sBfdCfgSessions);
            this.bfdSessionMap.put(routerId, bfdSessionMap);

            // get explicit paths from s explicit paths
            List<ExplicitPath> explicitPaths = this.getExplicitPathsFromSExplicitPaths(sExplicitPaths);

            // tunnels
            List<BfdSession> bfdSessions = new ArrayList<>(bfdSessionMap.values());
            ret = new ConcurrentHashMap<String, Tunnel>();
            for (SSrTeTunnel st : sSrTeTunnels) {
                Tunnel t = this.sSrTeTunnelToTunnel(st, bfdSessions, explicitPaths);
                ret.put(t.getTunnelName(), t);
            }

            this.tunnelMap.put(routerId, ret);
        }
        return ret;
    }

    @Override
    public Map<String, Map<String, Tunnel>> getTunnelMap() {
        return tunnelMap;
    }

    @Override
    public Map<String, Map<Integer, BfdSession>> getBfdSessionMap() {
        return bfdSessionMap;
    }

    private boolean createTunnelListTotalToDevice(List<Tunnel> tunnels, NetconfClient netconfClient) {
        List<SSrTeTunnel> srTeTunnels = new ArrayList<SSrTeTunnel>();
        List<SExplicitPath> explicitPaths = new ArrayList<SExplicitPath>();
        List<SBfdCfgSession> sBfdCfgSessions = new ArrayList<SBfdCfgSession>();
        Map<String, Tunnel> map;
        Map<Integer, BfdSession> bfdMap;

        String routerId = tunnels.get(0).getDevice().getRouterId();


        List<String> pathNames = null;
        List<String> bfdNames;
        Tunnel tunnel;
        map = tunnelMap.get(routerId);
        for (Tunnel t : tunnels) {
            pathNames = new ArrayList<String>();
            bfdNames = new ArrayList<String>();
            if (null != map) {
                tunnel = map.get(t.getTunnelName());
                if (null != tunnel) {
                    if (null != tunnel.getMasterPath()) {
                        String masterPath = tunnel.getMasterPath().getPathName();
                        if (null != masterPath && null == t.getMasterPath().getPathName()) {
                            pathNames.add(masterPath);
                        }
                    }

                    if (null != tunnel.getSlavePath()) {
                        String slavePath = tunnel.getSlavePath().getPathName();
                        if (null != slavePath && null == t.getSlavePath().getPathName()) {
                            pathNames.add(slavePath);
                        }
                    }

                    if (tunnel.getTunnelBfd() != null && null == t.getTunnelBfd()) {
                        bfdNames.add(tunnel.getTunnelBfd().getBfdName());
                    }

                    if (tunnel.getMasterBfd() != null && null == t.getMasterBfd()) {
                        bfdNames.add(tunnel.getMasterBfd().getBfdName());
                    }
                    if (bfdNames.size() > 0) {
                        boolean isDeleteBfdSessions = this.deleteBfdSessionsFromDeviceByNameList(bfdNames, netconfClient);
                        if (isDeleteBfdSessions) {
                            this.deleteBfdSessionsFromBase(bfdNames, routerId);
                        }
                    }
                }

            }
            // Add SExplictPaths
            explicitPaths.addAll(this.tunnelToSExplicitPath(t));

            // Add srTeTunnels
            srTeTunnels.add(this.tunnelToSSrTeTunnel(t));

            // Add SBfdCfgSessions
            sBfdCfgSessions.addAll(this.tunnelToSBfdCfgSession(t));
        }


        boolean isCreateExplicitPaths = this.createExplicitPathsToDevice(explicitPaths, netconfClient);

        if (!isCreateExplicitPaths) {
            return false;
        }


        boolean isCreateTunnels = this.createTunnelListToDevice(srTeTunnels, netconfClient);
        if (!isCreateTunnels) {
            //delete paths
            List<String> list = new ArrayList<>();
            for (SExplicitPath s : explicitPaths) {
                list.add(s.getExplicitPathName());
            }
            this.deleteExplicitPathsFromDeviceByNameList(list, netconfClient);

            return false;
        }


        //delete paths from device
        if (null != pathNames && pathNames.size() > 0) {
            boolean isDeleteExplicitPaths = this.deleteExplicitPathsFromDeviceByNameList(pathNames, netconfClient);
        }


        boolean isCreateBfds = this.createBfdSessionsToDevice(sBfdCfgSessions, netconfClient);
        if (!isCreateBfds) {

            //delete tunnels
            List<String> tunnelList = new ArrayList<>();
            for (SSrTeTunnel sr : srTeTunnels) {
                tunnelList.add(sr.getTunnelName());
            }
            this.deleteTunnelListFromDeviceByNameList(tunnelList, netconfClient);

            //delete paths
            List<String> pathList = new ArrayList<>();
            for (SExplicitPath s : explicitPaths) {
                pathList.add(s.getExplicitPathName());
            }
            this.deleteExplicitPathsFromDeviceByNameList(pathList, netconfClient);
            return false;
        }

        return true;
    }

    private boolean createExplicitPathsToDevice(List<SExplicitPath> explicitPaths, NetconfClient netconfClient) {

        if (explicitPaths.isEmpty()) {
            return true;
        }

        String commandCreateExplicitPathsXml = ExplicitPathXml.createExplicitPathXml(explicitPaths);
        LOG.info("CommandCreateExplicitPathsXml: " + commandCreateExplicitPathsXml);

        String outPutCreateExplicitPathsXml = netconfController.sendMessage(netconfClient, commandCreateExplicitPathsXml);
        LOG.info("OutPutCreateExplicitPathsXml: " + outPutCreateExplicitPathsXml);

        return CheckXml.RESULT_OK.equals(CheckXml.checkOk(outPutCreateExplicitPathsXml));
    }

    private boolean createTunnelListToDevice(List<SSrTeTunnel> srTeTunnels, NetconfClient netconfClient) {
        if (srTeTunnels.isEmpty()) {
            return false;
        }
        String commandCreateTunnelsXml = SrTeTunnelXml.createSrTeTunnelXml(srTeTunnels);
        LOG.info("CommandCreateTunnelsXml: " + commandCreateTunnelsXml);

        String outPutCreateTunnelsXml = netconfController.sendMessage(netconfClient, commandCreateTunnelsXml);
        LOG.info("OutPutCreateTunnelsXml: " + outPutCreateTunnelsXml);

        return CheckXml.RESULT_OK.equals(CheckXml.checkOk(outPutCreateTunnelsXml));
    }

    private boolean createBfdSessionsToDevice(List<SBfdCfgSession> sBfdCfgSessions, NetconfClient netconfClient) {

        if (sBfdCfgSessions.isEmpty()) {
            return true;
        }

        String commandCreateBfdSessionsXml = BfdCfgSessionXml.createBfdCfgSessionsXml(sBfdCfgSessions);
        LOG.info("CommandCreateBfdSessionsXml: " + commandCreateBfdSessionsXml);

        String outPutCreateBfdSessionsXml = netconfController.sendMessage(netconfClient, commandCreateBfdSessionsXml);
        LOG.info("OutPutCreateBfdSessionsXml: " + outPutCreateBfdSessionsXml);

        return CheckXml.RESULT_OK.equals(CheckXml.checkOk(outPutCreateBfdSessionsXml));
    }

    private SSrTeTunnel tunnelToSSrTeTunnel(Tunnel tunnel) {
        SSrTeTunnel ret = new SSrTeTunnel();

        ret.setTunnelName(tunnel.getTunnelName());
        ret.setMplsTunnelEgressLSRId(tunnel.getDestRouterId());
        ret.setMplsTunnelIndex(tunnel.getTunnelId());
        ret.setMplsTunnelBandwidth(tunnel.getBandWidth());
        ret.setTunnelDesc(tunnel.getTunnelDesc());

        TunnelServiceClass tsc = tunnel.getServiceClass();
        if (null != tsc) {
            STunnelServiceClass stsc = new STunnelServiceClass();
            stsc.setAf1ServiceClassEnable(tsc.isAf1());
            stsc.setAf2ServiceClassEnable(tsc.isAf2());
            stsc.setAf3ServiceClassEnable(tsc.isAf3());
            stsc.setAf4ServiceClassEnable(tsc.isAf4());
            stsc.setEfServiceClassEnable(tsc.isEf());
            stsc.setBeServiceClassEnable(tsc.isBe());
            stsc.setCs6ServiceClassEnable(tsc.isCs6());
            stsc.setCs7ServiceClassEnable(tsc.isCs7());
            stsc.setDefaultServiceClassEnable(tsc.isDef());

            ret.setMplsteServiceClass(stsc);
        }

        if (tunnel.getBfdType() == BfdTypeEnum.Dynamic.getCode()) {
            ret.setMplsTeTunnelBfdMinTx(tunnel.getDynamicBfd().getMinSendTime());
            ret.setMplsTeTunnelBfdMinnRx(tunnel.getDynamicBfd().getMinRecvTime());
            ret.setMplsTeTunnelBfdDetectMultiplier(tunnel.getDynamicBfd().getMultiplier());
        } else {
            ret.setMplsTeTunnelBfdMinTx("");
            ret.setMplsTeTunnelBfdMinnRx("");
            ret.setMplsTeTunnelBfdDetectMultiplier("");
        }

        List<SSrTeTunnelPath> srTeTunnelPaths = new ArrayList<>();
        if (null != tunnel.getMasterPath()) {
            SSrTeTunnelPath masterPath = new SSrTeTunnelPath();
            masterPath.setExplicitPathName(tunnel.getMasterPath().getPathName());
            masterPath.setPathType("primary");
            srTeTunnelPaths.add(masterPath);

        }

        if (null != tunnel.getSlavePath()) {
            SSrTeTunnelPath slavePath = new SSrTeTunnelPath();
            slavePath.setExplicitPathName(tunnel.getSlavePath().getPathName());
            slavePath.setPathType("hotStandby");
            srTeTunnelPaths.add(slavePath);
        }

        ret.setSrTeTunnelPaths(srTeTunnelPaths);

        return ret;
    }

    private List<SExplicitPath> tunnelToSExplicitPath(Tunnel tunnel) {
        List<SExplicitPath> ret = new ArrayList<SExplicitPath>();

        if (null != tunnel.getMasterPath()) {
            ExplicitPath masterPath = tunnel.getMasterPath();
            ret.add(this.explicitPathToSExplicitPath(masterPath));
        }

        if (null != tunnel.getSlavePath()) {
            ExplicitPath slavePath = tunnel.getSlavePath();
            ret.add(this.explicitPathToSExplicitPath(slavePath));
        }

        return ret;
    }

    private List<SBfdCfgSession> tunnelToSBfdCfgSession(Tunnel tunnel) {
        List<SBfdCfgSession> ret = new ArrayList<SBfdCfgSession>();
        if (null != tunnel.getTunnelBfd()) {
            BfdSession tunnelBfd = tunnel.getTunnelBfd();
            ret.add(this.bfdSessionToSBfdCfgSession(tunnelBfd, tunnel.getTunnelName()));
        }
        if (null != tunnel.getMasterBfd()) {
            BfdSession masterBfd = tunnel.getMasterBfd();
            ret.add(this.bfdSessionToSBfdCfgSession(masterBfd, tunnel.getTunnelName()));
        }
        return ret;
    }

    private SExplicitPath explicitPathToSExplicitPath(ExplicitPath explicitPath) {
        SExplicitPath ret = new SExplicitPath();

        ret.setExplicitPathName(explicitPath.getPathName());
        List<SExplicitPathHop> explicitPathHops = new ArrayList<>();
        Set<Map.Entry<String, Label>> set = explicitPath.getLabelMap().entrySet();

        for (Map.Entry<String, Label> entry : set) {
            SExplicitPathHop explicitPathHop = new SExplicitPathHop();
            Label label = entry.getValue();
            if (label.getType() == LabelTypeEnum.PREFIX.getCode()) {
                explicitPathHop.setMplsTunnelHopSidLabelType(LabelTypeEnum.PREFIX.getName());
            }
            explicitPathHop.setMplsTunnelHopIndex(entry.getKey());
            explicitPathHop.setMplsTunnelHopSidLabel(entry.getValue().getValue().toString());
            explicitPathHops.add(explicitPathHop);
        }
        ret.setExplicitPathHops(explicitPathHops);

        return ret;
    }

    private SBfdCfgSession bfdSessionToSBfdCfgSession(BfdSession bfdSession, String tunnelName) {
        SBfdCfgSession ret = new SBfdCfgSession();
        ret.setTunnelName(tunnelName);
        ret.setMultiplier(bfdSession.getMultiplier());
        ret.setMinTxInt(bfdSession.getMinSendTime());
        ret.setMinRxInt(bfdSession.getMinRecvTime());
        ret.setLocalDiscr(bfdSession.getDiscriminatorLocal());
        ret.setRemoteDiscr(bfdSession.getDiscriminatorRemote());
        ret.setCreateType("SESS_STATIC");
        ret.setLinkType(this.getBfdLinkType(bfdSession.getType()));
        ret.setSessName(bfdSession.getBfdName());
        return ret;
    }

    private String getBfdLinkType(Integer type) {
        if (type == BfdTypeEnum.Tunnel.getCode()) {
            return "TE_TUNNEL";
        } else {
            return "TE_LSP";
        }
    }


    private boolean deleteTunnelsFromDeviceByNameList(List<String> tunnels, String routerId, NetconfClient netconfClient) {

        List<String> pathNames = new ArrayList<String>();
        List<String> bfdNames = new ArrayList<String>();

        Map<String, Tunnel> tempTunnelsMap = this.tunnelMap.get(routerId);
        for (String tunnelName : tunnels) {
            Tunnel tunnel = tempTunnelsMap.get(tunnelName);
            if (null != tunnel) {
                String masterPath = this.getPathName(tunnel.getTunnelName() + this.Link, tunnel.getMasterPath());
                if (null != masterPath) {
                    pathNames.add(masterPath);
                }

                String slavePath = this.getPathName(tunnel.getTunnelName() + this.Linkback, tunnel.getSlavePath());
                if (null != slavePath) {
                    pathNames.add(slavePath);
                }

                if (tunnel.getTunnelBfd() != null) {
                    bfdNames.add(tunnel.getTunnelBfd().getBfdName());
                }

                if (tunnel.getMasterBfd() != null) {
                    bfdNames.add(tunnel.getMasterBfd().getBfdName());
                }
            }
        }

        //delete bfds from device
        boolean isDeleteBfdSessions = this.deleteBfdSessionsFromDeviceByNameList(bfdNames, netconfClient);
        //delete bfds from map
        if (isDeleteBfdSessions) {
            this.deleteBfdSessionsFromBase(bfdNames, routerId);
        }

        //delete tunnels from device
        boolean isDeleteTunnels = this.deleteTunnelListFromDeviceByNameList(tunnels, netconfClient);
        //delete from map
        if (isDeleteTunnels) {
            this.deleteTunnelsFromBase(tunnels, routerId);
        }

        //delete paths from device
        boolean isDeleteExplicitPaths = this.deleteExplicitPathsFromDeviceByNameList(pathNames, netconfClient);
        //none

        return (isDeleteBfdSessions && isDeleteTunnels && isDeleteExplicitPaths);

    }

    private boolean deleteExplicitPathsFromDeviceByNameList(List<String> names, NetconfClient netconfClient) {
        if (names.isEmpty()) {
            return true;
        }

        String commandDeleteExplicitPathsXml = ExplicitPathXml.getDeleteExplicitPathByNamesXml(names);
        LOG.info("CommandExplicitPathsXml: " + commandDeleteExplicitPathsXml);

        String outPutDeleteTunnelPolicyXml = netconfController.sendMessage(netconfClient, commandDeleteExplicitPathsXml);
        LOG.info("OutPutDeleteTunnelPolicyXml: " + outPutDeleteTunnelPolicyXml);

        return CheckXml.RESULT_OK.equals(CheckXml.checkOk(outPutDeleteTunnelPolicyXml));

    }

    private boolean deleteTunnelListFromDeviceByNameList(List<String> names, NetconfClient netconfClient) {
        if (names.isEmpty()) {
            return false;
        }

        String commandDeleteTunnelsXml = SrTeTunnelXml.getDeleteSrTeTunnelsXml(names);
        LOG.info("CommandDeleteTunnelsXml: " + commandDeleteTunnelsXml);

        String outPutDeleteTunnelsXml = netconfController.sendMessage(netconfClient, commandDeleteTunnelsXml);
        LOG.info("OutPutDeleteTunnelsXml: " + outPutDeleteTunnelsXml);

        return CheckXml.RESULT_OK.equals(CheckXml.checkOk(outPutDeleteTunnelsXml));
    }

    private boolean deleteBfdSessionsFromDeviceByNameList(List<String> names, NetconfClient netconfClient) {
        if (names.isEmpty()) {
            return true;
        }

        String commandDeleteBfdSessionsXml = BfdCfgSessionXml.deleteBfdCfgSessionsXml(names);
        LOG.info("CommandDeleteBfdSessionsXml: " + commandDeleteBfdSessionsXml);

        String outPutDeleteBfdSessionsXml = netconfController.sendMessage(netconfClient, commandDeleteBfdSessionsXml);
        LOG.info("OutPutDeleteBfdSessionsXml: " + outPutDeleteBfdSessionsXml);

        return CheckXml.RESULT_OK.equals(CheckXml.checkOk(outPutDeleteBfdSessionsXml));
    }

    private String getPathName(String tunnelPathName, ExplicitPath path) {
        String pathName = null;

        if (path != null && tunnelPathName.equals(path.getPathName())) {
            return tunnelPathName;
        }

        return pathName;
    }

    private void deleteBfdSessionsFromBase(List<String> names, String routerId) {
        Map<Integer, BfdSession> map = this.bfdSessionMap.get(routerId);
        if (null != map) {
            Map<Integer, BfdSession> newMap = new ConcurrentHashMap<>(map);

            Iterator<BfdSession> iterator = newMap.values().iterator();
            while (iterator.hasNext()) {
                BfdSession bfdSession = iterator.next();
                for (String name : names) {
                    if (bfdSession.getBfdName().equals(name)) {
                        map.remove(Integer.parseInt(bfdSession.getDiscriminatorLocal()));
                    }
                }
            }
        }
        return;
    }

    private void deleteTunnelsFromBase(List<String> names, String routerId) {
        Map<String, Tunnel> map = this.tunnelMap.get(routerId);
        if (null != map) {
            for (String name : names) {
                map.remove(name);
            }
        }
        return;
    }

    private List<SBfdCfgSession> getBfdSessionsFromDeviceByRouterId(NetconfClient netconfClient) {

        String commandGetBfdSessionsXml = BfdCfgSessionXml.getBfdCfgSessionsXml();
        LOG.info("CommandGetBfdSessionsXml: " + commandGetBfdSessionsXml);

        String outPutGetBfdSessionsXml = netconfController.sendMessage(netconfClient, commandGetBfdSessionsXml);
        LOG.info("OutPutGetTunnelsXml: " + outPutGetBfdSessionsXml);
        List<SBfdCfgSession> ret = BfdCfgSessionXml.getBfdCfgSessionsFromXml(outPutGetBfdSessionsXml);

        return ret;
    }

    private List<SSrTeTunnel> getTunnelListFromDeviceByRouterId(NetconfClient netconfClient) {

        String commandGetTunnelsXml = SrTeTunnelXml.getSrTeTunnelXml("");
        LOG.info("CommandGetTunnelsXml: " + commandGetTunnelsXml);

        String outPutGetTunnelsXml = netconfController.sendMessage(netconfClient, commandGetTunnelsXml);
        LOG.info("OutPutGetTunnelsXml: " + outPutGetTunnelsXml);

        List<SSrTeTunnel> ret = SrTeTunnelXml.getSrTeTunnelFromXml(outPutGetTunnelsXml);

        // Get Interfaces
        String commadGetTunnelInterfacesXml = SrTeTunnelXml.getSrTeTunnelInterfacesXml();
        LOG.info("CommadGetTunnelInterfacesXml: " + commadGetTunnelInterfacesXml);

        String outPutGetTunnelInterfacesXml = netconfController.sendMessage(netconfClient, commadGetTunnelInterfacesXml);
        LOG.info("OutPutGetTunnelInterfacesXml: " + outPutGetTunnelInterfacesXml);

        List<SSrTeTunnelInterface> list = SrTeTunnelXml.getSrTeTunnelInterfacesFromXml(outPutGetTunnelInterfacesXml);

        for (SSrTeTunnel t : ret) {
            for (SSrTeTunnelInterface i : list) {
                if (t.getTunnelName().equals(i.getIfName())) {
                    t.setTunnelDesc(i.getIfDescr());
                    break;
                }
            }
        }

        return ret;
    }

    private List<SExplicitPath> getExplicitPathsFromDeviceByRouterId(NetconfClient netconfClient) {
        List<SExplicitPath> ret = null;
        String getExplicitPathXml = ExplicitPathXml.getExplicitPathXml();
        LOG.info("command getExplicitPathXml: " + getExplicitPathXml);
        String outExplicitPathXml = netconfController.sendMessage(netconfClient, getExplicitPathXml);
        LOG.info("command outExplicitPathXml: " + outExplicitPathXml);
        ret = ExplicitPathXml.getExplicitPathFromXml(outExplicitPathXml);
        return ret;
    }

    private Map<String, Tunnel> getTunnelsMap(List<SBfdCfgSession> sBfdCfgSessions, List<SSrTeTunnel> sSrTeTunnels, List<SExplicitPath> sExplicitPaths) {
        Map<String, Tunnel> ret = new ConcurrentHashMap<>();

        return ret;
    }

    private Map<Integer, BfdSession> getBfdSessionFromSBfdCfgSessions(List<SBfdCfgSession> sBfdCfgSessions) {
        Map<Integer, BfdSession> ret = new ConcurrentHashMap<>();
        for (SBfdCfgSession s : sBfdCfgSessions) {
            BfdSession b = this.sBfdCfgSessionToBfdSession(s);
            if (null != b) {
                ret.put(Integer.parseInt(b.getDiscriminatorLocal()), b);
            }
        }
        return ret;
    }

    private BfdSession sBfdCfgSessionToBfdSession(SBfdCfgSession sBfdCfgSession) {
        BfdSession ret = null;
        if (null != sBfdCfgSession) {
            ret = new BfdSession();
            ret.setTunnelName(sBfdCfgSession.getTunnelName());
            ret.setBfdName(sBfdCfgSession.getSessName());
            ret.setMultiplier(sBfdCfgSession.getMultiplier());
            ret.setMinRecvTime(sBfdCfgSession.getMinRxInt());
            ret.setMinSendTime(sBfdCfgSession.getMinTxInt());
            ret.setDiscriminatorLocal(sBfdCfgSession.getLocalDiscr());
            ret.setDiscriminatorRemote(sBfdCfgSession.getRemoteDiscr());
            if (sBfdCfgSession.getLinkType().equals(SBfdCfgSessionLinkTypeEnum.Tunnel.getName())) {
                ret.setType(BfdTypeEnum.Tunnel.getCode());
            } else if (sBfdCfgSession.getLinkType().equals(SBfdCfgSessionLinkTypeEnum.Master.getName())) {
                ret.setType(BfdTypeEnum.Master.getCode());
            }
        }
        return ret;
    }

    private List<ExplicitPath> getExplicitPathsFromSExplicitPaths(List<SExplicitPath> sExplicitPaths) {
        List<ExplicitPath> ret = null;

        if (null != sExplicitPaths && !sExplicitPaths.isEmpty()) {
            ret = new ArrayList<ExplicitPath>();
            for (SExplicitPath s : sExplicitPaths) {
                ret.add(this.sExplicitPathToExplicitPath(s));
            }
        }

        return ret;
    }

    private ExplicitPath sExplicitPathToExplicitPath(SExplicitPath sExplicitPath) {
        ExplicitPath ret = new ExplicitPath();
        ret.setPathName(sExplicitPath.getExplicitPathName());
        if (sExplicitPath.getExplicitPathHops() != null) {
            Map<String, Label> labelMap = new HashMap<>();
            for (SExplicitPathHop sExplicitPathHop : sExplicitPath.getExplicitPathHops()) {
                Label label = new Label();
                label.setValue(Integer.valueOf(sExplicitPathHop.getMplsTunnelHopSidLabel()));
                label.setType(LabelTypeEnum.nameToCode(sExplicitPathHop.getMplsTunnelHopSidLabelType()));
                labelMap.put(sExplicitPathHop.getMplsTunnelHopIndex(), label);
            }
            ret.setLabelMap(labelMap);
        }
        return ret;
    }

    private Tunnel sSrTeTunnelToTunnel(SSrTeTunnel sSrTeTunnel, List<BfdSession> bfdCfgSessions, List<ExplicitPath> explicitPaths) {
        Tunnel ret = new Tunnel();

        // set tunnel values
        ret.setTunnelName(sSrTeTunnel.getTunnelName());
        ret.setDestRouterId(sSrTeTunnel.getMplsTunnelEgressLSRId());
        ret.setTunnelId(sSrTeTunnel.getMplsTunnelIndex());
        ret.setBandWidth(sSrTeTunnel.getMplsTunnelBandwidth());

        ret.setTunnelDesc(sSrTeTunnel.getTunnelDesc());

        if (null != sSrTeTunnel.getMplsteServiceClass()) {
            TunnelServiceClass tsc = new TunnelServiceClass();
            STunnelServiceClass stsc = sSrTeTunnel.getMplsteServiceClass();
            tsc.setAf1(stsc.isAf1ServiceClassEnable());
            tsc.setAf2(stsc.isAf2ServiceClassEnable());
            tsc.setAf3(stsc.isAf3ServiceClassEnable());
            tsc.setAf4(stsc.isAf4ServiceClassEnable());
            tsc.setBe(stsc.isBeServiceClassEnable());
            tsc.setEf(stsc.isEfServiceClassEnable());
            tsc.setCs6(stsc.isCs6ServiceClassEnable());
            tsc.setCs7(stsc.isCs7ServiceClassEnable());
            tsc.setDef(stsc.isDefaultServiceClassEnable());

            ret.setServiceClass(tsc);
        }

        // set bfd values
        BfdSession tunnelBfd = this.getBfdSessionFromListByTunnelNameAndType(ret.getTunnelName(), BfdTypeEnum.Tunnel.getCode(), bfdCfgSessions);
        BfdSession masterBfd = this.getBfdSessionFromListByTunnelNameAndType(ret.getTunnelName(), BfdTypeEnum.Master.getCode(), bfdCfgSessions);

        ret.setMasterBfd(masterBfd);
        ret.setTunnelBfd(tunnelBfd);

        if (null == masterBfd && null == tunnelBfd) {
            if (Boolean.valueOf(sSrTeTunnel.getMplsTeTunnelBfdEnable())) {
                // set dynamicBfd
                BfdSession dynamicBfd = new BfdSession();
                dynamicBfd.setMinSendTime(sSrTeTunnel.getMplsTeTunnelBfdMinTx());
                dynamicBfd.setMinRecvTime(sSrTeTunnel.getMplsTeTunnelBfdMinnRx());
                dynamicBfd.setMultiplier(sSrTeTunnel.getMplsTeTunnelBfdDetectMultiplier());
                ret.setDynamicBfd(dynamicBfd);
                ret.setBfdType(BfdTypeEnum.Dynamic.getCode());
            } else {
                ret.setBfdType(BfdTypeEnum.Empty.getCode());
            }
        } else {
            ret.setBfdType(BfdTypeEnum.Static.getCode());
        }

        // set explicit paths
        List<SSrTeTunnelPath> sSrTeTunnelPaths = sSrTeTunnel.getSrTeTunnelPaths();
        if (null != sSrTeTunnelPaths && !sSrTeTunnelPaths.isEmpty()) {
            for (SSrTeTunnelPath srtp : sSrTeTunnelPaths) {
                ExplicitPath ep = this.getExplictPathFromListByName(srtp.getExplicitPathName(), explicitPaths);
                if (null != ep) {
                    if (srtp.getPathType().equals(STunnelPathTypeEnum.Primary.getName())) {
                        ret.setMasterPath(ep);
                    } else if (srtp.getPathType().equals(STunnelPathTypeEnum.HotStandby.getName())) {
                        ret.setSlavePath(ep);
                    }
                }
            }
        }

        return ret;
    }

    private ExplicitPath getExplictPathFromListByName(String name, List<ExplicitPath> explicitPaths) {
        if (null == name || null == explicitPaths || explicitPaths.isEmpty()) {
            return null;
        }

        for (ExplicitPath e : explicitPaths) {
            if (e.getPathName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    private BfdSession getBfdSessionFromListByTunnelNameAndType(String tunnelName, Integer type, List<BfdSession> bfdSessions) {

        if (null == bfdSessions || bfdSessions.isEmpty() || null == tunnelName || null == type) {
            return null;
        }
        for (BfdSession b : bfdSessions) {
            if (b.getTunnelName().equals(tunnelName) && b.getType() == type) {
                return b;
            }
        }
        return null;
    }

    private boolean addBfdSessionToBase(BfdSession bfdSession, String routerId) {
        Map<Integer, BfdSession> map = this.bfdSessionMap.get(routerId);
        if (null == map) {
            map = new ConcurrentHashMap<Integer, BfdSession>();
            this.bfdSessionMap.put(bfdSession.getDevice().getRouterId(), map);
        }
        map.put(Integer.parseInt(bfdSession.getDiscriminatorLocal()), bfdSession);
        return true;
    }

    private boolean deleteBfdSessionFromBase(String name, String routerId) {
        Map<Integer, BfdSession> map = this.bfdSessionMap.get(routerId);
        if (null != map) {
            Iterator<BfdSession> iterator = map.values().iterator();
            while (iterator.hasNext()) {
                BfdSession bfdSession = iterator.next();
                if (bfdSession.getBfdName().equals(name)) {
                    map.remove(Integer.parseInt(bfdSession.getDiscriminatorLocal()));
                }
            }
        }
        return true;
    }

}
