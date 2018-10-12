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
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.impl.UpsrProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsrlabel.rev181126.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsrlabel.rev181126.srgblabelinfo.SrgbPrefixSidBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

public class SrLabelODLApi implements UpsrSrLabelService{
    private static final Logger LOG = LoggerFactory.getLogger(UpsrProvider.class);
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
        srgbPrefixSidBuilder.setAdjBegin(device.getMinAdjSID().toString());
        srgbPrefixSidBuilder.setAdjEnd(device.getMaxAdjSID().toString());
        srgbPrefixSidBuilder.setSrgbBegin(device.getMinNodeSID().toString());
        srgbPrefixSidBuilder.setSrgbEnd(device.getMaxNodeSID().toString());
        srgbPrefixSidBuilder.setPrefixId(device.getNodeLabel().getValue().toString());
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
        srLabelApi.updateIntfLabel(input.getRouterId(),input.getIntfLocalAddress(),
                input.getIntfRemoteAddress(),input.getIntfLabelVal());

        LOG.info("updateIntfLabel end");
        return RpcResultBuilder.success(updateIntfLabelOutputBuilder.build()).buildFuture();
    }
    @Override
    public Future<RpcResult<DelIntfLabelOutput>> delIntfLabel(DelIntfLabelInput input) {
        DelIntfLabelOutputBuilder delIntfLabelOutputBuilder = new DelIntfLabelOutputBuilder();
        LOG.info("delIntfLabel begin");
        if(SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(delIntfLabelOutputBuilder.build()).buildFuture();
        }else{
            this.getSrLabelApi();
        }
        LOG.info("delIntfLabel end");
        return RpcResultBuilder.success(delIntfLabelOutputBuilder.build()).buildFuture();
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
        LOG.info("getIntfLabel end");
        return RpcResultBuilder.success(getIntfLabelOutputBuilder.build()).buildFuture();
    }
}
