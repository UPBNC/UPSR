package cn.org.upbnc.service.entity;

import java.util.ArrayList;
import java.util.List;

public class DetectTunnelServiceEntity {
        String packetSend;
        String packetRecv;
        String lossRatio;
        String status;
        String errorType;
        List<TunnelHopServiceEntity> tunnelHopServiceEntityList;

    public DetectTunnelServiceEntity() {
        this.tunnelHopServiceEntityList = new ArrayList<>();
    }

    public String getPacketSend() {
        return packetSend;
    }

    public void setPacketSend(String packetSend) {
        this.packetSend = packetSend;
    }

    public String getPacketRecv() {
        return packetRecv;
    }

    public void setPacketRecv(String packetRecv) {
        this.packetRecv = packetRecv;
    }

    public String getLossRatio() {
        return lossRatio;
    }

    public void setLossRatio(String lossRatio) {
        this.lossRatio = lossRatio;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public List<TunnelHopServiceEntity> getTunnelHopServiceEntityList() {
        return tunnelHopServiceEntityList;
    }

    public void setTunnelHopServiceEntityList(List<TunnelHopServiceEntity> tunnelHopServiceEntityList) {
        this.tunnelHopServiceEntityList = tunnelHopServiceEntityList;
    }

    public void addTunnelHopServiceEntityList (TunnelHopServiceEntity tunnelHopServiceEntity) {
        this.tunnelHopServiceEntityList.add(tunnelHopServiceEntity);
    }
}
