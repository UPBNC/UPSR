package cn.org.upbnc.service.entity.TrafficPolicy;

import java.util.List;

public class TrafficPolicyServiceEntity {
    String routerId;

    String trafficPolicyName;

    List<TrafficPolicyNodeServiceEntity> trafficPolicyNodeServiceEntityList;

    public List<TrafficPolicyNodeServiceEntity> getTrafficPolicyNodeServiceEntityList() {
        return trafficPolicyNodeServiceEntityList;
    }

    public void setTrafficPolicyNodeServiceEntityList(List<TrafficPolicyNodeServiceEntity> trafficPolicyNodeServiceEntityList) {
        this.trafficPolicyNodeServiceEntityList = trafficPolicyNodeServiceEntityList;
    }

    public String getRouterId() {
        return routerId;
    }

    public void setRouterId(String routerId) {
        this.routerId = routerId;
    }

    public String getTrafficPolicyName() {
        return trafficPolicyName;
    }

    public void setTrafficPolicyName(String trafficPolicyName) {
        this.trafficPolicyName = trafficPolicyName;
    }
}
