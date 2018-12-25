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
    public static final int MaxNodeLabelRange = 65534;
    private static final Logger LOG = LoggerFactory.getLogger(SrLabelApiImpl.class);
    private static SrLabelApi ourInstance = new SrLabelApiImpl();
    private ServiceInterface serviceInterface;
    private SrLabelService srLabelService;

    public static SrLabelApi getInstance() {
        return ourInstance;
    }

    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        boolean ret = true;
        try {
            if (this.serviceInterface == null) {
                this.serviceInterface = serviceInterface;
                this.srLabelService = serviceInterface.getSrLabelService();
            }
        } catch (Exception e) {
            ret = false;
            LOG.info(e.toString());
        }
        return ret;
    }

    @Override
    public String updateNodeLabel(String routerId, String labelBegin, String labelValAbs, String action) {
        if ((labelBegin == null) || (labelValAbs == null)) {
            return null;
        }
        if (Integer.parseInt(labelValAbs) <= Integer.parseInt(labelBegin)) {
            return null;
        }
        int labelVal = Integer.parseInt(labelValAbs) - Integer.parseInt(labelBegin);
        srLabelService.updateNodeLabel(routerId, labelVal + "", action);
        return null;
    }

    @Override
    public String updateNodeLabelRange(String routerId, String labelBegin, String labelEnd, String action) {
        if ((labelBegin == null) || (labelEnd == null)) {
            return null;
        }
        if (Integer.parseInt(labelEnd) - Integer.parseInt(labelBegin) > this.MaxNodeLabelRange) {
            return null;
        }
        srLabelService.updateNodeLabelRange(routerId, labelBegin, labelEnd, action);
        return null;
    }

    @Override
    public String updateIntfLabel(String routerId, String ifAddress, String labelVal, String action) {
        if ((ifAddress == null) || (labelVal == null)) {
            return null;
        }
        srLabelService.updateIntfLabel(routerId, ifAddress, labelVal, action);
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
