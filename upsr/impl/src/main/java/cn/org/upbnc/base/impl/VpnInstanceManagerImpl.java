/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.entity.VPNInstance;
import cn.org.upbnc.base.VpnInstanceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

public class VpnInstanceManagerImpl  implements VpnInstanceManager {
    private static VpnInstanceManager instance = null;
   // private List<VPNInstance>  vpnInstanceList ;

    @Override
    public String toString() {
        return "VpnInstanceManagerImpl{}";
    }

    private VpnInstanceManagerImpl()
    {
      //  this.vpnInstanceList = new LinkedList<VPNInstance>();

    }
    public static VpnInstanceManager getInstance()
    {
        if(null == instance)
        {
            instance = new VpnInstanceManagerImpl();
        }
        return instance;
    }
}
