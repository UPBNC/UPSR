/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;

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

    public BGPConnect() {
        this.id = 0;
        this.name = null;
        this.status = 0;
        this.device = null;
        this.local = null;
        this.remote = null;
        this.as = null;
    }

    public BGPConnect(Integer id,
                      String name,
                      Integer status,
                      Device device,
                      Address local,
                      Address remote,
                      String as) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.device = device;
        this.local = local;
        this.remote = remote;
        this.as = as;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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


    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Address getLocal() {
        return local;
    }

    public void setLocal(Address local) {
        this.local = local;
    }

    public Address getRemote() {
        return remote;
    }

    public void setRemote(Address remote) {
        this.remote = remote;
    }

    public String getAs() {
        return as;
    }

    public void setAs(String as) {
        this.as = as;
    }
}
