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
    private Integer srStatus;
    private String name;
    private Integer status;
    private Address ip;
    private Address mask;
    private Address mac;
    private VPNInstance vpn;

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
    }

    public DeviceInterface(Integer id,
                           Device device,
                           Integer srStatus,
                           String name,
                           Integer status,
                           Address ip,
                           Address mask,
                           Address mac,
                           VPNInstance vpn) {
        this.id = id;
        this.device = device;
        this.srStatus = srStatus;
        this.name = name;
        this.status = status;
        this.ip = ip;
        this.mask = mask;
        this.mac = mac;
        this.vpn = vpn;
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
}
