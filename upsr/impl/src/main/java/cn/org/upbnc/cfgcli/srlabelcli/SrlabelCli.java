package cn.org.upbnc.cfgcli.srlabelcli;

import java.util.ArrayList;
import java.util.List;

public class SrlabelCli {
    public static List<String> srLabelCfgCli(){
        List<String> cliList = new ArrayList<>();
        cliList.add("segment-routing");
        cliList.add(" ipv4 adjacency local-ip-addr 34.1.1.2 remote-ip-addr 34.1.1.1 sid 322008");
        cliList.add(" ipv4 adjacency local-ip-addr 34.1.2.2 remote-ip-addr 34.1.2.1 sid 322010");
        cliList.add(" ipv4 adjacency local-ip-addr 24.1.1.2 remote-ip-addr 24.1.1.1 sid 322012");
        return cliList;
    }
}
