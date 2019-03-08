/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.VpnInstanceApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.entity.Address;
import cn.org.upbnc.entity.DeviceInterface;
import cn.org.upbnc.entity.NetworkSeg;
import cn.org.upbnc.entity.VPNInstance;
import cn.org.upbnc.enumtype.AddressTypeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.enumtype.VpnEnum.VpnAdvertiseCommunityEnum;
import cn.org.upbnc.enumtype.VpnEnum.VpnApplyLabelEnum;
import cn.org.upbnc.enumtype.VpnEnum.VpnFrrStatusEnum;
import cn.org.upbnc.enumtype.VpnEnum.VpnUseTemplateEnum;
import cn.org.upbnc.service.entity.UpdateVpnInstance;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.binddevices.DeviceBindBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.binddevices.devicebind.BindIfNetBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.ebgpinfo.Ebgp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.ebgpinfo.EbgpBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.updatebinddevices.DeviceBind;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.updatebinddevices.devicebind.BindIfNet;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.updatevpninstance.input.VpnInstance;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.vpninstanceinfo.BindInterface;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.vpninstanceinfo.BindInterfaceBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.vpninstanceinfo.NetSegment;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.vpninstanceinfo.NetSegmentBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.vpninstancelistinfo.VpnInstancesInfo;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.vpninstancelistinfo.VpnInstancesInfoBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.vpninstancelistretinfo.VpnInstances;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.vpninstancelistretinfo.VpnInstancesBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VpnInstanceODLApi implements UpsrVpnInstanceService {
    private static final Logger LOG = LoggerFactory.getLogger(VpnInstanceODLApi.class);
    Session session;
    VpnInstanceApi vpnInstanceApi;

    public VpnInstanceODLApi(Session session) {
        this.session = session;
    }

    private VpnInstanceApi getVpnInstanceApi() {
        if (this.vpnInstanceApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                vpnInstanceApi = apiInterface.getVpnInstanceApi();
            }
        }
        return this.vpnInstanceApi;
    }

    public Future<RpcResult<GetVpnInstancesOutput>> getVpnInstances(GetVpnInstancesInput input) {
        List<VPNInstance> vpnInstanceList = null;
        List<VpnInstancesInfo> retVpnInstanceList = new LinkedList<VpnInstancesInfo>();
        VpnInstancesInfoBuilder retVpnInstance = null;
        GetVpnInstancesOutputBuilder vpnInstanceOutputBuilder = new GetVpnInstancesOutputBuilder();

        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            vpnInstanceOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(vpnInstanceOutputBuilder.build()).buildFuture();
        } else {
            //调用系统Api层函数
            vpnInstanceList = (List<VPNInstance>) this.getVpnInstanceApi().getVpnInstanceList(input.getVpnName()).get(ResponseEnum.BODY.getName());
            if ((null != vpnInstanceList)) {
                vpnInstanceOutputBuilder.setResult("success");
                for (VPNInstance vpnInstance : vpnInstanceList) {
                    retVpnInstance = new VpnInstancesInfoBuilder();
                    retVpnInstance.setVpnName(vpnInstance.getVpnName());
                    retVpnInstance.setRouterId(vpnInstance.getRouterId());
                    retVpnInstance.setBussinessArea(vpnInstance.getBusinessRegion());
                    retVpnInstance.setVpnRd(vpnInstance.getRd());
                    retVpnInstance.setVpnRt(vpnInstance.getImportRT());
                    retVpnInstance.setPeerAS(vpnInstance.getPeerAS());
                    retVpnInstance.setPeerIP(vpnInstance.getPeerIP().getAddress());
                    retVpnInstance.setRouteSelectDelay(vpnInstance.getRouteSelectDelay());
                    retVpnInstance.setImportDirectRouteEnable(vpnInstance.getRouteSelectDelay());
                    List<BindInterface> deviceBindInterfaces = new LinkedList<BindInterface>();
                    List<NetSegment> netSegments = new LinkedList<NetSegment>();
                    if ((null != vpnInstance.getDeviceInterfaceList()) && (0 != vpnInstance.getDeviceInterfaceList().size())) {
                        for (DeviceInterface deviceInterface : vpnInstance.getDeviceInterfaceList()) {
                            BindInterfaceBuilder bindInterfaceBuilder = new BindInterfaceBuilder();
                            bindInterfaceBuilder.setIfName(deviceInterface.getName());
                            bindInterfaceBuilder.setIfAddress(deviceInterface.getIp().getAddress());
                            bindInterfaceBuilder.setIfNetmask(deviceInterface.getMask().getAddress());
                            deviceBindInterfaces.add(bindInterfaceBuilder.build());
                        }
                    }
                    if ((null != vpnInstance.getNetworkSegList()) && (0 != vpnInstance.getNetworkSegList().size())) {
                        for (NetworkSeg networkSeg : vpnInstance.getNetworkSegList()) {
                            NetSegmentBuilder netSegmentBuilder = new NetSegmentBuilder();
                            netSegmentBuilder.setAddress(networkSeg.getAddress().getAddress());
                            netSegmentBuilder.setMask(networkSeg.getMask().getAddress());
                            netSegments.add(netSegmentBuilder.build());
                        }
                    }
                    retVpnInstance.setBindInterface(deviceBindInterfaces);
                    retVpnInstance.setNetSegment(netSegments);
                    retVpnInstanceList.add(retVpnInstance.build());
                }
                vpnInstanceOutputBuilder.setVpnInstancesInfo(retVpnInstanceList);
                return RpcResultBuilder.success(vpnInstanceOutputBuilder.build()).buildFuture();
            }
        }
        //LOG.info("enter vpnInstance#s##");
        //以下是业务代码
        vpnInstanceOutputBuilder.setResult("failed");
        return RpcResultBuilder.success(vpnInstanceOutputBuilder.build()).buildFuture();
    }


    public Future<RpcResult<GetVpnInstanceOutput>> getVpnInstance(GetVpnInstanceInput input) {
        VPNInstance vpnInstance = null;
        List<BindInterface> deviceBindInterfaces = new LinkedList<BindInterface>();
        List<NetSegment> netSegments = new LinkedList<NetSegment>();
        GetVpnInstanceOutputBuilder vpnInstanceGetOutputBuilder = new GetVpnInstanceOutputBuilder();

        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            vpnInstanceGetOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(vpnInstanceGetOutputBuilder.build()).buildFuture();
        } else {
            LOG.info("enter vpnInstanceGet");
            //调用系统Api层函数
            vpnInstance = (VPNInstance) this.getVpnInstanceApi().getVpnInstance(input.getRouterId(), input.getVpnName()).get(ResponseEnum.BODY.getName());
            if (null != vpnInstance) {
                vpnInstanceGetOutputBuilder.setResult("success");
                vpnInstanceGetOutputBuilder.setVpnName(vpnInstance.getVpnName());
                vpnInstanceGetOutputBuilder.setRouterId(vpnInstance.getRouterId());
                vpnInstanceGetOutputBuilder.setBussinessArea(vpnInstance.getBusinessRegion());
                vpnInstanceGetOutputBuilder.setVpnRd(vpnInstance.getRd());
                vpnInstanceGetOutputBuilder.setVpnRt(vpnInstance.getImportRT());
                vpnInstanceGetOutputBuilder.setPeerAS(vpnInstance.getPeerAS());
                vpnInstanceGetOutputBuilder.setRouteSelectDelay(vpnInstance.getRouteSelectDelay());
                vpnInstanceGetOutputBuilder.setImportDirectRouteEnable(vpnInstance.getRouteSelectDelay());
                if (null != vpnInstance.getDeviceInterfaceList()) {
                    for (DeviceInterface deviceInterface : vpnInstance.getDeviceInterfaceList()) {
                        BindInterfaceBuilder bindInterfaceBuilder = new BindInterfaceBuilder();
                        bindInterfaceBuilder.setIfName(deviceInterface.getName());
                        bindInterfaceBuilder.setIfAddress(deviceInterface.getIp().getAddress());
                        bindInterfaceBuilder.setIfNetmask(deviceInterface.getMask().getAddress());
                        deviceBindInterfaces.add(bindInterfaceBuilder.build());
                    }
                }
                if (null != vpnInstance.getNetworkSegList()) {
                    for (NetworkSeg networkSeg : vpnInstance.getNetworkSegList()) {
                        NetSegmentBuilder netSegmentBuilder = new NetSegmentBuilder();
                        netSegmentBuilder.setAddress(networkSeg.getAddress().getAddress());
                        netSegmentBuilder.setMask(networkSeg.getMask().getAddress());
                        netSegments.add(netSegmentBuilder.build());
                    }
                }
                vpnInstanceGetOutputBuilder.setBindInterface(deviceBindInterfaces);
                vpnInstanceGetOutputBuilder.setNetSegment(netSegments);
                return RpcResultBuilder.success(vpnInstanceGetOutputBuilder.build()).buildFuture();
            }
        }

        //以下是业务代码


        vpnInstanceGetOutputBuilder.setResult("failed");
        return RpcResultBuilder.success(vpnInstanceGetOutputBuilder.build()).buildFuture();
    }

    public Future<RpcResult<DelVpnInstanceOutput>> delVpnInstance(DelVpnInstanceInput input) {
        boolean ret = false;
        DelVpnInstanceOutputBuilder vpnInstanceDelOutputBuilder = new DelVpnInstanceOutputBuilder();

        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            vpnInstanceDelOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(vpnInstanceDelOutputBuilder.build()).buildFuture();
        } else {
            LOG.info("enter vpnInstanceDel-02");
            //调用系统Api层函数
            ret = (boolean) this.getVpnInstanceApi().delVpnInstance(input.getRouterId(), input.getVpnName()).get(ResponseEnum.BODY.getName());
            if (true == ret) {
                vpnInstanceDelOutputBuilder.setResult("success");
                return RpcResultBuilder.success(vpnInstanceDelOutputBuilder.build()).buildFuture();
            }

        }
        //以下是业务代码
        vpnInstanceDelOutputBuilder.setResult("failed");

        return RpcResultBuilder.success(vpnInstanceDelOutputBuilder.build()).buildFuture();
    }

    private boolean checkRTD(String rtd) {
        boolean result = false;
        Pattern pattern = Pattern.compile
                ("((((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))|^[1-9][0-9]*):[1-9][0-9]*$");
        Matcher matcher = pattern.matcher(rtd);
        if (matcher.matches()) {
            result = true;
        }
        return result;
    }

    public Future<RpcResult<UpdateVpnInstanceOutput>> updateVpnInstance(UpdateVpnInstanceInput input) {
        boolean ret = false;

        UpdateVpnInstanceOutputBuilder vpnInstanceUpdateOutputBuilder = new UpdateVpnInstanceOutputBuilder();
        LOG.info("enter vpnInstanceUpdate");
        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            vpnInstanceUpdateOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(vpnInstanceUpdateOutputBuilder.build()).buildFuture();
        } else {
            VpnInstance vpnInstance_input = input.getVpnInstance();
            if (null == vpnInstance_input) {
                vpnInstanceUpdateOutputBuilder.setResult("failed");
                return RpcResultBuilder.success(vpnInstanceUpdateOutputBuilder.build()).buildFuture();
            }
            String message = "";
            String vpnName = vpnInstance_input.getVpnName();
            String vpnRT = vpnInstance_input.getVpnRt();
            String businessArea = vpnInstance_input.getBusinessArea();
            String notes = vpnInstance_input.getNotes();
            List<DeviceBind> bindDeviceList = vpnInstance_input.getDeviceBind();
            List<String> routerIdList = new ArrayList<>();
            if (null != bindDeviceList) {
                for (DeviceBind bindDevice : bindDeviceList) {
                    routerIdList.add(bindDevice.getRouterId());
                }
                if (bindDeviceList.size() > 0) {
                    Map<String, List<VPNInstance>> vpnInstanceMap = (Map<String, List<VPNInstance>>) this.getVpnInstanceApi().
                            getVpnInstanceMap(vpnName).get(ResponseEnum.BODY.getName());
                    if (vpnInstanceMap.containsKey(vpnName)) {
                        for (VPNInstance vpnInstance : vpnInstanceMap.get(vpnName)) {
                            if (!routerIdList.contains(vpnInstance.getRouterId())) {
                                ret = (boolean) this.getVpnInstanceApi().delVpnInstance(vpnInstance.getRouterId(), vpnName).get(ResponseEnum.BODY.getName());
                                LOG.info("delete vpn ret :" + ret);
                            }
                        }
                    }

                    boolean flag = false;
                    String errorMsg = "";
                    for (DeviceBind bindDevice : bindDeviceList) {
                        String peerAS = bindDevice.getEbgp().getPeerAS();
                        String peerIp = bindDevice.getEbgp().getPeerIP();
                        String Rd = bindDevice.getVpnRd();
                        String importRoutePolicy = bindDevice.getEbgp().getRouterImportPolicy();
                        String exportRoutePolicy = bindDevice.getEbgp().getRouterExportPolicy();
                        if ("".equals(peerAS)) {
                            peerAS = null;
                        }
                        if (null == peerIp) {
                            peerAS = "";
                        }
                        if (("".equals(peerIp) && (null != peerAS)) ||
                                ((!("".equals(peerIp))) && null == peerAS)) {
                            errorMsg += "Configure " + bindDevice.getRouterId() + "failed: peerIp or peerAS is null.";
                            flag = true;
                            LOG.info("bindDevice.getEbgp().getPeerIP() :" + bindDevice.getEbgp().getPeerIP());
                            LOG.info("bindDevice.getEbgp().getPeerAS() :" + bindDevice.getEbgp().getPeerAS());
                        }
                        if (!checkRTD(Rd)) {
                            errorMsg += "Configure " + bindDevice.getRouterId() + "failed: RD format is not right .the right example is 100:100 or 192.168.1.1:888.";
                            flag = true;
                        }
                        if ("".equals(importRoutePolicy)) {
                            importRoutePolicy = null;
                        }
                        if ("".equals(exportRoutePolicy)) {
                            exportRoutePolicy = null;
                        }
                        if ("".equals(peerIp) && (null != importRoutePolicy || null != exportRoutePolicy)) {
                            errorMsg += "Configure " + bindDevice.getRouterId() + "failed: peerIp is empty when config import or export route policy.";
                            flag = true;
                        }
                    }
                    if (!checkRTD(vpnRT)) {
                        errorMsg += "Configure failed: RT format is not right .the right example is 100:100 or 192.168.1.1:888.";
                        flag = true;
                    }
                    if (flag) {
                        vpnInstanceUpdateOutputBuilder.setResult("failed");
                        vpnInstanceUpdateOutputBuilder.setMessage(errorMsg);
                        return RpcResultBuilder.success(vpnInstanceUpdateOutputBuilder.build()).buildFuture();
                    }

                    for (DeviceBind bindDevice : bindDeviceList) {
                        List<DeviceInterface> deviceInterfaceList = new LinkedList<DeviceInterface>();
                        List<NetworkSeg> networkSegList = new LinkedList<NetworkSeg>();

                        String routerId = bindDevice.getRouterId();
                        String vpnRd = bindDevice.getVpnRd();
                        Ebgp ebgp = bindDevice.getEbgp();
                        String peerIP = null;
                        Integer peerAs = null;
                        Integer importDirect = null;
                        String network = null;
                        String mask = null;
                        Address peerIP_Address = null;
                        String routerImportPolicy = null;
                        String routerExportPolicy = null;
                        String ebgpPreference = null;
                        String ibgpPreference = null;
                        String localPreference = null;
                        String advertiseCommunity = null;

                        if (null != ebgp) {
                            peerIP = ebgp.getPeerIP();
                            if ((null != ebgp.getPeerAS()) && (true != ebgp.getPeerAS().equals(""))) {
                                peerAs = Integer.parseInt(ebgp.getPeerAS());
                            }
                            importDirect = ebgp.getImportDirect();
                            network = ebgp.getNetwork();
                            mask = ebgp.getMask();
                            if ((null != network) && (null != mask)) {
                                NetworkSeg networkSeg = new NetworkSeg(new Address(network, AddressTypeEnum.V4), new Address(mask, AddressTypeEnum.V4));
                                networkSegList.add(networkSeg);
                            }
                            routerImportPolicy = ebgp.getRouterImportPolicy();
                            routerExportPolicy = ebgp.getRouterExportPolicy();
                            ebgpPreference = ebgp.getEbgpPreference();
                            ibgpPreference = ebgp.getIbgpPreference();
                            localPreference = ebgp.getLocalPreference();
                            advertiseCommunity = ebgp.getAdvertiseCommunity();
                        }
                        if (null != peerIP) {
                            peerIP_Address = new Address(peerIP, AddressTypeEnum.V4);
                        }
                        List<BindIfNet> updateBindInterfaceList = bindDevice.getBindIfNet();
                        for (BindIfNet bindInterface : updateBindInterfaceList) {
                            String ifName = bindInterface.getIfName();
                            String ifnetIp = bindInterface.getIfAddress();
                            String ifNetmask = bindInterface.getIfNetMask();
                            DeviceInterface deviceInterface = new DeviceInterface();
                            deviceInterface.setName(ifName);
                            if ((null != ifnetIp) && (true != "".equals(ifnetIp))) {
                                deviceInterface.setIp(new Address(ifnetIp, AddressTypeEnum.V4));
                            }
                            if ((null != ifNetmask) && (true != "".equals(ifNetmask))) {
                                deviceInterface.setMask(new Address(ifNetmask, AddressTypeEnum.V4));
                            }
                            deviceInterfaceList.add(deviceInterface);
                        }
                        //调用系统Api层函数
                        UpdateVpnInstance updateVpnInstance = new UpdateVpnInstance(vpnName, routerId, businessArea,
                                vpnRd, vpnRT, vpnRT, peerAs, peerIP_Address, null, importDirect,
                                deviceInterfaceList, networkSegList, bindDevice.getTunnelPolicy(), bindDevice.getVpnFrr(), bindDevice.getApplyLabel(),
                                bindDevice.getTtlMode(), routerImportPolicy, routerExportPolicy, ebgpPreference, ibgpPreference,
                                localPreference, advertiseCommunity, bindDevice.getNotes());
                        Map<String, Object> retMap = this.getVpnInstanceApi().updateVpnInstance(updateVpnInstance);
                        ret = (boolean) retMap.get(ResponseEnum.BODY.getName());
                        LOG.info("enter vpnInstanceUpdate ret={}", new Object[]{ret});
                        if (true == ret) {
                            message += "routerId " + routerId + "is success!\\n";
                            vpnInstanceUpdateOutputBuilder.setResult("success");
                        } else {
                            message += "Configure " + routerId + "failed: " + retMap.get(ResponseEnum.MESSAGE.getName());
                            vpnInstanceUpdateOutputBuilder.setResult("failed");
                        }
                    }
                    //vpnInstanceUpdateOutputBuilder.setResult("success");
                    vpnInstanceUpdateOutputBuilder.setMessage(message);
                    if (VpnUseTemplateEnum.ENABLE.getName().equals(vpnInstance_input.getUseTemplate())) {
                        vpnInstanceApi.createTunnelsByVpnTemplate(vpnInstance_input.getVpnName());
                    }
                    return RpcResultBuilder.success(vpnInstanceUpdateOutputBuilder.build()).buildFuture();
                } else {
                    vpnInstanceUpdateOutputBuilder.setMessage("please select devices for vpn( " + vpnName + " ).");
                    vpnInstanceUpdateOutputBuilder.setResult("failed.");
                }
            } else {
                ret = (boolean) this.getVpnInstanceApi().delVpnInstance("", vpnName).get(ResponseEnum.BODY.getName());
                if (true == ret) {
                    vpnInstanceUpdateOutputBuilder.setResult("success");
                    return RpcResultBuilder.success(vpnInstanceUpdateOutputBuilder.build()).buildFuture();
                }
            }
        }
//        vpnInstanceUpdateOutputBuilder.setResult("failed");
        return RpcResultBuilder.success(vpnInstanceUpdateOutputBuilder.build()).buildFuture();
    }

    public Future<RpcResult<GetVpnInstanceMapOutput>> getVpnInstanceMap(GetVpnInstanceMapInput input) {
        Map<String, List<VPNInstance>> vpnInstanceMap = null;
        List<VPNInstance> vpnInstanceList = null;
        List<VpnInstances> retVpnInstanceList = new LinkedList<VpnInstances>();
        VpnInstancesBuilder vpnInstancesRetBuilder = null;
        GetVpnInstanceMapOutputBuilder getVpnInstanceListOutputBuilder = new GetVpnInstanceMapOutputBuilder();
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            getVpnInstanceListOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(getVpnInstanceListOutputBuilder.build()).buildFuture();
        } else {
            //logic
            //调用系统Api层函数
            vpnInstanceMap = (Map<String, List<VPNInstance>>) this.getVpnInstanceApi().getVpnInstanceMap(input.getVpnName()).get(ResponseEnum.BODY.getName());
            if (null != vpnInstanceMap) {
                getVpnInstanceListOutputBuilder.setResult("success");
                // 遍历map，根据vpnname 输出每个vpn信息
                //Iterator<String, List<VPNInstance>> iter = vpnInstanceMap.entrySet().iterator();
                for (String vpnName_str : vpnInstanceMap.keySet()) {
                    //遍历每个vpnName map下的vpnInstanceList,输出list信息
                    vpnInstanceList = vpnInstanceMap.get(vpnName_str);
                    vpnInstancesRetBuilder = new VpnInstancesBuilder();
                    vpnInstancesRetBuilder.setVpnName(vpnName_str);
                    DeviceBindBuilder bindDevice = null;
                    List<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.
                            binddevices.DeviceBind> bindDevices = new LinkedList<>();
                    if (null != vpnInstanceList) {
                        VPNInstance vpnInstance_front = vpnInstanceList.get(0);
                        vpnInstancesRetBuilder.setBusinessArea(vpnInstance_front.getBusinessRegion());
                        vpnInstancesRetBuilder.setNotes(vpnInstance_front.getNote());
                        vpnInstancesRetBuilder.setVpnRt(vpnInstance_front.getExportRT());
                        for (VPNInstance vpnInstance : vpnInstanceList) {
                            bindDevice = new DeviceBindBuilder();
                            if (null != vpnInstance.getDevice()) {
                                bindDevice.setDeviceName(vpnInstance.getDevice().getDeviceName());
                                bindDevice.setRouterId(vpnInstance.getDevice().getRouterId());
                            }
                            bindDevice.setVpnExport(vpnInstance.getExportRT());
                            bindDevice.setVpnImport(vpnInstance.getImportRT());
                            bindDevice.setVpnRd(vpnInstance.getRd());
                            bindDevice.setApplyLabel(VpnApplyLabelEnum.netconf2cmd(vpnInstance.getApplyLabel()));
                            bindDevice.setTunnelPolicy(vpnInstance.getImportTunnelPolicyName());
                            bindDevice.setTtlMode(vpnInstance.getTtlMode());
                            bindDevice.setVpnFrr("1".equals(vpnInstance.getVpnFrr()) ?
                                    VpnFrrStatusEnum.ENABLED.getName() : VpnFrrStatusEnum.DISENABLED.getName());
                            bindDevice.setNotes(vpnInstance.getNote());
                            if (null != vpnInstance.getDeviceInterfaceList()) {
                                List<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.
                                        rev181119.binddevices.devicebind.BindIfNet> bindIfs = new LinkedList<>();
                                for (DeviceInterface deviceInterface : vpnInstance.getDeviceInterfaceList()) {
                                    BindIfNetBuilder bindIf = new BindIfNetBuilder();
                                    bindIf.setIfName(deviceInterface.getName());
                                    if (null != deviceInterface.getIp()) {
                                        bindIf.setIfAddress(deviceInterface.getIp().getAddress());
                                    }
                                    if (null != deviceInterface.getMask()) {
                                        bindIf.setIfNetMask(deviceInterface.getMask().getAddress());
                                    }
                                    bindIfs.add(bindIf.build());
                                }
                                if (0 != bindIfs.size()) {
                                    bindDevice.setBindIfNet(bindIfs);
                                }
                            }
                            EbgpBuilder eBgp = new EbgpBuilder();
                            eBgp.setNetwork("");
                            if ((null != vpnInstance.getNetworkSegList()) && (0 != vpnInstance.getNetworkSegList().size())) {
                                NetworkSeg eBgp_network_seg_front = vpnInstance.getNetworkSegList().get(0);
                                eBgp.setImportDirect(vpnInstance.getImportDirectRouteEnable());
                                if (null != eBgp_network_seg_front) {
                                    if (null != eBgp_network_seg_front.getAddress()) {
                                        eBgp.setNetwork(eBgp_network_seg_front.getAddress().getAddress());
                                    }

                                    if (null != eBgp_network_seg_front.getMask()) {
                                        eBgp.setMask(eBgp_network_seg_front.getMask().getAddress());
                                    }

                                }

                            }
                            if (null != vpnInstance.getPeerAS()) {
                                eBgp.setPeerAS(vpnInstance.getPeerAS().toString());
                            }

                            if (null != vpnInstance.getPeerIP()) {
                                eBgp.setPeerIP(vpnInstance.getPeerIP().getAddress());
                            }
                            eBgp.setRouterImportPolicy(vpnInstance.getImportRoutePolicyName());
                            eBgp.setRouterExportPolicy(vpnInstance.getExportRoutePolicyName());
                            eBgp.setEbgpPreference(vpnInstance.getEbgpPreference());
                            eBgp.setIbgpPreference(vpnInstance.getIbgpPreference());
                            eBgp.setLocalPreference(vpnInstance.getLocalPreference());
                            eBgp.setAdvertiseCommunity("1".equals(vpnInstance.getAdvertiseCommunity()) ?
                                    VpnAdvertiseCommunityEnum.ENABLED.getName() : VpnAdvertiseCommunityEnum.DISENABLED.getName());
                            bindDevice.setEbgp(eBgp.build());
                            bindDevices.add(bindDevice.build());
                        }
                        vpnInstancesRetBuilder.setDeviceBind(bindDevices);
                    }
                    retVpnInstanceList.add(vpnInstancesRetBuilder.build());
                }
                getVpnInstanceListOutputBuilder.setVpnInstances(retVpnInstanceList);
                return RpcResultBuilder.success(getVpnInstanceListOutputBuilder.build()).buildFuture();
            }
        }
        getVpnInstanceListOutputBuilder.setResult("failed");
        return RpcResultBuilder.success(getVpnInstanceListOutputBuilder.build()).buildFuture();
    }

    public Future<RpcResult<IsContainVpnNameOutput>> isContainVpnName(IsContainVpnNameInput input) {
        IsContainVpnNameOutputBuilder isContainVpnNameOutputBuilder = new IsContainVpnNameOutputBuilder();
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            isContainVpnNameOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(isContainVpnNameOutputBuilder.build()).buildFuture();
        } else {
            //logic
            //调用系统Api层函数
            if (null != this.getVpnInstanceApi()) {
                isContainVpnNameOutputBuilder.setResult("success");
                if (null == input.getVpnName() || "".equals(input.getVpnName())) {
                    isContainVpnNameOutputBuilder.setIsContainVpnName(true);
                } else {
                    isContainVpnNameOutputBuilder.setIsContainVpnName((boolean) this.vpnInstanceApi.isContainVpnName(input.getVpnName()).get(ResponseEnum.BODY.getName()));
                }
            } else {
                isContainVpnNameOutputBuilder.setResult("failed");
            }
            return RpcResultBuilder.success(isContainVpnNameOutputBuilder.build()).buildFuture();
        }
    }

    public Future<RpcResult<IsContainRdOutput>> isContainRd(IsContainRdInput input) {
        IsContainRdOutputBuilder isContainRdOutputBuilder = new IsContainRdOutputBuilder();
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            isContainRdOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(isContainRdOutputBuilder.build()).buildFuture();
        } else {
            //logic
            //调用系统Api层函数
            if (null != this.getVpnInstanceApi()) {
                isContainRdOutputBuilder.setResult("success");
                isContainRdOutputBuilder.setIsContainRd((boolean) this.vpnInstanceApi.isContainRd(input.getRouterId(), input.getVpnRd()).get(ResponseEnum.BODY.getName()));
            } else {
                isContainRdOutputBuilder.setResult("failed");
            }
            return RpcResultBuilder.success(isContainRdOutputBuilder.build()).buildFuture();
        }
    }
}
