package cn.org.upbnc.xmlcompare;


import cn.org.upbnc.util.netconf.L3vpnInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class VpnTest {
    private static final Logger LOG = LoggerFactory.getLogger(VpnTest.class);
    public static void test() {
        String xml1 = VpnUtils.modify();
        String xml2 = VpnUtils.running();
        //xml1 candidate  xml2 running
        xml1 = XmlUtils.subString(xml1);
        xml2 = XmlUtils.subString(xml2);
        String flag = "explicitPath";
        ActionEntity actionEntity = XmlUtils.compare(xml1, xml2, flag);
        if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
            LOG.info("add");
            List<L3vpnInstance> l3vpnInstances = GetXml.getVpnFromXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            if (l3vpnInstances.size() != 0) {
                LOG.info(l3vpnInstances.get(0).toString());
            }
        } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
            LOG.info("delete");
            List<L3vpnInstance> l3vpnInstances = GetXml.getVpnFromXml(xml2, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            if (l3vpnInstances.size() != 0) {
                LOG.info(l3vpnInstances.get(0).toString());
            }
        } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
            List<L3vpnInstance> l3vpnInstances = GetXml.getVpnFromXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            LOG.info("modify");
            if (l3vpnInstances.size() != 0) {
                LOG.info(l3vpnInstances.get(0).getVrfName());
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
