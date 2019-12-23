package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.DiagnoseApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrdiagnose.rev191211.GetDiagnoseInfoInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrdiagnose.rev191211.GetDiagnoseInfoOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrdiagnose.rev191211.GetDiagnoseInfoOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrdiagnose.rev191211.UpsrDiagnoseService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class DiagnoseODLApi implements UpsrDiagnoseService {
    private static final Logger LOG = LoggerFactory.getLogger(DiagnoseODLApi.class);
    Session session;
    DiagnoseApi diagnoseApi;

    public DiagnoseODLApi(Session session) {
        this.session = session;
    }
    private DiagnoseApi getDiagnoseApi() {
        if (this.diagnoseApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                diagnoseApi = apiInterface.getDiagnoseApi();
            }
        }
        return this.diagnoseApi;
    }
    @Override
    public Future<RpcResult<GetDiagnoseInfoOutput>> getDiagnoseInfo(GetDiagnoseInfoInput input) {
        LOG.info("getDiagnoseInfo begin");
        GetDiagnoseInfoOutputBuilder getDiagnoseInfoOutputBuilder = new GetDiagnoseInfoOutputBuilder();
        Map<String, Object> resultMap = new HashMap<>();
        if(input.getDiagnoseType().equals("tunnel")) {
            resultMap = getDiagnoseApi().getDiagnoseTunnelInfo(input.getRouterId());
        }
        if(input.getDiagnoseType().equals("VPN")) {
            resultMap = getDiagnoseApi().getDiagnoseVpndownInfo(input.getRouterId());
        }
        String diagnoseInfo =  (String) resultMap.get(ResponseEnum.BODY.getName());
        getDiagnoseInfoOutputBuilder.setDiagnoseInfo(diagnoseInfo);
        getDiagnoseInfoOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("getDiagnoseInfo end");
        return RpcResultBuilder.success(getDiagnoseInfoOutputBuilder.build()).buildFuture();
    }
}
