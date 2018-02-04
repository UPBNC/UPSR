/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api;

import cn.org.upbnc.entity.Address;
import cn.org.upbnc.entity.DeviceInterface;
import cn.org.upbnc.entity.NetworkSeg;
import cn.org.upbnc.entity.VPNInstance;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.entity.UpdateVpnInstance;

import java.util.List;
import java.util.Map;

public interface VpnInstanceApi {
    // Set ServiceInterface
    boolean setServiceInterface(ServiceInterface serviceInterface);

    Map<String, Object> updateVpnInstance(UpdateVpnInstance updateVpnInstance);

    boolean delVpnInstance(Integer id);

    Map<String, Object> delVpnInstance(String routerId, String vpnName);

    Map<String, Object> getVpnInstance(String routerId, String vpnName);

    Map<String, Object> getVpnInstanceList(String vpnName);

    Map<String, Object> getVpnInstanceMap(String vpnName);

    String getTest();

    Map<String, Object> isContainVpnName(String vpnName);

    Map<String, Object> isContainRd(String routerId, String rd);

    Map<String, Object> createTunnelsByVpnTemplate(String vpnName);
}
