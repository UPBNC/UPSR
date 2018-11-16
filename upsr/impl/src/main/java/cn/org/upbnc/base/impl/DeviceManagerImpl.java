/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.entity.Device;

import java.util.HashMap;
import java.util.Map;

public class DeviceManagerImpl implements DeviceManager {
    private static DeviceManager instance = null;
    private Map<String,Device> mapInstance;

    private DeviceManagerImpl() {
        this.mapInstance = new HashMap<String,Device>();
        return;
    }
    public static DeviceManager getInstance() {
        if(null == instance) {
            instance = new DeviceManagerImpl();
        }
        return instance;
    }

    @Override
    public Device addDevice(String name, String routerId) {
        Device device = null;
        if(null != name && null != routerId) {
            device = new Device();
            this.mapInstance.put(routerId, device);
        }
        return device;
    }

    @Override
    public Device getDevice(String routerId) {
        Device device = null;
        if(null != routerId) {
            device = this.mapInstance.get(routerId);
        }
        return device;
    }
}
