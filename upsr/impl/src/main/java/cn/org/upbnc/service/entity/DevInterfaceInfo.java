/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service.entity;

import cn.org.upbnc.entity.AdjLabel;

public class DevInterfaceInfo {
    private String  ifnetName;
    private String  ifnetIP;
    private String  ifnetMask;
    private String  ifnetMac;
    private String  vpnName;
    private AdjLabel adjLabel;
    private String ifnetStatus;
    private String srStatus;
    private String linkStatus;
    private String runningStatus;
    private String trunkName;

    public DevInterfaceInfo(String ifnetName, String ifnetIP, String ifnetMask, String ifnetMac, String vpnName,
                            String ifnetStatus, String srStatus, String linkStatus, String runningStatus, String trunkName) {
        this.ifnetName = ifnetName;
        this.ifnetIP = ifnetIP;
        this.ifnetMask = ifnetMask;
        this.ifnetMac = ifnetMac;
        this.vpnName = vpnName;
        this.ifnetStatus = ifnetStatus;
        this.srStatus = srStatus;
        this.linkStatus = linkStatus;
        this.runningStatus = runningStatus;
        this.trunkName = trunkName;
    }


    public String getIfnetName() {
        return ifnetName;
    }

    public void setIfnetName(String ifnetName) {
        this.ifnetName = ifnetName;
    }

    public String getIfnetIP() {
        return ifnetIP;
    }

    public void setIfnetIP(String ifnetIP) {
        this.ifnetIP = ifnetIP;
    }

    public String getIfnetMask() {
        return ifnetMask;
    }

    public void setIfnetMask(String ifnetMask) {
        this.ifnetMask = ifnetMask;
    }

    public String getIfnetMac() {
        return ifnetMac;
    }

    public void setIfnetMac(String ifnetMac) {
        this.ifnetMac = ifnetMac;
    }

    public String getVpnName() {
        return vpnName;
    }

    public void setVpnName(String vpnName) {
        this.vpnName = vpnName;
    }

    public AdjLabel getAdjLabel() {
        return adjLabel;
    }

    public void setAdjLabel(AdjLabel adjLabel) {
        this.adjLabel = adjLabel;
    }

    public String getAdjLabelLocalValue() {
        return adjLabel.getAddressLocal().getAddress();
    }


    public String getIfnetStatus() {
        return ifnetStatus;
    }

    public void setIfnetStatus(String ifnetStatus) {
        this.ifnetStatus = ifnetStatus;
    }

    public String getSrStatus() {
        return srStatus;
    }

    public void setSrStatus(String srStatus) {
        this.srStatus = srStatus;
    }

    public String getLinkStatus() {
        return linkStatus;
    }

    public void setLinkStatus(String linkStatus) {
        this.linkStatus = linkStatus;
    }

    public String getRunningStatus() {
        return runningStatus;
    }

    public void setRunningStatus(String runningStatus) {
        this.runningStatus = runningStatus;
    }

    public String getTrunkName() {
        return trunkName;
    }

    public void setTrunkName(String trunkName) {
        this.trunkName = trunkName;
    }
}
