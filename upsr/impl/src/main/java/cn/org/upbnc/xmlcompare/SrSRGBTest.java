package cn.org.upbnc.xmlcompare;

import cn.org.upbnc.util.netconf.NetconfSrLabelInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SrSRGBTest {
    private static final Logger LOG = LoggerFactory.getLogger(SrSRGBTest.class);

    public static void test() {
        String xml1 = Rpc.getStart("1") + SrSRGBUtils.modify() + Rpc.getEnd();
        String xml2 = Rpc.getStart("2") + SrSRGBUtils.running() + Rpc.getEnd();
        xml1 = XmlUtils.subString(xml1);
        xml2 = XmlUtils.subString(xml2);
        //xml1 candidate  xml2 running
        String flag = "explicitPath";
        ActionEntity actionEntity = XmlUtils.compare(xml1, xml2, flag);
        if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
            LOG.info("add");
            NetconfSrLabelInfo netconfSrLabelInfo = GetXml.getSrSRGBRangeFromXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            LOG.info(netconfSrLabelInfo.toString());
        } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
            LOG.info("delete");
            NetconfSrLabelInfo netconfSrLabelInfo = GetXml.getSrSRGBRangeFromXml(xml2, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            LOG.info(netconfSrLabelInfo.toString());
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
