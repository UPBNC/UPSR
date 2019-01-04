/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;

public class BfdSession {
    // Local
    private Integer id;
    private Device device;
    private Integer type;
    private Integer bfdId;
    private String minRecvTime;
    private String minSendTime;
    private String multiplier;

    public BfdSession() {
    }

    public String getMinRecvTime() {
        return minRecvTime;
    }

    public void setMinRecvTime(String minRecvTime) {
        this.minRecvTime = minRecvTime;
    }

    public String getMinSendTime() {
        return minSendTime;
    }

    public void setMinSendTime(String minSendTime) {
        this.minSendTime = minSendTime;
    }

    public String getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(String multiplier) {
        this.multiplier = multiplier;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setBfdId(Integer bfdId) {
        this.bfdId = bfdId;
    }

    public Integer getBfdId() {
        return bfdId;
    }
}
