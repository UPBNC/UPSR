/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.service.impl.SRServiceImpl;
import cn.org.upbnc.service.impl.VPNServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceInterface.class);
    private VPNService vpnService;
    private SRService srService;

    public ServiceInterface(){
    }

    public void init(){
        try {
            LOG.info("ServiceInterface init Start...");
            this.vpnService = VPNServiceImpl.getInstance();
            this.srService = SRServiceImpl.getInstance();
            LOG.info("ServiceInterface init End!");
        }catch (Exception e){
            LOG.info("ServiceInterface init failure! "+e.getMessage());
            throw e;
        }

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
}
