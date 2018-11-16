/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.LinkManager;
import cn.org.upbnc.base.entity.Link;

import java.util.ArrayList;
import java.util.List;

public class LinkManagerImpl implements LinkManager {
    private static LinkManager instance = null;
    private List<Link> topoLinkList;

    private LinkManagerImpl(){
        this.topoLinkList = new ArrayList<Link>();
        return;
    }
    public static LinkManager getInstance(){
        if(null == instance){
            instance = new LinkManagerImpl();
        }
        return instance;
    }

    void test(){
        this.topoLinkList.clear();
    }
}
