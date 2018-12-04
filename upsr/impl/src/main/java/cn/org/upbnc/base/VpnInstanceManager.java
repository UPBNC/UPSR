/*
 * Copyright © 2018 Copyright (c) 2018 BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base;

import cn.org.upbnc.entity.*;

import java.util.List;


public interface VpnInstanceManager {
    boolean addVpnInstance(VPNInstance vpnInstance);
    boolean delVpnInstance(Integer id);
    boolean delVpnInstance(String routerId, String vpnName);
    VPNInstance getVpnInstance(Integer id);
    VPNInstance getVpnInstance(String routerId, String vpnName);
    VPNInstance updateVpnInstance(String vpnName,
                                     String routerId,
                                     Device device,
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


    VPNInstance  updateVpnInstance(VPNInstance vpnInstance);
    List<VPNInstance> getVpnInstanceList();
    List<VPNInstance> getVpnInstanceListByRouterId(String routerId);
}