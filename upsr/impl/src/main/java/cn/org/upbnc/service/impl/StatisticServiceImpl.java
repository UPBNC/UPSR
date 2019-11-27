package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.base.StatisticsManager;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.statistics.IfClearedStatEntity;
import cn.org.upbnc.entity.statistics.IfStatisticsEntity;
import cn.org.upbnc.enumtype.TimeEnum;
import cn.org.upbnc.service.StatisticService;
import cn.org.upbnc.service.entity.statistics.IfClearedStatServiceEntity;
import cn.org.upbnc.service.entity.statistics.IfStatisticsServiceEntity;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.NetconfDevice;
import cn.org.upbnc.util.netconf.statistics.SIfClearedStat;
import cn.org.upbnc.util.netconf.statistics.SIfStatistics;
import cn.org.upbnc.util.xml.StatisticXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
        for (Device device : devices) {
            routerId = device.getRouterId();
            this.setIfClearedStat(routerId,"");
        }
    }

    @Override
    public Map<String, List<IfClearedStatEntity>> getIfClearedStat(String routerId) {
        return null;
    }

    @Override
    public Map<String, List<IfStatisticsServiceEntity>> getIfStatistics(String routerId) {
        return null;
    }

    private void setIfClearedStat(String routerId, String ifName) {
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
        }
        return;
    }

    private void setIfStatistics(String routerId, String ifName) {
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
        }
        return;
    }

    private void setCpuInfo() {
        return;
    }

    private void setMemoryInfo() {
        return;
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
}
