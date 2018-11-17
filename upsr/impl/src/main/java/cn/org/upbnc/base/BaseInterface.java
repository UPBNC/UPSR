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

public class BaseInterface {
    private BGPManager bgpManager;
    private DeviceManager deviceManager;
    private NetConfManager netConfManager;

    public BaseInterface() {
    }

    public void init() {
        this.deviceManager = DeviceManagerImpl.getInstance();
        this.netConfManager = NetConfManagerImpl.getInstance();
        this.bgpManager = BGPManagerImpl.getInstance();
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
