/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.SrLabelApi;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.SrLabelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SrLabelApiImpl implements SrLabelApi {
    private static final Logger LOG = LoggerFactory.getLogger(SrLabelApiImpl.class);
    private static SrLabelApi ourInstance = new SrLabelApiImpl();
    private ServiceInterface serviceInterface;
    private SrLabelService srLabelService;

    public static SrLabelApi getInstance(){
        return ourInstance;
    }

    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        boolean ret = true;
        try {
            if(this.serviceInterface == null){
                this.serviceInterface = serviceInterface;
                this.srLabelService = serviceInterface.getSrLabelService();
            }
        }catch (Exception e){
            ret = false;
            LOG.info(e.toString());
        }
        return ret;
    }

    @Override
    public String updateNodeLabel(String routerId, String labelVal) {
        return null;
    }

    @Override
    public String updateNodeLabelRange(String routerId, String labelBegin, String labelEnd) {
        return null;
    }

    @Override
    public String updateIntfLabel(String routerId, String localAddress, String remoteAddress, String labelVal) {
        srLabelService.updateIntfLabel(routerId,localAddress,remoteAddress,labelVal);
        return null;
    }

    @Override
    public String delIntfLabel(String routerId, String localAddress, String remoteAddress, String labelVal) {
        return null;
    }

    @Override
    public Device getDevice(String routerId) {
        return srLabelService.getDevice(routerId);
    }
}
