/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.odlapi;
import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.InterfaceApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.service.entity.DevInterfaceInfo;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrinterface.rev181119.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrinterface.rev181119.interfacelistinfo.*;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterfaceODLApi implements UpsrInterfaceService{
    private static final Logger LOG = LoggerFactory.getLogger(NetconfSessionODLApi.class);
    Session session;
    InterfaceApi interfaceApi;

    public InterfaceODLApi(Session session){
        this.session = session;
        this.interfaceApi = null;
    }

    private InterfaceApi getInterfaceApi(){
        if(this.interfaceApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                interfaceApi = apiInterface.getInterfaceApi();
            }
        }
        return this.interfaceApi;
    }
    public Future<RpcResult<GetInterfacesOutput>> getInterfaces(GetInterfacesInput input)
    {
        DevInterfacesBuilder devInterfacesBuilder = null;
        List<DevInterfaces> devInterfaces = new LinkedList<DevInterfaces>();
        List<DevInterfaceInfo> devInterfaceInfos = null;
        GetInterfacesOutputBuilder getInterfacesOutputBuilder = new GetInterfacesOutputBuilder();
        if(SystemStatusEnum.ON != this.session.getStatus()){
            getInterfacesOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(getInterfacesOutputBuilder.build()).buildFuture();
        }else{
            getInterfacesOutputBuilder.setResult("success");
            //调用系统Api层函数
            devInterfaceInfos = this.getInterfaceApi().getDeviceInterfaceList(input.getRouterId());
            if(null != devInterfaceInfos) {
                for (DevInterfaceInfo  devInterfaceInfo:devInterfaceInfos) {
                    devInterfacesBuilder = new DevInterfacesBuilder();
                    devInterfacesBuilder.setIfnetName(devInterfaceInfo.getIfnetName());
                    devInterfacesBuilder.setIfnetIP(devInterfaceInfo.getIfnetIP());
                    devInterfacesBuilder.setIfnetMask(devInterfaceInfo.getIfnetMask());
                    devInterfacesBuilder.setIfnetMac(devInterfaceInfo.getIfnetMac());
                    devInterfacesBuilder.setVpnName(devInterfaceInfo.getVpnName());
                    devInterfacesBuilder.setIfnetStatus(devInterfaceInfo.getIfnetStatus());
                    devInterfacesBuilder.setSrStatus(devInterfaceInfo.getSrStatus());
                    devInterfacesBuilder.setLinkStatus(devInterfaceInfo.getLinkStatus());
                    devInterfacesBuilder.setRunningStatus(devInterfaceInfo.getRunningStatus());
                    devInterfaces.add(devInterfacesBuilder.build());
                }
                getInterfacesOutputBuilder.setDevInterfaces(devInterfaces);
                return RpcResultBuilder.success(getInterfacesOutputBuilder.build()).buildFuture();
            }
        }
        LOG.info("enter all getInterfaces###");
        //以下是业务代码
        getInterfacesOutputBuilder.setResult("failed");
        return RpcResultBuilder.success(getInterfacesOutputBuilder.build()).buildFuture();
    }
}
