package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.base.TunnelManager;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.entity.TunnelPolicy.TpNexthop;
import cn.org.upbnc.entity.TunnelPolicy.TunnelPolicy;
import cn.org.upbnc.enumtype.*;
import cn.org.upbnc.service.TunnelService;
import cn.org.upbnc.service.entity.*;
import cn.org.upbnc.util.netconf.*;
import cn.org.upbnc.util.xml.*;
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

        if (this.tunnelManager.isTunnelNameAndIdUsed(routerId, tunnelName, tunnelId)) {
            map.put(ResponseEnum.MESSAGE.getName(), "tunnel's id or name has been exist.");
            return map;
        }

        Device device = this.deviceManager.getDevice(tunnelServiceEntity.getRouterId());
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
        if (this.createSrTunnel(tunnelServiceEntity)) {
            map.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        } else {
            map.put(ResponseEnum.MESSAGE.getName(), "Create tunnel fail");
        }
        return map;
    }

    public boolean createSrTunnel(TunnelServiceEntity tunnelServiceEntity) {
        Tunnel tunnel = new Tunnel();

        tunnel.setBandWidth(tunnelServiceEntity.getBandwidth());
        tunnel.setDestRouterId(tunnelServiceEntity.getDestRouterId());
        tunnel.setTunnelId(tunnelServiceEntity.getTunnelId());
        tunnel.setTunnelName(tunnelServiceEntity.getTunnelName());
        // Create tunnel description
        tunnel.setTunnelDesc(tunnel.getTunnelName()+"_"+TunnelDescEnum.TunnelBegin.getName()+"_"+TunnelDescEnum.End.getName());

        if (null != tunnelServiceEntity.getTunnelServiceClassEntity()) {
            TunnelServiceClassEntity tsce = tunnelServiceEntity.getTunnelServiceClassEntity();
            TunnelServiceClass tsc = new TunnelServiceClass();
            tsc.setDef(tsce.isDef());
            tsc.setAf1(tsce.isAf1());
            tsc.setAf2(tsce.isAf2());
            tsc.setAf3(tsce.isAf3());
            tsc.setAf4(tsce.isAf4());
            tsc.setBe(tsce.isBe());
            tsc.setEf(tsce.isEf());
            tsc.setCs6(tsce.isCs6());
            tsc.setCs7(tsce.isCs7());

            tunnel.setServiceClass(tsc);
        }

        Device device = this.deviceManager.getDevice(tunnelServiceEntity.getRouterId());
        tunnel.setDevice(device);

        Device destDevice = this.deviceManager.getDevice(tunnelServiceEntity.getDestRouterId());
        tunnel.setDestDeviceName(destDevice.getDeviceName());
        tunnel.setBfdType(tunnelServiceEntity.getBfdType());

        if (tunnelServiceEntity.getBfdType() == BfdTypeEnum.Dynamic.getCode()) {
            BfdSession bfdSession = new BfdSession();
            bfdSession.setDevice(device);
            bfdSession.setMinRecvTime(tunnelServiceEntity.getDynamicBfd().getMinRecvTime());
            bfdSession.setMinSendTime(tunnelServiceEntity.getDynamicBfd().getMinSendTime());
            bfdSession.setMultiplier(tunnelServiceEntity.getDynamicBfd().getMultiplier());
            bfdSession.setType(BfdTypeEnum.Dynamic.getCode());
            tunnel.setDynamicBfd(bfdSession);
        } else if (tunnelServiceEntity.getBfdType() == BfdTypeEnum.Static.getCode()) {
            BfdSession bfdTunnel = this.getBfdSessionByType(tunnelServiceEntity.getTunnelBfd(), BfdTypeEnum.Tunnel, device);
            tunnel.setTunnelBfd(bfdTunnel);
            BfdSession bfdMaster = this.getBfdSessionByType(tunnelServiceEntity.getMasterBfd(), BfdTypeEnum.Master, device);
            tunnel.setMasterBfd(bfdMaster);
        }

        tunnel.setMasterPath(this.buildTunnelPath(tunnel, tunnelServiceEntity.getMainPath(), link));
        tunnel.setSlavePath(this.buildTunnelPath(tunnel, tunnelServiceEntity.getBackPath(), linkback));

        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
        List<Tunnel> tunnelList = new ArrayList<>();
        tunnelList.add(tunnel);

        if (this.tunnelManager.createTunnels(tunnelList, tunnelServiceEntity.getRouterId(), netconfClient)) {
            return true;
        } else {
            return false;
        }
    }

    public ExplicitPath buildTunnelPath(Tunnel tunnel, List<TunnelHopServiceEntity> tunnelHopList, String expType) {
        Label label;
        if (tunnelHopList.size() > 0) {
            ExplicitPath explicitPath = new ExplicitPath();
            explicitPath.setDevice(tunnel.getDevice());
            explicitPath.setPathName(tunnel.getTunnelName() + expType);
            Map<String, Label> slaveLabelMap = new LinkedHashMap<>();
            for (TunnelHopServiceEntity entity : tunnelHopList) {
                label = new Label();
                if (entity.getIfAddress().equals(entity.getRouterId())) {
                    label.setType(LabelTypeEnum.PREFIX.getCode());
                } else {
                    label.setAddressLocal(new Address(entity.getIfAddress(), AddressTypeEnum.V4));
                    label.setType(LabelTypeEnum.ADJACENCY.getCode());
                }
                Device entityDevice = deviceManager.getDevice(entity.getRouterId());
                label.setDevice(entityDevice);
                label.setValue(Integer.valueOf(entity.getAdjlabel()));
                slaveLabelMap.put(entity.getIndex(), label);
            }
            explicitPath.setLabelMap(slaveLabelMap);
            return explicitPath;
        }
        return null;
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
        tunnel.setDestRouterId(tunnelServiceEntity.getDestRouterId());
        tunnel.setTunnelId(tunnelServiceEntity.getTunnelId());
        tunnel.setTunnelName(tunnelServiceEntity.getTunnelName());
        Device device = deviceManager.getDevice(tunnelServiceEntity.getRouterId());
        tunnel.setDevice(device);
        Device destDevice = deviceManager.getDevice(tunnelServiceEntity.getDestRouterId());
        if (null != destDevice) {
            tunnel.setDestDeviceName(destDevice.getDeviceName());
        }

        // Bfd
        // Bfd type of tunnel
        tunnel.setBfdType(tunnelServiceEntity.getBfdType());
        if (tunnelServiceEntity.getBfdType() == BfdTypeEnum.Dynamic.getCode()) {
            // Bfd dynamic
            BfdSession bfdSession = new BfdSession();
            bfdSession.setDevice(device);
            bfdSession.setMinRecvTime(tunnelServiceEntity.getDynamicBfd().getMinRecvTime());
            bfdSession.setMinSendTime(tunnelServiceEntity.getDynamicBfd().getMinSendTime());
            bfdSession.setMultiplier(tunnelServiceEntity.getDynamicBfd().getMultiplier());
            bfdSession.setType(BfdTypeEnum.Dynamic.getCode());
            tunnel.setDynamicBfd(bfdSession);
        } else if (tunnelServiceEntity.getBfdType() == BfdTypeEnum.Static.getCode()) {
            // Bfd static
            // Tunnel
            BfdSession bfdTunnel = this.getBfdSessionByType(tunnelServiceEntity.getTunnelBfd(), BfdTypeEnum.Tunnel, device);
            tunnel.setTunnelBfd(bfdTunnel);
            // Master
            BfdSession bfdMaster = this.getBfdSessionByType(tunnelServiceEntity.getMasterBfd(), BfdTypeEnum.Master, device);
            tunnel.setMasterBfd(bfdMaster);
        }


        Label label;
        if (tunnelServiceEntity.getMainPath().size() > 0) {
            ExplicitPath masterPath = new ExplicitPath();
            masterPath.setDevice(device);
            masterPath.setPathName(mainPathExplicitPathName);
            Map<String, Label> masterLabelMap = new LinkedHashMap<>();
            for (TunnelHopServiceEntity entity : tunnelServiceEntity.getMainPath()) {
                label = new Label();
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
            Map<String, Label> slaveLabelMap = new LinkedHashMap<>();
            for (TunnelHopServiceEntity entity : tunnelServiceEntity.getBackPath()) {
                label = new Label();
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
        String egressLSRId = tunnelServiceEntity.getRouterId();
        String tunnelId = tunnelServiceEntity.getTunnelId();
        String bandwidth = tunnelServiceEntity.getBandwidth();
        String bfdMinRx = tunnelServiceEntity.getDynamicBfd().getMinRecvTime();
        String bfdMinTx = tunnelServiceEntity.getDynamicBfd().getMinSendTime();
        String multiplier = tunnelServiceEntity.getDynamicBfd().getMultiplier();
        List<TunnelHopServiceEntity> mainPath = tunnelServiceEntity.getMainPath();
        List<TunnelHopServiceEntity> backPath = tunnelServiceEntity.getBackPath();
        List<SSrTeTunnel> srTeTunnels = new ArrayList<>();
        SSrTeTunnel srTeTunnel = new SSrTeTunnel();
        srTeTunnel.setTunnelName(tunnelName);
        srTeTunnel.setMplsTunnelEgressLSRId(egressLSRId);
        srTeTunnel.setMplsTunnelIndex(tunnelId);
        srTeTunnel.setMplsTunnelBandwidth(bandwidth);

        // Dynamic bfd start
        //if(tunnelServiceEntity.getBfdType() == BfdTypeEnum.Dynamic.getCode()) {
        srTeTunnel.setMplsTeTunnelBfdMinTx(bfdMinTx);
        srTeTunnel.setMplsTeTunnelBfdMinnRx(bfdMinRx);
        srTeTunnel.setMplsTeTunnelBfdDetectMultiplier(multiplier);
        //}
        // Dynamic bfd end

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

    private SExplicitPath buildSExplicitPath(String explicitPathName, List<TunnelHopServiceEntity> tunnelHopServiceEntityList) {
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
        map.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        List<Tunnel> tunnelList = tunnelManager.getTunnel(routerId, tunnelName);
        if (tunnelList == null) {
            return map;
        }
        NetconfClient netconfClient = netConfManager.getNetconClient(routerId);
        List<String> tunnelNames = new ArrayList<>();
        tunnelNames.add(tunnelName);
        if (!tunnelManager.deleteTunnels(tunnelNames, routerId, netconfClient)) {
            map.put(ResponseEnum.MESSAGE.getName(), "Delete tunnel fail");
            map.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        }
        return map;
    }

    @Override
    // modify
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

            NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getRouterID());
            Map<String, Tunnel> map = this.tunnelManager.syncTunnelsConf(routerId, netconfClient);
            for (Tunnel t : map.values()) {
                t.setDevice(device);
                Device destDevice = this.deviceManager.getDevice(t.getDestRouterId());
                if (null != destDevice) {
                    t.setDestDeviceName(destDevice.getSysName());
                }

                if (null != t.getMasterPath()) {
                    t.getMasterPath().setDevice(device);
                    this.explicitLabelListToNodeList(t.getMasterPath());
                }

                if (null != t.getSlavePath()) {
                    t.getSlavePath().setDevice(device);
                    this.explicitLabelListToNodeList(t.getSlavePath());
                }

                if (null != t.getMasterBfd()) {
                    t.getMasterBfd().setDevice(device);
                }

                if (null != t.getTunnelBfd()) {
                    t.getTunnelBfd().setDevice(device);
                }

                if (null != t.getDynamicBfd()) {
                    t.getDynamicBfd().setDevice(device);
                }
            }


//            List<Tunnel> tunnels = getTunnelInstanceListFromDevice(routerId);
//            if (tunnels.size() > 0) {
//                for (Tunnel tunnel : tunnels) {
//                    tunnelManager.updateTunnel(tunnel);
//                }
//            } else {
//                LOG.info("Can not get device's tunnel info which device routerId=" + device.getRouterId());
//                return false;
//            }
        } else {
            LOG.info("Can not connect device by Netconf , status is Disconnected,which device routerId=" + device.getRouterId());
            return false;
        }
        return true;
    }

    private boolean explicitLabelListToNodeList(ExplicitPath explicit) {
        Device nextDevice = explicit.getDevice();
        for (String key : explicit.getLabelMap().keySet()) {
            Label label = explicit.getLabelMap().get(key);
            if (label.getType() == LabelTypeEnum.ADJACENCY.getCode()) {
                label.setDevice(nextDevice);
                for (AdjLabel adjLabel : nextDevice.getAdjLabelList()) {
                    if (label.getValue().equals(adjLabel.getValue())) {
                        label.setAddressLocal(adjLabel.getAddressLocal());
                        label.setAddressRemote(adjLabel.getAddressRemote());
                        nextDevice = this.findLocalAndRemoteAddress(adjLabel.getAddressRemote().getAddress());
                        if (nextDevice == null) {
                            return false;
                        }
                    }
                }
            } else if (label.getType() == LabelTypeEnum.PREFIX.getCode()) {
                nextDevice = deviceManager.getDeviceByNodeLabelValue(label.getValue());
                if (nextDevice == null) {
                    return false;
                }
                label.setDevice(nextDevice);
                label.setRouterId(nextDevice.getRouterId());
                label.setAddressLocal(new Address(nextDevice.getRouterId(), AddressTypeEnum.V4));
            } else {
                return false;
            }
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

        // get bfd start
        LOG.info("Get bfds!");
        String bfdxml = BfdCfgSessionXml.getBfdCfgSessionsXml();
        LOG.info(bfdxml);
        String bfdResult = netconfController.sendMessage(netconfClient, bfdxml);
        List<SBfdCfgSession> sBfdCfgSessions = BfdCfgSessionXml.getBfdCfgSessionsFromXml(bfdResult);
        // get bfd end

        List<SExplicitPath> explicitPaths;
        SExplicitPath explicitPath;
        BfdSession bfdSession;
        ExplicitPath path;
        Map<String, Label> labelMap;
        String masterPath = null;
        String slavePath;

        for (SSrTeTunnel srTeTunnel : srTeTunnels) {
            tunnel = new Tunnel();
            Device destDevice = this.deviceManager.getDevice(srTeTunnel.getMplsTunnelEgressLSRId());
            // sync dynamic bfd start
            bfdSession = new BfdSession();
            bfdSession.setMultiplier(srTeTunnel.getMplsTeTunnelBfdDetectMultiplier());
            bfdSession.setMinSendTime(srTeTunnel.getMplsTeTunnelBfdMinTx());
            bfdSession.setMinRecvTime(srTeTunnel.getMplsTeTunnelBfdMinnRx());
            bfdSession.setType(BfdTypeEnum.Dynamic.getCode());
            tunnel.setDynamicBfd(bfdSession);
            // sync dynamic bfd end

            tunnel.setDevice(device);
            tunnel.setTunnelName(srTeTunnel.getTunnelName());
            tunnel.setTunnelId(srTeTunnel.getMplsTunnelIndex());
            tunnel.setDestRouterId(srTeTunnel.getMplsTunnelEgressLSRId());

            // sync static bfd start
            // add to tunnel
            boolean isStaticBfd = this.syncStaticBfd(sBfdCfgSessions, tunnel);
            // sync static bfd end

            // tunnel bfd value
            if (isStaticBfd) {
                tunnel.setBfdType(BfdTypeEnum.Static.getCode());
            } else {
                tunnel.setBfdType(BfdTypeEnum.Dynamic.getCode());
            }


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
            if (explicitPaths.size() > 0) {
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
                        if (hop.getMplsTunnelHopSidLabelType().equals(SExplicitPathHop.SIDLABEL_TYPE_ADJACENCY)) {
                            Label adjLabel = new Label();
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
                            adjLabel.setType(LabelTypeEnum.ADJACENCY.getCode());
                            labelMap.put(hop.getMplsTunnelHopIndex(), adjLabel);
                        } else {
                            deviceTemp = deviceManager.getDeviceByNodeLabelValue(Integer.parseInt(hop.getMplsTunnelHopSidLabel()));
                            if (deviceTemp != null) {
                                Label nodeLabel = new Label();
                                nodeLabel.setType(LabelTypeEnum.PREFIX.getCode());
                                nodeLabel.setAddressLocal(new Address(deviceTemp.getRouterId(), AddressTypeEnum.V4));
                                nodeLabel.setDevice(deviceTemp);
                                nodeLabel.setValue(Integer.valueOf(hop.getMplsTunnelHopSidLabel()));
                                labelMap.put(hop.getMplsTunnelHopIndex(), nodeLabel);
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
            if (adjLabelList != null) {
                for (AdjLabel label : adjLabelList) {
                    if (label.getAddressLocal().getAddress().equals(remoteIp)) {
                        device = dev;
                        return device;
                    }
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
            if (!tunnelManager.isTunnelNameAndIdUsed(routerId,
                    "Tunnel" + tunnelId, tunnelId + "")) {
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

    /*
     Get Bfd entity, base entity from service entity
     */
    private BfdSession getBfdSessionByType(BfdServiceEntity bfdServiceEntity, BfdTypeEnum bfdTypeEnum, Device device) {
        BfdSession bfdSession = null;
        if (null != bfdServiceEntity) {
            bfdSession = new BfdSession();
            bfdSession.setDevice(device);
            bfdSession.setBfdName(bfdServiceEntity.getBfdName());
            bfdSession.setMinRecvTime(bfdServiceEntity.getMinRecvTime());
            bfdSession.setMinSendTime(bfdServiceEntity.getMinSendTime());
            bfdSession.setMultiplier(bfdServiceEntity.getMultiplier());
            bfdSession.setType(bfdTypeEnum.getCode());
            bfdSession.setDiscriminatorLocal(bfdServiceEntity.getDiscriminatorLocal());
            bfdSession.setDiscriminatorRemote(bfdServiceEntity.getDiscriminatorRemote());
        }
        return bfdSession;
    }

    // create Bfd static Session
    private boolean createBfdSessions(NetconfClient netconfClient, TunnelServiceEntity tunnelServiceEntity) {
        boolean ret = false;
        if (tunnelServiceEntity.getBfdType() == BfdTypeEnum.Dynamic.getCode()) {
            return true;
        } else {
            SBfdCfgSession tunnelBfd = this.getSBfdCfgSessionFromServiceEntity(tunnelServiceEntity.getTunnelBfd(), tunnelServiceEntity);
            SBfdCfgSession masterBfd = this.getSBfdCfgSessionFromServiceEntity(tunnelServiceEntity.getMasterBfd(), tunnelServiceEntity);
            List<SBfdCfgSession> sBfdCfgSessions = new ArrayList<SBfdCfgSession>();
            sBfdCfgSessions.add(tunnelBfd);
            sBfdCfgSessions.add(masterBfd);

            // while tunnelBfd and masterBfd is NULL
            // not need to set static bfd
            if (sBfdCfgSessions.isEmpty()) {
                return true;
            }

            String xml = BfdCfgSessionXml.createBfdCfgSessionsXml(sBfdCfgSessions);
            LOG.info(xml);

            String result = netconfController.sendMessage(netconfClient, xml);
            if (CheckXml.RESULT_OK.equals(CheckXml.checkOk(result))) {
                ret = true;
            }
        }

        return ret;
    }

    private SBfdCfgSession getSBfdCfgSessionFromServiceEntity(BfdServiceEntity bfdServiceEntity, TunnelServiceEntity tunnelServiceEntity) {
        SBfdCfgSession sBfdCfgSession = null;
        if (null != bfdServiceEntity) {
            sBfdCfgSession = new SBfdCfgSession();
            sBfdCfgSession.setTunnelName(tunnelServiceEntity.getTunnelName());
            sBfdCfgSession.setMultiplier(bfdServiceEntity.getMultiplier());
            sBfdCfgSession.setMinTxInt(bfdServiceEntity.getMinSendTime());
            sBfdCfgSession.setMinRxInt(bfdServiceEntity.getMinRecvTime());
            sBfdCfgSession.setLocalDiscr(bfdServiceEntity.getDiscriminatorLocal());
            sBfdCfgSession.setRemoteDiscr(bfdServiceEntity.getDiscriminatorRemote());
            sBfdCfgSession.setCreateType("SESS_STATIC");
            sBfdCfgSession.setLinkType(this.getBfdLinkType(bfdServiceEntity.getType()));
            sBfdCfgSession.setSessName(bfdServiceEntity.getBfdName());
        }

        return sBfdCfgSession;
    }

    private String getBfdLinkType(Integer type) {
        if (type == BfdTypeEnum.Tunnel.getCode()) {
            return "TE_TUNNEL";
        } else {
            return "TE_LSP";
        }
    }

    // delete bfd
    private boolean deleteBfdSessions(NetconfClient netconfClient, Tunnel tunnel) {
        boolean ret = false;
        if (tunnel != null && tunnel.getBfdType() == BfdTypeEnum.Static.getCode()) {
            List<String> sessNames = new ArrayList<String>();

            if (tunnel.getTunnelBfd() != null) {
                sessNames.add(tunnel.getTunnelBfd().getBfdName());
            }

            if (tunnel.getMasterBfd() != null) {
                sessNames.add(tunnel.getMasterBfd().getBfdName());
            }

            if (!sessNames.isEmpty()) {
                String xml = BfdCfgSessionXml.deleteBfdCfgSessionsXml(sessNames);
                LOG.info(xml);

                String result = netconfController.sendMessage(netconfClient, xml);
                if (CheckXml.RESULT_OK.equals(CheckXml.checkOk(result))) {
                    ret = true;
                    tunnel.setMasterBfd(null);
                    tunnel.setTunnelBfd(null);
                }
            } else {
                return true;
            }

        } else {
            ret = true;
        }

        return ret;
    }

    // add bfd to tunnel
    private boolean syncStaticBfd(List<SBfdCfgSession> sBfdCfgSessions, Tunnel tunnel) {
        boolean ret = false;
        if (sBfdCfgSessions == null || sBfdCfgSessions.isEmpty()) {
            return false;
        }

        for (SBfdCfgSession sBfdCfgSession : sBfdCfgSessions) {
            if (sBfdCfgSession.getTunnelName().equals(tunnel.getTunnelName())) {
                this.getBfdSessionFromSEntity(sBfdCfgSession, tunnel);
                ret = true;
            }
        }

        return ret;
    }

    private BfdSession getBfdSessionFromSEntity(SBfdCfgSession sBfdCfgSession, Tunnel tunnel) {
        BfdSession bfdSession = null;
        if (null != sBfdCfgSession) {
            bfdSession = new BfdSession();
            bfdSession.setDevice(tunnel.getDevice());
            bfdSession.setBfdName(sBfdCfgSession.getSessName());
            bfdSession.setMultiplier(sBfdCfgSession.getMultiplier());
            bfdSession.setMinRecvTime(sBfdCfgSession.getMinRxInt());
            bfdSession.setMinSendTime(sBfdCfgSession.getMinTxInt());
            bfdSession.setDiscriminatorLocal(sBfdCfgSession.getLocalDiscr());
            bfdSession.setDiscriminatorRemote(sBfdCfgSession.getRemoteDiscr());
            if (sBfdCfgSession.getLinkType().equals("TE_TUNNEL")) {
                bfdSession.setType(BfdTypeEnum.Tunnel.getCode());
                tunnel.setTunnelBfd(bfdSession);
            } else if (sBfdCfgSession.getLinkType().equals("TE_LSP")) {
                bfdSession.setType(BfdTypeEnum.Master.getCode());
                tunnel.setMasterBfd(bfdSession);
            }
        }
        return bfdSession;
    }


}
