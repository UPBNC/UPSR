package cn.org.upbnc.cfgcli.tunnelcli;

import cn.org.upbnc.util.netconf.SSrTeTunnel;
import cn.org.upbnc.xmlcompare.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class TunnelCli {
    private static final Logger LOG = LoggerFactory.getLogger(TunnelCli.class);
    public static String tunnelCfgCli(String xml1,String xml2){
        String cli = "";

        ActionEntity actionEntity = XmlUtils.compare(xml1, xml2);
        LOG.info(actionEntity.getPath());
        if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
            LOG.info("add");
        } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
            LOG.info("delete");
        } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
            List<SSrTeTunnel> sSrTeTunnels = GetXml.getSrTeTunnelFromXml(xml1, AttributeParse.parse(actionEntity.getPath()));
            LOG.info("modify");
            if (sSrTeTunnels.size() != 0) {
                cli = cli + "interface " + sSrTeTunnels.get(0).getTunnelName() + "\n";
                List<ModifyEntity> modifyEntities = actionEntity.getModifyEntities();
                for (ModifyEntity modifyEntity : modifyEntities) {
                    cli = cli + modifyEntity.getClicommand("modify");
                }
            }
        }
        return cli;
    }
}
