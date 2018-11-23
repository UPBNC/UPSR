/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.NetconfSessionApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.service.entity.NetconfSession;
/*
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.GetNetconfInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.GetNetconfOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.GetNetconfOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.GetAllNetconfInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.GetAllNetconfOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.GetAllNetconfOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.DelNetconfInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.DelNetconfOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.DelNetconfOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.UpdateNetconfInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.UpdateNetconfOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.UpdateNetconfOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.UpsrNetconfSessionService;
*/
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.*;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetconfSessionODLApi  implements UpsrNetconfSessionService {

    private static final Logger LOG = LoggerFactory.getLogger(NetconfSessionODLApi.class);
    Session session;
    NetconfSessionApi netconfSessionApi;

    public NetconfSessionODLApi(Session session){
        this.session = session;
        this.netconfSessionApi = null;
    }

    private NetconfSessionApi getNetconfSessionApi(){
        //LOG.info("enter getVpnInstanceApi");
        if(this.netconfSessionApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                netconfSessionApi = apiInterface.getNetconfSessionApi();
            }
        }
        return this.netconfSessionApi;
    }

    public Future<RpcResult<GetAllNetconfOutput>> getAllNetconf(GetAllNetconfInput input)
    {
        GetAllNetconfOutputBuilder netconfOutputBuilder = new GetAllNetconfOutputBuilder();

        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if(SystemStatusEnum.ON != this.session.getStatus()){
            netconfOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(netconfOutputBuilder.build()).buildFuture();
        }else{
            //调用系统Api层函数
            this.getNetconfSessionApi();

        }
        LOG.info("enter all netconf###");
        //以下是业务代码
        netconfOutputBuilder.setResult("failed");

        return RpcResultBuilder.success(netconfOutputBuilder.build()).buildFuture();
    }


    public Future<RpcResult<DelNetconfOutput>> delNetconf(DelNetconfInput input)
    {
        boolean ret = false;
        DelNetconfOutputBuilder netconfOutputBuilder = new DelNetconfOutputBuilder();

        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if(SystemStatusEnum.ON != this.session.getStatus()){
            netconfOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(netconfOutputBuilder.build()).buildFuture();
        }else{
            //调用系统Api层函数
            ret = this.getNetconfSessionApi().delNetconfSession(input.getDeviceName());
            if(true == ret)
            {
                netconfOutputBuilder.setResult("success");
                return RpcResultBuilder.success(netconfOutputBuilder.build()).buildFuture();
            }

        }
        LOG.info("enter delete netconf###");
        //以下是业务代码
        netconfOutputBuilder.setResult("failed");
        return RpcResultBuilder.success(netconfOutputBuilder.build()).buildFuture();
    }


    public Future<RpcResult<UpdateNetconfOutput>> updateNetconf(UpdateNetconfInput input)
    {
        boolean ret = false;
        UpdateNetconfOutputBuilder netconfOutputBuilder = new UpdateNetconfOutputBuilder();

        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if(SystemStatusEnum.ON != this.session.getStatus()){
            netconfOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(netconfOutputBuilder.build()).buildFuture();
        }else{
            //调用系统Api层函数
            ret = this.getNetconfSessionApi().updateNetconfSession(input.getDeviceName(),
                                            input.getDeviceDesc(), input.getDeviceIP(), input.getDevicePort(),
                                            input.getUserName(), input.getUserPassword());
            if(true == ret)
            {
                netconfOutputBuilder.setResult("success");
                return RpcResultBuilder.success(netconfOutputBuilder.build()).buildFuture();
            }

        }
        LOG.info("enter update netconf###");
        //以下是业务代码
        netconfOutputBuilder.setResult("failed");
        return RpcResultBuilder.success(netconfOutputBuilder.build()).buildFuture();
    }

    /**
     * @return <code>java.util.concurrent.Future</code> <code>netconf</code>, or <code>null</code> if not present
     */

    public Future<RpcResult<GetNetconfOutput>> getNetconf(GetNetconfInput input)
    {
        NetconfSession netconfSession = null;
        GetNetconfOutputBuilder netconfOutputBuilder = new GetNetconfOutputBuilder();

        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if(SystemStatusEnum.ON != this.session.getStatus()){
            netconfOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(netconfOutputBuilder.build()).buildFuture();
        }else{
            //调用系统Api层函数
            netconfSession = this.getNetconfSessionApi().getNetconfSession(input.getDeviceName());
            if(null != netconfSession)
            {
                netconfOutputBuilder.setResult("success");
                netconfOutputBuilder.setDeviceName(netconfSession.getDeviceName());
                netconfOutputBuilder.setDeviceDesc(netconfSession.getDeviceDesc());
                netconfOutputBuilder.setSysName(netconfSession.getSysName());
                netconfOutputBuilder.setDeviceIP(netconfSession.getDeviceIP());
                netconfOutputBuilder.setDevicePort(netconfSession.getDevicePort());
                netconfOutputBuilder.setUserName(netconfSession.getDeviceName());
                netconfOutputBuilder.setStatus(netconfSession.getStatus());
                return RpcResultBuilder.success(netconfOutputBuilder.build()).buildFuture();
            }

        }
        //LOG.info("enter get netconf###");
        //以下是业务代码
        netconfOutputBuilder.setResult("failed");
        return RpcResultBuilder.success(netconfOutputBuilder.build()).buildFuture();
    }
}
