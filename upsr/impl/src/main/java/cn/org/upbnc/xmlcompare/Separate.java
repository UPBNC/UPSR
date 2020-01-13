package cn.org.upbnc.xmlcompare;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Separate {
    public static List<SeparateEntity> getSeparate(String xml1, String xml2) {
        List<SeparateEntity> ReturnSeparates = new ArrayList<>();
        try {
            Diff diff = new Diff(xml1, xml2);
            DetailedDiff myDiff = new DetailedDiff(diff);
            Difference difference;
            List<DifferentEntity> differentEntities = new ArrayList<>();
            DifferentEntity differentEntity;
            String str = null;
            List allDifferences = myDiff.getAllDifferences();
            if (allDifferences.size() > 0) {
                for (int i = 0; i < allDifferences.size(); i++) {
                    differentEntity = new DifferentEntity();
                    difference = (Difference) allDifferences.get(i);
                    differentEntity.setId(difference.getId());
                    differentEntity.setDescription(difference.getDescription());
                    if (null != difference.getControlNodeDetail().getNode()) {
                        differentEntity.setControlNode(difference.getControlNodeDetail().getNode().toString());
                    }
                    str = difference.getControlNodeDetail().getValue();
                    if (null != str) {
                        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                        Matcher m = p.matcher(str);
                        str = m.replaceAll("");
                    }
                    differentEntity.setControlValue(str);
                    differentEntity.setControlXpathLocation(difference.getControlNodeDetail().getXpathLocation());
                    if (null != difference.getTestNodeDetail().getNode()) {
                        differentEntity.setTestNode(difference.getTestNodeDetail().getNode().toString());
                    }
                    str = difference.getTestNodeDetail().getValue();
                    if (null != str) {
                        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                        Matcher m = p.matcher(str);
                        str = m.replaceAll("");
                    }
                    differentEntity.setTestValue(str);
                    differentEntity.setTestXpathLocation(difference.getTestNodeDetail().getXpathLocation());
                    differentEntities.add(differentEntity);
                }
            }
            List<SeparateEntity> separateEntities = new ArrayList<>();
            List<SeparateEntity> separates = new ArrayList<>();
            SeparateEntity separateEntity;
            ActionEntity actionEntity;
            ModifyEntity modifyEntity;
            for (int i = 0; i < differentEntities.size(); i++) {
                DifferentEntity entity = differentEntities.get(i);
                if ("".equals(entity.getControlValue()) && "".equals(entity.getTestValue())) {
                    separateEntity = new SeparateEntity();
                    separateEntity.setNum(i);
                    separateEntities.add(separateEntity);
                }
            }
            int y;
            for (int j = 0; j < separateEntities.size(); j++) {
                if (differentEntities.get(separateEntities.get(j).getNum() + 1).getDescription().contains("sequence")
                        && (separateEntities.get(j).getNum() + 2) == allDifferences.size()) {
                } else {
                    separates.add(separateEntities.get(j));
                    if (differentEntities.get(separateEntities.get(j).getNum() + 1).getDescription().contains("sequence")) {
                        y = separateEntities.get(j).getNum() + 1;
                    } else {
                        y = separateEntities.get(j).getNum();
                    }
                    if (differentEntities.get(y + 1).getDescription().contains("presence")) {
                        actionEntity = new ActionEntity();
                        actionEntity.setAction(ActionTypeEnum.delete);
                        actionEntity.setPath(differentEntities.get(y + 1).getTestXpathLocation());
                        separateEntities.get(j).getActionEntities().add(actionEntity);
                        int sum = 0;
                        if ((j + 1 < separateEntities.size()) && separateEntities.get(j).getNum() + 2 < separateEntities.get(j + 1).getNum()) {
                            sum = (separateEntities.get(j + 1).getNum() - separateEntities.get(j).getNum() - 1) / 2;
                        } else if (separateEntities.get(j).getNum() + 1 < allDifferences.size()) {
                            sum = (allDifferences.size() - separateEntities.get(j).getNum() - 1) / 2;
                        }
                        if (sum > 1) {
                            for (int xyz = 1; xyz < sum; xyz++) {
                                separateEntity = new SeparateEntity();
                                actionEntity = new ActionEntity();
                                actionEntity.setAction(ActionTypeEnum.delete);
                                actionEntity.setPath(differentEntities.get(y + 2 * xyz + 1).getTestXpathLocation());
                                separateEntity.getActionEntities().add(actionEntity);
                                separates.add(separateEntity);
                            }
                        }
                    } else if (differentEntities.get(y + 1).getDescription().contains("text value")) {
                        actionEntity = new ActionEntity();
                        if (differentEntities.get(y + 1).getControlXpathLocation().equals(differentEntities.get(y + 1).getTestXpathLocation())) {
                            if (separateEntities.size() == 1) {
                                for (int x = separateEntities.get(j).getNum() + 1; x < allDifferences.size(); x++) {
                                    modifyEntity = new ModifyEntity();
                                    difference = (Difference) allDifferences.get(x);
                                    if (null == (difference.getControlNodeDetail().getXpathLocation())) {
                                        continue;
                                    } else {
                                        modifyEntity.setPath(difference.getControlNodeDetail().getXpathLocation());
                                        modifyEntity.setOdlValue(difference.getTestNodeDetail().getValue());
                                        modifyEntity.setNewValue(difference.getControlNodeDetail().getValue());
                                        actionEntity.getModifyEntities().add(modifyEntity);
                                    }
                                }
                            } else {
                                for (int x = separateEntities.get(j).getNum() + 1; x < separateEntities.get(j + 1).getNum(); x++) {
                                    modifyEntity = new ModifyEntity();
                                    difference = (Difference) allDifferences.get(x);
                                    if (null == (difference.getControlNodeDetail().getXpathLocation())) {
                                        continue;
                                    } else {
                                        modifyEntity.setPath(difference.getControlNodeDetail().getXpathLocation());
                                        modifyEntity.setOdlValue(difference.getTestNodeDetail().getValue());
                                        modifyEntity.setNewValue(difference.getControlNodeDetail().getValue());
                                        actionEntity.getModifyEntities().add(modifyEntity);
                                    }
                                }
                            }
                            actionEntity.setAction(ActionTypeEnum.modify);
                            actionEntity.setPath(differentEntities.get(y + 1).getControlXpathLocation());
                        } else {
                            actionEntity.setAction(ActionTypeEnum.add);
                            actionEntity.setPath(differentEntities.get(y + 1).getControlXpathLocation());
                        }
                        separateEntities.get(j).getActionEntities().add(actionEntity);
                    }
                }
            }
            for (int xy = 0; xy < separates.size(); xy++) {
                if (separateEntities.size() > xy) {
                    ReturnSeparates.add(separateEntities.get(xy));
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ReturnSeparates;
    }

    public static List<SeparateEntity> getSeparate2(String xml1, String xml2) {
        List<SeparateEntity> ReturnSeparates = new ArrayList<>();
        try {
            Diff diff = new Diff(xml1, xml2);
            DetailedDiff myDiff = new DetailedDiff(diff);
            Difference difference;
            List<DifferentEntity> differentEntities = new ArrayList<>();
            DifferentEntity differentEntity;
            String str = null;
            if (myDiff.getAllDifferences().size() > 0) {
                for (int i = 0; i < myDiff.getAllDifferences().size(); i++) {
                    differentEntity = new DifferentEntity();
                    difference = (Difference) myDiff.getAllDifferences().get(i);
                    differentEntity.setId(difference.getId());
                    differentEntity.setDescription(difference.getDescription());
                    if (null != difference.getControlNodeDetail().getNode()) {
                        differentEntity.setControlNode(difference.getControlNodeDetail().getNode().toString());
                    }
                    str = difference.getControlNodeDetail().getValue();
                    if (null != str) {
                        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                        Matcher m = p.matcher(str);
                        str = m.replaceAll("");
                    }
                    differentEntity.setControlValue(str);
                    differentEntity.setControlXpathLocation(difference.getControlNodeDetail().getXpathLocation());
                    if (null != difference.getTestNodeDetail().getNode()) {
                        differentEntity.setTestNode(difference.getTestNodeDetail().getNode().toString());
                    }
                    str = difference.getTestNodeDetail().getValue();
                    if (null != str) {
                        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                        Matcher m = p.matcher(str);
                        str = m.replaceAll("");
                    }
                    differentEntity.setTestValue(str);
                    differentEntity.setTestXpathLocation(difference.getTestNodeDetail().getXpathLocation());
                    differentEntities.add(differentEntity);

                    System.out.println("difference.getDescription() :" + difference.getDescription());
                    System.out.println("difference.getId() :" + difference.getId());
                    System.out.println("difference.isRecoverable() :" + difference.isRecoverable());
                    System.out.println("difference.getControlNodeDetail() getValue :" + difference.getControlNodeDetail().getValue());
                    System.out.println("difference.getControlNodeDetail() getValue str:" + str.equals(""));
                    System.out.println("difference.getControlNodeDetail() getXpathLocation :" + difference.getControlNodeDetail().getXpathLocation());
                    System.out.println("difference.getControlNodeDetail() getNode :" + difference.getControlNodeDetail().getNode());
                    System.out.println("difference.getTestNodeDetail() getValue :" + difference.getTestNodeDetail().getValue());
                    System.out.println("difference.getTestNodeDetail() getXpathLocation :" + difference.getTestNodeDetail().getXpathLocation());
                    System.out.println("difference.getTestNodeDetail() getNode :" + difference.getTestNodeDetail().getNode());

                }
            }
            List<SeparateEntity> separateEntities = new ArrayList<>();
            List<SeparateEntity> separates = new ArrayList<>();

            SeparateEntity separateEntity;
            ActionEntity actionEntity;
            ModifyEntity modifyEntity;
            for (int i = 0; i < differentEntities.size(); i++) {
                DifferentEntity entity = differentEntities.get(i);
                if ("".equals(entity.getControlValue()) && "".equals(entity.getTestValue())) {
                    separateEntity = new SeparateEntity();
                    separateEntity.setNum(i);
                    separateEntities.add(separateEntity);
                }
            }

            System.out.println("separateEntities.size() :" + separateEntities.size());

            for (SeparateEntity separateEntity1 : separateEntities) {
                System.out.println("separateEntity1.getNum() :" + separateEntity1.getNum());
            }

            int y;
            for (int j = 0; j < separateEntities.size(); j++) {
                if (differentEntities.get(separateEntities.get(j).getNum() + 1).getDescription().contains("sequence")
                        && (separateEntities.get(j).getNum() + 2) == myDiff.getAllDifferences().size()) {
                    System.out.println("num " + separateEntities.get(j).getNum() + " disuseable");
                } else {
                    separates.add(separateEntities.get(j));
                    if (differentEntities.get(separateEntities.get(j).getNum() + 1).getDescription().contains("sequence")) {
                        y = separateEntities.get(j).getNum() + 1;
                    } else {
                        y = separateEntities.get(j).getNum();
                    }
                    if (differentEntities.get(y + 1).getDescription().contains("presence")) {
                        actionEntity = new ActionEntity();
                        actionEntity.setAction(ActionTypeEnum.delete);
                        actionEntity.setPath(differentEntities.get(y + 1).getTestXpathLocation());
                        separateEntities.get(j).getActionEntities().add(actionEntity);
                        int sum = 0;
                        if ((j + 1 < separateEntities.size()) && separateEntities.get(j).getNum() + 2 < separateEntities.get(j + 1).getNum()) {
                            sum = (separateEntities.get(j + 1).getNum() - separateEntities.get(j).getNum() - 1) / 2;
                        } else if (separateEntities.get(j).getNum() + 1 < myDiff.getAllDifferences().size()) {
                            sum = (myDiff.getAllDifferences().size() - separateEntities.get(j).getNum() - 1) / 2;
                        }
                        if (sum > 1) {
                            for (int xyz = 1; xyz < sum; xyz++) {
                                separateEntity = new SeparateEntity();
                                actionEntity = new ActionEntity();
                                actionEntity.setAction(ActionTypeEnum.delete);
                                actionEntity.setPath(differentEntities.get(y + 2 * xyz + 1).getTestXpathLocation());
                                separateEntity.getActionEntities().add(actionEntity);
                                separates.add(separateEntity);
                            }
                        }
                    } else if (differentEntities.get(y + 1).getDescription().contains("text value")) {
                        actionEntity = new ActionEntity();
                        if (differentEntities.get(y + 1).getControlXpathLocation().equals(differentEntities.get(y + 1).getTestXpathLocation())) {
                            if (separateEntities.size() == 1) {
                                for (int x = separateEntities.get(j).getNum() + 1; x < myDiff.getAllDifferences().size(); x++) {
                                    modifyEntity = new ModifyEntity();
                                    difference = (Difference) myDiff.getAllDifferences().get(x);
                                    if (null == (difference.getControlNodeDetail().getXpathLocation())) {
                                        continue;
                                    } else {
                                        modifyEntity.setPath(difference.getControlNodeDetail().getXpathLocation());
                                        modifyEntity.setOdlValue(difference.getTestNodeDetail().getValue());
                                        modifyEntity.setNewValue(difference.getControlNodeDetail().getValue());
                                        actionEntity.getModifyEntities().add(modifyEntity);
                                    }
                                }
                            } else {
                                for (int x = separateEntities.get(j).getNum() + 1; x < separateEntities.get(j + 1).getNum(); x++) {
                                    modifyEntity = new ModifyEntity();
                                    difference = (Difference) myDiff.getAllDifferences().get(x);
                                    if (null == (difference.getControlNodeDetail().getXpathLocation())) {
                                        continue;
                                    } else {
                                        modifyEntity.setPath(difference.getControlNodeDetail().getXpathLocation());
                                        modifyEntity.setOdlValue(difference.getTestNodeDetail().getValue());
                                        modifyEntity.setNewValue(difference.getControlNodeDetail().getValue());
                                        actionEntity.getModifyEntities().add(modifyEntity);
                                    }
                                }
                            }
                            actionEntity.setAction(ActionTypeEnum.modify);
                            actionEntity.setPath(differentEntities.get(y + 1).getControlXpathLocation());
                        } else {
                            actionEntity.setAction(ActionTypeEnum.add);
                            actionEntity.setPath(differentEntities.get(y + 1).getControlXpathLocation());
                        }
                        separateEntities.get(j).getActionEntities().add(actionEntity);
                    }
                }
            }
            for (int xy = 0; xy < separates.size(); xy++) {
                ReturnSeparates.add(separateEntities.get(xy));
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ReturnSeparates;
    }

    public static List<SeparateEntity> getExplicitePathSeparate(String xml1, String xml2) {
        List<SeparateEntity> ReturnSeparates = new ArrayList<>();
        try {
            Diff diff = new Diff(xml1, xml2);
            DetailedDiff myDiff = new DetailedDiff(diff);
            Difference difference;
            List<DifferentEntity> differentEntities = new ArrayList<>();
            DifferentEntity differentEntity;
            String str = null;
            List allDifferences = myDiff.getAllDifferences();
            if (allDifferences.size() > 0) {
                for (int i = 0; i < allDifferences.size(); i++) {
                    differentEntity = new DifferentEntity();
                    difference = (Difference) allDifferences.get(i);
                    differentEntity.setId(difference.getId());
                    differentEntity.setDescription(difference.getDescription());
                    if (null != difference.getControlNodeDetail().getNode()) {
                        differentEntity.setControlNode(difference.getControlNodeDetail().getNode().toString());
                    }
                    str = difference.getControlNodeDetail().getValue();
                    if (null != str) {
                        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                        Matcher m = p.matcher(str);
                        str = m.replaceAll("");
                    }
                    differentEntity.setControlValue(str);
                    differentEntity.setControlXpathLocation(difference.getControlNodeDetail().getXpathLocation());
                    if (null != difference.getTestNodeDetail().getNode()) {
                        differentEntity.setTestNode(difference.getTestNodeDetail().getNode().toString());
                    }
                    str = difference.getTestNodeDetail().getValue();
                    if (null != str) {
                        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                        Matcher m = p.matcher(str);
                        str = m.replaceAll("");
                    }
                    differentEntity.setTestValue(str);
                    differentEntity.setTestXpathLocation(difference.getTestNodeDetail().getXpathLocation());
                    differentEntities.add(differentEntity);
                }
            }
            List<SeparateEntity> separateEntities = new ArrayList<>();
            List<SeparateEntity> separates = new ArrayList<>();
            SeparateEntity separateEntity;
            ActionEntity actionEntity;
            ModifyEntity modifyEntity;
            for (int i = 0; i < differentEntities.size(); i++) {
                DifferentEntity entity = differentEntities.get(i);
                if ("".equals(entity.getControlValue()) && "".equals(entity.getTestValue())) {
                    if (("explicitPaths").equals
                            (AttributeParse.parse(entity.getControlXpathLocation()).get(AttributeParse.parse(entity.
                                    getControlXpathLocation()).size() - 2).getName())) {
                        if (differentEntities.get(i + 1).getDescription().contains("sequence")) {
                            continue;
                        }
                        if (differentEntities.get(i + 1).getDescription().contains("text")) {
                            continue;
                        }
                    }
                    boolean flag = true;
                    for (int s = 0; s < separateEntities.size(); s++) {
                        if (separateEntities.get(s).getPath() != null && separateEntities.get(s).getPath().contains("[") &&
                                entity.getControlXpathLocation().contains("[")) {
                            if (separateEntities.get(s).getPath().substring(0, separateEntities.get(s).getPath().
                                    lastIndexOf("[")).equals(entity.getControlXpathLocation().substring(0,
                                    entity.getControlXpathLocation().lastIndexOf("[")))) {
                                flag = false;
                            }
                        }
                    }
                    if (flag) {
                        separateEntity = new SeparateEntity();
                        separateEntity.setNum(i);
                        separateEntity.setPath(entity.getControlXpathLocation());
                        separateEntities.add(separateEntity);
                    }
                    continue;
                }
                if (entity.getDescription().contains("attribute")) {
                    continue;
                }
                if (null == entity.getControlXpathLocation() || null == entity.getTestXpathLocation()) {

                } else {
                    if (entity.getControlXpathLocation().equals(entity.getTestXpathLocation())) {
                        boolean flag = true;
                        if (i > 0) {
                            if (("explicitPathHops").equals
                                    (AttributeParse.parse(entity.getControlXpathLocation()).get(AttributeParse.parse(entity.
                                            getControlXpathLocation()).size() - 4).getName())) {
                                boolean lable = false;
                                for (int s = 0; s < separateEntities.size(); s++) {
                                    if (separateEntities.get(s).getNextPath() != null) {
                                        if (separateEntities.get(s).getNextPath().contains("explicitPathHop") &&
                                                entity.getControlXpathLocation().contains("explicitPathHop")) {
                                            if (separateEntities.get(s).getNextPath().substring(0, separateEntities.get(s).getNextPath().
                                                    lastIndexOf("explicitPathHop")).equals(entity.getControlXpathLocation().substring(0,
                                                    entity.getControlXpathLocation().lastIndexOf("explicitPathHop")))) {
                                                lable = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (lable) {
                                    flag = false;
                                }
                            }
                            if (flag) {
                                separateEntity = new SeparateEntity();
                                separateEntity.setNum(i - 1);
                                separateEntity.setPath(differentEntities.get(i - 1).getControlXpathLocation());
                                separateEntity.setNextPath(entity.getControlXpathLocation());
                                separateEntities.add(separateEntity);
                            }
                        }
                        continue;
                    }
                }
            }
            int y;
            for (int j = 0; j < separateEntities.size(); j++) {
                if (differentEntities.get(separateEntities.get(j).getNum() + 1).getDescription().contains("sequence")
                        && (separateEntities.get(j).getNum() + 2) == allDifferences.size()) {
                } else {
                    separates.add(separateEntities.get(j));
                    if (differentEntities.get(separateEntities.get(j).getNum() + 1).getDescription().contains("sequence")) {
                        y = separateEntities.get(j).getNum() + 1;
                    } else {
                        y = separateEntities.get(j).getNum();
                    }
                    if (differentEntities.get(y + 1).getDescription().contains("presence")) {
                        actionEntity = new ActionEntity();
                        actionEntity.setAction(ActionTypeEnum.delete);
                        actionEntity.setPath(differentEntities.get(y + 1).getTestXpathLocation());
                        separateEntities.get(j).getActionEntities().add(actionEntity);
                        int sum = 0;
                        if ((j + 1 < separateEntities.size()) && separateEntities.get(j).getNum() + 2 < separateEntities.get(j + 1).getNum()) {
                            sum = (separateEntities.get(j + 1).getNum() - separateEntities.get(j).getNum() - 1) / 2;
                        } else if (separateEntities.get(j).getNum() + 1 < allDifferences.size()) {
                            sum = (allDifferences.size() - separateEntities.get(j).getNum() - 1) / 2;
                        }
                        if (sum > 1) {
                            for (int xyz = 1; xyz < sum; xyz++) {
                                separateEntity = new SeparateEntity();
                                actionEntity = new ActionEntity();
                                actionEntity.setAction(ActionTypeEnum.delete);
                                actionEntity.setPath(differentEntities.get(y + 2 * xyz + 1).getTestXpathLocation());
                                separateEntity.getActionEntities().add(actionEntity);
                                separateEntities.add(separateEntity);
                            }
                        }
                    } else if (differentEntities.get(y + 1).getDescription().contains("text value")) {
                        actionEntity = new ActionEntity();
                        if (differentEntities.get(y + 1).getControlXpathLocation().equals(differentEntities.get(y + 1).getTestXpathLocation())) {
                            if (separateEntities.size() == 1) {
                                for (int x = separateEntities.get(j).getNum() + 1; x < allDifferences.size(); x++) {
                                    modifyEntity = new ModifyEntity();
                                    difference = (Difference) allDifferences.get(x);
                                    if (null == (difference.getControlNodeDetail().getXpathLocation())) {
                                        continue;
                                    } else {
                                        modifyEntity.setPath(difference.getControlNodeDetail().getXpathLocation());
                                        List<Attribute> attributes = AttributeParse.parse(modifyEntity.getPath());
                                        String lable = attributes.get(attributes.size() - 2).getName();
                                        modifyEntity.setLabel(lable);
                                        modifyEntity.setOdlValue(difference.getTestNodeDetail().getValue());
                                        modifyEntity.setNewValue(difference.getControlNodeDetail().getValue());
                                        actionEntity.getModifyEntities().add(modifyEntity);
                                    }
                                }
                            } else {
                                if (j + 1 <= separateEntities.size() - 1) {
                                    for (int x = separateEntities.get(j).getNum() + 1; x <= separateEntities.get(j + 1).getNum(); x++) {
                                        modifyEntity = new ModifyEntity();
                                        difference = (Difference) allDifferences.get(x);
                                        if (null == (difference.getControlNodeDetail().getXpathLocation())) {
                                            continue;
                                        } else {
                                            modifyEntity.setPath(difference.getControlNodeDetail().getXpathLocation());
                                            List<Attribute> attributes = AttributeParse.parse(modifyEntity.getPath());
                                            String lable = attributes.get(attributes.size() - 2).getName();
                                            modifyEntity.setLabel(lable);
                                            modifyEntity.setOdlValue(difference.getTestNodeDetail().getValue());
                                            modifyEntity.setNewValue(difference.getControlNodeDetail().getValue());
                                            actionEntity.getModifyEntities().add(modifyEntity);
                                        }
                                    }
                                }
                            }
                            actionEntity.setAction(ActionTypeEnum.modify);
                            actionEntity.setPath(differentEntities.get(y + 1).getControlXpathLocation());
                        } else {
                            actionEntity.setAction(ActionTypeEnum.add);
                            actionEntity.setPath(differentEntities.get(y + 1).getControlXpathLocation());
                        }
                        separateEntities.get(j).getActionEntities().add(actionEntity);
                    }
                }
            }
            for (int xy = 0; xy < separateEntities.size(); xy++) {
                ReturnSeparates.add(separateEntities.get(xy));
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ReturnSeparates;
    }
}
