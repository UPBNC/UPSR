package cn.org.upbnc.base;

import cn.org.upbnc.entity.statistics.Statistics;
import cn.org.upbnc.entity.statistics.IfStatisticsEntity;
import cn.org.upbnc.enumtype.TimeEnum;

import java.util.List;

public interface ReadAndWriteManager {
    void write(List<Statistics> statistics);
    void writeIfStatisticsEntity(List<IfStatisticsEntity> ifStatisticsEntityList);
    List<Statistics> read(TimeEnum time);
}
