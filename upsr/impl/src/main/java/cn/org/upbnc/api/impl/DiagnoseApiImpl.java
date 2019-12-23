package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.DiagnoseApi;
import cn.org.upbnc.service.DiagnoseService;
import cn.org.upbnc.service.ServiceInterface;

import java.util.Map;

public class DiagnoseApiImpl implements DiagnoseApi {
    private ServiceInterface serviceInterface;
    private DiagnoseService diagnoseService;
    public static DiagnoseApiImpl ourInstance = new DiagnoseApiImpl();

    public static DiagnoseApiImpl getInstance() {
        return ourInstance;
    }

    public DiagnoseApiImpl() {
        this.serviceInterface = null;
    }

    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        this.serviceInterface = serviceInterface;
        if (serviceInterface != null) {
            this.diagnoseService = serviceInterface.getDiagnoseService();
        }
        return true;
    }

    @Override
    public Map<String, Object> getDiagnoseTunnelInfo(String routerId) {
        return this.diagnoseService.getDiagnoseTunnelInfo(routerId);
    }

    @Override
    public Map<String, Object> getDiagnoseVpndownInfo(String routerId) {
        return this.diagnoseService.getDiagnoseVpndownInfo(routerId);
    }
}
