package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.StatisticsApi;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.StatisticService;
import cn.org.upbnc.service.entity.StatisticsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsApiImpl implements StatisticsApi {
    private static final Logger LOG = LoggerFactory.getLogger(VpnInstanceApiImpl.class);
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
    public Map<String, Object> getStatisticsMap(String routerId, String ifName) {
        Map<String, Object> resultMap = new HashMap<>();
        if (null == this.statisticService) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.BODY.getName(), null);
            resultMap.put(ResponseEnum.MESSAGE.getName(), "StatisticsApiImpl-getStatisticsMap() : " +
                    "statisticService " +
                    "is null.");
            return resultMap;
        }
        List<StatisticsEntity> statisticsEntityList = new ArrayList<>();
        statisticsEntityList = statisticService.getStatisticsMap(routerId, ifName);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(), statisticsEntityList);
        return resultMap;
    }
}
