package cn.org.upbnc.xmlcompare;

import cn.org.upbnc.util.netconf.SSrTeTunnel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class XMLCompareTest {
    private static final Logger LOG = LoggerFactory.getLogger(XMLCompareTest.class);
    public static void test() {
        String xml1 = Util.candidate();
        String xml2 = Util.modify();
        ActionEntity actionEntity = XmlUtils.compare(xml1, xml2);
        if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
            LOG.info("add");
            List<SSrTeTunnel> sSrTeTunnels = GetXml.getSrTeTunnelFromXml(xml1, AttributeParse.parse(actionEntity.getPath()));
            if (sSrTeTunnels.size() != 0) {
                LOG.info(sSrTeTunnels.get(0).toString());
            }
        } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
            LOG.info("delete");
            List<SSrTeTunnel> sSrTeTunnels = GetXml.getSrTeTunnelFromXml(xml2, AttributeParse.parse(actionEntity.getPath()));
            if (sSrTeTunnels.size() != 0) {
                LOG.info(sSrTeTunnels.get(0).toString());
            }
        } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
            List<SSrTeTunnel> sSrTeTunnels = GetXml.getSrTeTunnelFromXml(xml1, AttributeParse.parse(actionEntity.getPath()));
            LOG.info("modify");
            if (sSrTeTunnels.size() != 0) {
                LOG.info(sSrTeTunnels.get(0).getTunnelName());
                List<ModifyEntity> modifyEntities = actionEntity.getModifyEntities();
                for (ModifyEntity modifyEntity : modifyEntities) {
                    LOG.info("lable :" + modifyEntity.getLabel());
                    LOG.info("modifyEntity.getOdlValue() :" + modifyEntity.getOdlValue());
                    LOG.info("modifyEntity.getNewValue() :" + modifyEntity.getNewValue());
                }
            }
        }
    }
}
