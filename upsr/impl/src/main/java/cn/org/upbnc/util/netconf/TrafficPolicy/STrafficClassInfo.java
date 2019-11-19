package cn.org.upbnc.util.netconf.TrafficPolicy;

import java.util.List;

public class STrafficClassInfo {
    String trafficClassName;
    String operator;
    List<STrafficClassAclInfo> sTrafficClassAclInfoList;

    public List<STrafficClassAclInfo> getsTrafficClassAclInfoList() {
        return sTrafficClassAclInfoList;
    }

    public void setsTrafficClassAclInfoList(List<STrafficClassAclInfo> sTrafficClassAclInfoList) {
        this.sTrafficClassAclInfoList = sTrafficClassAclInfoList;
    }

    public String getTrafficClassName() {
        return trafficClassName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setTrafficClassName(String trafficClassName) {
        this.trafficClassName = trafficClassName;
    }
}
