/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;

import cn.org.upbnc.enumtype.AddressTypeEnum;
import cn.org.upbnc.enumtype.NetConfStatusEnum;

public class NetConf {
    private Integer id;
    private Device device;
    private NetConfStatusEnum status;
    private Integer port;
    private Address ip;
    private String user;
    private String upsrStaticChek;
    private String routerID;

    public NetConf() {
        this.id = 0;
        this.device = null;
        this.status = NetConfStatusEnum.Disconnected;
        this.port = 0;
        this.ip = null;
        this.user = null;
        this.upsrStaticChek = null;
    }

    public NetConf(Integer id, Device device, NetConfStatusEnum status, Integer port, Address ip, String user, String upsrStaticChek) {
        this.id = id;
        this.device = device;
        this.status = status;
        this.port = port;
        this.ip = ip;
        this.user = user;
        this.upsrStaticChek = upsrStaticChek;
    }
    public NetConf(String ip,  Integer port,  String user, String upsrStaticChek)
    {
        this.port = port;
        this.user = user;
        this.upsrStaticChek = upsrStaticChek;
        this.ip = new Address(ip, AddressTypeEnum.V4);
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    public void setStatus(NetConfStatusEnum status) {
        this.status = status;
    }

    public NetConfStatusEnum getStatus() {
        return status;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Address getIp() {
        return ip;
    }

    public void setIp(Address ip) {
        this.ip = ip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUpsrStaticChek() {
        return upsrStaticChek;
    }

    public void setUpsrStaticChek(String upsrStaticChek) {
        this.upsrStaticChek = upsrStaticChek;
    }

    public String getUpsrpwd() {
        return upsrStaticChek;
    }

    public void setUpsrpwd(String upsrStaticChek) {
        this.upsrStaticChek = upsrStaticChek;
    }

    public String getRouterID() {
        return routerID;
    }

    public void setRouterID(String routerID) {
        this.routerID = routerID;
    }
}
