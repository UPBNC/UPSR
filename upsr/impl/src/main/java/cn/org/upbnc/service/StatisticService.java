package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.enumtype.TimeEnum;
import cn.org.upbnc.service.entity.StatisticsEntity;

import java.util.List;

public interface StatisticService {
    boolean setBaseInterface(BaseInterface baseInterface);
    List<StatisticsEntity> getStatisticsMap(String routerId, TimeEnum timeEnum);
    void setStatistics();
}
