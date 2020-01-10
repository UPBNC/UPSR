package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.entity.Address;
import cn.org.upbnc.entity.statistics.IfClearedStatEntity;
import cn.org.upbnc.enumtype.TimeEnum;
import cn.org.upbnc.service.entity.statistics.CpuInfoServiceEntity;
import cn.org.upbnc.service.entity.statistics.IfClearedStatServiceEntity;
import cn.org.upbnc.service.entity.statistics.IfStatisticsServiceEntity;
import cn.org.upbnc.service.entity.statistics.MemoryInfoServiceEntity;

import java.util.List;
import java.util.Map;

public interface StatisticService {
    boolean setBaseInterface(BaseInterface baseInterface);
    List<IfClearedStatServiceEntity> getStatisticsMap(String routerId, TimeEnum timeEnum);
    Map<String,List<IfStatisticsServiceEntity>> getIfStatistics(String routerId, int entityNum);
    Map<String,List<IfClearedStatServiceEntity>> getIfClearedStat(String routerId, int entityNum);
    Map<String,List<CpuInfoServiceEntity>> getCpuInfo(String routerId, int entityNum);
    Map<String,List<MemoryInfoServiceEntity>> getMemoryInfo(String routerId, int entityNum);
    Map<String,Integer> getDedicateBand(String routerId, String ifName);
    Map<String,Integer> getOutUsedBand(String routerId, String ifName);
    Map<String,Integer> getRemainingband(String routerId, String ifName);
    Map<String,Integer> getRemainingband(String routerId, Address ifIp);
    void setStatistics();
}
