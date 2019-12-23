package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.StatisticsApi;
import cn.org.upbnc.entity.Address;
import cn.org.upbnc.entity.statistics.IfClearedStatEntity;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.TimeEnum;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.StatisticService;
import cn.org.upbnc.service.entity.statistics.CpuInfoServiceEntity;
import cn.org.upbnc.service.entity.statistics.IfClearedStatServiceEntity;
import cn.org.upbnc.service.entity.statistics.IfStatisticsServiceEntity;
import cn.org.upbnc.service.entity.statistics.MemoryInfoServiceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsApiImpl implements StatisticsApi {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticsApiImpl.class);
    private static StatisticsApi ourInstance = new StatisticsApiImpl();
    private ServiceInterface serviceInterface;
    private StatisticService statisticService;

    public static StatisticsApi getInstance() {
        return ourInstance;
    }

    private StatisticsApiImpl() {
        this.serviceInterface = null;
    }

    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        this.serviceInterface = serviceInterface;
        if (null != serviceInterface) {
            statisticService = this.serviceInterface.getStatisticService();
        }
        return true;
    }

    @Override
    public Map<String, Object> getStatisticsMap(String routerId, String type) {
        Map<String, Object> resultMap = new HashMap<>();
        if (null == this.statisticService) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.BODY.getName(), null);
            resultMap.put(ResponseEnum.MESSAGE.getName(), "StatisticsApiImpl-getStatisticsMap() : " +
                    "statisticService " +
                    "is null.");
            return resultMap;
        }
        List<IfClearedStatServiceEntity> ifClearedStatServiceEntityList = new ArrayList<>();
        if (null == TimeEnum.getEnum(type)) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.BODY.getName(), null);
            resultMap.put(ResponseEnum.MESSAGE.getName(), "type is not exist.");
        } else {
            ifClearedStatServiceEntityList = statisticService.getStatisticsMap(routerId, TimeEnum.getEnum(type));
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
            resultMap.put(ResponseEnum.BODY.getName(), ifClearedStatServiceEntityList);
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getIfClearedStat(String routerId) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String,List<IfClearedStatServiceEntity>> ifClearedStatMap = statisticService.getIfClearedStat(routerId);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(),ifClearedStatMap);
        return resultMap;
    }

    @Override
    public Map<String, Object> getIfStatistics(String routerId) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String,List<IfStatisticsServiceEntity>> ifStatisticsMap = statisticService.getIfStatistics(routerId);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(),ifStatisticsMap);
        return resultMap;
    }

    @Override
    public Map<String, Object> getCpuInfo(String routerId) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String,List<CpuInfoServiceEntity>> cpuInfoMap = statisticService.getCpuInfo(routerId);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(),cpuInfoMap);
        return resultMap;
    }

    @Override
    public Map<String, Object> getMemoryInfo(String routerId) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String,List<MemoryInfoServiceEntity>> memoryInfoMap = statisticService.getMemoryInfo(routerId);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(),memoryInfoMap);
        return resultMap;
    }

    @Override
    public Map<String, Object> getDedicateBand(String routerId, String ifName) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String,Integer> dedicateBandMap = statisticService.getDedicateBand(routerId,ifName);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(),dedicateBandMap);
        return resultMap;
    }

    @Override
    public Map<String, Object> getOutUsedBand(String routerId, String ifName) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String,Integer> outUsedBandMap = statisticService.getOutUsedBand(routerId,ifName);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(),outUsedBandMap);
        return resultMap;
    }

	@Override
    public Map<String, Object> getRemainingband(String routerId, String ifName) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String,Integer> remainingbandMap = statisticService.getRemainingband(routerId,ifName);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(),remainingbandMap);
        return resultMap;
    }

    @Override
    public Map<String, Object> getRemainingband(String routerId, Address ifIp) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String,Integer> remainingbandMap = statisticService.getRemainingband(routerId,ifIp);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(),remainingbandMap);
        return resultMap;
    }

    @Override
    public void setStatistics() {
        statisticService.setStatistics();
    }
}
