package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.entity.statistics.IfClearedStatEntity;
import cn.org.upbnc.enumtype.TimeEnum;
import cn.org.upbnc.service.entity.statistics.IfClearedStatServiceEntity;
import cn.org.upbnc.service.entity.statistics.IfStatisticsServiceEntity;

import java.util.List;
import java.util.Map;

public interface StatisticService {
    boolean setBaseInterface(BaseInterface baseInterface);
    List<IfClearedStatServiceEntity> getStatisticsMap(String routerId, TimeEnum timeEnum);
    Map<String,List<IfStatisticsServiceEntity>> getIfStatistics(String routerId);
    Map<String,List<IfClearedStatEntity>> getIfClearedStat(String routerId);
    void setStatistics();
}
