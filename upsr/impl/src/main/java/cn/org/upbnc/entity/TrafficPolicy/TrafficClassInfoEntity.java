package cn.org.upbnc.entity.TrafficPolicy;

import java.util.List;

public class TrafficClassInfoEntity {
    String trafficClassName;

    List<TrafficClassAclInfoEntity> trafficClassAclInfoEntityList;

    public List<TrafficClassAclInfoEntity> getTrafficClassAclInfoEntityList() {
        return trafficClassAclInfoEntityList;
    }

    public void setTrafficClassAclInfoEntityList(List<TrafficClassAclInfoEntity> trafficClassAclInfoEntityList) {
        this.trafficClassAclInfoEntityList = trafficClassAclInfoEntityList;
    }

    public String getTrafficClassName() {
        return trafficClassName;
    }

    public void setTrafficClassName(String trafficClassName) {
        this.trafficClassName = trafficClassName;
    }
}
