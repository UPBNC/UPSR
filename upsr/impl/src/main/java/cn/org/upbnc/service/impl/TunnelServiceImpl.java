package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.base.TunnelManager;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.service.TunnelService;
import cn.org.upbnc.service.entity.TunnelHopServiceEntity;
import cn.org.upbnc.service.entity.TunnelServiceEntity;
import cn.org.upbnc.util.netconf.*;
import cn.org.upbnc.util.xml.CheckXml;
import cn.org.upbnc.util.xml.ExplicitPathXml;
import cn.org.upbnc.util.xml.SrTeTunnelXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TunnelServiceImpl implements TunnelService {
    private static final Logger LOG = LoggerFactory.getLogger(TunnelServiceImpl.class);
    private static TunnelService tunnelService;
    private BaseInterface baseInterface = null;
    private TunnelManager tunnelManager = null;
    private NetConfManager netConfManager = null;
    private DeviceManager deviceManager = null;
    private String link = "Link";
    private String linkback = "Linkback";

    public static NetconfDevice netconfController = new NetconfDevice();

    public static TunnelService getInstance() {
        if (null == tunnelService) {
            tunnelService = new TunnelServiceImpl();
        }
        return tunnelService;
    }

    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        if (null != baseInterface) {
            tunnelManager = baseInterface.getTunnelManager();
            netConfManager = baseInterface.getNetConfManager();
            deviceManager = baseInterface.getDeviceManager();
        }
        return true;
    }

    @Override
    public Map<String, Object> createTunnel(TunnelServiceEntity tunnelServiceEntity) {
        LOG.info("createTunnel :" + tunnelServiceEntity.toString());
        Device device = deviceManager.getDevice(tunnelServiceEntity.getRouterId());
        if (device != null) {
            LOG.info(device.getAddress().getAddress());
        } else {
            LOG.info("deviceManager.getDevice is null.");
        }
        String deviceIp = device.getAddress().getAddress();
        Map<String, Object> map = new HashMap<>();
        NetconfClient netconfClient = netConfManager.getNetconClient(deviceIp);
        boolean createExplicitPathFlag = createExplicitPath(netconfClient, tunnelServiceEntity);
        if (createExplicitPathFlag) {
            boolean createTunnelFlag = createTunnel(netconfClient, tunnelServiceEntity);
            if (createTunnelFlag) {
                addTunnel(tunnelServiceEntity);
                map.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
            }
        }
        return map;
    }

    public void addTunnel(TunnelServiceEntity tunnelServiceEntity) {
        String tunnelName = tunnelServiceEntity.getTunnelName();
        String mainPathExplicitPathName = tunnelName + link;
        String backPathExplicitPathName = tunnelName + linkback;
        Tunnel tunnel = new Tunnel();
        tunnel.setBandWidth(tunnelServiceEntity.getBandwidth());
        tunnel.setBfdEnable(true);
        tunnel.setTunnelId(tunnelServiceEntity.getTunnelId());
        tunnel.setTunnelName(tunnelServiceEntity.getTunnelName());
        Device device = deviceManager.getDevice(tunnelServiceEntity.getRouterId());
        tunnel.setDevice(device);
        Device destDevice = deviceManager.getDevice(tunnelServiceEntity.getEgressLSRId());
        if (null != destDevice) {
            tunnel.setDestIP(destDevice.getAddress());
        }
        BfdSession bfdSession = new BfdSession();
        bfdSession.setDevice(device);
        bfdSession.setMinRecvTime(tunnelServiceEntity.getBfdrxInterval());
        bfdSession.setMinSendTime(tunnelServiceEntity.getBfdtxInterval());
        bfdSession.setMultiplier(tunnelServiceEntity.getBfdMultiplier());
        tunnel.setBfdSession(bfdSession);
        ExplicitPath masterPath = new ExplicitPath();
        masterPath.setDevice(device);
        masterPath.setPathName(mainPathExplicitPathName);
        Map<String, Label> masterLabelMap = new LinkedHashMap<>();
        Label label;
        for (TunnelHopServiceEntity entity : tunnelServiceEntity.getMainPath()) {
            label = new Label();
            Device entityDevice = deviceManager.getDevice(entity.getRouterId());
            label.setDevice(entityDevice);
            label.setValue(Integer.valueOf(entity.getAdjlabel()));
            masterLabelMap.put(entity.getIndex(), label);
        }
        masterPath.setLabelMap(masterLabelMap);
        tunnel.setMasterPath(masterPath);
        ExplicitPath slavePath = new ExplicitPath();
        slavePath.setDevice(device);
        slavePath.setPathName(backPathExplicitPathName);
        Map<String, Label> slaveLabelMap = new LinkedHashMap<>();
        for (TunnelHopServiceEntity entity : tunnelServiceEntity.getBackPath()) {
            label = new Label();
            Device entityDevice = deviceManager.getDevice(entity.getRouterId());
            label.setDevice(entityDevice);
            label.setValue(Integer.valueOf(entity.getAdjlabel()));
            slaveLabelMap.put(entity.getIndex(), label);
        }
        slavePath.setLabelMap(slaveLabelMap);
        tunnel.setSlavePath(slavePath);
        device.getTunnelList().add(tunnel);
        tunnelManager.createTunnel(tunnel);
    }

    private boolean createTunnel(NetconfClient netconfClient, TunnelServiceEntity tunnelServiceEntity) {
        boolean flag = false;
        SSrTeTunnelPath srTeTunnelPath;
        String tunnelName = tunnelServiceEntity.getTunnelName();
        String egressLSRId = tunnelServiceEntity.getEgressLSRId();
        String tunnelId = tunnelServiceEntity.getTunnelId();
        String bandwidth = tunnelServiceEntity.getBandwidth();
        String bfdMinRx = tunnelServiceEntity.getBfdrxInterval();
        String bfdMinTx = tunnelServiceEntity.getBfdtxInterval();
        String multiplier = tunnelServiceEntity.getBfdMultiplier();
        List<TunnelHopServiceEntity> mainPath = tunnelServiceEntity.getMainPath();
        List<TunnelHopServiceEntity> backPath = tunnelServiceEntity.getBackPath();
        List<SSrTeTunnel> srTeTunnels = new ArrayList<>();
        SSrTeTunnel srTeTunnel = new SSrTeTunnel();
        srTeTunnel.setTunnelName(tunnelName);
        srTeTunnel.setMplsTunnelEgressLSRId(egressLSRId);
        srTeTunnel.setMplsTunnelIndex(tunnelId);
        srTeTunnel.setMplsTunnelBandwidth(bandwidth);
        srTeTunnel.setMplsTeTunnelBfdMinTx(bfdMinTx);
        srTeTunnel.setMplsTeTunnelBfdMinnRx(bfdMinRx);
        srTeTunnel.setMplsTeTunnelBfdDetectMultiplier(multiplier);
        List<SSrTeTunnelPath> srTeTunnelPaths = new ArrayList<>();
        if (backPath.size() > 0) {
            srTeTunnelPath = new SSrTeTunnelPath();
            srTeTunnelPath.setPathType("hotStandby");
            srTeTunnelPath.setExplicitPathName(tunnelName + linkback);
            srTeTunnelPaths.add(srTeTunnelPath);
        }
        if (mainPath.size() > 0) {
            srTeTunnelPath = new SSrTeTunnelPath();
            srTeTunnelPath.setPathType("primary");
            srTeTunnelPath.setExplicitPathName(tunnelName + link);
            srTeTunnelPaths.add(srTeTunnelPath);
        }
        srTeTunnel.setSrTeTunnelPaths(srTeTunnelPaths);
        if (null != tunnelServiceEntity.getUnNumIfName() && !("".equals(tunnelServiceEntity.getUnNumIfName()))) {
            srTeTunnel.setUnNumIfName(tunnelServiceEntity.getUnNumIfName());
        }
        srTeTunnels.add(srTeTunnel);
        LOG.info(SrTeTunnelXml.createSrTeTunnelXml(srTeTunnels));
        String result = netconfController.sendMessage(netconfClient, SrTeTunnelXml.createSrTeTunnelXml(srTeTunnels));
        if (CheckXml.RESULT_OK.equals(CheckXml.checkOk(result))) {
            flag = true;
        }
        return flag;
    }

    private boolean createExplicitPath(NetconfClient netconfClient, TunnelServiceEntity tunnelServiceEntity) {
        boolean flag = false;
        String tunnelName = tunnelServiceEntity.getTunnelName();
        List<SExplicitPath> explicitPaths = new ArrayList<>();
        SExplicitPath explicitPath;
        List<TunnelHopServiceEntity> tunnelMainPathHopServiceEntities = tunnelServiceEntity.getMainPath();
        if (tunnelMainPathHopServiceEntities.size() > 0) {
            String mainPathExplicitPathName = tunnelName + link;
            explicitPath = new SExplicitPath();
            explicitPath.setExplicitPathName(mainPathExplicitPathName);
            List<SExplicitPathHop> explicitPathHops = new ArrayList<>();
            for (TunnelHopServiceEntity entity : tunnelMainPathHopServiceEntities) {
                SExplicitPathHop explicitPathHop = new SExplicitPathHop();
                explicitPathHop.setMplsTunnelHopIndex(entity.getIndex());
                explicitPathHop.setMplsTunnelHopSidLabel(entity.getAdjlabel());
                explicitPathHops.add(explicitPathHop);
            }
            explicitPath.setExplicitPathHops(explicitPathHops);
            explicitPaths.add(explicitPath);
        }
        List<TunnelHopServiceEntity> tunnelBackPathHopServiceEntities = tunnelServiceEntity.getBackPath();
        if (tunnelBackPathHopServiceEntities.size() > 0) {
            String backPathExplicitPathName = tunnelName + linkback;
            explicitPath = new SExplicitPath();
            explicitPath.setExplicitPathName(backPathExplicitPathName);
            List<SExplicitPathHop> explicitPathHops = new ArrayList<>();
            for (TunnelHopServiceEntity entity : tunnelBackPathHopServiceEntities) {
                SExplicitPathHop explicitPathHop = new SExplicitPathHop();
                explicitPathHop.setMplsTunnelHopIndex(entity.getIndex());
                explicitPathHop.setMplsTunnelHopSidLabel(entity.getAdjlabel());
                explicitPathHops.add(explicitPathHop);
            }
            explicitPath.setExplicitPathHops(explicitPathHops);
            explicitPaths.add(explicitPath);
        }
        LOG.info(ExplicitPathXml.createExplicitPathXml(explicitPaths));
        String result = netconfController.sendMessage(netconfClient, ExplicitPathXml.createExplicitPathXml(explicitPaths));
        if (CheckXml.RESULT_OK.equals(CheckXml.checkOk(result))) {
            flag = true;
        }
        return flag;
    }


    @Override
    public Map<String, Object> updateTunnel(TunnelServiceEntity tunnelServiceEntity) {
        return null;
    }

    @Override
    public Map<String, Object> deleteTunnel(String routerId, String tunnelName) {
        LOG.info("deleteTunnel : routerId " + routerId + " tunnelName " + tunnelName);
        Map<String, Object> map = new HashMap<>();
        map.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        String mainPathExplicitPathName = tunnelName + link;
        String backPathExplicitPathName = tunnelName + linkback;
        boolean flag = false;
        Device device = deviceManager.getDevice(routerId);
        if (device != null) {
            LOG.info(device.getAddress().getAddress());
        } else {
            LOG.info("deviceManager.getDevice is null.");
        }
        String deviceIp = device.getAddress().getAddress();
        NetconfClient netconfClient = netConfManager.getNetconClient(deviceIp);
        LOG.info(SrTeTunnelXml.getDeleteSrTeTunnelXml(tunnelName));
        String result = netconfController.sendMessage(netconfClient, SrTeTunnelXml.getDeleteSrTeTunnelXml(tunnelName));
        if (CheckXml.RESULT_OK.equals(CheckXml.checkOk(result))) {
            flag = true;
        }
        if (flag) {
            List<SExplicitPath> explicitPaths = new ArrayList<>();
            SExplicitPath mainExplicitPath = new SExplicitPath();
            mainExplicitPath.setExplicitPathName(mainPathExplicitPathName);
            explicitPaths.add(mainExplicitPath);
            SExplicitPath backExplicitPath = new SExplicitPath();
            backExplicitPath.setExplicitPathName(backPathExplicitPathName);
            explicitPaths.add(backExplicitPath);
            LOG.info(ExplicitPathXml.getDeleteExplicitPathXml(explicitPaths));
            result = netconfController.sendMessage(netconfClient, ExplicitPathXml.getDeleteExplicitPathXml(explicitPaths));
            if (CheckXml.RESULT_OK.equals(CheckXml.checkOk(result))) {
                device.getTunnelList().removeAll(tunnelManager.getTunnel(routerId, tunnelName));
                tunnelManager.deleteTunnel(routerId, tunnelName);
                map.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
            } else {
                map.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
                map.put(ResponseEnum.MESSAGE.getName(), "delete explicit path error.");
            }
        }
        return map;
    }

    @Override
    public Map<String, Object> getAllTunnel() {
        return null;
    }
}
