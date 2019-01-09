package cn.org.upbnc.api;

import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.entity.TunnelServiceEntity;

import java.util.List;
import java.util.Map;

public interface TunnelApi {
    boolean setServiceInterface(ServiceInterface serviceInterface);

    Map<String, Object> createTunnel(TunnelServiceEntity tunnelServiceEntity);

    Map<String, Object> updateTunnel(TunnelServiceEntity tunnelServiceEntity);

    Map<String, Object> deleteTunnel(String routerId, String tunnelName);

    Map<String, Object> getAllTunnel(String routerId, String tunnelName);
}
