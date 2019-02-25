package cn.org.upbnc.service.entity;

import java.util.ArrayList;
import java.util.List;

public class TunnelServiceEntity {
    String tunnelId;
    String tunnelName;
    String bfdMultiplier;
    String bfdrxInterval;
    String bfdtxInterval;
    String routerId;
    String egressLSRId;
    String bandwidth;
    String unNumIfName;
    List<TunnelHopServiceEntity> mainPath;
    List<TunnelHopServiceEntity> backPath;
    List<BfdServiceEntity> bfdServiceEntities;

    public TunnelServiceEntity() {
        this.mainPath = new ArrayList<>();
        this.backPath = new ArrayList<>();
        this.bfdServiceEntities = new ArrayList<>();
    }

    public String getUnNumIfName() {
        return unNumIfName;
    }

    public void setUnNumIfName(String unNumIfName) {
        this.unNumIfName = unNumIfName;
    }

    public String getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(String bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getEgressLSRId() {
        return egressLSRId;
    }

    public void setEgressLSRId(String egressLSRId) {
        this.egressLSRId = egressLSRId;
    }

    public String getTunnelName() {
        return tunnelName;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }

    public String getRouterId() {
        return routerId;
    }

    public void setRouterId(String routerId) {
        this.routerId = routerId;
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

    public List<BfdServiceEntity> getBfdServiceEntities() {
        return bfdServiceEntities;
    }

    public void setBfdServiceEntities(List<BfdServiceEntity> bfdServiceEntities) {
        this.bfdServiceEntities = bfdServiceEntities;
    }

    public void addBfdServiceEntities(BfdServiceEntity bfdServiceEntity){
        this.bfdServiceEntities.add(bfdServiceEntity);
    }

    @Override
    public String toString() {
        return "TunnelServiceEntity{" +
                "tunnelId='" + tunnelId + '\'' +
                ", tunnelName='" + tunnelName + '\'' +
                ", bfdMultiplier='" + bfdMultiplier + '\'' +
                ", bfdrxInterval='" + bfdrxInterval + '\'' +
                ", bfdtxInterval='" + bfdtxInterval + '\'' +
                ", routerId='" + routerId + '\'' +
                ", egressLSRId='" + egressLSRId + '\'' +
                ", bandwidth='" + bandwidth + '\'' +
                ", unNumIfName='" + unNumIfName + '\'' +
                ", mainPath=" + mainPath +
                ", backPath=" + backPath +
                '}';
    }
}
