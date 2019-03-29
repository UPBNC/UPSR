package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.StatisticsManager;
import cn.org.upbnc.entity.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticsManagerImpl implements StatisticsManager{
    private static final Logger LOG = LoggerFactory.getLogger(RoutePolicyManagerImpl.class);

    private static StatisticsManager instance;
    public static Map<String, Map<String, Statistics>> statisticsMap = new ConcurrentHashMap<>();

    public static StatisticsManager getInstance() {
        if (null == instance) {
            instance = new StatisticsManagerImpl();
        }
        return instance;
    }
    @Override
    public List<Statistics> getStatistics(String routerId, String ifName) {
        return null;
    }
}
