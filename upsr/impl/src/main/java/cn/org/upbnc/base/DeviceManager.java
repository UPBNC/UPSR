/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base;

import cn.org.upbnc.entity.Device;

public interface DeviceManager {
    Device addDevice(String name, String routerId);
    Device addDevice(Device device);
    Device getDevice(String routerId);
    Device getDeviceByName(String deviceName);
    Device getDeviceByIP(String deviceIP);
    boolean delDevice(String routerId);
    boolean delDeviceByName(String deviceName);
    boolean delDeviceByIP(String deviceIP);

}
