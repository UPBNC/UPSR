package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.service.entity.TrafficPolicy.AclInfoServiceEntity;

import java.util.List;
import java.util.Map;

public interface TrafficPolicyService {
    boolean setBaseInterface(BaseInterface baseInterface);

    boolean syncTrafficPolicyConf(String routerId);

    Map<String,List<AclInfoServiceEntity>> getAclInfo(String routerId, String aclName);
}
