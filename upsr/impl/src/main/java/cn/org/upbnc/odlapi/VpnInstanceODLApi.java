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
import cn.org.upbnc.entity.VPNInstance;
import cn.org.upbnc.enumtype.SystemStatusEnum;

import cn.org.upbnc.service.ServiceInterface;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceGetInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceGetOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceGetOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceDelInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceDelOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceDelOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceUpdateInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceUpdateOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceUpdateOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpninstance.rev181119.VpnInstanceService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VpnInstanceODLApi implements  VpnInstanceService{
    private static final Logger LOG = LoggerFactory.getLogger(VpnInstanceODLApi.class);
    Session session;
    VpnInstanceApi vpnInstanceApi;

    public VpnInstanceODLApi(Session session){
        this.session = session;
    }

    private VpnInstanceApi getVpnInstanceApi(){
        //LOG.info("enter getVpnInstanceApi");
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
        //LOG.info("enter vpnInstance###");
        //以下是业务代码
        vpnInstanceOutputBuilder.setGreeting("Hello " + input.getName());

        // Call TopoTestAPI
        //this.topoTestApi.getTest();

        return RpcResultBuilder.success(vpnInstanceOutputBuilder.build()).buildFuture();
    }


    public Future<RpcResult<VpnInstanceGetOutput>> vpnInstanceGet(VpnInstanceGetInput input)
    {
        VPNInstance vpnInstance = null;
        VpnInstanceGetOutputBuilder vpnInstanceGetOutputBuilder = new VpnInstanceGetOutputBuilder();
        LOG.info("enter vpnInstanceGet-01");
        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if(SystemStatusEnum.ON != this.session.getStatus()){
            vpnInstanceGetOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(vpnInstanceGetOutputBuilder.build()).buildFuture();
        }else{
            LOG.info("enter vpnInstanceGet-02");
            //调用系统Api层函数
            vpnInstance = this.getVpnInstanceApi().getVpnInstance(input.getVpnName());
            if(null != vpnInstance) {
                vpnInstanceGetOutputBuilder.setResult("success");
                vpnInstanceGetOutputBuilder.setVpnName(vpnInstance.getVpnName());
                vpnInstanceGetOutputBuilder.setRouteId(vpnInstance.getRd());
                vpnInstanceGetOutputBuilder.setImportRT(vpnInstance.getImportRT());
                vpnInstanceGetOutputBuilder.setExportRT(vpnInstance.getExportRT());
                //vpnInstanceGetOutputBuilder.setPeerAS(vpnInstance.getPeerAS());
                return RpcResultBuilder.success(vpnInstanceGetOutputBuilder.build()).buildFuture();
            }
        }

        //以下是业务代码
        //vpnInstanceGetOutputBuilder.setResult("Hello " + input.getVpnName());
        LOG.info("enter vpnInstanceGet-03");
        // Call TopoTestAPI
        //this.topoTestApi.getTest();
        vpnInstanceGetOutputBuilder.setResult("failed");
        return RpcResultBuilder.success(vpnInstanceGetOutputBuilder.build()).buildFuture();
    }

    public Future<RpcResult<VpnInstanceDelOutput>> vpnInstanceDel(VpnInstanceDelInput input)
    {
        boolean ret = false;
        VpnInstanceDelOutputBuilder vpnInstanceDelOutputBuilder = new VpnInstanceDelOutputBuilder();
        LOG.info("enter vpnInstanceDel-01");
        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if(SystemStatusEnum.ON != this.session.getStatus()){
            vpnInstanceDelOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(vpnInstanceDelOutputBuilder.build()).buildFuture();
        }else{
            LOG.info("enter vpnInstanceDel-02");
            //调用系统Api层函数
            ret = this.getVpnInstanceApi().delVpnInstance(input.getVpnName());
            if(true == ret)
            {
                vpnInstanceDelOutputBuilder.setResult("success");
                return RpcResultBuilder.success(vpnInstanceDelOutputBuilder.build()).buildFuture();
            }

        }
        LOG.info("enter vpnInstanceDel-03");
        //以下是业务代码
        vpnInstanceDelOutputBuilder.setResult("failed");
        // Call TopoTestAPI
        //this.topoTestApi.getTest();

        return RpcResultBuilder.success(vpnInstanceDelOutputBuilder.build()).buildFuture();
    }

    public Future<RpcResult<VpnInstanceUpdateOutput>> vpnInstanceUpdate(VpnInstanceUpdateInput input)
    {
        boolean ret = false;
        VpnInstanceUpdateOutputBuilder vpnInstanceUpdateOutputBuilder = new VpnInstanceUpdateOutputBuilder();
        LOG.info("enter vpnInstanceUpdate-01");
        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if(SystemStatusEnum.ON != this.session.getStatus()){
            vpnInstanceUpdateOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(vpnInstanceUpdateOutputBuilder.build()).buildFuture();
        }else{
            LOG.info("enter vpnInstanceUpdate-02");
            LOG.info("enter vpnInstanceUpdate vpnName={} rd={}",new Object[]{input.getVpnName(), input.getRouteId()});
            //调用系统Api层函数
            ret = this.getVpnInstanceApi().updateVpnInstance(input.getVpnName(),
                                            null,
                                            null,
                                            input.getRouteId(),
                                            input.getImportRT(),
                                            input.getExportRT(),
                                            input.getPeerAS(),
                                            null,
                                            null,
                                            null,
                                            null,
                                            null
                                            );
            LOG.info("enter vpnInstanceUpdate ret={}",new Object[]{ ret });
            if(true == ret)
            {
                vpnInstanceUpdateOutputBuilder.setResult("success");
                return RpcResultBuilder.success(vpnInstanceUpdateOutputBuilder.build()).buildFuture();
            }
        }
        LOG.info("enter vpnInstanceUpdate-03");
        //以下是业务代码
        vpnInstanceUpdateOutputBuilder.setResult("failed-01");

        // Call TopoTestAPI
        //this.topoTestApi.getTest();

        return RpcResultBuilder.success(vpnInstanceUpdateOutputBuilder.build()).buildFuture();
    }
}
