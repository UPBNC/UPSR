package cn.org.upbnc.odlapi;

import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.CodeEnum;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.gettunnelpolicys.output.TunnelPolicyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.tunnelpolicy.PolicyDest;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.tunnelpolicy.PolicyDestBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.tunnelpolicy.policydest.BindTunnel;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.tunnelpolicy.policydest.BindTunnelBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.gettunnelpolicys.output.TunnelPolicy;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class TunnelPolicyODLApi implements UpsrTunnelPolicyService {
    private static final Logger LOG = LoggerFactory.getLogger(TunnelPolicyODLApi.class);
    Session session;

    public TunnelPolicyODLApi(Session session) {
        this.session = session;
    }

    @Override
    public Future<RpcResult<GetTunnelPolicysOutput>> getTunnelPolicys(GetTunnelPolicysInput input) {
        GetTunnelPolicysOutputBuilder getTunnelPolicysOutputBuilder = new GetTunnelPolicysOutputBuilder();
        LOG.info("getTunnelPolicys input : " + input);
        getTunnelPolicysOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());

        List<TunnelPolicy> tunnelPolicyList = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            TunnelPolicyBuilder tunnelPolicyBuilder = new TunnelPolicyBuilder();
            tunnelPolicyBuilder.setPolicyName("policy_" + i);
            List<PolicyDest> policyDestList = new ArrayList<>();
            for (int j = 1; j < 3; j++) {
                PolicyDestBuilder policyDestBuilder = new PolicyDestBuilder();
                policyDestBuilder.setDestAddr("2.2.2." + j);
                policyDestBuilder.setIgnoreDestCheck("1");
                policyDestBuilder.setIncludeLdp("1");

                List<BindTunnel> bindTunnelList = new ArrayList<>();
                for (int k = 1; k < 3; k++) {
                    BindTunnelBuilder bindTunnelBuilder = new BindTunnelBuilder();
                    bindTunnelBuilder.setTunnelName("tunnel_12" + k);
                    bindTunnelList.add(bindTunnelBuilder.build());
                }
                policyDestBuilder.setBindTunnel(bindTunnelList);
                policyDestList.add(policyDestBuilder.build());
            }
            tunnelPolicyBuilder.setPolicyDest(policyDestList);
            tunnelPolicyList.add(tunnelPolicyBuilder.build());
        }
        getTunnelPolicysOutputBuilder.setTunnelPolicy(tunnelPolicyList);
        return RpcResultBuilder.success(getTunnelPolicysOutputBuilder.build()).buildFuture();
    }
}
