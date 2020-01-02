package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.ActionCfgApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.entity.CommandLine;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.service.entity.actionCfg.CheckPointInfoServiceEntity;
import cn.org.upbnc.service.entity.actionCfg.PointChangeInfoServiceEntity;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.routers.Router;
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
    public Future<RpcResult<VpnOutput>> vpn(VpnInput input) {
        VpnOutputBuilder vpnOutputBuilder = new VpnOutputBuilder();
        List<String> routers = new ArrayList<>();
        for (Router router : input.getRouter()) {
            routers.add(router.getRouterId());
        }
        Map<String, Object> resultMap
                = this.getActionCfgApi().vpn(routers);
        vpnOutputBuilder.setResult((String) resultMap.get(ResponseEnum.BODY.getName()));
        return RpcResultBuilder.success(vpnOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<LableOutput>> lable(LableInput input) {
        LableOutputBuilder lableOutputBuilder = new LableOutputBuilder();
        List<String> routers = new ArrayList<>();
        for (Router router : input.getRouter()) {
            routers.add(router.getRouterId());
        }
        Map<String, Object> resultMap
                = this.getActionCfgApi().lable(routers);
        lableOutputBuilder.setResult((String) resultMap.get(ResponseEnum.BODY.getName()));
        return RpcResultBuilder.success(lableOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<TunnelOutput>> tunnel(TunnelInput input) {
        TunnelOutputBuilder tunnelOutputBuilder = new TunnelOutputBuilder();
        List<String> routers = new ArrayList<>();
        for (Router router : input.getRouter()) {
            routers.add(router.getRouterId());
        }
        Map<String, Object> resultMap
                = this.getActionCfgApi().tunnel(routers);
        tunnelOutputBuilder.setResult((String) resultMap.get(ResponseEnum.BODY.getName()));
        return RpcResultBuilder.success(tunnelOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<ConfirmOutput>> confirm(ConfirmInput input) {
        ConfirmOutputBuilder confirmOutputBuilder = new ConfirmOutputBuilder();
        List<String> routers = new ArrayList<>();
        for (Router router : input.getRouter()) {
            routers.add(router.getRouterId());
        }
        Map<String, Object> resultMap
                = this.getActionCfgApi().confirm(routers);
        confirmOutputBuilder.setResult((String) resultMap.get(ResponseEnum.BODY.getName()));
        return RpcResultBuilder.success(confirmOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<CancelOutput>> cancel(CancelInput input) {
        CancelOutputBuilder cancelOutputBuilder = new CancelOutputBuilder();
        List<String> routers = new ArrayList<>();
        for (Router router : input.getRouter()) {
            routers.add(router.getRouterId());
        }
        Map<String, Object> resultMap
                = this.getActionCfgApi().cancel(routers);
        cancelOutputBuilder.setResult((String) resultMap.get(ResponseEnum.BODY.getName()));
        return RpcResultBuilder.success(cancelOutputBuilder.build()).buildFuture();
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
    public Future<RpcResult<GetCfgChangeOutput>> getCfgChange(GetCfgChangeInput input) {
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
        this.actionCfgApi.cancelCfgChane(input.getRouterId(), input.getCfgType());
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
        this.actionCfgApi.commitCfgChane(input.getRouterId(), input.getCfgType());
        confirmCfgChangeOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        LOG.info("confirmCfgChane end");
        return RpcResultBuilder.success(confirmCfgChangeOutputBuilder.build()).buildFuture();
    }


    private void commandChangeBuild(CommandBuilder commandBuilder, List<String> commandList) {
        List<CommandChange> commandChangeList = new ArrayList<>();
        for (int i = 0; i < commandList.size(); i++) {
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
        Map<String, Object> resultMap;
        if (input == null) {
            resultMap = this.getActionCfgApi().getCfgCommitPointInfo(null, null);
        } else {
            resultMap = this.getActionCfgApi().getCfgCommitPointInfo(input.getRouterId(), input.getCommitId());
        }
        Map<String, List<CheckPointInfoServiceEntity>> checkInfoMap =
                (Map<String, List<CheckPointInfoServiceEntity>>) resultMap.get(ResponseEnum.BODY.getName());
        List<CommitRouters> commitRoutersList = new ArrayList<>();
        for (String key : checkInfoMap.keySet()) {
            CommitRoutersBuilder commitRoutersBuilder = new CommitRoutersBuilder();
            commitRoutersBuilder.setRouterId(key);
            List<CommitInfo> commitInfoList = new ArrayList<>();
            List<CheckPointInfoServiceEntity> checkPointInfoServiceEntityList = checkInfoMap.get(key);
            for (CheckPointInfoServiceEntity checkPointInfoServiceEntity : checkPointInfoServiceEntityList) {
                CommitInfoBuilder commitInfoBuilder = new CommitInfoBuilder();
                commitInfoBuilder.setCommitId(checkPointInfoServiceEntity.getCommitId());
                commitInfoBuilder.setUserName(checkPointInfoServiceEntity.getUserName());
                commitInfoBuilder.setUserLabel(checkPointInfoServiceEntity.getUserLabel());
                commitInfoBuilder.setTimeStamp(checkPointInfoServiceEntity.getTimeStamp());
                List<CurrentChanges> currentChangesList = new ArrayList<>();
                for (PointChangeInfoServiceEntity pointChangeInfoServiceEntity : checkPointInfoServiceEntity.getCurList()) {
                    CurrentChangesBuilder currentChangesBuilder = new CurrentChangesBuilder();
                    currentChangesBuilder.setIndex(pointChangeInfoServiceEntity.getIndex());
                    currentChangesBuilder.setChange(pointChangeInfoServiceEntity.getChange());
                    currentChangesList.add(currentChangesBuilder.build());
                }
                commitInfoBuilder.setCurrentChanges(currentChangesList);
                List<SinceChanges> sinceChangesList = new ArrayList<>();
                for (PointChangeInfoServiceEntity pointChangeInfoServiceEntity : checkPointInfoServiceEntity.getSinceList()) {
                    SinceChangesBuilder sinceChangesBuilder = new SinceChangesBuilder();
                    sinceChangesBuilder.setIndex(pointChangeInfoServiceEntity.getIndex());
                    sinceChangesBuilder.setChange(pointChangeInfoServiceEntity.getChange());
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
        RollbackOutputBuilder rollbackOutputBuilder = new RollbackOutputBuilder();
        input.getRouterId();
        input.getCommitId();
        Map<String, Object> resultMap
                = this.actionCfgApi.rollbackToCommitId(input.getRouterId(), input.getCommitId());
        rollbackOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        return RpcResultBuilder.success(rollbackOutputBuilder.build()).buildFuture();
    }
}
