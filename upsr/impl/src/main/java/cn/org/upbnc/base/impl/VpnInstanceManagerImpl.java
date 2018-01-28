/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.VpnInstanceManager;
import cn.org.upbnc.entity.*;

import java.util.*;

import cn.org.upbnc.util.netconf.L3vpnIf;
import cn.org.upbnc.util.netconf.L3vpnInstance;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.NetconfDevice;
import cn.org.upbnc.util.xml.VpnXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfClientMap;
import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class VpnInstanceManagerImpl implements VpnInstanceManager {
    private static final Logger LOG = LoggerFactory.getLogger(VpnInstanceManagerImpl.class);
    private static VpnInstanceManager instance = null;
    private List<VPNInstance> vpnInstanceList = null;


    @Override
    public String toString() {
        return "VpnInstanceManagerImpl{}";
    }

    private VpnInstanceManagerImpl() {
        LOG.info("VpnInstanceManagerImpl");
        this.vpnInstanceList = new ArrayList<VPNInstance>();
    }

    public static VpnInstanceManager getInstance() {
        LOG.info("VpnInstanceManager");
        if (null == instance) {
            instance = new VpnInstanceManagerImpl();
        }
        return instance;
    }

    public boolean addVpnInstance(VPNInstance vpnInstance) {
        if (null == vpnInstance) {
            return false;
        }
        vpnInstanceList.add(vpnInstance);
        return true;
    }

    public boolean delVpnInstance(Integer id) {
        VPNInstance vpnInstance = null;
        Iterator<VPNInstance> iter = vpnInstanceList.iterator();
        while (iter.hasNext()) {
            vpnInstance = iter.next();
            if (id == vpnInstance.getId().intValue()) {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    public boolean delVpnInstance(String routerId, String vpnName) {
        if ((null == routerId) || routerId.isEmpty() || (null == vpnName) || vpnName.isEmpty()) {
            return false;
        }
        VPNInstance vpnInstance = null;
        Iterator<VPNInstance> iter = vpnInstanceList.iterator();
        while (iter.hasNext()) {
            vpnInstance = iter.next();
            if (true == vpnInstance.getVpnName().equals(vpnName)) {
                if ((null != vpnInstance.getDevice()) &&
                        (true == routerId.equals(vpnInstance.getDevice().getRouterId()))) {
                    for(DeviceInterface deviceInterface1:vpnInstance.getDeviceInterfaceList()){
                        for(DeviceInterface deviceInterface2:vpnInstance.getDevice().getDeviceInterfaceList()){
                            if(deviceInterface1.getName().equals(deviceInterface2.getName())){
                                deviceInterface2.setVpn(null);
                            }
                        }
                    }
                    iter.remove();
                    return true;
                }
            }
        }
        return false;
    }

    public VPNInstance getVpnInstance(Integer id) {
        VPNInstance vpnInstance = null;
        Iterator<VPNInstance> iter = vpnInstanceList.iterator();
        while (iter.hasNext()) {
            vpnInstance = iter.next();
            if (id == vpnInstance.getId().intValue()) {
                return vpnInstance;
            }
        }
        return null;
    }

    public VPNInstance getVpnInstance(String routerId, String vpnName) {
        LOG.info("enter getVpnIstance routerid={} vpnName = {}", new Object[]{routerId, vpnName});
        if ((null == routerId) || routerId.isEmpty() || (null == vpnName) || vpnName.isEmpty()) {
            return null;
        }

        VPNInstance vpnInstance = null;
        Iterator<VPNInstance> iter = vpnInstanceList.iterator();
        while (iter.hasNext()) {
            vpnInstance = iter.next();
            if (true == vpnInstance.getVpnName().equals(vpnName)) {
                if ((null != vpnInstance.getDevice()) &&
                        (true == routerId.equals(vpnInstance.getDevice().getRouterId()))) {
                    return vpnInstance;
                }
            }
        }
        return null;
    }


    public VPNInstance updateVpnInstance(String vpnName, String routerId, Device device,
                                         String businessRegion, String rd, String importRT,
                                         String exportRT, Integer peerAS, Address peerIP,
                                         Integer routeSelectDelay, Integer importDirectRouteEnable,
                                         List<DeviceInterface> deviceInterfaceList, List<NetworkSeg> networkSegList, String note,
                                         String importTunnelPolicyName,
                                         String vpnFrr,
                                         String applyLabel,
                                         String ttlMode,
                                         String ebgpPreference,
                                         String ibgpPreference,
                                         String localPreference,
                                         String importRoutePolicyName,
                                         String exportRoutePolicyName,
                                         String advertiseCommunity) {
        LOG.info("enter updateVpnInstance vpnName={}", new Object[]{vpnName});
        if ((null == routerId) || routerId.isEmpty() || (null == vpnName) || vpnName.isEmpty())
            return null;

        VPNInstance vpnInstance = getVpnInstance(routerId, vpnName);
        if (null != vpnInstance) {
            LOG.info("################enter updateVpnInstance-01###################");
            vpnInstance.setRouterId(routerId);
            vpnInstance.setDevice(device);
            vpnInstance.setBusinessRegion(businessRegion);
            vpnInstance.setRd(rd);
            vpnInstance.setImportRT(importRT);
            vpnInstance.setExportRT(exportRT);
            vpnInstance.setPeerAS(peerAS);
            vpnInstance.setPeerIP(peerIP);
            vpnInstance.setRouteSelectDelay(routeSelectDelay);
            vpnInstance.setImportDirectRouteEnable(importDirectRouteEnable);

            for(DeviceInterface deviceInterface1:vpnInstance.getDeviceInterfaceList()){
                for(DeviceInterface deviceInterface2:vpnInstance.getDevice().getDeviceInterfaceList()){
                    if(deviceInterface1.getName().equals(deviceInterface2.getName())){
                        deviceInterface2.setVpn(null);
                    }
                }
            }
            for(DeviceInterface deviceInterface1:deviceInterfaceList){
                for(DeviceInterface deviceInterface2:vpnInstance.getDevice().getDeviceInterfaceList()){
                    if(deviceInterface1.getName().equals(deviceInterface2.getName())){
                        deviceInterface2.setVpn(vpnInstance);
                    }
                }
            }

            vpnInstance.setDeviceInterfaceList(deviceInterfaceList);
            vpnInstance.setNetworkSegList(networkSegList);
            vpnInstance.setNote(note);
            vpnInstance.setRefreshFlag(true);
        } else {
            Integer id = 0;
            vpnInstance = new VPNInstance(id, device, deviceInterfaceList, vpnName, routerId, businessRegion, rd,
                    importRT, exportRT, peerAS, peerIP, routeSelectDelay, importDirectRouteEnable, networkSegList);
            for(DeviceInterface deviceInterface1:deviceInterfaceList){
                for(DeviceInterface deviceInterface2:vpnInstance.getDevice().getDeviceInterfaceList()){
                    if(deviceInterface1.getName().equals(deviceInterface2.getName())){
                        deviceInterface2.setVpn(vpnInstance);
                    }
                }
            }
            vpnInstance.setNote(note);
            vpnInstance.setRefreshFlag(true);
            if (null != vpnInstance) {
                this.vpnInstanceList.add(vpnInstance);
            }
        }
        vpnInstance.setImportTunnelPolicyName(importTunnelPolicyName);
        vpnInstance.setVpnFrr(vpnFrr);
        vpnInstance.setApplyLabel(applyLabel);
        vpnInstance.setTtlMode(ttlMode);
        vpnInstance.setEbgpPreference(ebgpPreference);
        vpnInstance.setIbgpPreference(ibgpPreference);
        vpnInstance.setLocalPreference(localPreference);
        vpnInstance.setImportRoutePolicyName(importRoutePolicyName);
        vpnInstance.setExportRoutePolicyName(exportRoutePolicyName);
        vpnInstance.setAdvertiseCommunity(advertiseCommunity);
        LOG.info("vpnName={} rd={} exportRT={}", new Object[]{vpnName, rd, exportRT});
        return vpnInstance;
    }

    public VPNInstance updateVpnInstance(VPNInstance vpnInstance) {
        if (null == vpnInstance) {
            return null;
        }
        VPNInstance findVpnInstance = getVpnInstance(vpnInstance.getRouterId(), vpnInstance.getVpnName());
        if (null != findVpnInstance) {
            vpnInstance.setId(findVpnInstance.getId());
            vpnInstanceList.remove(findVpnInstance);
            vpnInstanceList.add(vpnInstance);
        } else {
            vpnInstanceList.add(vpnInstance);
        }
        return vpnInstance;
    }


    public List<VPNInstance> getVpnInstanceList() {
        List<VPNInstance> vpnInstances = new ArrayList<>();
        for (VPNInstance vpnInstance : vpnInstanceList) {
            if (vpnInstance.isRefreshFlag()) {
                vpnInstances.add(vpnInstance);
            }
        }
        return vpnInstances;
    }

    public List<VPNInstance> getVpnInstanceListByRouterId(String routerId) {
        LOG.info("enter getVpnIstance routerid={} ", new Object[]{routerId});
        if ((null == routerId) || routerId.isEmpty()) {
            return null;
        }
        List<VPNInstance> vpnInstanceList = new ArrayList<VPNInstance>();
        for (VPNInstance vpnInstance : this.vpnInstanceList) {
            if (vpnInstance.getRouterId().equals(routerId)) {
                vpnInstanceList.add(vpnInstance);
            }
        }

        return vpnInstanceList;
    }

    public boolean isContainVpnName(String vpnName) {
        boolean isContain = false;
        for (VPNInstance vpnInstance : this.vpnInstanceList) {
            if (vpnInstance.getVpnName().equals(vpnName)) {
                isContain = true;
                return isContain;
            }
        }
        return isContain;
    }

    public boolean isContainRd(String routerId, String rd) {
        boolean isContain = false;
        for (VPNInstance vpnInstance : getVpnInstanceListByRouterId(routerId)) {
            if (vpnInstance.getRd().equals(rd)) {
                isContain = true;
                return isContain;
            }
        }
        return isContain;
    }

    @Override
    public void emptyVpnInstancesByRouterId(String routerId) {
        VPNInstance vpnInstance;
        Iterator<VPNInstance> iter = vpnInstanceList.iterator();
        while (iter.hasNext()) {
            vpnInstance = iter.next();
            if (vpnInstance.getRouterId().equals(routerId)) {
                iter.remove();
            }
        }
    }
}
