package cn.org.upbnc.xmlcompare;

public class Rpc {
    public static String getStart(String num){
        return "<rpc-reply xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\""+num+"\">";
    }
    public static String getEnd(){
        return "</rpc-reply>";
    }
}
