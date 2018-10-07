package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;

public interface TunnelPolicyService {
    boolean setBaseInterface(BaseInterface baseInterface);
    boolean syncTunnelPolicyConf();
}
