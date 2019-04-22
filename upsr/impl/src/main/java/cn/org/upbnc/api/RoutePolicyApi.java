package cn.org.upbnc.api;

import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.entity.RoutePolicyEntity;

import java.util.List;
import java.util.Map;

public interface RoutePolicyApi {
    boolean setServiceInterface(ServiceInterface serviceInterface);
    Map<String, Object> getRoutePolicyMap(String routerId,String policyName);
    Map<String, Object> createRoutePolicys(List<RoutePolicyEntity> tunnelPolicyEntities);
    Map<String, Object> deleteRoutePolicys(List<RoutePolicyEntity> tunnelPolicyEntities);
}
