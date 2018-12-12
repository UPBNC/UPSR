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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.getallnetconf.output.Devices;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.getallnetconf.output.DevicesBuilder;

import org.opendaylight.yangtools.yang.binding.Augmentation;
import org.opendaylight.yangtools.yang.binding.DataContainer;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import java.util.LinkedList;
import java.util.List;
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
        if(this.netconfSessionApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                netconfSessionApi = apiInterface.getNetconfSessionApi();
            }
        }
        return this.netconfSessionApi;
    }

    public Future<RpcResult<GetAllNetconfOutput>> getAllNetconf()
    {
        DevicesBuilder devNetconfInfobuilder = null;
        List<NetconfSession> netconfSessionList = null;
        List<Devices> devNetconfInfoList = new LinkedList<Devices>();
        GetAllNetconfOutputBuilder netconfOutputBuilder = new GetAllNetconfOutputBuilder();

        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if(SystemStatusEnum.ON != this.session.getStatus()){
            netconfOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(netconfOutputBuilder.build()).buildFuture();
        }else{
            //调用系统Api层函数
            netconfSessionList = this.getNetconfSessionApi().getNetconfSessionList();
            if(null != netconfSessionList) {
                netconfOutputBuilder.setResult("success");
                for (NetconfSession netconfSession:netconfSessionList) {
                    devNetconfInfobuilder = new DevicesBuilder();
                    devNetconfInfobuilder.setDeviceName(netconfSession.getDeviceName());
                    devNetconfInfobuilder.setSysName(netconfSession.getSysName());
                    devNetconfInfobuilder.setCenterName(netconfSession.getDeviceDesc());
                    devNetconfInfobuilder.setDeviceType(netconfSession.getDeviceType());
                    devNetconfInfobuilder.setRouterId(netconfSession.getRouterId());
                    devNetconfInfobuilder.setSshIp(netconfSession.getDeviceIP());
                    devNetconfInfobuilder.setSshPort(netconfSession.getDevicePort());
                    devNetconfInfobuilder.setUserName(netconfSession.getUserName());
                    devNetconfInfobuilder.setPassword("");
                    devNetconfInfobuilder.setConnectStatus(netconfSession.getStatus());
                    devNetconfInfoList.add(devNetconfInfobuilder.build());
                }
                netconfOutputBuilder.setDevices(devNetconfInfoList);
                return RpcResultBuilder.success(netconfOutputBuilder.build()).buildFuture();
            }

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
            ret = this.getNetconfSessionApi().delNetconfSession(input.getRouterId());
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
            ret = this.getNetconfSessionApi().updateNetconfSession(input.getRouterId(),input.getDeviceName(),
                                            input.getCenterName(), input.getDeviceType(),input.getSshIp(), input.getSshPort(),
                                            input.getUserName(), input.getPassword());
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
            netconfSession = this.getNetconfSessionApi().getNetconfSession(input.getRouterId());
            if(null != netconfSession)
            {
                netconfOutputBuilder.setResult("success");
                netconfOutputBuilder.setRouterId(netconfSession.getRouterId());
                netconfOutputBuilder.setDeviceName(netconfSession.getDeviceName());
                netconfOutputBuilder.setCenterName(netconfSession.getDeviceDesc());
                netconfOutputBuilder.setDeviceType(netconfSession.getDeviceType());
                netconfOutputBuilder.setSysName(netconfSession.getSysName());
                netconfOutputBuilder.setSshIp(netconfSession.getDeviceIP());
                netconfOutputBuilder.setSshPort(netconfSession.getDevicePort());
                netconfOutputBuilder.setUserName(netconfSession.getDeviceName());
                netconfOutputBuilder.setPassword("");
                netconfOutputBuilder.setConnectStatus(netconfSession.getStatus());
                return RpcResultBuilder.success(netconfOutputBuilder.build()).buildFuture();
            }

        }
        //LOG.info("enter get netconf###");
        //以下是业务代码
        netconfOutputBuilder.setResult("failed");
        return RpcResultBuilder.success(netconfOutputBuilder.build()).buildFuture();
    }
}
