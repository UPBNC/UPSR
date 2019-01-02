package cn.org.upbnc.api;

import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.entity.ExplicitPathServiceEntity;
import cn.org.upbnc.service.entity.TunnelServiceEntity;

import java.util.List;
import java.util.Map;

public interface TunnelApi {
    boolean setServiceInterface(ServiceInterface serviceInterface);

    Map<String, Object> updateTunnel(TunnelServiceEntity tunnelServiceEntity, ExplicitPathServiceEntity explicitPathServiceEntity);

    Map<String, Object> deleteTunnel(String routerId, String tunnelId);

    Map<String, Object> getAllTunnel();
}
