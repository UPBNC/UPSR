/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.entity;

public class NetworkSeg {
    // Local
    private Address address;
    private Address mask;

    public NetworkSeg(){
        this.address = null;
        this.mask = null;
    }

    public NetworkSeg(Address address, Address mask) {
        this.address = address;
        this.mask = mask;
    }

    public Address getMask() {
        return mask;
    }

    public void setMask(Address mask) {
        this.mask = mask;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
