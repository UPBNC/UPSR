/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api.impl;


import cn.org.upbnc.api.VpnInstanceApi;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.VPNService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class VpnInstanceApiImpl implements VpnInstanceApi {
    private static final Logger LOG = LoggerFactory.getLogger(VpnInstanceApiImpl.class);
    private static VpnInstanceApi ourInstance = new VpnInstanceApiImpl();
    private ServiceInterface serviceInterface;
    private VPNService vpnService;
    public static VpnInstanceApi getInstance() {
        return ourInstance;
    }

    private VpnInstanceApiImpl()
    {
        this.serviceInterface = null;
    }

    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        this.serviceInterface = serviceInterface;
        if(null != serviceInterface) {
            vpnService = this.serviceInterface.getVpnService();
        }
        return true;
    }
    public boolean updateVpnInstance(String vpnName,
                                     String routerId,
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
        if(null == this.vpnService) {
            return false;
        }
        ret= this.vpnService.updateVpnInstance(vpnName,routerId,businessRegion,rd,importRT, exportRT,
                peerAS,peerIP,routeSelectDelay,importDirectRouteEnable,deviceInterfaceList,networkSegList);
        return ret;
    }
    public boolean delVpnInstance(Integer id)
    {
        return (null == this.vpnService)?false:this.vpnService.delVpnInstance(id);
    }
    public boolean delVpnInstance(String routerId,String vpnName)
    {
        return (null == this.vpnService)?false:this.vpnService.delVpnInstance(routerId, vpnName);
    }
    public VPNInstance getVpnInstance(Integer id)
    {
        return (null == this.vpnService)?null:this.vpnService.getVpnInstance(id);
    }
    public VPNInstance getVpnInstance(String routerId,String vpnName)
    {
        return (null == this.vpnService)?null:this.vpnService.getVpnInstance(routerId, vpnName);
    }
    public List<VPNInstance> getVpnInstanceList(String vpnName)
    {
        return (null == this.vpnService)?null:this.vpnService.getVpnInstanceListFromDevice(vpnName);
    }
    public Map<String, List<VPNInstance>> getVpnInstanceMap(String vpnName) {
        return (null == this.vpnService)?null:this.vpnService.getVpnInstanceMap(vpnName);
    }
    public String getTest() {
        return null;
    }
}
