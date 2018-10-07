package cn.org.upbnc.base;

import cn.org.upbnc.util.netconf.NetconfClient;

import java.util.List;

public interface TunnelPolicyManager {
    List<String> getAllTunnelPolicyName();
    boolean syncTunnelPolicyConf(NetconfClient netconfClient, String routerID);
}
