package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.StatisticsApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.core.StatisticsThread;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.service.entity.StatisticsEntity;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getstatistic.output.Statistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getstatistic.output.StatisticsBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class StatisticODLApi implements UpsrStatisticService {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticODLApi.class);
    Session session;
    StatisticsApi statisticsApi;

    public StatisticODLApi(Session session) {
        this.session = session;
    }

    private StatisticsApi getStatisticsApi() {
        if (this.statisticsApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                this.statisticsApi = apiInterface.getStatisticsApi();
            }
        }
        return this.statisticsApi;
    }

    @Override
    public Future<RpcResult<GetStatisticOutput>> getStatistic(GetStatisticInput input) {

        GetStatisticOutputBuilder getStatisticOutputBuilder = new GetStatisticOutputBuilder();
        getStatisticOutputBuilder.setResult(CodeEnum.ERROR.getMessage());
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            getStatisticOutputBuilder.setResult("SystemStatus is not on.");
            return RpcResultBuilder.success(getStatisticOutputBuilder.build()).buildFuture();
        } else {
            this.getStatisticsApi();
        }
        Map<String, Object> resultMap;
        resultMap = this.getStatisticsApi().getStatisticsMap(input.getRouterId(),input.getType());
        String code = (String) resultMap.get(ResponseEnum.CODE.getName());
        if (CodeEnum.SUCCESS.getName().equals(code)) {
            List<StatisticsEntity> statisticsEntityList = (List<StatisticsEntity>) resultMap.get(ResponseEnum.BODY.getName());
            getStatisticOutputBuilder.setStatistics(statisticsEntityMapToStatistics(statisticsEntityList));
            getStatisticOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        } else {
            getStatisticOutputBuilder.setResult(CodeEnum.ERROR.getMessage());
        }
        return RpcResultBuilder.success(getStatisticOutputBuilder.build()).buildFuture();
    }

    private List<Statistics> statisticsEntityMapToStatistics(List<StatisticsEntity> statisticsEntityList){
        List<Statistics> statisticsList=new ArrayList<>();
        StatisticsBuilder statistics;
        for(StatisticsEntity entity:statisticsEntityList){
            statistics=new StatisticsBuilder();
            statistics.setRouterId(entity.getRouterId());
            statistics.setDate(String.valueOf(entity.getDate()));
            statistics.setIfIndex(entity.getIfIndex());
            statistics.setIfName(entity.getIfName());
            statistics.setRcvUniPacket(entity.getRcvUniPacket());
            statistics.setSendUniPacket(entity.getSendUniPacket());
            statistics.setSendPacket(entity.getSendPacket());
            statistics.setInPacketRate(entity.getInPacketRate());
            statistics.setOutPacketRate(entity.getOutPacketRate());
            statistics.setInUseRate(entity.getInUseRate());
            statistics.setOutUseRate(entity.getOutUseRate());
            statistics.setRcvErrorPacket(entity.getRcvErrorPacket());
            statistics.setSendErrorPacket(entity.getSendErrorPacket());
            statisticsList.add(statistics.build());
        }
        return statisticsList;
    }

    @Override
    public Future<RpcResult<StatisticSwitchOutput>> statisticSwitch(StatisticSwitchInput input) {
        LOG.info("statisticSwitch end");
        StatisticSwitchOutputBuilder statisticSwitchOutputBuilder = new StatisticSwitchOutputBuilder();
        StatisticsThread statisticsThread = StatisticsThread.getInstance();
        if (input.getSwitchValue() != null) {
            statisticsThread.setSwitchValve(input.getSwitchValue());
        }
        if (input.getStatisticInterval() != null) {
            statisticsThread.setStatisticInterval(Long.valueOf(input.getStatisticInterval()).longValue());
        }
        statisticSwitchOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("switch   : " + statisticsThread.getSwitchValve());
        LOG.info("interval : " + statisticsThread.getStatisticInterval());
        LOG.info("statisticSwitch end");
        return RpcResultBuilder.success(statisticSwitchOutputBuilder.build()).buildFuture();
    }
}
