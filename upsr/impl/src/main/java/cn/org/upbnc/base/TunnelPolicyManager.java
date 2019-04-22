package cn.org.upbnc.base;

import cn.org.upbnc.entity.TunnelPolicy.TunnelPolicy;
import cn.org.upbnc.util.netconf.NetconfClient;

import java.util.List;

public interface TunnelPolicyManager {

    List<TunnelPolicy> getAllTunnelPolicys(String routerID);

    List<TunnelPolicy> getAllTunnelPolicys();

    TunnelPolicy getTunnelPolicy(String name);

    boolean createTunnelPolicy(TunnelPolicy tunnelPolicy,NetconfClient netconfClient);

    boolean createTunnelPolicyList(List<TunnelPolicy> tunnelPolicies,NetconfClient netconfClient);

    boolean deleteTunnelPolicyByName(String name,NetconfClient netconfClient);

    boolean deleteTunnelPolicyByNameList(List<String> names,NetconfClient netconfClient,String routerId);

    boolean syncTunnelPolicyConf(String routerID,NetconfClient netconfClient);

    boolean isNameDuplicate(String name);
}
