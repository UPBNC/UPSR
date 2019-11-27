package cn.org.upbnc.base;

import cn.org.upbnc.entity.statistics.IfClearedStatEntity;
import cn.org.upbnc.entity.statistics.IfStatisticsEntity;
import cn.org.upbnc.enumtype.TimeEnum;

import java.util.List;

public interface StatisticsManager {
    List<IfClearedStatEntity> getStatistics(String routerId, TimeEnum timeEnum);
    void setIfClearedStat(List<IfClearedStatEntity> statistics);
    void setifStatistics(List<IfStatisticsEntity> ifStatisticsEntityList);
}
