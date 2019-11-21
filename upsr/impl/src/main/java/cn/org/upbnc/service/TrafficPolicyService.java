package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.service.entity.TrafficPolicy.*;

import java.util.List;
import java.util.Map;

public interface TrafficPolicyService {
    boolean setBaseInterface(BaseInterface baseInterface);
    boolean syncTrafficPolicyConf();
    boolean syncTrafficPolicyConf(String routerId);
    Map<String,List<AclInfoServiceEntity>> getAclInfo(String routerId, String aclName);
    Map<String,Object> deleteAclInfo(String routerId, String aclName);
    Map<String,List<TrafficClassServiceEntity>> getTrafficClassInfo(String routerId, String trafficClassName);
    Map<String,Object> deleteTrafficClassInfo(String routerId, String trafficClassName);
    Map<String,List<TrafficBehaveServiceEntity>> getTrafficBehaveInfo(String routerId, String trafficBehaveName);
    Map<String,Object> deleteTrafficBehaveInfo(String routerId, String trafficBehaveName);
    Map<String,List<TrafficPolicyServiceEntity>> getTrafficPolicyInfo(String routerId, String trafficPolicyName);
    Map<String,Object> deleteTrafficPolicyInfo(String routerId, String trafficPolicyName);
    Map<String,List<TrafficIfPolicyServiceEntity>> getTrafficIfPolicyInfo(String routerId, String ifName);
    Map<String,Object> deleteTrafficIfPolicyInfo(String routerId, String ifName);
}
