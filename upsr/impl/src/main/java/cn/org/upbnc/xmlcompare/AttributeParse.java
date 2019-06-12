package cn.org.upbnc.xmlcompare;

import java.util.ArrayList;
import java.util.List;

public class AttributeParse {
    public static List<Attribute> parse(String path) {
        List<Attribute> attributes = new ArrayList<>();
        Attribute attribute;
        if (!("".equals(path)) && (null != path)) {
            String[] splitAddress = path.split("/");
            for (int i = 1; i < splitAddress.length; i++) {
                attribute = new Attribute();
                attribute.setIndex(Integer.parseInt(splitAddress[i].substring(splitAddress[i].indexOf("[") + 1, splitAddress[i].indexOf("]"))));
                attribute.setName(splitAddress[i].substring(0, splitAddress[i].indexOf("[")));
                attributes.add(attribute);
            }
        }
        return attributes;
    }
}
