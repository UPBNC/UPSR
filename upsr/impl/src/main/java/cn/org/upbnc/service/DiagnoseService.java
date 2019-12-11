package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;

import java.util.Map;

public interface DiagnoseService {
    boolean setBaseInterface(BaseInterface baseInterface);
    Map<String, Object> getDiagnoseTunnelInfo(String routerId);
    Map<String, Object> getDiagnoseVpndownInfo(String routerId);
}
