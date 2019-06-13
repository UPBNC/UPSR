package cn.org.upbnc.api;

import cn.org.upbnc.service.ServiceInterface;

import java.util.Map;

public interface ActionCfgApi {
    boolean setServiceInterface(ServiceInterface serviceInterface);
    Map<String, Object> getCfgChane(String routerId, String cfgType);
}
