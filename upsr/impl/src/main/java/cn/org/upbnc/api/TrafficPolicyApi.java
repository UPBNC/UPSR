package cn.org.upbnc.api;

import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.entity.TrafficPolicy.*;

import java.util.Map;

public interface TrafficPolicyApi {

    boolean setServiceInterface(ServiceInterface serviceInterface);

    Map<String, Object> getAclInfo(String routerId, String aclName);
    Map<String, Object> addAclInfo(AclInfoServiceEntity aclInfoServiceEntity);
    Map<String, Object> getTrafficClassInfo(String routerId, String trafficClassName);
    Map<String, Object> addTrafficClassInfo(TrafficClassServiceEntity trafficClassServiceEntity);
    Map<String, Object> getTrafficBehaveInfo(String routerId, String trafficBehaveName);
    Map<String, Object> addTrafficBehaveInfo(TrafficBehaveServiceEntity trafficBehaveServiceEntity);
    Map<String, Object> getTrafficPolicyInfo(String routerId, String trafficPolicyName);
    Map<String, Object> addTrafficPolicyInfo(TrafficPolicyServiceEntity trafficPolicyServiceEntity);
    Map<String, Object> getTrafficIfPolicyInfo(String routerId, String ifName);
    Map<String, Object> addTrafficIfPolicyInfo(TrafficIfPolicyServiceEntity trafficIfPolicyServiceEntity);
}
