/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.service.impl.NetconfSessionServiceImpl;
import cn.org.upbnc.service.impl.SRServiceImpl;
import cn.org.upbnc.service.impl.TopoServiceImpl;
import cn.org.upbnc.service.impl.VPNServiceImpl;
import cn.org.upbnc.util.UtilInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceInterface.class);
    // Base & Util interface
    private BaseInterface baseInterface;
    private UtilInterface utilInterface;

    private VPNService vpnService;
    private SRService srService;
    private TopoService topoService;
    private NetconfSessionService netconfSessionService;

    public ServiceInterface(){
        // Base Interface
        this.baseInterface = null;

        // Init Service
        this.vpnService = null;
        this.srService = null;
        this.topoService = null;
        this.netconfSessionService = null;
    }

    public void init(){
        try {
            LOG.info("ServiceInterface init Start...");
            this.vpnService = VPNServiceImpl.getInstance();
            this.srService = SRServiceImpl.getInstance();
            this.topoService = TopoServiceImpl.getInstance();
            this.netconfSessionService = NetconfSessionServiceImpl.getInstance();
            LOG.info("ServiceInterface init End!");
        }catch (Exception e){
            LOG.info("ServiceInterface init failure! "+e.getMessage());
            throw e;
        }
    }

    // 安装基础系统接口
    public boolean setBaseInterface(BaseInterface baseInterface){
        LOG.info("Service Interface setBaseInterface Start...");
        boolean ret = false;
        try {
            this.baseInterface = baseInterface;
            // 给每个业务服务安装基础系统
            ret = this.srService.setBaseInterface(this.baseInterface);
            ret = ret &&this.topoService.setBaseInterface(this.baseInterface);
            ret = ((true == ret )? this.vpnService.setBaseInterface(this.baseInterface):false);
            ret = ((true == ret )? this.netconfSessionService.setBaseInterface(this.baseInterface):false);
        }catch (Exception e){
            ret = false;
            LOG.info(e.getMessage());
            LOG.info("Service Interface setBaseInterface Failed");
        }
        LOG.info("Service Interface setBaseInterface End!");
        return ret;
    }

    // 安装工具系统
    public boolean setUtilInterface(UtilInterface utilInterface) {
        boolean ret = false;
        try {
            this.utilInterface = utilInterface;
            ret = true;
            // 给每个业务服务安装工具系统
            /// ...
        }catch (Exception e){
            ret = false;
            LOG.info(e.getMessage());
        }
        return ret;
    }

    public VPNService getVpnService() {
        if(null == this.vpnService){
            this.vpnService = VPNServiceImpl.getInstance();
        }
        return this.vpnService;
    }

    public SRService getSrService() {
        if(null == this.srService){
            this.srService = SRServiceImpl.getInstance();
        }
        return this.srService;
    }

    public TopoService getTopoService(){
        if(null == this.topoService){
            this.topoService = TopoServiceImpl.getInstance();
        }
        return this.topoService;
    }
    public NetconfSessionService getNetconfSessionService(){
        if(null == this.netconfSessionService){
            this.netconfSessionService = NetconfSessionServiceImpl.getInstance();
        }
        return this.netconfSessionService;
    }
}
