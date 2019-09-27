package cn.org.upbnc.cfgcli.srlabelcli;

import cn.org.upbnc.entity.CommandLine;
import cn.org.upbnc.xmlcompare.ActionEntity;
import cn.org.upbnc.xmlcompare.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SrlabelCli {
    private static final Logger LOG = LoggerFactory.getLogger(SrlabelCli.class);
    public static List<String> srLabelCfgCliTest(){
        List<String> cliList = new ArrayList<>();
        cliList.add("segment-routing");
        cliList.add(" ipv4 adjacency local-ip-addr 34.1.1.2 remote-ip-addr 34.1.1.1 sid 322008");
        cliList.add(" ipv4 adjacency local-ip-addr 34.1.2.2 remote-ip-addr 34.1.2.1 sid 322010");
        cliList.add(" ipv4 adjacency local-ip-addr 24.1.1.2 remote-ip-addr 24.1.1.1 sid 322012");
        return cliList;
    }
    public static List<String> srLabelCfgCli(String candidateCfg, String runningCfg){

        List<String> cliList = new ArrayList<>();
        ActionEntity actionEntity = XmlUtils.compare(candidateCfg, runningCfg);
        LOG.info(actionEntity.getPath());
        return cliList;
    }
}
