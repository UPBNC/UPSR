package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.TunnelApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.entity.AdjLabel;
import cn.org.upbnc.entity.ExplicitPath;
import cn.org.upbnc.entity.Tunnel;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.service.entity.DetectTunnelServiceEntity;
import cn.org.upbnc.service.entity.TunnelHopServiceEntity;
import cn.org.upbnc.service.entity.TunnelServiceEntity;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnel.rev181227.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnel.rev181227.detecttunnelpath.output.PingResultBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnel.rev181227.detecttunnelpath.output.TraceResultBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnel.rev181227.detecttunnelpath.output.traceresult.PathInfo;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnel.rev181227.detecttunnelpath.output.traceresult.PathInfoBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnel.rev181227.gettunnelinstances.output.TunnelInstances;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnel.rev181227.gettunnelinstances.output.TunnelInstancesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnel.rev181227.tunnelinstance.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnel.rev181227.tunnelinstance.TunnelBfd;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map;
import java.util.concurrent.Future;

public class TunnelODLApi implements UpsrTunnelService {
    private static final Logger LOG = LoggerFactory.getLogger(TunnelODLApi.class);
    Session session;
    TunnelApi tunnelApi;

    public TunnelODLApi(Session session) {
        this.session = session;
    }

    private TunnelApi getTunnelApi() {
        if (this.tunnelApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                this.tunnelApi = apiInterface.getTunnelApi();
            }
        }
        return this.tunnelApi;
    }

    List<TunnelInstances> getTunnel(List<Tunnel> tunnels) {
        List<TunnelInstances> tunnelInstancesList = new ArrayList<>();
        List<MainPath> mainPathList;
        List<BackPath> backPathList;
        TunnelBfdBuilder tunnelBfdBuilder;
        MainPathBuilder mainPathBuilder;
        BackPathBuilder backPathBuilder;
        Map<String, AdjLabel> map;
        TunnelInstancesBuilder tunnelInstancesBuilder;
        for (Tunnel tunnel : tunnels) {
            mainPathList = new ArrayList<>();
            backPathList = new ArrayList<>();
            tunnelInstancesBuilder = new TunnelInstancesBuilder();
            tunnelInstancesBuilder.setSrcDevice(tunnel.getDevice().getDeviceName());
            tunnelInstancesBuilder.setSrcRouterId(tunnel.getDevice().getRouterId());
            tunnelInstancesBuilder.setTunnelId(tunnel.getTunnelId());
            tunnelInstancesBuilder.setTunnelName(tunnel.getTunnelName());
            tunnelInstancesBuilder.setDestDevice(tunnel.getDestDeviceName());
            tunnelInstancesBuilder.setDestRouterId(tunnel.getDestRouterId());
            tunnelInstancesBuilder.setBandWidth(tunnel.getBandWidth());

            tunnelBfdBuilder = new TunnelBfdBuilder();
            tunnelBfdBuilder.setBfdMultiplier(tunnel.getBfdSession().getMultiplier());
            tunnelBfdBuilder.setBfdrxInterval(tunnel.getBfdSession().getMinRecvTime());
            tunnelBfdBuilder.setBfdtxInterval(tunnel.getBfdSession().getMinSendTime());
            tunnelInstancesBuilder.setTunnelBfd(tunnelBfdBuilder.build());

            ExplicitPath mainPath = tunnel.getMasterPath();
            map = mainPath.getLabelMap();
            for (String key : map.keySet()) {
                mainPathBuilder = new MainPathBuilder();
                mainPathBuilder.setIndex(key);
                if (null != map.get(key).getAddressLocal()) {
                    mainPathBuilder.setIfAddress(map.get(key).getAddressLocal().getAddress());
                }
                if (null != map.get(key).getDevice()) {
                    mainPathBuilder.setRouterId(map.get(key).getDevice().getRouterId());
                    mainPathBuilder.setDeviceName(map.get(key).getDevice().getDeviceName());
                }
                mainPathBuilder.setAdjlabel(String.valueOf(map.get(key).getValue()));
                mainPathList.add(mainPathBuilder.build());
            }
            tunnelInstancesBuilder.setMainPath(mainPathList);
            ExplicitPath backPath = tunnel.getSlavePath();
            if (null != backPath) {
                map = backPath.getLabelMap();
                for (String key : map.keySet()) {
                    backPathBuilder = new BackPathBuilder();
                    backPathBuilder.setIndex(key);
                    if (null != map.get(key).getAddressLocal()) {
                        backPathBuilder.setIfAddress(map.get(key).getAddressLocal().getAddress());
                    }
                    if (null != map.get(key).getDevice()) {
                        backPathBuilder.setRouterId(map.get(key).getDevice().getRouterId());
                        backPathBuilder.setDeviceName(map.get(key).getDevice().getDeviceName());
                    }
                    backPathBuilder.setAdjlabel(String.valueOf(map.get(key).getValue()));
                    backPathList.add(backPathBuilder.build());
                }
                tunnelInstancesBuilder.setBackPath(backPathList);
            }
            tunnelInstancesList.add(tunnelInstancesBuilder.build());
        }
        return tunnelInstancesList;
    }

    @Override
    public Future<RpcResult<GetTunnelInstancesOutput>> getTunnelInstances(GetTunnelInstancesInput input) {
        GetTunnelInstancesOutputBuilder getTunnelInstancesOutputBuilder = new GetTunnelInstancesOutputBuilder();
        getTunnelInstancesOutputBuilder.setResult(CodeEnum.ERROR.getMessage());
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            getTunnelInstancesOutputBuilder.setResult("SystemStatus is not on.");
            return RpcResultBuilder.success(getTunnelInstancesOutputBuilder.build()).buildFuture();
        } else {
            this.getTunnelApi();
        }
        Map<String, Object> resultMap;
        if (input == null) {
            resultMap = this.tunnelApi.getAllTunnel(null, null);
        } else {
            resultMap = this.tunnelApi.getAllTunnel(input.getRouterId(), input.getTunnelName());
        }
        String code = (String) resultMap.get(ResponseEnum.CODE.getName());
        if (CodeEnum.SUCCESS.getName().equals(code)) {
            List<Tunnel> tunnelList = (List<Tunnel>) resultMap.get(ResponseEnum.BODY.getName());
            getTunnelInstancesOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
            getTunnelInstancesOutputBuilder.setTunnelInstances(getTunnel(tunnelList));
        } else {
            getTunnelInstancesOutputBuilder.setResult(CodeEnum.ERROR.getMessage());
        }
        return RpcResultBuilder.success(getTunnelInstancesOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<UpdateTunnelInstanceOutput>> updateTunnelInstance(UpdateTunnelInstanceInput input) {
        UpdateTunnelInstanceOutputBuilder updateTunnelInstanceOutputBuilder = new UpdateTunnelInstanceOutputBuilder();
        updateTunnelInstanceOutputBuilder.setResult(CodeEnum.ERROR.getMessage());
        LOG.info("updateTunnelInstance input : " + input);
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            updateTunnelInstanceOutputBuilder.setResult("SystemStatus is not on.");
            return RpcResultBuilder.success(updateTunnelInstanceOutputBuilder.build()).buildFuture();
        } else {
            this.getTunnelApi();
        }
        TunnelServiceEntity tunnelServiceEntity = new TunnelServiceEntity();
        tunnelServiceEntity.setTunnelId(input.getTunnelId());
        tunnelServiceEntity.setTunnelName(input.getTunnelName());
        tunnelServiceEntity.setEgressLSRId(input.getDestRouterId());
        tunnelServiceEntity.setBandwidth(input.getBandWidth());
        this.tunnelBfdBuild(tunnelServiceEntity, input);
        this.tunnelPathBuild(tunnelServiceEntity, input);
        LOG.info(tunnelServiceEntity.toString());
        tunnelServiceEntity.setRouterId(input.getSrcRouterId());
        Map<String, Object> resultMap = this.tunnelApi.createTunnel(tunnelServiceEntity);
        String code = (String) resultMap.get(ResponseEnum.CODE.getName());
        if (CodeEnum.SUCCESS.getName().equals(code)) {
            updateTunnelInstanceOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        } else {
            String message = (String) resultMap.get(ResponseEnum.MESSAGE.getName());
            updateTunnelInstanceOutputBuilder.setResult(message);
        }
        return RpcResultBuilder.success(updateTunnelInstanceOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<DeleteTunnelInstanceOutput>> deleteTunnelInstance(DeleteTunnelInstanceInput input) {
        DeleteTunnelInstanceOutputBuilder deleteTunnelInstanceOutputBuilder = new DeleteTunnelInstanceOutputBuilder();
        deleteTunnelInstanceOutputBuilder.setResult(CodeEnum.ERROR.getMessage());
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            deleteTunnelInstanceOutputBuilder.setResult("SystemStatus is not on.");
            return RpcResultBuilder.success(deleteTunnelInstanceOutputBuilder.build()).buildFuture();
        } else {
            this.getTunnelApi();
        }
        LOG.info("deleteTunnelInstance input : " + input);
        Map<String, Object> resultMap = this.tunnelApi.deleteTunnel(input.getRouterId(), input.getTunnelName());
        String code = (String) resultMap.get(ResponseEnum.CODE.getName());
        if (CodeEnum.SUCCESS.getName().equals(code)) {
            deleteTunnelInstanceOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        } else {
            String message = (String) resultMap.get(ResponseEnum.MESSAGE.getName());
            deleteTunnelInstanceOutputBuilder.setResult(message);
        }
        return RpcResultBuilder.success(deleteTunnelInstanceOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<PingTunnelInstanceOutput>> pingTunnelInstance(PingTunnelInstanceInput input) {
        PingTunnelInstanceOutputBuilder pingTunnelInstanceOutputBuilder = new PingTunnelInstanceOutputBuilder();
        Map<String, Object> pingResultMap = new HashMap<>();
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(pingTunnelInstanceOutputBuilder.build()).buildFuture();
        } else {
            this.getTunnelApi();
        }
        LOG.info("pingTunnelInstance input : " + input);
        if (input != null) {
            pingResultMap = this.tunnelApi.pingTunnel(input.getRouterId(), input.getTunnelName(), null);
        } else {
            pingTunnelInstanceOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        }
        if (pingResultMap.get(ResponseEnum.CODE.getName()) != CodeEnum.SUCCESS.getName()) {
            pingTunnelInstanceOutputBuilder.setResult(CodeEnum.ERROR.getMessage());
        } else {
            pingTunnelInstanceOutputBuilder.setResult((String) pingResultMap.get(ResponseEnum.MESSAGE.getName()));
        }
        return RpcResultBuilder.success(pingTunnelInstanceOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<TraceTunnelInstanceOutput>> traceTunnelInstance(TraceTunnelInstanceInput input) {
        TraceTunnelInstanceOutputBuilder traceTunnelInstanceOutputBuilder = new TraceTunnelInstanceOutputBuilder();
        Map<String, Object> traceResultMap = new HashMap<>();
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(traceTunnelInstanceOutputBuilder.build()).buildFuture();
        } else {
            this.getTunnelApi();
        }
        LOG.info("traceTunnelInstance input : " + input);
        if (input != null) {
            traceResultMap = this.tunnelApi.traceTunnel(input.getRouterId(), input.getTunnelName(), null);
        } else {

        }
        if (traceResultMap.get(ResponseEnum.CODE.getName()) != CodeEnum.SUCCESS.getName()) {
            traceTunnelInstanceOutputBuilder.setResult(CodeEnum.ERROR.getMessage());
        } else {
            traceTunnelInstanceOutputBuilder.setResult((String) traceResultMap.get(ResponseEnum.MESSAGE.getName()));
        }
        return RpcResultBuilder.success(traceTunnelInstanceOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<DetectTunnelPathOutput>> detectTunnelPath(DetectTunnelPathInput input) {
        DetectTunnelPathOutputBuilder detectTunnelPathOutputBuilder = new DetectTunnelPathOutputBuilder();
        Map<String, Object> detectResultMap = new HashMap<>();
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(detectTunnelPathOutputBuilder.build()).buildFuture();
        } else {
            this.getTunnelApi();
        }
        LOG.info("detectTunnelPath input : " + input);
        if (input != null) {
            detectResultMap = this.tunnelApi.detectTunnel(input.getRouterId(), input.getTunnelName(), input.getLspPath());
        } else {

        }
        if (detectResultMap.get(ResponseEnum.CODE.getName()) != CodeEnum.SUCCESS.getName()) {
            detectTunnelPathOutputBuilder.setResult(CodeEnum.ERROR.getMessage());
        } else {
            detectTunnelPathOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
            detectTunnelPathOutputBuilder.setTunnelName(input.getTunnelName());
            detectTunnelPathOutputBuilder.setPingResult(this.pingResultBuilder(detectResultMap).build());
            detectTunnelPathOutputBuilder.setTraceResult(this.traceResultBuilder(detectResultMap).build());
        }
        return RpcResultBuilder.success(detectTunnelPathOutputBuilder.build()).buildFuture();
    }

    private PingResultBuilder pingResultBuilder(Map<String, Object> map) {
        PingResultBuilder pingResultBuilder = new PingResultBuilder();
        DetectTunnelServiceEntity detectTunnelServiceEntity =
                (DetectTunnelServiceEntity) map.get(ResponseEnum.BODY.getName());
        pingResultBuilder.setPacketSend(detectTunnelServiceEntity.getPacketSend());
        pingResultBuilder.setPacketRecv(detectTunnelServiceEntity.getPacketRecv());
        pingResultBuilder.setLossRatio(detectTunnelServiceEntity.getLossRatio() + "%");
        return pingResultBuilder;
    }

    private TraceResultBuilder traceResultBuilder(Map<String, Object> map) {
        TraceResultBuilder traceResultBuilder = new TraceResultBuilder();
        DetectTunnelServiceEntity detectTunnelServiceEntity =
                (DetectTunnelServiceEntity) map.get(ResponseEnum.BODY.getName());
        traceResultBuilder.setStatus(detectTunnelServiceEntity.getStatus());
        traceResultBuilder.setErrorType(detectTunnelServiceEntity.getErrorType());
        List<PathInfo> pathInfoList = new ArrayList<>();
        for (TunnelHopServiceEntity tunnelHopServiceEntity : detectTunnelServiceEntity.getTunnelHopServiceEntityList()) {
            PathInfoBuilder pathInfoBuilder = new PathInfoBuilder();
            pathInfoBuilder.setIfAddress(tunnelHopServiceEntity.getIfAddress());
            pathInfoBuilder.setIndex(tunnelHopServiceEntity.getIndex());
            pathInfoList.add(pathInfoBuilder.build());
        }
        traceResultBuilder.setPathInfo(pathInfoList);
        return traceResultBuilder;
    }

    private void tunnelBfdBuild(TunnelServiceEntity tunnelServiceEntity, UpdateTunnelInstanceInput input) {
        LOG.info("tunnelBfdBuild begin");
        TunnelBfd tunnelBfd = input.getTunnelBfd();
        if (tunnelBfd != null) {
            tunnelServiceEntity.setBfdMultiplier(tunnelBfd.getBfdMultiplier());
            tunnelServiceEntity.setBfdrxInterval(tunnelBfd.getBfdrxInterval());
            tunnelServiceEntity.setBfdtxInterval(tunnelBfd.getBfdtxInterval());
        }
        LOG.info("tunnelBfdBuild end");
        return;
    }

    private void tunnelPathBuild(TunnelServiceEntity tunnelServiceEntity, UpdateTunnelInstanceInput input) {
        LOG.info("tunnelPathBuild begin");
        List<MainPath> mainPathList = input.getMainPath();
        Iterator<MainPath> mainPathIterator = mainPathList.iterator();
        while (mainPathIterator.hasNext()) {
            TunnelHopServiceEntity tunnelHopServiceEntity = new TunnelHopServiceEntity();
            MainPath mainPath = mainPathIterator.next();
            tunnelHopServiceEntity.setIndex(mainPath.getIndex());
            tunnelHopServiceEntity.setDeviceName(mainPath.getDeviceName());
            tunnelHopServiceEntity.setRouterId(mainPath.getRouterId());
            tunnelHopServiceEntity.setAdjlabel(mainPath.getAdjlabel());
            tunnelHopServiceEntity.setIfAddress(mainPath.getIfAddress());
            tunnelServiceEntity.addMainPathHop(tunnelHopServiceEntity);
        }
        List<BackPath> backPathList = input.getBackPath();
        if (null == backPathList || 0 == backPathList.size()) {
            LOG.info("this tunnel does not have back path.");
        } else {
            Iterator<BackPath> backPathIterator = backPathList.iterator();
            while (backPathIterator.hasNext()) {
                TunnelHopServiceEntity tunnelHopServiceEntity = new TunnelHopServiceEntity();
                BackPath backPath = backPathIterator.next();
                tunnelHopServiceEntity.setIndex(backPath.getIndex());
                tunnelHopServiceEntity.setDeviceName(backPath.getDeviceName());
                tunnelHopServiceEntity.setRouterId(backPath.getRouterId());
                tunnelHopServiceEntity.setAdjlabel(backPath.getAdjlabel());
                tunnelHopServiceEntity.setIfAddress(backPath.getIfAddress());
                tunnelServiceEntity.addBackPathHop(tunnelHopServiceEntity);
            }
        }
        LOG.info("tunnelPathBuild end");
    }
}
