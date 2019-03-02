/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;

public class Label {
    // Local
    private Integer id;
    private Device device;

    private Integer type;
    private Integer value;

    private Address addressLocal;
    private Address addressRemote;

    private String routerId;

    public Label() {
        this.id = 0;
        this.device = null;
        this.type = 0;
        this.value = 0;
    }

    public Label(Integer id, Device device, Integer type, Integer value) {
        this.id = id;
        this.device = device;
        this.type = type;
        this.value = value;
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

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Address getAddressLocal() {
        return addressLocal;
    }

    public void setAddressLocal(Address addressLocal) {
        this.addressLocal = addressLocal;
    }

    public Address getAddressRemote() {
        return addressRemote;
    }

    public void setAddressRemote(Address addressRemote) {
        this.addressRemote = addressRemote;
    }

    public String getRouterId() {
        return routerId;
    }

    public void setRouterId(String routerId) {
        this.routerId = routerId;
    }
}
