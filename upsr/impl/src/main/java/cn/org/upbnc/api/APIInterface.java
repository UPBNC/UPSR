/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api;

import cn.org.upbnc.api.impl.*;
import cn.org.upbnc.entity.TunnelPolicy.TunnelPolicy;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.util.UtilInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIInterface {
    private static final Logger LOG = LoggerFactory.getLogger(APIInterface.class);
    private ServiceInterface serviceInterface;
    private UtilInterface utilInterface;
    private TopoInfoApi topoInfoApi;
    private BgplsSessionApi bgplsSessionApi;
    private VpnInstanceApi vpnInstanceApi;
    private NetconfSessionApi netconfSessionApi;
    private InterfaceApi interfaceApi;
    private ConfSyncApi  confSyncApi;
    private SrLabelApi srLabelApi;
    private TunnelApi tunnelApi;
    private TunnelPolicyApi tunnelPolicyApi;

    public APIInterface(){
        // Service Interface
        this.serviceInterface = null;
        this.utilInterface = null;

        // Init API
        this.topoInfoApi = null;
        this.bgplsSessionApi = null;
		this.vpnInstanceApi = null;
        this.interfaceApi = null;
		this.netconfSessionApi = null;
		this.confSyncApi = null;
		this.srLabelApi = null;
		this.tunnelApi = null;
		this.tunnelPolicyApi=null;
    }

    public void init(){
        try {
            LOG.info("APIInterface init Start...");
            this.topoInfoApi = TopoInfoApiImpl.getInstance();
            this.bgplsSessionApi = BgplsSessionApiImpl.getInstance();
			this.vpnInstanceApi = VpnInstanceApiImpl.getInstance();
			this.interfaceApi = InterfaceApiImpl.getInstance();
			this.confSyncApi = ConfSyncApiImpl.getInstance();
            this.netconfSessionApi = NetconfSessionApiImpl.getInstance();
            this.srLabelApi = SrLabelApiImpl.getInstance();
            this.tunnelApi = TunnelApiImpl.getInstance();
            this.tunnelPolicyApi=TunnelPolicyApiImpl.getInstance();
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
            ret = this.topoInfoApi.setServiceInterface(this.serviceInterface);
            ret = this.srLabelApi.setServiceInterface(this.serviceInterface);
            ret = this.tunnelApi.setServiceInterface(this.serviceInterface);
            ret = ((true == ret )? this.vpnInstanceApi.setServiceInterface(this.serviceInterface):false);
            ret = ((true == ret )? this.netconfSessionApi.setServiceInterface(this.serviceInterface):false);
            ret = ((true == ret )? this.interfaceApi.setServiceInterface(this.serviceInterface):false);
            ret = ((true == ret )? this.confSyncApi.setServiceInterface(this.serviceInterface):false);
            ret = ((true == ret )? this.tunnelPolicyApi.setServiceInterface(this.serviceInterface):false);
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

    public TopoInfoApi getTopoInfoApi() {
        return topoInfoApi;
    }
    public SrLabelApi getSrLabelApi(){
        return srLabelApi;
    }
    public TunnelApi getTunnelApi(){
        return tunnelApi;
    }
    public BgplsSessionApi getBgplsSessionApi() {
        return bgplsSessionApi;
    }
	public VpnInstanceApi getVpnInstanceApi()
    {
        return vpnInstanceApi;
    }
    public NetconfSessionApi getNetconfSessionApi(){
        return netconfSessionApi;
    }
    public InterfaceApi getInterfaceApi() {return  interfaceApi; }
    public ConfSyncApi  getConfSyncApi() {return confSyncApi;}
    public TunnelPolicyApi  getTunnelPolicyApi() {return tunnelPolicyApi;}
}
