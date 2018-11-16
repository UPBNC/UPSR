/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.entity;

public class BfdSession {
    // Local
    private Integer id;
    private Device device;
    private Integer type;
    private Integer bfdId;
    private Integer minRecvTime;
    private Integer minSendTime;

    public BfdSession(){
        this.id = 0;
        this.device = null;
        this.type = 0;
        this.bfdId = 0;
        this.minRecvTime = 0;
        this.minSendTime = 0;
    }

    public BfdSession(Integer id,
                      Device device,
                      Integer type,
                      Integer bfdId,
                      Integer minRecvTime,
                      Integer minSendTime) {
        this.id = id;
        this.device = device;
        this.type = type;
        this.bfdId = bfdId;
        this.minRecvTime = minRecvTime;
        this.minSendTime = minSendTime;
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

    public void setMinRecvTime(Integer minRecvTime) {
        this.minRecvTime = minRecvTime;
    }

    public Integer getMinRecvTime() {
        return minRecvTime;
    }

    public void setMinSendTime(Integer minSendTime) {
        this.minSendTime = minSendTime;
    }

    public Integer getMinSendTime() {
        return minSendTime;
    }
}
