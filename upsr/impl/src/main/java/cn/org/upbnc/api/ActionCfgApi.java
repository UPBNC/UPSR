package cn.org.upbnc.api;

import cn.org.upbnc.service.ServiceInterface;

import java.util.Map;

public interface ActionCfgApi {
    boolean setServiceInterface(ServiceInterface serviceInterface);
    Map<String, Object> getCfgChane(String routerId, String cfgType);
    Map<String, Object> commitCfgChane(String routerId, String cfgType);
    Map<String, Object> cancelCfgChane(String routerId, String cfgType);
    Map<String, Object> getCfgCommitPointInfo(String routerId, String commitId);
    Map<String, Object> rollbackToCommitId(String routerId, String commitId);
}
