package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.service.entity.TrafficPolicy.*;

import java.util.List;
import java.util.Map;

public interface TrafficPolicyService {
    boolean setBaseInterface(BaseInterface baseInterface);

    boolean syncTrafficPolicyConf(String routerId);

    Map<String,List<AclInfoServiceEntity>> getAclInfo(String routerId, String aclName);
    Map<String,List<TrafficClassServiceEntity>> getTrafficClassInfo(String routerId, String trafficClassName);
    Map<String,List<TrafficBehaveServiceEntity>> getTrafficBehaveInfo(String routerId, String trafficBehaveName);
    Map<String,List<TrafficPolicyServiceEntity>> getTrafficPolicyInfo(String routerId, String trafficPolicyName);
    Map<String,List<TrafficIfPolicyServiceEntity>> getTrafficIfPolicyInfo(String routerId, String ifName);
}
