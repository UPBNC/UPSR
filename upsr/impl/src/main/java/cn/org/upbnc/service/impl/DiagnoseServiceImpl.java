package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.service.DiagnoseService;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DiagnoseServiceImpl implements DiagnoseService {
    private static DiagnoseServiceImpl ourInstance = new DiagnoseServiceImpl();
    private DeviceManager deviceManager;
    private BaseInterface baseInterface;
    public DiagnoseServiceImpl() {
        deviceManager = null;
    }

    public static DiagnoseServiceImpl getInstance() {
        return ourInstance;
    }
    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        if (null != baseInterface) {
            this.deviceManager = this.baseInterface.getDeviceManager();
        }
        return true;
    }

    @Override
    public Map<String, Object> getDiagnoseTunnelInfo(String routerId) {
        Map<String, Object> resultMap = new HashMap<>();
        Device device = deviceManager.getDevice(routerId);
        String diagnoseTunnel = getDiagnoseInfoByScriptFile(
                "python diagnose/code/diagnose_pexpect.py --cmdfile diagnose/cmd/tunnel_down.txt" +
                        " --routerId " + routerId + " --deviceName <" + device.getDeviceName() + ">");
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(), diagnoseTunnel);
        return resultMap;
    }

    @Override
    public Map<String, Object> getDiagnoseVpndownInfo(String routerId) {
        Map<String, Object> resultMap = new HashMap<>();
        Device device = deviceManager.getDevice(routerId);
        String diagnoseVpn = getDiagnoseInfoByScriptFile(
                " python diagnose/code/diagnose_pexpect.py --cmdfile diagnose/cmd/vpn_down.txt" +
                        " --routerId " + routerId + " --deviceName <" + device.getDeviceName() + ">");
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
            String fileName = in.readLine();
            in.close();
            proc.waitFor();
            if (fileName != null) {
                File file = new File(fileName);
                if (file.isFile() && file.exists()) {
                    InputStreamReader is = new InputStreamReader(new FileInputStream(file), "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(is);
                    String lineTxt;
                    while ((lineTxt = bufferedReader.readLine()) != null) {
                        diagnoseInfo = diagnoseInfo + lineTxt + "\n";
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return diagnoseInfo;
    }
}
