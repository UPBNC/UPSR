package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.ReadAndWriteManager;
import cn.org.upbnc.base.StatisticsManager;
import cn.org.upbnc.entity.Statistics;
import cn.org.upbnc.enumtype.TimeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    public List<Statistics> getStatistics(String routerId, TimeEnum timeEnum) {
        List<Statistics> statistics = new ArrayList<>();
        List<Statistics> statisticsList = readAndWriteManager.read(timeEnum);
        for (Statistics statistic : statisticsList) {
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
    public void setStatistics(List<Statistics> statistics) {
        readAndWriteManager.write(statistics);
    }
}
