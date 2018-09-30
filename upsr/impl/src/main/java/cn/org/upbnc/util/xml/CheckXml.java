package cn.org.upbnc.util.xml;

public class CheckXml {
    public static String checkOk(String result) {
        String str;
        if (result.contains("ok")) {
            str = "ok";
        } else {
            str = result;
        }
        return str;
    }
}
