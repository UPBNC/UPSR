package cn.org.upbnc.xmlcompare;


import cn.org.upbnc.util.netconf.SBfdCfgSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BfdTest {
    private static final Logger LOG = LoggerFactory.getLogger(EbgpCompareTest.class);

    public static void test() {
        String xml1 = BfdUtils.running();
        String xml2 = BfdUtils.add();
        //xml1 candidate  xml2 running
        String flag = "explicitPath";
        xml1 = XmlUtils.subString(xml1);
        xml2 = XmlUtils.subString(xml2);
        ActionEntity actionEntity = XmlUtils.compare(xml1, xml2, flag);
        if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
            LOG.info("add");
            List<SBfdCfgSession> sBfdCfgSessions = GetXml.getBfdCfgSessionsFromXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            if (sBfdCfgSessions.size() != 0) {
                LOG.info(sBfdCfgSessions.get(0).toString());
            }
        } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
            LOG.info("delete");
            List<SBfdCfgSession> sBfdCfgSessions = GetXml.getBfdCfgSessionsFromXml(xml2, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            if (sBfdCfgSessions.size() != 0) {
                LOG.info(sBfdCfgSessions.get(0).toString());
            }
        } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
            List<SBfdCfgSession> sBfdCfgSessions = GetXml.getBfdCfgSessionsFromXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            LOG.info("modify");
            if (sBfdCfgSessions.size() != 0) {
                LOG.info(sBfdCfgSessions.get(0).getSessName());
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
