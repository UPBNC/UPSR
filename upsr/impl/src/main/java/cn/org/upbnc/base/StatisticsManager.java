package cn.org.upbnc.base;

import cn.org.upbnc.entity.Statistics;
import cn.org.upbnc.enumtype.TimeEnum;

import java.util.List;

public interface StatisticsManager {
    List<Statistics> getStatistics(String routerId, TimeEnum timeEnum);
    void setStatistics(List<Statistics> statistics);
}
