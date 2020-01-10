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
import cn.org.upbnc.xmlcompare.Interface;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.cpuinfogroup.CpuInfo;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.cpuinfogroup.CpuInfoBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.cpuinfostatlist.CpuInfoStat;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.cpuinfostatlist.CpuInfoStatBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getifstatistics.output.RouterIfStatistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getifstatistics.output.RouterIfStatisticsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getstatistic.output.Statistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getstatistic.output.StatisticsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.getstatistichistory.output.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.ifclearedstatgroup.IfCleared;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.ifclearedstatgroup.IfClearedBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.ifstatisticsgroup.IfStatistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.ifstatisticsgroup.IfStatisticsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.memoryinfogroup.MemoryInfo;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.memoryinfogroup.MemoryInfoBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.memoryinfostatlist.MemoryInfoStat;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.memoryinfostatlist.MemoryInfoStatBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.routerifclearedlist.RouterIfCleared;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.routerifclearedlist.RouterIfClearedBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
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
        if (input == null) {
            resultMap = this.getStatisticsApi().getMemoryInfo(null,1);
        } else {
            resultMap = this.getStatisticsApi().getMemoryInfo(input.getRouterId(),1);
        }
        List<Map<String,List<MemoryInfoServiceEntity>>> mapServiceList = (List<Map<String,
                List<MemoryInfoServiceEntity>>>)resultMap.get(ResponseEnum.BODY.getName());
        Map<String,List<MemoryInfoServiceEntity>> memoryInfoMap = mapServiceList.get(0);
        List<MemoryInfoStat> memoryInfoStatList = this.buildMemoryInfoStat(memoryInfoMap,null);
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
        if (input == null) {
            resultMap = this.getStatisticsApi().getCpuInfo(null,1);
        } else {
            resultMap = this.getStatisticsApi().getCpuInfo(input.getRouterId(),1);
        }
        List<Map<String,List<CpuInfoServiceEntity>>> mapServiceList = (List<Map<String,
                List<CpuInfoServiceEntity>>>)resultMap.get(ResponseEnum.BODY.getName());
        Map<String,List<CpuInfoServiceEntity>> cpuInfoMap = mapServiceList.get(0);
        List<CpuInfoStat> cpuInfoStatList = this.buildCpuInfoStat(cpuInfoMap,null);
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
        if (input == null) {
            resultMap = this.getStatisticsApi().getIfClearedStat(null,1);
        } else {
            resultMap = this.getStatisticsApi().getIfClearedStat(input.getRouterId(),1);
        }
        List<Map<String,List<IfClearedStatServiceEntity>>> mapServiceList = (List<Map<String,
                List<IfClearedStatServiceEntity>>>)resultMap.get(ResponseEnum.BODY.getName());
        Map<String,List<IfClearedStatServiceEntity>> ifClearedStatMap = mapServiceList.get(0);
        List<RouterIfCleared> routerIfClearedList = this.buildIfClearedStat(ifClearedStatMap,null,null,null);
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
        if (input == null) {
            resultMap = this.getStatisticsApi().getIfStatistics(null,1);
        } else {
            resultMap = this.getStatisticsApi().getIfStatistics(input.getRouterId(),1);
        }
        List<Map<String,List<IfStatisticsServiceEntity>>> mapServiceList = (List<Map<String,
                List<IfStatisticsServiceEntity>>>)resultMap.get(ResponseEnum.BODY.getName());
        Map<String,List<IfStatisticsServiceEntity>> ifStatisticsMap = mapServiceList.get(0);
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

    @Override
    public Future<RpcResult<GetStatisticHistoryOutput>> getStatisticHistory(GetStatisticHistoryInput input) {
        LOG.info("getStatisticHistory begin");
        GetStatisticHistoryOutputBuilder getStatisticHistoryOutputBuilder = new GetStatisticHistoryOutputBuilder();
        List<CpuInfoHistory> cpuInfoHistoryList = new ArrayList<>();
        List<MemoryInfoHistory> memoryInfoHistoryList = new ArrayList<>();
        List<IfClearedHistory> ifClearedHistoryList = new ArrayList<>();
        Map<String, Object> resultMap;
        int entityNum = 50;
        if ((input != null) && (input.getRouterId() != null)) {
            if (input.getHistoryNum() != null) {
                entityNum = Integer.parseInt(input.getHistoryNum()) + 1;
            }
            resultMap = this.getStatisticsApi().getMemoryInfo(null, entityNum);
            List<Map<String, List<MemoryInfoServiceEntity>>> mapMemServiceList = (List<Map<String,
                    List<MemoryInfoServiceEntity>>>) resultMap.get(ResponseEnum.BODY.getName());
            for (Map<String, List<MemoryInfoServiceEntity>> memoryInfoMap : mapMemServiceList) {
                MemoryInfoHistoryBuilder memoryInfoHistoryBuilder = new MemoryInfoHistoryBuilder();
                memoryInfoHistoryBuilder.setHistoryIndex("" + mapMemServiceList.indexOf(memoryInfoMap));
                memoryInfoHistoryBuilder.setMemoryInfoStat(this.buildMemoryInfoStat(memoryInfoMap, input.getRouterId()));
                memoryInfoHistoryList.add(memoryInfoHistoryBuilder.build());
            }

            resultMap = this.getStatisticsApi().getIfClearedStat(null, entityNum);
            List<Map<String, List<IfClearedStatServiceEntity>>> mapIfServiceList = (List<Map<String,
                    List<IfClearedStatServiceEntity>>>) resultMap.get(ResponseEnum.BODY.getName());
            for (Map<String, List<IfClearedStatServiceEntity>> ifClearedStatMap : mapIfServiceList) {
                IfClearedHistoryBuilder ifClearedHistoryBuilder = new IfClearedHistoryBuilder();
                ifClearedHistoryBuilder.setHistoryIndex("" + mapIfServiceList.indexOf(ifClearedStatMap));
                ifClearedHistoryBuilder.setRouterIfCleared(this.buildIfClearedStat(ifClearedStatMap,
                        input.getRouterId(),input.getVpnName(),input.getTunnelName()));
                ifClearedHistoryList.add(ifClearedHistoryBuilder.build());
            }

            resultMap = this.getStatisticsApi().getCpuInfo(null, entityNum);
            List<Map<String, List<CpuInfoServiceEntity>>> mapCpuServiceList = (List<Map<String,
                    List<CpuInfoServiceEntity>>>) resultMap.get(ResponseEnum.BODY.getName());
            for (Map<String, List<CpuInfoServiceEntity>> cpuInfoMap : mapCpuServiceList) {
                CpuInfoHistoryBuilder cpuInfoHistoryBuilder = new CpuInfoHistoryBuilder();
                cpuInfoHistoryBuilder.setHistoryIndex("" + mapCpuServiceList.indexOf(cpuInfoMap));
                cpuInfoHistoryBuilder.setCpuInfoStat(this.buildCpuInfoStat(cpuInfoMap,input.getRouterId()));
                cpuInfoHistoryList.add(cpuInfoHistoryBuilder.build());
            }
            getStatisticHistoryOutputBuilder.setCpuInfoHistory(cpuInfoHistoryList);
            getStatisticHistoryOutputBuilder.setMemoryInfoHistory(memoryInfoHistoryList);
            getStatisticHistoryOutputBuilder.setIfClearedHistory(ifClearedHistoryList);
        }
        getStatisticHistoryOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("getStatisticHistory end");
        return RpcResultBuilder.success(getStatisticHistoryOutputBuilder.build()).buildFuture();
    }
    private List<RouterIfCleared> buildIfClearedStat(Map<String,List<IfClearedStatServiceEntity>> ifClearedStatMap,
                                                     String routerId, String vpnName, String tunnelName) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<RouterIfCleared> routerIfClearedList = new ArrayList<>();
        for (String rid : ifClearedStatMap.keySet()) {
            if ((routerId != null) && (routerId.equals(rid))) {
                RouterIfClearedBuilder routerIfClearedBuilder = new RouterIfClearedBuilder();
                List<IfCleared> ifClearedList = new ArrayList<>();
                for (IfClearedStatServiceEntity ifClearedStatServiceEntity : ifClearedStatMap.get(rid)) {
                    if ((vpnName != null) && (vpnName.equals(ifClearedStatServiceEntity.getVpnName()) != true)) {
                        continue;
                    }
                    if ((tunnelName != null) && (tunnelName.equals(ifClearedStatServiceEntity.getIfName()) != true)) {
                        continue;
                    }
                    IfClearedBuilder ifClearedBuilder = new IfClearedBuilder();
                    ifClearedBuilder.setDate(dateformat.format(ifClearedStatServiceEntity.getDate()));
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
        }
        return routerIfClearedList;
    }
    private List<CpuInfoStat> buildCpuInfoStat(Map<String,List<CpuInfoServiceEntity>> cpuInfoMap, String routerId) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<CpuInfoStat> cpuInfoStatList = new ArrayList<>();
        for (String rid : cpuInfoMap.keySet()) {
            if ((routerId != null) && (routerId.equals(rid))) {
                CpuInfoStatBuilder cpuInfoStatBuilder = new CpuInfoStatBuilder();
                List<CpuInfo> cpuInfoList = new ArrayList<>();
                for (CpuInfoServiceEntity cpuInfoServiceEntity : cpuInfoMap.get(rid)) {
                    CpuInfoBuilder cpuInfoBuilder = new CpuInfoBuilder();
                    cpuInfoBuilder.setDate(dateformat.format(cpuInfoServiceEntity.getDate()));
                    cpuInfoBuilder.setEntIndex(cpuInfoServiceEntity.getEntIndex());
                    cpuInfoBuilder.setSystemCpuUsage(cpuInfoServiceEntity.getSystemCpuUsage());
                    cpuInfoBuilder.setOvloadThreshold(cpuInfoServiceEntity.getOvloadThreshold());
                    cpuInfoBuilder.setPosition(cpuInfoServiceEntity.getPosition());
                    cpuInfoBuilder.setUnovloadThreshold(cpuInfoServiceEntity.getUnovloadThreshold());
                    cpuInfoList.add(cpuInfoBuilder.build());
                }
                if (cpuInfoList.size() > 0) {
                    cpuInfoStatBuilder.setRouterId(rid);
                    cpuInfoStatBuilder.setCpuInfo(cpuInfoList);
                    cpuInfoStatList.add(cpuInfoStatBuilder.build());
                }
            }
        }
        return cpuInfoStatList;
    }
    private List<MemoryInfoStat> buildMemoryInfoStat(Map<String,List<MemoryInfoServiceEntity>> memoryInfoMap, String routerId) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<MemoryInfoStat> memoryInfoStatList = new ArrayList<>();
        for (String rid : memoryInfoMap.keySet()) {
            if ((routerId != null) && (routerId.equals(rid))) {
                MemoryInfoStatBuilder memoryInfoStatBuilder = new MemoryInfoStatBuilder();
                List<MemoryInfo> memoryInfoList = new ArrayList<>();
                for (MemoryInfoServiceEntity memoryInfoServiceEntity : memoryInfoMap.get(rid)) {
                    MemoryInfoBuilder memoryInfoBuilder = new MemoryInfoBuilder();
                    memoryInfoBuilder.setEntIndex(memoryInfoServiceEntity.getEntIndex());
                    memoryInfoBuilder.setOsMemoryUsage(memoryInfoServiceEntity.getOsMemoryUsage());
                    memoryInfoBuilder.setOsMemoryTotal(memoryInfoServiceEntity.getOsMemoryTotal());
                    memoryInfoBuilder.setOsMemoryUse(memoryInfoServiceEntity.getOsMemoryUse());
                    memoryInfoBuilder.setDate(dateformat.format(memoryInfoServiceEntity.getDate()));
                    memoryInfoList.add(memoryInfoBuilder.build());
                }
                if (memoryInfoList.size() > 0) {
                    memoryInfoStatBuilder.setMemoryInfo(memoryInfoList);
                    memoryInfoStatBuilder.setRouterId(rid);
                    memoryInfoStatList.add(memoryInfoStatBuilder.build());
                }
            }
        }
        return memoryInfoStatList;
    }
}
