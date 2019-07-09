package cn.org.upbnc.xmlcompare;

import cn.org.upbnc.util.netconf.NetconfSrLabelInfo;

import java.util.List;

public class NodeLabeTest {
    public static void test() {
        String xml1 = Rpc.getStart("1") + NodeLabelUtils.modify() + Rpc.getEnd();
        String xml2 = Rpc.getStart("2") + NodeLabelUtils.running() + Rpc.getEnd();
        xml1 = XmlUtils.subString(xml1);
        xml2 = XmlUtils.subString(xml2);
        //xml1 candidate  xml2 running
        String flag = "explicitPath";
        ActionEntity actionEntity = XmlUtils.compare(xml1, xml2, flag);
        if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
            System.out.println("add");
            NetconfSrLabelInfo netconfSrLabelInfo = GetXml.getSrNodeLabelFromgSrNodeLabelXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            System.out.println(netconfSrLabelInfo);
        } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
            System.out.println("delete");
            NetconfSrLabelInfo netconfSrLabelInfo = GetXml.getSrNodeLabelFromgSrNodeLabelXml(xml2, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            System.out.println(netconfSrLabelInfo);
        } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
            NetconfSrLabelInfo netconfSrLabelInfo = GetXml.getSrNodeLabelFromgSrNodeLabelXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            System.out.println(netconfSrLabelInfo.getPrefixIfName());
            List<ModifyEntity> modifyEntities = actionEntity.getModifyEntities();
            for (ModifyEntity modifyEntity : modifyEntities) {
                System.out.println("lable :" + modifyEntity.getLabel());
                System.out.println("modifyEntity.getOdlValue() :" + modifyEntity.getOdlValue());
                System.out.println("modifyEntity.getNewValue() :" + modifyEntity.getNewValue());
            }
        } else if (ActionTypeEnum.identical.name().equals(actionEntity.getAction().name())) {
            System.out.println("modifyEntity action() :" + actionEntity.getAction());
        }
    }
}
