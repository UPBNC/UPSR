/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api;

import cn.org.upbnc.api.impl.BgplsSessionApiImpl;
import cn.org.upbnc.api.impl.TopoApiImpl;
import cn.org.upbnc.api.impl.TopoInfoApiImpl;
import cn.org.upbnc.api.impl.VpnInstanceApiImpl;
import cn.org.upbnc.service.ServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIInterface {
    private static final Logger LOG = LoggerFactory.getLogger(APIInterface.class);
    private ServiceInterface serviceInterface;
    private TopoApi topoTestApi;
    private TopoInfoApi topoInfoApi;
    private BgplsSessionApi bgplsSessionApi;
	 private VpnInstanceApi vpnInstanceApi;

    public APIInterface(){
        // Service Interface
        this.serviceInterface = null;

        // Init API
        this.topoTestApi = null;
        this.topoInfoApi = null;
        this.bgplsSessionApi = null;
		this.vpnInstanceApi = null;

    }

    public void init(){
        try {
            LOG.info("APIInterface init Start...");
            this.topoTestApi = TopoApiImpl.getInstance();
            this.topoInfoApi = TopoInfoApiImpl.getInstance();
            this.bgplsSessionApi = BgplsSessionApiImpl.getInstance();
			this.vpnInstanceApi = VpnInstanceApiImpl.getInstance();

            LOG.info("APIInterface init End!");
        }catch (Exception e){
            LOG.info("APIInterface init Failure!" + e.getMessage());
        }
    }

    public boolean setServiceInterface(ServiceInterface serviceInterface){
        boolean ret = false;
        try {
            this.serviceInterface = serviceInterface;
            ret = this.topoTestApi.setServiceInterface(this.serviceInterface);
            ret = ((true == ret )? this.vpnInstanceApi.setServiceInterface(this.serviceInterface):false);
        }catch (Exception e){
            LOG.info(e.getMessage());
        }
        return ret;
    }

    public TopoApi getTopoTestApi() {
        return topoTestApi;
    }
    public TopoInfoApi getTopoInfoApi() {
        return topoInfoApi;
    }

    public BgplsSessionApi getBgplsSessionApi() {
        return bgplsSessionApi;
    }
	public VpnInstanceApi getVpnInstanceApi()
    {
        return vpnInstanceApi;
    }
}
