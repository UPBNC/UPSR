package cn.org.upbnc.base;

import cn.org.upbnc.entity.TunnelPolicy.TunnelPolicy;
import cn.org.upbnc.util.netconf.NetconfClient;

import java.util.List;

public interface TunnelPolicyManager {
    List<TunnelPolicy> getAllTunnelPolicys(String routerID);
    List<TunnelPolicy> getAllTunnelPolicys();
    boolean syncTunnelPolicyConf(NetconfClient netconfClient, String routerID);
}
