/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.TopoTestApi;

public class TopoTestApiImpl implements TopoTestApi {
    private static TopoTestApi ourInstance = new TopoTestApiImpl();

    public static TopoTestApi getInstance() {
        return ourInstance;
    }

    private TopoTestApiImpl() {
    }

    @Override
    public String getTest() {
        return null;
    }
}
