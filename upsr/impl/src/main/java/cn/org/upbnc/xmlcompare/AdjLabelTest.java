package cn.org.upbnc.xmlcompare;



import cn.org.upbnc.entity.AdjLabel;

import java.util.List;

public class AdjLabelTest {
    public static void main(String args[]) {
        String xml1 = Rpc.getStart("1") + AdjLabelUtils.modify() + Rpc.getEnd();
        String xml2 = Rpc.getStart("2") + AdjLabelUtils.running() + Rpc.getEnd();
        xml1 = XmlUtils.subString(xml1);
        xml2 = XmlUtils.subString(xml2);
        //xml1 candidate  xml2 running
        String flag = "explicitPath";
        ActionEntity actionEntity = XmlUtils.compare(xml1, xml2, flag);
        if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
            System.out.println("add");
            List<AdjLabel> adjLabels = GetXml.getSrAdjLabelFromSrAdjLabelXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            if (adjLabels.size() != 0) {
                System.out.println(adjLabels.get(0).toString());
                System.out.println(adjLabels.get(0).getValue());
            }
        } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
            System.out.println("delete");
            List<AdjLabel> adjLabels = GetXml.getSrAdjLabelFromSrAdjLabelXml(xml2, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            if (adjLabels.size() != 0) {
                System.out.println(adjLabels.get(0).toString());
                System.out.println(adjLabels.get(0).getValue());
            }
        } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
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
