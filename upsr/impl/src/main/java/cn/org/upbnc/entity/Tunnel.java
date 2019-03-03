/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;

import java.util.List;

public class Tunnel {
    // Local
    private Integer id;
    private Device device;
    private String tunnelId;
    private String tunnelName;
    private String description;
    private String destRouterId;
    private String destDeviceName;
    private String bandWidth = "0";
    private String egressLSRId;

    private ExplicitPath masterPath;
    private ExplicitPath slavePath;

    private boolean RefreshFlag;

    private Integer bfdType;
    private BfdSession dynamicBfd;
    private BfdSession masterBfd;
    private BfdSession tunnelBfd;
    private TunnelServiceClass serviceClass;

    public Tunnel() {
    }

    public boolean isRefreshFlag() {
        return RefreshFlag;
    }

    public void setRefreshFlag(boolean refreshFlag) {
        RefreshFlag = refreshFlag;
    }

    public String getDestRouterId() {
        return destRouterId;
    }

    public void setDestRouterId(String destRouterId) {
        this.destRouterId = destRouterId;
    }

    public String getDestDeviceName() {
        return destDeviceName;
    }

    public void setDestDeviceName(String destDeviceName) {
        this.destDeviceName = destDeviceName;
    }

    public String getTunnelId() {
        return tunnelId;
    }

    public void setTunnelId(String tunnelId) {
        this.tunnelId = tunnelId;
    }

    public String getTunnelName() {
        return tunnelName;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBandWidth() {
        return bandWidth;
    }

    public void setBandWidth(String bandWidth) {
        if ("".equals(bandWidth)) {
            this.bandWidth = "0";
        } else {
            this.bandWidth = bandWidth;
        }
    }

    public ExplicitPath getMasterPath() {
        return masterPath;
    }

    public void setMasterPath(ExplicitPath masterPath) {
        this.masterPath = masterPath;
    }

    public ExplicitPath getSlavePath() {
        return slavePath;
    }

    public void setSlavePath(ExplicitPath slavePath) {
        this.slavePath = slavePath;
    }


    public BfdSession getDynamicBfd() {
        return dynamicBfd;
    }

    public void setDynamicBfd(BfdSession dynamicBfd) {
        this.dynamicBfd = dynamicBfd;
    }

    public BfdSession getMasterBfd() {
        return masterBfd;
    }

    public void setMasterBfd(BfdSession masterBfd) {
        this.masterBfd = masterBfd;
    }

    public BfdSession getTunnelBfd() {
        return tunnelBfd;
    }

    public void setTunnelBfd(BfdSession tunnelBfd) {
        this.tunnelBfd = tunnelBfd;
    }

    public void setBfdType(Integer bfdType) {
        this.bfdType = bfdType;
    }

    public Integer getBfdType() {
        return bfdType;
    }

    public TunnelServiceClass getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(TunnelServiceClass serviceClass) {
        this.serviceClass = serviceClass;
    }

    public String getEgressLSRId() {
        return egressLSRId;
    }

    public void setEgressLSRId(String egressLSRId) {
        this.egressLSRId = egressLSRId;
    }

    @Override
    public String toString() {
        return "Tunnel{" +
                "id=" + id +
                ", device=" + device +
                ", tunnelId='" + tunnelId + '\'' +
                ", tunnelName='" + tunnelName + '\'' +
                ", description='" + description + '\'' +
                ", destRouterId='" + destRouterId + '\'' +
                ", destDeviceName='" + destDeviceName + '\'' +
                ", bandWidth='" + bandWidth + '\'' +
                ", masterPath=" + masterPath +
                ", slavePath=" + slavePath +
                ", RefreshFlag=" + RefreshFlag +
                '}';
    }
}
