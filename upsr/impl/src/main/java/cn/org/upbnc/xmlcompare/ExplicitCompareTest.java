package cn.org.upbnc.xmlcompare;

import cn.org.upbnc.util.netconf.SExplicitPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ExplicitCompareTest {
    private static final Logger LOG = LoggerFactory.getLogger(ExplicitCompareTest.class);

    public static void test() {
        String xml1 = ExplicitPathUtils.modify();
        String xml2 = ExplicitPathUtils.running();
        String flag = "explicitPath";
        ActionEntity actionEntity = XmlUtils.compare(xml1, xml2, flag);
        if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
            LOG.info("add");
            List<SExplicitPath> sExplicitPaths = GetXml.getExplicitPathFromXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            if (sExplicitPaths.size() != 0) {
                LOG.info(sExplicitPaths.get(0).toString());
            }
        } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
            LOG.info("delete");
            List<SExplicitPath> sExplicitPaths = GetXml.getExplicitPathFromXml(xml2, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            if (sExplicitPaths.size() != 0) {
                LOG.info(sExplicitPaths.get(0).toString());
            }
        } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
            List<SExplicitPath> sExplicitPaths = GetXml.getExplicitPathFromXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
            LOG.info("modify");
            if (sExplicitPaths.size() != 0) {
                LOG.info(sExplicitPaths.get(0).getExplicitPathName());
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
