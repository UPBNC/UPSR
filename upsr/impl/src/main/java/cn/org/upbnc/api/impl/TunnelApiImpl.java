package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.TunnelApi;
import cn.org.upbnc.enumtype.BfdTypeEnum;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.TunnelErrorCodeEnum;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.TunnelService;
import cn.org.upbnc.service.entity.BfdServiceEntity;
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
        Map<String, Object> map = new HashMap<>();
        map.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        if (null == tunnelServiceEntity.getTunnelName() || "".equals(tunnelServiceEntity.getTunnelName()) ||
                null == tunnelServiceEntity.getTunnelId() || "".equals(tunnelServiceEntity.getTunnelId()) ||
                null == tunnelServiceEntity.getRouterId() || "".equals(tunnelServiceEntity.getRouterId())) {
            map.put(ResponseEnum.MESSAGE.getName(), "tunnel name ,id or routerId is null");
            return map;
        }
        if (null == serviceInterface.getNetconfSessionService().getNetconfClient(tunnelServiceEntity.getRouterId())) {
            map.put(ResponseEnum.MESSAGE.getName(), "netconfClient is null");
            return map;
        }

        // check static bfd params
        if (tunnelServiceEntity.getBfdType() == BfdTypeEnum.Static.getCode()){
            if ( !this.checkStaticBfdParams(tunnelServiceEntity.getTunnelBfd())){
                tunnelServiceEntity.setTunnelBfd(null);
                map.put(ResponseEnum.MESSAGE.getName(), "Static Bfd : Tunnel Bfd is null");
                return map;
            }

            if (!this.checkStaticBfdParams(tunnelServiceEntity.getMasterBfd())) {
                tunnelServiceEntity.setMasterBfd(null);
                map.put(ResponseEnum.MESSAGE.getName(), "Static Bfd : Master Bfd is null");
                return map;
            }
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
        if(null == serviceInterface.getNetconfSessionService().getNetconfClient(routerId)){
            map.put(ResponseEnum.MESSAGE.getName(), "netconfClient is null");
            return map;
        }
        return tunnelService.deleteTunnel(routerId, tunnelName);
    }

    @Override
    public Map<String, Object> getAllTunnel(String routerId, String tunnelName) {
        return tunnelService.getAllTunnel(routerId, tunnelName);
    }

    @Override
    public Map<String, Object> pingTunnel(String routerId, String tunnelName, String lspPath) {
        Map<String, Object> resultMap = new HashMap<>();
        if ((routerId == null) || (tunnelName == null)) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), TunnelErrorCodeEnum.INPUT_INVALID.getMessage());
            return resultMap;
        }
        if(null == serviceInterface.getNetconfSessionService().getNetconfClient(routerId)){
            resultMap.put(ResponseEnum.MESSAGE.getName(), "netconfClient is null");
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
        if(null == serviceInterface.getNetconfSessionService().getNetconfClient(routerId)){
            resultMap.put(ResponseEnum.MESSAGE.getName(), "netconfClient is null");
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
        if(null == serviceInterface.getNetconfSessionService().getNetconfClient(routerId)){
            resultMap.put(ResponseEnum.MESSAGE.getName(), "netconfClient is null");
            return resultMap;
        }
        return tunnelService.detectTunnel(routerId, tunnelName, lspPath);
    }

    @Override
    public Map<String, Object> generateTunnelName(String routerId) {
        Map<String, Object> resultMap = new HashMap<>();
        if (routerId == null) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), TunnelErrorCodeEnum.INPUT_INVALID.getMessage());
            return resultMap;
        }
        if(null == serviceInterface.getNetconfSessionService().getNetconfClient(routerId)){
            resultMap.put(ResponseEnum.MESSAGE.getName(), "netconfClient is null");
            return resultMap;
        }
        return tunnelService.generateTunnelName(routerId);
    }

    private boolean checkStaticBfdParams(BfdServiceEntity bfdServiceEntity){
        if(bfdServiceEntity.getDiscriminatorLocal() == null || bfdServiceEntity.getDiscriminatorRemote() == null ||
                bfdServiceEntity.getMinRecvTime() == null || bfdServiceEntity.getMinSendTime() == null || bfdServiceEntity.getMultiplier() == null ||
                "".equals(bfdServiceEntity.getDiscriminatorLocal()) || "".equals(bfdServiceEntity.getDiscriminatorRemote()) ||
                "".equals(bfdServiceEntity.getMinRecvTime()) || "".equals(bfdServiceEntity.getMinSendTime()) || "".equals(bfdServiceEntity.getMultiplier()) )
        {
            return false;
        }

        return true;
    }
}
