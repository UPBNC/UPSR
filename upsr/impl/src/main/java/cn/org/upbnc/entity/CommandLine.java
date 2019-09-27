package cn.org.upbnc.entity;

import java.util.ArrayList;
import java.util.List;

public class CommandLine {
    String routerId;
    String deviceName;
    List<String> cliList;

    public CommandLine() {
        this.cliList = new ArrayList<String>();
    }

    public String getRouterId() {
        return routerId;
    }

    public void setRouterId(String routerId) {
        this.routerId = routerId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public List<String> getCliList() {
        return cliList;
    }

    public void setCliList(List<String> cliList) {
        this.cliList = cliList;
    }
}
