package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.ActionCfgApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.entity.CommandLine;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.apache.commons.collections.list.AbstractListDecorator;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgchange.output.Command;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgchange.output.CommandBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgchange.output.command.CommandChange;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgchange.output.command.CommandChangeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgcommit.output.CommitRouters;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgcommit.output.CommitRoutersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgcommit.output.commitrouters.CommitInfo;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgcommit.output.commitrouters.CommitInfoBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgcommit.output.commitrouters.commitinfo.CurrentChanges;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgcommit.output.commitrouters.commitinfo.CurrentChangesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgcommit.output.commitrouters.commitinfo.SinceChanges;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.getcfgcommit.output.commitrouters.commitinfo.SinceChangesBuilder;
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
    private static final Logger LOG = LoggerFactory.getLogger(ActionCfgODLApi.class);
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
            cfgChaneRet = this.actionCfgApi.getCfgChane(null, null);
        } else {
            cfgChaneRet = this.actionCfgApi.getCfgChane(input.getRouterId(), input.getCfgType());
        }
        if (cfgChaneRet.get(ResponseEnum.CODE.getName()) == CodeEnum.SUCCESS.getName()) {
            List<Command> commandList = new ArrayList<>();
            List<CommandLine> commandLineList = (List<CommandLine>) cfgChaneRet.get(ResponseEnum.MESSAGE.getName());
            for (CommandLine c : commandLineList) {
                CommandBuilder commandBuilder = new CommandBuilder();
                commandBuilder.setDeviceName(c.getDeviceName());
                commandBuilder.setRouterId(c.getRouterId());
                this.commandChangeBuild(commandBuilder, c.getCliList());
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
        this.actionCfgApi.cancelCfgChane(input.getRouterId(),input.getCfgType());
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
        this.actionCfgApi.commitCfgChane(input.getRouterId(),input.getCfgType());
        confirmCfgChangeOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("confirmCfgChane end");
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

    @Override
    public Future<RpcResult<GetCfgCommitOutput>> getCfgCommit(GetCfgCommitInput input) {
        LOG.info("getCfgCommit begin");
        GetCfgCommitOutputBuilder getCfgCommitOutputBuilder = new GetCfgCommitOutputBuilder();
        getCfgCommitOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        if (input != null) {
            this.actionCfgApi.getCfgCommitPointInfo(input.getRouterId(), input.getCommitId());
        }
        List<CommitRouters> commitRoutersList = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            CommitRoutersBuilder commitRoutersBuilder = new CommitRoutersBuilder();
            commitRoutersBuilder.setRouterId("1.1.1." + i);
            List<CommitInfo> commitInfoList = new ArrayList<>();
            for (int j = 1; j < 5; j++) {
                CommitInfoBuilder commitInfoBuilder = new CommitInfoBuilder();
                commitInfoBuilder.setCommitId("" + j);
                commitInfoBuilder.setUserName("user" + j);
                commitInfoBuilder.setUserLabel("label_" + j);
                commitInfoBuilder.setTimeStamp("2012-04-23 11:11:2" + j);
                List<CurrentChanges> currentChangesList = new ArrayList<>();
                for (int k = 1; k < 5; k++) {
                    CurrentChangesBuilder currentChangesBuilder = new CurrentChangesBuilder();
                    currentChangesBuilder.setIndex("1" + k);
                    currentChangesBuilder.setChange("ip address unnumbered interface LoopBack0");
                    currentChangesList.add(currentChangesBuilder.build());
                }
                commitInfoBuilder.setCurrentChanges(currentChangesList);
                List<SinceChanges> sinceChangesList = new ArrayList<>();
                for (int k = 1; k < 5; k++) {
                    SinceChangesBuilder sinceChangesBuilder = new SinceChangesBuilder();
                    sinceChangesBuilder.setIndex("2" + k);
                    sinceChangesBuilder.setChange("tunnel-protocol mpls te");
                    sinceChangesList.add(sinceChangesBuilder.build());
                }
                commitInfoBuilder.setSinceChanges(sinceChangesList);
                commitInfoList.add(commitInfoBuilder.build());
            }
            commitRoutersBuilder.setCommitInfo(commitInfoList);
            commitRoutersList.add(commitRoutersBuilder.build());
        }
        getCfgCommitOutputBuilder.setCommitRouters(commitRoutersList);
        LOG.info("getCfgCommit end");
        return RpcResultBuilder.success(getCfgCommitOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<RollbackOutput>> rollback(RollbackInput input) {
        return null;
    }
}
