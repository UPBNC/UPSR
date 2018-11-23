/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api;

import cn.org.upbnc.entity.NetConf;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.entity.NetconfSession;

public interface NetconfSessionApi {

    boolean setServiceInterface(ServiceInterface serviceInterface);
    boolean updateNetconfSession(String deviceName, String deviceDesc,
                                 String deviceIP, Integer devicePort,
                                 String userName, String userPassword);
    boolean delNetconfSession(String deviceName);
    boolean delNetconfSession(String deviceIP, Integer devicePort);
    NetconfSession getNetconfSession(String deviceName);
    NetconfSession getNetconfSession(String deviceIP, Integer devicePort);

}
