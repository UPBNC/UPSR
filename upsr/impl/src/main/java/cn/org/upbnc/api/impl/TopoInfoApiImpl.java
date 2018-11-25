/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.TopoInfoApi;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.enumtype.AddressTypeEnum;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.TopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.linkinfo.Links;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TopoInfoApiImpl implements TopoInfoApi {
    private static final Logger LOG = LoggerFactory.getLogger(TopoInfoApiImpl.class);
    private static TopoInfoApi ourInstance = new TopoInfoApiImpl();
    private ServiceInterface serviceInterface;
    private TopoService topoService;

    public static TopoInfoApi getInstance() {
        return ourInstance;
    }

    private TopoInfoApiImpl() {
        this.serviceInterface = null;
        this.topoService = null;
    }
    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        boolean ret = false;
        try{
            if(serviceInterface != null) {
                this.serviceInterface = serviceInterface;
                this.topoService = serviceInterface.getTopoService();
            }
            ret = true;
        }catch (Exception e){
            ret = false;
            LOG.info(e.getMessage());
        }
        return ret;
    }

    @Override
    public List<Links> getLinks() {
        return null;
    }

    @Override
    public TopoInfo getTopoInfo() {

        if (topoService == null) {
            LOG.info("topoService is null");
            TopoInfo topoInfo = new TopoInfo();
            List<Device> deviceList = new ArrayList<>();
            for(int i=1;i<5;i++){
                Device device = new Device();
                device.setRouterId(i + "." + i + "." + i + "." + i);
                List<DeviceInterface> deviceInterfaceList = device.getDeviceInterfaceList();
                for(int j=1; j<4; j++){
                    DeviceInterface deviceInterface = new DeviceInterface();
                    deviceInterface.setIp(new Address(i + "." + i + "." + i + "." +j, AddressTypeEnum.V4));
                    deviceInterfaceList.add(deviceInterface);
                }
                deviceList.add(device);
            }
            topoInfo.setDeviceList(deviceList);

            List<Link> linkList = new ArrayList<>();
            for(int i=1; i<10; i++){
                Link link = new Link();
                link.setId(i);

                DeviceInterface deviceInterface1 = new DeviceInterface();
                Device device = new Device();
                device.setRouterId(i + "." + i + "." + i + "." + i);
                deviceInterface1.setIp(new Address(i + "." + i + "." + i + "." + 1, AddressTypeEnum.V4));
                deviceInterface1.setDevice(device);
                link.setDeviceInterface1(deviceInterface1);

                DeviceInterface deviceInterface2 = new DeviceInterface();
                deviceInterface2.setIp(new Address(i + "." + i + "." + i + "." + 1, AddressTypeEnum.V4));
                deviceInterface2.setDevice(device);
                link.setDeviceInterface2(deviceInterface2);

                linkList.add(link);
            }

            topoInfo.setLinkList(linkList);
            return topoInfo;
        }
        return this.topoService.getTopoInfo();
    }
}
