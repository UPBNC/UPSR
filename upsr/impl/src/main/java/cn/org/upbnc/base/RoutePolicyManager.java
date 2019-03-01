package cn.org.upbnc.base;

import cn.org.upbnc.entity.RoutePolicy;

import java.util.List;

public interface RoutePolicyManager {
    List<RoutePolicy> getRoutePolicys(String routerId, String routePolicyName);

    List<RoutePolicy> getAllRoutePolicys();

    void deletePolicys(List<RoutePolicy> routePolicies);

    void updateRoutePolicys(List<RoutePolicy> routePolicies);
}
