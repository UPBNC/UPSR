package cn.org.upbnc.service.entity;

import java.util.ArrayList;
import java.util.List;

public class TunnelServiceEntity {
    private Integer bfdType;
    private String tunnelId;
    private String tunnelName;
    private String routerId;
    private String destRouterId;
    private String bandwidth;
    private String unNumIfName;

    private List<TunnelHopServiceEntity> mainPath;
    private List<TunnelHopServiceEntity> backPath;

    private BfdServiceEntity dynamicBfd;
    private BfdServiceEntity masterBfd;
    private BfdServiceEntity tunnelBfd;

    private TunnelServiceClassEntity tunnelServiceClassEntity;


    public TunnelServiceEntity() {
        this.mainPath = new ArrayList<>();
        this.backPath = new ArrayList<>();
    }

    public Integer getBfdType() {
        return bfdType;
    }

    public void setBfdType(Integer bfdType) {
        this.bfdType = bfdType;
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

    public String getDestRouterId() {
        return destRouterId;
    }

    public void setDestRouterId(String destRouterId) {
        this.destRouterId = destRouterId;
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

    public BfdServiceEntity getMasterBfd() {
        return masterBfd;
    }

    public void setMasterBfd(BfdServiceEntity masterBfd) {
        this.masterBfd = masterBfd;
    }

    public BfdServiceEntity getTunnelBfd() {
        return tunnelBfd;
    }

    public void setTunnelBfd(BfdServiceEntity tunnelBfd) {
        this.tunnelBfd = tunnelBfd;
    }

    public BfdServiceEntity getDynamicBfd() {
        return dynamicBfd;
    }

    public void setDynamicBfd(BfdServiceEntity dynamicBfd) {
        this.dynamicBfd = dynamicBfd;
    }

    public TunnelServiceClassEntity getTunnelServiceClassEntity() {
        return tunnelServiceClassEntity;
    }

    public void setTunnelServiceClassEntity(TunnelServiceClassEntity tunnelServiceClassEntity) {
        this.tunnelServiceClassEntity = tunnelServiceClassEntity;
    }

    @Override
    public String toString() {
        return "TunnelServiceEntity{" +
                "tunnelId='" + tunnelId + '\'' +
                ", tunnelName='" + tunnelName + '\'' +
                ", routerId='" + routerId + '\'' +
                ", bandwidth='" + bandwidth + '\'' +
                ", unNumIfName='" + unNumIfName + '\'' +
                ", mainPath=" + mainPath +
                ", backPath=" + backPath +
                '}';
    }
}
