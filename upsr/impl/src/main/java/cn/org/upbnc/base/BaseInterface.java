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
import cn.org.upbnc.base.impl.VpnInstanceManagerImpl;
import cn.org.upbnc.util.UtilInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseInterface {
    private static final Logger LOG = LoggerFactory.getLogger(BaseInterface.class);

    private UtilInterface utilInterface;
    private BGPManager bgpManager;
    private DeviceManager deviceManager;
    private NetConfManager netConfManager;
    private VpnInstanceManager vpnInstanceManager;

    public BaseInterface() {
    }

    // 基础系统初始化
    public void init() {
        try {
            // 每个基础系统初始化
            LOG.info("BaseInterface init Start...");
            this.deviceManager = DeviceManagerImpl.getInstance();
            this.netConfManager = NetConfManagerImpl.getInstance();
            this.bgpManager = BGPManagerImpl.getInstance();
            this.vpnInstanceManager = VpnInstanceManagerImpl.getInstance();
            LOG.info("BaseInterface init End!");
        }catch (Exception e){
            LOG.info("BaseInterface init failure! "+e.getMessage());
            throw e;
        }
    }

    // 安装工具系统
    public boolean setUtilInterface(UtilInterface utilInterface) {
        boolean ret = false;
        try {
            this.utilInterface = utilInterface;
            ret = true;
            // 每个基础系统安装工具
            ret = ret && this.bgpManager.setUtilInterface(this.utilInterface);
        }catch (Exception e){
            ret = false;
            LOG.info(e.getMessage());
        }
        return ret;
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

    public VpnInstanceManager getVpnInstanceManager()
    {
        if(null == this.vpnInstanceManager)
        {
            this.vpnInstanceManager = VpnInstanceManagerImpl.getInstance();
        }
        return this.vpnInstanceManager;
    }

    ///....
}
