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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Session implements Runnable{
    private static final Logger LOG = LoggerFactory.getLogger(Session.class);
    private static final Session session = new Session();

    private SystemStatusEnum status;
    private BaseInterface baseInterface;
    private ServiceInterface serviceInterface;
    private APIInterface apiInterface;

    private Session(){
        this.status = SystemStatusEnum.OFF;
        this.baseInterface = new BaseInterface();
        this.serviceInterface = new ServiceInterface();
        this.apiInterface = new APIInterface();
    }
    public final static Session getSession(){
        return session;
    }
    // Init itself by another thread
    public void init(){
        LOG.info("UPSR Session Init");
        new Thread(this).start();
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
