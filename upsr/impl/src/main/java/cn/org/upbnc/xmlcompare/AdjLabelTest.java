package cn.org.upbnc.xmlcompare;


import cn.org.upbnc.entity.AdjLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AdjLabelTest {
    private static final Logger LOG = LoggerFactory.getLogger(AdjLabelTest.class);

    public static void test() {
        String xml1 = Rpc.getStart("1") + AdjLabelUtils.modify() + Rpc.getEnd();
        String xml2 = Rpc.getStart("2") + AdjLabelUtils.running() + Rpc.getEnd();
        xml1 = XmlUtils.subString(xml1);
        xml2 = XmlUtils.subString(xml2);
        //xml1 candidate  xml2 running
        String flag = "explicitPath";
        ActionEntity actionEntity = XmlUtils.compare(xml1, xml2, flag);
        if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
            LOG.info("add");
            List<AdjLabel> adjLabels = GetXml.getSrAdjLabelFromSrAdjLabelXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            if (adjLabels.size() != 0) {
                LOG.info(adjLabels.get(0).toString());
                LOG.info(adjLabels.get(0).getValue().toString());
            }
        } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
            LOG.info("delete");
            List<AdjLabel> adjLabels = GetXml.getSrAdjLabelFromSrAdjLabelXml(xml2, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            if (adjLabels.size() != 0) {
                LOG.info(adjLabels.get(0).toString());
                LOG.info(adjLabels.get(0).getValue().toString());
            }
        } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
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
