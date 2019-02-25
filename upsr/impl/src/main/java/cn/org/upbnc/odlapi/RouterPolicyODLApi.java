package cn.org.upbnc.odlapi;

import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.CodeEnum;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.GetRoutePolicysInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.GetRoutePolicysOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.GetRoutePolicysOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.UpsrRouterPolicyService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.getroutepolicys.output.RoutePolicy;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.getroutepolicys.output.RoutePolicyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.routepolicy.RoutePolicyNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.routepolicy.RoutePolicyNodeBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class RouterPolicyODLApi implements UpsrRouterPolicyService {
    private static final Logger LOG = LoggerFactory.getLogger(RouterPolicyODLApi.class);
    Session session;
    public RouterPolicyODLApi (Session session) {
        this.session = session;
    }
    @Override
    public Future<RpcResult<GetRoutePolicysOutput>> getRoutePolicys(GetRoutePolicysInput input) {
        GetRoutePolicysOutputBuilder getRoutePolicysOutputBuilder = new GetRoutePolicysOutputBuilder();
        LOG.info("getRoutePolicys input : " + input);
        getRoutePolicysOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());

        List<RoutePolicy> routePolicyList = new ArrayList<>();
        for (int i = 1;i < 3; i++) {
            RoutePolicyBuilder routePolicyBuilder = new RoutePolicyBuilder();
            routePolicyBuilder.setPolicyName("policy_" + i);

            List<RoutePolicyNode> routePolicyNodeList = new ArrayList<>();
            for (int j = 1; j < 3; j++) {
                RoutePolicyNodeBuilder routePolicyNodeBuilder = new RoutePolicyNodeBuilder();
                routePolicyNodeBuilder.setNodeSequence(j + "0");
                routePolicyNodeList.add(routePolicyNodeBuilder.build());
            }
            routePolicyBuilder.setRoutePolicyNode(routePolicyNodeList);
            routePolicyList.add(routePolicyBuilder.build());
        }
        getRoutePolicysOutputBuilder.setRoutePolicy(routePolicyList);
        return RpcResultBuilder.success(getRoutePolicysOutputBuilder.build()).buildFuture();
    }
}
