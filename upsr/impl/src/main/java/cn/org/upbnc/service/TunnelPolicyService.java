package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.service.entity.TunnelPolicy.TunnelPolicyEntity;

import java.util.List;

public interface TunnelPolicyService {
    boolean setBaseInterface(BaseInterface baseInterface);
    boolean syncTunnelPolicyConf();
    List<TunnelPolicyEntity> getTunnelPolicys(String routerId);
}
