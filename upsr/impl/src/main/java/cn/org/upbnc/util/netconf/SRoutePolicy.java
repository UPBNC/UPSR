package cn.org.upbnc.util.netconf;

import java.util.List;

public class SRoutePolicy {
    private String name;
    private List<SRoutePolicyNode> routePolicyNodes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SRoutePolicyNode> getRoutePolicyNodes() {
        return routePolicyNodes;
    }

    public void setRoutePolicyNodes(List<SRoutePolicyNode> routePolicyNodes) {
        this.routePolicyNodes = routePolicyNodes;
    }

    @Override
    public String toString() {
        return "SRoutePolicy{" +
                "name='" + name + '\'' +
                ", routePolicyNodes=" + routePolicyNodes +
                '}';
    }
}
