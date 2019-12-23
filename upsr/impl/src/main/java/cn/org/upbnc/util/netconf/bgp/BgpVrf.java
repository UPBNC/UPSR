/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.netconf.bgp;

import cn.org.upbnc.util.netconf.SBgpVrfAF;

import java.util.ArrayList;
import java.util.List;

public class BgpVrf {
    private String vrfName;

    private List<BgpPeer> BgpPeers;

    private List<NetworkRoute> NetworkRoutes;

    private List<ImportRoute> ImportRoutes;

    private List<SBgpVrfAF> bgpVrfAFs;

    public BgpVrf() {
        this.vrfName = null;
        this.BgpPeers = null;
        this.NetworkRoutes = new ArrayList<NetworkRoute>();
        this.ImportRoutes = new ArrayList<ImportRoute>();
    }

    public BgpVrf(String vrfName, List<BgpPeer> BgpPeers, List<NetworkRoute> NetworkRoutes, List<ImportRoute> ImportRoutes) {
        this.vrfName = vrfName;
        this.BgpPeers = BgpPeers;
        this.NetworkRoutes = NetworkRoutes;
        this.ImportRoutes = ImportRoutes;
    }

    @Override
    public String toString() {
        return "BgpVrf{" +
                "vrfName='" + vrfName + '\'' +
                ", BgpPeers=" + BgpPeers +
                ", NetworkRoutes=" + NetworkRoutes +
                ", ImportRoutes=" + ImportRoutes +
                ", bgpVrfAFs=" + bgpVrfAFs +
                '}';
    }

    public List<SBgpVrfAF> getBgpVrfAFs() {
        return bgpVrfAFs;
    }

    public void setBgpVrfAFs(List<SBgpVrfAF> bgpVrfAFs) {
        this.bgpVrfAFs = bgpVrfAFs;
    }

    public String getVrfName() {
        return vrfName;
    }

    public List<BgpPeer> getBgpPeers() {
        return BgpPeers;
    }

    public List<ImportRoute> getImportRoutes() {
        return ImportRoutes;
    }

    public List<NetworkRoute> getNetworkRoutes() {
        return NetworkRoutes;
    }

    public void setImportRoutes(List<ImportRoute> ImportRoutes) {
        this.ImportRoutes = ImportRoutes;
    }

    public void setNetworkRoutes(List<NetworkRoute> NetworkRoutes) {
        this.NetworkRoutes = NetworkRoutes;
    }

    public void setBgpPeers(List<BgpPeer> BgpPeers) {
        this.BgpPeers = BgpPeers;
    }

    public void setVrfName(String vrfName) {
        this.vrfName = vrfName;
    }
}


