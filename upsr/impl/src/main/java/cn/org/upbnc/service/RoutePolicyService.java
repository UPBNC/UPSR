package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.entity.RoutePolicy;
import cn.org.upbnc.service.entity.RoutePolicyEntity;
import cn.org.upbnc.service.entity.TunnelPolicy.TunnelPolicyEntity;

import java.util.List;
import java.util.Map;

public interface RoutePolicyService {
    boolean setBaseInterface(BaseInterface baseInterface);

    boolean syncRoutePolicyConf();

    boolean createRoutePolicys(List<RoutePolicyEntity> routePolicyEntities);

    Map<String, Object> deleteRoutePolicys(List<RoutePolicyEntity> routePolicyEntities);

    List<RoutePolicyEntity> getRoutePolicys(String routerId, String policyName);
}
