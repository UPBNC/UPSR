/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service.entity;
public class NetconfSession {
    private String deviceName;
    private String deviceDesc;
    private String deviceType;
    private String sysName;
    private String deviceIP;
    private Integer devicePort;
    private String userName;
    private String routerId;
    private String status;
    private String userPassword;
    private boolean flag = true;

    public NetconfSession() {
    }

    public NetconfSession(String routerId, String deviceName, String deviceDesc,
                          String deviceType, String deviceIP, Integer devicePort,
                          String userName, String userPassword) {
        this.routerId = routerId;
        this.deviceName = deviceName;
        this.deviceDesc = deviceDesc;
        this.deviceType = deviceType;
        this.userPassword = userPassword;
        this.deviceIP = deviceIP;
        this.devicePort = devicePort;
        this.userName = userName;
    }

    public NetconfSession(String routerId, String deviceName, String deviceDesc, String deviceType, String sysName, String deviceIP, Integer devicePort, String userName) {
        this.routerId = routerId;
        this.deviceName = deviceName;
        this.deviceDesc = deviceDesc;
        this.deviceType = deviceType;
        this.sysName = sysName;
        this.deviceIP = deviceIP;
        this.devicePort = devicePort;
        this.userName = userName;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getRouterId() {
        return routerId;
    }

    public void setRouterId(String routerId) {
        this.routerId = routerId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceDesc() {
        return deviceDesc;
    }

    public String getDeviceIP() {
        return deviceIP;
    }

    public Integer getDevicePort() {
        return devicePort;
    }

    public String getUserName() {
        return userName;
    }

    public String getStatus() {
        return status;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setDeviceDesc(String deviceDesc) {
        this.deviceDesc = deviceDesc;
    }

    public void setDeviceIP(String deviceIP) {
        this.deviceIP = deviceIP;
    }

    public void setDevicePort(Integer devicePort) {
        this.devicePort = devicePort;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }
}
