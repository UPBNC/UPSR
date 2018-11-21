/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.BgplsSessionApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.impl.UpsrProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrbgplssession.rev181120.*;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

public class BgplsSessionODLApi implements UpsrBgplsSessionService{
    private static final Logger LOG = LoggerFactory.getLogger(UpsrProvider.class);
    Session session;
    BgplsSessionApi bgplsSessionApi;

    public BgplsSessionODLApi(Session session) {
        this.session = session;
    }

    private BgplsSessionApi getBgplsSessionApi(){
        if(this.bgplsSessionApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null){
                this.bgplsSessionApi = apiInterface.getBgplsSessionApi();
            }
        }
        return this.bgplsSessionApi;
    }

    @Override
    public Future<RpcResult<GetBgplsOutput>> getBgpls() {
        /*
        http://localhost:8181/restconf/operations/upsrBgplsSession:getBgpls
         */
        GetBgplsOutputBuilder getBgplsOutputBuilder = new GetBgplsOutputBuilder();
        LOG.info("getBgpls begin");
        if(SystemStatusEnum.ON != this.session.getStatus()){
            return  RpcResultBuilder.success(getBgplsOutputBuilder.build()).buildFuture();
        } else {
            this.getBgplsSessionApi();
        }
        getBgplsOutputBuilder.setSessionName("BGP Session");
        getBgplsOutputBuilder.setBgpAs("100");
        getBgplsOutputBuilder.setBgpIp("3.3.3.3");
        getBgplsOutputBuilder.setBgpStatus("UP");
        LOG.info("getBgpls end");
        return  RpcResultBuilder.success(getBgplsOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<AddBgplsOutput>> addBgpls(AddBgplsInput input) {
        /*
        http://localhost:8181/restconf/operations/upsrBgplsSession:addBgpls  :  {"input": { "bgpIp":"sunxasss","bgpAs":"100"}}
         */
        AddBgplsOutputBuilder addBgplsOutputBuilder = new AddBgplsOutputBuilder();
        LOG.info("addBgpls begin");
        addBgplsOutputBuilder.setRetCode("ok");
        addBgplsOutputBuilder.setRetMsg(input.getBgpIp()+input.getBgpAs());
        LOG.info("addBgpls end");
        return RpcResultBuilder.success(addBgplsOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<DelBgplsOutput>> delBgpls(DelBgplsInput input) {
        /*
        http://localhost:8181/restconf/operations/upsrBgplsSession:delBgpls  :  {"input": { "bgpIp":"sunxasss"}}
        */
        DelBgplsOutputBuilder delBgplsOutputBuilder = new DelBgplsOutputBuilder();
        LOG.info("delBgpls begin");
        delBgplsOutputBuilder.setRetCode("ok");
        delBgplsOutputBuilder.setRetMsg("delete suc : " + input.getBgpIp());
        LOG.info("delBgpls end");
        return RpcResultBuilder.success(delBgplsOutputBuilder.build()).buildFuture();
    }
}
