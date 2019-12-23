package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.ActionCfgApi;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.service.ActionCfgService;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.entity.actionCfg.CheckPointInfoServiceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionCfgApiImpl implements ActionCfgApi {
    private static final Logger LOG = LoggerFactory.getLogger(ActionCfgApiImpl.class);
    private ServiceInterface serviceInterface;
    private ActionCfgService actionCfgService;
    private static ActionCfgApi instance = new ActionCfgApiImpl();

    public static ActionCfgApi getInstance() {
        return instance;
    }

    @Override
    public Map<String, Object> getCfgChane(String routerId, String cfgType) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap = actionCfgService.getCfgChane(routerId, cfgType);
        return resultMap;
    }

    @Override
    public Map<String, Object> commitCfgChane(String routerId, String cfgType) {
        Map<String, Object> resultMap = actionCfgService.commitCfgChane(routerId, cfgType);
        return resultMap;
    }

    @Override
    public Map<String, Object> cancelCfgChane(String routerId, String cfgType) {
        Map<String, Object> resultMap = actionCfgService.cancelCfgChane(routerId, cfgType);
        return resultMap;
    }

    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        boolean ret = true;
        try {
            if (this.serviceInterface == null) {
                this.serviceInterface = serviceInterface;
                this.actionCfgService = serviceInterface.getActionCfgService();
            }
        } catch (Exception e) {
            ret = false;
            LOG.info(e.toString());
        }
        return ret;
    }

    @Override
    public Map<String, Object> vpn(List<String> routers) {
        return actionCfgService.vpn(routers);
    }

    @Override
    public Map<String, Object> lable(List<String> routers) {
        return actionCfgService.lable(routers);
    }

    @Override
    public Map<String, Object> tunnel(List<String> routers) {
        return actionCfgService.tunnel(routers);
    }

    @Override
    public Map<String, Object> confirm(List<String> routers) {
        return actionCfgService.confirm(routers);
    }

    @Override
    public Map<String, Object> cancel(List<String> routers) {
        return actionCfgService.cancel(routers);
    }

    @Override
    public Map<String, Object> getCfgCommitPointInfo(String routerId, String commitId) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, List<CheckPointInfoServiceEntity>> checkInfoMap = actionCfgService.getCfgCommitPointInfo(routerId, commitId);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(), checkInfoMap);
        return resultMap;
    }

    @Override
    public Map<String, Object> rollbackToCommitId(String routerId, String commitId) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> rollbackRet = actionCfgService.rollbackToCommitId(routerId, commitId);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(), rollbackRet);
        return resultMap;
    }
}
