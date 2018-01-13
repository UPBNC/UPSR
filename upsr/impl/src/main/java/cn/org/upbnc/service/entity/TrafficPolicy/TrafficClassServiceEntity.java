package cn.org.upbnc.service.entity.TrafficPolicy;

import java.util.List;

public class TrafficClassServiceEntity {
    String routerId;

    String trafficClassName;

    String operator;

    List<TrafficClassAclServiceEntity> trafficClassAclServiceEntityList;

    public List<TrafficClassAclServiceEntity> getTrafficClassAclServiceEntityList() {
        return trafficClassAclServiceEntityList;
    }

    public void setTrafficClassAclServiceEntityList(List<TrafficClassAclServiceEntity> trafficClassAclServiceEntityList) {
        this.trafficClassAclServiceEntityList = trafficClassAclServiceEntityList;
    }

    public String getRouterId() {
        return routerId;
    }

    public void setRouterId(String routerId) {
        this.routerId = routerId;
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
