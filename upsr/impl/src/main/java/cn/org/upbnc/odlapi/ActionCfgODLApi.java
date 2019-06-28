package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.ActionCfgApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgchange.output.Command;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgchange.output.CommandBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgchange.output.command.CommandChange;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgchange.output.command.CommandChangeBuilder;
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
    public Future<RpcResult<GetCfgCliChangeOutput>> getCfgCliChange(GetCfgCliChangeInput input) {
        LOG.info("getCfgCliChane begin");
        GetCfgCliChangeOutputBuilder getCfgCliChangeOutputBuilder = new GetCfgCliChangeOutputBuilder();
        getCfgCliChangeOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("getCfgCliChane end");
        return RpcResultBuilder.success(getCfgCliChangeOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetCfgChangeOutput>> getCfgChange(GetCfgChangeInput input){
        LOG.info("getCfgChane begin");
        GetCfgChangeOutputBuilder getCfgChangeOutputBuilder = new GetCfgChangeOutputBuilder();
        getCfgChangeOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(getCfgChangeOutputBuilder.build()).buildFuture();
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
            for (int i = 1;i<=2;i++) {
                CommandBuilder commandBuilder = new CommandBuilder();
                commandBuilder.setDeviceName("shanghai-pe-" + i);
                commandBuilder.setRouterId("1.1.1." + i);
                this.commandChangeBuild(commandBuilder, (List<String>) cfgChaneRet.get(ResponseEnum.MESSAGE.getName()));
                commandList.add(commandBuilder.build());
            }
            getCfgChangeOutputBuilder.setCommand(commandList);
        }
        LOG.info("getCfgChane end");
        return RpcResultBuilder.success(getCfgChangeOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<CancelCfgChangeOutput>> cancelCfgChange(CancelCfgChangeInput input) {
        CancelCfgChangeOutputBuilder cancelCfgChangeOutputBuilder = new CancelCfgChangeOutputBuilder();
        LOG.info("cancelCfgChane begin");
        LOG.info(input.getCfgType());
        LOG.info(input.getRouterId());
        cancelCfgChangeOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("cancelCfgChane begin");
        return RpcResultBuilder.success(cancelCfgChangeOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<ConfirmCfgChangeOutput>> confirmCfgChange(ConfirmCfgChangeInput input) {
        ConfirmCfgChangeOutputBuilder confirmCfgChangeOutputBuilder = new ConfirmCfgChangeOutputBuilder();
        LOG.info("confirmCfgChane begin");
        LOG.info(input.getCfgType());
        LOG.info(input.getRouterId());
        confirmCfgChangeOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("confirmCfgChane begin");
        return RpcResultBuilder.success(confirmCfgChangeOutputBuilder.build()).buildFuture();
    }

    private void commandChangeBuild(CommandBuilder commandBuilder, List<String> commandList) {
        List<CommandChange> commandChangeList = new ArrayList<>();
        for(int i = 0 ; i < commandList.size() ; i++) {
            CommandChangeBuilder commandChangeBuilder = new CommandChangeBuilder();
            commandChangeBuilder.setIndex("" + i);
            commandChangeBuilder.setChange(commandList.get(i));
            commandChangeList.add(commandChangeBuilder.build());
        }
        commandBuilder.setCommandChange(commandChangeList);
        return;
    }

}
