package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.service.entity.TunnelPolicy.TunnelPolicyEntity;

import java.util.List;
import java.util.Map;

public interface TunnelPolicyService {
    boolean setBaseInterface(BaseInterface baseInterface);

    boolean syncTunnelPolicyConf();

    Map<String, Object> createTunnelPolicys(List<TunnelPolicyEntity> tunnelPolicyEntities);

    Map<String, Object> deleteTunnelPolicys(List<TunnelPolicyEntity> tunnelPolicyEntities);

    List<TunnelPolicyEntity> getTunnelPolicys(String routerId);
}
