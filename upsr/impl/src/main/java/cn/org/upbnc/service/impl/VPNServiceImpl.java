/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.*;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.entity.TunnelPolicy.TpNexthop;
import cn.org.upbnc.entity.TunnelPolicy.TunnelPolicy;
import cn.org.upbnc.enumtype.*;
import cn.org.upbnc.enumtype.VpnEnum.VpnApplyLabelEnum;
import cn.org.upbnc.enumtype.VpnEnum.VpnFrrStatusEnum;
import cn.org.upbnc.enumtype.VpnEnum.VpnTtlModeEnum;
import cn.org.upbnc.service.VPNService;
import cn.org.upbnc.service.entity.UpdateVpnInstance;
import cn.org.upbnc.util.netconf.*;
import cn.org.upbnc.util.netconf.bgp.BgpPeer;
import cn.org.upbnc.util.netconf.bgp.BgpVrf;
import cn.org.upbnc.util.netconf.bgp.ImportRoute;
import cn.org.upbnc.util.netconf.bgp.NetworkRoute;
import cn.org.upbnc.util.xml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class VPNServiceImpl implements VPNService {
    private static final Logger LOG = LoggerFactory.getLogger(VPNServiceImpl.class);
    private static VPNService ourInstance = null;
    private BaseInterface baseInterface = null;
    private VpnInstanceManager vpnInstanceManager = null;
    private NetConfManager netConfManager = null;
    private DeviceManager deviceManager = null;
    private TunnelManager tunnelManager = null;

    private static long TUNNELID_MAX = 4294967295l;
    private static int BFDID_MAX = 65535;

    private Map<String, List<String>> tunnelMapTemp;
    private Map<String, List<Integer>> bfdDiscriminatorTemp;

    public static VPNService getInstance() {
        if (null == ourInstance) {
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
        if (null != baseInterface) {
            vpnInstanceManager = this.baseInterface.getVpnInstanceManager();
            netConfManager = this.baseInterface.getNetConfManager();
            deviceManager = this.baseInterface.getDeviceManager();
            tunnelManager = baseInterface.getTunnelManager();
        }
        return true;
    }

    public Map<String, Object> updateVpnInstance(UpdateVpnInstance updateVpnInstance) {
        String vpnName = updateVpnInstance.getVpnName();
        String routerId = updateVpnInstance.getRouterId();
        String businessRegion = updateVpnInstance.getBusinessRegion();
        String rd = updateVpnInstance.getRd();
        String importRT = updateVpnInstance.getImportRT();
        String exportRT = updateVpnInstance.getExportRT();
        Integer peerAS = updateVpnInstance.getPeerAS();
        Address peerIP = updateVpnInstance.getPeerIP();
        Integer routeSelectDelay = updateVpnInstance.getRouteSelectDelay();
        Integer importDirectRouteEnable = updateVpnInstance.getImportDirectRouteEnable();
        List<DeviceInterface> deviceInterfaceList = updateVpnInstance.getDeviceInterfaceList();
        List<NetworkSeg> networkSegList = updateVpnInstance.getNetworkSegList();


        boolean ret = false;
        Device device = null;
        VPNInstance vpnInstance = null;
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ResponseEnum.BODY.getName(), false);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        if (null == this.vpnInstanceManager) {
            resultMap.put(ResponseEnum.MESSAGE.getName(), "vpnInstanceManager is null.");
            return resultMap;
        }
        LOG.info("service updateVpnInstance");

        if (null != this.baseInterface) {
            device = this.deviceManager.getDevice(routerId);
        }
        if ((null == device) || (null == device.getNetConf())) {
            resultMap.put(ResponseEnum.MESSAGE.getName(), "device is null or device netconf is not set.");
            return resultMap;
        }

        List<L3vpnIf> l3vpnIfList = new LinkedList<L3vpnIf>();
        if (null != deviceInterfaceList) {
            for (DeviceInterface deviceInterface : deviceInterfaceList) {
                L3vpnIf l3vpnIf = new L3vpnIf(deviceInterface.getName(), null, null);
                if (null != deviceInterface.getIp()) {
                    l3vpnIf.setIpv4Addr(deviceInterface.getIp().getAddress());
                }
                if (null != deviceInterface.getMask()) {
                    l3vpnIf.setSubnetMask(deviceInterface.getMask().getAddress());
                }
                l3vpnIfList.add(l3vpnIf);
            }
        }
        if (null == device.getNetConf().getIp()) //device.getNetConf() can't be null before
        {
            LOG.info("service updateVpnInstance null == device.getNetConf().getIp()");
            resultMap.put(ResponseEnum.MESSAGE.getName(), "device.getNetConf().getIp() is null.");
            return resultMap;
        }

        L3vpnInstance l3vpnInstance = new L3vpnInstance(vpnName, businessRegion, rd, exportRT, l3vpnIfList,
                updateVpnInstance.getTunnelPolicy(),updateVpnInstance.getVpnFrr(),updateVpnInstance.getApplyLabel(),
                updateVpnInstance.getTtlMode());
        if (null != updateVpnInstance.getNote()) {
            l3vpnInstance.setVrfDescription(updateVpnInstance.getNote());
        }
        BgpVrf bgpVrf = mapEbgpInfoToBgpVfr(vpnName, peerAS, peerIP, routeSelectDelay, importDirectRouteEnable,
                networkSegList,updateVpnInstance.getEbgpPreference(),updateVpnInstance.getIbgpPreference(),
                updateVpnInstance.getLocalPreference(),updateVpnInstance.getRouterImportPolicy(),
                updateVpnInstance.getRouterExportPolicy(),updateVpnInstance.getAdvertiseCommunity());

        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getRouterID());

        vpnInstance = this.vpnInstanceManager.getVpnInstance(routerId, vpnName);

        if (null == vpnInstance) {
            //create vpn
            LOG.info("vpnInstance is null,add VPN " + vpnName);

            String sendMsg = VpnXml.createVpnXml(l3vpnInstance);
            LOG.info("sendMsg={}", new Object[]{sendMsg});
            String result = null;
            result = netconfController.sendMessage(netconfClient, sendMsg);
            LOG.info("result={}", new Object[]{result});
            ret = CheckXml.checkOk(result).equals("ok");
            if (!ret) {
                resultMap.put(ResponseEnum.MESSAGE.getName(), "create vpn error.");
                return resultMap;
            } else {
                //接口添加vpn
            }
            sendMsg = EbgpXml.createEbgpXml(bgpVrf);
            LOG.info("sendMsg={}", new Object[]{sendMsg});
            result = netconfController.sendMessage(netconfClient, sendMsg);
            LOG.info("result={}", new Object[]{result});
            ret = CheckXml.checkOk(result).equals("ok");
            if (!ret) {
                resultMap.put(ResponseEnum.MESSAGE.getName(), "create ebgp error.");
                return resultMap;
            }
        } else {
            //modify vpn
            LOG.info("VPN {} is already exist ,update", vpnName);

            Map<String, Boolean> modifyMap = vpnInstance.compareVpnInfo(vpnName, routerId, businessRegion,
                    rd, importRT, exportRT, peerAS, peerIP, routeSelectDelay, importDirectRouteEnable,
                    deviceInterfaceList, networkSegList);
            String sendMsg = "";
            L3vpnInstance l3vpnInstance1 = new L3vpnInstance();
            l3vpnInstance1.setVrfName(l3vpnInstance.getVrfName());
            l3vpnInstance1.setL3vpnIfs(l3vpnInstance.getL3vpnIfs());
            l3vpnInstance1.setVrfRTValue(l3vpnInstance.getVrfRTValue());
            l3vpnInstance1.setVrfDescription(l3vpnInstance.getVrfDescription());
            l3vpnInstance1.setVrfRD(l3vpnInstance.getVrfRD());
            if (null == vpnInstance.getDeviceInterfaceList() || vpnInstance.getDeviceInterfaceList().size() == 0) {
                List<L3vpnIf> l3vpnIfs = new ArrayList<>();
                l3vpnInstance1.setL3vpnIfs(l3vpnIfs);
            }
            if (vpnInstance.ebgpIsNull()) {
                sendMsg = VpnUpdateXml.getUpdateVpnDeleteXml(modifyMap, l3vpnInstance1, null);
            } else {
                sendMsg = VpnUpdateXml.getUpdateVpnDeleteXml(modifyMap, l3vpnInstance1, new BgpVrf(vpnName, null, null, null));
            }
            LOG.info("sendMsg={}", new Object[]{sendMsg});
            String result;
            if (sendMsg.contains("  <config>\n" +
                    "  </config>")) {
                ret = true;
            } else {
                result = netconfController.sendMessage(netconfClient, sendMsg);
                LOG.info("result={}", new Object[]{result});
                ret = CheckXml.checkOk(result).equals("ok");
            }

            if (!ret) {
                resultMap.put(ResponseEnum.MESSAGE.getName(), "vpn update(delete) error.");
                return resultMap;
            } else {
                //删除接口上的vpn
            }

            sendMsg = VpnUpdateXml.getUpdateVpnAddXml(modifyMap, l3vpnInstance, bgpVrf);
            if (sendMsg.contains("<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                    "  <target>\n" +
                    "    <running/>\n" +
                    "  </target>\n" +
                    "  <error-option>rollback-on-error</error-option>\n" +
                    "  <config>\n" +
                    "  </config>\n" +
                    "</edit-config>")) {

            } else {
                LOG.info("sendMsg={}", new Object[]{sendMsg});
                result = netconfController.sendMessage(netconfClient, sendMsg);
                LOG.info("result={}", new Object[]{result});
                ret = CheckXml.checkOk(result).equals("ok");
                if (!ret) {
                    resultMap.put(ResponseEnum.MESSAGE.getName(), "vpn update(add) error.");
                    return resultMap;
                } else {
                    //往接口添加vpn
                }
            }
            if (true == ret) {
                sendMsg = VpnUpdateXml.vpnApplyLabelUpdateXml(l3vpnInstance);
                LOG.info("vpnApplyLabelUpdateXml sendMsg = \n" + sendMsg);
                result = netconfController.sendMessage(netconfClient, sendMsg);
                LOG.info("vpnApplyLabelUpdateXml result = \n" + result);
                sendMsg = VpnUpdateXml.vpnApplyEbgpXml(bgpVrf);
                LOG.info("vpnApplyEbgpXml sendMsg = \n" + sendMsg);
                result = netconfController.sendMessage(netconfClient, sendMsg);
                LOG.info("vpnApplyEbgpXml result = \n" + result);
            }
        }
        //vpn info update in vpnManager
        if (true == ret) {
            this.vpnInstanceManager.updateVpnInstance(vpnName, routerId, device, businessRegion, rd, importRT, exportRT,
                    peerAS, peerIP, routeSelectDelay, importDirectRouteEnable, deviceInterfaceList, networkSegList, updateVpnInstance.getNote(),
                    updateVpnInstance.getTunnelPolicy(),updateVpnInstance.getVpnFrr(),updateVpnInstance.getApplyLabel(),
                    updateVpnInstance.getTtlMode(),updateVpnInstance.getEbgpPreference(),updateVpnInstance.getIbgpPreference(),
                    updateVpnInstance.getLocalPreference(),updateVpnInstance.getRouterImportPolicy(),
                    updateVpnInstance.getRouterExportPolicy(),updateVpnInstance.getAdvertiseCommunity());
        }
        resultMap.put(ResponseEnum.BODY.getName(), true);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        return resultMap;
    }

    public boolean delVpnInstance(Integer id) {
        return (null == this.vpnInstanceManager) ? false : this.vpnInstanceManager.delVpnInstance(id);
    }

    private Map<String, Object> delVpnInstanceByName(String vpnName) {
        Map<String, Object> resultMap = new HashMap<>();
        List<Device> deviceList = this.deviceManager.getDeviceList();
        for (Device device : deviceList) {
            if (null == device.getNetConf())  //may be some device generate by bgpls
            {
                continue;
            }
            List<VPNInstance> vpnInstanceList = this.vpnInstanceManager.getVpnInstanceListByRouterId(device.getRouterId());
            for (VPNInstance vpnInstance : vpnInstanceList) {

                if (true == vpnName.equals(vpnInstance.getVpnName())) {
                    NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getRouterID());
                    String sendMsg = VpnXml.getDeleteL3vpnXml(vpnName);
                    LOG.info("get sendMsg={}", new Object[]{sendMsg});
                    String result = netconfController.sendMessage(netconfClient, sendMsg);
                    LOG.info("get result={}", new Object[]{result});
                    boolean ret = CheckXml.checkOk(result).equals("ok");
                    if (true != ret) {
                        resultMap.put(ResponseEnum.BODY.getName(), false);
                        return resultMap;
                    }
                    this.vpnInstanceManager.delVpnInstance(device.getRouterId(), vpnName);
                }

            }

        }
        resultMap.put(ResponseEnum.BODY.getName(), true);
        return resultMap;
    }

    public Map<String, Object> delVpnInstance(String routerId, String vpnName) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        resultMap.put(ResponseEnum.BODY.getName(), false);
        if ((null == routerId) || (true == routerId.equals(""))) {
            return delVpnInstanceByName(vpnName);
        }
        Device device = this.deviceManager.getDevice(routerId);
        if ((null == device) || (null == device.getNetConf())) {
            resultMap.put(ResponseEnum.MESSAGE.getName(), "device is null or device netconf is not set.");
            return resultMap;
        }
        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getRouterID());
        LOG.info("enter delVpnIstance");
        String sendMsg = VpnXml.getDeleteL3vpnXml(vpnName);
        LOG.info("get sendMsg={}", new Object[]{sendMsg});
        String result = netconfController.sendMessage(netconfClient, sendMsg);
        LOG.info("get result={}", new Object[]{result});
        boolean ret = CheckXml.checkOk(result).equals("ok");
        if (true == ret) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
            this.vpnInstanceManager.delVpnInstance(routerId, vpnName);
        }
        resultMap.put(ResponseEnum.BODY.getName(), ret);
        return resultMap;
    }

    public VPNInstance getVpnInstanceFromDevice(String routerId, String vpnName) {
        if ((null == routerId) || routerId.isEmpty() || (null == vpnName) || vpnName.isEmpty()) {
            return null;
        }
        Device device = this.deviceManager.getDevice(routerId);
        if ((null == device) || (null == device.getNetConf())) {
            return null;
        }
        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getRouterID());

        LOG.info("enter getVpnIstance");
        String sendMsg = VpnXml.getVpnXml(vpnName);
        LOG.info("get vpnInfo sendMsg={}", new Object[]{sendMsg});
        String result = netconfController.sendMessage(netconfClient, sendMsg);
        LOG.info("get vpnInfo result={}", new Object[]{result});
        List<L3vpnInstance> l3vpnInstances = VpnXml.getVpnFromXml(result);
        sendMsg = EbgpXml.getEbgpXml(vpnName);
        LOG.info("get ebgpInfo sendMsg={}", new Object[]{sendMsg});
        result = netconfController.sendMessage(netconfClient, sendMsg);
        LOG.info("get vpnInfo result={}", new Object[]{result});

        List<BgpVrf> bgpVrfList = EbgpXml.getEbgpFromXml(result);

        if (null == l3vpnInstances) {
            return null;
        }
        VPNInstance vpnInsntance = null;
        for (L3vpnInstance l3vpnInstance : l3vpnInstances) {
            vpnInsntance = mapL3vpnInstanceToVPNInstance(l3vpnInstance, routerId);
            if (null != bgpVrfList && null != vpnInsntance) {
                for (BgpVrf bgpVrf : bgpVrfList) {
                    if (vpnInsntance.getVpnName().equals(bgpVrf.getVrfName())) {
                        mapBgpVrfToVPNInstance(vpnInsntance, bgpVrf);
                    }
                }
            }
        }
        return vpnInsntance;
    }

    public List<VPNInstance> getVpnInstanceListFromDevice(String vpnName) {
        List<VPNInstance> vpnInstancelist = new ArrayList<VPNInstance>();
        List<Device> deviceList = this.deviceManager.getDeviceList();
        for (Device device : deviceList) {
            List<VPNInstance> tmp = getVpnInstanceListFromDevice(device.getRouterId(), vpnName);
            if (tmp != null || tmp.size() != 0) {
                vpnInstancelist.addAll(tmp);
            }
        }
        return vpnInstancelist;
    }

    public List<VPNInstance> getVpnInstanceListFromDevice(String routerId, String vpnName) {
        List<VPNInstance> vpnInstancelist = new LinkedList<VPNInstance>();
        Device device = this.deviceManager.getDevice(routerId);
        if ((null == device) || (null == device.getNetConf())) {
            return vpnInstancelist;
        }

        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getRouterID());
        LOG.info("enter getVpnInstanceListFromDevice");
        String sendMsg = VpnXml.getVpnXml(vpnName);
        LOG.info("get sendMsg={}", new Object[]{sendMsg});
        String result = netconfController.sendMessage(netconfClient, sendMsg);
        List<L3vpnInstance> l3vpnInstances = VpnXml.getVpnFromXml(result);

        sendMsg = EbgpXml.getEbgpXml(vpnName);
        LOG.info("get ebgpInfo sendMsg={}", new Object[]{sendMsg});
        result = netconfController.sendMessage(netconfClient, sendMsg);
        LOG.info("get ebgpInfo result={}", new Object[]{result});

        List<BgpVrf> bgpVrfList = EbgpXml.getEbgpFromXml(result);
        VPNInstance vpnInsntance = null;
        if (null != l3vpnInstances) {
            for (L3vpnInstance l3vpnInstance : l3vpnInstances) {
                vpnInsntance = mapL3vpnInstanceToVPNInstance(l3vpnInstance, device.getRouterId());
                if (null != vpnInsntance) {
                    if (null != bgpVrfList) {
                        for (BgpVrf bgpVrf : bgpVrfList) {
                            if (bgpVrf.getVrfName().equals(vpnInsntance.getVpnName())) {
                                String importRoutePolicyName = null;
                                for (SBgpVrfAF sBgpVrfAF : bgpVrf.getBgpVrfAFs()) {
                                    for (SPeerAF sPeerAF : sBgpVrfAF.getPeerAFs()) {
                                        LOG.info("sPeerAF.getImportRtPolicyName() :" + sPeerAF.getImportRtPolicyName());
                                        importRoutePolicyName = sPeerAF.getImportRtPolicyName();
                                        vpnInsntance.setImportRoutePolicyName(importRoutePolicyName);
                                        vpnInsntance.setExportRoutePolicyName(sPeerAF.getExportRtPolicyName());
                                    }
                                }
                                if (null != importRoutePolicyName) {
                                    sendMsg = RoutePolicyXml.getRoutePolicyXml(importRoutePolicyName);
                                    LOG.info("getRoutePolicyXml sendMsg={}", new Object[]{sendMsg});
                                    result = netconfController.sendMessage(netconfClient, sendMsg);
                                    LOG.info("getRoutePolicyXml result={}", new Object[]{result});

                                    List<SRoutePolicy> sRoutePolicies = RoutePolicyXml.getRoutePolicyFromXml(result);
                                    for (SRoutePolicy routePolicy : sRoutePolicies) {
                                        routePolicy.toString();
                                    }
                                }
                                if (vpnInsntance.getVpnName().equals(bgpVrf.getVrfName())) {
                                    mapBgpVrfToVPNInstance(vpnInsntance, bgpVrf);
                                }
                            }
                        }
                    }
                    vpnInstancelist.add(vpnInsntance);
                }
            }
        } else {
            return null;
        }
        return vpnInstancelist;
    }

    public Map<String, Object> getVpnInstanceList(String vpnName) {
        Map<String, Object> resultMap = new HashMap<>();
        List<VPNInstance> vpnInstanceList = this.vpnInstanceManager.getVpnInstanceList();
        List<VPNInstance> vpnInstances = new LinkedList<VPNInstance>();
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        if (true == vpnName.equals("")) {
            resultMap.put(ResponseEnum.BODY.getName(), vpnInstanceList);
            return resultMap;
        } else {
            for (VPNInstance vpnInstance : vpnInstanceList) {
                if (true == vpnName.equals(vpnInstance.getVpnName())) {
                    vpnInstances.add(vpnInstance);
                }
            }
        }
        resultMap.put(ResponseEnum.BODY.getName(), vpnInstances);
        return resultMap;
    }

    public Map<String, Object> getVpnInstance(String routerId, String vpnName) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        resultMap.put(ResponseEnum.BODY.getName(), null);
        List<VPNInstance> vpnInstanceList = this.vpnInstanceManager.getVpnInstanceList();
        for (VPNInstance vpnInstance : vpnInstanceList) {
            if (vpnName.equals(vpnInstance.getVpnName()) && routerId.equals(vpnInstance.getRouterId())) {
                resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
                resultMap.put(ResponseEnum.BODY.getName(), vpnInstance);
                resultMap.put(ResponseEnum.MESSAGE.getName(), "success");
                return resultMap;
            }
        }
        return resultMap;
    }

    public String getTest() {
        return null;
    }


    private VPNInstance mapL3vpnInstanceToVPNInstance(L3vpnInstance l3vpnInstance, String routerId) {
        if (l3vpnInstance == null || l3vpnInstance.getVrfName() == null || routerId == null) {
            LOG.info("get l3vpnInstance={} routerId={}", new Object[]{l3vpnInstance.toString(), routerId});
            return null;
        }
        if (l3vpnInstance.getVrfName().isEmpty() || routerId.isEmpty()) {
            LOG.info("get l3vpnInstance.getVrfName()={} routerId={}", new Object[]{l3vpnInstance.getVrfName().isEmpty(), routerId.isEmpty()});
            return null;
        }

        Device device = this.deviceManager.getDevice(routerId);
        if (null == device) {
            LOG.info("get device={} ", new Object[]{device.toString()});
            return null;
        }
        VPNInstance vpnInstance = new VPNInstance(routerId, l3vpnInstance.getVrfName());
        vpnInstance.setDevice(device);
        vpnInstance.setRd(l3vpnInstance.getVrfRD());
        vpnInstance.setImportTunnelPolicyName(l3vpnInstance.getTunnelPolicy());
        vpnInstance.setVpnFrr(l3vpnInstance.getVpnFrr());
        vpnInstance.setApplyLabel(l3vpnInstance.getApplyLabel());
        vpnInstance.setTtlMode(l3vpnInstance.getTtlMode());
        vpnInstance.setExportRT(l3vpnInstance.getVrfRTValue());
        vpnInstance.setImportRT(l3vpnInstance.getVrfRTValue());
        vpnInstance.setNote(l3vpnInstance.getVrfDescription());
        vpnInstance.setBusinessRegion("VPN隔离");
        LOG.info("get vrfname={} rt={}", new Object[]{l3vpnInstance.getVrfRD(), l3vpnInstance.getVrfRTValue()});
        List<L3vpnIf> l3vpnIfs = l3vpnInstance.getL3vpnIfs();
        if (null != l3vpnIfs) {
            DeviceInterface deviceInterface = null;
            List<DeviceInterface> deviceInterfaces = new LinkedList<DeviceInterface>();
            for (L3vpnIf l3vpnIf : l3vpnIfs) {
                deviceInterface = new DeviceInterface(l3vpnIf.getIfName(), new Address(l3vpnIf.getIpv4Addr(), AddressTypeEnum.V4),
                        new Address(l3vpnIf.getSubnetMask(), AddressTypeEnum.V4));
                LOG.info("get ifname={} ip={} mask={}", new Object[]{l3vpnIf.getIfName(), l3vpnIf.getIpv4Addr(), l3vpnIf.getSubnetMask()});
                deviceInterfaces.add(deviceInterface);
            }

            for (DeviceInterface deviceInterface1 : vpnInstance.getDeviceInterfaceList()) {
                for (DeviceInterface deviceInterface2 : vpnInstance.getDevice().getDeviceInterfaceList()) {
                    if (deviceInterface1.getName().equals(deviceInterface2.getName())) {
                        deviceInterface2.setVpn(null);
                    }
                }
            }
            for (DeviceInterface deviceInterface1 : deviceInterfaces) {
                for (DeviceInterface deviceInterface2 : vpnInstance.getDevice().getDeviceInterfaceList()) {
                    if (deviceInterface1.getName().equals(deviceInterface2.getName())) {
                        deviceInterface2.setVpn(vpnInstance);
                    }
                }
            }

            vpnInstance.setDeviceInterfaceList(deviceInterfaces);
        }
        return vpnInstance;
    }

    private void mapBgpVrfToVPNInstance(VPNInstance vpnInstance, BgpVrf bgpVrf) {
        if (bgpVrf.getBgpPeers() != null && bgpVrf.getBgpPeers().size() != 0) {
            vpnInstance.setPeerAS(Integer.parseInt(bgpVrf.getBgpPeers().get(0).getRemoteAs()));
            vpnInstance.setPeerIP(new Address(bgpVrf.getBgpPeers().get(0).getPeerAddr(), AddressTypeEnum.V4));
        }
        if (bgpVrf.getBgpVrfAFs().size() > 0) {
            vpnInstance.setEbgpPreference(bgpVrf.getBgpVrfAFs().get(0).getPreferenceExternal());
            vpnInstance.setIbgpPreference(bgpVrf.getBgpVrfAFs().get(0).getPreferenceInternal());
            vpnInstance.setLocalPreference(bgpVrf.getBgpVrfAFs().get(0).getPreferenceLocal());
            if (bgpVrf.getBgpVrfAFs().get(0).getPeerAFs().size() > 0) {
                vpnInstance.setImportRoutePolicyName(bgpVrf.getBgpVrfAFs().get(0).getPeerAFs().get(0).getImportRtPolicyName());
                vpnInstance.setExportRoutePolicyName(bgpVrf.getBgpVrfAFs().get(0).getPeerAFs().get(0).getExportRtPolicyName());
                vpnInstance.setAdvertiseCommunity(bgpVrf.getBgpVrfAFs().get(0).getPeerAFs().get(0).getAdvertiseCommunity());
			}
		}

        if (bgpVrf.getNetworkRoutes() != null && bgpVrf.getNetworkRoutes().size() != 0) {
            List<NetworkSeg> networkSegList = new ArrayList<NetworkSeg>();
            for (NetworkRoute networkRoute : bgpVrf.getNetworkRoutes()) {
                networkSegList.add(new NetworkSeg(new Address(networkRoute.getNetworkAddress(), AddressTypeEnum.V4),
                        convertMaskLengthToIp(Integer.parseInt(networkRoute.getMaskLen()))));
            }
            vpnInstance.setNetworkSegList(networkSegList);
        }
        if (bgpVrf.getImportRoutes() != null && bgpVrf.getImportRoutes().size() != 0) {
            if (bgpVrf.getImportRoutes().get(0).equals("direct")) {
                vpnInstance.setImportDirectRouteEnable(1);
            } else {
                vpnInstance.setImportDirectRouteEnable(2);
            }
        } else {
            vpnInstance.setImportDirectRouteEnable(2);
        }

    }

    private BgpVrf mapEbgpInfoToBgpVfr(String vfrName, Integer peerAS, Address peerIP, Integer routeSelectDelay, Integer importDirectRouteEnable,
                                       List<NetworkSeg> networkSegList,String ebgpPreference, String ibgpPreference, String localPreference,
                                       String routerImportPolicy, String routerExportPolicy, String advertiseCommunity) {

        if ((null == peerAS || 0 == peerAS) && (null == peerIP || peerIP.getAddress().equals("")) &&
                (null == importDirectRouteEnable || 0 == importDirectRouteEnable) && (null == networkSegList || networkSegList.size() == 0)) {
            return null;
        }
        BgpVrf bgpVrf = new BgpVrf();
        bgpVrf.setVrfName(vfrName);
        List<BgpPeer> bgpPeerList = new ArrayList<BgpPeer>();
        List<ImportRoute> importRouteList = new ArrayList<ImportRoute>();
        List<NetworkRoute> networkRouteList = new ArrayList<NetworkRoute>();
        List<SBgpVrfAF> bgpVrfAFList = new ArrayList<>();
        BgpPeer bgpPeer = null;

        if ((null != peerIP) && (null != peerAS)) {
            bgpPeer = new BgpPeer(peerIP.getAddress(), peerAS.toString());
            bgpPeerList.add(bgpPeer);
        }
        bgpVrf.setBgpPeers(bgpPeerList);
        if ((null != importDirectRouteEnable) && importDirectRouteEnable.equals(1)) {
            ImportRoute importRoute = new ImportRoute("direct", "0");
            importRouteList.add(importRoute);
        }
        bgpVrf.setImportRoutes(importRouteList);
        if (networkSegList != null) {
            for (NetworkSeg networkSeg : networkSegList) {
                Integer masklen = convertMaskIPtoLength(networkSeg.getMask().getAddress());
                LOG.info("mask " + networkSeg.getMask().getAddress() + "masklength " + masklen);
                NetworkRoute networkRoute = new NetworkRoute(networkSeg.getAddress().getAddress(), masklen.toString());
                networkRouteList.add(networkRoute);
            }
            bgpVrf.setNetworkRoutes(networkRouteList);
        }
        SBgpVrfAF sBgpVrfAF = new SBgpVrfAF();
        sBgpVrfAF.setPreferenceExternal(ebgpPreference);
        sBgpVrfAF.setPreferenceInternal(ibgpPreference);
        sBgpVrfAF.setPreferenceLocal(localPreference);
        bgpVrfAFList.add(sBgpVrfAF);
        List<SPeerAF> sPeerAFList = new ArrayList<>();
        SPeerAF sPeerAF = new SPeerAF();
        sPeerAF.setRemoteAddress(peerIP.getAddress());
        sPeerAF.setImportRtPolicyName(routerImportPolicy);
        sPeerAF.setExportRtPolicyName(routerExportPolicy);
        sPeerAF.setAdvertiseCommunity(advertiseCommunity);
        sPeerAFList.add(sPeerAF);
        sBgpVrfAF.setPeerAFs(sPeerAFList);
        bgpVrf.setBgpVrfAFs(bgpVrfAFList);

        return bgpVrf;
    }

    /*
       // sync vpnInstance configure
        */
    @Override
    public boolean syncVpnInstanceConf() {
        if (null == vpnInstanceManager) {
            LOG.info("syncVpnInstanceConf is failed, vpnInstanceManager is null");
            return false;
        }
        if (null != vpnInstanceManager.getVpnInstanceList()) {
            for (VPNInstance vpnInstanceFromMem : vpnInstanceManager.getVpnInstanceList()) {
                vpnInstanceFromMem.setRefreshFlag(false);
            }
        }
        for (Device device : deviceManager.getDeviceList()) {
            syncVpnInstanceConf(device.getRouterId());
        }
        return true;
    }

    @Override
    public boolean syncVpnInstanceConf(String routerId) {
        if (null == routerId || routerId.equals("")) {
            LOG.info("syncVpnInstanceConf failed,routerId is null or empty ");
            return false;
        }
        Device device = deviceManager.getDevice(routerId);
        if ((null != device.getNetConf()) && (device.getNetConf().getStatus() == NetConfStatusEnum.Connected)) {
            List<VPNInstance> vpnInstanceListFromDevice = getVpnInstanceListFromDevice(device.getRouterId(), "");
            if (null != vpnInstanceListFromDevice) {
                for (VPNInstance vpnInstanceFromMem : vpnInstanceManager.getVpnInstanceListByRouterId(device.getRouterId())) {
                    vpnInstanceFromMem.setRefreshFlag(false);
                }
                for (VPNInstance vpnInstanceFromDevice : vpnInstanceListFromDevice) {
                    vpnInstanceFromDevice.setRefreshFlag(true);
                    vpnInstanceManager.updateVpnInstance(vpnInstanceFromDevice);
                }
                for (VPNInstance vpnInstanceFromMem : vpnInstanceManager.getVpnInstanceListByRouterId(device.getRouterId())) {
                    if (!vpnInstanceFromMem.isRefreshFlag()) {
                        vpnInstanceManager.delVpnInstance(vpnInstanceFromMem.getRouterId(), vpnInstanceFromMem.getVpnName());
                    }
                }
            } else {
                LOG.info("Can not get device's VPN info which device routerId=" + device.getRouterId());
                return false;
            }
        } else {
            LOG.info("Can not connect device by Netconf , status is Disconnected,which device routerId=" + device.getRouterId());
            return false;
        }
        return true;
    }

    /*
    // list to map for rest api
     */
    @Override
    public Map<String, Object> getVpnInstanceMap(String vpnName) {
        Map<String, Object> resultMap = new HashMap<>();
        Boolean findFlag = false;
        List<VPNInstance> vpnInstances = null;
        Map<String, List<VPNInstance>> vpnInstanceMap = new HashMap<String, List<VPNInstance>>();
        List<VPNInstance> vpnInstanceList = this.vpnInstanceManager.getVpnInstanceList();
        if (true == vpnName.equals("")) {
            for (VPNInstance vpnInstance : vpnInstanceList) {
                if (vpnInstanceMap.containsKey(vpnInstance.getVpnName())) {
                    vpnInstanceMap.get(vpnInstance.getVpnName()).add(vpnInstance);
                } else {
                    vpnInstances = new LinkedList<VPNInstance>();
                    vpnInstances.add(vpnInstance);
                    vpnInstanceMap.put(vpnInstance.getVpnName(), vpnInstances);
                }
            }
        } else {
            vpnInstances = new LinkedList<VPNInstance>();
            for (VPNInstance vpnInstance : vpnInstanceList) {
                if (true == vpnName.equals(vpnInstance.getVpnName())) {
                    vpnInstances.add(vpnInstance);
                    findFlag = true;
                }
            }
            if (true == findFlag) {
                vpnInstanceMap.put(vpnName, vpnInstances);
            }
        }
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(), vpnInstanceMap);
        return resultMap;
    }

    private Integer convertMaskIPtoLength(String mask) {
        int[] radix = {255, 254, 252, 248, 240, 224, 192, 128, 0};
        int[] bitcount = {0, 0, 0, 0};
        int[] netmask_value = {0, 0, 0, 0};
        String[] netmask = {"0", "0", "0", "0"};
        if (true == mask.equals("")) {
            //mask = "255.255.255.0";
            return 24;
        }
        netmask = mask.split("\\.");
        netmask_value[0] = Integer.parseInt(netmask[0]);
        netmask_value[1] = Integer.parseInt(netmask[1]);
        netmask_value[2] = Integer.parseInt(netmask[2]);
        netmask_value[3] = Integer.parseInt(netmask[3]);

        for (int i = 0; i < netmask_value.length; i++) {
            for (int j = 0; j < radix.length; j++) {
                if (radix[j] == (netmask_value[i] & radix[j])) {
                    bitcount[i] = 8 - j;
                    break;
                }
            }
            if (8 != bitcount[i]) {
                break;
            }
        }
        return bitcount[0] + bitcount[1] + bitcount[2] + bitcount[3];
    }

    private Address convertMaskLengthToIp(Integer maskLength) {
        if (maskLength < 0 || maskLength > 32) {
            LOG.info("Mask length is illegal" + maskLength);
            return null;
        }
        int inetMask = Integer.valueOf(maskLength);
        int part = inetMask / 8;
        int remainder = inetMask % 8;
        int sum = 0;
        String ipMask = "";
        for (int i = 8; i > 8 - remainder; i--) {
            sum = sum + (int) Math.pow(2, i - 1);
        }
        if (part == 0) {
            ipMask = sum + ".0.0.0";
        } else if (part == 1) {
            ipMask = "255." + sum + ".0.0";
        } else if (part == 2) {
            ipMask = "255.255." + sum + ".0";
        } else if (part == 3) {
            ipMask = "255.255.255." + sum;
        } else if (part == 4) {
            ipMask = "255.255.255.255";
        }
        return new Address(ipMask, AddressTypeEnum.V4);
    }

    public Map<String, Object> isContainVpnName(String vpnName) {
        Map<String, Object> resultMap = new HashMap<>();
        if (null == this.vpnInstanceManager) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.BODY.getName(), false);
            resultMap.put(ResponseEnum.MESSAGE.getName(), "vpnInstanceManager is null.");
            return resultMap;
        }
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(), this.vpnInstanceManager.isContainVpnName(vpnName));
        resultMap.put(ResponseEnum.MESSAGE.getName(), "success.");
        return resultMap;
    }

    public Map<String, Object> isContainRd(String routerId, String rd) {
        Map<String, Object> resultMap = new HashMap<>();
        if (null == this.vpnInstanceManager) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.BODY.getName(), false);
            resultMap.put(ResponseEnum.MESSAGE.getName(), "vpnInstanceManager is null.");
        }
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(), this.vpnInstanceManager.isContainRd(routerId, rd));
        resultMap.put(ResponseEnum.MESSAGE.getName(), "success.");
        return resultMap;
    }

    /*********************************************
     *
     * Create Tunnels TunnelPolicys Bfds from topo
     *
     *********************************************/

    public void createTunnelsFromTopo(Map<String,List<Tunnel>> tunnels ,List<TunnelPolicy> tunnelPolicies){

        this.tunnelMapTemp = new HashMap<>();
        this.bfdDiscriminatorTemp = new HashMap<>();

        Map<String,List<Device>> area =this.deviceManager.getAreaDeviceList();
//        Map<String,List<Tunnel>> tunnels = new HashMap<String, List<Tunnel>>();
//        List<TunnelPolicy> tunnelPolicies = new ArrayList<TunnelPolicy>();

        Set<Map.Entry<String,List<Device>>> areaSet = area.entrySet();
        Iterator<Map.Entry<String,List<Device>>> iterator = areaSet.iterator();
        while(iterator.hasNext()){
            Map.Entry<String,List<Device>> entry = iterator.next();
            this.createByArea(entry,area,tunnelPolicies,tunnels);
        }


        return;
    }

    private void createByArea(Map.Entry<String,List<Device>> entry,
                              Map<String,List<Device>> area,
                              List<TunnelPolicy> tunnelPolicies,
                              Map<String,List<Tunnel>> tunnels){

        String currentArea = entry.getKey();
        List<Device> currentDevice = entry.getValue();
        Set<String> allArea = new HashSet<>(area.keySet());
        // remove current area
        allArea.remove(currentArea);

        for(Device device:currentDevice){
            for(String other : allArea){
                List<Device> list = area.get(other);
                this.createByDeviceToOtherArea(device,list,tunnelPolicies,tunnels);
            }
        }

        return;
    }

    private void createByDeviceToOtherArea(Device device,
                                           List<Device> list,
                                           List<TunnelPolicy> tunnelPolicies,
                                           Map<String,List<Tunnel>> tunnels){
        for(Device t : list){
            this.createTPByDeviceSrcToDeviceDest(device,t,tunnelPolicies,tunnels);
        }
        return;
    }

    private void createTPByDeviceSrcToDeviceDest(Device s,Device d,
                                                 List<TunnelPolicy> tunnelPolicies,
                                                 Map<String,List<Tunnel>> tunnels){
        String keySD = s.getSysName() + "_" + d.getSysName();
        String keyDS = d.getSysName() + "_" + s.getSysName();

        List<Tunnel> listSD = new ArrayList<Tunnel>();
        List<Tunnel> listDS = new ArrayList<Tunnel>();
        tunnels.put(keySD,listSD);
        tunnels.put(keyDS,listDS);

        TunnelPolicy tpS = this.getTunnelPolicy(s.getRouterId(),tunnelPolicies);
        TunnelPolicy tpD = this.getTunnelPolicy(d.getRouterId(),tunnelPolicies);
        List<TpNexthop> tpNexthopsS = tpS.getTpNexthops();
        List<TpNexthop> tpNexthopsD = tpD.getTpNexthops();

        TpNexthop tpNexthopS = this.getTpNextHopFromDest(d.getRouterId(),tpNexthopsS);
        TpNexthop tpNexthopD = this.getTpNextHopFromDest(s.getRouterId(),tpNexthopsD);

        this.createTunnelByDeviceSrcToDeviceDest(s,d,listSD,listDS,tpNexthopS,tpNexthopD, TunnelServiceClassEnum.AF1.getCode());
        this.createTunnelByDeviceSrcToDeviceDest(s,d,listSD,listDS,tpNexthopS,tpNexthopD,TunnelServiceClassEnum.AF3.getCode());
        this.createTunnelByDeviceSrcToDeviceDest(s,d,listSD,listDS,tpNexthopS,tpNexthopD,TunnelServiceClassEnum.EF.getCode());

        return;
    }

    private void createTunnelByDeviceSrcToDeviceDest(Device s,
                                                     Device d,
                                                     List<Tunnel> listSD,
                                                     List<Tunnel> listDS,
                                                     TpNexthop tpNexthopS,
                                                     TpNexthop tpNexthopD,
                                                     Integer serviceClassType){

        Tunnel tunnelSD = new Tunnel();
        Tunnel tunnelDS = new Tunnel();

        tunnelSD.setDevice(s);
        tunnelSD.setBfdType(BfdTypeEnum.Static.getCode());
        tunnelSD.setDestRouterId(d.getRouterId());
        tunnelSD.setServiceClass(serviceClassType);
        tunnelSD.setTunnelName(this.getTunnelName(s.getRouterId()));
        tpNexthopS.addTpTunnels(tunnelSD.getTunnelName());

        tunnelDS.setDevice(d);
        tunnelDS.setBfdType(BfdTypeEnum.Static.getCode());
        tunnelDS.setDestRouterId(s.getRouterId());
        tunnelDS.setServiceClass(serviceClassType);
        tunnelDS.setTunnelName(this.getTunnelName(s.getRouterId()));
        tpNexthopD.addTpTunnels(tunnelDS.getTunnelName());

        this.createBfdByDeviceSrcToDeviceDest(tunnelSD,tunnelDS);

        return;
    }

    private void createBfdByDeviceSrcToDeviceDest(Tunnel tunnelSD,Tunnel tunnelDS){
        BfdSession bfdSDTunnel = new BfdSession();
        BfdSession bfdSDLSP = new BfdSession();
        BfdSession bfdDSTunnel = new BfdSession();
        BfdSession bfdDSLSP = new BfdSession();

        String bfdSDTunnelLocal = this.getBfdDiscriminator(tunnelSD.getDevice().getRouterId());
        String bfdDSTunnelLocal = this.getBfdDiscriminator(tunnelSD.getDevice().getRouterId());
        String bfdSDLSPLocal = this.getBfdDiscriminator(tunnelSD.getDevice().getRouterId());
        String bfdDSLSPLocal = this.getBfdDiscriminator(tunnelSD.getDevice().getRouterId());

        bfdSDTunnel.setBfdName(tunnelSD.getTunnelName()+"_"+BfdTypeEnum.Tunnel.getName());
        bfdSDTunnel.setType(BfdTypeEnum.Tunnel.getCode());
        bfdSDTunnel.setMinSendTime(BfdMinIntervalEnum.TunnelMinTX.getName());
        bfdSDTunnel.setMinRecvTime(BfdMinIntervalEnum.TunnelMinRX.getName());
        bfdSDTunnel.setDiscriminatorLocal(bfdSDTunnelLocal);
        bfdSDTunnel.setDiscriminatorRemote(bfdDSTunnelLocal);
        tunnelSD.setTunnelBfd(bfdSDTunnel);

        bfdDSTunnel.setBfdName(tunnelDS.getTunnelName()+"_"+BfdTypeEnum.Tunnel.getName());
        bfdDSTunnel.setType(BfdTypeEnum.Tunnel.getCode());
        bfdDSTunnel.setMinSendTime(BfdMinIntervalEnum.TunnelMinTX.getName());
        bfdDSTunnel.setMinRecvTime(BfdMinIntervalEnum.TunnelMinRX.getName());
        bfdDSTunnel.setDiscriminatorLocal(bfdDSTunnelLocal);
        bfdDSTunnel.setDiscriminatorRemote(bfdSDTunnelLocal);
        tunnelDS.setTunnelBfd(bfdDSTunnel);

        bfdSDLSP.setBfdName(tunnelSD.getTunnelName()+"_"+BfdTypeEnum.Master.getName());
        bfdSDLSP.setType(BfdTypeEnum.Master.getCode());
        bfdSDLSP.setMinSendTime(BfdMinIntervalEnum.LSPMinTX.getName());
        bfdSDLSP.setMinRecvTime(BfdMinIntervalEnum.LSPMinRX.getName());
        bfdSDLSP.setDiscriminatorLocal(bfdSDLSPLocal);
        bfdSDLSP.setDiscriminatorRemote(bfdDSLSPLocal);
        tunnelSD.setMasterBfd(bfdSDLSP);

        bfdDSLSP.setBfdName(tunnelDS.getTunnelName()+"_"+BfdTypeEnum.Master.getName());
        bfdDSLSP.setType(BfdTypeEnum.Master.getCode());
        bfdDSLSP.setMinSendTime(BfdMinIntervalEnum.LSPMinTX.getName());
        bfdDSLSP.setMinRecvTime(BfdMinIntervalEnum.LSPMinRX.getName());
        bfdDSLSP.setDiscriminatorLocal(bfdDSLSPLocal);
        bfdDSLSP.setDiscriminatorRemote(bfdSDLSPLocal);
        tunnelDS.setMasterBfd(bfdDSLSP);

        return;
    }


    private TunnelPolicy getTunnelPolicy(String routerId,List<TunnelPolicy> tunnelPolicies){
        TunnelPolicy tunnelPolicy = null;
        for(TunnelPolicy tp : tunnelPolicies){
            if(tp.getRouterID().equals(routerId)){
                return tp;
            }
        }
        tunnelPolicy = new TunnelPolicy();
        tunnelPolicy.setRouterID(routerId);
        tunnelPolicy.setTnlPolicyType(TnlPolicyTypeEnum.TnlBinding.getCode());
        List<TpNexthop> tpNexthops = new ArrayList<TpNexthop>();
        tunnelPolicy.setTpNexthops(tpNexthops);

        tunnelPolicies.add(tunnelPolicy);

        return tunnelPolicy;
    }

    private TpNexthop getTpNextHopFromDest(String routerId, List<TpNexthop> tpNexthops){
        TpNexthop tpNexthop = null;
        for(TpNexthop tp : tpNexthops){
            if(tp.getNexthopIPaddr().equals(routerId)){
                return tp;
            }
        }
        tpNexthop = new TpNexthop();
        tpNexthop.setNexthopIPaddr(routerId);

        tpNexthops.add(tpNexthop);

        return tpNexthop;
    }

    private String getTunnelName(String routerId) {
        int index = 1;
        while (index <= this.TUNNELID_MAX) {
            String tunnelName = "Tunnel" + index;
            List<String> tunnelNameList = this.tunnelMapTemp.get(routerId);
            if (tunnelNameList == null) {
                tunnelNameList = new ArrayList<>();
                tunnelNameList.add(tunnelName);
                this.tunnelMapTemp.put(routerId,tunnelNameList);
                return tunnelName;
            }

            if (!(tunnelManager.checkTunnelNameAndId(routerId, tunnelName, index +"")) &&
                    !tunnelNameList.contains(tunnelName)) {
                tunnelNameList.add(tunnelName);
                return tunnelName;
            }
            index = index + 1;
        }
        return null;
    }

    private String getBfdDiscriminator(String routerId) {
        int index = 1;
        while (index < this.BFDID_MAX) {
            String tunnelName = "Tunnel" + index;
            List<Integer> bfdIdList = this.bfdDiscriminatorTemp.get(routerId);
            if (bfdIdList == null) {
                bfdIdList = new ArrayList<>();
                bfdIdList.add(new Integer(index));
                this.bfdDiscriminatorTemp.put(routerId,bfdIdList);
                return index + "";
            }
            if (!tunnelManager.isBfdDiscriminatorLocalUsed(index) &&
                    !bfdIdList.contains(new Integer(index))) {
                bfdIdList.add(new Integer(index));
                return index + "";
            }
            index = index + 1;
        }
        return null;
    }


    private void getVPNS(){
        Map<String,List<Tunnel>> tunnels = new HashMap<String, List<Tunnel>>();
        List<TunnelPolicy> tunnelPolicies = new ArrayList<TunnelPolicy>();
    }


}
