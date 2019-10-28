package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.TrafficPolicyApi;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.TrafficPolicyService;
import cn.org.upbnc.service.entity.TrafficPolicy.AclInfoServiceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrafficPolicyApiImpl implements TrafficPolicyApi {
    private static final Logger LOG = LoggerFactory.getLogger(TopoInfoApiImpl.class);
    private ServiceInterface serviceInterface;
    public static TrafficPolicyApiImpl ourInstance = new TrafficPolicyApiImpl();
    private TrafficPolicyService trafficPolicyService;

    public static TrafficPolicyApiImpl getInstance() {
        return ourInstance;
    }

    private TrafficPolicyApiImpl() {
        this.serviceInterface = null;
    }

    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        this.serviceInterface = serviceInterface;
        if (null != serviceInterface) {
            trafficPolicyService = this.serviceInterface.getTrafficPolicyService();
        }
        return true;
    }

    @Override
    public Map<String, Object> getAclInfo(String routerId, String aclName) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String,List<AclInfoServiceEntity>> aclMaps = trafficPolicyService.getAclInfo(routerId, aclName);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(),aclMaps);
        return resultMap;
    }

    @Override
    public Map<String, Object> addAclInfo(AclInfoServiceEntity aclInfoServiceEntity) {
        return null;
    }
}
