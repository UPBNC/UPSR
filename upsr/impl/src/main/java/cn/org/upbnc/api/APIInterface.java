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
import cn.org.upbnc.util.UtilInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIInterface {
    private static final Logger LOG = LoggerFactory.getLogger(APIInterface.class);
    private ServiceInterface serviceInterface;
    private UtilInterface utilInterface;
    private TopoApi topoTestApi;
    private TopoInfoApi topoInfoApi;
    private BgplsSessionApi bgplsSessionApi;
    private VpnInstanceApi vpnInstanceApi;

    public APIInterface(){
        // Service Interface
        this.serviceInterface = null;
        this.utilInterface = null;

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

    // 安装业务服务接口
    public boolean setServiceInterface(ServiceInterface serviceInterface){
        boolean ret = false;
        try {
            this.serviceInterface = serviceInterface;
            ret = true;
            ret = this.topoTestApi.setServiceInterface(this.serviceInterface);
            ret = ((true == ret )? this.vpnInstanceApi.setServiceInterface(this.serviceInterface):false);
        }catch (Exception e){
            ret = false;
            LOG.info(e.getMessage());
        }
        return ret;
    }

    // 安装工具系统
    public boolean setUtilInterface(UtilInterface utilInterface) {
        boolean ret = false;
        try {
            this.utilInterface = utilInterface;
            ret = true;
            //给每个API接口安装工具
        }catch (Exception e){
            ret = false;
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
