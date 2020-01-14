/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.NetconfSessionApi;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.service.NetconfSessionService;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.entity.NetconfSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class NetconfSessionApiImpl implements NetconfSessionApi {
    private static final Logger LOG = LoggerFactory.getLogger(NetconfSessionApiImpl.class);
    private static NetconfSessionApi ourInstance = new NetconfSessionApiImpl();
    private ServiceInterface serviceInterface;
    private NetconfSessionService netconfSessionService;
    private String disConnected = "未连接";
    private String connected = "已连接";

    public static NetconfSessionApi getInstance() {
        return ourInstance;
    }

    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        if (null == serviceInterface) {
            return false;
        }
        this.serviceInterface = serviceInterface;
        this.netconfSessionService = this.serviceInterface.getNetconfSessionService();
        return true;
    }

    @Override
    public Map<String, Object> updateNetconfSession(NetconfSession netconfSession) {
        String routerId = netconfSession.getRouterId();
        String deviceIP = netconfSession.getDeviceIP();
        Integer devicePort = netconfSession.getDevicePort();
        String password = netconfSession.getUserPassword();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        resultMap.put(ResponseEnum.BODY.getName(), false);
        boolean flag = false;
        String errorMsg = "";
        if ((null == routerId) || routerId.isEmpty()) {
            errorMsg += "rouoterId is empty.";
            flag = true;
        }
        if ((null == password) || password.isEmpty()) {
            errorMsg += "password is empty";
            flag = true;
        }
        if ((null == deviceIP) || deviceIP.isEmpty() || (null == devicePort)) {
            errorMsg += "deviceIP is empty.";
            flag = true;
        }
        if (null == devicePort) {
            errorMsg += "devicePort is empty.";
            flag = true;
        }
        if (flag) {
            resultMap.put(ResponseEnum.MESSAGE.getName(), errorMsg);
            return resultMap;
        }
        boolean isSyn = this.netconfSessionService.isSyn(netconfSession);
        Map<String, Object> map = this.netconfSessionService.updateNetconfSession(netconfSession);
        boolean ret = (boolean) map.get(ResponseEnum.BODY.getName());
        String msg = (String) map.get(ResponseEnum.MESSAGE.getName());
        String result = null;
        if (ret) {
            if (((NetconfSession) netconfSessionService.getNetconfSession(routerId).get(ResponseEnum.BODY.getName())).getStatus().equals(connected)) {
                if (isSyn) {
                    result = sync(routerId);
                }
            }
            resultMap.put(ResponseEnum.MESSAGE.getName(), "success");
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
            resultMap.put(ResponseEnum.BODY.getName(), true);
        } else {
            resultMap.put(ResponseEnum.MESSAGE.getName(), msg);
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.BODY.getName(), false);
        }

        return resultMap;
    }

    private String sync(String routerId) {
        String tmpRet = "sync device configure start......";
        tmpRet += this.serviceInterface.getInterfaceService().syncInterfaceConf(routerId) ? " success" : "failed";
        tmpRet += "\n";
        tmpRet += "sync vpnInstance configure....";
        tmpRet += this.serviceInterface.getVpnService().syncVpnInstanceConf(routerId) ? " success" : "failed";
        tmpRet += "\n";
        tmpRet += "sync IntfLabel configure....";
        tmpRet += this.serviceInterface.getSrLabelService().syncIntfLabel(routerId) ? " success" : "failed";
        tmpRet += "\n";
        tmpRet += "sync NodeLabel configure....";
        tmpRet += this.serviceInterface.getSrLabelService().syncNodeLabel(routerId) ? " success" : "failed";
        tmpRet += "\n";
        tmpRet += this.serviceInterface.getTunnelService().syncTunnelInstanceConf(routerId) ? " success" : "failed";
        tmpRet += "\n";
        tmpRet += "sync tunnel configure....";
//        tmpRet += "sync traffic policy configure....";
//        tmpRet += this.serviceInterface.getTrafficPolicyService().syncTrafficPolicyConf(routerId) ? " success" : "failed";
        tmpRet += "\n";

        tmpRet += "sync device configure end.";
        return tmpRet;
    }


    @Override
    public Map<String, Object> delNetconfSession(String routerId) {
        Map<String, Object> resultMap = new HashMap<>();
        if (null == routerId || routerId.isEmpty()) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), "routerId is null or empty");
            resultMap.put(ResponseEnum.BODY.getName(), false);
            return resultMap;
        }
        return this.netconfSessionService.delNetconfSession(routerId);
    }

    @Override
    public Map<String, Object> getNetconfSession(String routerId) {
        if (null == routerId) {
            return null;
        }
        return this.netconfSessionService.getNetconfSession(routerId);
    }


    @Override
    public Map<String, Object> getNetconfSessionList() {
        return this.netconfSessionService.getNetconfSession();
    }

    @Override
    public boolean reconnectNetconfSession(String routerId) {
        boolean flag = this.netconfSessionService.reconnectNetconfSession(routerId);
        if (flag) {
            String result = sync(routerId);
            LOG.info("syn result:" + result);
        }
        return flag;
    }

    @Override
    public void close() {
        this.netconfSessionService.close();
    }
}
