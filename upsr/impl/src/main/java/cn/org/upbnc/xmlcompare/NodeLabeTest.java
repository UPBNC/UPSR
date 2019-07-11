package cn.org.upbnc.xmlcompare;

import cn.org.upbnc.util.netconf.NetconfSrLabelInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NodeLabeTest {
    private static final Logger LOG = LoggerFactory.getLogger(NodeLabeTest.class);

    public static void test() {
        String xml1 = Rpc.getStart("1") + NodeLabelUtils.modify() + Rpc.getEnd();
        String xml2 = Rpc.getStart("2") + NodeLabelUtils.running() + Rpc.getEnd();
        xml1 = XmlUtils.subString(xml1);
        xml2 = XmlUtils.subString(xml2);
        //xml1 candidate  xml2 running
        String flag = "explicitPath";
        ActionEntity actionEntity = XmlUtils.compare(xml1, xml2, flag);
        if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
            LOG.info("add");
            NetconfSrLabelInfo netconfSrLabelInfo = GetXml.getSrNodeLabelFromgSrNodeLabelXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            LOG.info(netconfSrLabelInfo.toString());
        } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
            LOG.info("delete");
            NetconfSrLabelInfo netconfSrLabelInfo = GetXml.getSrNodeLabelFromgSrNodeLabelXml(xml2, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            LOG.info(netconfSrLabelInfo.toString());
        } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
            NetconfSrLabelInfo netconfSrLabelInfo = GetXml.getSrNodeLabelFromgSrNodeLabelXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            LOG.info(netconfSrLabelInfo.getPrefixIfName());
            List<ModifyEntity> modifyEntities = actionEntity.getModifyEntities();
            for (ModifyEntity modifyEntity : modifyEntities) {
                LOG.info("lable :" + modifyEntity.getLabel());
                LOG.info("modifyEntity.getOdlValue() :" + modifyEntity.getOdlValue());
                LOG.info("modifyEntity.getNewValue() :" + modifyEntity.getNewValue());
            }
        } else if (ActionTypeEnum.identical.name().equals(actionEntity.getAction().name())) {
            LOG.info("modifyEntity action() :" + actionEntity.getAction());
        }
    }
}
