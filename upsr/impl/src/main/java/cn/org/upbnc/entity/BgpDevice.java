/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;

import cn.org.upbnc.enumtype.DeviceTypeEnum;

import java.util.ArrayList;
import java.util.List;

public class BgpDevice {
    private Integer id;
    private String name;
    private String routerId;
    private DeviceTypeEnum deviceTypeEnum;
    private List<Prefix> prefixList;
    private Address address;
    private List<BgpDeviceInterface> bgpDeviceInterfaceList;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setRouterId(String routerId) {
        this.routerId = routerId;
    }

    public String getRouterId() {
        return routerId;
    }

    public void setDeviceTypeEnum(DeviceTypeEnum deviceTypeEnum) {
        this.deviceTypeEnum = deviceTypeEnum;
    }

    public DeviceTypeEnum getDeviceTypeEnum() {
        return deviceTypeEnum;
    }

    public void setPrefixList(List<Prefix> prefixList) {
        this.prefixList = prefixList;
    }

    public List<Prefix> getPrefixList() {
        return prefixList;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public void setBgpDeviceInterfaceList(List<BgpDeviceInterface> bgpDeviceInterfaceList) {
        this.bgpDeviceInterfaceList = bgpDeviceInterfaceList;
    }

    public List<BgpDeviceInterface> getBgpDeviceInterfaceList() {
        return bgpDeviceInterfaceList;
    }

    public void addBgpDeviceInterface(BgpDeviceInterface bgpDeviceInterface){
        if(null == this.bgpDeviceInterfaceList){
            this.bgpDeviceInterfaceList = new ArrayList<BgpDeviceInterface>();
        }
        this.bgpDeviceInterfaceList.add(bgpDeviceInterface);
    }
}
