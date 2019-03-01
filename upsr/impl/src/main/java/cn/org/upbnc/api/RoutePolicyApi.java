package cn.org.upbnc.api;

import cn.org.upbnc.service.ServiceInterface;

import java.util.Map;

public interface RoutePolicyApi {
    boolean setServiceInterface(ServiceInterface serviceInterface);
    Map<String, Object> getRoutePolicyMap(String routerId,String policyName);
}
