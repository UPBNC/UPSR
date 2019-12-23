package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.cfgcli.srlabelcli.SrlabelCli;
import cn.org.upbnc.cfgcli.tunnelcli.TunnelCli;
import cn.org.upbnc.cfgcli.vpncli.VpnCli;
import cn.org.upbnc.entity.CommandLine;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.enumtype.CfgTypeEnum;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.service.ActionCfgService;
import cn.org.upbnc.service.entity.actionCfg.CheckPointInfoServiceEntity;
import cn.org.upbnc.service.entity.actionCfg.PointChangeInfoServiceEntity;
import cn.org.upbnc.util.netconf.L3vpnInstance;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.NetconfSrLabelInfo;
import cn.org.upbnc.util.netconf.actionCfg.SCheckPointInfo;
import cn.org.upbnc.util.netconf.actionCfg.SPointChangeInfo;
import cn.org.upbnc.util.netconf.bgp.BgpVrf;
import cn.org.upbnc.util.xml.*;
import cn.org.upbnc.xmlcompare.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class ActionCfgServiceImpl implements ActionCfgService {
    private static final Logger LOG = LoggerFactory.getLogger(ActionCfgServiceImpl.class);
    private static ActionCfgService ourInstance = null;
    private BaseInterface baseInterface;
    private DeviceManager deviceManager;
    private NetConfManager netConfManager;
    private String synType = "0";

    public static ActionCfgService getInstance() {
        if (ourInstance == null) {
            ourInstance = new ActionCfgServiceImpl();
        }
        return ourInstance;
    }

    public ActionCfgServiceImpl() {
        this.deviceManager = null;
        this.netConfManager = null;
    }

    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        if (null != baseInterface) {
            this.netConfManager = this.baseInterface.getNetConfManager();
            this.deviceManager = this.baseInterface.getDeviceManager();
        }
        return true;
    }

    @Override
    public Map<String, Object> vpn(List<String> routers) {
        Map<String, Object> resultMap = new HashMap<>();
        String result = "";
        String flag;
        ActionEntity actionEntity;
        for (String routerId : routers) {
            result = result + "\nrouter : " + routerId + "\n";
            synType = "2";
            NetconfClient netconfClient = netConfManager.getNetconClient(routerId);

            String vpnRunningXml = RunningXml.getVpnXml();
            LOG.info("vpnRunningXml :" + vpnRunningXml);
            String vpnCandidateXml = CandidateXml.getVpnXml();
            LOG.info("vpnCandidateXml :" + vpnCandidateXml);
            String xmlRunningResult = netconfController.sendMessage(netconfClient, vpnRunningXml);
//        LOG.info("xmlRunningResult :" + xmlRunningResult);
            String xmlCandidateResult = netconfController.sendMessage(netconfClient, vpnCandidateXml);
//        LOG.info("xmlRunningResult :" + xmlRunningResult);
//        String xml1 = VpnUtils.modify();
//        String xml2 = VpnUtils.running();
            //xml1 candidate  xml2 running
            xmlRunningResult = XmlUtils.subString(xmlRunningResult);
            xmlCandidateResult = XmlUtils.subString(xmlCandidateResult);
            flag = "explicitPath";
            String vpnName = "";
            boolean deleteFlag = false;
            boolean ignore = false;
            boolean modifyFlay = false;
            actionEntity = XmlUtils.compare(xmlCandidateResult, xmlRunningResult, flag);
            if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
                LOG.info("add");
                List<L3vpnInstance> l3vpnInstances = GetXml.getVpnFromXml(xmlCandidateResult, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                if (l3vpnInstances.size() != 0 && (!("_public_").equals(l3vpnInstances.get(0).getVrfName()))) {
                    vpnName = l3vpnInstances.get(0).getVrfName();
                    if ("tnlPolicyName".equals(AttributeParse.parse(actionEntity.getPath()).get(AttributeParse.parse(actionEntity.getPath()).size() - 1).getName())) {
                        ignore = true;
                        result = result + "\n # \n ip vpn-instance " + vpnName + "\n   ipv4-family \n +   tnl-policy " + l3vpnInstances.get(0).getTunnelPolicy() + "\n # \n";
                    } else if ("vrfDescription".equals(AttributeParse.parse(actionEntity.getPath()).get(AttributeParse.parse(actionEntity.getPath()).size() - 1).getName())) {
                        ignore = true;
                        result = result + "\n # \n ip vpn-instance " + vpnName + "\n +  description " + l3vpnInstances.get(0).getVrfDescription() + "\n # \n";
                    } else {
                        result = result + "# \n" + " + ip vpn-instance " + l3vpnInstances.get(0).getVrfName();
                        if (l3vpnInstances.get(0).getVrfDescription() != null) {
                            result = result + "\n +  description " + l3vpnInstances.get(0).getVrfDescription();
                        }
                        result = result +
                                "\n +   ipv4-family" + "" +
                                "\n +   route-distinguisher " + l3vpnInstances.get(0).getVrfRD();
                        if ("true".equals(l3vpnInstances.get(0).getVpnFrr())) {
                            result = result + "\n +   vpn frr";
                        }
                        if (l3vpnInstances.get(0).getTunnelPolicy() != null) {
                            result = result + "\n +   tnl-policy " + l3vpnInstances.get(0).getTunnelPolicy();
                        }
                        result = result +
                                "\n +   apply-label " + l3vpnInstances.get(0).getApplyLabel() +
                                "\n +   vpn-target " + l3vpnInstances.get(0).getVrfRTValue() + " export-extcommunity" +
                                "\n +   vpn-target " + l3vpnInstances.get(0).getVrfRTValue() + " import-extcommunity" +
                                "\n +   ttl-mode " + l3vpnInstances.get(0).getTtlMode() + "\n #";
                    }

                }
            } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
                LOG.info("delete");
//                result = "delete";
                List<L3vpnInstance> l3vpnInstances = GetXml.getVpnFromXml(xmlRunningResult, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                if (l3vpnInstances.size() != 0 && (!("_public_").equals(l3vpnInstances.get(0).getVrfName()))) {
                    LOG.info(l3vpnInstances.get(0).toString());
                    vpnName = l3vpnInstances.get(0).getVrfName();
                    deleteFlag = true;
                    result = result + "# \n" + " - ip vpn-instance " + l3vpnInstances.get(0).getVrfName();
                    if (l3vpnInstances.get(0).getVrfDescription() != null) {
                        result = result + "\n -  description " + l3vpnInstances.get(0).getVrfDescription();
                    }
                    result = result +
                            "\n -   ipv4-family" + "" +
                            "\n -   route-distinguisher " + l3vpnInstances.get(0).getVrfRD();
                    if ("true".equals(l3vpnInstances.get(0).getVpnFrr())) {
                        result = result + "\n -   vpn frr";
                    }
                    if (l3vpnInstances.get(0).getTunnelPolicy() != null) {
                        result = result + "\n -   tnl-policy " + l3vpnInstances.get(0).getTunnelPolicy();
                    }
                    result = result +
                            "\n -   apply-label " + l3vpnInstances.get(0).getApplyLabel() +
                            "\n -   vpn-target " + l3vpnInstances.get(0).getVrfRTValue() + " export-extcommunity" +
                            "\n -   vpn-target " + l3vpnInstances.get(0).getVrfRTValue() + " import-extcommunity" +
                            "\n -   ttl-mode " + l3vpnInstances.get(0).getTtlMode() + "\n #";
                }
            } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
                List<L3vpnInstance> l3vpnInstances = GetXml.getVpnFromXml(xmlCandidateResult, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                LOG.info("modify");
                vpnName = l3vpnInstances.get(0).getVrfName();
                String remove = "";
                String add = "";
                String instance = "\n # \n   ip vpn-instance " + l3vpnInstances.get(0).getVrfName();
                String ipv4Family = "\n    ipv4-family";
                String ipv4FamilyRemove = "";
                String ipv4FamilyAdd = "";
                boolean ipv4FamilyFlag = false;
                modifyFlay = true;
                if (l3vpnInstances.size() != 0) {
                    LOG.info(l3vpnInstances.get(0).getVrfName());
                    List<ModifyEntity> modifyEntities = actionEntity.getModifyEntities();
                    for (ModifyEntity modifyEntity : modifyEntities) {
                        if ("vrfDescription".equals(modifyEntity.getLabel())) {
                            remove = remove + "\n -  description " + modifyEntity.getOdlValue();
                            add = add + "\n +  description " + modifyEntity.getNewValue();
                        }
                        if ("vrfRD".equals(modifyEntity.getLabel())) {
                            ipv4FamilyFlag = true;
                            ipv4FamilyRemove = ipv4FamilyRemove + "\n -   route-distinguisher " + modifyEntity.getOdlValue();
                            ipv4FamilyAdd = ipv4FamilyAdd + "\n +   route-distinguisher " + modifyEntity.getNewValue();
                        }
                        if ("vpnFrr".equals(modifyEntity.getLabel())) {
                            ipv4FamilyFlag = true;
                            if (("false").equals(modifyEntity.getOdlValue())) {
                                ipv4FamilyAdd = ipv4FamilyAdd + " \n +   vpn frr";
                            } else {
                                ipv4FamilyRemove = ipv4FamilyRemove + " \n -   vpn frr";
                            }
                        }
                        if ("tnlPolicyName".equals(modifyEntity.getLabel())) {
                            ipv4FamilyFlag = true;
                            ipv4FamilyRemove = ipv4FamilyRemove + "\n -   tnl-policy   " + modifyEntity.getOdlValue();
                            ipv4FamilyAdd = ipv4FamilyAdd + "\n +   tnl-policy  " + modifyEntity.getNewValue();
                        }
                        if ("vrfLabelMode".equals(modifyEntity.getLabel())) {
                            ipv4FamilyFlag = true;
                            ipv4FamilyRemove = ipv4FamilyRemove + "\n -   apply-label  " + modifyEntity.getOdlValue();
                            ipv4FamilyAdd = ipv4FamilyAdd + "\n +   apply-label  " + modifyEntity.getNewValue();
                        }
                        if ("ttlMode".equals(modifyEntity.getLabel())) {
                            if ("pipe".equals(modifyEntity.getOdlValue())) {
                            } else {
                                ipv4FamilyFlag = true;
                                ipv4FamilyRemove = ipv4FamilyRemove + "\n -   ttl-mode   " + modifyEntity.getOdlValue();
                            }
                            if ("pipe".equals(modifyEntity.getNewValue())) {
                            } else {
                                ipv4FamilyFlag = true;
                                ipv4FamilyAdd = ipv4FamilyAdd + "\n +   ttl-mode   " + modifyEntity.getNewValue();
                            }
                        }
                        LOG.info("lable :" + modifyEntity.getLabel());
                        LOG.info("modifyEntity.getOdlValue() :" + modifyEntity.getOdlValue());
                        LOG.info("modifyEntity.getNewValue() :" + modifyEntity.getNewValue());
                    }
                    result = result + instance + remove;
                    if (ipv4FamilyFlag) {
                        result = result + ipv4Family + ipv4FamilyRemove;
                    }
                    result = result + add;
                    if (ipv4FamilyFlag) {
                        result = result + ipv4Family + ipv4FamilyAdd;
                    }
                    result = result + "\n #";
                }
            } else if (ActionTypeEnum.identical.name().equals(actionEntity.getAction().name())) {
                LOG.info("modifyEntity action() :" + actionEntity.getAction());
            }

            if (!ignore && !("").equals(vpnName) && !modifyFlay) {
                {
                    String sendMsg;
                    if (deleteFlag) {
                        sendMsg = VpnXml.getRunningVpnXml(vpnName);
                    } else {
                        sendMsg = VpnXml.getCandidateVpnXml(vpnName);
                    }
                    LOG.info(sendMsg);
                    String getResult = netconfController.sendMessage(netconfClient, sendMsg);
                    LOG.info(getResult);
                    List<L3vpnInstance> l3vpnInstances = VpnXml.getVpnFromXml(getResult);
                    if (l3vpnInstances.size() > 0 && l3vpnInstances.get(0).getL3vpnIfs().size() > 0) {
                        if (deleteFlag) {
                            result = result + "#" + "\n" + "  interface " + l3vpnInstances.get(0).getL3vpnIfs().get(0).getIfName() + "\n"
                                    + "-   ip binding vpn-instance " + vpnName + "\n" + "-   ip address " + l3vpnInstances.get(0).getL3vpnIfs().get(0).getIpv4Addr()
                                    + " " + l3vpnInstances.get(0).getL3vpnIfs().get(0).getSubnetMask() + "\n #";
                        } else {
                            result = result + "#" + "\n" + "  interface " + l3vpnInstances.get(0).getL3vpnIfs().get(0).getIfName() + "\n"
                                    + "+   ip binding vpn-instance " + vpnName + "\n" + "+   ip address " + l3vpnInstances.get(0).getL3vpnIfs().get(0).getIpv4Addr()
                                    + " " + l3vpnInstances.get(0).getL3vpnIfs().get(0).getSubnetMask() + "\n #";
                        }
                    }
                    deleteFlag = true;
                }
            }

            if (!deleteFlag && modifyFlay) {
                {
                    String ifmRunningXml = RunningXml.getIfmXml();
                    LOG.info("ifmRunningXml :" + ifmRunningXml);
                    String ifmCandidateXml = CandidateXml.getIfmXml();
                    LOG.info("ifmCandidateXml :" + ifmCandidateXml);
                    String ifmRunningResult = netconfController.sendMessage(netconfClient, ifmRunningXml);
                    String ifmCandidateResult = netconfController.sendMessage(netconfClient, ifmCandidateXml);
                    ifmCandidateResult = XmlUtils.subString(ifmCandidateResult);
                    ifmRunningResult = XmlUtils.subString(ifmRunningResult);
                    //xml1 candidate  xml2 running
                    flag = "explicitPath";
                    actionEntity = XmlUtils.compare(ifmCandidateResult, ifmRunningResult, flag);
                    if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
                        LOG.info("add");
                        List<Interface> interfaces = GetXml.getInterface(ifmCandidateResult, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                        LOG.info(interfaces.get(0).toString());
//                        result = result + "#2" + "\n" + "interface " + interfaces.get(0).getIfName() + "\n"
//                                + " +   ip address " + interfaces.get(0).getIfIpAddr()
//                                + " " + interfaces.get(0).getSubnetMask() + "\n #";
                        //+ "+   ip binding vpn-instance "+vpnName + "\n"
//                        LOG.info(result);
                    } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
                        LOG.info("delete");
                        List<Interface> interfaces = GetXml.getInterface(ifmRunningResult, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                        LOG.info(interfaces.toString());
//                        result = result + "#2" + "\n" + "interface " + interfaces.get(0).getIfName() + "\n"
//                                + " -   ip address " + interfaces.get(0).getIfIpAddr()
//                                + " " + interfaces.get(0).getSubnetMask() + "\n #";
                        // + "-   ip binding vpn-instance "+vpnName + "\n"
//                        LOG.info(result);
                    } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
                        List<ModifyEntity> modifyEntities = actionEntity.getModifyEntities();
                        for (ModifyEntity modifyEntity : modifyEntities) {
                            List<Interface> interfaces = GetXml.getInterface(ifmCandidateResult, AttributeParse.parse(modifyEntity.getPath()), actionEntity.getAction());
                            LOG.info(interfaces.get(0).toString());
                            LOG.info("lable :" + modifyEntity.getLabel());
                            LOG.info("modifyEntity.getOdlValue() :" + modifyEntity.getOdlValue());
                            LOG.info("modifyEntity.getNewValue() :" + modifyEntity.getNewValue());
                            if ("ifIpAddr".equals(modifyEntity.getLabel())) {
                                result = result + "#" + "\n" + "   interface " + interfaces.get(0).getIfName() + "\n"
                                        + " -   ip address " + modifyEntity.getOdlValue()
                                        + " " + interfaces.get(0).getSubnetMask();
                                // + "-   ip binding vpn-instance "+vpnName + "\n"
                                result = result + "\n +   ip address " + modifyEntity.getNewValue()
                                        + " " + interfaces.get(0).getSubnetMask() + "\n #";
                                LOG.info(result);
                            }
                        }
                    } else if (ActionTypeEnum.identical.name().equals(actionEntity.getAction().name())) {
                        LOG.info("modifyEntity action() :" + actionEntity.getAction());
                    }
                }
            }


            {
                String ebgpRunningXml = RunningXml.getEbgpXml();
                LOG.info("ebgpRunningXml :" + ebgpRunningXml);
                String ebgpCandidateXml = CandidateXml.getEbgpXml();
                LOG.info("ebgpCandidateXml :" + ebgpCandidateXml);
                String xmlEbgpRunningResult = netconfController.sendMessage(netconfClient, ebgpRunningXml);
                String xmlEbgpCandidateResult = netconfController.sendMessage(netconfClient, ebgpCandidateXml);
                //xml1 candidate  xml2 running
                flag = "explicitPath";
                xmlEbgpCandidateResult = XmlUtils.subString(xmlEbgpCandidateResult);
                xmlEbgpRunningResult = XmlUtils.subString(xmlEbgpRunningResult);
                actionEntity = XmlUtils.compare(xmlEbgpCandidateResult, xmlEbgpRunningResult, flag);
                String asXml = EbgpXml.getAsXml();
                String asResult = netconfController.sendMessage(netconfClient, asXml);
                String as = EbgpXml.getAsFromXml(asResult);
                if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
                    LOG.info("add");
                    List<BgpVrf> bgpVrfs = GetXml.getEbgpFromXml(xmlEbgpCandidateResult, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                    if (bgpVrfs.size() != 0 && (AttributeParse.parse(actionEntity.getPath()).get(AttributeParse.parse(actionEntity.getPath()).size() - 2).getName().equals("peerAF")
                            || AttributeParse.parse(actionEntity.getPath()).get(AttributeParse.parse(actionEntity.getPath()).size() - 2).getName().equals("advertiseCommunity"))) {
                        if (bgpVrfs.get(0).getBgpVrfAFs().size() > 0 && bgpVrfs.get(0).getBgpPeers().size() > 0) {
                            result = result + "# \n bgp " + as + " \n" +
                                    "+   ipv4-family vpn-instance " + bgpVrfs.get(0).getVrfName();
                            if (bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().size() > 0) {
                                if (null != bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getImportRtPolicyName() &&
                                        AttributeParse.parse(actionEntity.getPath()).get(AttributeParse.parse(actionEntity.getPath()).size() - 1).getIndex() == 3) {
                                    result = result + "\n+   peer " + bgpVrfs.get(0).getBgpPeers().get(0).getPeerAddr() + " route-policy " + bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getImportRtPolicyName() + " import";
                                }
                                if (null != bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getExportRtPolicyName() &&
                                        AttributeParse.parse(actionEntity.getPath()).get(AttributeParse.parse(actionEntity.getPath()).size() - 1).getIndex() == 4) {
                                    result = result + "\n+   peer " + bgpVrfs.get(0).getBgpPeers().get(0).getPeerAddr() + " route-policy " + bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getExportRtPolicyName() + " export";
                                }
                                if ("true".equals(bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getAdvertiseCommunity())) {
                                    result = result + "\n+   peer " + bgpVrfs.get(0).getBgpPeers().get(0).getPeerAddr() + " advertise-community" + "\n #";
                                } else {
                                    result = result + "\n#";
                                }
                            }
                        }
                    } else {
                        if (bgpVrfs.size() != 0 && bgpVrfs.get(0).getBgpVrfAFs().size() > 0 && bgpVrfs.get(0).getBgpPeers().size() > 0) {
                            LOG.info(bgpVrfs.get(0).toString());
                            result = result + "# \n bgp " + as + " \n" +
                                    "   ipv4-family vpn-instance " + bgpVrfs.get(0).getVrfName() +
                                    "\n+   preference " + bgpVrfs.get(0).getBgpVrfAFs().get(0).getPreferenceExternal() +
                                    " " + bgpVrfs.get(0).getBgpVrfAFs().get(0).getPreferenceInternal() +
                                    " " + bgpVrfs.get(0).getBgpVrfAFs().get(0).getPreferenceLocal() +
                                    "\n+   peer " + bgpVrfs.get(0).getBgpPeers().get(0).getPeerAddr() + " as-number " + bgpVrfs.get(0).getBgpPeers().get(0).getRemoteAs();
                            if (bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().size() > 0) {
                                if (null != bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getImportRtPolicyName()) {
                                    result = result + "\n+   peer " + bgpVrfs.get(0).getBgpPeers().get(0).getPeerAddr() + " route-policy " + bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getImportRtPolicyName() + " import";
                                }
                                if (null != bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getExportRtPolicyName()) {
                                    result = result + "\n+   peer " + bgpVrfs.get(0).getBgpPeers().get(0).getPeerAddr() + " route-policy " + bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getExportRtPolicyName() + " export";
                                }
                                if ("true".equals(bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getAdvertiseCommunity())) {
                                    result = result + "\n+   peer " + bgpVrfs.get(0).getBgpPeers().get(0).getPeerAddr() + " advertise-community" + "\n #";
                                } else {
                                    result = result + "\n#";
                                }
                            }
                        }
                    }
                } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
                    LOG.info("delete");
                    List<BgpVrf> bgpVrfs = GetXml.getEbgpFromXml(xmlEbgpRunningResult, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                    if (bgpVrfs.size() != 0 && bgpVrfs.get(0).getBgpVrfAFs().size() > 0 && bgpVrfs.get(0).getBgpPeers().size() > 0) {
                        LOG.info(bgpVrfs.get(0).toString());
                        result = result + "# \n bgp " + as + " \n" +
                                "-   ipv4-family vpn-instance " + bgpVrfs.get(0).getVrfName() +
                                "\n-   preference " + bgpVrfs.get(0).getBgpVrfAFs().get(0).getPreferenceExternal() +
                                " " + bgpVrfs.get(0).getBgpVrfAFs().get(0).getPreferenceInternal() +
                                " " + bgpVrfs.get(0).getBgpVrfAFs().get(0).getPreferenceLocal() +
                                "\n-   peer " + bgpVrfs.get(0).getBgpPeers().get(0).getPeerAddr() + " as-number " + bgpVrfs.get(0).getBgpPeers().get(0).getRemoteAs();
                        if ((bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().size() > 0)) {
                            if (null != bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getImportRtPolicyName()) {
                                result = result + "\n-   peer " + bgpVrfs.get(0).getBgpPeers().get(0).getPeerAddr() + " route-policy " + bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getImportRtPolicyName() + " import";
                            }
                            if (null != bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getExportRtPolicyName()) {
                                result = result + "\n-   peer " + bgpVrfs.get(0).getBgpPeers().get(0).getPeerAddr() + " route-policy " + bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getExportRtPolicyName() + " export";
                            }
                            if ("true".equals(bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getAdvertiseCommunity())) {
                                result = result + "\n-   peer " + bgpVrfs.get(0).getBgpPeers().get(0).getPeerAddr() + " advertise-community" + "\n #";
                            } else {
                                result = result + "\n#";
                            }
                        }
                    }
                } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
                    List<BgpVrf> bgpVrfs = GetXml.getEbgpFromXml(xmlEbgpCandidateResult, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                    LOG.info("modify");
                    if (bgpVrfs.size() != 0) {
                        LOG.info(bgpVrfs.get(0).getVrfName());
                        String peerAddrOld = "";
                        String peerAddrNew = "";
                        String asOld = "";
                        String asNew = "";
                        if (bgpVrfs.get(0).getBgpPeers().size() > 0) {
                            peerAddrOld = bgpVrfs.get(0).getBgpPeers().get(0).getPeerAddr();
                            asOld = bgpVrfs.get(0).getBgpPeers().get(0).getRemoteAs();
                        }
                        peerAddrNew = peerAddrOld;
                        asNew = asOld;
                        String ipv4Family = "\n   ipv4-family vpn-instance " + bgpVrfs.get(0).getVrfName();
                        String remove = "";
                        String add = "";
                        result = result + "\n # \n   bgp " + as;
                        List<ModifyEntity> modifyEntities = actionEntity.getModifyEntities();
                        String preferenceExternal = bgpVrfs.get(0).getBgpVrfAFs().get(0).getPreferenceExternal();
                        String preferenceInternal = bgpVrfs.get(0).getBgpVrfAFs().get(0).getPreferenceInternal();
                        String preferenceLocal = bgpVrfs.get(0).getBgpVrfAFs().get(0).getPreferenceLocal();
                        String preferenceExternalNew = preferenceExternal;
                        String preferenceInternalNew = preferenceInternal;
                        String preferenceLocalNew = preferenceLocal;
                        String importOld="";
                        String exportOld="";
                        String communityOld="";
                        if(bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().size()>0){
                            importOld = bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getImportRtPolicyName();
                            exportOld = bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getExportRtPolicyName();
                            communityOld=bgpVrfs.get(0).getBgpVrfAFs().get(0).getPeerAFs().get(0).getAdvertiseCommunity();
                        }
                        String importNew = importOld;
                        String exportNew = exportOld;
                        String communityNew=communityOld;
                        boolean perferenceFlag = false;
                        boolean peerIpOnlyFlag = false;
                        for (ModifyEntity modifyEntity : modifyEntities) {
                            LOG.info("lable :" + modifyEntity.getLabel());
                            LOG.info("modifyEntity.getOdlValue() :" + modifyEntity.getOdlValue());
                            LOG.info("modifyEntity.getNewValue() :" + modifyEntity.getNewValue());
                            if ("peerAddr".equals(modifyEntity.getLabel())) {
                                peerIpOnlyFlag = true;
                                peerAddrOld = modifyEntity.getOdlValue();
                                peerAddrNew = modifyEntity.getNewValue();
                            }
                            if ("preferenceExternal".equals(modifyEntity.getLabel())) {
                                preferenceExternal = modifyEntity.getOdlValue();
                                preferenceExternalNew = modifyEntity.getNewValue();
                                perferenceFlag = true;
                            }
                            if ("preferenceInternal".equals(modifyEntity.getLabel())) {
                                preferenceInternal = modifyEntity.getOdlValue();
                                preferenceInternalNew = modifyEntity.getNewValue();
                                perferenceFlag = true;
                            }
                            if ("preferenceLocal".equals(modifyEntity.getLabel())) {
                                preferenceLocal = modifyEntity.getOdlValue();
                                preferenceLocalNew = modifyEntity.getNewValue();
                                perferenceFlag = true;
                            }
                            if ("remoteAs".equals(modifyEntity.getLabel())) {
                                asOld=modifyEntity.getOdlValue();
                                asNew=modifyEntity.getNewValue();
                                remove = remove + "\n -   peer " + peerAddrOld + " as-number " + modifyEntity.getOdlValue();
                                add = add + "\n +   peer " + peerAddrNew + " as-number " + modifyEntity.getNewValue();
                            }
                            if ("importRtPolicyName".equals(modifyEntity.getLabel())) {
                                importOld=modifyEntity.getOdlValue();
                                importNew=modifyEntity.getNewValue();
                                remove = remove + "\n -   peer " + peerAddrOld + " route-policy " + modifyEntity.getOdlValue() + " import";
                                add = add + "\n +   peer " + peerAddrNew + " route-policy " + modifyEntity.getNewValue() + " import";
                            }
                            if ("exportRtPolicyName".equals(modifyEntity.getLabel())) {
                                exportOld=modifyEntity.getOdlValue();
                                exportNew=modifyEntity.getNewValue();
                                remove = remove + "\n -   peer " + peerAddrOld + " route-policy " + modifyEntity.getOdlValue() + " export";
                                add = add + "\n +   peer " + peerAddrNew + " route-policy " + modifyEntity.getNewValue() + " export";
                            }
                            if ("advertiseCommunity".equals(modifyEntity.getLabel())) {
                                communityOld=modifyEntity.getOdlValue();
                                communityNew=modifyEntity.getNewValue();
                                if ("true".equals(modifyEntity.getNewValue())) {
                                    add = add + "\n +   peer " + peerAddrNew + " advertise-community ";
                                }
                                if ("true".equals(modifyEntity.getOdlValue())) {
                                    remove = remove + "\n -   peer " + peerAddrOld + " advertise-community ";
                                }
                            }
                        }

                        if (peerIpOnlyFlag) {
                            remove="";
                            add="";
                            remove = remove + "\n -   peer " + peerAddrOld + " as-number " + asOld;
                            add = add + "\n +   peer " + peerAddrNew + " as-number " + asNew;
                            if(!("".equals(importOld))){
                                remove = remove + "\n -   peer " + peerAddrOld + " route-policy " + importOld + " import";
                            }
                            if(!("".equals(importNew))){
                                add = add + "\n +   peer " + peerAddrNew + " route-policy " + importNew + " import";
                            }
                            if(!("".equals(exportOld))){
                                remove = remove + "\n -   peer " + peerAddrOld + " route-policy " + exportOld + " export";
                            }
                            if(!("".equals(exportNew))){
                                add = add + "\n +   peer " + peerAddrNew + " route-policy " + exportNew + " export";
                            }
                            if ("true".equals(communityNew)) {
                                add = add + "\n +   peer " + peerAddrNew + " advertise-community ";
                            }
                            if ("true".equals(communityOld)) {
                                remove = remove + "\n -   peer " + peerAddrOld + " advertise-community ";
                            }
                        }
                        if (perferenceFlag) {
                            if ("255".equals(preferenceExternal) && "255".equals(preferenceInternal) && "255".equals(preferenceLocal)) {
                            } else {
                                remove = "\n -   preference " + preferenceExternal + " " + preferenceInternal + " " + preferenceLocal + remove;
                            }
                            add = "\n +   preference " + preferenceExternalNew + " " + preferenceInternalNew + " " + preferenceLocalNew + add;
                        }
                        result += ipv4Family + remove + ipv4Family + add + "\n #";

                    }
                } else if (ActionTypeEnum.identical.name().equals(actionEntity.getAction().name())) {
                    LOG.info("modifyEntity action() :" + actionEntity.getAction());
                }
            }
        }
        LOG.info(result);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.BODY.getName(), result);
        return resultMap;
    }

    @Override
    public Map<String, Object> lable(List<String> routers) {
        Map<String, Object> resultMap = new HashMap<>();
        String result = "";
        String flag;
        ActionEntity actionEntity;
        for (String routerId : routers) {
            result = result + "\nrouter : " + routerId + "\n";
            synType = "3";
            NetconfClient netconfClient = netConfManager.getNetconClient(routerId);
            {
                String areasRunningXml = RunningXml.getAreasXml();
                LOG.info("areasRunningXml :" + areasRunningXml);
                String areasCandidateXml = CandidateXml.getAreasXml();
                LOG.info("areasCandidateXml :" + areasCandidateXml);
                String xmlRunningResult = netconfController.sendMessage(netconfClient, areasRunningXml);
                String xmlCandidateResult = netconfController.sendMessage(netconfClient, areasCandidateXml);
                String xml1 = xmlRunningResult;
                String xml2 = xmlCandidateResult;
                xml1 = XmlUtils.subString(xml1);
                xml2 = XmlUtils.subString(xml2);
                //xml1 candidate  xml2 running
                flag = "explicitPath";
                actionEntity = XmlUtils.compare(xml1, xml2, flag);
                if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
                    LOG.info("add");
                    NetconfSrLabelInfo netconfSrLabelInfo = GetXml.getSrNodeLabelFromgSrNodeLabelXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                    LOG.info(netconfSrLabelInfo.toString());
                } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
                    LOG.info("delete");
                    NetconfSrLabelInfo netconfSrLabelInfo = GetXml.getSrNodeLabelFromgSrNodeLabelXml(xml2, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                    LOG.info(netconfSrLabelInfo.toString());
                } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
                    NetconfSrLabelInfo netconfSrLabelInfo = GetXml.getSrNodeLabelFromgSrNodeLabelXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                    LOG.info(netconfSrLabelInfo.getPrefixIfName());
                    List<ModifyEntity> modifyEntities = actionEntity.getModifyEntities();
                    for (ModifyEntity modifyEntity : modifyEntities) {
                        LOG.info("lable :" + modifyEntity.getLabel());
                        LOG.info("modifyEntity.getOdlValue() :" + modifyEntity.getOdlValue());
                        LOG.info("modifyEntity.getNewValue() :" + modifyEntity.getNewValue());
                    }
                } else if (ActionTypeEnum.identical.name().equals(actionEntity.getAction().name())) {
                    LOG.info("modifyEntity action() :" + actionEntity.getAction());
                }
            }


            {
                String srgbRunningXml = RunningXml.getSrgbXml();
                LOG.info("srgbRunningXml :" + srgbRunningXml);
                String srgbCandidateXml = CandidateXml.getSrgbXml();
                LOG.info("srgbCandidateXml :" + srgbCandidateXml);
                String xml1 = netconfController.sendMessage(netconfClient, srgbCandidateXml);
                String xml2 = netconfController.sendMessage(netconfClient, srgbRunningXml);
                xml1 = XmlUtils.subString(xml1);
                xml2 = XmlUtils.subString(xml2);
                //xml1 candidate  xml2 running
                flag = "explicitPath";
                actionEntity = XmlUtils.compare(xml1, xml2, flag);
                if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
                    LOG.info("add");
                    NetconfSrLabelInfo netconfSrLabelInfo = GetXml.getSrNodeLabelRangeFromNodeLabelRangeXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                    LOG.info(netconfSrLabelInfo.toString());
                } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
                    LOG.info("delete");
                    NetconfSrLabelInfo netconfSrLabelInfo = GetXml.getSrNodeLabelRangeFromNodeLabelRangeXml(xml2, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                    LOG.info(netconfSrLabelInfo.toString());
                } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
                    List<ModifyEntity> modifyEntities = actionEntity.getModifyEntities();
                    for (ModifyEntity modifyEntity : modifyEntities) {
                        LOG.info("lable :" + modifyEntity.getLabel());
                        LOG.info("modifyEntity.getOdlValue() :" + modifyEntity.getOdlValue());
                        LOG.info("modifyEntity.getNewValue() :" + modifyEntity.getNewValue());
                    }
                } else if (ActionTypeEnum.identical.name().equals(actionEntity.getAction().name())) {
                    LOG.info("modifyEntity action() :" + actionEntity.getAction());
                }
            }

            {
                String segrRunningXml = RunningXml.getSegrXml();
                LOG.info("segrRunningXml :" + segrRunningXml);
                String segrCandidateXml = CandidateXml.getSegrXml();
                LOG.info("segrCandidateXml :" + segrCandidateXml);
                String xml1 = netconfController.sendMessage(netconfClient, segrRunningXml);
                String xml2 = netconfController.sendMessage(netconfClient, segrCandidateXml);
                xml1 = XmlUtils.subString(xml1);
                xml2 = XmlUtils.subString(xml2);
                //xml1 candidate  xml2 running
                flag = "explicitPath";
                actionEntity = XmlUtils.compare(xml1, xml2, flag);
                if (ActionTypeEnum.add.name().equals(actionEntity.getAction().name())) {
                    LOG.info("add");
                    NetconfSrLabelInfo netconfSrLabelInfo = GetXml.getSrSRGBRangeFromXml(xml1, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                    LOG.info(netconfSrLabelInfo.toString());
                } else if (ActionTypeEnum.delete.name().equals(actionEntity.getAction().name())) {
                    LOG.info("delete");
                    NetconfSrLabelInfo netconfSrLabelInfo = GetXml.getSrSRGBRangeFromXml(xml2, AttributeParse.parse(actionEntity.getPath()), actionEntity.getAction());
                    LOG.info(netconfSrLabelInfo.toString());
                } else if (ActionTypeEnum.modify.name().equals(actionEntity.getAction().name())) {
                    List<ModifyEntity> modifyEntities = actionEntity.getModifyEntities();
                    for (ModifyEntity modifyEntity : modifyEntities) {
                        LOG.info("lable :" + modifyEntity.getLabel());
                        LOG.info("modifyEntity.getOdlValue() :" + modifyEntity.getOdlValue());
                        LOG.info("modifyEntity.getNewValue() :" + modifyEntity.getNewValue());
                    }
                } else if (ActionTypeEnum.identical.name().equals(actionEntity.getAction().name())) {
                    LOG.info("modifyEntity action() :" + actionEntity.getAction());
                }
            }

        }
        resultMap.put(ResponseEnum.BODY.getName(), result);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        return resultMap;
    }

    @Override
    public Map<String, Object> tunnel(List<String> routers) {
        return null;
    }

    @Override
    public Map<String, Object> confirm(List<String> routers) {
        Map<String, Object> resultMap = new HashMap<>();
        String confirmXml = ActionCfgXml.getCommitCfgXml();
        String result = "";
        NetconfClient netconfClient;
        for (String routerId : routers) {
//            result = result + "\n routerId : " + routerId + "\n";
            netconfClient = netConfManager.getNetconClient(routerId);
            result = netconfController.sendMessage(netconfClient, confirmXml);
            result = CheckXml.checkOk(result);
            result = result + "\n";
            LOG.info(result);
        }
        syn(synType);
        resultMap.put(ResponseEnum.BODY.getName(), result);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        return resultMap;
    }

    @Override
    public Map<String, Object> cancel(List<String> routers) {
        Map<String, Object> resultMap = new HashMap<>();
        String cancelXml = ActionCfgXml.getCancelCfgXml();
        String result = "";
        NetconfClient netconfClient;
        for (String routerId : routers) {
            result = result + "\n routerId : " + routerId + "\n";
            netconfClient = netConfManager.getNetconClient(routerId);
            result = netconfController.sendMessage(netconfClient, cancelXml);
            result = CheckXml.checkOk(result);
            result = result + "\n";
            LOG.info(result);
        }
        syn(synType);
        resultMap.put(ResponseEnum.BODY.getName(), result);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        return resultMap;
    }

    private void syn(String type) {
        if (type.equals("2")) {
            VPNServiceImpl.getInstance().syncVpnInstanceConf();
            RoutePolicyServiceImpl.getInstance().syncRoutePolicyConf();
        }
        InterfaceServiceImpl.getInstance().syncInterfaceConf();
        synType = "0";
    }

    @Override
    public Map<String, Object> getCfgChane(String routerId, String cfgType) {
        Map<String, Object> resultMap = new HashMap<>();
        List<CommandLine> commandLineList = new ArrayList<>();
        List<Device> deviceList = new ArrayList<>();
        Device device = deviceManager.getDevice(routerId);
        if (device == null) {
            deviceList = deviceManager.getDeviceList();
        } else {
            deviceList.add(device);
        }
        for (Device d : deviceList) {
            CommandLine commandLine = new CommandLine();
            String candidateCfg = this.getCandidateCfgXml(d, cfgType);
            String runningCfg = this.getRunningCfgXml(d, cfgType);
            LOG.info("candidateCfg : \n" + candidateCfg);
            LOG.info("runningCfg : \n" + runningCfg);
            commandLine.setDeviceName(d.getDeviceName());
            commandLine.setRouterId(d.getRouterId());
            if (cfgType.equals(CfgTypeEnum.SR_LABEL.getCfgType())) {
                commandLine.getCliList().addAll(SrlabelCli.srLabelCfgCli(candidateCfg, runningCfg));
            } else if (cfgType.equals(CfgTypeEnum.VPN.getCfgType())) {
                commandLine.getCliList().addAll(VpnCli.vpnCfgCli(candidateCfg, runningCfg));
            } else if (cfgType.equals(CfgTypeEnum.SR_TUNNEL.getCfgType())) {
                commandLine.getCliList().addAll(TunnelCli.tunnelCfgCli(candidateCfg, runningCfg));
            } else {
                String xml1 = Util.candidate();
                String xml2 = Util.modify();
                commandLine.getCliList().addAll(TunnelCli.tunnelCfgCliTest(xml1, xml2));
                commandLine.getCliList().addAll(SrlabelCli.srLabelCfgCliTest());
            }
            if (commandLine.getCliList().size() != 0) {
                commandLineList.add(commandLine);
            }
        }
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.MESSAGE.getName(), commandLineList);
        return resultMap;
    }

    @Override
    public Map<String, Object> commitCfgChane(String routerId, String cfgType) {
        Map<String, Object> resultMap = new HashMap<>();
        String commitXml = ActionCfgXml.getCommitCfgXml();
        List<Device> deviceList = new ArrayList<>();
        Device device = deviceManager.getDevice(routerId);
        if (device == null) {
            deviceList = deviceManager.getDeviceList();
        } else {
            deviceList.add(device);
        }
        for (Device d : deviceList) {
            NetconfClient netconfClient = netConfManager.getNetconClient(d.getNetConf().getRouterID());
            String outPutXml = netconfController.sendMessage(netconfClient, commitXml);
            LOG.info(outPutXml);
        }
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        return resultMap;
    }

    @Override
    public Map<String, Object> cancelCfgChane(String routerId, String cfgType) {
        Map<String, Object> resultMap = new HashMap<>();
        String cancelXml = ActionCfgXml.getCancelCfgXml();
        List<Device> deviceList = new ArrayList<>();
        Device device = deviceManager.getDevice(routerId);
        if (device == null) {
            deviceList = deviceManager.getDeviceList();
        } else {
            deviceList.add(device);
        }
        for (Device d : deviceList) {
            NetconfClient netconfClient = netConfManager.getNetconClient(d.getNetConf().getRouterID());
            String outPutXml = netconfController.sendMessage(netconfClient, cancelXml);
            LOG.info(outPutXml);
        }
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        return resultMap;
    }

    private String getCandidateCfgXml(Device device, String cfgType) {
        String xml = null;
        if (cfgType.equals(CfgTypeEnum.SR_LABEL.getCfgType())) {
            xml = CandidateXml.getCandidateSrLabelXml();
        } else if (cfgType.equals(CfgTypeEnum.VPN.getCfgType())) {
            xml = CandidateXml.getCandidateVpnXml();
        } else if (cfgType.equals(CfgTypeEnum.SR_TUNNEL.getCfgType())) {
            xml = CandidateXml.getCandidateTunnelXml();
        } else {
//            xml = CandidateXml.getCandidateXml();
            return null;
        }
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
        String outPutXml = netconfController.sendMessage(netconfClient, xml);
        return outPutXml;
    }

    private String getRunningCfgXml(Device device, String cfgType) {
        String xml;
        if (cfgType.equals(CfgTypeEnum.SR_LABEL.getCfgType())) {
            xml = RunningXml.getRunningSrLabelXml();
        } else if (cfgType.equals(CfgTypeEnum.VPN.getCfgType())) {
            xml = RunningXml.getRunningVpnXml();
        } else if (cfgType.equals(CfgTypeEnum.SR_TUNNEL.getCfgType())) {
            xml = RunningXml.getRunningTunnelXml();
        } else {
//            xml = RunningXml.getRunningXml();
            return null;
        }
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getRouterID());
        String outPutXml = netconfController.sendMessage(netconfClient, xml);
        return outPutXml;
    }

    @Override
    public Map<String, List<CheckPointInfoServiceEntity>> getCfgCommitPointInfo(String routerId, String commitId) {
        Map<String, List<CheckPointInfoServiceEntity>> checkInfoMap = new HashMap<>();
        List<Device> deviceList = new ArrayList<>();
        Device device = deviceManager.getDevice(routerId);
        if (device == null) {
            deviceList = deviceManager.getDeviceList();
        } else {
            deviceList.add(device);
        }
        for (Device d : deviceList) {
            String getCommitXml = ActionCfgXml.getCheckPointInfoXml(commitId);
            LOG.info(d.getRouterId() + " getCommitXml: " + getCommitXml);
            NetconfClient netconfClient = netConfManager.getNetconClient(d.getNetConf().getRouterID());
            String outPutCommitXml = netconfController.sendMessage(netconfClient, getCommitXml);
            LOG.info(d.getRouterId() + " outPutCommitXml: " + outPutCommitXml);
            List<SCheckPointInfo> sCheckPointInfoList = ActionCfgXml.getCheckPointInfoFromXml(outPutCommitXml);
            List<CheckPointInfoServiceEntity> checkPointInfoServiceEntityList = sCheckPointInfoToCheckPointInfoServiceEntity(sCheckPointInfoList);
            checkInfoMap.put(d.getRouterId(), checkPointInfoServiceEntityList);
        }
        return checkInfoMap;
    }

    @Override
    public Map<String, Object> rollbackToCommitId(String routerId, String commitId) {
        Map<String, Object> resultMap = new HashMap<>();
        Device device = deviceManager.getDevice(routerId);
        if (device == null) {
            return null;
        }
        String getCommitXml = ActionCfgXml.getRollBackToCommitIdXml(commitId);
        LOG.info("getCommitXml: " + getCommitXml);
        NetconfClient netconfClient = netConfManager.getNetconClient(routerId);
        String outPutCommitXml = netconfController.sendMessage(netconfClient, getCommitXml);
        LOG.info("outPutCommitXml: " + outPutCommitXml);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        return resultMap;
    }

    private List<CheckPointInfoServiceEntity> sCheckPointInfoToCheckPointInfoServiceEntity(List<SCheckPointInfo> sCheckPointInfoList) {
        List<CheckPointInfoServiceEntity> checkPointInfoServiceEntityList = new ArrayList<>();
        for (SCheckPointInfo sCheckPointInfo : sCheckPointInfoList) {
            CheckPointInfoServiceEntity checkPointInfoServiceEntity = new CheckPointInfoServiceEntity();
            checkPointInfoServiceEntity.setCommitId(sCheckPointInfo.getCommitId());
            checkPointInfoServiceEntity.setUserLabel(sCheckPointInfo.getUserLabel());
            checkPointInfoServiceEntity.setUserName(sCheckPointInfo.getUserName());
            checkPointInfoServiceEntity.setTimeStamp(sCheckPointInfo.getTimeStamp());
            checkPointInfoServiceEntity.setSinceList(this.sPointChangeInfoToPointChangeInfoServiceEntity(sCheckPointInfo.getSinceList()));
            checkPointInfoServiceEntity.setCurList(this.sPointChangeInfoToPointChangeInfoServiceEntity(sCheckPointInfo.getCurrList()));
            checkPointInfoServiceEntityList.add(checkPointInfoServiceEntity);
        }
        return checkPointInfoServiceEntityList;
    }

    private List<PointChangeInfoServiceEntity> sPointChangeInfoToPointChangeInfoServiceEntity(List<SPointChangeInfo> sPointChangeInfoList) {
        List<PointChangeInfoServiceEntity> pointChangeInfoServiceEntityList = new ArrayList<>();
        for (SPointChangeInfo sPointChangeInfo : sPointChangeInfoList) {
            PointChangeInfoServiceEntity pointChangeInfoServiceEntity = new PointChangeInfoServiceEntity();
            pointChangeInfoServiceEntity.setIndex(sPointChangeInfo.getIndex());
            pointChangeInfoServiceEntity.setChange(sPointChangeInfo.getChange());
            pointChangeInfoServiceEntityList.add(pointChangeInfoServiceEntity);
        }
        return pointChangeInfoServiceEntityList;
    }

}
