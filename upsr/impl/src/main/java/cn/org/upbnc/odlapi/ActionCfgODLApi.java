package cn.org.upbnc.odlapi;

import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.CodeEnum;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.*;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

public class ActionCfgODLApi implements UpsrActionCfgService {
    private static final Logger LOG = LoggerFactory.getLogger(NetconfSessionODLApi.class);
    Session session;

    public ActionCfgODLApi(Session session) {
        this.session = session;
    }

    @Override
    public Future<RpcResult<CommitCfgOutput>> commitCfg(CommitCfgInput input) {
        LOG.info("commitCfg begin");
        CommitCfgOutputBuilder commitCfgOutputBuilder = new CommitCfgOutputBuilder();
        commitCfgOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("commitCfg end");
        return RpcResultBuilder.success(commitCfgOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetCfgCliChaneOutput>> getCfgCliChane(GetCfgCliChaneInput input) {
        LOG.info("getCfgCliChane begin");
        GetCfgCliChaneOutputBuilder getCfgCliChaneOutputBuilder = new GetCfgCliChaneOutputBuilder();
        getCfgCliChaneOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("getCfgCliChane end");
        return RpcResultBuilder.success(getCfgCliChaneOutputBuilder.build()).buildFuture();
    }
}
