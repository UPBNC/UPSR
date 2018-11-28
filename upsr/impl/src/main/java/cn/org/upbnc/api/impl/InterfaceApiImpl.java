/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.InterfaceApi;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.entity.DevInterfaceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class InterfaceApiImpl implements InterfaceApi{
    private static final Logger LOG = LoggerFactory.getLogger(NetconfSessionApiImpl.class);
    private static InterfaceApi ourInstance = new InterfaceApiImpl();
    private ServiceInterface serviceInterface;

    public static InterfaceApi getInstance() {
        return ourInstance;
    }

    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        if(null == serviceInterface){
            return false;
        }
        this.serviceInterface = serviceInterface;
        return true;
    }

    @Override
    public List<DevInterfaceInfo> getDeviceInterfaceList(String routerId) {
        return this.serviceInterface.getInterfaceService().getInterfaceListFromDevice(routerId);
    }
}
