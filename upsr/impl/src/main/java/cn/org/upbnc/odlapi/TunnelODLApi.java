package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.TunnelApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.enumtype.*;
import cn.org.upbnc.service.entity.*;
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
        Map<String, Label> map;
        // get bfd static start
        MainLspBfdBuilder mainLspBfdBuilder;
        TunnelBfdBuilder tunnelStaticBfdBuilder;
        //TunnelStaticBfdBuilder tunnelStaticBfdBuilder;

        // get bfd end
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

            if(!tunnel.getBandWidth().equals("0")) {
                tunnelInstancesBuilder.setBandWidth(tunnel.getBandWidth());
            }

            if(tunnel.getServiceClass() != null) {
                tunnelInstancesBuilder.setServiceClass(tunnel.getServiceClass().getString());
            }

            // get bfd
            // set bfd type
            String bfdType = BfdTypeEnum.valueOf(tunnel.getBfdType()).getUi();
            tunnelInstancesBuilder.setBfdType(bfdType);

            // set dynamic bfd
            if(tunnel.getBfdType() == BfdTypeEnum.Dynamic.getCode()) {
                tunnelBfdBuilder = new TunnelBfdBuilder();
                tunnelBfdBuilder.setBfdMultiplier(tunnel.getDynamicBfd().getMultiplier());
                tunnelBfdBuilder.setBfdrxInterval(tunnel.getDynamicBfd().getMinRecvTime());
                tunnelBfdBuilder.setBfdtxInterval(tunnel.getDynamicBfd().getMinSendTime());
                tunnelInstancesBuilder.setTunnelBfd(tunnelBfdBuilder.build());

            }else if(tunnel.getBfdType() == BfdTypeEnum.Static.getCode()) {// set static bfd
                // set tunnel bfd
                if(tunnel.getTunnelBfd() != null){
                    BfdSession bfdSession = tunnel.getTunnelBfd();
//                    tunnelStaticBfdBuilder = new TunnelStaticBfdBuilder();
//                    tunnelStaticBfdBuilder.setBfdMultiplier(bfdSession.getMultiplier());
//                    tunnelStaticBfdBuilder.setBfdrxInterval(bfdSession.getMinRecvTime());
//                    tunnelStaticBfdBuilder.setBfdtxInterval(bfdSession.getMinSendTime());
//                    tunnelStaticBfdBuilder.setLocalDiscriminator(bfdSession.getDiscriminatorLocal());
//                    tunnelStaticBfdBuilder.setRemoteDiscriminator(bfdSession.getDiscriminatorRemote());
//
//                    tunnelInstancesBuilder.setTunnelStaticBfd(tunnelStaticBfdBuilder.build());

                    tunnelStaticBfdBuilder = new TunnelBfdBuilder();
                    tunnelStaticBfdBuilder.setBfdMultiplier(bfdSession.getMultiplier());
                    tunnelStaticBfdBuilder.setBfdrxInterval(bfdSession.getMinRecvTime());
                    tunnelStaticBfdBuilder.setBfdtxInterval(bfdSession.getMinSendTime());
                    tunnelStaticBfdBuilder.setLocalDiscriminator(bfdSession.getDiscriminatorLocal());
                    tunnelStaticBfdBuilder.setRemoteDiscriminator(bfdSession.getDiscriminatorRemote());

                    tunnelInstancesBuilder.setTunnelBfd(tunnelStaticBfdBuilder.build());
                }


                // set master bfd
                if(tunnel.getMasterBfd() != null) {
                    BfdSession bfdSession = tunnel.getMasterBfd();
                    mainLspBfdBuilder = new MainLspBfdBuilder();
                    mainLspBfdBuilder.setBfdMultiplier(bfdSession.getMultiplier());
                    mainLspBfdBuilder.setBfdrxInterval(bfdSession.getMinRecvTime());
                    mainLspBfdBuilder.setBfdtxInterval(bfdSession.getMinSendTime());
                    mainLspBfdBuilder.setLocalDiscriminator(bfdSession.getDiscriminatorLocal());
                    mainLspBfdBuilder.setRemoteDiscriminator(bfdSession.getDiscriminatorRemote());

                    tunnelInstancesBuilder.setMainLspBfd(mainLspBfdBuilder.build());
                }
            }


            //
            ExplicitPath mainPath = tunnel.getMasterPath();
            if (mainPath != null) {
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
            }
            tunnelInstancesBuilder.setBackPath(backPathList);
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
        String tunnelName = input.getTunnelName();
        String partOne = tunnelName.substring(0, 1).toUpperCase();
        String partTwo = tunnelName.substring(1);
        tunnelName = partOne + partTwo;
        tunnelServiceEntity.setTunnelName(tunnelName);
        tunnelServiceEntity.setDestRouterId(input.getDestRouterId());
        tunnelServiceEntity.setBandwidth(input.getBandWidth());

        // set service class
        tunnelServiceEntity.setTunnelServiceClassEntity(this.getTSCEByODL(input.getServiceClass()));

        //add tunnel bfd and master bfd
        tunnelServiceEntity.setBfdType(this.getBfdTypeByInput(input));

        if(tunnelServiceEntity.getBfdType() == BfdTypeEnum.Static.getCode()) {
            tunnelServiceEntity.setTunnelBfd(this.getTunnelBfdByInput(input, tunnelServiceEntity.getTunnelName()));
            tunnelServiceEntity.setMasterBfd(this.getMasterBfdByInput(input, tunnelServiceEntity.getTunnelName()));
        }else if(tunnelServiceEntity.getBfdType() == BfdTypeEnum.Dynamic.getCode()) {
            tunnelServiceEntity.setDynamicBfd(this.getDynamicBfdByInput(input, tunnelServiceEntity.getTunnelName()));
        }
        //end

        //this.tunnelBfdBuild(tunnelServiceEntity, input);
        String buildPathRet = this.tunnelPathBuild(tunnelServiceEntity, input);
        if (buildPathRet != null) {
            updateTunnelInstanceOutputBuilder.setResult(buildPathRet);
            return RpcResultBuilder.success(updateTunnelInstanceOutputBuilder.build()).buildFuture();
        }
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
        String tunnelName = input.getTunnelName();
        String partOne = tunnelName.substring(0, 1).toUpperCase();
        String partTwo = tunnelName.substring(1);
        tunnelName = partOne + partTwo;
        Map<String, Object> resultMap = this.tunnelApi.deleteTunnel(input.getRouterId(), tunnelName);
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

    @Override
    public Future<RpcResult<GenerateTunnelNameOutput>> generateTunnelName(GenerateTunnelNameInput input) {
        Map<String, Object> generateResultMap;
        GenerateTunnelNameOutputBuilder generateTunnelNameOutputBuilder = new GenerateTunnelNameOutputBuilder();
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(generateTunnelNameOutputBuilder.build()).buildFuture();
        } else {
            this.getTunnelApi();
        }
        generateResultMap = this.tunnelApi.generateTunnelName(input.getRouterId());
        long tunnelId = (long)generateResultMap.get(ResponseEnum.BODY.getName());
        LOG.info("generateTunnelName : " + tunnelId);
        generateTunnelNameOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        generateTunnelNameOutputBuilder.setTunnelId("" + tunnelId);
        generateTunnelNameOutputBuilder.setTunnelName("Tunnel" + tunnelId);
        return RpcResultBuilder.success(generateTunnelNameOutputBuilder.build()).buildFuture();
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

//    private void tunnelBfdBuild(TunnelServiceEntity tunnelServiceEntity, UpdateTunnelInstanceInput input) {
//        LOG.info("tunnelBfdBuild begin");
//        TunnelBfd tunnelBfd = input.getTunnelBfd();
//        if (tunnelBfd != null) {
//            tunnelServiceEntity.setBfdMultiplier(tunnelBfd.getBfdMultiplier());
//            tunnelServiceEntity.setBfdrxInterval(tunnelBfd.getBfdrxInterval());
//            tunnelServiceEntity.setBfdtxInterval(tunnelBfd.getBfdtxInterval());
//        }
//        LOG.info("tunnelBfdBuild end");
//        return;
//    }
    private Integer getBfdTypeByInput(UpdateTunnelInstanceInput input){
        if (BfdTypeEnum.Dynamic.getUi().equals(input.getBfdType())) {
            return new Integer(BfdTypeEnum.Dynamic.getCode());
        } else if (BfdTypeEnum.Static.getUi().equals(input.getBfdType())) {
            return new Integer(BfdTypeEnum.Static.getCode());
        } else {
            return new Integer(BfdTypeEnum.Empty.getCode());
        }
    }
    private BfdServiceEntity getTunnelBfdByInput(UpdateTunnelInstanceInput input,String tunnelName){
        BfdServiceEntity ret = null;
        //TunnelStaticBfd tunnelBfd = input.getTunnelStaticBfd();
        TunnelBfd tunnelBfd = input.getTunnelBfd();

        if("".equals(tunnelBfd.getBfdMultiplier())
                && "".equals(tunnelBfd.getBfdrxInterval())
                && "".equals(tunnelBfd.getBfdtxInterval())
                && "".equals(tunnelBfd.getLocalDiscriminator())
                && "".equals(tunnelBfd.getRemoteDiscriminator())){
            return null;
        }

        if(tunnelBfd != null) {
            ret = new BfdServiceEntity();
            ret.setType(BfdTypeEnum.Tunnel.getCode());
            ret.setDiscriminatorLocal(tunnelBfd.getLocalDiscriminator());
            ret.setDiscriminatorRemote(tunnelBfd.getRemoteDiscriminator());
            ret.setMinRecvTime(tunnelBfd.getBfdrxInterval());
            ret.setMinSendTime(tunnelBfd.getBfdtxInterval());
            ret.setMultiplier(tunnelBfd.getBfdMultiplier());
            ret.setBfdName(tunnelName+"_"+BfdTypeEnum.Tunnel.getName());
        }
        return ret;
    }

    private BfdServiceEntity getMasterBfdByInput(UpdateTunnelInstanceInput input,String tunnelName){
        BfdServiceEntity ret = null;

        MainLspBfd mainLspBfd = input.getMainLspBfd();

        if("".equals(mainLspBfd.getBfdMultiplier())
                && "".equals(mainLspBfd.getBfdrxInterval())
                && "".equals(mainLspBfd.getBfdtxInterval())
                && "".equals(mainLspBfd.getLocalDiscriminator())
                && "".equals(mainLspBfd.getRemoteDiscriminator())){
            return null;
        }

        if(mainLspBfd != null) {
            ret = new BfdServiceEntity();
            ret.setType(BfdTypeEnum.Master.getCode());
            ret.setDiscriminatorLocal(mainLspBfd.getLocalDiscriminator());
            ret.setDiscriminatorRemote(mainLspBfd.getRemoteDiscriminator());
            ret.setMinRecvTime(mainLspBfd.getBfdrxInterval());
            ret.setMinSendTime(mainLspBfd.getBfdtxInterval());
            ret.setMultiplier(mainLspBfd.getBfdMultiplier());
            ret.setBfdName(tunnelName+"_"+BfdTypeEnum.Master.getName());
        }
        return ret;
    }

    private BfdServiceEntity getDynamicBfdByInput(UpdateTunnelInstanceInput input,String tunnelName){
        BfdServiceEntity ret = null;

        TunnelBfd tunnelBfd = input.getTunnelBfd();
        if(tunnelBfd != null) {
            ret = new BfdServiceEntity();
            ret.setType(BfdTypeEnum.Dynamic.getCode());
            ret.setDiscriminatorLocal(tunnelBfd.getLocalDiscriminator());
            ret.setDiscriminatorRemote(tunnelBfd.getRemoteDiscriminator());
            ret.setMinRecvTime(tunnelBfd.getBfdrxInterval());
            ret.setMinSendTime(tunnelBfd.getBfdtxInterval());
            ret.setMultiplier(tunnelBfd.getBfdMultiplier());
            ret.setBfdName(tunnelName+"_"+BfdTypeEnum.Dynamic.getName());
        }
        return ret;
    }


    private String tunnelPathBuild(TunnelServiceEntity tunnelServiceEntity, UpdateTunnelInstanceInput input) {
        LOG.info("tunnelPathBuild begin");
        List<MainPath> mainPathList = input.getMainPath();
        Iterator<MainPath> mainPathIterator = mainPathList.iterator();
        while (mainPathIterator.hasNext()) {
            TunnelHopServiceEntity tunnelHopServiceEntity = new TunnelHopServiceEntity();
            MainPath mainPath = mainPathIterator.next();
            if ("".equals(mainPath.getAdjlabel()) || "".equals(mainPath.getIfAddress())) {
                return "Main path is invalid at the node " + mainPath.getDeviceName() + ".";
            }
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
                if ("".equals(backPath.getAdjlabel()) || "".equals(backPath.getIfAddress())) {
                    return "Backup path is invalid at the node " + backPath.getDeviceName() + ".";
                }
                tunnelHopServiceEntity.setIndex(backPath.getIndex());
                tunnelHopServiceEntity.setDeviceName(backPath.getDeviceName());
                tunnelHopServiceEntity.setRouterId(backPath.getRouterId());
                tunnelHopServiceEntity.setAdjlabel(backPath.getAdjlabel());
                tunnelHopServiceEntity.setIfAddress(backPath.getIfAddress());
                tunnelServiceEntity.addBackPathHop(tunnelHopServiceEntity);
            }
        }
        LOG.info("tunnelPathBuild end");
        return null;
    }

    private TunnelServiceClassEntity getTSCEByODL(String odlTsce){
        TunnelServiceClassEntity ret = null;

        if(null != odlTsce && !odlTsce.equals("")){
            ret = new TunnelServiceClassEntity();
            ret.setDef(odlTsce.contains(TunnelServiceClassEnum.DEF.getName()));
            ret.setAf1(odlTsce.contains(TunnelServiceClassEnum.AF1.getName()));
            ret.setAf2(odlTsce.contains(TunnelServiceClassEnum.AF2.getName()));
            ret.setAf3(odlTsce.contains(TunnelServiceClassEnum.AF3.getName()));
            ret.setAf4(odlTsce.contains(TunnelServiceClassEnum.AF4.getName()));
            ret.setEf(odlTsce.contains(TunnelServiceClassEnum.EF.getName()));
            ret.setBe(odlTsce.contains(TunnelServiceClassEnum.BE.getName()));
            ret.setCs6(odlTsce.contains(TunnelServiceClassEnum.CS6.getName()));
            ret.setCs6(odlTsce.contains(TunnelServiceClassEnum.CS7.getName()));
        }

        return ret;
    }

}
