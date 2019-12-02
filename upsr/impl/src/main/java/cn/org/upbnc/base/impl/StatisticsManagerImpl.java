package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.ReadAndWriteManager;
import cn.org.upbnc.base.StatisticsManager;
import cn.org.upbnc.entity.statistics.IfClearedStatEntity;
import cn.org.upbnc.entity.statistics.IfStatisticsEntity;
import cn.org.upbnc.entity.statistics.CpuInfoEntity;
import cn.org.upbnc.entity.statistics.MemoryInfoEntity;
import cn.org.upbnc.enumtype.TimeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StatisticsManagerImpl implements StatisticsManager {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticsManagerImpl.class);

    private static StatisticsManager instance;
    ReadAndWriteManager readAndWriteManager = ReadAndWriteManagerImpl.getInstance();


    public static StatisticsManager getInstance() {
        if (null == instance) {
            instance = new StatisticsManagerImpl();
        }
        return instance;
    }

    @Override
    public List<IfClearedStatEntity> getStatistics(String routerId, TimeEnum timeEnum) {
        List<IfClearedStatEntity> statistics = new ArrayList<>();
        List<IfClearedStatEntity> ifClearedStatEntityList = readAndWriteManager.readIfClearedStat(timeEnum);
        for (IfClearedStatEntity statistic : ifClearedStatEntityList) {
            if ("".equals(routerId)) {
                statistics.add(statistic);
            } else {
                if (routerId.equals(statistic.getRouterId())) {
                    statistics.add(statistic);
                }
            }
        }
        return statistics;
    }

    @Override
    public void setIfClearedStat(List<IfClearedStatEntity> statistics) {
        readAndWriteManager.writeIfClearedStat(statistics);
    }

    @Override
    public void setifStatistics(List<IfStatisticsEntity> ifStatisticsEntityList) {
        readAndWriteManager.writeIfStatisticsEntity(ifStatisticsEntityList);
    }

    @Override
    public void setCpuInfo(List<CpuInfoEntity> cpuInfoEntityList) {
        readAndWriteManager.writeCpuInfoEntity(cpuInfoEntityList);
    }

    @Override
    public void setMemoryInfo(List<MemoryInfoEntity> memoryInfoEntityList) {
        readAndWriteManager.writeMemoryInfoEntity(memoryInfoEntityList);
    }
}
