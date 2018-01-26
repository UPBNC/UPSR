package cn.org.upbnc.cfgcli.tunnelcli;

import cn.org.upbnc.util.netconf.SSrTeTunnel;
import cn.org.upbnc.xmlcompare.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TunnelCli {
    private static final Logger LOG = LoggerFactory.getLogger(TunnelCli.class);

    public static List<String> tunnelCfgCliTest(String xml1, String xml2) {
        List<String> cliList = new ArrayList<>();

        ActionEntity actionEntity = XmlUtils.compare(xml1, xml2);
        LOG.info(actionEntity.getPath());
        if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
            LOG.info("add");
        } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
            LOG.info("delete");
        } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
            List<SSrTeTunnel> sSrTeTunnels = GetXml.getSrTeTunnelFromXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            LOG.info("modify");
            if (sSrTeTunnels.size() != 0) {
                cliList.add("interface " + sSrTeTunnels.get(0).getTunnelName());
                List<ModifyEntity> modifyEntities = actionEntity.getModifyEntities();
                for (ModifyEntity modifyEntity : modifyEntities) {
                    String cliStr = modifyEntity.getClicommand("modify");
                    if (cliStr != null) {
                        cliList.add(modifyEntity.getClicommand("modify"));
                    }
                }
            }
        }
        return cliList;
    }

    public static List<String> tunnelCfgCli(String candidateCfg, String runningCfg) {
        List<String> cliList = new ArrayList<>();
        ActionEntity actionEntity = XmlUtils.compare(candidateCfg, runningCfg);
        LOG.info(actionEntity.getPath());
        return cliList;
    }
}
