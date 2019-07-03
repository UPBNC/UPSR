package cn.org.upbnc.xmlcompare;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlUtils {
    public static ActionEntity compare(String xml1, String xml2, String flag) {
        ActionEntity actionEntity = new ActionEntity();
        Diff diff;
        try {
            diff = new Diff(xml1, xml2);
            DetailedDiff myDiff = new DetailedDiff(diff);
            List allDifferences = myDiff.getAllDifferences();
            if (myDiff.identical()) {
                actionEntity.setAction(ActionTypeEnum.identical);
            } else {
                if (allDifferences.size() > 0) {
                    Difference difference = (Difference) allDifferences.get(0);
                    if (difference.getDescription().contains("text value")) {
                        Difference difference1;
                        List<ModifyEntity> modifyEntities = new ArrayList<>();
                        ModifyEntity modifyEntity;
                        int i;
                        if ("explicitPath".equals(flag)) {
                            i = 0;
                        } else {
                            i = 1;
                        }
                        for (; i < allDifferences.size(); i++) {
                            difference1 = (Difference) allDifferences.get(i);
                            modifyEntity = new ModifyEntity();
                            modifyEntity.setPath(difference1.getControlNodeDetail().getXpathLocation());
                            List<Attribute> attributes = AttributeParse.parse(modifyEntity.getPath());
                            String lable = attributes.get(attributes.size() - 2).getName();
                            modifyEntity.setLabel(lable);
                            modifyEntity.setOdlValue(difference1.getTestNodeDetail().getValue());
                            modifyEntity.setNewValue(difference1.getControlNodeDetail().getValue());
                            modifyEntities.add(modifyEntity);
                        }
                        actionEntity.setPath(difference.getControlNodeDetail().getXpathLocation());
                        actionEntity.setModifyEntities(modifyEntities);
                        actionEntity.setAction(ActionTypeEnum.modify);
                    } else if (difference.getDescription().contains("number of child nodes")) {
                        Difference difference1 = (Difference) allDifferences.get(1);
                        if (Integer.parseInt(difference.getControlNodeDetail().getValue()) > Integer.parseInt(difference.getTestNodeDetail().getValue())) {
                            actionEntity.setPath(difference1.getControlNodeDetail().getXpathLocation());
                            actionEntity.setAction(ActionTypeEnum.add);
                        } else if (Integer.parseInt(difference.getControlNodeDetail().getValue()) < Integer.parseInt(difference.getTestNodeDetail().getValue())) {
                            actionEntity.setAction(ActionTypeEnum.delete);
                            actionEntity.setPath(difference1.getTestNodeDetail().getXpathLocation());
                        }
                    } else {
                        System.out.println("difference.getDescription() :" + difference.getDescription());
                        actionEntity.setAction(ActionTypeEnum.unknown);
                        System.out.println("others");
                    }
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return actionEntity;
    }

    public static ActionEntity compare(String xml1, String xml2) {
        return compare(xml1, xml2, "");
    }

    public static String subString(String string) {
        String str;
        str = string.substring(string.indexOf("<data"), string.lastIndexOf("</data>") + 7);
        return str;
    }
}
