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

import java.util.List;
import java.util.Map;

public interface NetconfSessionService {
    //set BaseInterface
    boolean setBaseInterface(BaseInterface baseInterface);

    Map<String, Object> updateNetconfSession(NetconfSession netconfSession);

    Map<String, Object> delNetconfSession(String routerId);

    Map<String, Object> getNetconfSession(String routerId);

    Map<String, Object> getNetconfSession();

    boolean isSyn(NetconfSession netconfSession);

    boolean recoverNetconfSession();

    void close();
}
