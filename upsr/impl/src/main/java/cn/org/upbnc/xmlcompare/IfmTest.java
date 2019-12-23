package cn.org.upbnc.xmlcompare;
import java.util.List;

public class IfmTest {
    public static void test() {
        String xml1 = Rpc.getStart("1") + IfmUtils.delete() + Rpc.getEnd();
        String xml2 = Rpc.getStart("2") + IfmUtils.modify() + Rpc.getEnd();
        xml1 = XmlUtils.subString(xml1);
        xml2 = XmlUtils.subString(xml2);
        //xml1 candidate  xml2 running
        String flag = "explicitPath";
        ActionEntity actionEntity = XmlUtils.compare(xml1, xml2, flag);
        if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
            System.out.println("add");
            List<Interface> interfaces = GetXml.getInterface(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            System.out.println(interfaces.get(0).toString());
        } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
            System.out.println("delete");
            List<Interface> interfaces = GetXml.getInterface(xml2, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            System.out.println(interfaces);
        } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
            List<ModifyEntity> modifyEntities = actionEntity.getModifyEntities();
            for (ModifyEntity modifyEntity : modifyEntities) {
                List<Interface> interfaces = GetXml.getInterface(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                System.out.println(interfaces.get(0).toString());
                System.out.println("lable :" + modifyEntity.getLabel());
                System.out.println("modifyEntity.getOdlValue() :" + modifyEntity.getOdlValue());
                System.out.println("modifyEntity.getNewValue() :" + modifyEntity.getNewValue());
            }
        } else if (ActionTypeEnum.identical.name().equals(actionEntity.getAction().name())) {
            System.out.println("modifyEntity action() :" + actionEntity.getAction());
        }
    }
}
