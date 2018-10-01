/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;

public class DeviceInterface {
    private Integer id;
    private Device device;
    private String deviceName;
    private Integer srStatus;
    private String name;
    private Integer status;
    private Address ip;
    private Address mask;
    private Address mac;
    private VPNInstance vpn;
    private Integer bgpStatus;
    private AdjLabel adjLabel;

    public DeviceInterface() {
        this.id = 0;
        this.device = null;
        this.srStatus = 0;
        this.name = null;
        this.status = 0;
        this.ip = null;
        this.mask = null;
        this.mac = null;
        this.vpn = null;
        this.deviceName = null;
        this.bgpStatus = 0;
        this.adjLabel = null;
    }

    public DeviceInterface(String name, Address ip, Address mask) {
        this.name = name;
        this.ip = ip;
        this.mask = mask;
        this.bgpStatus = 0;
    }

    public DeviceInterface(Integer id,
                           Device device,
                           String deviceName,
                           Integer srStatus,
                           String name,
                           Integer status,
                           Address ip,
                           Address mask,
                           Address mac,
                           VPNInstance vpn,
                           Integer bgpStatus,
                           AdjLabel adjLabel) {
        this.id = id;
        this.device = device;
        this.deviceName = deviceName;
        this.srStatus = srStatus;
        this.name = name;
        this.status = status;
        this.ip = ip;
        this.mask = mask;
        this.mac = mac;
        this.vpn = vpn;
        this.bgpStatus = bgpStatus;
        this.adjLabel = adjLabel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Integer getSrStatus() {
        return srStatus;
    }

    public void setSrStatus(Integer srStatus) {
        this.srStatus = srStatus;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Address getIp() {
        return ip;
    }

    public void setIp(Address ip) {
        this.ip = ip;
    }

    public Address getMask() {
        return mask;
    }

    public void setMask(Address mask) {
        this.mask = mask;
    }

    public Address getMac() {
        return mac;
    }

    public void setMac(Address mac) {
        this.mac = mac;
    }

    public void setVpn(VPNInstance vpn) {
        this.vpn = vpn;
    }

    public VPNInstance getVpn() {
        return vpn;
    }

    public AdjLabel getAdjLabel() {
        return adjLabel;
    }

    public void setAdjLabel(AdjLabel adjLabel) {
        this.adjLabel = adjLabel;
    }

    public void setBgpStatus(Integer bgpStatus) {
        this.bgpStatus = bgpStatus;
    }

    public Integer getBgpStatus() {
        return bgpStatus;
    }
}
