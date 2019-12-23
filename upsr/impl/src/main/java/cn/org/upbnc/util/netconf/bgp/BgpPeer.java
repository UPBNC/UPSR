/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.netconf.bgp;

public class BgpPeer {
    private String peerAddr;
    private String remoteAs;

    public void setRemoteAs(String remoteAs) {
        this.remoteAs = remoteAs;
    }

    public void setPeerAddr(String peerAddr) {
        this.peerAddr = peerAddr;
    }

    public String getRemoteAs() {
        return remoteAs;
    }

    public String getPeerAddr() {
        return peerAddr;
    }
    public BgpPeer(String peerAddr, String remoteAs){
        this.peerAddr=peerAddr;
        this.remoteAs=remoteAs;
    }

    @Override
    public String toString() {
        return "BgpPeer{" +
                "peerAddr='" + peerAddr + '\'' +
                ", remoteAs='" + remoteAs + '\'' +
                '}';
    }
}
