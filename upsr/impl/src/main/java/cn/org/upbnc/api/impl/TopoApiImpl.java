/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.TopoApi;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.impl.SRServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopoApiImpl implements TopoApi {
    private static final Logger LOG = LoggerFactory.getLogger(TopoApiImpl.class);
    private static TopoApi ourInstance = new TopoApiImpl();
    private ServiceInterface serviceInterface;
    public static TopoApi getInstance() {
        return ourInstance;
    }

    private TopoApiImpl() {
        // Init ServiceInterface
        this.serviceInterface = null;
    }

    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        boolean ret = false;
        try{
            this.serviceInterface = serviceInterface;
            this.serviceInterface.getSrService();
        }catch (Exception e){
            LOG.info(e.getMessage());
        }
        return false;
    }

    @Override
    public String getTest() {
        return null;
    }
}
