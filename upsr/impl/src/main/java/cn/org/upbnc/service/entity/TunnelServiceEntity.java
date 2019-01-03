package cn.org.upbnc.service.entity;

import java.util.ArrayList;
import java.util.List;

public class TunnelServiceEntity {
    String tunnelId;
    String bfdMultiplier;
    String bfdrxInterval;
    String bfdtxInterval;
    List<TunnelHopServiceEntity> mainPath;
    List<TunnelHopServiceEntity> backPath;

    public TunnelServiceEntity() {
        this.mainPath = new ArrayList<>();
        this.backPath = new ArrayList<>();
    }

    public String getTunnelId() {
        return tunnelId;
    }

    public void setTunnelId(String tunnelId) {
        this.tunnelId = tunnelId;
    }

    public String getBfdMultiplier() {
        return bfdMultiplier;
    }

    public void setBfdMultiplier(String bfdMultiplier) {
        this.bfdMultiplier = bfdMultiplier;
    }

    public String getBfdrxInterval() {
        return bfdrxInterval;
    }

    public void setBfdrxInterval(String bfdrxInterval) {
        this.bfdrxInterval = bfdrxInterval;
    }

    public String getBfdtxInterval() {
        return bfdtxInterval;
    }

    public void setBfdtxInterval(String bfdtxInterval) {
        this.bfdtxInterval = bfdtxInterval;
    }

    public List<TunnelHopServiceEntity> getMainPath() {
        return mainPath;
    }

    public void setMainPath(List<TunnelHopServiceEntity> mainPath) {
        this.mainPath = mainPath;
    }

    public List<TunnelHopServiceEntity> getBackPath() {
        return backPath;
    }

    public void setBackPath(List<TunnelHopServiceEntity> backPath) {
        this.backPath = backPath;
    }

    public void addMainPathHop(TunnelHopServiceEntity tunnelHopServiceEntity) {
        this.mainPath.add(tunnelHopServiceEntity);
    }

    public void addBackPathHop(TunnelHopServiceEntity tunnelHopServiceEntity) {
        this.backPath.add(tunnelHopServiceEntity);
    }
}
