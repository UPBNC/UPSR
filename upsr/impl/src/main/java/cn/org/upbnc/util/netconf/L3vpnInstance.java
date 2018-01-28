/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.netconf;

import cn.org.upbnc.enumtype.VpnEnum.VpnApplyLabelEnum;
import cn.org.upbnc.enumtype.VpnEnum.VpnFrrStatusEnum;
import cn.org.upbnc.enumtype.VpnEnum.VpnTtlModeEnum;

import java.util.List;

public class L3vpnInstance {
    private static String notSet = "未设置";
    private String vrfName;
    private String vrfDescription;
    private String vrfRD;
    private String vrfRTValue;
    private List<L3vpnIf> l3vpnIfs;
    private String tunnelPolicy;
    private String vpnFrr;
    private String applyLabel;
    private String ttlMode;

    public L3vpnInstance() {
        this.vrfName = null;
        this.vrfDescription = null;
        this.vrfRD = null;
        this.vrfRTValue = null;
        this.l3vpnIfs = null;
    }

    public L3vpnInstance(String vrfName, String vrfDescription, String vrfRD, String vrfRTValue, List<L3vpnIf> l3vpnIfs,
                         String tunnelPolicy, String vpnFrr, String applyLabel, String ttlMode) {
        this.vrfName = vrfName;
        this.vrfDescription = vrfDescription;
        this.vrfRD = vrfRD;
        this.vrfRTValue = vrfRTValue;
        this.l3vpnIfs = l3vpnIfs;
        this.tunnelPolicy = ((tunnelPolicy == null) || (tunnelPolicy.equals("")) || tunnelPolicy.equals(notSet))?null:tunnelPolicy;
        this.vpnFrr = VpnFrrStatusEnum.ENABLED.getName().equals(vpnFrr)?"true":"false";
        this.applyLabel = VpnApplyLabelEnum.cmd2netconf(applyLabel);
        this.ttlMode = VpnTtlModeEnum.UNIFORM.getName().equals(ttlMode)?VpnTtlModeEnum.UNIFORM.getName():
                VpnTtlModeEnum.PIPE.getName();
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

    public String getTunnelPolicy() {
        return tunnelPolicy;
    }

    public void setTunnelPolicy(String tunnelPolicy) {
        this.tunnelPolicy = ((tunnelPolicy == null) || (tunnelPolicy.equals("")) || tunnelPolicy.equals(notSet))?null:tunnelPolicy;
    }

    public String getVpnFrr() {
        return vpnFrr;
    }

    public void setVpnFrr(String vpnFrr) {
        this.vpnFrr = (VpnFrrStatusEnum.ENABLED.getName().equals(vpnFrr) || "true".equals(vpnFrr))?"true":"false";
    }

    public String getApplyLabel() {
        return applyLabel;
    }

    public void setApplyLabel(String applyLabel) {
        this.applyLabel = VpnApplyLabelEnum.cmd2netconf(applyLabel);
    }

    public String getTtlMode() {
        return ttlMode;
    }

    public void setTtlMode(String ttlMode) {
        this.ttlMode = (VpnTtlModeEnum.UNIFORM.getName().equals(ttlMode) || VpnTtlModeEnum.UNIFORM.getName().equals(ttlMode))?
                VpnTtlModeEnum.UNIFORM.getName():VpnTtlModeEnum.PIPE.getName();
    }
}
