package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.TunnelPolicy.STnlSelSeq;
import cn.org.upbnc.util.netconf.TunnelPolicy.STpNexthop;
import cn.org.upbnc.util.netconf.TunnelPolicy.STunnelPolicy;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TunnelPolicyXml {
    private static final Logger LOG = LoggerFactory.getLogger(TunnelPolicyXml.class);

    public static String getTunnelPolicyXml() {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>\n" +
                "  <filter type=\"subtree\">\n" +
                "    <tnlm:tnlm xmlns:tnlm=\"http://www.huawei.com/netconf/vrp/huawei-tnlm\">\n" +
                "      <tnlm:tunnelPolicys/>\n" +
                "    </tnlm:tnlm>\n" +
                "  </filter>\n" +
                "</get>\n" +
                "</rpc>";
    }

    public static String deleteTunnelPolicyXml(List<String> tnl) {
        String head =
                "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                        "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">                \n" +
                        "  <target>                                                                     \n" +
                        "    <running/>                                                                 \n" +
                        "  </target>                                                                    \n" +
                        "  <error-option>rollback-on-error</error-option>                               \n" +
                        "  <config>                                                                     \n" +
                        "    <tnlm xmlns=\"http://www.huawei.com/netconf/vrp/huawei-tnlm\">             \n" +
                        "      <tunnelPolicys xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">     \n";
        String tunnelPolicys = "";
        for (String policyName : tnl) {
            tunnelPolicys = tunnelPolicys + getTunnelPolicysDeleteXml(policyName);
        }
        String end =
                "      </tunnelPolicys>                                                         \n" +
                        "    </tnlm>                                                                    \n" +
                        "  </config>                                                                    \n" +
                        "</edit-config>                                                                 \n" +
                        "</rpc>";
        return head + tunnelPolicys + end;
    }

    private static String getTunnelPolicysDeleteXml(String tnlPolicyName) {
        String tunnelPolicy =
                "        <tunnelPolicy nc:operation=\"delete\">                                 \n" +
                        "          <tnlPolicyName>" + tnlPolicyName + "</tnlPolicyName>                   \n" +
                        "        </tunnelPolicy>                                                        \n";
        return tunnelPolicy;
    }

    public static String createTunnelPolicyXml(List<STunnelPolicy> sTunnelPolicyList, Map<String, List<String>> nexthopIPaddrMap, Map<String, Map<String, List<String>>> maps) {
        String head =
                "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                        "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">                \n" +
                        "  <target>                                                                     \n" +
                        "    <running/>                                                                 \n" +
                        "  </target>                                                                    \n" +
                        "  <error-option>rollback-on-error</error-option>                               \n" +
                        "  <config>                                                                     \n" +
                        "    <tnlm xmlns=\"http://www.huawei.com/netconf/vrp/huawei-tnlm\">             \n" +
                        "      <tunnelPolicys xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">     \n";
        String tunnelPolicys = "";
        for (STunnelPolicy sTunnelPolicy : sTunnelPolicyList) {
            tunnelPolicys = tunnelPolicys + getTunnelPolicysCreateXml(sTunnelPolicy, nexthopIPaddrMap, maps);
        }
        String end =
                "      </tunnelPolicys>                                                         \n" +
                        "    </tnlm>                                                                    \n" +
                        "  </config>                                                                    \n" +
                        "</edit-config>                                                                 \n" +
                        "</rpc>";
        return head + tunnelPolicys + end;
    }

    private static String getTunnelPolicysCreateXml(STunnelPolicy sTunnelPolicy, Map<String, List<String>> nexthopIPaddrMap, Map<String, Map<String, List<String>>> maps) {
        String tunnelPolicy =
                "        <tunnelPolicy>                                                                      \n" +
                        "          <tnlPolicyName>" + sTunnelPolicy.getTnlPolicyName() + "</tnlPolicyName>               \n";
        String tpNexthop = "";
        if (sTunnelPolicy.getSTpNexthops() != null) {
            tpNexthop = tpNexthop +
                    "          <tpNexthops>                                                                      \n";
            for (STpNexthop sTpNexthop : sTunnelPolicy.getSTpNexthops()) {
                tpNexthop = tpNexthop +
                        "            <tpNexthop>                                                                     \n" +
                        "              <nexthopIPaddr>" + sTpNexthop.getNexthopIPaddr() + "</nexthopIPaddr>              \n" +
                        "              <ignoreDestCheck>" + "true" + "</ignoreDestCheck>                              \n" +
                        "              <isIncludeLdp>" + "true" + "</isIncludeLdp>                                    \n";
                String tpTunnels = "";
                if (sTpNexthop.getTpTunnels() != null) {
                    tpTunnels = tpTunnels +
                            "              <tpTunnels>                                                                   \n";
                    for (String tunnelName : sTpNexthop.getTpTunnels()) {
                        tpTunnels = tpTunnels +
                                "                <tpTunnel><tunnelName>" + tunnelName + "</tunnelName></tpTunnel>              \n";
                    }
                    if (maps.containsKey(sTunnelPolicy.getTnlPolicyName())) {
                        if (maps.get(sTunnelPolicy.getTnlPolicyName()).containsKey(sTpNexthop.getNexthopIPaddr())) {
                            for (String string : maps.get(sTunnelPolicy.getTnlPolicyName()).get(sTpNexthop.getNexthopIPaddr())) {
                                tpTunnels = tpTunnels + "                <tpTunnel xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\">\n" +
                                        "                  <tunnelName >" + string + "</tunnelName>\n" +
                                        "                </tpTunnel>";
                            }
                        }
                    }
                    tpTunnels = tpTunnels +
                            "              </tpTunnels>                                                                  \n";
                }
                tpNexthop = tpNexthop + tpTunnels +
                        "            </tpNexthop>                                                                    \n";
            }
            if (nexthopIPaddrMap.containsKey(sTunnelPolicy.getTnlPolicyName())) {
                for (String str : nexthopIPaddrMap.get(sTunnelPolicy.getTnlPolicyName())) {
                    tpNexthop = tpNexthop + "            <tpNexthop xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\">\n" +
                            "              <nexthopIPaddr>" + str + "</nexthopIPaddr>\n" +
                            "            </tpNexthop>";
                }
            }
            tpNexthop = tpNexthop +
                    "          </tpNexthops>                                                                     \n";
        }
        String tunnelPolicyEnd =
                "        </tunnelPolicy>                                                        \n";
        return tunnelPolicy + tpNexthop + tunnelPolicyEnd;
    }

    public static List<STunnelPolicy> getSTunnelPolicyFromXml(String xml) {
        List<STunnelPolicy> sTunnelPolicyList = new ArrayList<>();
        if (null == xml || xml.isEmpty()) {//判断xml是否为空
            return sTunnelPolicyList;
        }
        SAXReader reader = new SAXReader();
        org.dom4j.Document document = null;
        try {
            document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            if (root.element("data").element("tnlm") == null) {
                return sTunnelPolicyList;
            }
            List<Element> tunnelPolicyElements = root.element("data").element("tnlm").element("tunnelPolicys").elements("tunnelPolicy");
            if ((tunnelPolicyElements == null) || (tunnelPolicyElements.size() == 0)) {
                return sTunnelPolicyList;
            }
            for (org.dom4j.Element tunnelPolicyElement : tunnelPolicyElements) {
                STunnelPolicy sTunnelPolicy = getSTunnelPolicyFromXml(tunnelPolicyElement);//赋值传递实体类
                if (null == sTunnelPolicy) {
                    break;
                }
                sTunnelPolicyList.add(sTunnelPolicy);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return sTunnelPolicyList;
    }

    private static STunnelPolicy getSTunnelPolicyFromXml(org.dom4j.Element tunnelPolicyElement) {
        STunnelPolicy sTunnelPolicy = new STunnelPolicy();//赋值传递实体类
        //set tnlPolicyName key值
        if (null == tunnelPolicyElement.elementText("tnlPolicyName") || tunnelPolicyElement.elementText("tnlPolicyName").isEmpty()) {
            return null;
        } else {
            sTunnelPolicy.setTnlPolicyName(tunnelPolicyElement.elementText("tnlPolicyName"));
        }
        //set description
        if (null == tunnelPolicyElement.elementText("description")) {
            sTunnelPolicy.setDescription("");
        } else {
            sTunnelPolicy.setDescription(tunnelPolicyElement.elementText("description"));
        }
        //set tnlPolicyType
        if (null == tunnelPolicyElement.elementText("tnlPolicyType")) {
            sTunnelPolicy.setTnlPolicyType("");
        } else {
            sTunnelPolicy.setTnlPolicyType(tunnelPolicyElement.elementText("tnlPolicyType"));
        }
        //##############根据tnlPolicyType，展开不同的读取逻辑#################
        if (sTunnelPolicy.getTnlPolicyType().equals("tnlSelectSeq")) {//若type为tnlSelectSeq，读取tnlSelSeqs数据
            List<Element> tnlSelSeqElements = tunnelPolicyElement.element("tnlSelSeqs").elements("tnlSelSeq");
            if (null != tnlSelSeqElements && tnlSelSeqElements.size() != 0) {
                for (org.dom4j.Element tnlSelSeqElement : tnlSelSeqElements) {
                    STnlSelSeq stnlSelSeq = getSTnlSelSeqFromXml(tnlSelSeqElement);//赋值传递实体类
                    if (null == stnlSelSeq) {
                        break;
                    }
                    sTunnelPolicy.getSTnlSelSeqls().add(stnlSelSeq);
                }
            }
        }
        if (sTunnelPolicy.getTnlPolicyType().equals("tnlBinding")) {//若type为binding，读取tpNexthops数据
            List<Element> tpNexthopElements = tunnelPolicyElement.element("tpNexthops").elements("tpNexthop");
            if (null != tpNexthopElements && tpNexthopElements.size() != 0) {
                for (org.dom4j.Element tpNexthopElement : tpNexthopElements) {
                    STpNexthop sTpNexthop = getSTpNexthopFromXml(tpNexthopElement);//赋值传递实体类
                    if (null == sTpNexthop) {
                        break;
                    }
                    sTunnelPolicy.getSTpNexthops().add(sTpNexthop);
                }
            }
        }
        //########################################################################
        return sTunnelPolicy;
    }

    private static STpNexthop getSTpNexthopFromXml(org.dom4j.Element tpNexthopElement) {
        STpNexthop sTpNexthop = new STpNexthop();//赋值传递实体类
        //set nexthopIPaddr key值
        if (null == tpNexthopElement.elementText("nexthopIPaddr") || tpNexthopElement.elementText("nexthopIPaddr").isEmpty()) {
            return null;
        } else {
            sTpNexthop.setNexthopIPaddr(tpNexthopElement.elementText("nexthopIPaddr"));
        }
        //set downSwitch
        if (null == tpNexthopElement.elementText("downSwitch")) {
            sTpNexthop.setDownSwitch(false);
        } else {
            sTpNexthop.setDownSwitch(tpNexthopElement.elementText("downSwitch") == "true" ? true : false);
        }
        //set ignoreDestCheck
        if (null == tpNexthopElement.elementText("ignoreDestCheck")) {
            sTpNexthop.setIgnoreDestCheck(false);
        } else {
            sTpNexthop.setIgnoreDestCheck(tpNexthopElement.elementText("ignoreDestCheck") == "true" ? true : false);
        }
        //set isIncludeLdp
        if (null == tpNexthopElement.elementText("isIncludeLdp")) {
            sTpNexthop.setIncludeLdp(false);
        } else {
            sTpNexthop.setIncludeLdp(tpNexthopElement.elementText("isIncludeLdp") == "true" ? true : false);
        }
        List<Element> tpTunnelElements = tpNexthopElement.element("tpTunnels").elements(
                "tpTunnel");
        for (org.dom4j.Element tpTunnelElement : tpTunnelElements) {
            if (null == tpTunnelElement.elementText("tunnelName") || tpTunnelElement.elementText(
                    "tunnelName").isEmpty()) {
                break;
            }
            sTpNexthop.getTpTunnels().add(tpTunnelElement.elementText("tunnelName"));
        }
        return sTpNexthop;
    }

    private static STnlSelSeq getSTnlSelSeqFromXml(org.dom4j.Element tnlSelSeqElement) {
        STnlSelSeq sTnlSelSeq = new STnlSelSeq();//赋值传递实体类
        if (null != tnlSelSeqElement.elementText("loadBalanceNum")) {
            sTnlSelSeq.setLoadBalanceNum(Integer.parseInt(tnlSelSeqElement.elementText("loadBalanceNum")));
        }
        if (null != tnlSelSeqElement.elementText("selTnlType1")) {
            sTnlSelSeq.setSelTnlType1(tnlSelSeqElement.elementText("selTnlType1"));
        }
        if (null != tnlSelSeqElement.elementText("selTnlType2")) {
            sTnlSelSeq.setSelTnlType2(tnlSelSeqElement.elementText("selTnlType2"));
        }
        if (null != tnlSelSeqElement.elementText("selTnlType3")) {
            sTnlSelSeq.setSelTnlType3(tnlSelSeqElement.elementText("selTnlType3"));
        }
        if (null != tnlSelSeqElement.elementText("selTnlType4")) {
            sTnlSelSeq.setSelTnlType4(tnlSelSeqElement.elementText("selTnlType4"));
        }
        if (null != tnlSelSeqElement.elementText("selTnlType5")) {
            sTnlSelSeq.setSelTnlType5(tnlSelSeqElement.elementText("selTnlType5"));
        }
        if (null != tnlSelSeqElement.elementText("unmix")) {
            sTnlSelSeq.setUnmix(tnlSelSeqElement.elementText("unmix") == "true" ? true : false);
        }
        return sTnlSelSeq;
    }
}
