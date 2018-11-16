/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.LabelManager;
import cn.org.upbnc.base.entity.Label;

import java.util.ArrayList;
import java.util.List;

public class LabelManagerImpl implements LabelManager {
    private static LabelManager instance = null;
    private List<Label> labelList;

    private LabelManagerImpl(){
        this.labelList = new ArrayList<Label>();
    }

    public static LabelManager getInstance() {
        if(null == instance) {
            instance = new LabelManagerImpl();
        }
        return instance;
    }

    void test(){
        this.labelList.clear();
    }
}
