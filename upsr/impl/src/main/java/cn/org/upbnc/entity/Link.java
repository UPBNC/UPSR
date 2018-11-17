/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;

public class Link {
    // Base property
    private Integer id;
    private String name;
    private DeviceInterface deviceInterface1;
    private DeviceInterface deviceInterface2;

    public Link(){
        this.id = 0;
        this.name = null;
        this.deviceInterface1 = null;
        this.deviceInterface2 = null;
    }

    public Link(Integer id, String name, DeviceInterface deviceInterface1, DeviceInterface deviceInterface2) {
        this.id = id;
        this.name = name;
        this.deviceInterface1 = deviceInterface1;
        this.deviceInterface2 = deviceInterface2;
    }

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

    public DeviceInterface getDeviceInterface1() {
        return deviceInterface1;
    }

    public void setDeviceInterface1(DeviceInterface deviceInterface1) {
        this.deviceInterface1 = deviceInterface1;
    }

    public DeviceInterface getDeviceInterface2() {
        return deviceInterface2;
    }

    public void setDeviceInterface2(DeviceInterface deviceInterface2) {
        this.deviceInterface2 = deviceInterface2;
    }
}
