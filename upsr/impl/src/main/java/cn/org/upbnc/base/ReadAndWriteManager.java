package cn.org.upbnc.base;

import cn.org.upbnc.entity.statistics.IfClearedStatEntity;
import cn.org.upbnc.entity.statistics.IfStatisticsEntity;
import cn.org.upbnc.enumtype.TimeEnum;

import java.util.List;

public interface ReadAndWriteManager {
    void write(List<IfClearedStatEntity> statistics);
    void writeIfStatisticsEntity(List<IfStatisticsEntity> ifStatisticsEntityList);
    List<IfClearedStatEntity> read(TimeEnum time);
}
