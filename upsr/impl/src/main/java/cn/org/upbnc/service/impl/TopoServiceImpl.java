/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BGPManager;
import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.entity.TopoInfo;
import cn.org.upbnc.service.TopoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopoServiceImpl implements TopoService {
    private static final Logger LOG = LoggerFactory.getLogger(TopoServiceImpl.class);
    private static TopoService ourInstance = new TopoServiceImpl();
    public static TopoService getInstance() {
        return ourInstance;
    }

    // 基础系统
    private BaseInterface baseInterface;
    private BGPManager bgpManager;

    // Topo数据管理
    private TopoInfo topoInfo;

    private TopoServiceImpl() {
        this.baseInterface = null;
        this.bgpManager = null;
        this.topoInfo = null;
    }

    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        boolean ret = false;
        try {
            if (null != baseInterface) {
                this.baseInterface = baseInterface;

                // get base manager
                this.bgpManager = this.baseInterface.getBgpManager();
            }
            ret = true;
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
        return ret;
    }

    @Override
    public TopoInfo getTopoInfo(){
        if( null == this.topoInfo) {
            try {
                //注入回调函数
                this.bgpManager.getTopoInfo(this);
            } catch (Exception e) {
                LOG.info(e.getMessage());
            }
        }
        return this.topoInfo;
    }

    @Override
    public void test(){
        this.bgpManager.test();
    }

    @Override
    public void setTopoInfoCb(TopoInfo topoInfo){
        this.topoInfo = topoInfo;
        return;
    }

}
