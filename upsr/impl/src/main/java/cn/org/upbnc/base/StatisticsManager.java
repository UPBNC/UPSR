package cn.org.upbnc.base;

import cn.org.upbnc.entity.statistics.Statistics;
import cn.org.upbnc.entity.statistics.IfStatisticsEntity;
import cn.org.upbnc.enumtype.TimeEnum;

import java.util.List;

public interface StatisticsManager {
    List<Statistics> getStatistics(String routerId, TimeEnum timeEnum);
    void setIfClearedStat(List<Statistics> statistics);
    void setifStatistics(List<IfStatisticsEntity> ifStatisticsEntityList);
}
