package cn.org.upbnc.service.entity;

import java.util.Map;

public class ExplicitPathServiceEntity {
    Map<String, TunnelHopServiceEntity> tunnelHopMain;
    Map<String, TunnelHopServiceEntity> tunnelHopBack;

    public Map<String, TunnelHopServiceEntity> getTunnelHopMain() {
        return tunnelHopMain;
    }

    public void setTunnelHopMain(Map<String, TunnelHopServiceEntity> tunnelHopMain) {
        this.tunnelHopMain = tunnelHopMain;
    }

    public Map<String, TunnelHopServiceEntity> getTunnelHopBack() {
        return tunnelHopBack;
    }

    public void setTunnelHopBack(Map<String, TunnelHopServiceEntity> tunnelHopBack) {
        this.tunnelHopBack = tunnelHopBack;
    }
}
