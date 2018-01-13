package cn.org.upbnc.util.netconf.TrafficPolicy;

import java.util.List;

public class STrafficClassInfo {
    String trafficClassName;
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

    public void setTrafficClassName(String trafficClassName) {
        this.trafficClassName = trafficClassName;
    }
}
