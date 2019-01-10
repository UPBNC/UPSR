package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.TunnelApi;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.TunnelService;
import cn.org.upbnc.service.entity.TunnelServiceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TunnelApiImpl implements TunnelApi {
    private static final Logger LOG = LoggerFactory.getLogger(TunnelApiImpl.class);
    private ServiceInterface serviceInterface;
    public static TunnelApi ourInstance = new TunnelApiImpl();
    private TunnelService tunnelService;

    public static TunnelApi getInstance() {
        return ourInstance;
    }

    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        boolean ret = false;
        this.serviceInterface = serviceInterface;
        if (null != serviceInterface) {
            tunnelService = serviceInterface.getTunnelService();
            ret = true;
        }
        return ret;
    }

    @Override
    public Map<String, Object> createTunnel(TunnelServiceEntity tunnelServiceEntity) {
        Map<String, Object> map = new HashMap<>();
        map.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        if (null == tunnelServiceEntity.getTunnelName() || "".equals(tunnelServiceEntity.getTunnelName()) ||
                null == tunnelServiceEntity.getTunnelId() || "".equals(tunnelServiceEntity.getTunnelId()) ||
                null == tunnelServiceEntity.getRouterId() || "".equals(tunnelServiceEntity.getRouterId())) {
            map.put(ResponseEnum.MESSAGE.getName(), "tunnel name ,id or routerId is null");
            return map;
        }
        return tunnelService.createTunnel(tunnelServiceEntity);
    }

    @Override
    public Map<String, Object> updateTunnel(TunnelServiceEntity tunnelServiceEntity) {
        return null;
    }

    @Override
    public Map<String, Object> deleteTunnel(String routerId, String tunnelName) {
        Map<String, Object> map = new HashMap<>();
        map.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        if (null == routerId || "".equals(routerId) || null == tunnelName || "".equals(tunnelName)) {
            map.put(ResponseEnum.MESSAGE.getName(), "tunnel name or routerId is null");
            return map;
        }
        return tunnelService.deleteTunnel(routerId, tunnelName);
    }

    @Override
    public Map<String, Object> getAllTunnel(String routerId, String tunnelName) {
        return tunnelService.getAllTunnel(routerId, tunnelName);
    }
}
