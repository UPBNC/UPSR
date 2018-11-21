/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.TopoInfoApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.impl.UpsrProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.linkinfo.Links;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.linkinfo.LinksBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.linkinfo.links.DestBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.linkinfo.links.SourceBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class TopoInfoODLApi implements UpsrTopoService {
    private static final Logger LOG = LoggerFactory.getLogger(UpsrProvider.class);
    Session session;
    TopoInfoApi topoInfoApi;

    public TopoInfoODLApi(Session session){
        this.session = session;
    }

    private TopoInfoApi getTopoInfoApi(){
        if(this.topoInfoApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                topoInfoApi = apiInterface.getTopoInfoApi();
            }
        }
        return this.topoInfoApi;
    }
    @Override
    public Future<RpcResult<GetNodesOutput>> getNodes(GetNodesInput input) {
        return null;
    }

    @Override
    public Future<RpcResult<GetTopoOutput>> getTopo() {
        return null;
    }

    @Override
    public Future<RpcResult<GetLinksOutput>> getLinks(GetLinksInput input) {
        /*
        http://localhost:8181/restconf/operations/upsrTopo:getLinks  :  {"input": { "linkId":"sunxasss"}}
         */
        GetLinksOutputBuilder getLinksOutputBuilder = new GetLinksOutputBuilder();
        LOG.info("getLinks begin");
        if(SystemStatusEnum.ON != this.session.getStatus()){
            return RpcResultBuilder.success(getLinksOutputBuilder.build()).buildFuture();
        }else{
            this.getTopoInfoApi();
        }
        getLinksOutputBuilder.setLinks(topoInfoApi.getLinks());
        LOG.info("getLinks end");
        return  RpcResultBuilder.success(getLinksOutputBuilder.build()).buildFuture();
    }
}
