/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;

import cn.org.upbnc.api.VpnInstanceApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.SystemStatusEnum;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import java.util.concurrent.Future;

public class VpnInstanceODLApi implements  VpnInstanceService{

    Session session;
    VpnInstanceApi vpnInstanceApi;

    public VpnInstanceODLApi(Session session){
        this.session = session;
    }

    private VpnInstanceApi getVpnInstanceApi(){
        if(this.vpnInstanceApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                vpnInstanceApi = apiInterface.getVpnInstanceApi();
            }
        }
        return this.vpnInstanceApi;
    }
    public Future<RpcResult<VpnInstanceOutput>> vpnInstance(VpnInstanceInput input)
    {
        VpnInstanceOutputBuilder vpnInstanceOutputBuilder = new VpnInstanceOutputBuilder();

        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if(SystemStatusEnum.ON != this.session.getStatus()){
            vpnInstanceOutputBuilder.setGreeting("System is not ready or shutdown");
            return RpcResultBuilder.success(vpnInstanceOutputBuilder.build()).buildFuture();
        }else{
            //调用系统Api层函数
            this.getVpnInstanceApi();

        }

        //以下是业务代码
        vpnInstanceOutputBuilder.setGreeting("Hello " + input.getName());

        // Call TopoTestAPI
        //this.topoTestApi.getTest();

        return RpcResultBuilder.success(vpnInstanceOutputBuilder.build()).buildFuture();
    }
}
