/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service.impl;

import cn.org.upbnc.api.impl.TopoApiImpl;
import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.VpnInstanceManager;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.service.VPNService;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VPNServiceImpl implements VPNService {
    private static final Logger LOG = LoggerFactory.getLogger(VPNServiceImpl.class);
    private static VPNService ourInstance = new VPNServiceImpl();
    private BaseInterface  baseInterface;
    private VpnInstanceManager vpnInstanceManager;
    public static VPNService getInstance() {
        if(null == ourInstance)
        {
            ourInstance = new VPNServiceImpl();
        }
        return ourInstance;
    }

    private VPNServiceImpl() {
        this.baseInterface = null;
        this.vpnInstanceManager = null;
    }

    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        if(null != baseInterface) {
            vpnInstanceManager = this.baseInterface.getVpnInstanceManager();
        }
        return true;
    }
    public boolean updateVpnInstance(String vpnName,
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
        boolean ret = false;
        LOG.info("service updateVpnInstance-01");
        if(null == this.vpnInstanceManager)
        {
            return false;
        }
        LOG.info("service updateVpnInstance-02");
        ret =(null == this.vpnInstanceManager.updateVpnInstance(vpnName,device,businessRegion,rd,importRT, exportRT,
                peerAS,peerIP,routeSelectDelay,importDirectRouteEnable,deviceInterfaceList,networkSegList))?false:true;
        return ret;
    }
    public boolean delVpnInstance(Integer id)
    {
        return (null == this.vpnInstanceManager)?false: this.vpnInstanceManager.delVpnInstance(id);
    }
    public boolean delVpnInstance(String vpnName)
    {
        return (null == this.vpnInstanceManager)?false: this.vpnInstanceManager.delVpnInstance(vpnName);
    }
    public VPNInstance getVpnInstance(Integer id)
    {
        return (null == this.vpnInstanceManager)?null: this.vpnInstanceManager.getVpnIstance(id);
    }
    public VPNInstance getVpnInstance(String vpnName)
    {
        return (null == this.vpnInstanceManager)?null: this.vpnInstanceManager.getVpnIstance(vpnName);
    }
    public List<VPNInstance> getVpnInstanceList()
    {
        return (null == this.vpnInstanceManager)?null: this.vpnInstanceManager.getVpnInstanceList();
    }
    public String getTest() {
        return null;
    }
}
