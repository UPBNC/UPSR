/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service.impl;

import cn.org.upbnc.service.VPNService;

public class VPNServiceImpl implements VPNService {
    private static VPNService ourInstance = new VPNServiceImpl();

    public static VPNService getInstance() {
        return ourInstance;
    }

    private VPNServiceImpl() {
    }
}
