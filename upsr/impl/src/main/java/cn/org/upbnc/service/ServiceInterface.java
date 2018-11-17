/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service;

import cn.org.upbnc.service.impl.SRServiceImpl;
import cn.org.upbnc.service.impl.VPNServiceImpl;

public class ServiceInterface {
    private VPNService vpnService;
    private SRService srService;

    public ServiceInterface(){
    }

    public void init(){
        this.vpnService = VPNServiceImpl.getInstance();
        this.srService = SRServiceImpl.getInstance();
    }
}
