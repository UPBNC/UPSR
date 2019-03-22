package cn.org.upbnc.api;

import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.entity.TunnelPolicy.TunnelPolicyEntity;

import java.util.List;
import java.util.Map;

public interface TunnelPolicyApi {
    boolean setServiceInterface(ServiceInterface serviceInterface);
    Map<String, Object> getTunnelPolicyMap(String routerId);
    Map<String, Object> createTunnelPolicys(List<TunnelPolicyEntity> tunnelPolicyEntities);

    Map<String, Object> deleteTunnelPolicys(List<TunnelPolicyEntity> tunnelPolicyEntities);
}
