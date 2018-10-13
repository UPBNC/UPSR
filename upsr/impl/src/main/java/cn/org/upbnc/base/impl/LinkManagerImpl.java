/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.LinkManager;
import cn.org.upbnc.entity.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinkManagerImpl implements LinkManager {
    private static LinkManager instance = null;
    private List<Link> linkList;
    private Integer linkId;

    private LinkManagerImpl(){
        this.linkId = 0;
        this.linkList = new ArrayList<Link>();
        return;
    }
    public static LinkManager getInstance(){
        if(null == instance){
            instance = new LinkManagerImpl();
        }
        return instance;
    }

    @Override
    public boolean addLink(Link link){
        if(null != link){
            Link tempLink = this.getLink(link);
            if(null == tempLink) {
                link.setId(this.linkId);
                this.linkList.add(link);
                this.linkId++;
                return true;
            }
        }
        return false;
    }

    @Override
    public Link getLink(Link link){
        Link ret = null;
        if(null != link){
            ret = this.getLinkByInterfaces(link.getDeviceInterface1(),link.getDeviceInterface2());
        }
        return ret;
    }

//    @Override
//    public Link getLinkByDevices(Device device1, Device device2){
//        Link link = null;
//        if(null != device1 && null != device2){
//            link = this.getLinkByDevicesName(device1.getDeviceName(),device2.getDeviceName());
//        }
//        return link;
//    }
//    @Override
//    public Link getLinkByDevicesName(String deviceName1,String deviceName2){
//        if(null != deviceName1 && null != deviceName2){
//            Iterator<Link> linkIterator = this.linkList.iterator();
//            while(linkIterator.hasNext()){
//                Link link = linkIterator.next();
//                if(deviceName1.equals(link.getDeviceInterface1().getDeviceName()) && deviceName2.equals(link.getDeviceInterface2().getDeviceName())){
//                    return link;
//                }
//            }
//        }
//        return null;
//    }

    @Override
    public Link getLinkByInterfaces(DeviceInterface deviceInterface1, DeviceInterface deviceInterface2){
        if(null != deviceInterface1 && null != deviceInterface2){
            Iterator<Link> linkIterator = this.linkList.iterator();
            while(linkIterator.hasNext()){
                Link link = linkIterator.next();
                if(this.compareInterfaces(link.getDeviceInterface1(),deviceInterface1) && this.compareInterfaces(link.getDeviceInterface2(),deviceInterface2)){
                    return link;
                }
            }
        }
        return null;
    }

    @Override
    public List<Link> getLinkList() {
        return this.linkList;
    }

    @Override
    public void setLinkList(List<Link> linkList) {
        this.linkList.clear();
        this.linkList.addAll(linkList);
        return;
    }

    private boolean compareInterfaces(DeviceInterface a,DeviceInterface b){
        if(null != a && null != b){
            boolean bDeviceName = a.getDeviceName().equals(b.getDeviceName());
            //boolean bInterfacename = a.getName().equals(b.getName());
            boolean bIp = a.getIp().getAddress().equals(b.getIp().getAddress());
            return bDeviceName&&bIp;
        }
        return false;
    }

    @Override
    public List<Link> updateLinkListByBgpLinkList(List<Device> devices,List<BgpLink> bgpLinkList){
        // 当 BGP 为空，不处理
        if(null == bgpLinkList || bgpLinkList.isEmpty()){
            this.linkList.clear();
            return this.linkList;
        }
        if(null == devices || devices.isEmpty()){
            this.linkList.clear();
            return null;
        }

        List<Link> links = new ArrayList<Link>();
        int linkId = 0;
        Iterator<BgpLink> bgpLinkIterator = bgpLinkList.iterator();
        while(bgpLinkIterator.hasNext()){
            BgpLink bgpLink = bgpLinkIterator.next();
            Link link = new Link();
            link.setId(linkId);
            link.setName(bgpLink.getName());
            link.setMetric(bgpLink.getMetric());
            link.setDeviceInterface1(this.findDeviceInterfaceByBgpInterface(devices,bgpLink.getBgpDeviceInterface1()));
            link.setDeviceInterface2(this.findDeviceInterfaceByBgpInterface(devices,bgpLink.getBgpDeviceInterface2()));
            linkId++;
            links.add(link);
        }
        this.linkList = links;
        return this.linkList;
    }

    private DeviceInterface findDeviceInterfaceByBgpInterface(List<Device> devices, BgpDeviceInterface bgpDeviceInterface){
        Iterator<Device> deviceIterator = devices.iterator();
        while(deviceIterator.hasNext()){
            Device device = deviceIterator.next();
            String bgpDeviceName = bgpDeviceInterface.getBgpDeviceName();
            BgpDevice bgpDevice = device.getBgpDevice();
            if( null != bgpDevice && bgpDevice.getName().equals(bgpDeviceName)){
                return this.findDeviceInterface(device.getDeviceInterfaceList(),bgpDeviceInterface);
            }
        }
        return null;
    }

    private DeviceInterface findDeviceInterface(List<DeviceInterface> deviceInterfaceList,BgpDeviceInterface bgpDeviceInterface){
        Iterator<DeviceInterface> deviceInterfaceIterator = deviceInterfaceList.iterator();
        while(deviceInterfaceIterator.hasNext()){
            DeviceInterface deviceInterface = deviceInterfaceIterator.next();
            if(deviceInterface.getIp().getAddress().equals(bgpDeviceInterface.getIp().getAddress())){
                return deviceInterface;
            }
        }
        return null;
    }

    @Override
    public DeviceInterface getPeerDeviceInterface(DeviceInterface deviceInterface) {
        if(this.linkList == null){
            return null;
        }
        Iterator<Link> linkIterator = this.linkList.iterator();
        while (linkIterator.hasNext()){
            Link link = linkIterator.next();
            if(link.getDeviceInterface1() == deviceInterface){
                return link.getDeviceInterface2();
            }
        }
        return null;
    }
}
