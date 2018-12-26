/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util;

import cn.org.upbnc.service.ServiceInterface;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceInterface.class);
    private DataBroker dataBroker;

    public void init() {
        try {
            LOG.info("UtilInterface init Start...");
            LOG.info("UtilInterface init End!");
        } catch (Exception e) {
            LOG.info("UtilInterface init failure! " + e.getMessage());
            throw e;
        }
    }

    public void setDataBroker(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    public DataBroker getDataBroker() {
        return dataBroker;
    }
}
