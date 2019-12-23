package cn.org.upbnc.util.netconf;

public class SPingLspResultInfo {
    String tunnelName;
    String packetSend;
    String packetRecv;
    String lossRatio;
    String resultType;
    String rttValue;

    public String getTunnelName() {
        return tunnelName;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
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

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getRttValue() {
        return rttValue;
    }

    public void setRttValue(String rttValue) {
        this.rttValue = rttValue;
    }

    @Override
    public String toString() {
        String ret = "Ping result: " + " \n " +
                "tunnelName : " + this.tunnelName + "; \n " +
                "packetSend : " + this.packetSend + "; \n " +
                "packetRecv : " + this.packetRecv + "; \n " +
                "lossRatio  : " + this.lossRatio + "%; \n " +
                "rttValue   : " + this.rttValue + "  ;\n ";
        return ret;
    }
}
