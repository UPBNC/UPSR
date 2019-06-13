package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;

import java.util.Map;

public interface ActionCfgService {
    boolean setBaseInterface(BaseInterface baseInterface);
    Map<String, Object> getCfgChane(String routerId, String cfgType);
}
