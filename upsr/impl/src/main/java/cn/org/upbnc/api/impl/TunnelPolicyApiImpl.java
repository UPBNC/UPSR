package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.TunnelPolicyApi;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.TunnelPolicyService;
import cn.org.upbnc.service.entity.TunnelPolicy.TunnelPolicyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TunnelPolicyApiImpl implements TunnelPolicyApi {
    private static final Logger LOG = LoggerFactory.getLogger(VpnInstanceApiImpl.class);
    private static TunnelPolicyApi ourInstance = new TunnelPolicyApiImpl();
    private ServiceInterface serviceInterface;
    private TunnelPolicyService tunnelPolicyService;

    public static TunnelPolicyApi getInstance() {
        return ourInstance;
    }

    private TunnelPolicyApiImpl() {
        this.serviceInterface = null;
    }

    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        this.serviceInterface = serviceInterface;
        if (null != serviceInterface) {
            tunnelPolicyService = this.serviceInterface.getTunnelPolicyService();
        }
        return true;
    }

    public Map<String, Object> getTunnelPolicyMap(String routerId) {
        Map<String, Object> resultMap = new HashMap<>();
        if (null == this.tunnelPolicyService) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.BODY.getName(), null);
            resultMap.put(ResponseEnum.MESSAGE.getName(), "TunnelPolicyApiImpl-getTunnelPolicys() : " +
                    "tunnelPolicyService " +
                    "is null.");
            return resultMap;
        }
        List<TunnelPolicyEntity> tunnelPolicyEntityList = new ArrayList<TunnelPolicyEntity>();
        tunnelPolicyEntityList = tunnelPolicyService.getTunnelPolicys(routerId);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(), tunnelPolicyEntityList);
        return resultMap;
    }

    @Override
    public Map<String, Object> createTunnelPolicys(List<TunnelPolicyEntity> tunnelPolicyEntities) {
        Map<String, Object> resultMap = new HashMap<>();
        if (null == this.tunnelPolicyService) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.BODY.getName(), null);
            resultMap.put(ResponseEnum.MESSAGE.getName(), "TunnelPolicyApiImpl-createTunnelPolicys() : " +
                    "tunnelPolicyService " +
                    "is null.");
            return resultMap;
        }
        resultMap = tunnelPolicyService.createTunnelPolicys(tunnelPolicyEntities);
        return resultMap;
    }

    @Override
    public Map<String, Object> deleteTunnelPolicys(List<TunnelPolicyEntity> tunnelPolicyEntities) {
        Map<String, Object> resultMap = new HashMap<>();
        if (null == this.tunnelPolicyService) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.BODY.getName(), null);
            resultMap.put(ResponseEnum.MESSAGE.getName(), "TunnelPolicyApiImpl-deleteTunnelPolicys() : " +
                    "tunnelPolicyService " +
                    "is null.");
            return resultMap;
        }
        resultMap = tunnelPolicyService.deleteTunnelPolicys(tunnelPolicyEntities);
        return resultMap;
    }
}
