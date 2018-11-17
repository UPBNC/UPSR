/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.BGPManager;
import cn.org.upbnc.entity.BGPConnect;

import java.util.ArrayList;
import java.util.List;

public class BGPManagerImpl implements BGPManager {
    private static BGPManager instance = null;

    private List<BGPConnect> bgpConnectList;

    private BGPManagerImpl(){

        this.bgpConnectList = new ArrayList<BGPConnect>();
        return;
    }
    public static BGPManager getInstance(){
        if(null == instance) {
            instance = new BGPManagerImpl();
        }
        return instance;
    }

    void test(){
        this.bgpConnectList.clear();
    }
}
