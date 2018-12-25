/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.service.entity.UpdateVpnInstance;

import java.util.List;
import java.util.Map;

public interface VPNService {
    // Set BaseInterface
    boolean setBaseInterface(BaseInterface baseInterface);

    Map<String, Object> updateVpnInstance(UpdateVpnInstance updateVpnInstance);

    boolean delVpnInstance(Integer id);

    Map<String, Object> delVpnInstance(String routerId, String vpnName);

    Map<String, Object> getVpnInstanceList(String vpnName);

    Map<String, Object> getVpnInstance(String routerId, String vpnName);

    VPNInstance getVpnInstanceFromDevice(String routerId, String vpnName);

    List<VPNInstance> getVpnInstanceListFromDevice(String vpnName);

    Map<String, Object> getVpnInstanceMap(String vpnName);

    boolean syncVpnInstanceConf();

    boolean syncVpnInstanceConf(String routerId);

    Map<String, Object> isContainVpnName(String vpnName);

    Map<String, Object> isContainRd(String routerId, String rd);

    String getTest();
}
