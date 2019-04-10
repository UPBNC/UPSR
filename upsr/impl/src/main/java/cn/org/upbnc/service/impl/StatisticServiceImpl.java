package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.base.StatisticsManager;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.Statistics;
import cn.org.upbnc.enumtype.TimeEnum;
import cn.org.upbnc.service.StatisticService;
import cn.org.upbnc.service.entity.StatisticsEntity;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.NetconfDevice;
import cn.org.upbnc.util.netconf.SIfClearedStat;
import cn.org.upbnc.util.xml.IfClearedStatXml;
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
    public List<StatisticsEntity> getStatisticsMap(String routerId, TimeEnum timeEnum) {
        List<StatisticsEntity> statisticsEntityList = new ArrayList<>();
        StatisticsEntity statisticsEntity;
        List<Statistics> statisticsList = statisticsManager.getStatistics(routerId, timeEnum);
        for (Statistics statistics : statisticsList) {
            statisticsEntity = statisticsMapToStatisticsEntity(statistics);
            statisticsEntityList.add(statisticsEntity);
        }
        return statisticsEntityList;
    }

    private StatisticsEntity statisticsMapToStatisticsEntity(Statistics statistics) {
        StatisticsEntity statisticsEntity = new StatisticsEntity();
        statisticsEntity.setRouterId(statistics.getRouterId());
        statisticsEntity.setDate(statistics.getDate());
        statisticsEntity.setIfIndex(statistics.getIfIndex());
        statisticsEntity.setIfName(statistics.getIfName());
        statisticsEntity.setRcvUniPacket(statistics.getRcvUniPacket());
        statisticsEntity.setSendUniPacket(statistics.getSendUniPacket());
        statisticsEntity.setSendPacket(statistics.getSendPacket());
        statisticsEntity.setInPacketRate(statistics.getInPacketRate());
        statisticsEntity.setOutPacketRate(statistics.getOutPacketRate());
        statisticsEntity.setInUseRate(statistics.getInUseRate());
        statisticsEntity.setOutUseRate(statistics.getOutUseRate());
        statisticsEntity.setRcvErrorPacket(statistics.getRcvErrorPacket());
        statisticsEntity.setSendErrorPacket(statistics.getSendErrorPacket());
        return statisticsEntity;
    }

    @Override
    public void setStatistics() {
        String routerId;
        NetconfClient netconfClient;
        List<Device> devices = deviceManager.getDeviceList();
        for (Device device : devices) {
            routerId = device.getRouterId();
            netconfClient = this.netConfManager.getNetconClient(routerId);
            if (null != netconfClient) {
                String sendMsg = IfClearedStatXml.getIfClearedStatXml("");
                String result = netconfController.sendMessage(netconfClient, sendMsg);
                List<SIfClearedStat> sIfClearedStats = IfClearedStatXml.getIfClearedStatFromXml(result);
                List<Statistics> statistics = new ArrayList<>();
                Statistics statistic;
                for (SIfClearedStat sIfClearedStat : sIfClearedStats) {
                    statistic = sIfClearedStatMapToStatistics(sIfClearedStat);
                    statistic.setRouterId(routerId);
                    statistics.add(statistic);
                }
                if (sIfClearedStats.size() > 0) {
                    statisticsManager.setStatistics(statistics);
                }
            }
        }
    }

    public Statistics sIfClearedStatMapToStatistics(SIfClearedStat sIfClearedStat) {
        Statistics statistics = new Statistics();
        statistics.setDate(System.currentTimeMillis());
        statistics.setIfIndex(sIfClearedStat.getIfIndex());
        statistics.setIfName(sIfClearedStat.getIfName());
        statistics.setRcvUniPacket(sIfClearedStat.getRcvUniPacket());
        statistics.setSendUniPacket(sIfClearedStat.getSendUniPacket());
        statistics.setSendPacket(sIfClearedStat.getSendPacket());
        statistics.setInPacketRate(sIfClearedStat.getInPacketRate());
        statistics.setOutPacketRate(sIfClearedStat.getOutPacketRate());
        statistics.setInUseRate(sIfClearedStat.getInUseRate());
        statistics.setOutUseRate(sIfClearedStat.getOutUseRate());
        statistics.setRcvErrorPacket(sIfClearedStat.getRcvErrorPacket());
        statistics.setSendErrorPacket(sIfClearedStat.getSendErrorPacket());
        return statistics;
    }
}
