package cn.org.upbnc.entity.TrafficPolicy;

import java.util.List;

public class TrafficClassInfoEntity {
    String trafficClassName;
    String operator;

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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
