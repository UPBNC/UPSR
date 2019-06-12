package cn.org.upbnc.xmlcompare;

import cn.org.upbnc.util.netconf.SSrTeTunnel;

import java.util.List;

public class XMLCompareTest {
    public static void test() {
        String xml1 = Util.candidate();
        String xml2 = Util.modify();
        ActionEntity actionEntity = XmlUtils.compare(xml1, xml2);
        System.out.println(actionEntity.getPath());
        if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
            System.out.println("add");
            List<SSrTeTunnel> sSrTeTunnels = GetXml.getSrTeTunnelFromXml(xml1, AttributeParse.parse(actionEntity.getPath()));
            if (sSrTeTunnels.size() != 0) {
                System.out.println(sSrTeTunnels.get(0).toString());
            }
        } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
            System.out.println("delete");
            List<SSrTeTunnel> sSrTeTunnels = GetXml.getSrTeTunnelFromXml(xml2, AttributeParse.parse(actionEntity.getPath()));
            if (sSrTeTunnels.size() != 0) {
                System.out.println(sSrTeTunnels.get(0).toString());
            }
        } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
            List<SSrTeTunnel> sSrTeTunnels = GetXml.getSrTeTunnelFromXml(xml1, AttributeParse.parse(actionEntity.getPath()));
            System.out.println("modify");
            if (sSrTeTunnels.size() != 0) {
                System.out.println(sSrTeTunnels.get(0).getTunnelName());
                List<ModifyEntity> modifyEntities = actionEntity.getModifyEntities();
                for (ModifyEntity modifyEntity : modifyEntities) {
                    System.out.println("lable :" + modifyEntity.getLabel());
                    System.out.println("modifyEntity.getOdlValue() :" + modifyEntity.getOdlValue());
                    System.out.println("modifyEntity.getNewValue() :" + modifyEntity.getNewValue());
                }
            }
        }
    }
}
