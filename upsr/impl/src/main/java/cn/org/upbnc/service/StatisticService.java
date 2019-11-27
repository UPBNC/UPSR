package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.entity.statistics.Statistics;
import cn.org.upbnc.enumtype.TimeEnum;
import cn.org.upbnc.service.entity.statistics.StatisticsEntity;
import cn.org.upbnc.service.entity.statistics.IfStatisticsServiceEntity;

import java.util.List;
import java.util.Map;

public interface StatisticService {
    boolean setBaseInterface(BaseInterface baseInterface);
    List<StatisticsEntity> getStatisticsMap(String routerId, TimeEnum timeEnum);
    Map<String,List<IfStatisticsServiceEntity>> getIfStatistics(String routerId);
    Map<String,List<Statistics>> getIfClearedStat(String routerId);
    void setStatistics();
}
