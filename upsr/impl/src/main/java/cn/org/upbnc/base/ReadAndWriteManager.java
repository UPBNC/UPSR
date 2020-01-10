package cn.org.upbnc.base;

import cn.org.upbnc.entity.statistics.IfClearedStatEntity;
import cn.org.upbnc.entity.statistics.IfStatisticsEntity;
import cn.org.upbnc.entity.statistics.CpuInfoEntity;
import cn.org.upbnc.entity.statistics.MemoryInfoEntity;
import cn.org.upbnc.enumtype.TimeEnum;

import java.util.List;
import java.util.Map;

public interface ReadAndWriteManager {
    void writeIfClearedStat(List<IfClearedStatEntity> statistics);
    void writeIfStatisticsEntity(List<IfStatisticsEntity> ifStatisticsEntityList);
    void writeCpuInfoEntity(List<CpuInfoEntity> cpuInfoEntityList);
    void writeMemoryInfoEntity(List<MemoryInfoEntity> memoryInfoEntityList);
    void writeIfClearedStatMap(Map<String,List<IfClearedStatEntity>> ifClearedStatMap);
    void writeIfStatisticsMap(Map<String,List<IfStatisticsEntity>> ifStatisticsMap);
    void writeCpuInfoMap(Map<String,List<CpuInfoEntity>> cpuInfoMap);
    void writeMemoryInfoMap(Map<String,List<MemoryInfoEntity>> memoryInfoMap);
    List<Map<String, List<IfClearedStatEntity>>> getIfClearedStatMap(int rows);
    List<Map<String,List<IfStatisticsEntity>>>  getIfStatisticsMap(int rows);
    List<Map<String,List<CpuInfoEntity>>> getCpuInfoMap(int rows);
    List<Map<String,List<MemoryInfoEntity>>> getMemoryInfoMap(int rows);
    List<IfClearedStatEntity> readIfClearedStat(TimeEnum time);
}
