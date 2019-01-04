package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.TunnelApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.service.entity.TunnelHopServiceEntity;
import cn.org.upbnc.service.entity.TunnelServiceEntity;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnel.rev181227.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnel.rev181227.gettunnelinstances.output.TunnelInstances;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnel.rev181227.gettunnelinstances.output.TunnelInstancesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnel.rev181227.tunnelinstance.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnel.rev181227.tunnelinstance.TunnelBfd;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    List<TunnelInstances> testTuunel() {
        List<TunnelInstances> tunnelInstancesList = new ArrayList<>();
        List<MainPath> mainPathList = new ArrayList<>();
        List<BackPath> backPathList = new ArrayList<>();
        TunnelInstancesBuilder tunnelInstancesBuilder = new TunnelInstancesBuilder();
        tunnelInstancesBuilder.setSrcDevice("R1");
        tunnelInstancesBuilder.setSrcRouterId("1.1.1.1");
        tunnelInstancesBuilder.setTunnelId("100");
        tunnelInstancesBuilder.setDestDevice("R2");
        tunnelInstancesBuilder.setDestRouterId("2.2.2.2");
        tunnelInstancesBuilder.setBandWidth("200");
        TunnelBfdBuilder tunnelBfdBuilder = new TunnelBfdBuilder();
        tunnelBfdBuilder.setBfdMultiplier("5");
        tunnelBfdBuilder.setBfdrxInterval("10");
        tunnelBfdBuilder.setBfdtxInterval("10");
        tunnelInstancesBuilder.setTunnelBfd(tunnelBfdBuilder.build());

        MainPathBuilder mainPathBuilder = new MainPathBuilder();
        mainPathBuilder.setIndex("0");
        mainPathBuilder.setIfAddress("12.1.2.2");
        mainPathBuilder.setRouterId("2.2.2.2");
        mainPathBuilder.setDeviceName("R2");
        mainPathBuilder.setAdjlabel("322006");
        mainPathList.add(mainPathBuilder.build());

        BackPathBuilder backPathBuilder = new BackPathBuilder();
        backPathBuilder.setIndex("0");
        backPathBuilder.setIfAddress("13.1.1.2");
        backPathBuilder.setRouterId("3.3.3.3");
        backPathBuilder.setDeviceName("R3");
        backPathBuilder.setAdjlabel("322002");
        backPathList.add(backPathBuilder.build());
        backPathBuilder = new BackPathBuilder();
        backPathBuilder.setIndex("1");
        backPathBuilder.setIfAddress("4.4.4.4");
        backPathBuilder.setRouterId("4.4.4.4");
        backPathBuilder.setDeviceName("R4");
        backPathBuilder.setAdjlabel("230040");
        backPathList.add(backPathBuilder.build());
        backPathBuilder = new BackPathBuilder();
        backPathBuilder.setIndex("2");
        backPathBuilder.setIfAddress("24.1.1.1");
        backPathBuilder.setRouterId("2.2.2.2");
        backPathBuilder.setDeviceName("R2");
        backPathBuilder.setAdjlabel("322001");
        backPathList.add(backPathBuilder.build());

        tunnelInstancesBuilder.setMainPath(mainPathList);
        tunnelInstancesBuilder.setBackPath(backPathList);
        tunnelInstancesList.add(tunnelInstancesBuilder.build());
//tunnel2---------------------------------------------------------------------
        tunnelInstancesBuilder = new TunnelInstancesBuilder();
        tunnelInstancesBuilder.setSrcDevice("R3");
        tunnelInstancesBuilder.setSrcRouterId("3.3.3.3");
        tunnelInstancesBuilder.setTunnelId("300");
        tunnelInstancesBuilder.setDestDevice("R1");
        tunnelInstancesBuilder.setDestRouterId("1.1.1.1");
        tunnelInstancesBuilder.setBandWidth("200");
        tunnelInstancesList.add(tunnelInstancesBuilder.build());
//-----------------------------------------------------------------------------------
        return tunnelInstancesList;
    }

    @Override
    public Future<RpcResult<GetTunnelInstancesOutput>> getTunnelInstances(GetTunnelInstancesInput input) {
        GetTunnelInstancesOutputBuilder getTunnelInstancesOutputBuilder = new GetTunnelInstancesOutputBuilder();

        getTunnelInstancesOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        getTunnelInstancesOutputBuilder.setTunnelInstances(testTuunel());
        return RpcResultBuilder.success(getTunnelInstancesOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<UpdateTunnelInstanceOutput>> updateTunnelInstance(UpdateTunnelInstanceInput input) {
        UpdateTunnelInstanceOutputBuilder updateTunnelInstanceOutputBuilder = new UpdateTunnelInstanceOutputBuilder();
        LOG.info("updateTunnelInstance input : " + input);
        if (SystemStatusEnum.ON != this.session.getStatus()) {
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
        this.tunnelApi.createTunnel(tunnelServiceEntity);
        updateTunnelInstanceOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        return RpcResultBuilder.success(updateTunnelInstanceOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<DeleteTunnelInstanceOutput>> deleteTunnelInstance(DeleteTunnelInstanceInput input) {
        DeleteTunnelInstanceOutputBuilder deleteTunnelInstanceOutputBuilder = new DeleteTunnelInstanceOutputBuilder();
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(deleteTunnelInstanceOutputBuilder.build()).buildFuture();
        } else {
            this.getTunnelApi();
        }
        LOG.info("deleteTunnelInstance input : " + input);
        this.tunnelApi.deleteTunnel(input.getRouterId(), input.getTunnelName());
        deleteTunnelInstanceOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        return RpcResultBuilder.success(deleteTunnelInstanceOutputBuilder.build()).buildFuture();
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
        LOG.info("tunnelPathBuild end");
        return;
    }
}
