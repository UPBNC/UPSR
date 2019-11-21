package cn.org.upbnc.base;

import cn.org.upbnc.entity.TrafficPolicy.*;
import cn.org.upbnc.util.netconf.NetconfClient;

import java.util.List;
import java.util.Map;

public interface TrafficPolicyManager {
    List<AclInfoEntity> getAllTunnelPolicys(String routerID, String aclName);
    Map<String, Map<String,AclInfoEntity>> getAllAclInfoEntity();
    Map<String, Object> deleteAclInfoEntity(String routerId, String aclName,NetconfClient netconfClient);
    Map<String, Map<String,TrafficClassInfoEntity>> getAllTrafficClassInfoEntity();
    Map<String, Object> deleteTrafficClassInfoEntity(String routerId, String trafficClassName,NetconfClient netconfClient);
    Map<String, Map<String,TrafficBehaveInfoEntity>> getAllTrafficBehaveInfoEntity();
    Map<String, Object> deleteTrafficBehaveInfoEntity(String routerId, String trafficBehaveName,NetconfClient netconfClient);
    Map<String, Map<String,TrafficPolicyInfoEntity>> getAllTrafficPolicyInfoEntity();
    Map<String, Object> deleteTrafficPolicyInfoEntity(String routerId, String trafficPolicyName,NetconfClient netconfClient);
    Map<String, Map<String,TrafficIfPolicyInfoEntity>> getAllTrafficIfPolicyInfoEntity();
    Map<String, Object> deleteTrafficIfPolicyInfoEntity(String routerId, String ifName,NetconfClient netconfClient);
    boolean syncTrafficPolicyConf(String routerID, NetconfClient netconfClient);
}
