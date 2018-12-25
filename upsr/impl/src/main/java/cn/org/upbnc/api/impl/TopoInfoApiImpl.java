/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.SrLabelApi;
import cn.org.upbnc.api.TopoInfoApi;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.enumtype.AddressTypeEnum;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.SrLabelService;
import cn.org.upbnc.service.TopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.linkinfo.Links;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TopoInfoApiImpl implements TopoInfoApi {
    private static final Logger LOG = LoggerFactory.getLogger(TopoInfoApiImpl.class);
    private static TopoInfoApi ourInstance = new TopoInfoApiImpl();
    private static boolean inited = false;
    private ServiceInterface serviceInterface;
    private TopoService topoService;
    private SrLabelService srLabelService;

    public static TopoInfoApi getInstance() {
        return ourInstance;
    }

    private TopoInfoApiImpl() {
        this.serviceInterface = null;
        this.topoService = null;
        this.srLabelService = null;
    }

    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        boolean ret = false;
        try {
            if (serviceInterface != null) {
                this.serviceInterface = serviceInterface;
                this.topoService = serviceInterface.getTopoService();
                this.srLabelService = serviceInterface.getSrLabelService();
            }
            ret = true;
        } catch (Exception e) {
            ret = false;
            LOG.info(e.getMessage());
        }
        return ret;
    }

    @Override
    public Map<String, Object> getTopoInfo() {
        if (inited == false) {
            srLabelService.syncAllIntfLabel();
            inited = true;
        }
        return this.topoService.getTopoInfo();
    }
}
