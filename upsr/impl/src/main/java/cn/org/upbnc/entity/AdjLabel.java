/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;

public class AdjLabel extends Label {
    Address addressLocal;
    Address addressRemote;
    public AdjLabel() {

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
}
