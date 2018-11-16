/*
 * Copyright © 2018 Copyright (c) 2018 BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.entity;

import java.util.ArrayList;
import java.util.List;

public class Device {
    // Base property
    private Integer id;
    private String deviceName;
    private String sysName;
    private String routerId;
    private Integer deviceType;
    private Integer dateCenter;

    private NetConf netConf;

    //private SRInfo srInfo;
    private Integer minNodeSID;
    private Integer maxNodeSID;

    private Integer srStatus;
    private Integer bgpAS;
    private Integer ospfId;

    private List<DeviceInterface> deviceInterfaceList;
    private LoopBack loopBack;
    private List<VPNInstance> vpnInstanceList;
    private List<Tunnel> tunnelList;

    //private Integer status;

//    // Local property
//    private Integer type;// default: 0
//    private String routerId;
//    private String sysname;
//    private String password;
//    private NetConfConnect netConfConnect;
//    private BGPConnect bgpConnect;
//    private Address address;
//    private List<Port> portList;

    public Device() {
//        this.id = 0;
//        this.name = null;
//        this.status = 0;
//        this.type = 0;
//        this.password = null;
//        this.netConfConnect = null;
//        this.bgpConnect = null;
//        this.address = null;
//        this.portList = new ArrayList<>();
//        this.sysname = null;
//        this.routerId = null;
    }

//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setStatus(Integer status) {
//        this.status = status;
//    }
//
//    public Integer getStatus() {
//        return status;
//    }
//
//    public void setType(Integer type) {
//        this.type = type;
//    }
//
//    public Integer getType() {
//        return type;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setNetConfConnect(NetConfConnect netConfConnect) {
//        this.netConfConnect = netConfConnect;
//    }
//
//    public NetConfConnect getNetConfConnect() {
//        return netConfConnect;
//    }
//
//    public void setBgpConnect(BGPConnect bgpConnect) {
//        this.bgpConnect = bgpConnect;
//    }
//
//    public BGPConnect getBgpConnect() {
//        return bgpConnect;
//    }
//
//    public void setAddress(Address address) {
//        this.address = address;
//    }
//
//    public Address getAddress() {
//        return address;
//    }
//
//    public void setPortList(List<Port> portList) {
//        this.portList.addAll(portList);
//    }
//
//    public List<Port> getPortList() {
//        return portList;
//    }
//
//    public void setRouterId(String routerId) {
//        this.routerId = routerId;
//    }
//
//    public String getRouterId() {
//        return routerId;
//    }
//
//    public void setSysname(String sysname) {
//        this.sysname = sysname;
//    }
//
//    public String getSysname() {
//        return sysname;
//    }
}
