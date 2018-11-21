/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.core;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.util.UtilInterface;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Session implements Runnable{
    private static final Logger LOG = LoggerFactory.getLogger(Session.class);
    private static final Session session = new Session();

    private SystemStatusEnum status;
    private BaseInterface baseInterface;
    private ServiceInterface serviceInterface;
    private APIInterface apiInterface;
    private UtilInterface utilInterface;

    private DataBroker dataBroker;

    private Session(){
        this.status = SystemStatusEnum.OFF;
        this.baseInterface = new BaseInterface();
        this.serviceInterface = new ServiceInterface();
        this.apiInterface = new APIInterface();
        this.utilInterface = new UtilInterface();
    }
    public final static Session getSession(){
        return session;
    }
    // Init itself by another thread
    public void init(DataBroker dataBroker){
        LOG.info("UPSR Session Init");
        this.dataBroker = dataBroker;
        new Thread(this).start();
    }

    public BaseInterface getBaseInterface(){
        if(SystemStatusEnum.ON == status ){
            return this.baseInterface;
        }else{
            return null;
        }
    }

    public ServiceInterface getServiceInterface(){
        if(SystemStatusEnum.ON == status ){
            return this.serviceInterface;
        }else{
            return null;
        }
    }

    public APIInterface getApiInterface() {
        if(SystemStatusEnum.ON == status ){
            return this.apiInterface;
        }else{
            return null;
        }
    }

    public UtilInterface getUtilInterface(){
        if(SystemStatusEnum.ON == status ){
            return this.utilInterface;
        }else{
            return null;
        }
    }

    public SystemStatusEnum getStatus() {
        return status;
    }

    // private init
    private void initReal(){
        try {
            LOG.info("UPSR is Starting");
            this.status = SystemStatusEnum.STARTING;

            // Doing anything init in order
            this.baseInterface.init();
            this.serviceInterface.init();
            this.apiInterface.init();
            this.utilInterface.init();

            // Add manager to caller
            this.serviceInterface.setBaseInterface(this.baseInterface);
            this.apiInterface.setServiceInterface(this.serviceInterface);

            // Add util to caller
            this.apiInterface.setUtilInterface(this.utilInterface);
            this.serviceInterface.setUtilInterface(this.utilInterface);
            this.baseInterface.setUtilInterface(this.utilInterface);

            // Add databroker to util
            this.utilInterface.setDataBroker(this.dataBroker);

            this.status = SystemStatusEnum.ON;
            LOG.info("UPSR is ON");
        }catch (Exception e){
            LOG.info("UPSR Session init failure! "+e.getMessage());
        }
    }

    @Override
    public void run(){
        this.initReal();
    }
}
