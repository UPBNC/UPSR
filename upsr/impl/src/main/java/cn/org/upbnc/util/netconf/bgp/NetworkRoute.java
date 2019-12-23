/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.netconf.bgp;

public class NetworkRoute {
    private String networkAddress;
    private String maskLen;

    public NetworkRoute(String networkAddress, String maskLen){
        this.networkAddress=networkAddress;
        this.maskLen=maskLen;
    }

    @Override
    public String toString() {
        return "NetworkRoute{" +
                "networkAddress='" + networkAddress + '\'' +
                ", maskLen='" + maskLen + '\'' +
                '}';
    }

    public String getNetworkAddress() {
        return networkAddress;
    }

    public String getMaskLen(){
        return maskLen;
    }

    public void setMaskLen(String maskLen) {
        this.maskLen = maskLen;
    }

    public void setNetworkAddress(String networkAddress) {
        this.networkAddress = networkAddress;
    }
}
