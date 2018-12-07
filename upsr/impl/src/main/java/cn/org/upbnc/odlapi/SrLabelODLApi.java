/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.SrLabelApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.DeviceInterface;
import cn.org.upbnc.enumtype.SrStatus;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.impl.UpsrProvider;
import cn.org.upbnc.util.xml.SrLabelXml;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsrlabel.rev181126.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsrlabel.rev181126.srglobal.SrgbPrefixSid;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsrlabel.rev181126.srglobal.SrgbPrefixSidBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsrlabel.rev181126.updatesrlabel.input.IntfLabel;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

public class SrLabelODLApi implements UpsrSrLabelService{
    private static final Logger LOG = LoggerFactory.getLogger(SrLabelODLApi.class);
    Session session;
    SrLabelApi srLabelApi;

    public SrLabelODLApi(Session session) {
        this.session = session;
    }

    private SrLabelApi getSrLabelApi(){
        if (this.srLabelApi == null){
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null){
                this.srLabelApi = apiInterface.getSrLabelApi();
            }
        }
        return this.srLabelApi;
    }

    @Override
    public Future<RpcResult<GetSrgbLabelOutput>> getSrgbLabel(GetSrgbLabelInput input) {
        GetSrgbLabelOutputBuilder getSrgbLabelOutputBuilder = new GetSrgbLabelOutputBuilder();
        LOG.info("getSrgbLabel begin");
        if(SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(getSrgbLabelOutputBuilder.build()).buildFuture();
        }else{
            this.getSrLabelApi();
        }
        Device device = srLabelApi.getDevice(input.getRouterId());

        getSrgbLabelOutputBuilder.setRouterId(device.getRouterId());
        getSrgbLabelOutputBuilder.setSrEnabled("1");
        SrgbPrefixSidBuilder srgbPrefixSidBuilder = new SrgbPrefixSidBuilder();
        Integer prefixLabel = device.getNodeLabel().getValue() + device.getMinNodeSID();
        srgbPrefixSidBuilder.setAdjBegin(device.getMinAdjSID().toString());
        srgbPrefixSidBuilder.setAdjEnd(device.getMaxAdjSID().toString());
        srgbPrefixSidBuilder.setSrgbBegin(device.getMinNodeSID().toString());
        srgbPrefixSidBuilder.setSrgbEnd(device.getMaxNodeSID().toString());
        srgbPrefixSidBuilder.setPrefixId(prefixLabel.toString());
        getSrgbLabelOutputBuilder.setSrgbPrefixSid(srgbPrefixSidBuilder.build());

        LOG.info("getSrgbLabel end");
        return RpcResultBuilder.success(getSrgbLabelOutputBuilder.build()).buildFuture();
    }
    @Override
    public Future<RpcResult<UpdateSrgbLabelOutput>> updateSrgbLabel(UpdateSrgbLabelInput input) {
        UpdateSrgbLabelOutputBuilder updateSrgbLabelOutputBuilder = new UpdateSrgbLabelOutputBuilder();
        LOG.info("updateSrgbLabel begin");
        if(SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(updateSrgbLabelOutputBuilder.build()).buildFuture();
        }else{
            this.getSrLabelApi();
        }
        Integer prefixLabel = Integer.parseInt(input.getSrgbPrefixSid().getPrefixId()) -
                                            Integer.parseInt(input.getSrgbPrefixSid().getSrgbBegin());
        if(input.getSrEnabled().equals(SrStatus.DISENABLED.getName())){
            srLabelApi.updateNodeLabel(input.getRouterId(),prefixLabel.toString(),SrLabelXml.ncOperationDelete);
            srLabelApi.updateNodeLabelRange(input.getRouterId(),input.getSrgbPrefixSid().getSrgbBegin(),
                                                input.getSrgbPrefixSid().getSrgbEnd(),SrLabelXml.ncOperationDelete);
        } else if(input.getSrEnabled().equals(SrStatus.ENABLED.getName())){
            srLabelApi.updateNodeLabel(input.getRouterId(),prefixLabel.toString(),SrLabelXml.ncOperationMerge);
            srLabelApi.updateNodeLabelRange(input.getRouterId(),input.getSrgbPrefixSid().getSrgbBegin(),
                    input.getSrgbPrefixSid().getSrgbEnd(),SrLabelXml.ncOperationMerge);
        } else{

        }

        LOG.info("updateSrgbLabel eng");
        return RpcResultBuilder.success(updateSrgbLabelOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<UpdateIntfLabelOutput>> updateIntfLabel(UpdateIntfLabelInput input) {
        UpdateIntfLabelOutputBuilder updateIntfLabelOutputBuilder = new UpdateIntfLabelOutputBuilder();
        LOG.info("updateIntfLabel begin");
        if(SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(updateIntfLabelOutputBuilder.build()).buildFuture();
        }else{
            this.getSrLabelApi();
        }
        if (input.getSrEnabled().equals(SrStatus.DISENABLED.getName())){
            srLabelApi.updateIntfLabel(input.getRouterId(), input.getIntfLocalAddress(),
                    input.getIntfRemoteAddress(), input.getIntfLabelVal(), SrLabelXml.ncOperationDelete);
        }else if (input.getSrEnabled().equals(SrStatus.ENABLED.getName())){
            srLabelApi.updateIntfLabel(input.getRouterId(), input.getIntfLocalAddress(),
                    input.getIntfRemoteAddress(), input.getIntfLabelVal(), SrLabelXml.ncOperationMerge);
        }else{

        }
        LOG.info("updateIntfLabel end");
        return RpcResultBuilder.success(updateIntfLabelOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetIntfLabelOutput>> getIntfLabel(GetIntfLabelInput input) {
        GetIntfLabelOutputBuilder getIntfLabelOutputBuilder = new GetIntfLabelOutputBuilder();
        LOG.info("getIntfLabel begin");
        if(SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(getIntfLabelOutputBuilder.build()).buildFuture();
        }else{
            this.getSrLabelApi();
        }
        Device device = srLabelApi.getDevice(input.getRouterId());
        DeviceInterface deviceInterface = device.getDeviceInterfaceByAddress(input.getIntfLocalAddress());
        if ((deviceInterface != null) && (deviceInterface.getAdjLabel() != null)) {
            getIntfLabelOutputBuilder.setAdjlabel(deviceInterface.getAdjLabel().getValue().toString());
        }
        LOG.info("getIntfLabel end");
        return RpcResultBuilder.success(getIntfLabelOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<UpdateSrLabelOutput>> updateSrLabel(UpdateSrLabelInput input) {
        UpdateSrLabelOutputBuilder updateSrLabelOutputBuilder = new UpdateSrLabelOutputBuilder();
        LOG.info("updateSrLabel begin");
        if(SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(updateSrLabelOutputBuilder.build()).buildFuture();
        }else{
            this.getSrLabelApi();
        }

        if (input == null || input.getRouterId() == null){
            LOG.info("updateSrLabel input is null");
            return null;
        }

        String routerId = input.getRouterId();
        String srStatus = input.getSrEnabled();
        SrgbPrefixSid srgbPrefixSid = input.getSrgbPrefixSid();
        List<IntfLabel> intfLabelList = input.getIntfLabel();
        if (srStatus != null && srStatus.equals(SrStatus.DISENABLED.getName())){
            Integer prefixLabel = Integer.parseInt(input.getSrgbPrefixSid().getPrefixId()) -
                    Integer.parseInt(input.getSrgbPrefixSid().getSrgbBegin());
            srLabelApi.updateNodeLabel(input.getRouterId(),srgbPrefixSid.getPrefixId(),SrLabelXml.ncOperationDelete);
            srLabelApi.updateNodeLabelRange(input.getRouterId(),input.getSrgbPrefixSid().getSrgbBegin(),
                    input.getSrgbPrefixSid().getSrgbEnd(),SrLabelXml.ncOperationDelete);
            Iterator<IntfLabel> intfLabelIterator = intfLabelList.iterator();
            while (intfLabelIterator.hasNext()){
                IntfLabel intfLabel = intfLabelIterator.next();
                srLabelApi.updateIntfLabel(input.getRouterId(), intfLabel.getIntfLocalAddress(),
                        intfLabel.getIntfRemoteAddress(), intfLabel.getIntfLabelVal(), SrLabelXml.ncOperationDelete);
            }
        } else if (srStatus != null && srStatus.equals(SrStatus.ENABLED.getName())){
            Integer prefixLabel = Integer.parseInt(input.getSrgbPrefixSid().getPrefixId()) -
                    Integer.parseInt(input.getSrgbPrefixSid().getSrgbBegin());
            srLabelApi.updateNodeLabel(input.getRouterId(),prefixLabel.toString(),SrLabelXml.ncOperationMerge);
            srLabelApi.updateNodeLabelRange(input.getRouterId(),input.getSrgbPrefixSid().getSrgbBegin(),
                    input.getSrgbPrefixSid().getSrgbEnd(),SrLabelXml.ncOperationMerge);
            Iterator<IntfLabel> intfLabelIterator = intfLabelList.iterator();
            while (intfLabelIterator.hasNext()){
                IntfLabel intfLabel = intfLabelIterator.next();
                if (intfLabel.getSrEnabled().equals(SrStatus.DISENABLED.getName())){
                    srLabelApi.updateIntfLabel(input.getRouterId(), intfLabel.getIntfLocalAddress(),
                            intfLabel.getIntfRemoteAddress(), intfLabel.getIntfLabelVal(), SrLabelXml.ncOperationDelete);
                }else {
                    srLabelApi.updateIntfLabel(input.getRouterId(), intfLabel.getIntfLocalAddress(),
                            intfLabel.getIntfRemoteAddress(), intfLabel.getIntfLabelVal(), SrLabelXml.ncOperationMerge);
                }
            }
        }
        updateSrLabelOutputBuilder.setResult("success");
        LOG.info("updateSrLabel begin");
        return RpcResultBuilder.success(updateSrLabelOutputBuilder.build()).buildFuture();
    }
}
