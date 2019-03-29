package cn.org.upbnc.api;

import cn.org.upbnc.service.ServiceInterface;

import java.util.Map;

public interface StatisticsApi {
    boolean setServiceInterface(ServiceInterface serviceInterface);
    Map<String, Object> getStatisticsMap(String routerId,String ifName);
}
