package cn.org.upbnc.xmlcompare;

import cn.org.upbnc.util.netconf.SSrTeTunnel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class XMLCompareTest {
    private static final Logger LOG = LoggerFactory.getLogger(XMLCompareTest.class);

    public static void test() {
        String xml1 = Rpc.getStart("1") + Util.modify() + Rpc.getEnd();
        String xml2 = Rpc.getStart("2") + Util.candidate() + Rpc.getEnd();
        List<SeparateEntity> separateEntities = Separate.getSeparate(xml1, xml2);
        ActionEntity actionEntity;
        for (int xy = 0; xy < separateEntities.size(); xy++) {
            actionEntity = separateEntities.get(xy).getActionEntities().get(0);
            if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
                LOG.info("add");
                List<SSrTeTunnel> sSrTeTunnels = GetXml.getSrTeTunnelFromXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                if (sSrTeTunnels.size() != 0) {
                    LOG.info(sSrTeTunnels.get(0).toString());
                }
            } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
                LOG.info("delete");
                List<SSrTeTunnel> sSrTeTunnels = GetXml.getSrTeTunnelFromXml(xml2, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                if (sSrTeTunnels.size() != 0) {
                    LOG.info(sSrTeTunnels.get(0).toString());
                }
            } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
                List<SSrTeTunnel> sSrTeTunnels = GetXml.getSrTeTunnelFromXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                LOG.info("modify");
                if (sSrTeTunnels.size() != 0) {
                    LOG.info(sSrTeTunnels.get(0).getTunnelName());
                    List<ModifyEntity> modifyEntities = actionEntity.getModifyEntities();
                    for (ModifyEntity modifyEntity : modifyEntities) {
                        List<Attribute> attributes = AttributeParse.parse(modifyEntity.getPath());
                        String label = attributes.get(attributes.size() - 2).getName();
                        modifyEntity.setLabel(label);
                        LOG.info("label :" + modifyEntity.getLabel());
                        LOG.info("modifyEntity.getOdlValue() :" + modifyEntity.getOdlValue());
                        LOG.info("modifyEntity.getNewValue() :" + modifyEntity.getNewValue());
                    }
                }
            } else if (ActionTypeEnum.identical.name().equals(actionEntity.getAction().name())) {
                System.out.println("modifyEntity action() :" + actionEntity.getAction());
            }
        }
    }
}
