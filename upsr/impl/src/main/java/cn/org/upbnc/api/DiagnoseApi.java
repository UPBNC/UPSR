package cn.org.upbnc.api;

import cn.org.upbnc.service.ServiceInterface;

import java.util.Map;

public interface DiagnoseApi {
    boolean setServiceInterface(ServiceInterface serviceInterface);
    Map<String, Object> getDiagnoseTunnelInfo(String routerId);
    Map<String, Object> getDiagnoseVpndownInfo(String routerId);
}
