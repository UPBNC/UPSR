package cn.org.upbnc.entity;

import java.util.List;

public class RoutePolicy {
    private String routerId;
    private String policyName;
    private List<RoutePolicyNode> routePolicyNodes;

    public String getRouterId() {
        return routerId;
    }

    public void setRouterId(String routerId) {
        this.routerId = routerId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public List<RoutePolicyNode> getRoutePolicyNodes() {
        return routePolicyNodes;
    }

    public void setRoutePolicyNodes(List<RoutePolicyNode> routePolicyNodes) {
        this.routePolicyNodes = routePolicyNodes;
    }

    @Override
    public String toString() {
        return "RoutePolicy{" +
                "routerId='" + routerId + '\'' +
                ", policyName='" + policyName + '\'' +
                ", routePolicyNodes=" + routePolicyNodes +
                '}';
    }
}
