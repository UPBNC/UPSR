package cn.org.upbnc.util.netconf.TrafficPolicy;

import java.util.List;

public class STrafficPolicyInfo {
    String trafficPolicyName;

    List<STrafficPolicyNodeInfo> sTrafficPolicyNodeInfoList;

    public List<STrafficPolicyNodeInfo> getsTrafficPolicyNodeInfoList() {
        return sTrafficPolicyNodeInfoList;
    }

    public void setsTrafficPolicyNodeInfoList(List<STrafficPolicyNodeInfo> sTrafficPolicyNodeInfoList) {
        this.sTrafficPolicyNodeInfoList = sTrafficPolicyNodeInfoList;
    }

    public String getTrafficPolicyName() {
        return trafficPolicyName;
    }

    public void setTrafficPolicyName(String trafficPolicyName) {
        this.trafficPolicyName = trafficPolicyName;
    }
}
