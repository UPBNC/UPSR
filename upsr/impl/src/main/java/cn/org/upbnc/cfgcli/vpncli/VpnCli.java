package cn.org.upbnc.cfgcli.vpncli;

import cn.org.upbnc.xmlcompare.ActionEntity;
import cn.org.upbnc.xmlcompare.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class VpnCli {
    private static final Logger LOG = LoggerFactory.getLogger(VpnCli.class);
    public static List<String> vpnCfgCli(String candidateCfg, String runningCfg) {
        List<String> cliList = new ArrayList<>();
        ActionEntity actionEntity = XmlUtils.compare(candidateCfg, runningCfg);
        LOG.info(actionEntity.getPath());
        return cliList;
    }
}
