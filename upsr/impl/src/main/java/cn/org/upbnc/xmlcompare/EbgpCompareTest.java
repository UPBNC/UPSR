package cn.org.upbnc.xmlcompare;


import cn.org.upbnc.util.netconf.bgp.BgpVrf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EbgpCompareTest {
    private static final Logger LOG = LoggerFactory.getLogger(EbgpCompareTest.class);

    public static void test() {
        String xml1 = EbgpUtils.modify();
        String xml2 = EbgpUtils.running();
        //xml1 candidate  xml2 running
        String flag = "explicitPath";
        xml1 = XmlUtils.subString(xml1);
        xml2 = XmlUtils.subString(xml2);
        ActionEntity actionEntity = XmlUtils.compare(xml1, xml2, flag);
        if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
            LOG.info("add");
            List<BgpVrf> bgpVrfs = GetXml.getEbgpFromXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            if (bgpVrfs.size() != 0) {
                LOG.info(bgpVrfs.get(0).toString());
            }
        } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
            LOG.info("delete");
            List<BgpVrf> bgpVrfs = GetXml.getEbgpFromXml(xml2, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            if (bgpVrfs.size() != 0) {
                LOG.info(bgpVrfs.get(0).toString());
            }
        } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
            List<BgpVrf> bgpVrfs = GetXml.getEbgpFromXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            LOG.info("modify");
            if (bgpVrfs.size() != 0) {
                LOG.info(bgpVrfs.get(0).getVrfName());
                List<ModifyEntity> modifyEntities = actionEntity.getModifyEntities();
                for (ModifyEntity modifyEntity : modifyEntities) {
                    LOG.info("lable :" + modifyEntity.getLabel());
                    LOG.info("modifyEntity.getOdlValue() :" + modifyEntity.getOdlValue());
                    LOG.info("modifyEntity.getNewValue() :" + modifyEntity.getNewValue());
                }
            }
        } else if (ActionTypeEnum.identical.name().equals(actionEntity.getAction().name())) {
            LOG.info("modifyEntity action() :" + actionEntity.getAction());
        }
    }
}
