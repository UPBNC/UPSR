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
import java.util.Map;
import java.util.concurrent.Future;

public class SrLabelODLApi implements UpsrSrLabelService {
    private static final Logger LOG = LoggerFactory.getLogger(SrLabelODLApi.class);
    Session session;
    SrLabelApi srLabelApi;

    public SrLabelODLApi(Session session) {
        this.session = session;
    }

    private SrLabelApi getSrLabelApi() {
        if (this.srLabelApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                this.srLabelApi = apiInterface.getSrLabelApi();
            }
        }
        return this.srLabelApi;
    }

    @Override
    public Future<RpcResult<GetSrgbLabelOutput>> getSrgbLabel(GetSrgbLabelInput input) {
        //该函数暂未用到，待删除
        GetSrgbLabelOutputBuilder getSrgbLabelOutputBuilder = new GetSrgbLabelOutputBuilder();
        LOG.info("getSrgbLabel begin : " + input.getRouterId());
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(getSrgbLabelOutputBuilder.build()).buildFuture();
        } else {
            this.getSrLabelApi();
        }
        LOG.info("getSrgbLabel end");
        return RpcResultBuilder.success(getSrgbLabelOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<UpdateSrgbLabelOutput>> updateSrgbLabel(UpdateSrgbLabelInput input) {
        //该函数暂未用到，待删除
        UpdateSrgbLabelOutputBuilder updateSrgbLabelOutputBuilder = new UpdateSrgbLabelOutputBuilder();
        LOG.info("updateSrgbLabel begin");
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(updateSrgbLabelOutputBuilder.build()).buildFuture();
        } else {
            this.getSrLabelApi();
        }
        LOG.info("updateSrgbLabel end");
        return RpcResultBuilder.success(updateSrgbLabelOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<UpdateIntfLabelOutput>> updateIntfLabel(UpdateIntfLabelInput input) {
        //该函数暂未用到，待删除
        UpdateIntfLabelOutputBuilder updateIntfLabelOutputBuilder = new UpdateIntfLabelOutputBuilder();
        LOG.info("updateIntfLabel begin");
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(updateIntfLabelOutputBuilder.build()).buildFuture();
        } else {
            this.getSrLabelApi();
        }
        LOG.info("updateIntfLabel end");
        return RpcResultBuilder.success(updateIntfLabelOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetIntfLabelOutput>> getIntfLabel(GetIntfLabelInput input) {
        //该函数暂未用到，待删除
        GetIntfLabelOutputBuilder getIntfLabelOutputBuilder = new GetIntfLabelOutputBuilder();
        LOG.info("getIntfLabel begin");
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(getIntfLabelOutputBuilder.build()).buildFuture();
        } else {
            this.getSrLabelApi();
        }
        LOG.info("getIntfLabel end");
        return RpcResultBuilder.success(getIntfLabelOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<UpdateSrLabelOutput>> updateSrLabel(UpdateSrLabelInput input) {
        UpdateSrLabelOutputBuilder updateSrLabelOutputBuilder = new UpdateSrLabelOutputBuilder();
        LOG.info("updateSrLabel begin");
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(updateSrLabelOutputBuilder.build()).buildFuture();
        } else {
            this.getSrLabelApi();
        }
        if ((input == null) || (input.getRouterId() == null) || (input.getSrEnabled() == null)) {
            LOG.info("updateSrLabel input is null");
            return RpcResultBuilder.success(updateSrLabelOutputBuilder.build()).buildFuture();
        }
        LOG.info(input.toString());
        String srStatus = input.getSrEnabled();
        if (input.getSrEnabled().equals(SrStatus.DISENABLED.getName())) {
            this.disableSrLabel(input);
        } else if (input.getSrEnabled().equals(SrStatus.ENABLED.getName())) {
            this.enableSrLabel(input);
        }
        updateSrLabelOutputBuilder.setResult("success");
        LOG.info("updateSrLabel end");
        return RpcResultBuilder.success(updateSrLabelOutputBuilder.build()).buildFuture();
    }
    private Map<String,Object> disableSrLabel(UpdateSrLabelInput input) {
        List<IntfLabel> intfLabelList = input.getIntfLabel();
        srLabelApi.updateNodeLabel(input.getRouterId(), input.getSrgbPrefixSid().getSrgbBegin(),
                input.getSrgbPrefixSid().getPrefixId(),SrLabelXml.ncOperationDelete);
        srLabelApi.updateNodeLabelRange(input.getRouterId(), input.getSrgbPrefixSid().getSrgbBegin(),
                input.getSrgbPrefixSid().getSrgbEnd(), SrLabelXml.ncOperationDelete);
        Iterator<IntfLabel> intfLabelIterator = intfLabelList.iterator();
        while (intfLabelIterator.hasNext()) {
            IntfLabel intfLabel = intfLabelIterator.next();
            srLabelApi.updateIntfLabel(input.getRouterId(), intfLabel.getIntfLocalAddress(),
                    intfLabel.getIntfLabelVal(), SrLabelXml.ncOperationDelete);
        }
        return null;
    }
    private Map<String,Object> enableSrLabel(UpdateSrLabelInput input) {
        List<IntfLabel> intfLabelList = input.getIntfLabel();
        srLabelApi.updateNodeLabelRange(input.getRouterId(), input.getSrgbPrefixSid().getSrgbBegin(),
                input.getSrgbPrefixSid().getSrgbEnd(), SrLabelXml.ncOperationMerge);
        srLabelApi.updateNodeLabel(input.getRouterId(), input.getSrgbPrefixSid().getSrgbBegin(),
                input.getSrgbPrefixSid().getPrefixId(), SrLabelXml.ncOperationMerge);
        Iterator<IntfLabel> intfLabelIterator = intfLabelList.iterator();
        while (intfLabelIterator.hasNext()) {
            IntfLabel intfLabel = intfLabelIterator.next();
            if (intfLabel.getSrEnabled().equals(SrStatus.DISENABLED.getName())) {
                srLabelApi.updateIntfLabel(input.getRouterId(), intfLabel.getIntfLocalAddress(),
                        intfLabel.getIntfLabelVal(), SrLabelXml.ncOperationDelete);
            } else {
                srLabelApi.updateIntfLabel(input.getRouterId(), intfLabel.getIntfLocalAddress(),
                        intfLabel.getIntfLabelVal(), SrLabelXml.ncOperationMerge);
            }
        }
        return null;
    }
}
