package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.base.TunnelManager;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.NetConfStatusEnum;
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
        Map<String, Object> map = new HashMap<>();
        map.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        String routerId = tunnelServiceEntity.getRouterId();
        String tunnelId = tunnelServiceEntity.getTunnelId();
        String tunnelName = tunnelServiceEntity.getTunnelName();
        if (tunnelManager.checkTunnelNameAndId(routerId, tunnelName, tunnelId)) {
            map.put(ResponseEnum.MESSAGE.getName(), "tunnel's id or name has been exist.");
            return map;
        }
        Device device = deviceManager.getDevice(tunnelServiceEntity.getRouterId());
        if (device != null) {
            LOG.info(device.getAddress().getAddress());
        } else {
            map.put(ResponseEnum.MESSAGE.getName(), "get device is null,which routerId is " + tunnelServiceEntity.getRouterId());
            LOG.info("get device is null,which routerId is " + tunnelServiceEntity.getRouterId());
            return map;
        }
        if (!("".equals(device.getOspfProcess().getIntfName())) || null != device.getOspfProcess().getIntfName()) {
            LOG.info("device.getOspfProcess().getIntfName() :" + device.getOspfProcess().getIntfName());
            tunnelServiceEntity.setUnNumIfName(device.getOspfProcess().getIntfName());
        }
        LOG.info("device.getLoopBack() :" + device.getLoopBack());
        String deviceIp = device.getAddress().getAddress();
        NetconfClient netconfClient = netConfManager.getNetconClient(deviceIp);
        boolean createExplicitPathFlag = createExplicitPath(netconfClient, tunnelServiceEntity);
        if (createExplicitPathFlag) {
            boolean createTunnelFlag = createTunnel(netconfClient, tunnelServiceEntity);
            if (createTunnelFlag) {
                addTunnel(tunnelServiceEntity);
                map.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
            } else {
                map.put(ResponseEnum.MESSAGE.getName(), "create tunnel error,which name is : " + tunnelServiceEntity.getTunnelName());
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
        tunnel.setDestRouterId(tunnelServiceEntity.getEgressLSRId());
        tunnel.setTunnelId(tunnelServiceEntity.getTunnelId());
        tunnel.setTunnelName(tunnelServiceEntity.getTunnelName());
        Device device = deviceManager.getDevice(tunnelServiceEntity.getRouterId());
        tunnel.setDevice(device);
        Device destDevice = deviceManager.getDevice(tunnelServiceEntity.getEgressLSRId());
        if (null != destDevice) {
            tunnel.setDestDeviceName(destDevice.getDeviceName());
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
        Map<String, AdjLabel> masterLabelMap = new LinkedHashMap<>();
        AdjLabel label;
        for (TunnelHopServiceEntity entity : tunnelServiceEntity.getMainPath()) {
            label = new AdjLabel();
            Device entityDevice = deviceManager.getDevice(entity.getRouterId());
            label.setDevice(entityDevice);
            label.setValue(Integer.valueOf(entity.getAdjlabel()));
            Address address = new Address();
            address.setAddress(entity.getIfAddress());
            label.setAddressLocal(address);
            masterLabelMap.put(entity.getIndex(), label);
        }
        masterPath.setLabelMap(masterLabelMap);
        tunnel.setMasterPath(masterPath);
        ExplicitPath slavePath = new ExplicitPath();
        slavePath.setDevice(device);
        slavePath.setPathName(backPathExplicitPathName);
        Map<String, AdjLabel> slaveLabelMap = new LinkedHashMap<>();
        for (TunnelHopServiceEntity entity : tunnelServiceEntity.getBackPath()) {
            label = new AdjLabel();
            Device entityDevice = deviceManager.getDevice(entity.getRouterId());
            label.setDevice(entityDevice);
            label.setValue(Integer.valueOf(entity.getAdjlabel()));
            Address address = new Address();
            address.setAddress(entity.getIfAddress());
            label.setAddressLocal(address);
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
            LOG.info("get device is null,which routerId is " + routerId);
            map.put(ResponseEnum.MESSAGE.getName(), "get device is null,which routerId is " + routerId);
            return map;
        }
        String deviceIp = device.getAddress().getAddress();
        NetconfClient netconfClient = netConfManager.getNetconClient(deviceIp);
        LOG.info(SrTeTunnelXml.getDeleteSrTeTunnelXml(tunnelName));
        String result = netconfController.sendMessage(netconfClient, SrTeTunnelXml.getDeleteSrTeTunnelXml(tunnelName));
        if (CheckXml.RESULT_OK.equals(CheckXml.checkOk(result))) {
            flag = true;
        } else {
            map.put(ResponseEnum.MESSAGE.getName(), "delete tunnel error: " + result + " .");
            return map;
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
                LOG.info("deleteTunnel routerId :" + routerId);
                boolean deleteFlag = tunnelManager.deleteTunnel(routerId, tunnelName);
                LOG.info("tunnel（" + tunnelName + "）deleteFlag :" + deleteFlag);
                map.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
            } else {
                map.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
                map.put(ResponseEnum.MESSAGE.getName(), "delete explicit path error: " + result);
            }
        }
        return map;
    }

    @Override
    public Map<String, Object> getAllTunnel(String routerId, String tunnelName) {
        LOG.info("getAllTunnel : routerId " + routerId + " tunnelName " + tunnelName);
        Map<String, Object> map = new HashMap<>();
        if (null == tunnelName || "".equals(tunnelName)) {
            if (null == routerId || "".equals(routerId)) {
                map.put(ResponseEnum.BODY.getName(), tunnelManager.getTunnels());
            } else {
                map.put(ResponseEnum.BODY.getName(), tunnelManager.getTunnel(routerId, ""));
            }
        } else {
            map.put(ResponseEnum.BODY.getName(), tunnelManager.getTunnel(routerId, tunnelName));
        }
        map.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        return map;
    }

    @Override
    public boolean syncTunnelInstanceConf() {
        boolean flag = true;
        List<Device> devices = deviceManager.getDeviceList();
        for (Device device : devices) {
            flag = syncTunnelInstanceConf(device.getRouterId());
        }
        return flag;
    }

    @Override
    public boolean syncTunnelInstanceConf(String routerId) {
        if (null == routerId || routerId.equals("")) {
            LOG.info("syncTunnelInstanceConf failed,routerId is null or empty ");
            return false;
        }
        Device device = deviceManager.getDevice(routerId);
        tunnelManager.emptyTunnels(routerId);
        if ((null != device.getNetConf()) && (device.getNetConf().getStatus() == NetConfStatusEnum.Connected)) {
            List<Tunnel> tunnels = getTunnelInstanceListFromDevice(routerId);
            if (tunnels.size() > 0) {
                for (Tunnel tunnel : tunnels) {
                    tunnelManager.updateTunnel(tunnel);
                }
            } else {
                LOG.info("Can not get device's tunnel info which device routerId=" + device.getRouterId());
                return false;
            }
        } else {
            LOG.info("Can not connect device by Netconf , status is Disconnect,which device routerId=" + device.getRouterId());
            return false;
        }
        return true;
    }

    private List<Tunnel> getTunnelInstanceListFromDevice(String routerId) {
        List<Tunnel> tunnels = new ArrayList<>();
        Tunnel tunnel;
        Device device = this.deviceManager.getDevice(routerId);
        if ((null == device) || (null == device.getNetConf())) {
            return tunnels;
        }
        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        LOG.info("enter getTunnelInstanceListFromDevice");
        String xml = SrTeTunnelXml.getSrTeTunnelXml("");
        LOG.info(xml);
        String result = netconfController.sendMessage(netconfClient, xml);
        List<SSrTeTunnel> srTeTunnels = SrTeTunnelXml.getSrTeTunnelFromXml(result);
        List<SExplicitPath> explicitPaths;
        SExplicitPath explicitPath;
        BfdSession bfdSession;
        ExplicitPath path;
        Map<String, AdjLabel> labelMap;
        String masterPath = null;
        String slavePath;
        AdjLabel adjLabel;
        AdjLabel adjLabelTemp;
        for (SSrTeTunnel srTeTunnel : srTeTunnels) {
            tunnel = new Tunnel();
            bfdSession = new BfdSession();
            bfdSession.setMultiplier(srTeTunnel.getMplsTeTunnelBfdDetectMultiplier());
            bfdSession.setMinSendTime(srTeTunnel.getMplsTeTunnelBfdMinTx());
            bfdSession.setMinRecvTime(srTeTunnel.getMplsTeTunnelBfdMinnRx());
            tunnel.setBfdSession(bfdSession);
            tunnel.setDevice(device);
            tunnel.setBfdEnable(true);
            tunnel.setTunnelName(srTeTunnel.getTunnelName());
            tunnel.setTunnelId(srTeTunnel.getMplsTunnelIndex());
            tunnel.setDestRouterId(srTeTunnel.getMplsTunnelEgressLSRId());
            tunnel.setBandWidth(srTeTunnel.getMplsTunnelBandwidth());
            explicitPaths = new ArrayList<>();
            List<SSrTeTunnelPath> srTeTunnelPaths = srTeTunnel.getSrTeTunnelPaths();
            for (SSrTeTunnelPath srTeTunnelPath : srTeTunnelPaths) {
                explicitPath = new SExplicitPath();
                explicitPath.setExplicitPathName(srTeTunnelPath.getExplicitPathName());
                if ("primary".equals(srTeTunnelPath.getPathType())) {
                    masterPath = srTeTunnelPath.getExplicitPathName();
                } else {
                    slavePath = srTeTunnelPath.getExplicitPathName();
                }
                explicitPaths.add(explicitPath);
            }
            xml = ExplicitPathXml.getExplicitPathXml(explicitPaths);
            LOG.info(xml);
            result = netconfController.sendMessage(netconfClient, xml);
            List<SExplicitPath> paths = ExplicitPathXml.getExplicitPathFromXml(result);
            boolean flag;
            for (SExplicitPath sExplicitPath : paths) {
                Device deviceTemp = this.deviceManager.getDevice(routerId);
                flag = true;
                path = new ExplicitPath();
                labelMap = new LinkedHashMap<>();
                List<SExplicitPathHop> sExplicitPathHops = sExplicitPath.getExplicitPathHops();
                for (SExplicitPathHop hop : sExplicitPathHops) {
                    adjLabel = new AdjLabel();
                    if (flag) {
                        adjLabel.setDevice(deviceTemp);
                        for (AdjLabel label : deviceTemp.getAdjLabelList()) {
                            if (label.getValue().equals(Integer.valueOf(hop.getMplsTunnelHopSidLabel()))) {
                                adjLabel.setAddressLocal(label.getAddressLocal());
                                adjLabel.setAddressRemote(label.getAddressRemote());
                                break;
                            }
                        }
                        if (null == adjLabel.getAddressRemote()) {
                            deviceTemp = null;
                        } else {
                            deviceTemp = findLocalAndRemoteAddress(adjLabel.getAddressRemote().getAddress());
                        }
                    }
                    if (null == deviceTemp) {
                        flag = false;
                    }
                    adjLabel.setValue(Integer.valueOf(hop.getMplsTunnelHopSidLabel()));
                    labelMap.put(hop.getMplsTunnelHopIndex(), adjLabel);
                }
                path.setPathName(sExplicitPath.getExplicitPathName());
                path.setDevice(device);
                path.setLabelMap(labelMap);
                if (masterPath.equals(sExplicitPath.getExplicitPathName())) {
                    tunnel.setMasterPath(path);
                } else {
                    tunnel.setSlavePath(path);
                }
            }
            tunnels.add(tunnel);
        }
        return tunnels;
    }

    private Device findLocalAndRemoteAddress(String remoteIp) {

        Device device = null;
        if (remoteIp == null || remoteIp.equals("")) {
            return device;
        }
        List<Device> devices = deviceManager.getDeviceList();
        for (Device dev : devices) {
            List<AdjLabel> adjLabelList = dev.getAdjLabelList();
            for (AdjLabel label : adjLabelList) {
                if (label.getAddressLocal().getAddress().equals(remoteIp)) {
                    device = dev;
                    return device;
                }
            }
        }
        return device;
    }

}
