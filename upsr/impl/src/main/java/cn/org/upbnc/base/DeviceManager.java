/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base;

import cn.org.upbnc.entity.BgpDevice;
import cn.org.upbnc.entity.Device;

import java.util.List;

public interface DeviceManager {
    // Add device fucntions
    Device addDevice(String deviceName, String routerId);
    Device addDevice(Device device);

    // Get device fucntions
    Device getDevice(String routerId);
    Device getDeviceByDeviceName(String deviceName);
    Device getDeviceByNetconfIP(String deviceIP);
    List<Device> getDeviceList();

    // Delete device fucntions
    boolean delDevice(String routerId);
    boolean delDeviceByDeviceName(String deviceName);
    boolean delDeviceByNetconfIP(String deviceIP);

    // Update device functions
    List<Device> updateDeviceListByBgpDeviceList(List<BgpDevice> bgpDeviceList);

}
