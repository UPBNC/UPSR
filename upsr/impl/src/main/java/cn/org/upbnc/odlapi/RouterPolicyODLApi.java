package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.RoutePolicyApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.service.entity.RoutePolicyEntity;
import cn.org.upbnc.service.entity.RoutePolicyNodeEntity;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.GetRoutePolicysInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.GetRoutePolicysOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.GetRoutePolicysOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.UpsrRouterPolicyService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.getroutepolicys.output.RoutePolicys;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.getroutepolicys.output.RoutePolicysBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.routepolicy.RoutePolicyNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.routepolicy.RoutePolicyNodeBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class RouterPolicyODLApi implements UpsrRouterPolicyService {
    private static final Logger LOG = LoggerFactory.getLogger(RouterPolicyODLApi.class);
    Session session;
    private RoutePolicyApi routePolicyApi;

    public RouterPolicyODLApi(Session session) {
        this.session = session;
    }

    private RoutePolicyApi getRoutePolicyApi() {
        if (this.routePolicyApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                routePolicyApi = apiInterface.getRoutePolicyApi();
            }
        }
        return this.routePolicyApi;
    }

    @Override
    public Future<RpcResult<GetRoutePolicysOutput>> getRoutePolicys(GetRoutePolicysInput input) {
        LOG.info("getRoutePolicys input : " + input);
        GetRoutePolicysOutputBuilder getRoutePolicysOutputBuilder = new GetRoutePolicysOutputBuilder();
        Map<String, Object> resultMap = new HashMap<>();
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            getRoutePolicysOutputBuilder.setResult("System is not ready or shutdown");
            return RpcResultBuilder.success(getRoutePolicysOutputBuilder.build()).buildFuture();
        }
        resultMap = getRoutePolicyApi().getRoutePolicyMap(input.getRouterId(), input.getPolicyName());
        List<RoutePolicyEntity> routePolicyEntities =
                (List<RoutePolicyEntity>) resultMap.get(ResponseEnum.BODY.getName());
        List<RoutePolicys> routePolicyList = new ArrayList<>();
        for (RoutePolicyEntity routePolicyEntity : routePolicyEntities) {
            RoutePolicysBuilder routePolicyBuilder = new RoutePolicysBuilder();
            routePolicyBuilder.setPolicyName(routePolicyEntity.getPolicyName());
            routePolicyBuilder.setRouterId(routePolicyEntity.getRouterId());
            List<RoutePolicyNode> routePolicyNodeList = new ArrayList<>();
            List<RoutePolicyNodeEntity> routePolicyNodeEntities = routePolicyEntity.getRoutePolicyNodes();
            for (RoutePolicyNodeEntity routePolicyNodeEntity : routePolicyNodeEntities) {
                RoutePolicyNodeBuilder routePolicyNodeBuilder = new RoutePolicyNodeBuilder();
                routePolicyNodeBuilder.setNodeSequence(routePolicyNodeEntity.getNodeSequence());
                routePolicyNodeList.add(routePolicyNodeBuilder.build());
            }
            routePolicyBuilder.setRoutePolicyNode(routePolicyNodeList);
            routePolicyList.add(routePolicyBuilder.build());
        }
        getRoutePolicysOutputBuilder.setRoutePolicys(routePolicyList);
        getRoutePolicysOutputBuilder.setResult("success");
        return RpcResultBuilder.success(getRoutePolicysOutputBuilder.build()).buildFuture();
    }
}
