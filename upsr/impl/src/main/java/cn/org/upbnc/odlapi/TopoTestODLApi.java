/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.odlapi;

import java.util.concurrent.Future;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.TopoTestApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topo.rev181119.TopoInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topo.rev181119.TopoOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topo.rev181119.TopoOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topo.rev181119.TopoService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

//注入给ODL的API，调用我们的API
public class TopoTestODLApi implements TopoService {

    Session session;
    TopoTestApi topoTestApi;

    public TopoTestODLApi(Session session){
        this.session = session;
    }

    private TopoTestApi getTopoTestApi(){
        if(this.topoTestApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                topoTestApi = apiInterface.getTopoTestApi();
            }
        }
        return this.topoTestApi;
    }
    @Override
    public Future<RpcResult<TopoOutput>> topo(TopoInput input) {
        TopoOutputBuilder topoOutputBuilder = new TopoOutputBuilder();

        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if(SystemStatusEnum.ON != this.session.getStatus()){
            topoOutputBuilder.setGreeting("System is not ready or shutdown");
            return RpcResultBuilder.success(topoOutputBuilder.build()).buildFuture();
        }else{
            //调用系统Api层函数
            this.getTopoTestApi();
        }

        //以下是业务代码
        topoOutputBuilder.setGreeting("Hello " + input.getName());

        // Call TopoTestAPI
        //this.topoTestApi.getTest();

        return RpcResultBuilder.success(topoOutputBuilder.build()).buildFuture();
    }
}
