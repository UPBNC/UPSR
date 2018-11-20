/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api;

import cn.org.upbnc.api.impl.TopoApiImpl;
import cn.org.upbnc.service.ServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIInterface {
    private static final Logger LOG = LoggerFactory.getLogger(APIInterface.class);
    private ServiceInterface serviceInterface;
    private TopoApi topoTestApi;

    public APIInterface(){
        // Service Interface
        this.serviceInterface = null;

        // Init API
        this.topoTestApi = null;
    }

    public void init(){
        try {
            LOG.info("APIInterface init Start...");
            this.topoTestApi = TopoApiImpl.getInstance();

            LOG.info("APIInterface init End!");
        }catch (Exception e){
            LOG.info("APIInterface init Failure!");
        }
    }

    public boolean setServiceInterface(ServiceInterface serviceInterface){
        boolean ret = false;
        try {
            this.serviceInterface = serviceInterface;
            ret = this.topoTestApi.setServiceInterface(this.serviceInterface);
        }catch (Exception e){
            LOG.info(e.getMessage());
        }
        return ret;
    }

    public TopoApi getTopoTestApi() {
        return topoTestApi;
    }
}
