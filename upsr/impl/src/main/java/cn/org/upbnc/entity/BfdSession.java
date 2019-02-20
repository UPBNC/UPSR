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
    private String bfdId;
    private String minRecvTime;
    private String minSendTime;
    private String multiplier;
    private Integer timeout;
    private Integer discriminatorLocal;
    private Integer discriminatorRemote;

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

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getDiscriminatorLocal() {
        return discriminatorLocal;
    }

    public void setDiscriminatorLocal(Integer discriminatorLocal) {
        this.discriminatorLocal = discriminatorLocal;
    }

    public Integer getDiscriminatorRemote() {
        return discriminatorRemote;
    }

    public void setDiscriminatorRemote(Integer discriminatorRemote) {
        this.discriminatorRemote = discriminatorRemote;
    }

    public void setBfdId(String bfdId) {
        this.bfdId = bfdId;
    }

    public String getBfdId() {
        return bfdId;
    }

    @Override
    public String toString() {
        return "BfdSession{" +
                "id=" + id +
                ", device=" + device +
                ", type=" + type +
                ", bfdId=" + bfdId +
                ", minRecvTime='" + minRecvTime + '\'' +
                ", minSendTime='" + minSendTime + '\'' +
                ", multiplier='" + multiplier + '\'' +
                '}';
    }
}
