package cn.org.upbnc.service.entity;

public class PingTunnelServiceEntity {
    String packetSend;
    String packetRecv;
    String lossRatio;
    String rttValue;

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

    public String getRttValue() {
        return rttValue;
    }

    public void setRttValue(String rttValue) {
        this.rttValue = rttValue;
    }
}
