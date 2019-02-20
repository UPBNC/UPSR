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
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.SrStatusEnum;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.util.xml.SrLabelXml;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsrlabel.rev181126.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsrlabel.rev181126.updatesrlabel.input.IntfLabel;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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
        if (input.getSrEnabled().equals(SrStatusEnum.DISENABLED.getName())) {
            updateSrLabelOutputBuilder.setResult(this.disableSrLabel(input));
        } else if (input.getSrEnabled().equals(SrStatusEnum.ENABLED.getName())) {
            updateSrLabelOutputBuilder.setResult(this.enableSrLabel(input));
        }
        LOG.info("updateSrLabel end");
        return RpcResultBuilder.success(updateSrLabelOutputBuilder.build()).buildFuture();
    }
    private String disableSrLabel(UpdateSrLabelInput input) {
        String resultString = "";
        List<IntfLabel> intfLabelList = input.getIntfLabel();
        Map<String, Object> updateLabelRet = srLabelApi.updateNodeLabel(input.getRouterId(), input.getSrgbPrefixSid().getSrgbBegin(),
                input.getSrgbPrefixSid().getPrefixId(),SrLabelXml.ncOperationDelete);
        if (updateLabelRet.get(ResponseEnum.CODE.getName()) != CodeEnum.SUCCESS.getName()) {
            resultString = resultString + updateLabelRet.get(ResponseEnum.MESSAGE.getName());
        }
        Map<String, Object> updateLabelRangeRet = srLabelApi.updateNodeLabelRange(input.getRouterId(), input.getSrgbPrefixSid().getSrgbBegin(),
                input.getSrgbPrefixSid().getSrgbEnd(), SrLabelXml.ncOperationDelete);
        if (updateLabelRangeRet.get(ResponseEnum.CODE.getName()) != CodeEnum.SUCCESS.getName()) {
            resultString = resultString + updateLabelRangeRet.get(ResponseEnum.MESSAGE.getName());
        }
        Iterator<IntfLabel> intfLabelIterator = intfLabelList.iterator();
        while (intfLabelIterator.hasNext()) {
            IntfLabel intfLabel = intfLabelIterator.next();
            Map<String, Object> updateIntfLabelRet = srLabelApi.updateIntfLabel(input.getRouterId(), intfLabel.getIfAddress(),
                    intfLabel.getAdjlabel(), SrLabelXml.ncOperationDelete);
            if (updateIntfLabelRet.get(ResponseEnum.CODE.getName()) != CodeEnum.SUCCESS.getName()) {
                resultString = resultString + updateIntfLabelRet.get(ResponseEnum.MESSAGE.getName());
            }
        }
        LOG.info(resultString);
        return CodeEnum.SUCCESS.getMessage();
    }
    private String enableSrLabel(UpdateSrLabelInput input) {
        String resultString = "";
        List<IntfLabel> intfLabelList = input.getIntfLabel();
        Map<String, Object> updateLabelRangeRet = srLabelApi.updateNodeLabelRange(input.getRouterId(), input.getSrgbPrefixSid().getSrgbBegin(),
                input.getSrgbPrefixSid().getSrgbEnd(), SrLabelXml.ncOperationMerge);
        if (updateLabelRangeRet.get(ResponseEnum.CODE.getName()) != CodeEnum.SUCCESS.getName()) {
            resultString = resultString + updateLabelRangeRet.get(ResponseEnum.MESSAGE.getName());
        }
        Map<String, Object> updateLabelRet = srLabelApi.updateNodeLabel(input.getRouterId(), input.getSrgbPrefixSid().getSrgbBegin(),
                input.getSrgbPrefixSid().getPrefixId(), SrLabelXml.ncOperationMerge);
        if (updateLabelRet.get(ResponseEnum.CODE.getName()) != CodeEnum.SUCCESS.getName()) {
            resultString = resultString + updateLabelRet.get(ResponseEnum.MESSAGE.getName());
        }
        Iterator<IntfLabel> intfLabelIterator = intfLabelList.iterator();
        while (intfLabelIterator.hasNext()) {
            IntfLabel intfLabel = intfLabelIterator.next();
            Map<String, Object> updateIntfLabelRet = null;
            if (intfLabel.getSrEnabled().equals(SrStatusEnum.DISENABLED.getName())) {
                updateIntfLabelRet = srLabelApi.updateIntfLabel(input.getRouterId(), intfLabel.getIfAddress(),
                        intfLabel.getAdjlabel(), SrLabelXml.ncOperationDelete);
            } else {
                updateIntfLabelRet = srLabelApi.updateIntfLabel(input.getRouterId(), intfLabel.getIfAddress(),
                        intfLabel.getAdjlabel(), SrLabelXml.ncOperationMerge);
            }
            if (updateIntfLabelRet.get(ResponseEnum.CODE.getName()) != CodeEnum.SUCCESS.getName()) {
                resultString = resultString + updateIntfLabelRet.get(ResponseEnum.MESSAGE.getName());
            }
        }
        LOG.info(resultString);
        if(resultString.equals("") != true) {
            return resultString;
        }
        return CodeEnum.SUCCESS.getMessage();
    }
}
