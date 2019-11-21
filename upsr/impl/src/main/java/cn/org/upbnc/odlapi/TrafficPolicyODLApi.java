package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.TrafficPolicyApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.service.entity.TrafficPolicy.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.aclinfo.AclEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.aclinfo.AclEntriesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.aclinfo.aclentries.Rules;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.aclinfo.aclentries.RulesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.getacl.output.AclRouters;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.getacl.output.AclRoutersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.getifpolicy.output.IfPolicyRouters;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.getifpolicy.output.IfPolicyRoutersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.gettrafficbehave.output.TrafficBehaveRouters;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.gettrafficbehave.output.TrafficBehaveRoutersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.gettrafficclass.output.TrafficClassRouters;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.gettrafficclass.output.TrafficClassRoutersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.gettrafficpolicy.output.TrafficPolicyRouters;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.gettrafficpolicy.output.TrafficPolicyRoutersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.ifpolicyinfo.IfPolicyEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.ifpolicyinfo.IfPolicyEntriesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.trafficbehaveinfo.TrafficBehaveEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.trafficbehaveinfo.TrafficBehaveEntriesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.trafficclassinfo.TrafficClassEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.trafficclassinfo.TrafficClassEntriesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.trafficclassinfo.trafficclassentries.Matches;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.trafficclassinfo.trafficclassentries.MatchesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.trafficpolicyinfo.TrafficPolicyEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.trafficpolicyinfo.TrafficPolicyEntriesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.trafficpolicyinfo.trafficpolicyentries.Policy;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.trafficpolicyinfo.trafficpolicyentries.PolicyBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class TrafficPolicyODLApi implements UpsrTrafficPolicyService {
    private static final Logger LOG = LoggerFactory.getLogger(TrafficPolicyODLApi.class);
    Session session;
    TrafficPolicyApi trafficPolicyApi;

    public TrafficPolicyODLApi(Session session) {
        this.session = session;
    }

    private TrafficPolicyApi getTrafficPolicyApi() {
        if (this.trafficPolicyApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                trafficPolicyApi = apiInterface.getTrafficPolicyApi();
            }
        }
        return this.trafficPolicyApi;
    }
    //acl
    @Override
    public Future<RpcResult<GetAclOutput>> getAcl(GetAclInput input) {
        LOG.info("getAcl begin");
        GetAclOutputBuilder getAclOutputBuilder = new GetAclOutputBuilder();
        getAclOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        Map<String, Object> resultMap;
        if (input == null) {
            resultMap = getTrafficPolicyApi().getAclInfo(null, null);
        } else {
            resultMap = getTrafficPolicyApi().getAclInfo(input.getRouterId(), input.getAclName());
        }
        Map<String,List<AclInfoServiceEntity>> aclMaps =
                (Map<String,List<AclInfoServiceEntity>>)resultMap.get(ResponseEnum.BODY.getName());
        List<AclRouters> aclRoutersList = new ArrayList<>();
        for (String key : aclMaps.keySet()) {
            AclRoutersBuilder ruleListBuilder = new AclRoutersBuilder();
            List<AclEntries> aclEntriesList = new ArrayList<>();

            List<AclInfoServiceEntity> aclInfoServiceEntityList = aclMaps.get(key);
            for (AclInfoServiceEntity aclInfoServiceEntity : aclInfoServiceEntityList) {
                AclEntriesBuilder aclEntriesBuilder = new AclEntriesBuilder();
                List<Rules> rulesList = new ArrayList<>();
                for (AclRuleInfoServiceEntity aclRuleInfoServiceEntity : aclInfoServiceEntity.getAclRuleInfoServiceEntityList()) {
                    RulesBuilder rulesBuilder = new RulesBuilder();
                    rulesBuilder.setRuleId(aclRuleInfoServiceEntity.getRuleId());
                    rulesBuilder.setRuleType(aclRuleInfoServiceEntity.getRuleType());
                    rulesBuilder.setProtoType(aclRuleInfoServiceEntity.getProtoType());
                    rulesBuilder.setSource(aclRuleInfoServiceEntity.getSourcce() + " " + aclRuleInfoServiceEntity.getSourcceWild());
                    rulesBuilder.setSourcePort(aclRuleInfoServiceEntity.getSourcePortOp() + " " + aclRuleInfoServiceEntity.getSourcePort());
                    rulesBuilder.setDestination(aclRuleInfoServiceEntity.getDestination() + " " + aclRuleInfoServiceEntity.getDestinationWild());
                    rulesBuilder.setDestinationPort(aclRuleInfoServiceEntity.getDestinationPortOp() + " " + aclRuleInfoServiceEntity.getDestinationPort());
                    rulesList.add(rulesBuilder.build());
                }
                aclEntriesBuilder.setAclName(aclInfoServiceEntity.getAclName());
                aclEntriesBuilder.setRules(rulesList);
                aclEntriesList.add(aclEntriesBuilder.build());
            }
            ruleListBuilder.setRouterId(key);
            ruleListBuilder.setAclEntries(aclEntriesList);
            aclRoutersList.add(ruleListBuilder.build());
        }
        getAclOutputBuilder.setAclRouters(aclRoutersList);
        LOG.info("getAcl end");
        return RpcResultBuilder.success(getAclOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<AddAclOutput>> addAcl(AddAclInput input) {
        LOG.info("addAcl begin");
        LOG.info(input.getRouterId());
        AddAclOutputBuilder addAclOutputBuilder = new AddAclOutputBuilder();


        AclInfoServiceEntity aclInfoServiceEntity = new AclInfoServiceEntity();
        aclInfoServiceEntity.setRouterId(input.getRouterId());
        if (input.getAclEntries().size() != 0) {
            aclInfoServiceEntity.setAclName(input.getAclEntries().get(0).getAclName());
        }



        addAclOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("addAcl end");
        return RpcResultBuilder.success(addAclOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<DeleteAclOutput>> deleteAcl(DeleteAclInput input) {
        LOG.info("getTunnelStatistics begin");
        DeleteAclOutputBuilder deleteAclOutputBuilder = new DeleteAclOutputBuilder();

        Map<String, Object> resultMap = getTrafficPolicyApi().deleteAclInfo(input.getRouterId(), input.getAclName());
        deleteAclOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("getTunnelStatistics end");
        return RpcResultBuilder.success(deleteAclOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<UpdateAclOutput>> updateAcl(UpdateAclInput input) {
        LOG.info("getTunnelStatistics begin");
        LOG.info("getTunnelStatistics end");
        return null;
    }

    //trafficClass
    @Override
    public Future<RpcResult<GetTrafficClassOutput>> getTrafficClass(GetTrafficClassInput input) {
        LOG.info("getTrafficClass begin");
        GetTrafficClassOutputBuilder getTrafficClassOutputBuilder = new GetTrafficClassOutputBuilder();
        getTrafficClassOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        List<TrafficClassRouters> trafficClassRoutersList = new ArrayList<>();
        Map<String, Object> resultMap;
        if (input == null) {
            resultMap = getTrafficPolicyApi().getTrafficClassInfo(null, null);
        } else {
            resultMap = getTrafficPolicyApi().getTrafficClassInfo(input.getRouterId(), input.getClassName());
        }
        Map<String,List<TrafficClassServiceEntity>> trafficClassMaps =
                (Map<String,List<TrafficClassServiceEntity>>)resultMap.get(ResponseEnum.BODY.getName());

        for (String key : trafficClassMaps.keySet()) {
            List<TrafficClassServiceEntity> trafficClassServiceEntityList = trafficClassMaps.get(key);
            TrafficClassRoutersBuilder trafficClassRoutersBuilder = new TrafficClassRoutersBuilder();
            trafficClassRoutersBuilder.setRouterId(key);
            List<TrafficClassEntries> trafficClassEntriesList = new ArrayList<>();
            for (TrafficClassServiceEntity trafficClassServiceEntity : trafficClassServiceEntityList) {
                TrafficClassEntriesBuilder trafficClassEntriesBuilder = new TrafficClassEntriesBuilder();
                trafficClassEntriesBuilder.setClassName(trafficClassServiceEntity.getTrafficClassName());
                trafficClassEntriesBuilder.setOperator(trafficClassServiceEntity.getOperator());
                List<Matches> matchesList = new ArrayList<>();
                for (TrafficClassAclServiceEntity trafficClassAclServiceEntity:trafficClassServiceEntity.getTrafficClassAclServiceEntityList()) {
                    MatchesBuilder matchesBuilder = new MatchesBuilder();
                    matchesBuilder.setAclName(trafficClassAclServiceEntity.getAclName());
                    matchesList.add(matchesBuilder.build());
                }
                trafficClassEntriesBuilder.setMatches(matchesList);
                trafficClassEntriesList.add(trafficClassEntriesBuilder.build());
            }
            trafficClassRoutersBuilder.setTrafficClassEntries(trafficClassEntriesList);
            trafficClassRoutersList.add(trafficClassRoutersBuilder.build());
        }

        getTrafficClassOutputBuilder.setTrafficClassRouters(trafficClassRoutersList);
        LOG.info("getTrafficClass end");
        return RpcResultBuilder.success(getTrafficClassOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<UpdateTrafficClassOutput>> updateTrafficClass(UpdateTrafficClassInput input) {
        LOG.info("updateTrafficClass begin");
        LOG.info("updateTrafficClass end");
        return null;
    }

    @Override
    public Future<RpcResult<DeleteTrafficClassOutput>> deleteTrafficClass(DeleteTrafficClassInput input) {
        LOG.info("deleteTrafficClass begin");
        DeleteTrafficClassOutputBuilder deleteTrafficClassOutputBuilder = new DeleteTrafficClassOutputBuilder();

        Map<String, Object> resultMap = getTrafficPolicyApi().deleteTrafficClassInfo(input.getRouterId(), input.getClassName());
        deleteTrafficClassOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("deleteTrafficClass end");
        return RpcResultBuilder.success(deleteTrafficClassOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<AddTrafficClassOutput>> addTrafficClass(AddTrafficClassInput input) {
        LOG.info("addTrafficClass begin");
        LOG.info(input.getRouterId());
        AddTrafficClassOutputBuilder addTrafficClassOutputBuilder = new AddTrafficClassOutputBuilder();
        addTrafficClassOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("addTrafficClass end");
        return RpcResultBuilder.success(addTrafficClassOutputBuilder.build()).buildFuture();
    }

    //trafficBehave
    @Override
    public Future<RpcResult<UpdateTrafficBehaveOutput>> updateTrafficBehave(UpdateTrafficBehaveInput input) {
        LOG.info("getTunnelStatistics begin");
        LOG.info("getTunnelStatistics end");
        return null;
    }

    @Override
    public Future<RpcResult<DeleteTrafficBehaveOutput>> deleteTrafficBehave(DeleteTrafficBehaveInput input) {
        LOG.info("getTunnelStatistics begin");
        DeleteTrafficBehaveOutputBuilder deleteTrafficBehaveOutputBuilder = new DeleteTrafficBehaveOutputBuilder();
        Map<String, Object> resultMap = getTrafficPolicyApi().deleteTrafficBehaveInfo(input.getRouterId(), input.getBehaveName());
        deleteTrafficBehaveOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("getTunnelStatistics end");
        return RpcResultBuilder.success(deleteTrafficBehaveOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<AddTrafficBehaveOutput>> addTrafficBehave(AddTrafficBehaveInput input) {
        LOG.info("addTrafficBehave begin");
        AddTrafficBehaveOutputBuilder addTrafficBehaveOutputBuilder = new AddTrafficBehaveOutputBuilder();
        addTrafficBehaveOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("addTrafficBehave end");
        return RpcResultBuilder.success(addTrafficBehaveOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetTrafficBehaveOutput>> getTrafficBehave(GetTrafficBehaveInput input) {
        LOG.info("getTrafficBehave begin");
        GetTrafficBehaveOutputBuilder getTrafficBehaveOutputBuilder = new GetTrafficBehaveOutputBuilder();
        getTrafficBehaveOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        List<TrafficBehaveRouters> trafficBehaveRoutersList = new ArrayList<>();
        Map<String, Object> resultMap;
        if (input == null) {
            resultMap = getTrafficPolicyApi().getTrafficBehaveInfo(null, null);
        } else {
            resultMap = getTrafficPolicyApi().getTrafficBehaveInfo(input.getRouterId(), input.getBehaveName());
        }
        Map<String,List<TrafficBehaveServiceEntity>> trafficBehaveMaps =
                (Map<String,List<TrafficBehaveServiceEntity>>)resultMap.get(ResponseEnum.BODY.getName());
        for (String key : trafficBehaveMaps.keySet()) {
            List<TrafficBehaveServiceEntity> trafficBehaveServiceEntityList = trafficBehaveMaps.get(key);
            TrafficBehaveRoutersBuilder trafficBehaveRoutersBuilder = new TrafficBehaveRoutersBuilder();
            trafficBehaveRoutersBuilder.setRouterId(key);
            List<TrafficBehaveEntries> trafficBehaveEntriesList = new ArrayList<>();
            for (TrafficBehaveServiceEntity trafficBehaveServiceEntity : trafficBehaveServiceEntityList) {
                TrafficBehaveEntriesBuilder trafficBehaveEntriesBuilder = new TrafficBehaveEntriesBuilder();
                trafficBehaveEntriesBuilder.setBehaveName(trafficBehaveServiceEntity.getTrafficBehaveName());
                trafficBehaveEntriesBuilder.setTunnelName(trafficBehaveServiceEntity.getRedirectTunnelName());
                trafficBehaveEntriesList.add(trafficBehaveEntriesBuilder.build());
            }
            trafficBehaveRoutersBuilder.setTrafficBehaveEntries(trafficBehaveEntriesList);
            trafficBehaveRoutersList.add(trafficBehaveRoutersBuilder.build());
        }
        getTrafficBehaveOutputBuilder.setTrafficBehaveRouters(trafficBehaveRoutersList);
        LOG.info("getTrafficBehave end");
        return RpcResultBuilder.success(getTrafficBehaveOutputBuilder.build()).buildFuture();
    }

    //trafficPolicy
    @Override
    public Future<RpcResult<UpdateTrafficPolicyOutput>> updateTrafficPolicy(UpdateTrafficPolicyInput input) {
        LOG.info("getTunnelStatistics begin");
        LOG.info("getTunnelStatistics end");
        return null;
    }

    @Override
    public Future<RpcResult<DeleteTrafficPolicyOutput>> deleteTrafficPolicy(DeleteTrafficPolicyInput input) {
        LOG.info("getTunnelStatistics begin");
        DeleteTrafficPolicyOutputBuilder deleteTrafficPolicyOutputBuilder = new DeleteTrafficPolicyOutputBuilder();
        Map<String, Object> resultMap = getTrafficPolicyApi().deleteTrafficPolicyInfo(input.getRouterId(), input.getPolicyName());
        deleteTrafficPolicyOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("getTunnelStatistics end");
        return RpcResultBuilder.success(deleteTrafficPolicyOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<AddTrafficPolicyOutput>> addTrafficPolicy(AddTrafficPolicyInput input) {
        LOG.info("addTrafficPolicy begin");
        AddTrafficPolicyOutputBuilder addTrafficPolicyOutputBuilder = new AddTrafficPolicyOutputBuilder();
        addTrafficPolicyOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("addTrafficPolicy end");
        return RpcResultBuilder.success(addTrafficPolicyOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetTrafficPolicyOutput>> getTrafficPolicy(GetTrafficPolicyInput input) {
        LOG.info("getTrafficPolicy begin");
        GetTrafficPolicyOutputBuilder getTrafficPolicyOutputBuilder = new GetTrafficPolicyOutputBuilder();
        getTrafficPolicyOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        List<TrafficPolicyRouters> trafficPolicyRoutersList = new ArrayList<>();

        Map<String, Object> resultMap;
        if (input == null) {
            resultMap = getTrafficPolicyApi().getTrafficPolicyInfo(null, null);
        } else {
            resultMap = getTrafficPolicyApi().getTrafficPolicyInfo(input.getRouterId(), input.getPolicyName());
        }
        Map<String,List<TrafficPolicyServiceEntity>> trafficPolicyMaps =
                (Map<String,List<TrafficPolicyServiceEntity>>)resultMap.get(ResponseEnum.BODY.getName());
        for (String key : trafficPolicyMaps.keySet()) {
            List<TrafficPolicyServiceEntity> trafficPolicyServiceEntityList = trafficPolicyMaps.get(key);
            TrafficPolicyRoutersBuilder trafficPolicyRoutersBuilder = new TrafficPolicyRoutersBuilder();
            trafficPolicyRoutersBuilder.setRouterId(key);
            List<TrafficPolicyEntries> trafficPolicyEntriesList = new ArrayList<>();
            for (TrafficPolicyServiceEntity trafficPolicyServiceEntity : trafficPolicyServiceEntityList) {
                TrafficPolicyEntriesBuilder trafficPolicyEntriesBuilder = new TrafficPolicyEntriesBuilder();
                trafficPolicyEntriesBuilder.setPolicyName(trafficPolicyServiceEntity.getTrafficPolicyName());
                List<Policy> policyList = new ArrayList<>();
                for (TrafficPolicyNodeServiceEntity trafficPolicyNodeServiceEntity:trafficPolicyServiceEntity.getTrafficPolicyNodeServiceEntityList()) {
                    PolicyBuilder policyBuilder = new PolicyBuilder();
                    policyBuilder.setBehaveName(trafficPolicyNodeServiceEntity.getBehaveName());
                    policyBuilder.setClassName(trafficPolicyNodeServiceEntity.getClassName());
                    policyList.add(policyBuilder.build());
                }
                trafficPolicyEntriesBuilder.setPolicy(policyList);
                trafficPolicyEntriesList.add(trafficPolicyEntriesBuilder.build());
            }
            trafficPolicyRoutersBuilder.setTrafficPolicyEntries(trafficPolicyEntriesList);
            trafficPolicyRoutersList.add(trafficPolicyRoutersBuilder.build());
        }

        getTrafficPolicyOutputBuilder.setTrafficPolicyRouters(trafficPolicyRoutersList);
        LOG.info("getTrafficPolicy end");
        return RpcResultBuilder.success(getTrafficPolicyOutputBuilder.build()).buildFuture();
    }

    //ifPolicy
    @Override
    public Future<RpcResult<GetIfPolicyOutput>> getIfPolicy(GetIfPolicyInput input) {
        LOG.info("getIfPolicy begin");
        GetIfPolicyOutputBuilder getIfPolicyOutputBuilder = new GetIfPolicyOutputBuilder();
        getIfPolicyOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        List<IfPolicyRouters> ifPolicyRoutersList = new ArrayList<>();

        Map<String, Object> resultMap;
        if (input == null) {
            resultMap = getTrafficPolicyApi().getTrafficIfPolicyInfo(null , null);
        } else {
            resultMap = getTrafficPolicyApi().getTrafficIfPolicyInfo(input.getRouterId(), input.getIfName());
        }
        Map<String,List<TrafficIfPolicyServiceEntity>> trafficIfPolicyMaps =
                (Map<String,List<TrafficIfPolicyServiceEntity>>)resultMap.get(ResponseEnum.BODY.getName());
        for (String key : trafficIfPolicyMaps.keySet()) {
            List<TrafficIfPolicyServiceEntity> trafficIfPolicyServiceEntityList = trafficIfPolicyMaps.get(key);
            IfPolicyRoutersBuilder ifPolicyRoutersBuilder = new IfPolicyRoutersBuilder();
            ifPolicyRoutersBuilder.setRouterId(key);
            List<IfPolicyEntries> ifPolicyEntriesList = new ArrayList<>();
            for (TrafficIfPolicyServiceEntity trafficIfPolicyServiceEntity : trafficIfPolicyServiceEntityList) {
                IfPolicyEntriesBuilder ifPolicyEntriesBuilder = new IfPolicyEntriesBuilder();
                ifPolicyEntriesBuilder.setIfName(trafficIfPolicyServiceEntity.getIfName());
                ifPolicyEntriesBuilder.setPolicyName(trafficIfPolicyServiceEntity.getPolicyName());
                ifPolicyEntriesList.add(ifPolicyEntriesBuilder.build());
            }
            ifPolicyRoutersBuilder.setIfPolicyEntries(ifPolicyEntriesList);
            ifPolicyRoutersList.add(ifPolicyRoutersBuilder.build());
        }
        getIfPolicyOutputBuilder.setIfPolicyRouters(ifPolicyRoutersList);
        LOG.info("getIfPolicy end");
        return RpcResultBuilder.success(getIfPolicyOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<AddIfPolicyOutput>> addIfPolicy(AddIfPolicyInput input) {
        LOG.info("addIfPolicy begin");
        AddIfPolicyOutputBuilder addIfPolicyOutputBuilder = new AddIfPolicyOutputBuilder();
        addIfPolicyOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("addIfPolicy end");
        return RpcResultBuilder.success(addIfPolicyOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<UpdateIfPolicyOutput>> updateIfPolicy(UpdateIfPolicyInput input) {
        LOG.info("updateIfPolicy begin");
        LOG.info("updateIfPolicy end");
        return null;
    }

    @Override
    public Future<RpcResult<DeleteIfPolicyOutput>> deleteIfPolicy(DeleteIfPolicyInput input) {
        LOG.info("deleteIfPolicy begin");
        DeleteIfPolicyOutputBuilder deleteIfPolicyOutputBuilder = new DeleteIfPolicyOutputBuilder();
        Map<String, Object> resultMap = getTrafficPolicyApi().deleteTrafficIfPolicyInfo(input.getRouterId(), input.getIfName());
        deleteIfPolicyOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("deleteIfPolicy end");
        return RpcResultBuilder.success(deleteIfPolicyOutputBuilder.build()).buildFuture();
    }
}
