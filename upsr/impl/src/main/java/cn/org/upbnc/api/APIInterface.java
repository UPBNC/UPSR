/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api;

import cn.org.upbnc.api.impl.TopoTestApiImpl;
import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.impl.BGPManagerImpl;
import cn.org.upbnc.base.impl.DeviceManagerImpl;
import cn.org.upbnc.base.impl.NetConfManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIInterface {
    private static final Logger LOG = LoggerFactory.getLogger(APIInterface.class);
    private TopoTestApi topoTestApi;
    public APIInterface(){

    }
    public void init(){
        try {
            LOG.info("APIInterface init Start...");
            this.topoTestApi = TopoTestApiImpl.getInstance();

            LOG.info("APIInterface init End!");
        }catch (Exception e){
            LOG.info("APIInterface init Failure!");
        }
    }

    public TopoTestApi getTopoTestApi() {
        return topoTestApi;
    }
}
