/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.LinkManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.enumtype.AddressTypeEnum;
import cn.org.upbnc.enumtype.NetConfStatusEnum;
import cn.org.upbnc.enumtype.SrStatus;
import cn.org.upbnc.service.SrLabelService;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.NetconfSrLabelInfo;
import cn.org.upbnc.util.xml.CheckXml;
import cn.org.upbnc.util.xml.SrLabelXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class SrLabelServiceImpl implements SrLabelService {
    private static final Logger LOG = LoggerFactory.getLogger(SrLabelServiceImpl.class);
    private static SrLabelService ourInstance = null;
    private BaseInterface baseInterface;
    private NetConfManager netConfManager = null;
    private DeviceManager deviceManager = null;
    private LinkManager linkManager = null;

    public SrLabelServiceImpl() {
        this.baseInterface = null;
        this.netConfManager = null;
        this.deviceManager = null;
        this.linkManager = null;
    }

    public static SrLabelService getInstance() {
        if (ourInstance == null){
            ourInstance = new SrLabelServiceImpl();
        }
        return ourInstance;
    }
    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        if(null != baseInterface) {
            this.netConfManager = this.baseInterface.getNetConfManager();
            this.deviceManager = this.baseInterface.getDeviceManager();
            this.linkManager = this.baseInterface.getLinkManager();
        }
        return true;
    }

    @Override
    public String updateNodeLabel(String routerId, String labelVal, String action) {
        boolean isChanged = true;
        Device device = null;
        LOG.info("updateNodeLabel begin");
        device = deviceManager.getDevice(routerId);
        if ((device != null) && (device.getNodeLabel() != null) && device.getNodeLabel().getValue() == Integer.parseInt(labelVal)){
            LOG.info("node label is not changed: " + labelVal);
            isChanged = false;
        }
        if ((isChanged == false) && (action == SrLabelXml.ncOperationMerge)){
            LOG.info("do not need update");
            return null;
        }
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        String commandSetSrNodeLabelXml = SrLabelXml.setSrNodeLabelXml(device.getOspfProcess().getProcessId().toString(),
                device.getOspfProcess().getAreaId(),device.getOspfProcess().getIntfName(),"index",labelVal,action);
        LOG.info("command xml: " + commandSetSrNodeLabelXml);
        String outPutXml = netconfController.sendMessage(netconfClient,commandSetSrNodeLabelXml);
        if (CheckXml.checkOk(outPutXml).equals("ok")){
            if (action == SrLabelXml.ncOperationDelete){
                device.setNodeLabel(null);
                device.setSrStatus(SrStatus.DISENABLED.getName());
            } else {
                NodeLabel nodeLabel = device.getNodeLabel() == null? new NodeLabel():device.getNodeLabel();
                nodeLabel.setValue(Integer.parseInt(labelVal));
                device.setNodeLabel(nodeLabel);
                device.setSrStatus(SrStatus.ENABLED.getName());
            }
        }
        LOG.info("updateNodeLabel end");
        return null;
    }

    @Override
    public String updateNodeLabelRange(String routerId, String labelBegin, String labelEnd, String action) {
        boolean isChanged = true;
        Device device = null;
        LOG.info("updateNodeLabelRange begin :" + action);
        device = deviceManager.getDevice(routerId);
        if ((device != null) && (device.getMinNodeSID() != null) && (device.getMinNodeSID() == Integer.parseInt(labelBegin)) &&
                (device.getMaxNodeSID() != null) && (device.getMaxNodeSID() == Integer.parseInt(labelEnd))){
            LOG.info("node label range is not changed: " + labelBegin + " , " + labelEnd);
            isChanged = false;
        }
        if ((isChanged == false) && (action == SrLabelXml.ncOperationMerge)){
            LOG.info("do not need update range");
            return null;
        }

        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        if (device.getMinNodeSID() != null) {
            String commandDeleteSrNodeLabelRangeXml = SrLabelXml.setSrNodeLabelRangeXml(device.getOspfProcess().getProcessId().toString(),
                    device.getMinNodeSID().toString(), device.getMaxNodeSID().toString(), SrLabelXml.ncOperationDelete);
            LOG.info("command xml: " + commandDeleteSrNodeLabelRangeXml);
            String outPutdeleteXml = netconfController.sendMessage(netconfClient, commandDeleteSrNodeLabelRangeXml);
            LOG.info("outPutdeleteXml xml: " + outPutdeleteXml);
        }
        String commandCreateSrNodeLabelRangeXml = SrLabelXml.setSrNodeLabelRangeXml(device.getOspfProcess().getProcessId().toString(),
                labelBegin,labelEnd,SrLabelXml.ncOperationCreate);
        LOG.info("command xml: " + commandCreateSrNodeLabelRangeXml);
        String outPutcreateXml = netconfController.sendMessage(netconfClient,commandCreateSrNodeLabelRangeXml);
        if (CheckXml.checkOk(outPutcreateXml).equals("ok")){
            if (action == SrLabelXml.ncOperationDelete){
                device.setMinNodeSID(null);
                device.setMaxNodeSID(null);
            } else {
                device.setMinNodeSID(Integer.parseInt(labelBegin));
                device.setMaxNodeSID(Integer.parseInt(labelEnd));
            }
        }
        LOG.info("updateNodeLabelRange end");
        return null;
    }

    @Override
    public String syncNodeLabel(String routerId) {
        Device device = null;
        LOG.info("syncNodeLabel begin " + routerId);
        device = deviceManager.getDevice(routerId);
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        String commandGetSrNodeLabelXml = SrLabelXml.getSrNodeLabelXml();
        LOG.info("command xml: " + commandGetSrNodeLabelXml);
        String outPutXml = netconfController.sendMessage(netconfClient,commandGetSrNodeLabelXml);
        LOG.info("output xml: " + outPutXml);
        NetconfSrLabelInfo netconfSrLabelInfo = SrLabelXml.getSrNodeLabelFromgSrNodeLabelXml(outPutXml);
        NodeLabel nodeLabel = new NodeLabel();
        OspfProcess ospfProcess = new OspfProcess();
        ospfProcess.setAreaId(netconfSrLabelInfo.getOspfAreaId());
        ospfProcess.setProcessId(Integer.parseInt(netconfSrLabelInfo.getOspfProcessId()));
        ospfProcess.setIntfName(netconfSrLabelInfo.getPrefixIfName());
        nodeLabel.setValue(Integer.parseInt(netconfSrLabelInfo.getPrefixLabel()));
        device.setNodeLabel(nodeLabel);
        device.setOspfProcess(ospfProcess);
        device.setSrStatus(SrStatus.ENABLED.getName());

        String commandGetSrNodeLabelRangeXml = SrLabelXml.getSrNodeLabelRangeXml();
        LOG.info("command sid range xml: " + commandGetSrNodeLabelRangeXml);
        String outPutLabelRange = netconfController.sendMessage(netconfClient,commandGetSrNodeLabelRangeXml);
        LOG.info("command out sid range xml: " + outPutLabelRange);
        netconfSrLabelInfo = SrLabelXml.getSrNodeLabelRangeFromNodeLabelRangeXml(outPutLabelRange);
        device.setMinNodeSID(Integer.parseInt(netconfSrLabelInfo.getSrgbBegin()));
        device.setMaxNodeSID(Integer.parseInt(netconfSrLabelInfo.getSrgbEnd()));
        LOG.info("syncNodeLabel end " + routerId);
        return null;
    }

    public String syncNodeLabel() {
        LOG.info("syncNodeLabel begin");
        if(this.deviceManager != null ) {
            for (Device device : this.deviceManager.getDeviceList()){
                if ((device.getNetConf() == null) || (device.getNetConf().getStatus() != NetConfStatusEnum.Connected)){
                    continue;
                }
                this.syncNodeLabel(device.getRouterId());
            }
        }
        LOG.info("syncNodeLabel end");
        return null;
    }

    @Override
    public String updateIntfLabel(String routerId, String localAddress, String remoteAddress, String labelVal, String action) {
        String peerAddress = null;
        Device device = null;
        LOG.info("updateIntfLabel begin");
        device = deviceManager.getDevice(routerId);
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        DeviceInterface localDeviceInterface = device.getDeviceInterfaceByAddress(localAddress);
        if(remoteAddress == null) {
            DeviceInterface remoteDeviceInterface = linkManager.getPeerDeviceInterface(localDeviceInterface);
            if (remoteDeviceInterface == null) {
                return null;
            }
            peerAddress = remoteDeviceInterface.getIp().getAddress();
        }else{
            peerAddress = remoteAddress;
        }
        if ((localDeviceInterface != null) && (localDeviceInterface.getAdjLabel() != null) &&
                (localDeviceInterface.getAdjLabel().getValue() == Integer.parseInt(labelVal)) &&
                (action == SrLabelXml.ncOperationMerge)) {
            LOG.info("adjlabel is not changed" + localAddress + " -> " + remoteAddress + " : " + labelVal);
            return null;
        }
        String commandUpdateXml = SrLabelXml.setSrAdjLabelXml(action,localAddress,peerAddress,labelVal);
        LOG.info("command xml: " + commandUpdateXml);
        String outPutXml = netconfController.sendMessage(netconfClient,commandUpdateXml);
        LOG.info("output xml: " + outPutXml);
        if (CheckXml.checkOk(outPutXml).equals("ok")){
            if (action == SrLabelXml.ncOperationDelete){
                localDeviceInterface.setSrStatus(SrStatus.DISENABLED.getName());
                localDeviceInterface.setAdjLabel(null);
            } else {
                AdjLabel adjLabel = new AdjLabel();
                adjLabel.setAddressLocal(new Address(localAddress, AddressTypeEnum.V4));
                adjLabel.setAddressRemote(new Address(remoteAddress, AddressTypeEnum.V4));
                adjLabel.setValue(Integer.parseInt(labelVal));
                localDeviceInterface.setSrStatus(SrStatus.ENABLED.getName());
                localDeviceInterface.setAdjLabel(adjLabel);
            }
        }
        LOG.info("updateIntfLabel end");
        return null;
    }

    @Override
    public String syncIntfLabel() {
        LOG.info("syncIntfLael begin");
        if(this.deviceManager !=null ) {
            for (Device device:this.deviceManager.getDeviceList()) {
                if ((device.getNetConf() == null) || (device.getNetConf().getStatus() != NetConfStatusEnum.Connected)){
                    continue;
                }
                LOG.info("syncIntfLael routerId : " + device.getRouterId());
                NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
                String commandGetSrAdjLabelRangeXml = SrLabelXml.getSrAdjLabelRangeXml();
                LOG.info("command xml: " + commandGetSrAdjLabelRangeXml);
                String outPutAdjLabelRangeXml = netconfController.sendMessage(netconfClient,commandGetSrAdjLabelRangeXml);
                LOG.info("output xml: " + outPutAdjLabelRangeXml);
                NetconfSrLabelInfo netconfSrLabelInfo = SrLabelXml.getAdjLabelRangeFromAdjLabelRangeXml(outPutAdjLabelRangeXml);
                device.setMinAdjSID(Integer.parseInt(netconfSrLabelInfo.getAdjLowerSid()));
                device.setMaxAdjSID(Integer.parseInt(netconfSrLabelInfo.getAdjUpperSid()));
                String commandGetSrAdjLabelXml = SrLabelXml.getSrAdjLabelXml();
                LOG.info("command xml: " + commandGetSrAdjLabelXml);
                String outPutXml = netconfController.sendMessage(netconfClient, commandGetSrAdjLabelXml);
                LOG.info("output xml: " + outPutXml);
                List<AdjLabel> adjLabelList = SrLabelXml.getSrAdjLabelFromSrAdjLabelXml(outPutXml);
                device.setAdjLabelList(adjLabelList);
                Iterator<AdjLabel> adjLabelIterator = adjLabelList.iterator();
                while(adjLabelIterator.hasNext()) {
                    AdjLabel adjLabel = adjLabelIterator.next();
                    DeviceInterface deviceInterface = device.getDeviceInterfaceByAddress(adjLabel.getAddressLocal().getAddress());
                    if ((deviceInterface != null) && (linkManager != null)){
                        DeviceInterface deviceInterface1Peer = linkManager.getPeerDeviceInterface(deviceInterface);
                        if (deviceInterface1Peer != null) {
                            deviceInterface.setAdjLabel(adjLabel);
                            deviceInterface.setSrStatus(SrStatus.ENABLED.getName());
                        }else{
                            deviceInterface.setAdjLabel(null);
                            deviceInterface.setSrStatus(null);
                        }
                    }
                }
            }
        }
        LOG.info("syncIntfLael end");
        return null;
    }

    @Override
    public String delIntfLabel(String routerId, String localAddress, String remoteAddress, String labelVal) {
        return null;
    }

    @Override
    public Device getDevice(String routerId) {
        Device device = deviceManager.getDevice(routerId);
        return device;
    }
}
