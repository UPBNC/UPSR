package cn.org.upbnc.base;

import cn.org.upbnc.entity.statistics.IfClearedStatEntity;
import cn.org.upbnc.entity.statistics.IfStatisticsEntity;
import cn.org.upbnc.entity.statistics.CpuInfoEntity;
import cn.org.upbnc.entity.statistics.MemoryInfoEntity;
import cn.org.upbnc.enumtype.TimeEnum;

import java.util.List;
import java.util.Map;

public interface StatisticsManager {
    List<IfClearedStatEntity> getStatistics(String routerId, TimeEnum timeEnum);
    void setIfClearedStat(List<IfClearedStatEntity> statistics);
    void setIfClearedStatMap(Map<String,List<IfClearedStatEntity>> ifClearedStatMap);
    void setIfStatistics(List<IfStatisticsEntity> ifStatisticsEntityList);
    void setIfStatisticsMap(Map<String,List<IfStatisticsEntity>> ifStatisticsMap);
    void setCpuInfo(List<CpuInfoEntity> cpuInfoEntityList);
    void setCpuInfoMap(Map<String,List<CpuInfoEntity>> cpuInfoMap);
    void setMemoryInfo(List<MemoryInfoEntity> memoryInfoEntityList);
    void setMemoryInfoMap(Map<String,List<MemoryInfoEntity>> memoryInfoMap);
    Map<String,List<IfClearedStatEntity>> getIfClearedStatMap();
    Map<String,List<IfStatisticsEntity>>  getIfStatisticsMap();
    Map<String,List<CpuInfoEntity>> getCpuInfoMap();
    Map<String,List<MemoryInfoEntity>> getMemoryInfoMap();
}
