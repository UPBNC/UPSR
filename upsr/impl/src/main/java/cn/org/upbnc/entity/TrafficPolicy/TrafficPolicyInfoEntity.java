package cn.org.upbnc.entity.TrafficPolicy;

import java.util.List;

public class TrafficPolicyInfoEntity {
    String trafficPolicyName;

    List<TrafficPolicyNodeInfoEntity> trafficPolicyNodeInfoEntityList;

    public List<TrafficPolicyNodeInfoEntity> getTrafficPolicyNodeInfoEntityList() {
        return trafficPolicyNodeInfoEntityList;
    }

    public void setTrafficPolicyNodeInfoEntityList(List<TrafficPolicyNodeInfoEntity> trafficPolicyNodeInfoEntityList) {
        this.trafficPolicyNodeInfoEntityList = trafficPolicyNodeInfoEntityList;
    }

    public String getTrafficPolicyName() {
        return trafficPolicyName;
    }

    public void setTrafficPolicyName(String trafficPolicyName) {
        this.trafficPolicyName = trafficPolicyName;
    }
}
