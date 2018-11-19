/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util;

import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.impl.SRServiceImpl;
import cn.org.upbnc.service.impl.VPNServiceImpl;
import cn.org.upbnc.util.impl.NetConfCmd2XmlImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceInterface.class);
    private NetConfCmd2Xml netConfCmd2Xml;

    public void init(){
        try {
            LOG.info("UtilInterface init Start...");
            this.netConfCmd2Xml = NetConfCmd2XmlImpl.getInstance();
            this.netConfCmd2Xml.initXml();
            LOG.info("UtilInterface init End!");
        }catch (Exception e){
            LOG.info("UtilInterface init failure! "+e.getMessage());
            throw e;
        }
    }

    public NetConfCmd2Xml getNetConfCmd2Xml() {
        if(null == this.netConfCmd2Xml){
            this.netConfCmd2Xml = NetConfCmd2XmlImpl.getInstance();
            this.netConfCmd2Xml.initXml();
        }
        return this.netConfCmd2Xml;
    }
}
