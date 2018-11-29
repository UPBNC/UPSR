/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.service.entity.NetconfSession;

public interface NetconfSessionService {
    //set BaseInterface
    boolean setBaseInterface(BaseInterface baseInterface);
    boolean updateNetconfSession(String routerId, String deviceName, String deviceDesc,
                                 String deviceType, String deviceIP, Integer devicePort,
                                 String userName, String userPassword);
    boolean delNetconfSession(String routerId);
    NetconfSession getNetconfSession(String routerId);

}
