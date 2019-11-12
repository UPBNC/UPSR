package cn.org.upbnc.base;

import cn.org.upbnc.entity.TrafficPolicy.*;
import cn.org.upbnc.util.netconf.NetconfClient;

import java.util.List;
import java.util.Map;

public interface TrafficPolicyManager {
    List<AclInfoEntity> getAllTunnelPolicys(String routerID, String aclName);
    Map<String, Map<String,AclInfoEntity>> getAllAclInfoEntity();
    Map<String, Map<String,TrafficClassInfoEntity>> getAllTrafficClassInfoEntity();
    Map<String, Map<String,TrafficBehaveInfoEntity>> getAllTrafficBehaveInfoEntity();
    Map<String, Map<String,TrafficPolicyInfoEntity>> getAllTrafficPolicyInfoEntity();
    Map<String, Map<String,TrafficIfPolicyInfoEntity>> getAllTrafficIfPolicyInfoEntity();
    boolean syncTrafficPolicyConf(String routerID, NetconfClient netconfClient);
}
