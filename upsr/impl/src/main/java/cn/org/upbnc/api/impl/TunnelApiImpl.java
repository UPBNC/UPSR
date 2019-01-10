package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.TunnelApi;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.TunnelErrorCodeEnum;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.TunnelService;
import cn.org.upbnc.service.entity.TunnelServiceEntity;
import cn.org.upbnc.util.xml.TunnelDetectXml;
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

    @Override
    public Map<String, Object> pingTunnel(String routerId, String tunnelName, String lspPath) {
        Map<String, Object> resultMap = new HashMap<>();
        if ((routerId == null) || (tunnelName == null)) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), TunnelErrorCodeEnum.INPUT_INVALID.getMessage());
            return resultMap;
        }
        String lspPathType = lspPath;
        if (lspPathType == null){
            lspPathType = TunnelDetectXml.LSPPATH_WORKING;
        }
        return tunnelService.pingTunnel(routerId, tunnelName, lspPathType);
    }

    @Override
    public Map<String, Object> traceTunnel(String routerId, String tunnelName, String lspPath) {
        Map<String, Object> resultMap = new HashMap<>();
        if ((routerId == null) || (tunnelName == null)) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), TunnelErrorCodeEnum.INPUT_INVALID.getMessage());
            return resultMap;
        }
        String lspPathType = lspPath;
        if (lspPathType == null){
            lspPathType = TunnelDetectXml.LSPPATH_WORKING;
        }
        return tunnelService.traceTunnel(routerId, tunnelName, lspPathType);
    }

    @Override
    public Map<String, Object> detectTunnel(String routerId, String tunnelName, String lspPath) {
        Map<String, Object> resultMap = new HashMap<>();
        if ((routerId == null) || (tunnelName == null) || lspPath == null) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), TunnelErrorCodeEnum.INPUT_INVALID.getMessage());
            return resultMap;
        }
        return tunnelService.detectTunnel(routerId, tunnelName, lspPath);
    }
}
