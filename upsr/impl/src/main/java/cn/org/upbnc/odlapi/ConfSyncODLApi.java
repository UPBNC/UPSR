/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.ConfSyncApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsyncconf.rev181129.SyncConfOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsyncconf.rev181129.SyncConfOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsyncconf.rev181129.UpsrSyncConfService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

public class ConfSyncODLApi implements UpsrSyncConfService {
    private static final Logger LOG = LoggerFactory.getLogger(NetconfSessionODLApi.class);
    Session session;
    ConfSyncApi confSyncApi;

    public ConfSyncODLApi(Session session){
        this.session = session;
        this.confSyncApi = null;
    }
    private ConfSyncApi getConfSyncApi(){
        if(this.confSyncApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                confSyncApi = apiInterface.getConfSyncApi();
            }
        }
        return this.confSyncApi;
    }
    public Future<RpcResult<SyncConfOutput>> syncConf() {
        String result = null;
        SyncConfOutputBuilder syncConfOutputBuilder = new SyncConfOutputBuilder();

        if(SystemStatusEnum.ON != this.session.getStatus()){
            syncConfOutputBuilder.setResult("System is not ready or shutdown");

        }else{
            //调用系统Api层函数
            result = this.getConfSyncApi().syncDeviceConf();
            syncConfOutputBuilder.setResult(result);
        }
        return RpcResultBuilder.success(syncConfOutputBuilder.build()).buildFuture();
    }
}
