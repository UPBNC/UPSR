package cn.org.upbnc.service.entity.TrafficPolicy;

public class TrafficBehaveServiceEntity {
    String routerId;

    String trafficBehaveName;

    String redirectTunnelName;

    public String getRouterId() {
        return routerId;
    }

    public void setRouterId(String routerId) {
        this.routerId = routerId;
    }

    public String getTrafficBehaveName() {
        return trafficBehaveName;
    }

    public void setTrafficBehaveName(String trafficBehaveName) {
        this.trafficBehaveName = trafficBehaveName;
    }

    public String getRedirectTunnelName() {
        return redirectTunnelName;
    }

    public void setRedirectTunnelName(String redirectTunnelName) {
        this.redirectTunnelName = redirectTunnelName;
    }
}
