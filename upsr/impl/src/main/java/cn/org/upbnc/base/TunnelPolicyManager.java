package cn.org.upbnc.base;

import cn.org.upbnc.entity.TunnelPolicy.TunnelPolicy;
import cn.org.upbnc.util.netconf.NetconfClient;

import java.util.List;

public interface TunnelPolicyManager {

    List<TunnelPolicy> getAllTunnelPolicys(String routerID);

    List<TunnelPolicy> getAllTunnelPolicys();

    TunnelPolicy getTunnelPolicy(String name);

    boolean createTunnelPolicy(TunnelPolicy tunnelPolicy);

    boolean createTunnelPolicyList(List<TunnelPolicy> tunnelPolicies);

    boolean deleteTunnelPolicyByName(String name);

    boolean deleteTunnelPolicyByNameList(List<String> names);

    boolean deleteTunnelPolicy(TunnelPolicy tunnelPolicy);

    boolean syncTunnelPolicyConf(NetconfClient netconfClient, String routerID);
}
