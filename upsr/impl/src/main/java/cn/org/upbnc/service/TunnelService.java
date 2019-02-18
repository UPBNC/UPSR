package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.service.entity.TunnelServiceEntity;

import java.util.Map;

public interface TunnelService {
    boolean setBaseInterface(BaseInterface baseInterface);

    Map<String, Object> createTunnel(TunnelServiceEntity tunnelServiceEntity);

    Map<String, Object> updateTunnel(TunnelServiceEntity tunnelServiceEntity);

    Map<String, Object> deleteTunnel(String routerId, String tunnelName);

    Map<String, Object> getAllTunnel(String routerId, String tunnelName);

    boolean syncTunnelInstanceConf();

    boolean syncTunnelInstanceConf(String routerId);

    Map<String, Object> pingTunnel(String routerId, String tunnelName, String lspPath);

    Map<String, Object> traceTunnel(String routerId, String tunnelName, String lspPath);

    Map<String, Object> detectTunnel(String routerId, String tunnelName, String lspPath);

    Map<String, Object> generateTunnelName(String routerId);
}
