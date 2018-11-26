/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.callback.TopoCallback;
import cn.org.upbnc.entity.TopoInfo;
import cn.org.upbnc.enumtype.TopoStatusEnum;

public interface TopoService extends TopoCallback {
    // Set BaseInterface
    boolean setBaseInterface(BaseInterface baseInterface);

    // Get Topology Status;
    //TopoStatusEnum getTopoStatus();

    // Get Topology Info
    TopoInfo getTopoInfo();

    // test function
    void test();
}
