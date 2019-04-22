package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.RoutePolicyApi;
import cn.org.upbnc.api.TunnelPolicyApi;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.service.RoutePolicyService;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.entity.RoutePolicyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutePolicyApiImpl implements RoutePolicyApi {
    private static final Logger LOG = LoggerFactory.getLogger(VpnInstanceApiImpl.class);
    private static RoutePolicyApi instance = new RoutePolicyApiImpl();
    private ServiceInterface serviceInterface;
    private RoutePolicyService routePolicyService;

    public static RoutePolicyApi getInstance() {
        return instance;
    }

    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        this.serviceInterface = serviceInterface;
        if (null != serviceInterface) {
            routePolicyService = this.serviceInterface.getRoutePolicyService();
        }
        return true;
    }

    @Override
    public Map<String, Object> getRoutePolicyMap(String routerId, String policyName) {
        Map<String, Object> resultMap = new HashMap<>();
        if (null == this.routePolicyService) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.BODY.getName(), null);
            resultMap.put(ResponseEnum.MESSAGE.getName(), "RoutePolicyApiImpl-getRoutePolicyMap() : " +
                    "routePolicyService " +
                    "is null.");
            return resultMap;
        }
        List<RoutePolicyEntity> routePolicyEntities = routePolicyService.getRoutePolicys(routerId, policyName);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(), routePolicyEntities);
        return resultMap;
    }

    @Override
    public Map<String, Object> createRoutePolicys(List<RoutePolicyEntity> routePolicyEntities) {
        Map<String, Object> resultMap = new HashMap<>();
        boolean flag = routePolicyService.createRoutePolicys(routePolicyEntities);
        if (flag) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        } else {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> deleteRoutePolicys(List<RoutePolicyEntity> routePolicyEntities) {
        Map<String, Object> resultMap = new HashMap<>();
        boolean flag = routePolicyService.deleteRoutePolicys(routePolicyEntities);
        if (flag) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        } else {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        }
        return resultMap;
    }
}
