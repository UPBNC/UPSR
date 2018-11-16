/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.base.entity.NetConf;

public class NetConfManagerImpl implements NetConfManager {

    private static NetConfManager instance = null;

    private NetConfManagerImpl(){
        return;
    }
    public static NetConfManager getInstance(){
        if(null == instance){
            instance = new NetConfManagerImpl();
        }
        return instance;
    }
    @Override
    public NetConf createNetConfConnect() {
        return new NetConf();
    }

}
