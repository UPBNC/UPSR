/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.netconf;

import java.util.List;

public class L3vpnInstance {
    private String vrfName;
    private String vrfDescription;
    private String vrfRD;
    private String vrfRTValue;
    private List<L3vpnIf> l3vpnIfs;

    public L3vpnInstance() {
        this.vrfName = null;
        this.vrfDescription = null;
        this.vrfRD = null;
        this.vrfRTValue = null;
        this.l3vpnIfs = null;
    }

    public L3vpnInstance(String vrfName, String vrfDescription, String vrfRD, String vrfRTValue, List<L3vpnIf> l3vpnIfs) {
        this.vrfName = vrfName;
        this.vrfDescription = vrfDescription;
        this.vrfRD = vrfRD;
        this.vrfRTValue = vrfRTValue;
        this.l3vpnIfs = l3vpnIfs;
    }

    public String getVrfName() {
        return vrfName;
    }

    public void setVrfName(String vrfName) {
        this.vrfName = vrfName;
    }

    public String getVrfDescription() {
        return vrfDescription;
    }

    public void setVrfDescription(String vrfDescription) {
        this.vrfDescription = vrfDescription;
    }

    public String getVrfRD() {
        return vrfRD;
    }

    public void setVrfRD(String vrfRD) {
        this.vrfRD = vrfRD;
    }

    public String getVrfRTValue() {
        return vrfRTValue;
    }

    public void setVrfRTValue(String vrfRTValue) {
        this.vrfRTValue = vrfRTValue;
    }

    public List<L3vpnIf> getL3vpnIfs() {
        return l3vpnIfs;
    }

    public void setL3vpnIfs(List<L3vpnIf> l3vpnIfs) {
        this.l3vpnIfs = l3vpnIfs;
    }
}
