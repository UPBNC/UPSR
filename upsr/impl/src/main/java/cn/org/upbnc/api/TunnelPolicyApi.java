package cn.org.upbnc.api;

import cn.org.upbnc.service.ServiceInterface;

import java.util.Map;

public interface TunnelPolicyApi {
    boolean setServiceInterface(ServiceInterface serviceInterface);
    Map<String, Object> getTunnelPolicyMap(String routerId);
}
