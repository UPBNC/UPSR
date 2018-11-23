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
    public boolean updateNetconfSession(String deviceName, String deviceDesc, String deviceIP, Integer devicePort, String userName, String userPassword) {
        if((null == deviceName)||(null == deviceIP)||(0 == devicePort)) {
            return false;
        }
        return this.netconfSessionService.updateNetconfSession(deviceName, deviceDesc, deviceIP, devicePort, userName, userPassword);
    }

    @Override
    public boolean delNetconfSession(String deviceName) {
        if(null == deviceName){
            return false;
        }
        return this.netconfSessionService.delNetconfSession(deviceName);
    }

    @Override
    public boolean delNetconfSession(String deviceIP, Integer devicePort) {
        if((null == deviceIP)|| (0 == devicePort)){
            return false;
        }
        return this.netconfSessionService.delNetconfSessionByIP(deviceIP);
    }

    @Override
    public NetconfSession getNetconfSession(String deviceName) {
        if(null == deviceName){
            return null;
        }
        return this.netconfSessionService.getNetconfSession(deviceName);
    }

    @Override
    public NetconfSession getNetconfSession(String deviceIP, Integer devicePort) {
        if((null == deviceIP)|| (0 == devicePort)){
            return null;
        }
        return this.netconfSessionService.getNetconfSessionByIP(deviceIP);
    }

    @Override
    public List<NetconfSession> getNetconfSessionList(String deviceName) {
        return null;
    }
}
