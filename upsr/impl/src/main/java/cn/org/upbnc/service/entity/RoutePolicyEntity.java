package cn.org.upbnc.service.entity;

import cn.org.upbnc.entity.RoutePolicyNode;

import java.util.List;

public class RoutePolicyEntity {
    private String routerId;
    private String policyName;
    private List<RoutePolicyNodeEntity> routePolicyNodes;

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

    public List<RoutePolicyNodeEntity> getRoutePolicyNodes() {
        return routePolicyNodes;
    }

    public void setRoutePolicyNodes(List<RoutePolicyNodeEntity> routePolicyNodes) {
        this.routePolicyNodes = routePolicyNodes;
    }

    @Override
    public String toString() {
        return "RoutePolicyEntity{" +
                "routerId='" + routerId + '\'' +
                ", policyName='" + policyName + '\'' +
                ", routePolicyNodes=" + routePolicyNodes +
                '}';
    }
}
