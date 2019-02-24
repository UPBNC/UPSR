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
import cn.org.upbnc.enumtype.*;
import cn.org.upbnc.service.SrLabelService;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.NetconfSrLabelInfo;
import cn.org.upbnc.util.xml.CheckXml;
import cn.org.upbnc.util.xml.SrLabelXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class SrLabelServiceImpl implements SrLabelService {
    public static final String PREFIX_SID_TYPE_ABSOLUTE = "absolute";
    public static final String PREFIX_SID_TYPE_INDEX = "index";
    public static final int ADJACENCY_LABEL_IS_DUPLICATED = 25783;
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

    private Map<String, Object> buildResult(SrLabelErrorCodeEnum srLabelErrorCodeEnum) {
        Map<String, Object> resultMap = new HashMap<>();
        if (srLabelErrorCodeEnum != SrLabelErrorCodeEnum.EXECUTE_SUCCESS) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), srLabelErrorCodeEnum.getMessage());
        } else {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), CodeEnum.SUCCESS.getMessage());
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> updateNodeLabel(String routerId, String labelVal, String action) {
        boolean isChanged = true;
        Device device = null;
        LOG.info("updateNodeLabel begin");
        device = deviceManager.getDevice(routerId);
        if (device == null) {
            return buildResult(SrLabelErrorCodeEnum.DEVICE_INVALID);
        }
        if (SrLabelXml.ncOperationDelete.equals(action) && SrStatusEnum.DISENABLED.getName().equals(device.getSrStatus())) {
            return buildResult(SrLabelErrorCodeEnum.EXECUTE_SUCCESS);
        }
        if ((device.getNodeLabel() != null) && (device.getNodeLabel().getValue().equals(Integer.parseInt(labelVal)))) {
            LOG.info("node label is not changed: " + labelVal);
            isChanged = false;
        }
        if ((isChanged == false) && action.equals(SrLabelXml.ncOperationMerge)) {
            LOG.info("do not need update");
            return buildResult(SrLabelErrorCodeEnum.EXECUTE_SUCCESS);
        }
        if ((device.getOspfProcess() == null) || (device.getNetConf() == null)) {
            return buildResult(SrLabelErrorCodeEnum.CONFIG_FAILED);
        }
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
        String commandSetSrNodeLabelXml = SrLabelXml.setSrNodeLabelXml(device.getOspfProcess().getProcessId().toString(),
                device.getOspfProcess().getAreaId(), device.getOspfProcess().getIntfName(), this.PREFIX_SID_TYPE_ABSOLUTE, labelVal, action);
        LOG.info("commandSetSrNodeLabelXml : " + commandSetSrNodeLabelXml);
        String outPutXml = netconfController.sendMessage(netconfClient, commandSetSrNodeLabelXml);
        if (CheckXml.checkOk(outPutXml).equals(CheckXml.RESULT_OK) != true) {
            LOG.info("updateNodeLabel fail");
            return buildResult(SrLabelErrorCodeEnum.CONFIG_FAILED);
        }
        if (action.equals(SrLabelXml.ncOperationDelete)) {
            device.setNodeLabel(null);
            device.setSrStatus(SrStatusEnum.DISENABLED.getName());
        } else {
            NodeLabel nodeLabel = (device.getNodeLabel() == null) ? new NodeLabel() : device.getNodeLabel();
            nodeLabel.setValue(Integer.valueOf(labelVal));
            device.setNodeLabel(nodeLabel);
            device.setSrStatus(SrStatusEnum.ENABLED.getName());
        }
        LOG.info("updateNodeLabel end");
        return buildResult(SrLabelErrorCodeEnum.EXECUTE_SUCCESS);
    }

    public Map<String, Object> deleteSrNodeLabelRange(Device device) {
        if (((device.getMinNodeSID() != null) && (device.getMinNodeSID().intValue() != 0)) ||
                ((device.getMaxNodeSID() != null) && (device.getMaxNodeSID().intValue() != 0))) {
            NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
            String commandDeleteSrNodeLabelRangeXml = SrLabelXml.setSrNodeLabelRangeXml(device.getOspfProcess().getProcessId().toString(),
                    device.getMinNodeSID().toString(), device.getMaxNodeSID().toString(), SrLabelXml.ncOperationDelete);
            LOG.info("commandDeleteSrNodeLabelRangeXml: " + commandDeleteSrNodeLabelRangeXml);
            String outPutdeleteXml = netconfController.sendMessage(netconfClient, commandDeleteSrNodeLabelRangeXml);
            LOG.info("outPutdeleteXml: " + outPutdeleteXml);
            device.setMinNodeSID(null);
            device.setMaxNodeSID(null);
        }
        return null;
    }

    @Override
    public Map<String, Object> updateNodeLabelRange(String routerId, String labelBegin, String labelEnd, String action) {
        boolean isChanged = true;
        Device device = null;
        LOG.info("updateNodeLabelRange begin :" + action);
        device = deviceManager.getDevice(routerId);
        if (device == null) {
            return buildResult(SrLabelErrorCodeEnum.DEVICE_INVALID);
        }
        if ((device.getMinNodeSID() != null) && (device.getMinNodeSID() == Integer.parseInt(labelBegin)) &&
                (device.getMaxNodeSID() != null) && (device.getMaxNodeSID() == Integer.parseInt(labelEnd))) {
            LOG.info("node label range is not changed: " + labelBegin + " , " + labelEnd);
            isChanged = false;
        }
        if ((isChanged == false) && action.equals(SrLabelXml.ncOperationMerge)) {
            LOG.info("do not need update range");
            return buildResult(SrLabelErrorCodeEnum.EXECUTE_SUCCESS);
        }
        if ((device.getNetConf() == null) || (device.getOspfProcess() == null)) {
            return buildResult(SrLabelErrorCodeEnum.CONFIG_FAILED);
        }
        //刪除原有配置
        Map<String, Object> delLabelRangeRet = this.deleteSrNodeLabelRange(device);
        if (delLabelRangeRet != null) {
            return delLabelRangeRet;
        }
        //配置新的标签范围
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
        if (action.equals(SrLabelXml.ncOperationDelete) != true) {
            String commandCreateSrNodeLabelRangeXml = SrLabelXml.setSrNodeLabelRangeXml(device.getOspfProcess().getProcessId().toString(),
                    labelBegin, labelEnd, SrLabelXml.ncOperationCreate);
            LOG.info("commandCreateSrNodeLabelRangeXml: " + commandCreateSrNodeLabelRangeXml);
            String outPutcreateXml = netconfController.sendMessage(netconfClient, commandCreateSrNodeLabelRangeXml);
            if (CheckXml.checkOk(outPutcreateXml).equals(CheckXml.RESULT_OK) != true) {
                LOG.info("updateNodeLabelRange create failed");
                return buildResult(SrLabelErrorCodeEnum.CONFIG_FAILED);
            }
            device.setMinNodeSID(Integer.valueOf(labelBegin));
            device.setMaxNodeSID(Integer.valueOf(labelEnd));
        }
        LOG.info("updateNodeLabelRange end");
        return buildResult(SrLabelErrorCodeEnum.EXECUTE_SUCCESS);
    }

    //节点标签范围同步
    public String syncDeviceNodeLabelRange(Device device) {
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
        String commandGetSrNodeLabelRangeXml = SrLabelXml.getSrNodeLabelRangeXml();
        LOG.info("commandGetSrNodeLabelRangeXml: " + commandGetSrNodeLabelRangeXml);
        String outPutLabelRangeXml = netconfController.sendMessage(netconfClient, commandGetSrNodeLabelRangeXml);
        LOG.info("outPutLabelRangeXml: " + outPutLabelRangeXml);
        NetconfSrLabelInfo netconfSrLabelInfo = SrLabelXml.getSrNodeLabelRangeFromNodeLabelRangeXml(outPutLabelRangeXml);
        if (netconfSrLabelInfo == null) {
            LOG.info("can not get netconfSrLabelInfo");
            return null;
        }
        if (netconfSrLabelInfo.getSrgbBegin() != null) {
            device.setMinNodeSID(Integer.valueOf(netconfSrLabelInfo.getSrgbBegin()));
        }
        if (netconfSrLabelInfo.getSrgbEnd() != null) {
            device.setMaxNodeSID(Integer.valueOf(netconfSrLabelInfo.getSrgbEnd()));
        }
        return null;
    }

    //routerid所在接口、ospf进程号同步
    public String syncDeviceOspfProcess(Device device) {
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
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
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
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
            if (netconfSrLabelInfo.getPrefixType().equals(this.PREFIX_SID_TYPE_INDEX)) {
                nodeLabel.setValue(Integer.parseInt(netconfSrLabelInfo.getPrefixLabel()) + device.getMinNodeSID().intValue());
            } else {
                nodeLabel.setValue(Integer.valueOf(netconfSrLabelInfo.getPrefixLabel()));
            }
            nodeLabel.setPrefixType(netconfSrLabelInfo.getPrefixType());
            device.setSrStatus(SrStatusEnum.ENABLED.getName());
        } else {
            device.setSrStatus(SrStatusEnum.DISENABLED.getName());
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
        this.syncDeviceNodeLabelRange(device);
        this.syncDeviceNodeLabel(device);
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
                LOG.info("router : " + device.getRouterId() + " ret : " + ret);
            }
        }
        LOG.info("syncNodeLabel end");
        return ret;
    }

    @Override
    public Map<String, Object> updateIntfLabel(String routerId, String ifAddress, String labelVal, String action) {
        Device device;
        device = deviceManager.getDevice(routerId);
        if (device == null) {
            return buildResult(SrLabelErrorCodeEnum.DEVICE_INVALID);
        }
        if (device.getNetConf() == null) {
            return buildResult(SrLabelErrorCodeEnum.CONFIG_FAILED);
        }
        DeviceInterface localDeviceInterface = device.getDeviceInterfaceByAddress(ifAddress);
        if (localDeviceInterface == null) {
            return buildResult(SrLabelErrorCodeEnum.CONFIG_FAILED);
        }
        DeviceInterface remoteDeviceInterface = linkManager.getPeerDeviceInterface(localDeviceInterface);
        if (remoteDeviceInterface == null) {
            return buildResult(SrLabelErrorCodeEnum.CONFIG_FAILED);
        }
        String remoteAddress = remoteDeviceInterface.getIp().getAddress();
        if ((localDeviceInterface != null) && (localDeviceInterface.getAdjLabel() != null) &&
                (localDeviceInterface.getAdjLabel().getValue() == Integer.parseInt(labelVal)) &&
                (true == action.equals(SrLabelXml.ncOperationMerge))) {
            LOG.info("adjlabel is not changed" + localDeviceInterface.getIp().getAddress() + " -> " + remoteAddress + " : " + labelVal);
            return buildResult(SrLabelErrorCodeEnum.EXECUTE_SUCCESS);
        }
        Map<String, Object> updateLabelRet = this.updateIntfAdjLabel(device, localDeviceInterface, remoteAddress, labelVal, action);
        if (updateLabelRet != null) {
            return updateLabelRet;
        }
        return buildResult(SrLabelErrorCodeEnum.EXECUTE_SUCCESS);
    }

    @Override
    public Map<String, Object> checkIntfLabel(String routerId, String ifAddress, String labelVal) {
        Device device;
        device = deviceManager.getDevice(routerId);
        if (device == null) {
            return buildResult(SrLabelErrorCodeEnum.DEVICE_INVALID);
        }
        DeviceInterface localDeviceInterface = device.getDeviceInterfaceByAddress(ifAddress);
        if (localDeviceInterface == null) {
            return buildResult(SrLabelErrorCodeEnum.CONFIG_FAILED);
        }
        DeviceInterface remoteDeviceInterface = linkManager.getPeerDeviceInterface(localDeviceInterface);
        if (remoteDeviceInterface == null) {
            return buildResult(SrLabelErrorCodeEnum.CONFIG_FAILED);
        }
        String remoteAddress = remoteDeviceInterface.getIp().getAddress();
        if (device.getAdjLabelList() != null) {
            Iterator<AdjLabel> adjLabelIterator = device.getAdjLabelList().iterator();
            while (adjLabelIterator.hasNext()) {
                AdjLabel adjLabel = adjLabelIterator.next();
                if (Integer.parseInt(labelVal) == adjLabel.getValue() &&
                        ((adjLabel.getAddressLocal().equals(ifAddress) != true) ||
                                (adjLabel.getAddressRemote().equals(remoteAddress) != true))) {
                    {
                        return buildResult(SrLabelErrorCodeEnum.LABEL_INVALID);
                    }
                }
            }
        }
        return buildResult(SrLabelErrorCodeEnum.EXECUTE_SUCCESS);
    }

    //接口标签更新
    public Map<String, Object> updateIntfAdjLabel(Device device, DeviceInterface localDeviceInterface, String remoteAddress, String labelVal, String action) {
        LOG.info("updateIntfLabel begin");
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
        String commandUpdateXml = SrLabelXml.setSrAdjLabelXml(action, localDeviceInterface.getIp().getAddress(), remoteAddress, labelVal);
        LOG.info("commandUpdateXml: " + commandUpdateXml);
        String outPutUpdateXml = netconfController.sendMessage(netconfClient, commandUpdateXml);
        LOG.info("outPutUpdateXml: " + outPutUpdateXml);
        if (CheckXml.checkOk(outPutUpdateXml).equals(CheckXml.RESULT_OK) != true) {
            LOG.info("updateIntfLabel failed");
            if (CheckXml.getErrorInfoCode(outPutUpdateXml) == this.ADJACENCY_LABEL_IS_DUPLICATED) {
                return buildResult(SrLabelErrorCodeEnum.LABEL_DUPLICATED);
            } else {
                return buildResult(SrLabelErrorCodeEnum.CONFIG_FAILED);
            }
        }
        if (true == action.equals(SrLabelXml.ncOperationDelete)) {
            localDeviceInterface.setSrStatus(SrStatusEnum.DISENABLED.getName());
            localDeviceInterface.setAdjLabel(null);
        } else {
            AdjLabel adjLabel = new AdjLabel();
            adjLabel.setAddressLocal(new Address(localDeviceInterface.getIp().getAddress(), AddressTypeEnum.V4));
            adjLabel.setAddressRemote(new Address(remoteAddress, AddressTypeEnum.V4));
            adjLabel.setValue(Integer.valueOf(labelVal));
            localDeviceInterface.setSrStatus(SrStatusEnum.ENABLED.getName());
            localDeviceInterface.setAdjLabel(adjLabel);
        }
        LOG.info("updateIntfLabel end");
        return null;
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
        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getRouterID());
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
        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getRouterID());
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
                    deviceInterface.setSrStatus(SrStatusEnum.ENABLED.getName());
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
}
