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

import java.util.List;

public interface VPNService {
    // Set BaseInterface
    boolean setBaseInterface(BaseInterface baseInterface);
    boolean updateVpnInstance(String vpnName,
                              String routerId,
                              String businessRegion,
                              String rd,
                              String importRT,
                              String exportRT,
                              Integer peerAS,
                              Address peerIP,
                              Integer routeSelectDelay,
                              Integer importDirectRouteEnable,
                              List<DeviceInterface> deviceInterfaceList,
                              List<NetworkSeg> networkSegList);
    boolean delVpnInstance(Integer id);
    boolean delVpnInstance(String vpnName);
    VPNInstance getVpnInstance(Integer id);
    VPNInstance getVpnInstance(String vpnName);
    List<VPNInstance> getVpnInstanceList();

    String getTest();
}
