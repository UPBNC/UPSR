/*
 * Copyright © 2018 Copyright (c) 2018 BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.LinkManager;

public class LinkManagerImpl implements LinkManager {
    private static LinkManager instance = null;

    private LinkManagerImpl(){
        return;
    }
    public static LinkManager getInstance(){
        if(null == instance){
            instance = new LinkManagerImpl();
        }
        return instance;
    }
}
