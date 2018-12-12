/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.NetconfSessionApi;
import cn.org.upbnc.service.NetconfSessionService;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.entity.NetconfSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NetconfSessionApiImpl implements NetconfSessionApi{
    private static final Logger LOG = LoggerFactory.getLogger(NetconfSessionApiImpl.class);
    private static NetconfSessionApi ourInstance = new NetconfSessionApiImpl();
    private ServiceInterface serviceInterface;
    private NetconfSessionService netconfSessionService;
    public static NetconfSessionApi getInstance() {
        return ourInstance;
    }
    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        if(null == serviceInterface){
            return false;
        }
        this.serviceInterface = serviceInterface;
        this.netconfSessionService = this.serviceInterface.getNetconfSessionService();
        return true;
    }

    @Override
    public boolean updateNetconfSession(String routerId, String deviceName, String deviceDesc, String deviceType, String deviceIP, Integer devicePort, String userName, String userPassword) {
        if((null == routerId)||(null == deviceName)||(null == deviceIP)||(0 == devicePort)) {
            return false;
        }
        boolean ret=this.netconfSessionService.updateNetconfSession(routerId, deviceName, deviceDesc, deviceType, deviceIP, devicePort, userName, userPassword);

        if(netconfSessionService.getNetconfSession(routerId).getStatus().equals("已连接")){
            String tmpRet=null;
            String result = null;
            result = "sync device configure start......";
            tmpRet=this.serviceInterface.getInterfaceService().syncInterfaceConf(routerId) ? " success":"failed";
            result +=  tmpRet;
            result += "\n";
            result += "sync vpnInstance configure....";
            tmpRet = this.serviceInterface.getVpnService().syncVpnInstanceConf(routerId) ? " success":"failed";
            result += tmpRet;
            result += "\n";
            result += "sync IntfLabel configure....";
            tmpRet = this.serviceInterface.getSrLabelService().syncIntfLabel(routerId) ? " success":"failed";
            result += tmpRet;
            result += "\n";
            result += "sync NodeLabel configure....";
            tmpRet = this.serviceInterface.getSrLabelService().syncNodeLabel(routerId) ? " success":"failed";
            result += tmpRet;
            result += "\n";
            result += "sync device configure end.";
        }

        return ret;

    }

    @Override
    public boolean delNetconfSession(String routerId) {
        if(null == routerId){
            return false;
        }
        return this.netconfSessionService.delNetconfSession(routerId);
    }

    @Override
    public NetconfSession getNetconfSession(String routerId) {
        if(null == routerId){
            return null;
        }
        return this.netconfSessionService.getNetconfSession(routerId);
    }



    @Override
    public List<NetconfSession> getNetconfSessionList() {
        return this.netconfSessionService.getNetconfSession();
    }
}
