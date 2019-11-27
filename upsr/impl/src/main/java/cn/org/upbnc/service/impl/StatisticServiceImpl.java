package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.base.StatisticsManager;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.statistics.IfClearedStatEntity;
import cn.org.upbnc.entity.statistics.IfStatisticsEntity;
import cn.org.upbnc.entity.statistics.CpuInfoEntity;
import cn.org.upbnc.entity.statistics.MemoryInfoEntity;
import cn.org.upbnc.enumtype.TimeEnum;
import cn.org.upbnc.service.StatisticService;
import cn.org.upbnc.service.entity.statistics.CpuInfoServiceEntity;
import cn.org.upbnc.service.entity.statistics.IfClearedStatServiceEntity;
import cn.org.upbnc.service.entity.statistics.IfStatisticsServiceEntity;
import cn.org.upbnc.service.entity.statistics.MemoryInfoServiceEntity;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.NetconfDevice;
import cn.org.upbnc.util.netconf.statistics.SCpuInfo;
import cn.org.upbnc.util.netconf.statistics.SIfClearedStat;
import cn.org.upbnc.util.netconf.statistics.SIfStatistics;
import cn.org.upbnc.util.netconf.statistics.SMemoryInfo;
import cn.org.upbnc.util.xml.StatisticXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticServiceImpl implements StatisticService {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticServiceImpl.class);
    private static StatisticService ourInstance = null;
    private BaseInterface baseInterface;
    private NetConfManager netConfManager;
    private DeviceManager deviceManager;
    private StatisticsManager statisticsManager;
    private Map<String,List<IfStatisticsEntity>> ifStatisticsEntityMaps;
    private Map<String,List<IfClearedStatEntity>> ifClearedStatEntityMaps;
    private Map<String,List<CpuInfoEntity>> cpuInfoEntityMaps = new HashMap<>();
    private Map<String,List<MemoryInfoEntity>> memoryInfoEntityMaps = new HashMap<>();
    public static NetconfDevice netconfController = new NetconfDevice();

    public static StatisticService getInstance() {
        if (null == ourInstance) {
            ourInstance = new StatisticServiceImpl();
        }
        return ourInstance;
    }


    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        if (null != baseInterface) {
            this.netConfManager = this.baseInterface.getNetConfManager();
            this.deviceManager = this.baseInterface.getDeviceManager();
            this.statisticsManager = this.baseInterface.getStatisticsManager();
        }
        return true;
    }

    @Override
    public List<IfClearedStatServiceEntity> getStatisticsMap(String routerId, TimeEnum timeEnum) {
        List<IfClearedStatServiceEntity> ifClearedStatServiceEntityList = new ArrayList<>();
        IfClearedStatServiceEntity ifClearedStatServiceEntity;
        List<IfClearedStatEntity> ifClearedStatEntityList = statisticsManager.getStatistics(routerId, timeEnum);
        for (IfClearedStatEntity ifClearedStatEntity : ifClearedStatEntityList) {
            ifClearedStatServiceEntity = statisticsMapToStatisticsEntity(ifClearedStatEntity);
            ifClearedStatServiceEntityList.add(ifClearedStatServiceEntity);
        }
        return ifClearedStatServiceEntityList;
    }

    private IfClearedStatServiceEntity statisticsMapToStatisticsEntity(IfClearedStatEntity ifClearedStatEntity) {
        IfClearedStatServiceEntity ifClearedStatServiceEntity = new IfClearedStatServiceEntity();
        ifClearedStatServiceEntity.setRouterId(ifClearedStatEntity.getRouterId());
        ifClearedStatServiceEntity.setDate(ifClearedStatEntity.getDate());
        ifClearedStatServiceEntity.setIfIndex(ifClearedStatEntity.getIfIndex());
        ifClearedStatServiceEntity.setIfName(ifClearedStatEntity.getIfName());
        ifClearedStatServiceEntity.setRcvUniPacket(ifClearedStatEntity.getRcvUniPacket());
        ifClearedStatServiceEntity.setSendUniPacket(ifClearedStatEntity.getSendUniPacket());
        ifClearedStatServiceEntity.setSendPacket(ifClearedStatEntity.getSendPacket());
        ifClearedStatServiceEntity.setInPacketRate(ifClearedStatEntity.getInPacketRate());
        ifClearedStatServiceEntity.setOutPacketRate(ifClearedStatEntity.getOutPacketRate());
        ifClearedStatServiceEntity.setInUseRate(ifClearedStatEntity.getInUseRate());
        ifClearedStatServiceEntity.setOutUseRate(ifClearedStatEntity.getOutUseRate());
        ifClearedStatServiceEntity.setRcvErrorPacket(ifClearedStatEntity.getRcvErrorPacket());
        ifClearedStatServiceEntity.setSendErrorPacket(ifClearedStatEntity.getSendErrorPacket());
        return ifClearedStatServiceEntity;
    }

    @Override
    public void setStatistics() {
        String routerId;
        List<Device> devices = deviceManager.getDeviceList();
        Map<String,List<IfClearedStatEntity>> ifClearedStatMaps = new HashMap<>();
        Map<String,List<IfStatisticsEntity>> ifStatisticsMaps = new HashMap<>();
        Map<String,List<CpuInfoEntity>> cpuInfoMaps = new HashMap<>();
        Map<String,List<MemoryInfoEntity>> memoryInfoMaps = new HashMap<>();
        for (Device device : devices) {
            routerId = device.getRouterId();
            List<IfClearedStatEntity> ifClearedStatEntityList = this.setIfClearedStat(routerId,"");
            List<IfStatisticsEntity> ifStatisticsEntityList = this.setIfStatistics(routerId,"");
            List<CpuInfoEntity> cpuInfoEntityList = this.setCpuInfo(routerId);
            List<MemoryInfoEntity> memoryInfoEntityList = this.setMemoryInfo(routerId);
            if (ifClearedStatEntityList != null) {
                ifClearedStatMaps.put(routerId,ifClearedStatEntityList);
            }
            if (ifStatisticsEntityList != null) {
                ifStatisticsMaps.put(routerId,ifStatisticsEntityList);
            }
            if (cpuInfoEntityList != null) {
                cpuInfoMaps.put(routerId,cpuInfoEntityList);
            }
            if (memoryInfoEntityList != null) {
                memoryInfoMaps.put(routerId,memoryInfoEntityList);
            }
        }
        ifClearedStatEntityMaps = ifClearedStatMaps;
        ifStatisticsEntityMaps = ifStatisticsMaps;
        cpuInfoEntityMaps = cpuInfoMaps;
        memoryInfoEntityMaps = memoryInfoMaps;
        return;
    }

    @Override
    public Map<String, List<IfClearedStatServiceEntity>> getIfClearedStat(String routerId) {
        Map<String, List<IfClearedStatServiceEntity>> ret = new HashMap<>();
        for (String rid : ifClearedStatEntityMaps.keySet()) {
            List<IfClearedStatServiceEntity> ifClearedStatServiceEntityList = new ArrayList<>();
            List<IfClearedStatEntity> ifClearedStatEntityList = ifClearedStatEntityMaps.get(rid);
            for (IfClearedStatEntity ifClearedStatEntity : ifClearedStatEntityList) {
                ifClearedStatServiceEntityList.add(ifClearedStatEntityToIfClearedStatServiceEntity(ifClearedStatEntity));
            }
            ret.put(rid,ifClearedStatServiceEntityList);
        }
        return ret;
    }

    @Override
    public Map<String, List<IfStatisticsServiceEntity>> getIfStatistics(String routerId) {
        Map<String, List<IfStatisticsServiceEntity>> ret = new HashMap<>();
        for (String rid : ifStatisticsEntityMaps.keySet()) {
            List<IfStatisticsServiceEntity> ifStatisticsServiceEntityList= new ArrayList<>();
            List<IfStatisticsEntity> ifStatisticsEntityList = ifStatisticsEntityMaps.get(rid);
            for (IfStatisticsEntity ifStatisticsEntity: ifStatisticsEntityList) {
                ifStatisticsServiceEntityList.add(ifStatisticsEntityToIfStatisticsServiceEntity(ifStatisticsEntity));
            }
            ret.put(rid,ifStatisticsServiceEntityList);
        }
        return ret;
    }

    @Override
    public Map<String, List<CpuInfoServiceEntity>> getCpuInfo(String routerId) {
        Map<String, List<CpuInfoServiceEntity>> ret = new HashMap<>();
        for (String rid : cpuInfoEntityMaps.keySet()) {
            List<CpuInfoServiceEntity> cpuInfoServiceEntityList= new ArrayList<>();
            List<CpuInfoEntity> ifStatisticsEntityList = cpuInfoEntityMaps.get(rid);
            for (CpuInfoEntity cpuInfoEntity: ifStatisticsEntityList) {
                cpuInfoServiceEntityList.add(cpuInfoEntityToCpuInfoServiceEntity(cpuInfoEntity));
            }
            ret.put(rid,cpuInfoServiceEntityList);
        }
        return ret;

    }

    @Override
    public Map<String, List<MemoryInfoServiceEntity>> getMemoryInfo(String routerId) {
        Map<String, List<MemoryInfoServiceEntity>> ret = new HashMap<>();
        for (String rid : memoryInfoEntityMaps.keySet()) {
            List<MemoryInfoServiceEntity> memoryInfoServiceEntityList= new ArrayList<>();
            List<MemoryInfoEntity> memoryInfoEntityList = memoryInfoEntityMaps.get(rid);
            for (MemoryInfoEntity memoryInfoEntity: memoryInfoEntityList) {
                memoryInfoServiceEntityList.add(memoryInfoEntityToMemoryInfoServiceEntity(memoryInfoEntity));
            }
            ret.put(rid,memoryInfoServiceEntityList);
        }
        return ret;
    }

    private List<IfClearedStatEntity> setIfClearedStat(String routerId, String ifName) {
        NetconfClient netconfClient = this.netConfManager.getNetconClient(routerId);
        if (null != netconfClient) {
            String sendMsg = StatisticXml.getIfClearedStatXml(ifName);
            String result = netconfController.sendMessage(netconfClient, sendMsg);
            List<SIfClearedStat> sIfClearedStats = StatisticXml.getIfClearedStatFromXml(result);
            List<IfClearedStatEntity> statistics = new ArrayList<>();
            IfClearedStatEntity statistic;
            for (SIfClearedStat sIfClearedStat : sIfClearedStats) {
                statistic = sIfClearedStatMapToStatistics(sIfClearedStat);
                statistic.setRouterId(routerId);
                statistics.add(statistic);
            }
            if (sIfClearedStats.size() > 0) {
                statisticsManager.setIfClearedStat(statistics);
            }
            return statistics;
        }
        return null;
    }

    private List<IfStatisticsEntity> setIfStatistics(String routerId, String ifName) {
        NetconfClient netconfClient = this.netConfManager.getNetconClient(routerId);
        if (null != netconfClient) {
            String sendMsg = StatisticXml.getIfStatisticsXml(ifName);
            String result = netconfController.sendMessage(netconfClient, sendMsg);
            List<SIfStatistics> sIfStatisticsList= StatisticXml.getIfStatisticsFromXml(result);
            List<IfStatisticsEntity> ifStatisticsEntityList = new ArrayList<>();
            for (SIfStatistics sIfStatistics : sIfStatisticsList) {
                IfStatisticsEntity ifStatisticsEntity = sIfStatisticsToIfStatisticsEntity(sIfStatistics);
                ifStatisticsEntityList.add(ifStatisticsEntity);
            }
            if (ifStatisticsEntityList.size() > 0) {
                statisticsManager.setifStatistics(ifStatisticsEntityList);
            }
            return  ifStatisticsEntityList;
        }
        return null;
    }

    private List<CpuInfoEntity> setCpuInfo(String routerId) {
        NetconfClient netconfClient = this.netConfManager.getNetconClient(routerId);
        if (null != netconfClient) {
            String sendMsg = StatisticXml.getCpuInfoXml();
            String result = netconfController.sendMessage(netconfClient, sendMsg);
            List<SCpuInfo> sCpuInfoList = StatisticXml.getCpuInfoFromXml(result);
            List<CpuInfoEntity> cpuInfoEntityList =  new ArrayList<>();
            for (SCpuInfo sCpuInfo : sCpuInfoList) {
                CpuInfoEntity cpuInfoEntity = sCpuInfoToSCpuInfoEntity(sCpuInfo);
                cpuInfoEntityList.add(cpuInfoEntity);
            }
            statisticsManager.setCpuInfo(cpuInfoEntityList);
            return  cpuInfoEntityList;
        }
        return null;
    }

    private List<MemoryInfoEntity> setMemoryInfo(String routerId) {
        NetconfClient netconfClient = this.netConfManager.getNetconClient(routerId);
        if (null != netconfClient) {
            String sendMsg = StatisticXml.getMemoryInfoXml();
            String result = netconfController.sendMessage(netconfClient, sendMsg);
            List<SMemoryInfo> sMemoryInfoList = StatisticXml.getMemoryInfoFromXml(result);
            List<MemoryInfoEntity> memoryInfoEntityList = new ArrayList<>();
            for (SMemoryInfo sMemoryInfo : sMemoryInfoList) {
                MemoryInfoEntity memoryInfoEntity = sMemoryInfoToMemoryInfoEntity(sMemoryInfo);
                memoryInfoEntityList.add(memoryInfoEntity);
            }
            statisticsManager.setMemoryInfo(memoryInfoEntityList);
            return  memoryInfoEntityList;
        }
        return null;
    }

    public IfClearedStatEntity sIfClearedStatMapToStatistics(SIfClearedStat sIfClearedStat) {
        IfClearedStatEntity ifClearedStatEntity = new IfClearedStatEntity();
        ifClearedStatEntity.setDate(System.currentTimeMillis());
        ifClearedStatEntity.setIfIndex(sIfClearedStat.getIfIndex());
        ifClearedStatEntity.setIfName(sIfClearedStat.getIfName());
        ifClearedStatEntity.setRcvUniPacket(sIfClearedStat.getRcvUniPacket());
        ifClearedStatEntity.setSendUniPacket(sIfClearedStat.getSendUniPacket());
        ifClearedStatEntity.setSendPacket(sIfClearedStat.getSendPacket());
        ifClearedStatEntity.setInPacketRate(sIfClearedStat.getInPacketRate());
        ifClearedStatEntity.setOutPacketRate(sIfClearedStat.getOutPacketRate());
        ifClearedStatEntity.setInUseRate(sIfClearedStat.getInUseRate());
        ifClearedStatEntity.setOutUseRate(sIfClearedStat.getOutUseRate());
        ifClearedStatEntity.setRcvErrorPacket(sIfClearedStat.getRcvErrorPacket());
        ifClearedStatEntity.setSendErrorPacket(sIfClearedStat.getSendErrorPacket());
        return ifClearedStatEntity;
    }

    private IfStatisticsEntity sIfStatisticsToIfStatisticsEntity (SIfStatistics sIfStatistics) {
        IfStatisticsEntity ifStatisticsEntity = new IfStatisticsEntity();
        ifStatisticsEntity.setIfName(sIfStatistics.getIfName());
        ifStatisticsEntity.setIfIndex(sIfStatistics.getIfIndex());
        ifStatisticsEntity.setReceiveByte(sIfStatistics.getReceiveByte());
        ifStatisticsEntity.setSendByte(sIfStatistics.getSendByte());
        ifStatisticsEntity.setReceivePacket(sIfStatistics.getReceivePacket());
        ifStatisticsEntity.setSendPacket(sIfStatistics.getSendPacket());
        ifStatisticsEntity.setRcvUniPacket(sIfStatistics.getRcvUniPacket());
        ifStatisticsEntity.setRcvMutiPacket(sIfStatistics.getRcvMutiPacket());
        ifStatisticsEntity.setRcvBroadPacket(sIfStatistics.getRcvBroadPacket());
        ifStatisticsEntity.setSendUniPacket(sIfStatistics.getSendUniPacket());
        ifStatisticsEntity.setSendMutiPacket(sIfStatistics.getSendMutiPacket());
        ifStatisticsEntity.setSendBroadPacket(sIfStatistics.getSendBroadPacket());
        ifStatisticsEntity.setRcvErrorPacket(sIfStatistics.getRcvErrorPacket());
        ifStatisticsEntity.setRcvDropPacket(sIfStatistics.getRcvDropPacket());
        ifStatisticsEntity.setSendErrorPacket(sIfStatistics.getSendErrorPacket());
        ifStatisticsEntity.setSendDropPacket(sIfStatistics.getSendDropPacket());
        return ifStatisticsEntity;
    }

    private CpuInfoEntity sCpuInfoToSCpuInfoEntity(SCpuInfo sCpuInfo) {
        CpuInfoEntity cpuInfoEntity = new CpuInfoEntity();

        cpuInfoEntity.setPosition(sCpuInfo.getPosition());
        cpuInfoEntity.setEntIndex(sCpuInfo.getEntIndex());
        cpuInfoEntity.setSystemCpuUsage(sCpuInfo.getSystemCpuUsage());
        cpuInfoEntity.setOvloadThreshold(sCpuInfo.getOvloadThreshold());
        cpuInfoEntity.setUnovloadThreshold(sCpuInfo.getUnovloadThreshold());
        cpuInfoEntity.setInterval(sCpuInfo.getInterval());

        return cpuInfoEntity;
    }

    private MemoryInfoEntity sMemoryInfoToMemoryInfoEntity(SMemoryInfo sMemoryInfo) {
        MemoryInfoEntity memoryInfoEntity = new MemoryInfoEntity();
        memoryInfoEntity.setPosition(sMemoryInfo.getPosition());
        memoryInfoEntity.setEntIndex(sMemoryInfo.getEntIndex());
        memoryInfoEntity.setOsMemoryTotal(sMemoryInfo.getOsMemoryTotal());
        memoryInfoEntity.setOsMemoryUse(sMemoryInfo.getOsMemoryUse());
        memoryInfoEntity.setOsMemoryFree(sMemoryInfo.getOsMemoryFree());
        memoryInfoEntity.setOsMemoryUsage(sMemoryInfo.getOsMemoryUsage());
        memoryInfoEntity.setDoMemoryTotal(sMemoryInfo.getDoMemoryTotal());
        memoryInfoEntity.setDoMemoryUse(sMemoryInfo.getDoMemoryUse());
        memoryInfoEntity.setDoMemoryFree(sMemoryInfo.getDoMemoryFree());
        memoryInfoEntity.setDoMemoryUsage(sMemoryInfo.getDoMemoryUsage());
        return memoryInfoEntity;
    }

    private IfClearedStatServiceEntity ifClearedStatEntityToIfClearedStatServiceEntity(IfClearedStatEntity ifClearedStatEntity) {
        IfClearedStatServiceEntity ifClearedStatServiceEntity = new IfClearedStatServiceEntity();

        ifClearedStatServiceEntity.setDate(ifClearedStatEntity.getDate());
        ifClearedStatServiceEntity.setIfIndex(ifClearedStatEntity.getIfIndex());
        ifClearedStatServiceEntity.setIfName(ifClearedStatEntity.getIfName());
        ifClearedStatServiceEntity.setRcvUniPacket(ifClearedStatEntity.getRcvUniPacket());
        ifClearedStatServiceEntity.setSendUniPacket(ifClearedStatEntity.getSendUniPacket());
        ifClearedStatServiceEntity.setSendPacket(ifClearedStatEntity.getSendPacket());
        ifClearedStatServiceEntity.setInPacketRate(ifClearedStatEntity.getInPacketRate());
        ifClearedStatServiceEntity.setOutPacketRate(ifClearedStatEntity.getOutPacketRate());
        ifClearedStatServiceEntity.setInUseRate(ifClearedStatEntity.getInUseRate());
        ifClearedStatServiceEntity.setOutUseRate(ifClearedStatEntity.getOutUseRate());
        ifClearedStatServiceEntity.setRcvErrorPacket(ifClearedStatEntity.getRcvErrorPacket());
        ifClearedStatServiceEntity.setSendErrorPacket(ifClearedStatEntity.getSendErrorPacket());

        return ifClearedStatServiceEntity;
    }

    private IfStatisticsServiceEntity ifStatisticsEntityToIfStatisticsServiceEntity(IfStatisticsEntity ifStatisticsEntity) {
        IfStatisticsServiceEntity ifStatisticsServiceEntity = new IfStatisticsServiceEntity();
        ifStatisticsServiceEntity.setIfName(ifStatisticsEntity.getIfName());
        ifStatisticsServiceEntity.setIfIndex(ifStatisticsEntity.getIfIndex());
        ifStatisticsServiceEntity.setReceiveByte(ifStatisticsEntity.getReceiveByte());
        ifStatisticsServiceEntity.setSendByte(ifStatisticsEntity.getSendByte());
        ifStatisticsServiceEntity.setReceivePacket(ifStatisticsEntity.getReceivePacket());
        ifStatisticsServiceEntity.setSendPacket(ifStatisticsEntity.getSendPacket());
        ifStatisticsServiceEntity.setRcvUniPacket(ifStatisticsEntity.getRcvUniPacket());
        ifStatisticsServiceEntity.setRcvMutiPacket(ifStatisticsEntity.getRcvMutiPacket());
        ifStatisticsServiceEntity.setRcvBroadPacket(ifStatisticsEntity.getRcvBroadPacket());
        ifStatisticsServiceEntity.setSendUniPacket(ifStatisticsEntity.getSendUniPacket());
        ifStatisticsServiceEntity.setSendMutiPacket(ifStatisticsEntity.getSendMutiPacket());
        ifStatisticsServiceEntity.setSendBroadPacket(ifStatisticsEntity.getSendBroadPacket());
        ifStatisticsServiceEntity.setRcvErrorPacket(ifStatisticsEntity.getRcvErrorPacket());
        ifStatisticsServiceEntity.setRcvDropPacket(ifStatisticsEntity.getRcvDropPacket());
        ifStatisticsServiceEntity.setSendErrorPacket(ifStatisticsEntity.getSendErrorPacket());
        ifStatisticsServiceEntity.setSendDropPacket(ifStatisticsEntity.getSendDropPacket());
        return  ifStatisticsServiceEntity;
    }

    private CpuInfoServiceEntity cpuInfoEntityToCpuInfoServiceEntity(CpuInfoEntity cpuInfoEntity) {
        CpuInfoServiceEntity cpuInfoServiceEntity = new CpuInfoServiceEntity();
        cpuInfoServiceEntity.setPosition(cpuInfoEntity.getPosition());
        cpuInfoServiceEntity.setEntIndex(cpuInfoEntity.getEntIndex());
        cpuInfoServiceEntity.setSystemCpuUsage(cpuInfoEntity.getSystemCpuUsage());
        cpuInfoServiceEntity.setOvloadThreshold(cpuInfoEntity.getOvloadThreshold());
        cpuInfoServiceEntity.setUnovloadThreshold(cpuInfoEntity.getUnovloadThreshold());
        cpuInfoServiceEntity.setInterval(cpuInfoEntity.getInterval());
        return  cpuInfoServiceEntity;
    }

    private MemoryInfoServiceEntity memoryInfoEntityToMemoryInfoServiceEntity(MemoryInfoEntity memoryInfoEntity) {
        MemoryInfoServiceEntity memoryInfoServiceEntity = new MemoryInfoServiceEntity();
        memoryInfoServiceEntity.setPosition(memoryInfoEntity.getPosition());
        memoryInfoServiceEntity.setEntIndex(memoryInfoEntity.getEntIndex());
        memoryInfoServiceEntity.setOsMemoryTotal(memoryInfoEntity.getOsMemoryTotal());
        memoryInfoServiceEntity.setOsMemoryUse(memoryInfoEntity.getOsMemoryUse());
        memoryInfoServiceEntity.setOsMemoryFree(memoryInfoEntity.getOsMemoryFree());
        memoryInfoServiceEntity.setOsMemoryUsage(memoryInfoEntity.getOsMemoryUsage());
        memoryInfoServiceEntity.setDoMemoryTotal(memoryInfoEntity.getDoMemoryTotal());
        memoryInfoServiceEntity.setDoMemoryUse(memoryInfoEntity.getDoMemoryUse());
        memoryInfoServiceEntity.setDoMemoryFree(memoryInfoEntity.getDoMemoryFree());
        memoryInfoServiceEntity.setDoMemoryUsage(memoryInfoEntity.getDoMemoryUsage());
        return  memoryInfoServiceEntity;
    }


}
