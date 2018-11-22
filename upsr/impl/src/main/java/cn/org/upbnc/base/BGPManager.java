/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base;

import cn.org.upbnc.callback.TopoCallback;
import cn.org.upbnc.entity.TopoInfo;
import cn.org.upbnc.util.UtilInterface;

public interface BGPManager {
    // Install Util System
    boolean setUtilInterface(UtilInterface utilInterface);

    //
    TopoInfo getTopoInfo(TopoCallback tcb);

    // For test
    void test();

}
