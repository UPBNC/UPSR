package cn.org.upbnc.api;

import cn.org.upbnc.entity.Address;
import cn.org.upbnc.enumtype.TimeEnum;
import cn.org.upbnc.service.ServiceInterface;

import java.util.Map;

public interface StatisticsApi {
    boolean setServiceInterface(ServiceInterface serviceInterface);
    Map<String, Object> getStatisticsMap(String routerId, String type);
    Map<String, Object> getIfClearedStat(String routerId, int entityNum);
    Map<String, Object> getIfStatistics(String routerId, int entityNum);
    Map<String, Object> getCpuInfo(String routerId, int entityNum);
    Map<String, Object> getMemoryInfo(String routerId, int entityNum);
    Map<String, Object> getDedicateBand(String routerId,String ifName);
    Map<String, Object> getOutUsedBand(String routerId,String ifName);
    Map<String, Object> getRemainingband(String routerId,String ifName);
    Map<String, Object> getRemainingband(String routerId, Address ifIp);
    void setStatistics();
}
