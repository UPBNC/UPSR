/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.base.VpnInstanceManager;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.enumtype.AddressTypeEnum;
import cn.org.upbnc.service.VPNService;

import java.util.*;

import cn.org.upbnc.util.netconf.L3vpnIf;
import cn.org.upbnc.util.netconf.L3vpnInstance;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.xml.CheckXml;
import cn.org.upbnc.util.xml.VpnXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class VPNServiceImpl implements VPNService {
    private static final Logger LOG = LoggerFactory.getLogger(VPNServiceImpl.class);
    private static VPNService ourInstance = null;
    private BaseInterface  baseInterface = null;
    private VpnInstanceManager vpnInstanceManager = null;
    private NetConfManager netConfManager = null;
    private DeviceManager  deviceManager = null;
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
        this.netConfManager = null;
        this.deviceManager = null;
    }

    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        if(null != baseInterface) {
            vpnInstanceManager = this.baseInterface.getVpnInstanceManager();
            netConfManager = this.baseInterface.getNetConfManager();
            deviceManager = this.baseInterface.getDeviceManager();
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
        if((null == routerId)||(routerId.isEmpty())||(null == this.vpnInstanceManager))
        {
            return false;
        }
        LOG.info("service updateVpnInstance-02");

        if(null != this.baseInterface) {
            device = this.deviceManager.getDevice(routerId);
        }
        if((null == device)||(null == device.getNetConf())){
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

        L3vpnInstance l3vpnInstance =  new L3vpnInstance(vpnName, businessRegion, rd, exportRT, l3vpnIf);
        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());

        vpnInstance=this.vpnInstanceManager.getVpnInstance(routerId,vpnName);

        if(null==vpnInstance){
            //create vpn
            LOG.info("vpnInstance is null,add VPN {}",vpnName);

            String sendMsg = VpnXml.createVpnXml(l3vpnInstance);
            LOG.info("sendMsg={}", new Object[]{sendMsg});
            String result = netconfController.sendMessage(netconfClient, sendMsg);
            LOG.info("result={}", new Object[]{result});
            ret = (true == CheckXml.checkOk(result).equals("ok"))?true: false;

        }else{
            //modify vpn
            LOG.info("VPN {} is already exist ,update",vpnName);
            boolean isRdChanged=false;
            boolean isRtChanged=false;
            boolean isIfmChanged=false;
            if(rd!=vpnInstance.getRd()){
                //delete rd,add rd rt ebgp
                isRdChanged=true;
                isRtChanged=true;
                //String sendMsg = VpnXml.modifyVpnXml(isRdChanged,isRtChanged,isEbgpChanged,isIfmChanged,l3vpnInstance);
            }else{
                if(exportRT!=vpnInstance.getExportRT()){
                    isRtChanged=true;
                }
            }

            if(l3vpnIf.equals(vpnInstance.getDeviceInterfaceList())){
                isIfmChanged=true;
            }

        }

        //vpn info update in vpnManager
        if(true == ret)
        {
            this.vpnInstanceManager.updateVpnInstance(vpnName,routerId, device,businessRegion,rd,importRT, exportRT,
                    peerAS,peerIP,routeSelectDelay,importDirectRouteEnable,deviceInterfaceList,networkSegList);
        }

        return true;
    }
    public boolean delVpnInstance(Integer id)
    {
        return (null == this.vpnInstanceManager)?false: this.vpnInstanceManager.delVpnInstance(id);
    }
    public boolean delVpnInstance(String routerId,String vpnName)
    {
        if((null == routerId)||(routerId.isEmpty())||(null == vpnName)||(vpnName.isEmpty()))
        {
            return false;
        }

        Device device = this.deviceManager.getDevice(routerId);
        if(null == device) {
            return false;
        }
        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        LOG.info("enter getVpnIstance");
        String sendMsg = VpnXml.getDeleteL3vpnXml(vpnName);
        LOG.info("get sendMsg={}", new Object[]{sendMsg});
        String result = netconfController.sendMessage(netconfClient, sendMsg);
        LOG.info("get result={}", new Object[]{result});
        boolean ret = (true == CheckXml.checkOk(result).equals("ok"))?true: false;
        if(true == ret) {
            this.vpnInstanceManager.delVpnInstance(routerId, vpnName);
        }
        return ret;
    }
    public VPNInstance getVpnInstance(Integer id)
    {
        VPNInstance vpnInsntance = this.vpnInstanceManager.getVpnInstance(id);
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
        if((null == routerId)||routerId.isEmpty()||(null == vpnName)||vpnName.isEmpty()) {
            return null;
        }
        Device device = this.deviceManager.getDevice(routerId);
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
            vpnInsntance = mapL3vpnInstanceToVPNInstance(l3vpnInstance,routerId);
        }
        return vpnInsntance;
    }
    public List<VPNInstance> getVpnInstanceListFromDevice(String vpnName)
    {
        List<VPNInstance> vpnInstancelist=new LinkedList<VPNInstance>();
        List<Device>  deviceList = this.deviceManager.getDeviceList();
        for (Device device:deviceList) {
            NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
            LOG.info("enter getVpnInstanceListFromDevice");
            String sendMsg = VpnXml.getVpnXml(vpnName);
            LOG.info("get sendMsg={}", new Object[]{sendMsg});
            String result = netconfController.sendMessage(netconfClient, sendMsg);
            List<L3vpnInstance> l3vpnInstances = VpnXml.getVpnFromXml(result);
            if(null != l3vpnInstances) {
                for(L3vpnInstance l3vpnInstance:l3vpnInstances){
                    VPNInstance tmpVPNInstance=mapL3vpnInstanceToVPNInstance(l3vpnInstance,device.getRouterId());
                    if(null!=tmpVPNInstance){
                        vpnInstancelist.add(tmpVPNInstance);
                    }
                }
            }
        }
        return vpnInstancelist;
    }
    public List<VPNInstance> getVpnInstanceList(String vpnName)
    {
        if(null == vpnName)
        {
            return null;
        }
        List<VPNInstance> vpnInstanceList = this.vpnInstanceManager.getVpnInstanceList();
        List<VPNInstance> vpnInstances = new LinkedList<VPNInstance>();
        if(true == vpnName.equals("")) {
            return vpnInstanceList;
        }
        else
        {
            for (VPNInstance vpnInstance:vpnInstanceList) {
                if(true == vpnName.equals(vpnInstance.getVpnName())) {
                    vpnInstances.add(vpnInstance);
                }
            }
        }
        return vpnInstances;
    }
    public String getTest() {
        return null;
    }




    private VPNInstance mapL3vpnInstanceToVPNInstance(L3vpnInstance l3vpnInstance,String routerId){
        if(l3vpnInstance==null||l3vpnInstance.getVrfName()==null||routerId==null){
            LOG.info("get l3vpnInstance={} routerId={}", new Object[]{l3vpnInstance.toString(),routerId});
            return null;
        }
        if(l3vpnInstance.getVrfName().isEmpty()||routerId.isEmpty()){
            LOG.info("get l3vpnInstance.getVrfName()={} routerId={}", new Object[]{l3vpnInstance.getVrfName().isEmpty(),routerId.isEmpty()});
            return null;
        }

        Device device = this.deviceManager.getDevice(routerId);
        if(null == device) {
            LOG.info("get device={} ", new Object[]{device.toString()});
            return null;
        }
        VPNInstance vpnInstance= new VPNInstance(l3vpnInstance.getVrfName(),routerId);

        vpnInstance.setDevice(device);
        vpnInstance.setRd(l3vpnInstance.getVrfRD());
        vpnInstance.setExportRT(l3vpnInstance.getVrfRTValue());
        vpnInstance.setImportRT(l3vpnInstance.getVrfRTValue());
        LOG.info("get vrfname={} rt={}", new Object[]{l3vpnInstance.getVrfRD(),l3vpnInstance.getVrfRTValue()});
        List<L3vpnIf> l3vpnIfs = l3vpnInstance.getL3vpnIfs();
        if(null != l3vpnIfs) {
            DeviceInterface deviceInterface = null;
            List<DeviceInterface> deviceInterfaces = new LinkedList<DeviceInterface>();
            for (L3vpnIf l3vpnIf : l3vpnIfs) {
                deviceInterface = new DeviceInterface(l3vpnIf.getIfName(), new Address(l3vpnIf.getIpv4Addr(), AddressTypeEnum.V4),
                        new Address(l3vpnIf.getSubnetMask(), AddressTypeEnum.V4));
                LOG.info("get ifname={} ip={} mask={}", new Object[]{l3vpnIf.getIfName(), l3vpnIf.getIpv4Addr(), l3vpnIf.getSubnetMask()});
                deviceInterfaces.add(deviceInterface);
            }
            vpnInstance.setDeviceInterfaceList(deviceInterfaces);
        }
        return vpnInstance;
    }

    @Override
    public String syncVpnInstanceConf() {
        return null;
    }
}
