/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service.entity;

public class DevInterfaceInfo {
    private String  ifnetName;
    private String  ifnetIP;
    private String  ifnetMask;
    private String  ifnetMac;
    private String  vpnName;
    private Integer ifnetStatus;
    private Integer srStatus;
    private Integer linkStatus;
    private Integer runningStatus;

    public DevInterfaceInfo(String ifnetName, String ifnetIP, String ifnetMask, String ifnetMac, String vpnName, Integer ifnetStatus, Integer srStatus, Integer linkStatus, Integer runningStatus) {
        this.ifnetName = ifnetName;
        this.ifnetIP = ifnetIP;
        this.ifnetMask = ifnetMask;
        this.ifnetMac = ifnetMac;
        this.vpnName = vpnName;
        this.ifnetStatus = ifnetStatus;
        this.srStatus = srStatus;
        this.linkStatus = linkStatus;
        this.runningStatus = runningStatus;
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

    public Integer getIfnetStatus() {
        return ifnetStatus;
    }

    public void setIfnetStatus(Integer ifnetStatus) {
        this.ifnetStatus = ifnetStatus;
    }

    public Integer getSrStatus() {
        return srStatus;
    }

    public void setSrStatus(Integer srStatus) {
        this.srStatus = srStatus;
    }

    public Integer getLinkStatus() {
        return linkStatus;
    }

    public void setLinkStatus(Integer linkStatus) {
        this.linkStatus = linkStatus;
    }

    public Integer getRunningStatus() {
        return runningStatus;
    }

    public void setRunningStatus(Integer runningStatus) {
        this.runningStatus = runningStatus;
    }
}
