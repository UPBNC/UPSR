package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.RoutePolicyManager;
import cn.org.upbnc.base.TunnelManager;
import cn.org.upbnc.entity.RoutePolicy;
import cn.org.upbnc.entity.Tunnel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoutePolicyManagerImpl implements RoutePolicyManager {
    private static final Logger LOG = LoggerFactory.getLogger(RoutePolicyManagerImpl.class);

    private static RoutePolicyManager instance;
    public static Map<String, Map<String, RoutePolicy>> routePolicyMap = new ConcurrentHashMap<>();

    public static RoutePolicyManager getInstance() {
        if (null == instance) {
            instance = new RoutePolicyManagerImpl();
        }
        return instance;
    }

    @Override
    public List<RoutePolicy> getRoutePolicys(String routerId, String routePolicyName) {
        List<RoutePolicy> routePolicyList = new ArrayList<>();
        if (routePolicyMap.containsKey(routerId)) {
            if (null == routePolicyName || "".equals(routePolicyName)) {
                Collection<RoutePolicy> collection = routePolicyMap.get(routerId).values();
                routePolicyList = new ArrayList<RoutePolicy>(collection);
            } else {
                if (routePolicyMap.get(routerId).containsKey(routePolicyName)) {
                    routePolicyList.add(routePolicyMap.get(routerId).get(routePolicyName));
                }
            }
        }
        return routePolicyList;
    }

    @Override
    public List<RoutePolicy> getAllRoutePolicys() {
        List<RoutePolicy> routePolicyList = new ArrayList<>();
        for (String key : routePolicyMap.keySet()) {
            for (String keyRoute : routePolicyMap.get(key).keySet()) {
                routePolicyList.add(routePolicyMap.get(key).get(keyRoute));
            }
        }
        return routePolicyList;
    }

    @Override
    public void deletePolicys(List<RoutePolicy> routePolicies) {
        for (RoutePolicy routePolicy : routePolicies) {
            if (routePolicyMap.containsKey(routePolicy.getRouterId())) {
                if (routePolicyMap.get(routePolicy.getRouterId()).containsKey(routePolicy.getPolicyName())) {
                    routePolicyMap.get(routePolicy.getRouterId()).remove(routePolicy.getPolicyName());
                }
            }
        }
    }

    @Override
    public void updateRoutePolicys(List<RoutePolicy> routePolicies) {
        for (RoutePolicy routePolicy : routePolicies) {
            if (routePolicyMap.containsKey(routePolicy.getRouterId())) {
                routePolicyMap.get(routePolicy.getRouterId()).put(routePolicy.getPolicyName(), routePolicy);
            } else {
                Map<String, RoutePolicy> map = new ConcurrentHashMap<>();
                map.put(routePolicy.getPolicyName(), routePolicy);
                routePolicyMap.put(routePolicy.getRouterId(), map);
            }
        }
    }
}
