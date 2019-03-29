package cn.org.upbnc.base;

import cn.org.upbnc.entity.Statistics;

import java.util.List;

public interface StatisticsManager {
    List<Statistics> getStatistics(String routerId,String ifName);
}
