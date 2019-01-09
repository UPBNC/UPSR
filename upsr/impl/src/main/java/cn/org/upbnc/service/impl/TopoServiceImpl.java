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
import cn.org.upbnc.enumtype.ServiceStatusEnum;
import cn.org.upbnc.enumtype.BgpTopoStatusEnum;
import cn.org.upbnc.service.TopoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

    // Service status
    private ServiceStatusEnum serviceStatusEnum;

    // Service BgpLabel
    private BgpTopoStatusEnum bgpTopoStatusEnum;


    private TopoServiceImpl() {
        this.baseInterface = null;
        this.bgpManager = null;
        this.deviceManager = null;
        this.linkManager = null;
        this.topoInfo = new TopoInfo();
        this.serviceStatusEnum = ServiceStatusEnum.INIT;
        this.bgpTopoStatusEnum = BgpTopoStatusEnum.INIT;
    }

    @Override
    public void startService(){
        this.serviceStatusEnum = ServiceStatusEnum.STARTING;

        // Start bgp update
        if(this.bgpTopoStatusEnum == BgpTopoStatusEnum.UPDATED){
            BgpTopoInfo bgpTopoInfo = this.bgpManager.getBgpTopoInfo();
            if( null != bgpTopoInfo) {
                this.updateBgpTopoInfoCb(bgpTopoInfo);
            }
        }

        // Other Service

        this.serviceStatusEnum = ServiceStatusEnum.READY;
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


    @Override
    public TopoInfo getTopoInfo(){
        if(this.serviceStatusEnum == ServiceStatusEnum.INIT){
            return null;
        }
        List<Device> devices = this.deviceManager.getDeviceList();
        List<Link> links = this.linkManager.getLinkList();
        this.topoInfo.setDeviceList(devices);
        this.topoInfo.setLinkList(links);
        try {
            // 调用Bgp Topology查询
            this.bgpManager.getBgpTopoInfo();
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }

        return this.topoInfo;
    }


    @Override
    public void updateBgpTopoInfoCb(BgpTopoInfo bgpTopoInfo){
        this.bgpTopoStatusEnum = BgpTopoStatusEnum.UPDATED;

        if(this.serviceStatusEnum == ServiceStatusEnum.INIT){
            return;
        }
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

//    @Override
//    public void updateBgpTopoInfoDomainCb(Map<String,BgpTopoInfo> map){
//        this.bgpTopoStatusEnum = BgpTopoStatusEnum.UPDATED;
//
//        if(this.serviceStatusEnum == ServiceStatusEnum.INIT){
//            return;
//        }
//        LOG.info("Update Topo Domain By BGP Start ...");
//        LOG.info("Update BGP Device Start...");
//        Set<Map.Entry<String,BgpTopoInfo>> set =  map.entrySet();
//        Iterator<Map.Entry<String,BgpTopoInfo>> iterator = set.iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String,BgpTopoInfo> entry = iterator.next();
//            BgpTopoInfo bgpTopoInfo = entry.getValue();
//            List<BgpDevice> bgpDevicelist = bgpTopoInfo.getBgpDeviceList();
//            List<Device> deviceList = this.deviceManager.updateDeviceListByBgpDeviceList(bgpDevicelist);
//            this.topoInfo.setDeviceList(deviceList);
//            List<Link> linkList = this.linkManager.updateLinkListByBgpLinkList(deviceList, bgpTopoInfo.getBgpLinkList());
//            this.topoInfo.setLinkList(linkList);
//        }
//
//        LOG.info("Update Topo By BGP End!");
//        return;
//    }

}
