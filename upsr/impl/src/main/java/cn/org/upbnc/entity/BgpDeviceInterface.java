/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;

public class BgpDeviceInterface {
    private Integer id;
    private String name;
    private String bgpDeviceName;
    private Integer srStatus;
    private Integer status;
    private Address ip;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setBgpDeviceName(String bgpDeviceName) {
        this.bgpDeviceName = bgpDeviceName;
    }

    public String getBgpDeviceName() {
        return bgpDeviceName;
    }

    public void setIp(Address ip) {
        this.ip = ip;
    }

    public Address getIp() {
        return ip;
    }
}
