package cn.org.upbnc.entity.TrafficPolicy;

public class TrafficBehaveInfoEntity {
    String trafficBehaveName;

    String redirectTunnelName;

    public String getRedirectTunnelName() {
        return redirectTunnelName;
    }

    public void setRedirectTunnelName(String redirectTunnelName) {
        this.redirectTunnelName = redirectTunnelName;
    }

    public String getTrafficBehaveName() {
        return trafficBehaveName;
    }

    public void setTrafficBehaveName(String trafficBehaveName) {
        this.trafficBehaveName = trafficBehaveName;
    }
}
