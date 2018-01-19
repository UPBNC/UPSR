package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.StatisticsApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.core.StatisticsThread;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.service.entity.statistics.CpuInfoServiceEntity;
import cn.org.upbnc.service.entity.statistics.IfClearedStatServiceEntity;
import cn.org.upbnc.service.entity.statistics.IfStatisticsServiceEntity;
import cn.org.upbnc.service.entity.statistics.MemoryInfoServiceEntity;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.cpuinfogroup.CpuInfo;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.cpuinfogroup.CpuInfoBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getcpuinfo.output.CpuInfoStat;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getcpuinfo.output.CpuInfoStatBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getifclearedstat.output.RouterIfCleared;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getifclearedstat.output.RouterIfClearedBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getifstatistics.output.RouterIfStatistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getifstatistics.output.RouterIfStatisticsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getmemoryinfo.output.MemoryInfoStat;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getmemoryinfo.output.MemoryInfoStatBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getstatistic.output.Statistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getstatistic.output.StatisticsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.ifclearedstatgroup.IfCleared;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.ifclearedstatgroup.IfClearedBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.ifstatisticsgroup.IfStatistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.ifstatisticsgroup.IfStatisticsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.memoryinfogroup.MemoryInfo;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.memoryinfogroup.MemoryInfoBuilder;
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
            List<IfClearedStatServiceEntity> ifClearedStatServiceEntityList = (List<IfClearedStatServiceEntity>) resultMap.get(ResponseEnum.BODY.getName());
            getStatisticOutputBuilder.setStatistics(statisticsEntityMapToStatistics(ifClearedStatServiceEntityList));
            getStatisticOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        } else {
            getStatisticOutputBuilder.setResult(CodeEnum.ERROR.getMessage());
        }
        return RpcResultBuilder.success(getStatisticOutputBuilder.build()).buildFuture();
    }

    private List<Statistics> statisticsEntityMapToStatistics(List<IfClearedStatServiceEntity> ifClearedStatServiceEntityList){
        List<Statistics> statisticsList=new ArrayList<>();
        StatisticsBuilder statistics;
        for(IfClearedStatServiceEntity entity: ifClearedStatServiceEntityList){
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
    public Future<RpcResult<UpdateStatisticSwitchOutput>> updateStatisticSwitch(UpdateStatisticSwitchInput input) {
        LOG.info("statisticSwitch end");
        UpdateStatisticSwitchOutputBuilder statisticSwitchOutputBuilder = new UpdateStatisticSwitchOutputBuilder();
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

    @Override
    public Future<RpcResult<GetMemoryInfoOutput>> getMemoryInfo(GetMemoryInfoInput input) {
        LOG.info("getMemoryInfo begin");
        GetMemoryInfoOutputBuilder getMemoryInfoOutputBuilder = new GetMemoryInfoOutputBuilder();
        Map<String, Object> resultMap;
        resultMap = this.getStatisticsApi().getMemoryInfo(input.getRouterId());
        Map<String,List<MemoryInfoServiceEntity>> memoryInfoMap =
                (Map<String,List<MemoryInfoServiceEntity>>)resultMap.get(ResponseEnum.BODY.getName());
        List<MemoryInfoStat> memoryInfoStatList = new ArrayList<>();
        for (String rid : memoryInfoMap.keySet()) {
            MemoryInfoStatBuilder memoryInfoStatBuilder = new MemoryInfoStatBuilder();
            List<MemoryInfo> memoryInfoList = new ArrayList<>();
            for (MemoryInfoServiceEntity memoryInfoServiceEntity : memoryInfoMap.get(rid)) {
                MemoryInfoBuilder memoryInfoBuilder = new MemoryInfoBuilder();
                memoryInfoBuilder.setEntIndex(memoryInfoServiceEntity.getEntIndex());
                memoryInfoBuilder.setOsMemoryUsage(memoryInfoServiceEntity.getOsMemoryUsage());
                memoryInfoBuilder.setOsMemoryTotal(memoryInfoServiceEntity.getOsMemoryTotal());
                memoryInfoBuilder.setOsMemoryUse(memoryInfoServiceEntity.getOsMemoryUse());
                memoryInfoList.add(memoryInfoBuilder.build());
            }
            memoryInfoStatBuilder.setMemoryInfo(memoryInfoList);
            memoryInfoStatBuilder.setRouterId(rid);
            memoryInfoStatList.add(memoryInfoStatBuilder.build());
        }
        getMemoryInfoOutputBuilder.setMemoryInfoStat(memoryInfoStatList);
        getMemoryInfoOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("getMemoryInfo end");
        return RpcResultBuilder.success(getMemoryInfoOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetCpuInfoOutput>> getCpuInfo(GetCpuInfoInput input) {
        LOG.info("getCpuInfo begin");
        GetCpuInfoOutputBuilder getCpuInfoOutputBuilder = new GetCpuInfoOutputBuilder();
        Map<String, Object> resultMap;
        resultMap = this.getStatisticsApi().getCpuInfo(input.getRouterId());
        Map<String,List<CpuInfoServiceEntity>> cpuInfoMap =
                (Map<String,List<CpuInfoServiceEntity>>)resultMap.get(ResponseEnum.BODY.getName());
        List<CpuInfoStat> cpuInfoStatList = new ArrayList<>();
        for (String rid : cpuInfoMap.keySet()) {
            CpuInfoStatBuilder cpuInfoStatBuilder = new CpuInfoStatBuilder();
            List<CpuInfo> cpuInfoList = new ArrayList<>();
            for (CpuInfoServiceEntity cpuInfoServiceEntity : cpuInfoMap.get(rid)) {
                CpuInfoBuilder cpuInfoBuilder = new CpuInfoBuilder();
                cpuInfoBuilder.setEntIndex(cpuInfoServiceEntity.getEntIndex());
                cpuInfoBuilder.setSystemCpuUsage(cpuInfoServiceEntity.getSystemCpuUsage());
                cpuInfoBuilder.setOvloadThreshold(cpuInfoServiceEntity.getOvloadThreshold());
                cpuInfoBuilder.setPosition(cpuInfoServiceEntity.getPosition());
                cpuInfoBuilder.setUnovloadThreshold(cpuInfoServiceEntity.getUnovloadThreshold());
                cpuInfoList.add(cpuInfoBuilder.build());
            }
            cpuInfoStatBuilder.setRouterId(rid);
            cpuInfoStatBuilder.setCpuInfo(cpuInfoList);
            cpuInfoStatList.add(cpuInfoStatBuilder.build());
        }
        getCpuInfoOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        getCpuInfoOutputBuilder.setCpuInfoStat(cpuInfoStatList);
        LOG.info("getCpuInfo end");
        return RpcResultBuilder.success(getCpuInfoOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetIfClearedStatOutput>> getIfClearedStat(GetIfClearedStatInput input) {
        LOG.info("getIfClearedStat begin");
        GetIfClearedStatOutputBuilder getIfClearedStatOutputBuilder= new GetIfClearedStatOutputBuilder();
        Map<String, Object> resultMap;
        resultMap = this.getStatisticsApi().getIfClearedStat(input.getRouterId());
        Map<String,List<IfClearedStatServiceEntity>> ifClearedStatMap =
                (Map<String,List<IfClearedStatServiceEntity>>)resultMap.get(ResponseEnum.BODY.getName());
        List<RouterIfCleared> routerIfClearedList = new ArrayList<>();
        for (String rid : ifClearedStatMap.keySet()) {
            RouterIfClearedBuilder routerIfClearedBuilder = new RouterIfClearedBuilder();
            List<IfCleared> ifClearedList = new ArrayList<>();
            for (IfClearedStatServiceEntity ifClearedStatServiceEntity : ifClearedStatMap.get(rid)) {
                IfClearedBuilder ifClearedBuilder = new IfClearedBuilder();
                ifClearedBuilder.setDate(String.valueOf(ifClearedStatServiceEntity.getDate()));
                ifClearedBuilder.setIfIndex(ifClearedStatServiceEntity.getIfIndex());
                ifClearedBuilder.setIfName(ifClearedStatServiceEntity.getIfName());
                ifClearedBuilder.setRcvUniPacket(ifClearedStatServiceEntity.getRcvUniPacket());
                ifClearedBuilder.setSendUniPacket(ifClearedStatServiceEntity.getSendUniPacket());
                ifClearedBuilder.setInPacketRate(ifClearedStatServiceEntity.getInPacketRate());
                ifClearedBuilder.setOutPacketRate(ifClearedStatServiceEntity.getOutPacketRate());
                ifClearedBuilder.setInUseRate(ifClearedStatServiceEntity.getInUseRate());
                ifClearedBuilder.setOutUseRate(ifClearedStatServiceEntity.getOutUseRate());
                ifClearedBuilder.setRcvErrorPacket(ifClearedStatServiceEntity.getRcvErrorPacket());
                ifClearedBuilder.setSendErrorPacket(ifClearedStatServiceEntity.getSendErrorPacket());
                ifClearedBuilder.setVpnName(ifClearedStatServiceEntity.getVpnName());
                ifClearedList.add(ifClearedBuilder.build());
            }
            routerIfClearedBuilder.setRouterId(rid);
            routerIfClearedBuilder.setIfCleared(ifClearedList);
            routerIfClearedList.add(routerIfClearedBuilder.build());
        }
        getIfClearedStatOutputBuilder.setRouterIfCleared(routerIfClearedList);
        getIfClearedStatOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("getIfClearedStat end");
        return RpcResultBuilder.success(getIfClearedStatOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetIfStatisticsOutput>> getIfStatistics(GetIfStatisticsInput input) {
        LOG.info("getIfStatistics begin");
        GetIfStatisticsOutputBuilder getIfStatisticsOutputBuilder = new GetIfStatisticsOutputBuilder();
        getIfStatisticsOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        Map<String, Object> resultMap;
        resultMap = this.getStatisticsApi().getIfStatistics(input.getRouterId());
        Map<String,List<IfStatisticsServiceEntity>> ifStatisticsMap =
                (Map<String,List<IfStatisticsServiceEntity>>)resultMap.get(ResponseEnum.BODY.getName());
        List<RouterIfStatistics> routerIfStatisticsList = new ArrayList<>();
        for (String rid : ifStatisticsMap.keySet()) {
            RouterIfStatisticsBuilder routerIfStatisticsBuilder = new RouterIfStatisticsBuilder();
            List<IfStatistics> ifStatisticsList = new ArrayList<>();
            for (IfStatisticsServiceEntity ifStatisticsServiceEntity : ifStatisticsMap.get(rid)) {
                IfStatisticsBuilder ifStatisticsBuilder = new IfStatisticsBuilder();
                ifStatisticsBuilder.setIfIndex(ifStatisticsServiceEntity.getIfIndex());
                ifStatisticsBuilder.setIfName(ifStatisticsServiceEntity.getIfName());
                ifStatisticsList.add(ifStatisticsBuilder.build());
            }
            routerIfStatisticsBuilder.setRouterId(rid);
            routerIfStatisticsBuilder.setIfStatistics(ifStatisticsList);
            routerIfStatisticsList.add(routerIfStatisticsBuilder.build());
        }
        getIfStatisticsOutputBuilder.setRouterIfStatistics(routerIfStatisticsList);
        LOG.info("getIfStatistics end");
        return RpcResultBuilder.success(getIfStatisticsOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetStatisticSwitchOutput>> getStatisticSwitch(GetStatisticSwitchInput input) {
        LOG.info("getStatisticSwitch end");
        GetStatisticSwitchOutputBuilder getStatisticSwitchOutputBuilder = new GetStatisticSwitchOutputBuilder();
        StatisticsThread statisticsThread = StatisticsThread.getInstance();
        LOG.info("switch   : " + statisticsThread.getSwitchValve());
        LOG.info("interval : " + statisticsThread.getStatisticInterval());
        getStatisticSwitchOutputBuilder.setStatisticInterval("" + statisticsThread.getStatisticInterval());
        getStatisticSwitchOutputBuilder.setSwitchValue(statisticsThread.getSwitchValve());
        getStatisticSwitchOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("getStatisticSwitch end");
        return RpcResultBuilder.success(getStatisticSwitchOutputBuilder.build()).buildFuture();
    }
}
