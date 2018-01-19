package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.cfgcli.srlabelcli.SrlabelCli;
import cn.org.upbnc.cfgcli.tunnelcli.TunnelCli;
import cn.org.upbnc.cfgcli.vpncli.VpnCli;
import cn.org.upbnc.entity.CommandLine;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.enumtype.CfgTypeEnum;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.service.ActionCfgService;
import cn.org.upbnc.service.entity.actionCfg.CheckPointInfoServiceEntity;
import cn.org.upbnc.service.entity.actionCfg.PointChangeInfoServiceEntity;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.actionCfg.SCheckPointInfo;
import cn.org.upbnc.util.netconf.actionCfg.SPointChangeInfo;
import cn.org.upbnc.util.xml.ActionCfgXml;
import cn.org.upbnc.util.xml.CandidateXml;
import cn.org.upbnc.util.xml.RunningXml;
import cn.org.upbnc.xmlcompare.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class ActionCfgServiceImpl implements ActionCfgService {
    private static final Logger LOG = LoggerFactory.getLogger(ActionCfgServiceImpl.class);
    private static ActionCfgService ourInstance = null;
    private BaseInterface baseInterface;
    private DeviceManager deviceManager;
    private NetConfManager netConfManager;

    public static ActionCfgService getInstance() {
        if (ourInstance == null) {
            ourInstance = new ActionCfgServiceImpl();
        }
        return ourInstance;
    }

    public ActionCfgServiceImpl() {
        this.deviceManager = null;
        this.netConfManager = null;
    }

    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        if (null != baseInterface) {
            this.netConfManager = this.baseInterface.getNetConfManager();
            this.deviceManager = this.baseInterface.getDeviceManager();
        }
        return true;
    }

    @Override
    public Map<String, Object> getCfgChane(String routerId, String cfgType) {
        Map<String, Object> resultMap = new HashMap<>();
        List<CommandLine> commandLineList = new ArrayList<>();
        List<Device> deviceList = new ArrayList<>();
        Device device = deviceManager.getDevice(routerId);
        if (device == null) {
            deviceList = deviceManager.getDeviceList();
        } else {
            deviceList.add(device);
        }
        for (Device d : deviceList) {
            CommandLine commandLine = new CommandLine();
            String candidateCfg = this.getCandidateCfgXml(d, cfgType);
            String runningCfg = this.getRunningCfgXml(d, cfgType);
            LOG.info("candidateCfg : \n" + candidateCfg);
            LOG.info("runningCfg : \n" + runningCfg);
            commandLine.setDeviceName(d.getDeviceName());
            commandLine.setRouterId(d.getRouterId());
            if (cfgType.equals(CfgTypeEnum.SR_LABEL.getCfgType())) {
                commandLine.getCliList().addAll(SrlabelCli.srLabelCfgCli(candidateCfg, runningCfg));
            } else if (cfgType.equals(CfgTypeEnum.VPN.getCfgType())) {
                commandLine.getCliList().addAll(VpnCli.vpnCfgCli(candidateCfg, runningCfg));
            } else if (cfgType.equals(CfgTypeEnum.SR_TUNNEL.getCfgType())) {
                commandLine.getCliList().addAll(TunnelCli.tunnelCfgCli(candidateCfg, runningCfg));
            } else {
                String xml1 = Util.candidate();
                String xml2 = Util.modify();
                commandLine.getCliList().addAll(TunnelCli.tunnelCfgCliTest(xml1, xml2));
                commandLine.getCliList().addAll(SrlabelCli.srLabelCfgCliTest());
            }
            if (commandLine.getCliList().size() != 0) {
                commandLineList.add(commandLine);
            }
        }
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.MESSAGE.getName(), commandLineList);
        return resultMap;
    }

    @Override
    public Map<String, Object> commitCfgChane(String routerId, String cfgType) {
        Map<String, Object> resultMap = new HashMap<>();
        String commitXml = ActionCfgXml.getCommitCfgXml();
        List<Device> deviceList = new ArrayList<>();
        Device device = deviceManager.getDevice(routerId);
        if (device == null) {
            deviceList = deviceManager.getDeviceList();
        } else {
            deviceList.add(device);
        }
        for (Device d : deviceList) {
            NetconfClient netconfClient = netConfManager.getNetconClient(d.getNetConf().getRouterID());
            String outPutXml = netconfController.sendMessage(netconfClient, commitXml);
            LOG.info(outPutXml);
        }
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        return resultMap;
    }

    @Override
    public Map<String, Object> cancelCfgChane(String routerId, String cfgType) {
        Map<String, Object> resultMap = new HashMap<>();
        String cancelXml = ActionCfgXml.getCancelCfgXml();
        List<Device> deviceList = new ArrayList<>();
        Device device = deviceManager.getDevice(routerId);
        if (device == null) {
            deviceList = deviceManager.getDeviceList();
        } else {
            deviceList.add(device);
        }
        for (Device d : deviceList) {
            NetconfClient netconfClient = netConfManager.getNetconClient(d.getNetConf().getRouterID());
            String outPutXml = netconfController.sendMessage(netconfClient, cancelXml);
            LOG.info(outPutXml);
        }
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        return resultMap;
    }

    private String getCandidateCfgXml(Device device, String cfgType) {
        String xml = null;
        if (cfgType.equals(CfgTypeEnum.SR_LABEL.getCfgType())) {
            xml = CandidateXml.getCandidateSrLabelXml();
        } else if (cfgType.equals(CfgTypeEnum.VPN.getCfgType())) {
            xml = CandidateXml.getCandidateVpnXml();
        } else if (cfgType.equals(CfgTypeEnum.SR_TUNNEL.getCfgType())) {
            xml = CandidateXml.getCandidateTunnelXml();
        } else {
//            xml = CandidateXml.getCandidateXml();
            return null;
        }
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
        String outPutXml = netconfController.sendMessage(netconfClient, xml);
        return outPutXml;
    }

    private String getRunningCfgXml(Device device, String cfgType) {
        String xml;
        if (cfgType.equals(CfgTypeEnum.SR_LABEL.getCfgType())) {
            xml = RunningXml.getRunningSrLabelXml();
        } else if (cfgType.equals(CfgTypeEnum.VPN.getCfgType())) {
            xml = RunningXml.getRunningVpnXml();
        } else if (cfgType.equals(CfgTypeEnum.SR_TUNNEL.getCfgType())) {
            xml = RunningXml.getRunningTunnelXml();
        } else {
//            xml = RunningXml.getRunningXml();
            return null;
        }
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
        String outPutXml = netconfController.sendMessage(netconfClient, xml);
        return outPutXml;
    }

    @Override
    public Map<String, List<CheckPointInfoServiceEntity>> getCfgCommitPointInfo(String routerId, String commitId) {
        Map<String,List<CheckPointInfoServiceEntity>>  checkInfoMap = new HashMap<>();
        List<Device> deviceList = new ArrayList<>();
        Device device = deviceManager.getDevice(routerId);
        if (device == null) {
            deviceList = deviceManager.getDeviceList();
        } else {
            deviceList.add(device);
        }
        for (Device d : deviceList) {
            String getCommitXml = ActionCfgXml.getCheckPointInfoXml(commitId);
            LOG.info(d.getRouterId() + " getCommitXml: " + getCommitXml);
            NetconfClient netconfClient = netConfManager.getNetconClient(d.getNetConf().getRouterID());
            String outPutCommitXml = netconfController.sendMessage(netconfClient, getCommitXml);
            LOG.info(d.getRouterId() + " outPutCommitXml: " + outPutCommitXml);
            List<SCheckPointInfo> sCheckPointInfoList = ActionCfgXml.getCheckPointInfoFromXml(outPutCommitXml);
            List<CheckPointInfoServiceEntity> checkPointInfoServiceEntityList = sCheckPointInfoToCheckPointInfoServiceEntity(sCheckPointInfoList);
            checkInfoMap.put(d.getRouterId(),checkPointInfoServiceEntityList);
        }
        return checkInfoMap;
    }

    @Override
    public Map<String, Object> rollbackToCommitId(String routerId, String commitId) {
        Map<String, Object> resultMap = new HashMap<>();
        Device device = deviceManager.getDevice(routerId);
        if (device == null) {
            return null;
        }
        String getCommitXml = ActionCfgXml.getRollBackToCommitIdXml(commitId);
        LOG.info("getCommitXml: " + getCommitXml);
        NetconfClient netconfClient = netConfManager.getNetconClient(routerId);
        String outPutCommitXml = netconfController.sendMessage(netconfClient, getCommitXml);
        LOG.info("outPutCommitXml: " + outPutCommitXml);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        return resultMap;
    }

    private List<CheckPointInfoServiceEntity> sCheckPointInfoToCheckPointInfoServiceEntity(List<SCheckPointInfo> sCheckPointInfoList) {
        List<CheckPointInfoServiceEntity> checkPointInfoServiceEntityList = new ArrayList<>();
        for (SCheckPointInfo sCheckPointInfo : sCheckPointInfoList) {
            CheckPointInfoServiceEntity checkPointInfoServiceEntity = new CheckPointInfoServiceEntity();
            checkPointInfoServiceEntity.setCommitId(sCheckPointInfo.getCommitId());
            checkPointInfoServiceEntity.setUserLabel(sCheckPointInfo.getUserLabel());
            checkPointInfoServiceEntity.setUserName(sCheckPointInfo.getUserName());
            checkPointInfoServiceEntity.setTimeStamp(sCheckPointInfo.getTimeStamp());
            checkPointInfoServiceEntity.setSinceList(this.sPointChangeInfoToPointChangeInfoServiceEntity(sCheckPointInfo.getSinceList()));
            checkPointInfoServiceEntity.setCurList(this.sPointChangeInfoToPointChangeInfoServiceEntity(sCheckPointInfo.getCurrList()));
            checkPointInfoServiceEntityList.add(checkPointInfoServiceEntity);
        }
        return checkPointInfoServiceEntityList;
    }

    private List<PointChangeInfoServiceEntity> sPointChangeInfoToPointChangeInfoServiceEntity(List<SPointChangeInfo> sPointChangeInfoList) {
        List<PointChangeInfoServiceEntity> pointChangeInfoServiceEntityList = new ArrayList<>();
        for (SPointChangeInfo sPointChangeInfo : sPointChangeInfoList) {
            PointChangeInfoServiceEntity pointChangeInfoServiceEntity = new PointChangeInfoServiceEntity();
            pointChangeInfoServiceEntity.setIndex(sPointChangeInfo.getIndex());
            pointChangeInfoServiceEntity.setChange(sPointChangeInfo.getChange());
            pointChangeInfoServiceEntityList.add(pointChangeInfoServiceEntity);
        }
        return  pointChangeInfoServiceEntityList;
    }

}
