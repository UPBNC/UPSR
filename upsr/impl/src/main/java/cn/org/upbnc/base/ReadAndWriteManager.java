package cn.org.upbnc.base;

import cn.org.upbnc.entity.statistics.IfClearedStatEntity;
import cn.org.upbnc.entity.statistics.IfStatisticsEntity;
import cn.org.upbnc.entity.statistics.CpuInfoEntity;
import cn.org.upbnc.entity.statistics.MemoryInfoEntity;
import cn.org.upbnc.enumtype.TimeEnum;

import java.util.List;

public interface ReadAndWriteManager {
    void writeIfClearedStat(List<IfClearedStatEntity> statistics);
    void writeIfStatisticsEntity(List<IfStatisticsEntity> ifStatisticsEntityList);
    void writeCpuInfoEntity(List<CpuInfoEntity> cpuInfoEntityList);
    void writeMemoryInfoEntity(List<MemoryInfoEntity> memoryInfoEntityList);
    List<IfClearedStatEntity> readIfClearedStat(TimeEnum time);
}
