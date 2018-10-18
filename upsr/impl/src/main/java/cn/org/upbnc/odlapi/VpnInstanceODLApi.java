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
import cn.org.upbnc.enumtype.SystemStatusEnum;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.binddevices.Bind;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.binddevices.BindBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.ebgpinfo.Ebgp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.ebgpinfo.EbgpBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.vpninstanceinfo.BindInterface;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.vpninstanceinfo.BindInterfaceBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.vpninstanceinfo.NetSegment;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.vpninstanceinfo.NetSegmentBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.vpninstancelistinfo.VpnInstancesInfo;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.vpninstancelistinfo.VpnInstancesInfoBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.vpninstancelistretinfo.VpnInstances;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.vpninstancelistretinfo.VpnInstancesBuilder;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import org.opendaylight.yangtools.yang.binding.DataContainer;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class VpnInstanceODLApi implements  UpsrVpnInstanceService {
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
            vpnInstanceList = this.getVpnInstanceApi().getVpnInstanceList(input.getVpnName());
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
        LOG.info("enter vpnInstanceGet-01");
        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            vpnInstanceGetOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(vpnInstanceGetOutputBuilder.build()).buildFuture();
        } else {
            LOG.info("enter vpnInstanceGet-02");
            //调用系统Api层函数
            vpnInstance = this.getVpnInstanceApi().getVpnInstance(input.getRouterId(), input.getVpnName());
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

        LOG.info("enter vpnInstanceGet-03");
        vpnInstanceGetOutputBuilder.setResult("failed");
        return RpcResultBuilder.success(vpnInstanceGetOutputBuilder.build()).buildFuture();
    }

    public Future<RpcResult<DelVpnInstanceOutput>> delVpnInstance(DelVpnInstanceInput input) {
        boolean ret = false;
        DelVpnInstanceOutputBuilder vpnInstanceDelOutputBuilder = new DelVpnInstanceOutputBuilder();
        LOG.info("enter vpnInstanceDel-01");
        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            vpnInstanceDelOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(vpnInstanceDelOutputBuilder.build()).buildFuture();
        } else {
            LOG.info("enter vpnInstanceDel-02");
            //调用系统Api层函数
            ret = this.getVpnInstanceApi().delVpnInstance(input.getRouterId(), input.getVpnName());
            if (true == ret) {
                vpnInstanceDelOutputBuilder.setResult("success");
                return RpcResultBuilder.success(vpnInstanceDelOutputBuilder.build()).buildFuture();
            }

        }
        LOG.info("enter vpnInstanceDel-03");
        //以下是业务代码
        vpnInstanceDelOutputBuilder.setResult("failed");

        return RpcResultBuilder.success(vpnInstanceDelOutputBuilder.build()).buildFuture();
    }

    public Future<RpcResult<UpdateVpnInstanceOutput>> updateVpnInstance(UpdateVpnInstanceInput input) {
        boolean ret = false;
        List<DeviceInterface> deviceInterfaceList = new LinkedList<DeviceInterface>();
        List<NetworkSeg> networkSegList = new LinkedList<NetworkSeg>();
        UpdateVpnInstanceOutputBuilder vpnInstanceUpdateOutputBuilder = new UpdateVpnInstanceOutputBuilder();
        LOG.info("enter vpnInstanceUpdate");
        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            vpnInstanceUpdateOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(vpnInstanceUpdateOutputBuilder.build()).buildFuture();
        } else {
            LOG.info("enter vpnInstanceUpdate vpnName={} rd={}", new Object[]{input.getVpnName(), input.getRouterId()});
            List<BindInterface> deviceInterfaces = input.getBindInterface();
            List<NetSegment> netSegments = input.getNetSegment();
            if (null != deviceInterfaces) {
                for (BindInterface deviceInterface : deviceInterfaces) {
                    deviceInterfaceList.add(new DeviceInterface(deviceInterface.getIfName(), new Address(deviceInterface.getIfAddress(), AddressTypeEnum.V4), new Address(deviceInterface.getIfNetmask(), AddressTypeEnum.V4)));
                }
            }
            if (null != netSegments) {
                for (NetSegment netSegment : netSegments) {
                    networkSegList.add(new NetworkSeg(new Address(netSegment.getAddress(), AddressTypeEnum.V4), new Address(netSegment.getMask(), AddressTypeEnum.V4)));
                }
            }
            //调用系统Api层函数
            ret = this.getVpnInstanceApi().updateVpnInstance(input.getVpnName(),
                    input.getRouterId(),
                    input.getBussinessArea(),
                    input.getVpnRd(),
                    input.getVpnRt(),
                    input.getVpnRt(),
                    input.getPeerAS(),
                    new Address(input.getPeerIP(), AddressTypeEnum.V4),
                    input.getRouteSelectDelay(),
                    input.getImportDirectRouteEnable(),
                    deviceInterfaceList,
                    networkSegList
            );
            LOG.info("enter vpnInstanceUpdate ret={}", new Object[]{ret});
            if (true == ret) {
                vpnInstanceUpdateOutputBuilder.setResult("success");
                return RpcResultBuilder.success(vpnInstanceUpdateOutputBuilder.build()).buildFuture();
            }
        }
        LOG.info("enter vpnInstanceUpdate-03");
        //以下是业务代码
        vpnInstanceUpdateOutputBuilder.setResult("failed");

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
            vpnInstanceMap = this.getVpnInstanceApi().getVpnInstanceMap(input.getVpnName());
            if ((null != vpnInstanceMap) && (0 != vpnInstanceMap.size())) {
                getVpnInstanceListOutputBuilder.setResult("success");
                // 遍历map，根据vpnname 输出每个vpn信息
                //Iterator<String, List<VPNInstance>> iter = vpnInstanceMap.entrySet().iterator();
                for(String vpnName_str:vpnInstanceMap.keySet()) {
                    //遍历每个vpnName map下的vpnInstanceList,输出list信息
                    vpnInstanceList = vpnInstanceMap.get(vpnName_str);
                    vpnInstancesRetBuilder = new VpnInstancesBuilder();
                    //vpnInstancesRetBuilder.setVpnName(vpnInstancemap.getKey().toString());
                    vpnInstancesRetBuilder.setVpnName(vpnName_str);
                    BindBuilder bindDevice = null;
                    List<Bind> bindDevices = new LinkedList<Bind>();
                    //vpnInstanceList = (List<VPNInstance>) vpnInstancemap.getValue();
                    if(null != vpnInstanceList) {
                        VPNInstance vpnInstance_front = vpnInstanceList.get(0);
                        vpnInstancesRetBuilder.setBusinessArea(vpnInstance_front.getBusinessRegion());
                        vpnInstancesRetBuilder.setNotes("VPN隔离");
                        vpnInstancesRetBuilder.setVpnRt(vpnInstance_front.getExportRT());
                        for (VPNInstance vpnInstance:vpnInstanceList) {
                            bindDevice = new BindBuilder();
                            if(null != vpnInstance.getDevice()) {
                                bindDevice.setDeviceName(vpnInstance.getDevice().getDeviceName());
                                bindDevice.setRouterId(vpnInstance.getDevice().getRouterId());
                            }
                            bindDevice.setVpnExport(vpnInstance.getExportRT());
                            bindDevice.setVpnImport(vpnInstance.getImportRT());
                            bindDevice.setVpnRd(vpnInstance.getRd());
                            if(null != vpnInstance.getDeviceInterfaceList())
                            {
                                List<String>  ifnetNames = new LinkedList<String>();
                                for (DeviceInterface deviceInterface:vpnInstance.getDeviceInterfaceList()) {
                                    ifnetNames.add(deviceInterface.getName());
                                }
                                if(0 != ifnetNames.size())
                                {
                                    bindDevice.setIfName(ifnetNames);
                                }
                            }
                            if((null != vpnInstance.getNetworkSegList())&&(0 != vpnInstance.getNetworkSegList().size()))
                            {
                                EbgpBuilder eBgp = new EbgpBuilder();
                                NetworkSeg eBgp_network_seg_front = vpnInstance.getNetworkSegList().get(0);
                                eBgp.setImportDirect(vpnInstance.getImportDirectRouteEnable());
                                if(null != eBgp_network_seg_front) {
                                    eBgp.setNetwork(eBgp_network_seg_front.getAddress().getAddress());
                                    eBgp.setMask(eBgp_network_seg_front.getMask().getAddress());
                                }
                                eBgp.setPeerAS(vpnInstance.getPeerAS());
                                if(null != vpnInstance.getPeerIP()) {
                                    eBgp.setPeerIP(vpnInstance.getPeerIP().getAddress());
                                }
                                bindDevice.setEbgp(eBgp.build());
                            }
                            bindDevices.add(bindDevice.build());
                        }
                        vpnInstancesRetBuilder.setBind(bindDevices);
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
            if(null!=this.vpnInstanceApi){
                isContainVpnNameOutputBuilder.setResult("success");
                isContainVpnNameOutputBuilder.setIsContainVpnName(this.vpnInstanceApi.isContainVpnName(input.getVpnName()));
            }else{
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
            if(null!=this.vpnInstanceApi){
                isContainRdOutputBuilder.setResult("success");
                isContainRdOutputBuilder.setIsContainRd(this.vpnInstanceApi.isContainRd(input.getRouterId(),input.getVpnRd()));
            }else{
                isContainRdOutputBuilder.setResult("failed");
            }
            return RpcResultBuilder.success(isContainRdOutputBuilder.build()).buildFuture();
        }
    }
}
