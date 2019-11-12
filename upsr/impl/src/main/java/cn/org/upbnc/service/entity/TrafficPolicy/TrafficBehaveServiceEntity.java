package cn.org.upbnc.service.entity.TrafficPolicy;

public class TrafficBehaveServiceEntity {
    String routerId;

    String trafficBehaveName;

    String tunnelName;

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

    public String getTunnelName() {
        return tunnelName;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }
}
