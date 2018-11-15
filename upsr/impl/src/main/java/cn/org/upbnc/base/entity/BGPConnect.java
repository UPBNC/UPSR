/*
 * Copyright © 2018 Copyright (c) 2018 BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.entity;

public class BGPConnect {
    // Base property
    private Integer id;
    private String name;
    private Integer status;

    // Device property
    private Device device;

    // Local property
    private Address local;
    private Address remote;
    private String as;

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

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    public void setLocal(Address local) {
        this.local = local;
    }

    public Address getLocal() {
        return local;
    }

    public void setRemote(Address remote) {
        this.remote = remote;
    }

    public Address getRemote() {
        return remote;
    }

    public void setAs(String as) {
        this.as = as;
    }

    public String getAs() {
        return as;
    }
}
