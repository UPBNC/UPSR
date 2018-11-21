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
import cn.org.upbnc.service.TopoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopoApiImpl implements TopoApi {
    private static final Logger LOG = LoggerFactory.getLogger(TopoApiImpl.class);
    private static TopoApi ourInstance = new TopoApiImpl();
    private ServiceInterface serviceInterface;
    private TopoService topoService;
    public static TopoApi getInstance() {
        return ourInstance;
    }

    private TopoApiImpl() {
        // Init ServiceInterface
        this.serviceInterface = null;
        this.topoService = null;
    }

    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        boolean ret = false;
        try{
            if(null != serviceInterface) {
                this.serviceInterface = serviceInterface;
                this.topoService = serviceInterface.getTopoService();
            }
            ret = true;
        }catch (Exception e){
            ret = false;
            LOG.info(e.getMessage());
        }
        return ret;
    }

    @Override
    public String getTest() {
        this.topoService.test();
        return "Test";
    }


}
