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
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.LinkManager;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.enumtype.TopoStatusEnum;
import cn.org.upbnc.service.TopoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TopoServiceImpl implements TopoService {
    private static final Logger LOG = LoggerFactory.getLogger(TopoServiceImpl.class);
    private static TopoService ourInstance = new TopoServiceImpl();
    public static TopoService getInstance() {
        return ourInstance;
    }

    // 基础系统
    private BaseInterface baseInterface;
    private BGPManager bgpManager;
    private DeviceManager deviceManager;
    private LinkManager linkManager;

    // Topo数据管理
    private TopoInfo topoInfo;


    private TopoServiceImpl() {
        this.baseInterface = null;
        this.bgpManager = null;
        this.deviceManager = null;
        this.linkManager = null;
        this.topoInfo = new TopoInfo();
    }

    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        LOG.info("Topo Service setBaseInterface Start...");
        boolean ret = false;
        try {
            if (null != baseInterface) {
                this.baseInterface = baseInterface;

                // get bgp manager
                this.bgpManager = this.baseInterface.getBgpManager();
                this.bgpManager.setTopoCallback(this);

                // get device manager
                this.deviceManager = this.baseInterface.getDeviceManager();
                if(deviceManager == null){
                    LOG.info("Device Manager is Null");
                }

                // get link manager
                this.linkManager = this.baseInterface.getLinkManager();
                if(deviceManager == null){
                    LOG.info("Link Manager is Null");
                }
            }
            ret = true;
        } catch (Exception e) {
            LOG.info(e.getMessage());
            LOG.info("Topo Service setBaseInterface Fail!");
        }
        LOG.info("Topo ServicesetBaseInterface End!");
        return ret;
    }

//    @Override
//    public TopoStatusEnum getTopoStatus(){
//        return this.topoStatusEnum;
//    }

    @Override
    public TopoInfo getTopoInfo(){
        List<Device> devices = this.deviceManager.getDeviceList();
        List<Link> links = this.linkManager.getLinkList();
        this.topoInfo.setDeviceList(devices);
        this.topoInfo.setLinkList(links);
        try {
            // 调用Bgp Topology查询
            this.bgpManager.getTopoInfo();
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }

        return this.topoInfo;
    }

    @Override
    public void test(){
        this.bgpManager.test();
    }

    @Override
    public void updateBgpTopoInfoCb(BgpTopoInfo bgpTopoInfo){
        LOG.info("Update Topo By BGP Start ...");
        LOG.info("Update BGP Device Start...");
        List<Device> deviceList = this.deviceManager.updateDeviceListByBgpDeviceList(bgpTopoInfo.getBgpDeviceList());
        this.topoInfo.setDeviceList(deviceList);
        LOG.info("Update BGP Device End!");
        List<Link> linkList = this.linkManager.updateLinkListByBgpLinkList(deviceList,bgpTopoInfo.getBgpLinkList());
        this.topoInfo.setLinkList(linkList);
        LOG.info("Update Topo By BGP End!");
        return;
    }

}
