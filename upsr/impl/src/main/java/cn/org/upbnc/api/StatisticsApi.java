package cn.org.upbnc.api;

import cn.org.upbnc.enumtype.TimeEnum;
import cn.org.upbnc.service.ServiceInterface;

import java.util.Map;

public interface StatisticsApi {
    boolean setServiceInterface(ServiceInterface serviceInterface);
    Map<String, Object> getStatisticsMap(String routerId, String type);
    Map<String, Object> getIfClearedStat(String routerId);
    Map<String, Object> getIfStatistics(String routerId);
    Map<String, Object> getCpuInfo(String routerId);
    Map<String, Object> getMemoryInfo(String routerId);
    void setStatistics();
}
