/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;

import cn.org.upbnc.enumtype.AddressTypeEnum;

public class Address {
    // Local
    private String address;
    private AddressTypeEnum type; //4:IPv4  6:IPv6  8:mac 10:mask
    //...get set

    public Address() {
        this.address = null;
        this.type = AddressTypeEnum.V4;
    }

    public Address(String address,AddressTypeEnum type) {
        this.address = address;
        this.type = type;
    }


    public void setType(AddressTypeEnum type) {
        this.type = type;
    }

    public AddressTypeEnum getType() {
        return type;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
