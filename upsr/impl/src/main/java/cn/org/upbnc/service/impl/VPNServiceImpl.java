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
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.base.VpnInstanceManager;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.enumtype.AddressTypeEnum;
import cn.org.upbnc.service.VPNService;

import java.util.LinkedList;
import java.util.List;

import cn.org.upbnc.util.netconf.L3vpnIf;
import cn.org.upbnc.util.netconf.L3vpnInstance;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.NetconfDevice;
import cn.org.upbnc.util.xml.VpnXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfClientMap;
import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class VPNServiceImpl implements VPNService {
    private static final Logger LOG = LoggerFactory.getLogger(VPNServiceImpl.class);
    private static VPNService ourInstance = null;
    private BaseInterface  baseInterface = null;
    private VpnInstanceManager vpnInstanceManager = null;
    private NetConfManager netConfManager = null;
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
            netConfManager = this.baseInterface.getNetConfManager();
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
        Device device = null;
        VPNInstance vpnInstance = null;
        LOG.info("service updateVpnInstance-01");
        if(null == this.vpnInstanceManager)
        {
            return false;
        }
        LOG.info("service updateVpnInstance-02");

        if(null != this.baseInterface) {
            device = this.baseInterface.getDeviceManager().getDevice(routerId);
        }

        vpnInstance = this.vpnInstanceManager.updateVpnInstance(vpnName,device,businessRegion,rd,importRT, exportRT,
                peerAS,peerIP,routeSelectDelay,importDirectRouteEnable,deviceInterfaceList,networkSegList);
        if((null == vpnInstance)||(null == device)||(null == device.getNetConf())){
            return false;
        }
        List<L3vpnIf> l3vpnIf = new LinkedList<L3vpnIf>();
        if(null != deviceInterfaceList)
        {
            for (DeviceInterface deviceInterface:deviceInterfaceList) {
                l3vpnIf.add(new L3vpnIf(deviceInterface.getName(), deviceInterface.getIp().getAddress(), deviceInterface.getMask().getAddress()));
            }
        }
        if(null == device.getNetConf().getIp())
        {
            LOG.info("service updateVpnInstance null == device.getNetConf().getIp()");
        }

        L3vpnInstance l3vpnInstance =  new L3vpnInstance(vpnName, null, rd, exportRT, l3vpnIf);
        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        String sendMsg = VpnXml.createVpnXml(l3vpnInstance);
        LOG.info("sendMsg={}", new Object[]{sendMsg});
        String result = netconfController.sendMessage(netconfClient, sendMsg);
        LOG.info("result={}", new Object[]{result});
        /*
        if (netconfClientMap.containsKey(device.getNetConf().getIp().getAddress())) {
            if (netconfClientMap.get(device.getNetConf().getIp().getAddress()).isFlag()) {

                String sendMsg = VpnXml.createVpnXml(l3vpnInstance);
                LOG.info("sendMsg={}", new Object[]{sendMsg});
                String result = netconfController.sendMessage(netconfClientMap.get(device.getNetConf().getIp().getAddress()), sendMsg);
                LOG.info("result={}", new Object[]{result});
            }
        }
        */
        return true;
    }
    public boolean delVpnInstance(Integer id)
    {
        return (null == this.vpnInstanceManager)?false: this.vpnInstanceManager.delVpnInstance(id);
    }
    public boolean delVpnInstance(String routerId,String vpnName)
    {
        return (null == this.vpnInstanceManager)?false: this.vpnInstanceManager.delVpnInstance(routerId, vpnName);
    }
    public VPNInstance getVpnInstance(Integer id)
    {
        VPNInstance vpnInsntance = this.vpnInstanceManager.getVpnIstance(id);
        if((null == vpnInsntance)||(null != vpnInsntance.getDevice())||(null != vpnInsntance.getDevice().getNetConf())){
            return null;
        }
        NetconfClient netconfClient = this.netConfManager.getNetconClient(vpnInsntance.getDevice().getNetConf().getIp().getAddress());

        LOG.info("enter getVpnIstance");
        String sendMsg = VpnXml.getVpnXml(vpnInsntance.getVpnName());
        LOG.info("get sendMsg={}", new Object[]{sendMsg});
        String result = netconfController.sendMessage(netconfClient, sendMsg);
        LOG.info("get result={}", new Object[]{result});

        return vpnInsntance;
    }
    public VPNInstance getVpnInstance(String routerId, String vpnName)
    {
        /*
        VPNInstance vpnInsntance = this.vpnInstanceManager.getVpnIstance(routerId, vpnName);
        if((null == vpnInsntance)||(null != vpnInsntance.getDevice())||(null != vpnInsntance.getDevice().getNetConf())){
            return null;
        }
        */
        if((null == routerId)||(null == vpnName)) {
            return null;
        }
        Device device = this.baseInterface.getDeviceManager().getDevice(routerId);
        if(null == device) {
            return null;
        }
        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());

        LOG.info("enter getVpnIstance");
        String sendMsg = VpnXml.getVpnXml(vpnName);
        LOG.info("get sendMsg={}", new Object[]{sendMsg});
        String result = netconfController.sendMessage(netconfClient, sendMsg);
        LOG.info("get result={}", new Object[]{result});
        List<L3vpnInstance> l3vpnInstances = VpnXml.getVpnFromXml(result);
        if(null == l3vpnInstances) {
            return null;
        }
        VPNInstance vpnInsntance = null;
        for (L3vpnInstance l3vpnInstance:l3vpnInstances) {
            if(true == vpnName.equals(l3vpnInstance.getVrfName())){
                vpnInsntance.setRd(l3vpnInstance.getVrfRD());
                vpnInsntance.setExportRT(l3vpnInstance.getVrfRTValue());
                vpnInsntance.setImportRT(l3vpnInstance.getVrfRTValue());
                LOG.info("get vrfname={} rt={}", new Object[]{l3vpnInstance.getVrfRD(),l3vpnInstance.getVrfRTValue()});
                List<L3vpnIf> l3vpnIfs = l3vpnInstance.getL3vpnIfs();
                if(null != l3vpnIfs) {
                    DeviceInterface deviceInterface = null;
                    List<DeviceInterface> deviceInterfaces = new LinkedList<DeviceInterface>();
                    for (L3vpnIf l3vpnIf:l3vpnIfs) {
                        deviceInterface = new DeviceInterface(l3vpnIf.getIfName(),new Address(l3vpnIf.getIpv4Addr(), AddressTypeEnum.V4),
                                                            new Address(l3vpnIf.getSubnetMask(), AddressTypeEnum.V4));
                        LOG.info("get ifname={} ip={} mask={}", new Object[]{l3vpnIf.getIfName(),l3vpnIf.getIpv4Addr(), l3vpnIf.getSubnetMask()});
                        deviceInterfaces.add(deviceInterface);
                    }
                    vpnInsntance.setDeviceInterfaceList(deviceInterfaces);
                }
            }
        }
        LOG.info("can't find  vpnName={}", new Object[]{vpnName});
        return vpnInsntance;
    }
    public List<VPNInstance> getVpnInstanceList()
    {
        return (null == this.vpnInstanceManager)?null: this.vpnInstanceManager.getVpnInstanceList();
    }
    public String getTest() {
        return null;
    }
}
