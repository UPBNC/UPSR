package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.service.entity.actionCfg.CheckPointInfoServiceEntity;

import java.util.List;
import java.util.Map;

public interface ActionCfgService {
    boolean setBaseInterface(BaseInterface baseInterface);
    Map<String, Object> vpn(List<String> routers);
    Map<String, Object> lable(List<String> routers);
    Map<String, Object> tunnel(List<String> routers);
    Map<String, Object> confirm(List<String> routers);
    Map<String, Object> cancel(List<String> routers);
    Map<String, Object> getCfgChane(String routerId, String cfgType);
    Map<String, Object> commitCfgChane(String routerId, String cfgType);
    Map<String, Object> cancelCfgChane(String routerId, String cfgType);
    Map<String, List<CheckPointInfoServiceEntity>> getCfgCommitPointInfo(String routerId, String commitId);
    Map<String, Object> rollbackToCommitId(String routerId, String commitId);
}
