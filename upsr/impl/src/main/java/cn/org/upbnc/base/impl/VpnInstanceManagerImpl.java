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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VpnInstanceManagerImpl  implements VpnInstanceManager {
    private static final Logger LOG = LoggerFactory.getLogger(VpnInstanceManagerImpl.class);
    private static VpnInstanceManager instance = null;
    private List<VPNInstance>  vpnInstanceList =  null;

    @Override
    public String toString() {
        return "VpnInstanceManagerImpl{}";
    }

    private VpnInstanceManagerImpl()
    {
       LOG.info("VpnInstanceManagerImpl");
       this.vpnInstanceList = new ArrayList<VPNInstance>();
    }
    public static VpnInstanceManager getInstance()
    {
        LOG.info("VpnInstanceManager");
        if(null == instance)
        {
            instance = new VpnInstanceManagerImpl();
        }
        return instance;
    }
    public boolean addVpnInstance(VPNInstance vpnInstance)
    {
        if(null == vpnInstance)
        {
            return false;
        }
        vpnInstanceList.add(vpnInstance);
        return true;
    }
    public boolean delVpnInstance(Integer id)
    {
        VPNInstance vpnInstance = null;
        Iterator<VPNInstance> iter =  vpnInstanceList.iterator();
        while(iter.hasNext())
        {
            vpnInstance = iter.next();
            if(id == vpnInstance.getId().intValue())
            {
                iter.remove();
                return true;
            }
        }
        return false;
    }
    public boolean delVpnInstance(String vpnName)
    {
        if(null == vpnName)
            return false;
        VPNInstance vpnInstance = null;
        Iterator<VPNInstance> iter = vpnInstanceList.iterator();
        while(iter.hasNext())
        {
            vpnInstance = iter.next();
            if(true == vpnInstance.getVpnName().equals(vpnName) )
            {
                iter.remove();
                return true;
            }
        }
        return false;
    }
    public VPNInstance getVpnIstance(Integer id)
    {
        VPNInstance vpnInstance = null;
        Iterator<VPNInstance> iter = vpnInstanceList.iterator();
        while(iter.hasNext())
        {
            vpnInstance = iter.next();
            if(id  == vpnInstance.getId().intValue())
            {
                return vpnInstance;
            }
        }
        return null;
    }
    public VPNInstance getVpnIstance(String vpnName)
    {
        LOG.info("enter getVpnIstance vpnName = {}", new Object[]{vpnName});
        if(null == vpnName)
            return null;
        LOG.info("$$$$$$$$$$$$$enter getVpnIstance-01$$$$$$$$$");
        VPNInstance vpnInstance = null;
        Iterator<VPNInstance> iter = vpnInstanceList.iterator();
        while(iter.hasNext())
        {
            vpnInstance = iter.next();
            if(true == vpnInstance.getVpnName().equals(vpnName))
            {
                LOG.info("$$$$$$$$$$$$$enter getVpnIstance-02$$$$$$$$$");
                return vpnInstance;
            }
        }
        LOG.info("$$$$$$$$$$$$$enter getVpnIstance-03$$$$$$$$$");
        return null;
    }
    public VPNInstance getVpnInstance(String routerId)
    {
        if(null == routerId)
            return null;
        VPNInstance vpnInstance = null;
        Iterator<VPNInstance> iter = vpnInstanceList.iterator();
        while(iter.hasNext())
        {
            vpnInstance = iter.next();
            if(true == vpnInstance.getRd().equals(routerId))
            {
                return vpnInstance;
            }
        }
        return null;
    }
    public VPNInstance updateVpnInstance(String vpnName,
                                         Device device,
                                         String businessRegion,
                                         String rd,
                                         String importRT,
                                         String exportRT,
                                         Integer peerAS,
                                         Address peerIP,
                                         Integer routeSelectDelay,
                                         Integer importDirectRouteEnable,
                                         List<DeviceInterface> deviceInterfaceList,
                                         List<NetworkSeg> networkSegList)
    {
        LOG.info("enter updateVpnInstance vpnName={}", new Object[]{vpnName} );
        if(null == vpnName)
            return null;


        VPNInstance  vpnInstance = getVpnInstance(vpnName);
        if(null != vpnInstance)
        {
            LOG.info("################enter updateVpnInstance-01###################");
            vpnInstance.setDevice(device);
            vpnInstance.setBusinessRegion(businessRegion);
            vpnInstance.setRd(rd);
            vpnInstance.setImportRT(importRT);
            vpnInstance.setExportRT(exportRT);
            vpnInstance.setPeerAS(peerAS);
            vpnInstance.setPeerIP(peerIP);
            vpnInstance.setRouteSelectDelay(routeSelectDelay);
            vpnInstance.setImportDirectRouteEnable(importDirectRouteEnable);
            vpnInstance.setDeviceInterfaceList(deviceInterfaceList);
            vpnInstance.setNetworkSegList(networkSegList);

        }
        else
        {
            Integer id = 0 ;
            vpnInstance = new VPNInstance(id, device, deviceInterfaceList, vpnName, businessRegion,rd,
                    importRT, exportRT, peerAS, peerIP, routeSelectDelay, importDirectRouteEnable, networkSegList);
            if(null != vpnInstance) {
                this.vpnInstanceList.add(vpnInstance);
            }
        }
        return vpnInstance;
    }

    public VPNInstance  updateVpnInstance(VPNInstance vpnInstance)
    {
        if(null == vpnInstance)
            return null;
        VPNInstance  findVpnInstance = getVpnInstance(vpnInstance.getVpnName());
        if(null != findVpnInstance)
        {
            vpnInstance.setId(findVpnInstance.getId());
            findVpnInstance = vpnInstance;
            return findVpnInstance;
        }
        else
        {
            vpnInstanceList.add(vpnInstance);
        }
        return vpnInstance;
    }
    public List<VPNInstance> getVpnInstanceList()
    {
        return vpnInstanceList;
    }

}
