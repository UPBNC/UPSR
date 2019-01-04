package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.TunnelApi;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.TunnelService;
import cn.org.upbnc.service.entity.TunnelServiceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
        }
        return ret;
    }

    @Override
    public Map<String, Object> createTunnel(TunnelServiceEntity tunnelServiceEntity) {
        return tunnelService.createTunnel(tunnelServiceEntity);
    }

    @Override
    public Map<String, Object> updateTunnel(TunnelServiceEntity tunnelServiceEntity) {
        return null;
    }

    @Override
    public Map<String, Object> deleteTunnel(String routerId, String tunnelName) {
        return tunnelService.deleteTunnel(routerId, tunnelName);
    }

    @Override
    public Map<String, Object> getAllTunnel() {
        return null;
    }
}
