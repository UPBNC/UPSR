package cn.org.upbnc.base;

import cn.org.upbnc.entity.TrafficPolicy.AclInfoEntity;
import cn.org.upbnc.util.netconf.NetconfClient;

import java.util.List;
import java.util.Map;

public interface TrafficPolicyManager {
    List<AclInfoEntity> getAllTunnelPolicys(String routerID, String aclName);
    Map<String, Map<String,AclInfoEntity>> getAllTrafficPolicy();
    boolean syncTrafficPolicyConf(String routerID, NetconfClient netconfClient);
}
