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
    public static final String PrefixSidType = "index";
    private static final Logger LOG = LoggerFactory.getLogger(SrLabelServiceImpl.class);
    private static SrLabelService ourInstance = null;
    private BaseInterface baseInterface;
    private NetConfManager netConfManager;
    private DeviceManager deviceManager;
    private LinkManager linkManager;

    public SrLabelServiceImpl() {
        this.baseInterface = null;
        this.netConfManager = null;
        this.deviceManager = null;
        this.linkManager = null;
    }

    public static SrLabelService getInstance() {
        if (ourInstance == null) {
            ourInstance = new SrLabelServiceImpl();
        }
        return ourInstance;
    }

    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        if (null != baseInterface) {
            this.netConfManager = this.baseInterface.getNetConfManager();
            this.deviceManager = this.baseInterface.getDeviceManager();
            this.linkManager = this.baseInterface.getLinkManager();
        }
        return true;
    }

    @Override
    public boolean updateNodeLabel(String routerId, String labelVal, String action) {
        boolean isChanged = true;
        Device device = null;
        LOG.info("updateNodeLabel begin");
        device = deviceManager.getDevice(routerId);
        if ((device == null) ||
                (SrLabelXml.ncOperationDelete.equals(action) && SrStatus.DISENABLED.getName().equals(device.getSrStatus()))) {
            return false;
        }
        if ((device.getNodeLabel() != null) && (device.getNodeLabel().getValue().equals(Integer.parseInt(labelVal)))) {
            LOG.info("node label is not changed: " + labelVal);
            isChanged = false;
        }
        if ((isChanged == false) && action.equals(SrLabelXml.ncOperationMerge)) {
            LOG.info("do not need update");
            return true;
        }
        if ((device.getOspfProcess() == null) || (device.getNetConf() == null)) {
            return false;
        }
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        String commandSetSrNodeLabelXml = SrLabelXml.setSrNodeLabelXml(device.getOspfProcess().getProcessId().toString(),
                device.getOspfProcess().getAreaId(), device.getOspfProcess().getIntfName(), this.PrefixSidType, labelVal, action);
        LOG.info("commandSetSrNodeLabelXml : " + commandSetSrNodeLabelXml);
        String outPutXml = netconfController.sendMessage(netconfClient, commandSetSrNodeLabelXml);
        if (CheckXml.checkOk(outPutXml).equals("ok") != true) {
            LOG.info("updateNodeLabel fail");
            return false;
        }
        if (action.equals(SrLabelXml.ncOperationDelete)) {
            device.setNodeLabel(null);
            device.setSrStatus(SrStatus.DISENABLED.getName());
        } else {
            NodeLabel nodeLabel = (device.getNodeLabel() == null) ? new NodeLabel() : device.getNodeLabel();
            nodeLabel.setValue(Integer.parseInt(labelVal));
            device.setNodeLabel(nodeLabel);
            device.setSrStatus(SrStatus.ENABLED.getName());
        }
        LOG.info("updateNodeLabel end");
        return true;
    }

    @Override
    public boolean updateNodeLabelRange(String routerId, String labelBegin, String labelEnd, String action) {
        boolean isChanged = true;
        Device device = null;
        LOG.info("updateNodeLabelRange begin :" + action);
        device = deviceManager.getDevice(routerId);
        if (device == null) {
            return false;
        }
        if ((device.getMinNodeSID() != null) && (device.getMinNodeSID() == Integer.parseInt(labelBegin)) &&
                (device.getMaxNodeSID() != null) && (device.getMaxNodeSID() == Integer.parseInt(labelEnd))) {
            LOG.info("node label range is not changed: " + labelBegin + " , " + labelEnd);
            isChanged = false;
        }
        if ((isChanged == false) && action.equals(SrLabelXml.ncOperationMerge)) {
            LOG.info("do not need update range");
            return true;
        }
        if ((device.getNetConf() == null) || (device.getOspfProcess() == null)) {
            return false;
        }
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        if ((device.getMinNodeSID() != null) || (device.getMaxNodeSID() != null)) {
            String commandDeleteSrNodeLabelRangeXml = SrLabelXml.setSrNodeLabelRangeXml(device.getOspfProcess().getProcessId().toString(),
                    device.getMinNodeSID().toString(), device.getMaxNodeSID().toString(), SrLabelXml.ncOperationDelete);
            LOG.info("commandDeleteSrNodeLabelRangeXml: " + commandDeleteSrNodeLabelRangeXml);
            String outPutdeleteXml = netconfController.sendMessage(netconfClient, commandDeleteSrNodeLabelRangeXml);
            LOG.info("outPutdeleteXml: " + outPutdeleteXml);
            if (CheckXml.checkOk(outPutdeleteXml).equals("ok") != true) {
                LOG.info("updateNodeLabelRange delete failed");
                return false;
            }
            device.setMinNodeSID(null);
            device.setMaxNodeSID(null);
        }
        if (action.equals(SrLabelXml.ncOperationDelete) != true) {
            String commandCreateSrNodeLabelRangeXml = SrLabelXml.setSrNodeLabelRangeXml(device.getOspfProcess().getProcessId().toString(),
                    labelBegin, labelEnd, SrLabelXml.ncOperationCreate);
            LOG.info("commandCreateSrNodeLabelRangeXml: " + commandCreateSrNodeLabelRangeXml);
            String outPutcreateXml = netconfController.sendMessage(netconfClient, commandCreateSrNodeLabelRangeXml);
            if (CheckXml.checkOk(outPutcreateXml).equals("ok") != true) {
                LOG.info("updateNodeLabelRange create failed");
                return false;
            }
        }
        device.setMinNodeSID(Integer.valueOf(labelBegin));
        device.setMaxNodeSID(Integer.valueOf(labelEnd));
        LOG.info("updateNodeLabelRange end");
        return true;
    }
    //节点标签范围同步
    public String syncDeviceNodeLabelRange(Device device) {
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        String commandGetSrNodeLabelRangeXml = SrLabelXml.getSrNodeLabelRangeXml();
        LOG.info("commandGetSrNodeLabelRangeXml: " + commandGetSrNodeLabelRangeXml);
        String outPutLabelRangeXml = netconfController.sendMessage(netconfClient, commandGetSrNodeLabelRangeXml);
        LOG.info("outPutLabelRangeXml: " + outPutLabelRangeXml);
        NetconfSrLabelInfo netconfSrLabelInfo = SrLabelXml.getSrNodeLabelRangeFromNodeLabelRangeXml(outPutLabelRangeXml);
        if (null == netconfSrLabelInfo) {
            LOG.info("can not get netconfSrLabelInfo");
            return null;
        }
        if (null != netconfSrLabelInfo.getSrgbBegin()) {
            device.setMinNodeSID(new Integer(Integer.parseInt(netconfSrLabelInfo.getSrgbBegin())));
        }
        if (null != netconfSrLabelInfo.getSrgbEnd()) {
            device.setMaxNodeSID(new Integer(Integer.parseInt(netconfSrLabelInfo.getSrgbEnd())));
        }
        return null;
    }
    //routerid所在接口、ospf进程号同步
    public String syncDeviceOspfProcess(Device device) {
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        String commandGetOspfProcessXml = SrLabelXml.getOspfProcessXml();
        LOG.info("commandGetOspfProcessXml: " + commandGetOspfProcessXml);
        String outPutOspfProcessXml = netconfController.sendMessage(netconfClient, commandGetOspfProcessXml);
        LOG.info("outPutOspfProcessXml: " + outPutOspfProcessXml);
        NetconfSrLabelInfo netconfSrLabelInfo = SrLabelXml.getOspfProcessFromOspfProcessXml(outPutOspfProcessXml, device.getRouterId());

        String commandGetLoopBackXml = SrLabelXml.getLoopBackXml();
        LOG.info("commandGetLoopBackXml: " + commandGetLoopBackXml);
        String outPutLoopBackXml = netconfController.sendMessage(netconfClient, commandGetLoopBackXml);
        LOG.info("outPutLoopBackXml: " + outPutLoopBackXml);
        String loobackName = SrLabelXml.getLoopBackFromLoopBackXml(outPutLoopBackXml, device.getRouterId());

        OspfProcess ospfProcess = new OspfProcess();
        ospfProcess.setIntfName(loobackName);
        if (netconfSrLabelInfo.getOspfProcessId() != null) {
            ospfProcess.setProcessId(Integer.valueOf(netconfSrLabelInfo.getOspfProcessId()));
        }
        device.setOspfProcess(ospfProcess);
        return null;
    }
    //同步节点标签
    public String syncDeviceNodeLabel(Device device) {
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        OspfProcess ospfProcess = device.getOspfProcess();
        String commandGetSrNodeLabelXml = SrLabelXml.getSrNodeLabelXml(ospfProcess.getProcessId().toString());
        LOG.info("commandGetSrNodeLabelXml: " + commandGetSrNodeLabelXml);
        String outPutSrNodeLabelXml = netconfController.sendMessage(netconfClient, commandGetSrNodeLabelXml);
        LOG.info("outPutSrNodeLabelXml: " + outPutSrNodeLabelXml);
        NetconfSrLabelInfo netconfSrLabelInfo = SrLabelXml.getSrNodeLabelFromgSrNodeLabelXml(outPutSrNodeLabelXml,
                ospfProcess.getProcessId().toString(), ospfProcess.getIntfName());
        if (netconfSrLabelInfo == null) {
            LOG.info("can not get netconfSrLabelInfo");
            return null;
        }
        ospfProcess.setAreaId(netconfSrLabelInfo.getOspfAreaId());
        NodeLabel nodeLabel = new NodeLabel();
        if ((null != netconfSrLabelInfo.getPrefixLabel()) && (Integer.parseInt(netconfSrLabelInfo.getPrefixLabel()) != 0)) {
            nodeLabel.setValue(Integer.valueOf(netconfSrLabelInfo.getPrefixLabel()));
            device.setSrStatus(SrStatus.ENABLED.getName());
        } else {
            device.setSrStatus(SrStatus.DISENABLED.getName());
        }
        device.setNodeLabel(nodeLabel);
        return null;
    }

    @Override
    public boolean syncNodeLabel(String routerId) {
        Device device = null;
        LOG.info("syncNodeLabel begin " + routerId);
        device = deviceManager.getDevice(routerId);
        if ((device == null) || (device.getNetConf() == null)) {
            LOG.info("can not find device or netconf");
            return false;
        }
        this.syncDeviceOspfProcess(device);
        this.syncDeviceNodeLabel(device);
        this.syncDeviceNodeLabelRange(device);
        LOG.info("syncNodeLabel end ");
        return true;
    }

    public boolean syncAllNodeLabel() {
        boolean ret = false;
        LOG.info("syncNodeLabel begin");
        if (this.deviceManager != null) {
            for (Device device : this.deviceManager.getDeviceList()) {
                if ((device.getNetConf() == null) || (device.getNetConf().getStatus() != NetConfStatusEnum.Connected)) {
                    continue;
                }
                ret = this.syncNodeLabel(device.getRouterId());
                LOG.info("router ： " + device.getRouterId() + " ret : " + ret);
            }
        }
        LOG.info("syncNodeLabel end");
        return ret;
    }

    @Override
    public boolean updateIntfLabel(String routerId, String ifAddress, String labelVal, String action) {
        Device device = null;
        device = deviceManager.getDevice(routerId);
        if ((device == null) || (device.getNetConf()) == null) {
            return false;
        }
        DeviceInterface localDeviceInterface = device.getDeviceInterfaceByAddress(ifAddress);
        if (localDeviceInterface == null) {
            return false;
        }
        DeviceInterface remoteDeviceInterface = linkManager.getPeerDeviceInterface(localDeviceInterface);
        if (remoteDeviceInterface == null) {
            return false;
        }
        String remoteAddress = remoteDeviceInterface.getIp().getAddress();
        if ((localDeviceInterface != null) && (localDeviceInterface.getAdjLabel() != null) &&
                (localDeviceInterface.getAdjLabel().getValue() == Integer.parseInt(labelVal)) &&
                (true == action.equals(SrLabelXml.ncOperationMerge))) {
            LOG.info("adjlabel is not changed" + localDeviceInterface.getIp().getAddress() + " -> " + remoteAddress + " : " + labelVal);
            return false;
        }
        this.updateIntfAdjLabel(device,localDeviceInterface,remoteAddress,labelVal,action);
        return false;
    }

    //接口标签更新
    public boolean updateIntfAdjLabel(Device device, DeviceInterface localDeviceInterface, String remoteAddress, String labelVal, String action) {
        LOG.info("updateIntfLabel begin");
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        String commandUpdateXml = SrLabelXml.setSrAdjLabelXml(action, localDeviceInterface.getIp().getAddress(), remoteAddress, labelVal);
        LOG.info("commandUpdateXml: " + commandUpdateXml);
        String outPutUpdateXml = netconfController.sendMessage(netconfClient, commandUpdateXml);
        LOG.info("outPutUpdateXml: " + outPutUpdateXml);
        if (CheckXml.checkOk(outPutUpdateXml).equals("ok") != true) {
            LOG.info("updateIntfLabel failed");
            return false;
        }
        if (true == action.equals(SrLabelXml.ncOperationDelete)) {
            localDeviceInterface.setSrStatus(SrStatus.DISENABLED.getName());
            localDeviceInterface.setAdjLabel(null);
        } else {
            AdjLabel adjLabel = new AdjLabel();
            adjLabel.setAddressLocal(new Address(localDeviceInterface.getIp().getAddress(), AddressTypeEnum.V4));
            adjLabel.setAddressRemote(new Address(remoteAddress, AddressTypeEnum.V4));
            adjLabel.setValue(Integer.valueOf(labelVal));
            localDeviceInterface.setSrStatus(SrStatus.ENABLED.getName());
            localDeviceInterface.setAdjLabel(adjLabel);
        }
        LOG.info("updateIntfLabel end");
        return true;
    }

    @Override
    public boolean syncAllIntfLabel() {
        LOG.info("syncIntfLael begin");
        if (this.deviceManager == null) {
            LOG.info("deviceManager is null");
            return false;
        }

        for (Device device : this.deviceManager.getDeviceList()) {
            syncIntfLabel(device.getRouterId());
        }
        LOG.info("syncIntfLael end");
        return true;
    }
    public String syncDeviceAdjLabelRange(Device device) {
        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        String commandGetSrAdjLabelRangeXml = SrLabelXml.getSrAdjLabelRangeXml();
        LOG.info("command xml: " + commandGetSrAdjLabelRangeXml);
        String outPutAdjLabelRangeXml = netconfController.sendMessage(netconfClient, commandGetSrAdjLabelRangeXml);
        LOG.info("output xml: " + outPutAdjLabelRangeXml);
        NetconfSrLabelInfo netconfSrLabelInfo = SrLabelXml.getAdjLabelRangeFromAdjLabelRangeXml(outPutAdjLabelRangeXml);
        if (null != netconfSrLabelInfo.getAdjLowerSid()) {
            device.setMinAdjSID(Integer.valueOf(netconfSrLabelInfo.getAdjLowerSid()));
        }
        if (null != netconfSrLabelInfo.getAdjUpperSid()) {
            device.setMaxAdjSID(Integer.valueOf(netconfSrLabelInfo.getAdjUpperSid()));
        }
        return null;
    }
    public String syncDeviceIntfLabel(Device device) {
        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        String commandGetSrAdjLabelXml = SrLabelXml.getSrAdjLabelXml();
        LOG.info("commandGetSrAdjLabelXml: " + commandGetSrAdjLabelXml);
        String outPutAdjLabelXml = netconfController.sendMessage(netconfClient, commandGetSrAdjLabelXml);
        LOG.info("outPutAdjLabelXml: " + outPutAdjLabelXml);
        List<AdjLabel> adjLabelList = SrLabelXml.getSrAdjLabelFromSrAdjLabelXml(outPutAdjLabelXml);
        device.setAdjLabelList(adjLabelList);
        Iterator<AdjLabel> adjLabelIterator = adjLabelList.iterator();
        while (adjLabelIterator.hasNext()) {
            AdjLabel adjLabel = adjLabelIterator.next();
            if (adjLabel.getAddressLocal() == null) {
                continue;
            }
            DeviceInterface deviceInterface = device.getDeviceInterfaceByAddress(adjLabel.getAddressLocal().getAddress());
            if ((deviceInterface != null) && (linkManager != null)) {
                DeviceInterface deviceInterface1Peer = linkManager.getPeerDeviceInterface(deviceInterface);
                if (deviceInterface1Peer != null) {
                    deviceInterface.setAdjLabel(adjLabel);
                    deviceInterface.setSrStatus(SrStatus.ENABLED.getName());
                } else {
                    deviceInterface.setAdjLabel(null);
                    deviceInterface.setSrStatus(null);
                }
            }
        }
        return null;
    }
    public boolean syncIntfLabel(String routerId) {
        if ((routerId == null) || (routerId.equals(""))) {
            LOG.info("routerId is null or empty ");
            return false;
        }
        Device device = deviceManager.getDevice(routerId);
        if ((device.getNetConf() == null) || (device.getNetConf().getStatus() != NetConfStatusEnum.Connected)) {
            LOG.info("can not connect device by netconf, which device routerId=" + device.getRouterId());
            return false;
        }
        this.syncDeviceAdjLabelRange(device);
        this.syncDeviceIntfLabel(device);
        return true;
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
