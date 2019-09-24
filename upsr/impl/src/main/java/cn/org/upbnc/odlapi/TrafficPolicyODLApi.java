package cn.org.upbnc.odlapi;

import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.CodeEnum;
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
import java.util.concurrent.Future;

public class TrafficPolicyODLApi implements UpsrTrafficPolicyService {
    private static final Logger LOG = LoggerFactory.getLogger(TrafficPolicyODLApi.class);
    Session session;

    public TrafficPolicyODLApi(Session session) {
        this.session = session;
    }
    //acl
    @Override
    public Future<RpcResult<GetAclOutput>> getAcl(GetAclInput input) {
        LOG.info("getAcl begin");
        GetAclOutputBuilder getAclOutputBuilder = new GetAclOutputBuilder();
        getAclOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        List<AclRouters> aclRoutersList = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            AclRoutersBuilder ruleListBuilder = new AclRoutersBuilder();
            List<AclEntries> aclEntriesList = new ArrayList<>();
            for (int k = 1; k < 3; k++) {
                AclEntriesBuilder aclEntriesBuilder = new AclEntriesBuilder();
                List<Rules> rulesList = new ArrayList<>();
                for (int j = 1; j < 5; j++) {
                    RulesBuilder rulesBuilder = new RulesBuilder();
                    rulesBuilder.setRuleId("" + j);
                    rulesBuilder.setRuleType("permit");
                    rulesBuilder.setProtoType("tcp");
                    rulesBuilder.setSource("10.10.10." + j);
                    rulesBuilder.setSourcePort("10" + j);
                    rulesBuilder.setDestination("11.11.11." + j);
                    rulesBuilder.setDestinationPort("11" + j);
                    rulesList.add(rulesBuilder.build());
                }
                aclEntriesBuilder.setAclName("acl300" + k);
                aclEntriesBuilder.setRules(rulesList);
                aclEntriesList.add(aclEntriesBuilder.build());
            }
            ruleListBuilder.setRouterId("1.1.1." + i);
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
        addAclOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("addAcl end");
        return RpcResultBuilder.success(addAclOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<DeleteAclOutput>> deleteAcl(DeleteAclInput input) {
        LOG.info("getTunnelStatistics begin");
        LOG.info("getTunnelStatistics end");
        return null;
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
        for (int i = 1; i < 3; i++) {
            TrafficClassRoutersBuilder trafficClassRoutersBuilder = new TrafficClassRoutersBuilder();
            trafficClassRoutersBuilder.setRouterId("1.1.1." + i);
            List<TrafficClassEntries> trafficClassEntriesList = new ArrayList<>();
            for (int j = 1; j < 3 ;j ++) {
                TrafficClassEntriesBuilder trafficClassEntriesBuilder = new TrafficClassEntriesBuilder();
                trafficClassEntriesBuilder.setClassName("c" + j);
                trafficClassEntriesBuilder.setOperator(j%2==0?"and":"or");
                List<Matches> matchesList = new ArrayList<>();
                for (int k = 1; k < 5; k++) {
                    MatchesBuilder matchesBuilder = new MatchesBuilder();
                    matchesBuilder.setAclName("300" + k);
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
        LOG.info("deleteTrafficClass end");
        return null;
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
        LOG.info("getTunnelStatistics end");
        return null;
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
        for (int i = 1; i < 3; i++) {
            TrafficBehaveRoutersBuilder trafficBehaveRoutersBuilder = new TrafficBehaveRoutersBuilder();
            trafficBehaveRoutersBuilder.setRouterId("1.1.1." + i);
            List<TrafficBehaveEntries> trafficBehaveEntriesList = new ArrayList<>();
            for (int j = 1; j < 3; j++) {
                TrafficBehaveEntriesBuilder trafficBehaveEntriesBuilder = new TrafficBehaveEntriesBuilder();
                trafficBehaveEntriesBuilder.setBehaveName("be" + j);
                trafficBehaveEntriesBuilder.setTunnelName("Tunnel_" + j);
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
        LOG.info("getTunnelStatistics end");
        return null;
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
        for (int i = 1; i < 3; i++) {
            TrafficPolicyRoutersBuilder trafficPolicyRoutersBuilder = new TrafficPolicyRoutersBuilder();
            trafficPolicyRoutersBuilder.setRouterId("1.1.1." + i);
            List<TrafficPolicyEntries> trafficPolicyEntriesList = new ArrayList<>();
            for (int j = 1; j < 3; j++) {
                TrafficPolicyEntriesBuilder trafficPolicyEntriesBuilder = new TrafficPolicyEntriesBuilder();
                trafficPolicyEntriesBuilder.setPolicyName("policy_" + j);
                List<Policy> policyList = new ArrayList<>();
                for (int k = 1; k < 4; k++) {
                    PolicyBuilder policyBuilder = new PolicyBuilder();
                    policyBuilder.setBehaveName("b" + k);
                    policyBuilder.setClassName("c" + k);
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
        for (int i = 1; i < 3; i++) {
            IfPolicyRoutersBuilder ifPolicyRoutersBuilder = new IfPolicyRoutersBuilder();
            ifPolicyRoutersBuilder.setRouterId("1.1.1." + i);
            List<IfPolicyEntries> ifPolicyEntriesList = new ArrayList<>();
            for (int j = 1; j < 3; j++) {
                IfPolicyEntriesBuilder ifPolicyEntriesBuilder = new IfPolicyEntriesBuilder();
                ifPolicyEntriesBuilder.setIfName("interface G0/0/" + j);
                ifPolicyEntriesBuilder.setPolicyName("policy" + j);
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
        LOG.info("deleteIfPolicy end");
        return null;
    }
}
