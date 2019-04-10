package cn.org.upbnc.base;

import cn.org.upbnc.entity.Statistics;
import cn.org.upbnc.enumtype.TimeEnum;

import java.util.List;

public interface ReadAndWriteManager {
    void write(List<Statistics> statistics);
    List<Statistics> read(TimeEnum time);
}
