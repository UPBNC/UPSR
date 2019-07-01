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
//            System.out.println(separateEntities.get(xy).toString());
            actionEntity = separateEntities.get(xy).getActionEntities().get(0);
            if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
                System.out.println("add");
                List<SSrTeTunnel> sSrTeTunnels = GetXml.getSrTeTunnelFromXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                if (sSrTeTunnels.size() != 0) {
                    System.out.println(sSrTeTunnels.get(0).toString());
                }
            } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
                System.out.println("delete");
                List<SSrTeTunnel> sSrTeTunnels = GetXml.getSrTeTunnelFromXml(xml2, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                if (sSrTeTunnels.size() != 0) {
                    System.out.println(sSrTeTunnels.get(0).toString());
                }
            } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
                List<SSrTeTunnel> sSrTeTunnels = GetXml.getSrTeTunnelFromXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                System.out.println("modify");
                if (sSrTeTunnels.size() != 0) {
                    System.out.println(sSrTeTunnels.get(0).getTunnelName());
                    List<ModifyEntity> modifyEntities = actionEntity.getModifyEntities();
                    for (ModifyEntity modifyEntity : modifyEntities) {
                        List<Attribute> attributes = AttributeParse.parse(modifyEntity.getPath());
                        String label = attributes.get(attributes.size() - 2).getName();
                        modifyEntity.setLabel(label);
                        System.out.println("label :" + modifyEntity.getLabel());
                        System.out.println("modifyEntity.getOdlValue() :" + modifyEntity.getOdlValue());
                        System.out.println("modifyEntity.getNewValue() :" + modifyEntity.getNewValue());
                    }
                }
            } else if (ActionTypeEnum.identical.name().equals(actionEntity.getAction().name())) {
                System.out.println("modifyEntity action() :" + actionEntity.getAction());
            }
        }
    }
}
