/*
 * Copyright © 2018 Copyright (c) 2018 BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.entity;

public class NetConfConnect {
    // Base property
    private Integer id;
    private String name;
    private Integer status;

    // Device property
    private Device device;

    // Local property
    private String port;
    private Address ip;


    // Functions
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Address getIp() {
        return this.ip;
    }

    public void setIp(Address ip) {
        this.ip = ip;
    }

    public Integer getStats() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
