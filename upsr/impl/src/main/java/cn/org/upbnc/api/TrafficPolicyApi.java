package cn.org.upbnc.api;

import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.entity.TrafficPolicy.AclInfoServiceEntity;

import java.util.Map;

public interface TrafficPolicyApi {

    boolean setServiceInterface(ServiceInterface serviceInterface);

    Map<String, Object> getAclInfo(String routerId, String aclName);
    Map<String, Object> addAclInfo(AclInfoServiceEntity aclInfoServiceEntity);
}
