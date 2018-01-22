package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.TunnelPolicyApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.entity.TunnelPolicy.TunnelPolicy;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.enumtype.TnlPolicyTypeEnum;
import cn.org.upbnc.service.entity.TunnelPolicy.TnlSelSeqEntity;
import cn.org.upbnc.service.entity.TunnelPolicy.TpNexthopEntity;
import cn.org.upbnc.service.entity.TunnelPolicy.TunnelPolicyEntity;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.gettunnelpolicys.output.TunnelPolicys;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.gettunnelpolicys.output.TunnelPolicysBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.tunnelpolicy.TnlSelSeqs;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.tunnelpolicy.TnlSelSeqsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.tunnelpolicy.TpNexthops;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.tunnelpolicy.TpNexthopsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.tunnelpolicy.tpnexthops.BindTunnel;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.tunnelpolicy.tpnexthops.BindTunnelBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Future;

public class TunnelPolicyODLApi implements UpsrTunnelPolicyService {
    private static final Logger LOG = LoggerFactory.getLogger(TunnelPolicyODLApi.class);
    Session session;
    TunnelPolicyApi tunnelPolicyApi;

    public TunnelPolicyODLApi(Session session) {
        this.session = session;
    }

    private TunnelPolicyApi getTunnelPolicyApi() {
        if (this.tunnelPolicyApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                tunnelPolicyApi = apiInterface.getTunnelPolicyApi();
            }
        }
        return this.tunnelPolicyApi;
    }

    @Override
    public Future<RpcResult<GetTunnelPolicysOutput>> getTunnelPolicys(GetTunnelPolicysInput input) {
        GetTunnelPolicysOutputBuilder getTunnelPolicysOutputBuilder = new GetTunnelPolicysOutputBuilder();

        Map<String, Object> resultMap = new HashMap<>();
        // 判断系统是否准备完毕：
        // 系统状态，未准备完毕返回失败
        // 系统状态，准备成功调用API
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            getTunnelPolicysOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(getTunnelPolicysOutputBuilder.build()).buildFuture();
        }
        resultMap = getTunnelPolicyApi().getTunnelPolicyMap(input.getRouterId());
        List<TunnelPolicyEntity> tunnelPolicyEntityList =
                (List<TunnelPolicyEntity>) resultMap.get(ResponseEnum.BODY.getName());
        if (null == tunnelPolicyEntityList) {
            getTunnelPolicysOutputBuilder.setResult("Failed:" + resultMap.get(ResponseEnum.MESSAGE.getName()));
            return RpcResultBuilder.success(getTunnelPolicysOutputBuilder.build()).buildFuture();
        }
        List<TunnelPolicys> tunnelPolicysList = new ArrayList<>();
        for (TunnelPolicyEntity tunnelPolicyEntity : tunnelPolicyEntityList) {
            TunnelPolicysBuilder tunnelPolicysBuilder = new TunnelPolicysBuilder();
//            tunnelPolicysBuilder.setRouterId(tunnelPolicyEntity.getRouterID());
            tunnelPolicysBuilder.setPolicyName(tunnelPolicyEntity.getTnlPolicyName());
            tunnelPolicysBuilder.setDescription(tunnelPolicyEntity.getDescription());
            //tunnelPolicysBuilder.setTnlPolicyType(tunnelPolicyEntity.getTnlPolicyType());

            if(tunnelPolicyEntity.getTnlPolicyType() == TnlPolicyTypeEnum.TnlBinding.getCode()){
                tunnelPolicysBuilder.setTnlPolicyType(TnlPolicyTypeEnum.TnlBinding.getName());
                //TpNexthops list解析
                List<TpNexthops> tpNexthopsList = new ArrayList<>();

                for(TpNexthopEntity tpNexthopEntity:tunnelPolicyEntity.getTpNexthopEntities()){
                    TpNexthopsBuilder tpNexthopsBuilder=new TpNexthopsBuilder();
                    tpNexthopsBuilder.setDestAddr(tpNexthopEntity.getNexthopIPaddr());
                    tpNexthopsBuilder.setDownSwitch(tpNexthopEntity.isDownSwitch()?"true":"false");
                    tpNexthopsBuilder.setIgnoreDestCheck(tpNexthopEntity.isIgnoreDestCheck()?"true":"false");
                    tpNexthopsBuilder.setIncludeLdp(tpNexthopEntity.isIncludeLdp()?"true":"false");
                    //绑定隧道名称list解析
                    List<BindTunnel> bindTunnelList=new ArrayList<BindTunnel>();

                    for(String bindTunnelName:tpNexthopEntity.getTpTunnels()){
                        BindTunnelBuilder bindTunnelBuilder=new BindTunnelBuilder();
                        bindTunnelBuilder.setTunnelName(bindTunnelName);
                        bindTunnelList.add(bindTunnelBuilder.build());
                    }
                    tpNexthopsBuilder.setBindTunnel(bindTunnelList);
                    tpNexthopsList.add(tpNexthopsBuilder.build());
                }
                tunnelPolicysBuilder.setTpNexthops(tpNexthopsList);

            }else if(tunnelPolicyEntity.getTnlPolicyType() == TnlPolicyTypeEnum.TnlSelectSeq.getCode()){
                tunnelPolicysBuilder.setTnlPolicyType(TnlPolicyTypeEnum.TnlSelectSeq.getName());
                List<TnlSelSeqs> tnlSelSeqsList = new ArrayList<>();

                for(TnlSelSeqEntity tnlSelSeqEntity:tunnelPolicyEntity.getTnlSelSeqlEntities()){
                    TnlSelSeqsBuilder tnlSelSeqsBuilder=new TnlSelSeqsBuilder();
                    tnlSelSeqsBuilder.setLoadBalanceNum(String.valueOf(tnlSelSeqEntity.getLoadBalanceNum()));
                    tnlSelSeqsBuilder.setSelTnlType1(tnlSelSeqEntity.getSelTnlType1());
                    tnlSelSeqsBuilder.setSelTnlType2(tnlSelSeqEntity.getSelTnlType2());
                    tnlSelSeqsBuilder.setSelTnlType3(tnlSelSeqEntity.getSelTnlType3());
                    tnlSelSeqsBuilder.setSelTnlType4(tnlSelSeqEntity.getSelTnlType4());
                    tnlSelSeqsBuilder.setSelTnlType5(tnlSelSeqEntity.getSelTnlType5());
                    tnlSelSeqsBuilder.setUnmix(tnlSelSeqEntity.isUnmix()?"true":"false");
                }
                tunnelPolicysBuilder.setTnlSelSeqs(tnlSelSeqsList);
            }else if(tunnelPolicyEntity.getTnlPolicyType() == TnlPolicyTypeEnum.Invalid.getCode()){
                tunnelPolicysBuilder.setTnlPolicyType(TnlPolicyTypeEnum.Invalid.getName());
            }


//            if(tunnelPolicysBuilder.getTnlPolicyType().equals("invalid")){
//                tunnelPolicysList.add(tunnelPolicysBuilder.build());
//                break;
//            }else if(tunnelPolicysBuilder.getTnlPolicyType().equals("tnlBinding")){
//                //TpNexthops list解析
//                List<TpNexthops> tpNexthopsList = new ArrayList<>();
//
//                for(TpNexthopEntity tpNexthopEntity:tunnelPolicyEntity.getTpNexthopEntities()){
//                    TpNexthopsBuilder tpNexthopsBuilder=new TpNexthopsBuilder();
//                    tpNexthopsBuilder.setDestAddr(tpNexthopEntity.getNexthopIPaddr());
//                    tpNexthopsBuilder.setDownSwitch(tpNexthopEntity.isDownSwitch()?"true":"false");
//                    tpNexthopsBuilder.setIgnoreDestCheck(tpNexthopEntity.isIgnoreDestCheck()?"true":"false");
//                    tpNexthopsBuilder.setIncludeLdp(tpNexthopEntity.isIncludeLdp()?"true":"false");
//                    //绑定隧道名称list解析
//                    List<BindTunnel> bindTunnelList=new ArrayList<BindTunnel>();
//
//                    for(String bindTunnelName:tpNexthopEntity.getTpTunnels()){
//                        BindTunnelBuilder bindTunnelBuilder=new BindTunnelBuilder();
//                        bindTunnelBuilder.setTunnelName(bindTunnelName);
//                        bindTunnelList.add(bindTunnelBuilder.build());
//                    }
//                    tpNexthopsBuilder.setBindTunnel(bindTunnelList);
//                    tpNexthopsList.add(tpNexthopsBuilder.build());
//                }
//                tunnelPolicysBuilder.setTpNexthops(tpNexthopsList);
//            }else if(tunnelPolicysBuilder.getTnlPolicyType().equals("tnlSelectSeq")){
//                List<TnlSelSeqs> tnlSelSeqsList = new ArrayList<>();
//
//                for(TnlSelSeqEntity tnlSelSeqEntity:tunnelPolicyEntity.getTnlSelSeqlEntities()){
//                    TnlSelSeqsBuilder tnlSelSeqsBuilder=new TnlSelSeqsBuilder();
//                    tnlSelSeqsBuilder.setLoadBalanceNum(String.valueOf(tnlSelSeqEntity.getLoadBalanceNum()));
//                    tnlSelSeqsBuilder.setSelTnlType1(tnlSelSeqEntity.getSelTnlType1());
//                    tnlSelSeqsBuilder.setSelTnlType2(tnlSelSeqEntity.getSelTnlType2());
//                    tnlSelSeqsBuilder.setSelTnlType3(tnlSelSeqEntity.getSelTnlType3());
//                    tnlSelSeqsBuilder.setSelTnlType4(tnlSelSeqEntity.getSelTnlType4());
//                    tnlSelSeqsBuilder.setSelTnlType5(tnlSelSeqEntity.getSelTnlType5());
//                    tnlSelSeqsBuilder.setUnmix(tnlSelSeqEntity.isUnmix()?"true":"false");
//                }
//                tunnelPolicysBuilder.setTnlSelSeqs(tnlSelSeqsList);
//            }

            tunnelPolicysList.add(tunnelPolicysBuilder.build());
        }
        getTunnelPolicysOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        getTunnelPolicysOutputBuilder.setTunnelPolicys(tunnelPolicysList);
        return RpcResultBuilder.success(getTunnelPolicysOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<UpdateTunnelPolicyOutput>> updateTunnelPolicy(UpdateTunnelPolicyInput input) {
        UpdateTunnelPolicyOutputBuilder updateTunnelPolicyOutputBuilder = new UpdateTunnelPolicyOutputBuilder();
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            updateTunnelPolicyOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(updateTunnelPolicyOutputBuilder.build()).buildFuture();
        }
        return RpcResultBuilder.success(updateTunnelPolicyOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<DeleteTunnelPolicyOutput>> deleteTunnelPolicy(DeleteTunnelPolicyInput input) {
        DeleteTunnelPolicyOutputBuilder deleteTunnelPolicyOutputBuilder = new DeleteTunnelPolicyOutputBuilder();
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            deleteTunnelPolicyOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(deleteTunnelPolicyOutputBuilder.build()).buildFuture();
        }
        return RpcResultBuilder.success(deleteTunnelPolicyOutputBuilder.build()).buildFuture();
    }
}
