package cn.org.upbnc.util.netconf.bgp;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class BgpVrf {
    private String vrfName;

    private List<BgpPeer> BgpPeers;

    private List<NetworkRoute> NetworkRoutes;

    private List<ImportRoute> ImportRoutes;

    public BgpVrf(){
        this.vrfName=null;
        this.BgpPeers =null;
        this.NetworkRoutes =new ArrayList<NetworkRoute>();
        this.ImportRoutes =new ArrayList<ImportRoute>();
    }

    public BgpVrf(String vrfName, List<BgpPeer> BgpPeers, List<NetworkRoute> NetworkRoutes, List<ImportRoute> ImportRoutes){
        this.vrfName=vrfName;
        this.BgpPeers = BgpPeers;
        this.NetworkRoutes = NetworkRoutes;
        this.ImportRoutes = ImportRoutes;
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


