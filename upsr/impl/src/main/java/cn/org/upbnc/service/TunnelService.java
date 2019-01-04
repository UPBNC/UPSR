package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.service.entity.TunnelServiceEntity;

import java.util.Map;

public interface TunnelService {
    boolean setBaseInterface(BaseInterface baseInterface);

    Map<String, Object> createTunnel(TunnelServiceEntity tunnelServiceEntity);

    Map<String, Object> updateTunnel(TunnelServiceEntity tunnelServiceEntity);

    Map<String, Object> deleteTunnel(String routerId, String tunnelName);

    Map<String, Object> getAllTunnel();
}
