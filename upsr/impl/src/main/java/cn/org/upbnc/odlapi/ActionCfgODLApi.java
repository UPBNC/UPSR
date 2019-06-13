package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.ActionCfgApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgchane.output.Command;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgchane.output.CommandBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class ActionCfgODLApi implements UpsrActionCfgService {
    private static final Logger LOG = LoggerFactory.getLogger(NetconfSessionODLApi.class);
    Session session;
    ActionCfgApi actionCfgApi;

    public ActionCfgODLApi(Session session) {
        this.session = session;
    }

    private ActionCfgApi getActionCfgApi() {
        if (this.actionCfgApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                this.actionCfgApi = apiInterface.getActionCfgApi();
            }
        }
        return this.actionCfgApi;
    }

    @Override
    public Future<RpcResult<CommitCfgOutput>> commitCfg(CommitCfgInput input) {
        LOG.info("commitCfg begin");
        CommitCfgOutputBuilder commitCfgOutputBuilder = new CommitCfgOutputBuilder();
        commitCfgOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("commitCfg end");
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(commitCfgOutputBuilder.build()).buildFuture();
        } else {
            this.getActionCfgApi();
        }
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

    @Override
    public Future<RpcResult<GetCfgChaneOutput>> getCfgChane(GetCfgChaneInput input){
        LOG.info("getCfgChane begin");
        GetCfgChaneOutputBuilder getCfgChaneOutputBuilder = new GetCfgChaneOutputBuilder();
        getCfgChaneOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(getCfgChaneOutputBuilder.build()).buildFuture();
        } else {
            this.getActionCfgApi();
        }
        Map<String, Object> cfgChaneRet = new HashMap<>();
        if (input == null) {
            cfgChaneRet = this.actionCfgApi.getCfgChane(input.getRouterId(), input.getCfgType());
        } else {
            cfgChaneRet = this.actionCfgApi.getCfgChane(input.getRouterId(), input.getCfgType());
        }
        if (cfgChaneRet.get(ResponseEnum.CODE.getName()) == CodeEnum.SUCCESS.getName()) {
            List<Command> commandList = new ArrayList<>();
            CommandBuilder commandBuilder = new CommandBuilder();
            commandBuilder.setDeviceName("shanghai-pe-1");
            commandBuilder.setRouterId("1.1.1.1");
            commandBuilder.setChange((String) cfgChaneRet.get(ResponseEnum.MESSAGE.getName()));
            commandList.add(commandBuilder.build());
            getCfgChaneOutputBuilder.setCommand(commandList);
        }
        LOG.info("getCfgChane end");
        return RpcResultBuilder.success(getCfgChaneOutputBuilder.build()).buildFuture();
    }

}
