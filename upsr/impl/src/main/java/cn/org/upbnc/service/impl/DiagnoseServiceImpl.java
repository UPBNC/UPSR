package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.service.DiagnoseService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DiagnoseServiceImpl implements DiagnoseService {
    private static DiagnoseServiceImpl ourInstance = new DiagnoseServiceImpl();
    private BaseInterface baseInterface;
    public DiagnoseServiceImpl() {
    }

    public static DiagnoseServiceImpl getInstance() {
        return ourInstance;
    }
    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        return true;
    }

    @Override
    public Map<String, Object> getDiagnoseTunnelInfo(String routerId) {
        Map<String, Object> resultMap = new HashMap<>();
        String diagnoseTunnel = getDiagnoseInfoByScriptFile(
                "python diagnose/code/diagnose.py --cmdfile diagnose/cmd/tunnel_down.txt");
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(), diagnoseTunnel);
        return resultMap;
    }

    @Override
    public Map<String, Object> getDiagnoseVpndownInfo(String routerId) {
        Map<String, Object> resultMap = new HashMap<>();
        String diagnoseVpn = getDiagnoseInfoByScriptFile(
                " python diagnose/code/diagnose.py --cmdfile diagnose/cmd/vpn_down.txt");
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(), diagnoseVpn);
        return resultMap;
    }

    private String getDiagnoseInfoByScriptFile(String cmd) {
        String diagnoseInfo = "";
        Process proc;
        try {
            proc = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                diagnoseInfo = diagnoseInfo + line + "\n";
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return diagnoseInfo;
    }
}
