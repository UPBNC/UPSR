/*
 * Copyright © 2018 Copyright (c) 2018 BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base;

import cn.org.upbnc.entity.Tunnel;

import java.util.List;
import java.util.Map;

public interface TunnelManager {
    Tunnel createTunnel(Tunnel tunnel);

    Tunnel updateTunnel(Tunnel tunnel);

    List<Tunnel> getTunnel(String routerId, String name);

    List<Tunnel> getTunnels();

    boolean deleteTunnel(String routerId, String name);
}
