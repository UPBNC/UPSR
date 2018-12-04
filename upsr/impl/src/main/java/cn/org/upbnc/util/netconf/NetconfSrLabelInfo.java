/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.netconf;

public class NetconfSrLabelInfo {
    String ospfProcessId;
    String ospfAreaId;

    String srgbBegin;
    String srgbEnd;
    String prefixIfName;
    String prefixLabel;

    String adjLowerSid;
    String adjUpperSid;

    public String getPrefixIfName() {
        return prefixIfName;
    }

    public void setPrefixIfName(String prefixIfName) {
        this.prefixIfName = prefixIfName;
    }

    public String getSrgbBegin() {
        return srgbBegin;
    }

    public void setSrgbBegin(String srgbBegin) {
        this.srgbBegin = srgbBegin;
    }

    public String getSrgbEnd() {
        return srgbEnd;
    }

    public void setSrgbEnd(String srgbEnd) {
        this.srgbEnd = srgbEnd;
    }

    public String getPrefixLabel() {
        return prefixLabel;
    }

    public void setPrefixLabel(String prefixLabel) {
        this.prefixLabel = prefixLabel;
    }

    public String getAdjLowerSid() {
        return adjLowerSid;
    }

    public void setAdjLowerSid(String adjLowerSid) {
        this.adjLowerSid = adjLowerSid;
    }

    public String getAdjUpperSid() {
        return adjUpperSid;
    }

    public void setAdjUpperSid(String adjUpperSid) {
        this.adjUpperSid = adjUpperSid;
    }

    public String getOspfProcessId() {
        return ospfProcessId;
    }

    public void setOspfProcessId(String ospfProcessId) {
        this.ospfProcessId = ospfProcessId;
    }

    public String getOspfAreaId() {
        return ospfAreaId;
    }

    public void setOspfAreaId(String ospfAreaId) {
        this.ospfAreaId = ospfAreaId;
    }
}
