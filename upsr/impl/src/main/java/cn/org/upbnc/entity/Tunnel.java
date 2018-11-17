/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;

public class Tunnel {
    // Local
    private Integer id;
    private Device device;
    private Integer tunnelID;
    private String description;
    private Address destIP;
    private Integer bandWidth;
    private ExplicitPath masterPath;
    private ExplicitPath slavePath;
    private Boolean bfdEnable;
    private BfdSession bfdSession;

    public Tunnel() {
        this.id = 0;
        this.device = null;
        this.description = null;
        this.destIP = null;
        this.bandWidth = 0;
        this.masterPath = null;
        this.slavePath = null;
        this.bfdEnable =false;
        this.bfdSession = null;
    }

    public Tunnel(Integer id,
                  Device device,
                  Integer tunnelID,
                  String description,
                  Address destIP,
                  Integer bandWidth,
                  ExplicitPath masterPath,
                  ExplicitPath slavePath,
                  Boolean bfdEnable,
                  BfdSession bfdSession) {
        this.id = id;
        this.device = device;
        this.tunnelID = tunnelID;
        this.description = description;
        this.destIP = destIP;
        this.bandWidth = bandWidth;
        this.masterPath = masterPath;
        this.slavePath = slavePath;
        this.bfdEnable = bfdEnable;
        this.bfdSession = bfdSession;
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

    public Integer getTunnelID() {
        return tunnelID;
    }

    public void setTunnelID(Integer tunnelID) {
        this.tunnelID = tunnelID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Address getDestIP() {
        return destIP;
    }

    public void setDestIP(Address destIP) {
        this.destIP = destIP;
    }


    public Integer getBandWidth() {
        return bandWidth;
    }

    public void setBandWidth(Integer bandWidth) {
        this.bandWidth = bandWidth;
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

    public BfdSession getBfdSession() {
        return bfdSession;
    }

    public Boolean getBfdEnable() {
        return bfdEnable;
    }

    public void setBfdEnable(Boolean bfdEnable) {
        this.bfdEnable = bfdEnable;
    }

    public void setBfdSession(BfdSession bfdSession) {
        this.bfdSession = bfdSession;
    }
}
