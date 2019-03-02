/*
 * Copyright © 2018 Copyright (c) 2018 BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base;

import cn.org.upbnc.entity.BfdSession;
import cn.org.upbnc.entity.Tunnel;
import cn.org.upbnc.util.netconf.NetconfClient;

import java.util.List;

public interface TunnelManager {
    Tunnel createTunnel(Tunnel tunnel);

    Tunnel updateTunnel(Tunnel tunnel);

    List<Tunnel> getTunnel(String routerId, String name);

    List<Tunnel> getTunnels();

    void emptyTunnelsByRouterId(String routerId);

    boolean deleteTunnel(String routerId, String name);

    boolean checkTunnelNameAndId(String routerId, String tunnelName, String tunnelId);

//    boolean addBfdSession(BfdSession bfdSession,String routerId);
//
//    boolean deleteBfdSession(String name,String routerId);

    boolean isBfdDiscriminatorLocal(Integer local);

    Integer getBfdDiscriminatorLocal();

    boolean isBfdDiscriminatorLocalUsed(int i);

    boolean createTunnels(List<Tunnel> tunnels,String routerId, NetconfClient netconfClient);

    boolean deleteTunnels(List<String> tunnels,String routerId, NetconfClient netconfClient);

    boolean syncTunnelsConf(String routerId,NetconfClient netconfClient);

}
