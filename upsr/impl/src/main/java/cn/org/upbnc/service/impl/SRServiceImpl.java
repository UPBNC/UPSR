/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.service.SRService;
import cn.org.upbnc.service.ServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SRServiceImpl implements SRService {
    private static final Logger LOG = LoggerFactory.getLogger(SRServiceImpl.class);
    private static SRService ourInstance = new SRServiceImpl();
    public static SRService getInstance() {
        return ourInstance;
    }

    private BaseInterface baseInterface;

    private SRServiceImpl() {
        this.baseInterface = null;
    }

    @Override
    public boolean setBaseInterface(BaseInterface baseInterface){
        boolean ret = false;
        try {
            if (null != baseInterface) {
                this.baseInterface = baseInterface;

                // get base manager
                //....
            }
            ret = true;
        }catch (Exception e){
            LOG.info(e.getMessage());
        }
        return ret;
    }
}
