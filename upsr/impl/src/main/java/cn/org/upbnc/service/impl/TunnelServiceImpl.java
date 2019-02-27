package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.base.TunnelManager;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.enumtype.*;
import cn.org.upbnc.service.TunnelService;
import cn.org.upbnc.service.entity.DetectTunnelServiceEntity;
import cn.org.upbnc.service.entity.TunnelHopServiceEntity;
import cn.org.upbnc.service.entity.TunnelServiceEntity;
import cn.org.upbnc.util.netconf.*;
import cn.org.upbnc.util.xml.CheckXml;
import cn.org.upbnc.util.xml.ExplicitPathXml;
import cn.org.upbnc.util.xml.SrTeTunnelXml;
import cn.org.upbnc.util.xml.TunnelDetectXml;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedInts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.lang.Thread.sleep;

public class TunnelServiceImpl implements TunnelService {
    private static final Logger LOG = LoggerFactory.getLogger(TunnelServiceImpl.class);
    private static long TUNNELID_MAX = 4294967295l;
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
            LOG.info(device.getNetConf().getIp().getAddress());
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
        //String deviceIp = device.getNetConf().getIp().getAddress();
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
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
        if ("".equals(tunnelServiceEntity.getBandwidth())) {
            tunnel.setBandWidth("0");
        } else {
            tunnel.setBandWidth(tunnelServiceEntity.getBandwidth());
        }
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
        bfdSession.setType(BfdTypeEnum.Dynamic.getCode());
        tunnel.setBfdSession(bfdSession);
        AdjLabel label;
        if (tunnelServiceEntity.getMainPath().size() > 0) {
            ExplicitPath masterPath = new ExplicitPath();
            masterPath.setDevice(device);
            masterPath.setPathName(mainPathExplicitPathName);
            Map<String, AdjLabel> masterLabelMap = new LinkedHashMap<>();
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
        }
        if (tunnelServiceEntity.getBackPath().size() > 0) {
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
        }
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

    private SExplicitPath buildSExplicitPath(String explicitPathName,List<TunnelHopServiceEntity> tunnelHopServiceEntityList) {
        SExplicitPath explicitPath = new SExplicitPath();
        explicitPath.setExplicitPathName(explicitPathName);
        List<SExplicitPathHop> explicitPathHops = new ArrayList<>();
        for (TunnelHopServiceEntity entity : tunnelHopServiceEntityList) {
            SExplicitPathHop explicitPathHop = new SExplicitPathHop();
            if (entity.getIfAddress().equals(entity.getRouterId())) {
                explicitPathHop.setMplsTunnelHopSidLabelType(SExplicitPathHop.SIDLABEL_TYPE_PREFIX);
            }
            explicitPathHop.setMplsTunnelHopIndex(entity.getIndex());
            explicitPathHop.setMplsTunnelHopSidLabel(entity.getAdjlabel());
            explicitPathHops.add(explicitPathHop);
        }
        explicitPath.setExplicitPathHops(explicitPathHops);
        return explicitPath;
    }

    private boolean createExplicitPath(NetconfClient netconfClient, TunnelServiceEntity tunnelServiceEntity) {
        boolean flag = false;
        String tunnelName = tunnelServiceEntity.getTunnelName();
        List<SExplicitPath> explicitPaths = new ArrayList<>();
        if (tunnelServiceEntity.getMainPath().size() > 0) {
            explicitPaths.add(buildSExplicitPath(tunnelName + link, tunnelServiceEntity.getMainPath()));
        }
        if (tunnelServiceEntity.getBackPath().size() > 0) {
            explicitPaths.add(buildSExplicitPath(tunnelName + linkback, tunnelServiceEntity.getBackPath()));
        }
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
        List<Tunnel> tunnelList = tunnelManager.getTunnel(routerId, tunnelName);
        ExplicitPath masterPath;
        String mainPathExplicitPathName = null;
        String backPathExplicitPathName = null;
        if (tunnelList.size() > 0) {
            Tunnel tunnel = tunnelList.get(0);
            LOG.info("masterPath :" + tunnel.getMasterPath());
            masterPath = tunnel.getMasterPath();
            if (null != masterPath) {
                LOG.info("masterPath.getPathName() :" + masterPath.getPathName());
                mainPathExplicitPathName = masterPath.getPathName();
            }
            LOG.info("slvePath :" + tunnel.getSlavePath());
            if (null != tunnel.getSlavePath()) {
                backPathExplicitPathName = tunnel.getSlavePath().getPathName();
            }
        }
        boolean flag = false;
        Device device = deviceManager.getDevice(routerId);
        if (device != null) {
            LOG.info(device.getNetConf().getIp().getAddress());
        } else {
            LOG.info("get device is null,which routerId is " + routerId);
            map.put(ResponseEnum.MESSAGE.getName(), "get device is null,which routerId is " + routerId);
            return map;
        }
        //String deviceIp = device.getNetConf().getIp().getAddress();
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
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
            if (null != mainPathExplicitPathName) {
                String mainPath = tunnelName + link;
                if (mainPath.equals(mainPathExplicitPathName)) {
                    SExplicitPath mainExplicitPath = new SExplicitPath();
                    mainExplicitPath.setExplicitPathName(mainPathExplicitPathName);
                    explicitPaths.add(mainExplicitPath);
                }
            }
            if (null != backPathExplicitPathName) {
                String backPath = tunnelName + linkback;
                if (backPath.equals(backPathExplicitPathName)) {
                    SExplicitPath backExplicitPath = new SExplicitPath();
                    backExplicitPath.setExplicitPathName(backPathExplicitPathName);
                    explicitPaths.add(backExplicitPath);
                }
            }
            if (explicitPaths.size() > 0) {
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
            } else {
                device.getTunnelList().removeAll(tunnelManager.getTunnel(routerId, tunnelName));
                LOG.info("deleteTunnel routerId :" + routerId);
                boolean deleteFlag = tunnelManager.deleteTunnel(routerId, tunnelName);
                LOG.info("tunnel（" + tunnelName + "）deleteFlag :" + deleteFlag);
                map.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
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
        tunnelManager.emptyTunnelsByRouterId(routerId);
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
            LOG.info("Can not connect device by Netconf , status is Disconnected,which device routerId=" + device.getRouterId());
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
        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getRouterID());
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
            Device destDevice = this.deviceManager.getDevice(srTeTunnel.getMplsTunnelEgressLSRId());
            bfdSession = new BfdSession();
            bfdSession.setMultiplier(srTeTunnel.getMplsTeTunnelBfdDetectMultiplier());
            bfdSession.setMinSendTime(srTeTunnel.getMplsTeTunnelBfdMinTx());
            bfdSession.setMinRecvTime(srTeTunnel.getMplsTeTunnelBfdMinnRx());
            bfdSession.setType(BfdTypeEnum.Dynamic.getCode());
            tunnel.setBfdSession(bfdSession);
            tunnel.setDevice(device);
            tunnel.setBfdEnable(true);
            tunnel.setTunnelName(srTeTunnel.getTunnelName());
            tunnel.setTunnelId(srTeTunnel.getMplsTunnelIndex());
            tunnel.setDestRouterId(srTeTunnel.getMplsTunnelEgressLSRId());
            if (destDevice != null) {
                tunnel.setDestDeviceName(destDevice.getDeviceName());
            }
            tunnel.setBandWidth(srTeTunnel.getMplsTunnelBandwidth());
            explicitPaths = new ArrayList<>();
            List<SSrTeTunnelPath> srTeTunnelPaths = srTeTunnel.getSrTeTunnelPaths();
            for (SSrTeTunnelPath srTeTunnelPath : srTeTunnelPaths) {
                String pathName = srTeTunnelPath.getExplicitPathName();
                if (null != pathName) {
                    explicitPath = new SExplicitPath();
                    explicitPath.setExplicitPathName(pathName);
                    if ("primary".equals(srTeTunnelPath.getPathType())) {
                        masterPath = pathName;
                    } else {
                        slavePath = pathName;
                    }
                    explicitPaths.add(explicitPath);
                }
            }
            if(explicitPaths.size()>0){
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
                        if (hop.getMplsTunnelHopSidLabelType().equals(SExplicitPathHop.SIDLABEL_TYPE_ADJACENCY)) {
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
                        } else {
                            deviceTemp = deviceManager.getDeviceByNodeLabelValue(Integer.parseInt(hop.getMplsTunnelHopSidLabel()));
                            if (deviceTemp != null) {
                                adjLabel.setDevice(deviceTemp);
                                adjLabel.setAddressLocal(new Address(deviceTemp.getRouterId(), AddressTypeEnum.V4));
                                adjLabel.setValue(Integer.valueOf(hop.getMplsTunnelHopSidLabel()));
                                labelMap.put(hop.getMplsTunnelHopIndex(), adjLabel);
                            }
                        }
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

    @Override
    public Map<String, Object> pingTunnel(String routerId, String tunnelName, String lspPath) {
        Map<String, Object> resultMap = new HashMap<>();
        Device device = deviceManager.getDevice(routerId);
        if (device == null) {
            return buildResult(TunnelErrorCodeEnum.DEVICE_INVALID);
        }
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
        SPingLspResultInfo pingMainResult = this.pingLsp(netconfClient, tunnelName, lspPath);
        //String pingBackResult = this.pingLsp(netconfClient, tunnelName, TunnelDetectXml.LSPPATH_HOT);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.MESSAGE.getName(), pingMainResult.toString());
        return resultMap;
    }

    @Override
    public Map<String, Object> traceTunnel(String routerId, String tunnelName, String lspPath) {
        Map<String, Object> resultMap = new HashMap<>();
        Device device = deviceManager.getDevice(routerId);
        if (device == null) {
            return buildResult(TunnelErrorCodeEnum.DEVICE_INVALID);
        }
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
        STraceLspResultInfo traceMainResult = this.traceLsp(netconfClient, tunnelName, lspPath);
        //String traceBackResult = this.traceLsp(netconfClient, tunnelName, TunnelDetectXml.LSPPATH_HOT);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.MESSAGE.getName(), traceMainResult.toString());
        return resultMap;
    }

    @Override
    public Map<String, Object> detectTunnel(String routerId, String tunnelName, String lspPath) {
        Map<String, Object> resultMap = new HashMap<>();
        Device device = deviceManager.getDevice(routerId);
        if (device == null) {
            return buildResult(TunnelErrorCodeEnum.DEVICE_INVALID);
        }
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
        SPingLspResultInfo pingMainResult = this.pingLsp(netconfClient, tunnelName, lspPath);
        STraceLspResultInfo traceMainResult = this.traceLsp(netconfClient, tunnelName, lspPath);
        DetectTunnelServiceEntity detectTunnelServiceEntity = new DetectTunnelServiceEntity();
        detectTunnelServiceEntity.setPacketSend(pingMainResult.getPacketSend());
        detectTunnelServiceEntity.setPacketRecv(pingMainResult.getPacketRecv());
        detectTunnelServiceEntity.setLossRatio(pingMainResult.getLossRatio());
        detectTunnelServiceEntity.setStatus(traceMainResult.getStatus());
        detectTunnelServiceEntity.setErrorType(traceMainResult.getErrorType());
        for (STraceLspHopInfo sTraceLspHopInfo : traceMainResult.getsTraceLspHopInfoList()) {
            TunnelHopServiceEntity tunnelHopServiceEntity = new TunnelHopServiceEntity();
            tunnelHopServiceEntity.setIndex(sTraceLspHopInfo.getHopIndex());
            tunnelHopServiceEntity.setIfAddress(sTraceLspHopInfo.getDsIpAddr());
            detectTunnelServiceEntity.addTunnelHopServiceEntityList(tunnelHopServiceEntity);
        }
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(), detectTunnelServiceEntity);
        return resultMap;
    }

    private SPingLspResultInfo pingLsp(NetconfClient netconfClient, String tunnelName, String lspPath) {
        String pingLspXml = TunnelDetectXml.startLspPingXml(tunnelName, lspPath);
        LOG.info("pingLspXml : " + pingLspXml);
        String pingLspOutPutXml = netconfController.sendMessage(netconfClient, pingLspXml);
        if (CheckXml.checkOk(pingLspOutPutXml).equals(CheckXml.RESULT_OK) != true) {
            return null;
        }
        try {
            sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String pingLspResultXml = TunnelDetectXml.getLspPingResult(tunnelName);
        String pingLspResultOutPutXml = netconfController.sendMessage(netconfClient, pingLspResultXml);
        SPingLspResultInfo sPingLspResultInfo = TunnelDetectXml.pingLspResultFromResultXml(pingLspResultOutPutXml);

        String stopLspPingXml = TunnelDetectXml.stopLspPingXml(tunnelName);
        String stopLspPingOutPutXml = netconfController.sendMessage(netconfClient, stopLspPingXml);
        String deleteLspPingXml = TunnelDetectXml.deleteLspPingXml(tunnelName);
        String deleteLspPingOutPutXml = netconfController.sendMessage(netconfClient, deleteLspPingXml);
        return sPingLspResultInfo;
    }

    private STraceLspResultInfo traceLsp(NetconfClient netconfClient, String tunnelName, String lspPath) {
        String traceLspXml = TunnelDetectXml.startLspTraceXml(tunnelName, lspPath);
        LOG.info("traceLspXml : \n" + traceLspXml);
        String traceLspOutPutXml = netconfController.sendMessage(netconfClient, traceLspXml);
        if (CheckXml.checkOk(traceLspOutPutXml).equals(CheckXml.RESULT_OK) != true) {
            return null;
        }
        try {
            sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String traceLspResultXml = TunnelDetectXml.getLspTraceResult(tunnelName);
        String traceLspResultOutPutXml = netconfController.sendMessage(netconfClient, traceLspResultXml);
        STraceLspResultInfo sTraceLspResultInfo = TunnelDetectXml.traceLspResultFromResultXml(traceLspResultOutPutXml);

        String stopLspTraceXml = TunnelDetectXml.stopLspTraceXml(tunnelName);
        String stopLspTraceOutPutXml = netconfController.sendMessage(netconfClient, stopLspTraceXml);
        String deleteLspTraceXml = TunnelDetectXml.deleteLspTraceXml(tunnelName);
        String deleteLspTraceOutPutXml = netconfController.sendMessage(netconfClient, deleteLspTraceXml);
        return sTraceLspResultInfo;
    }

    @Override
    public Map<String, Object> generateTunnelName(String routerId) {
        Map<String, Object> resultMap = new HashMap<>();
        long tunnelId = 1;
        while (tunnelId <= TUNNELID_MAX) {
            List<Tunnel> tunnelList = tunnelManager.getTunnel(routerId, "Tunnel" + tunnelId);
            if (tunnelList.isEmpty()) {
                break;
            }
            tunnelId = tunnelId + 1;
        }
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(), tunnelId);
        return resultMap;
    }

    private Map<String, Object> buildResult(TunnelErrorCodeEnum tunnelErrorCodeEnum) {
        Map<String, Object> resultMap = new HashMap<>();
        if (tunnelErrorCodeEnum != TunnelErrorCodeEnum.EXECUTE_SUCCESS) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), tunnelErrorCodeEnum.getMessage());
        } else {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), CodeEnum.SUCCESS.getMessage());
        }
        return resultMap;
    }

}
