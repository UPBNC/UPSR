/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;

public class LoopBack {
    private Integer id;
    private String name;
    private Device device;
    private Address ip;
    private Address mask;

    public LoopBack() {
        this.id = 0;
        this.name = null;
        this.device = null;
        this.ip = null;
        this.mask = null;
    }

    public LoopBack(Integer id, String name, Device device, Address ip, Address mask) {
        this.id = id;
        this.name = name;
        this.device = device;
        this.ip = ip;
        this.mask = mask;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setIp(Address ip) {
        this.ip = ip;
    }

    public Address getIp() {
        return ip;
    }

    public void setMask(Address mask) {
        this.mask = mask;
    }

    public Address getMask() {
        return mask;
    }

}
