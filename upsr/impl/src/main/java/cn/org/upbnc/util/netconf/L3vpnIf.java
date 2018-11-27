/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.netconf;

public class L3vpnIf {
    private String ifName;
    private String ipv4Addr;
    private String subnetMask;

    public L3vpnIf() {
        this.ifName = null;
        this.ipv4Addr = null;
        this.subnetMask = null;
    }

    public L3vpnIf(String ifName, String ipv4Addr, String subnetMask) {
        this.ifName = ifName;
        this.ipv4Addr = ipv4Addr;
        this.subnetMask = subnetMask;
    }

    public String getIfName() {
        return ifName;
    }

    public void setIfName(String ifName) {
        this.ifName = ifName;
    }

    public String getIpv4Addr() {
        return ipv4Addr;
    }

    public void setIpv4Addr(String ipv4Addr) {
        this.ipv4Addr = ipv4Addr;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }
}
