/*
 * Copyright © 2018 Copyright (c) 2018 BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base;

import cn.org.upbnc.enumtype.NetConfStatusEnum;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.entity.NetConf;

import java.util.List;

public interface NetConfManager {
    NetConf createNetConfConnect();

    NetConf addDevice(NetConf netConf);

    List<NetConf> getDevices();

    List<NetconfClient> getNetconClients();

    NetconfClient getNetconClient(String routerId);

    NetConf getDevice(String routerId);

    boolean setDevice(String routerId, NetConfStatusEnum netConfStatusEnum);

    boolean deleteDevice(NetConf netConf);

    void close();

    void closeNetconfByRouterId(String routerId);
}