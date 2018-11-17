/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base;

import cn.org.upbnc.base.impl.BGPManagerImpl;
import cn.org.upbnc.base.impl.DeviceManagerImpl;
import cn.org.upbnc.base.impl.NetConfManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseInterface {
    private static final Logger LOG = LoggerFactory.getLogger(BaseInterface.class);
    private BGPManager bgpManager;
    private DeviceManager deviceManager;
    private NetConfManager netConfManager;

    public BaseInterface() {
    }

    public void init() {
        try {
            LOG.info("BaseInterface init Start...");
            this.deviceManager = DeviceManagerImpl.getInstance();
            this.netConfManager = NetConfManagerImpl.getInstance();
            this.bgpManager = BGPManagerImpl.getInstance();
            LOG.info("BaseInterface init End!");
        }catch (Exception e){
            LOG.info("BaseInterface init failure! "+e.getMessage());
            throw e;
        }
    }

    public DeviceManager getDeviceManager() {
        if (null == this.deviceManager) {
            this.deviceManager = DeviceManagerImpl.getInstance();
        }
        return this.deviceManager;
    }

    public NetConfManager getNetConfManager() {
        if (null == this.netConfManager) {
            this.netConfManager = NetConfManagerImpl.getInstance();
        }
        return this.netConfManager;
    }

    public BGPManager getBgpManager(){
        if(null == this.bgpManager){
            this.bgpManager = BGPManagerImpl.getInstance();
        }
        return this.bgpManager;
    }

    ///....
}
