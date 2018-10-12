package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.entity.AdjLabel;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.NodeLabel;
import cn.org.upbnc.entity.OspfProcess;
import cn.org.upbnc.service.SrLabelService;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.NetconfSrLabelInfo;
import cn.org.upbnc.util.xml.CheckXml;
import cn.org.upbnc.util.xml.SrLabelXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class SrLabelServiceImpl implements SrLabelService {
    private static final Logger LOG = LoggerFactory.getLogger(SRServiceImpl.class);
    private static SrLabelService ourInstance = null;
    private BaseInterface baseInterface;
    private NetConfManager netConfManager = null;
    private DeviceManager deviceManager = null;

    public SrLabelServiceImpl() {
        this.baseInterface = null;
        this.netConfManager = null;
        this.deviceManager = null;
    }

    public static SrLabelService getInstance() {
        if (ourInstance == null){
            ourInstance = new SrLabelServiceImpl();
        }
        return ourInstance;
    }
    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        if(null != baseInterface) {
            netConfManager = this.baseInterface.getNetConfManager();
            deviceManager = this.baseInterface.getDeviceManager();
        }
        return true;
    }

    @Override
    public String updateNodeLabel(String routerId, String labelVal) {
        Device device = null;
        LOG.info("updateNodeLabel begin");
        device = deviceManager.getDevice(routerId);
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        String commandSetSrNodeLabelXml = SrLabelXml.setSrNodeLabelXml("","","","","");
        LOG.info("command xml: " + commandSetSrNodeLabelXml);
        String outPutXml = netconfController.sendMessage(netconfClient,commandSetSrNodeLabelXml);
        if (CheckXml.checkOk(outPutXml).equals("ok")){

        }
        LOG.info("updateNodeLabel end");
        return null;
    }

    @Override
    public String updateNodeLabelRange(String routerId, String labelBegin, String labelEnd) {
        Device device = null;
        LOG.info("updateNodeLabelRange begin");
        device = deviceManager.getDevice(routerId);
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        String commandSetSrNodeLabelRangeXml = SrLabelXml.setSrNodeLabelRangeXml("",labelBegin,labelEnd);
        LOG.info("command xml: " + commandSetSrNodeLabelRangeXml);
        String outPutXml = netconfController.sendMessage(netconfClient,commandSetSrNodeLabelRangeXml);
        if (CheckXml.checkOk(outPutXml).equals("ok")){

        }
        LOG.info("updateNodeLabelRange end");
        return null;
    }

    @Override
    public String syncNodeLabel(String routerId) {
        Device device = null;
        LOG.info("syncNodeLabel begin " + routerId);
        device = deviceManager.getDevice(routerId);
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        String commandGetSrNodeLabelXml = SrLabelXml.getSrNodeLabelXml();
        LOG.info("command xml: " + commandGetSrNodeLabelXml);
        String outPutXml = netconfController.sendMessage(netconfClient,commandGetSrNodeLabelXml);
        LOG.info("output xml: " + outPutXml);
        NetconfSrLabelInfo netconfSrLabelInfo = SrLabelXml.getSrNodeLabelFromgSrNodeLabelXml(outPutXml);
        NodeLabel nodeLabel = new NodeLabel();
        OspfProcess ospfProcess = new OspfProcess();
        ospfProcess.setAreaId(netconfSrLabelInfo.getOspfAreaId());
        ospfProcess.setProcessId(Integer.parseInt(netconfSrLabelInfo.getOspfProcessId()));
        nodeLabel.setValue(Integer.parseInt(netconfSrLabelInfo.getPrefixLabel()));
        device.setNodeLabel(nodeLabel);
        device.setOspfProcess(ospfProcess);

        String commandGetSrNodeLabelRangeXml = SrLabelXml.getSrNodeLabelRangeXml();
        LOG.info("command sid range xml: " + commandGetSrNodeLabelRangeXml);
        String outPutLabelRange = netconfController.sendMessage(netconfClient,commandGetSrNodeLabelRangeXml);
        LOG.info("command out sid range xml: " + outPutLabelRange);
        netconfSrLabelInfo = SrLabelXml.getSrNodeLabelRangeFromNodeLabelRangeXml(outPutLabelRange);
        device.setMinNodeSID(Integer.parseInt(netconfSrLabelInfo.getSrgbBegin()));
        device.setMaxNodeSID(Integer.parseInt(netconfSrLabelInfo.getSrgbEnd()));
        LOG.info("syncNodeLabel end " + routerId);
        return null;
    }

    public String syncNodeLabel() {
        LOG.info("syncNodeLabel begin");
        if(this.deviceManager !=null ) {
            for (Device device : this.deviceManager.getDeviceList()) {
                this.syncNodeLabel(device.getRouterId());
            }
        }
        LOG.info("syncNodeLabel end");
        return null;
    }

    @Override
    public String updateIntfLabel(String routerId, String localAddress, String remoteAddress, String labelVal) {
        Device device = null;
        LOG.info("updateIntfLabel begin");
        device = deviceManager.getDevice(routerId);
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        String commandUpdateXml = SrLabelXml.setSrAdjLabelXml(SrLabelXml.ncOperationCreate,localAddress,remoteAddress,labelVal);
        LOG.info("command xml: " + commandUpdateXml);
        String outPutXml = netconfController.sendMessage(netconfClient,commandUpdateXml);
        LOG.info("output xml: " + outPutXml);
        if (CheckXml.checkOk(outPutXml).equals("ok")){

        }
        LOG.info("updateIntfLabel end");
        return null;
    }

    @Override
    public String syncIntfLabel() {
        LOG.info("syncIntfLael begin");
        if(this.deviceManager !=null ) {
            for (Device device:this.deviceManager.getDeviceList()) {
                NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
                String commandGetSrAdjLabelRangeXml = SrLabelXml.getSrAdjLabelRangeXml();
                LOG.info("command xml: " + commandGetSrAdjLabelRangeXml);
                String outPutAdjLabelRangeXml = netconfController.sendMessage(netconfClient,commandGetSrAdjLabelRangeXml);
                LOG.info("output xml: " + outPutAdjLabelRangeXml);
                NetconfSrLabelInfo netconfSrLabelInfo = SrLabelXml.getAdjLabelRangeFromAdjLabelRangeXml(outPutAdjLabelRangeXml);
                device.setMinAdjSID(Integer.parseInt(netconfSrLabelInfo.getAdjLowerSid()));
                device.setMaxAdjSID(Integer.parseInt(netconfSrLabelInfo.getAdjUpperSid()));
                String commandGetSrAdjLabelXml = SrLabelXml.getSrAdjLabelXml();
                LOG.info("command xml: " + commandGetSrAdjLabelXml);
                String outPutXml = netconfController.sendMessage(netconfClient, commandGetSrAdjLabelXml);
                LOG.info("output xml: " + outPutXml);
                if (CheckXml.checkOk(outPutXml).equals("ok")) {
                    List<AdjLabel> adjLabelList = SrLabelXml.getSrAdjLabelFromSrAdjLabelXml(outPutXml);
                    device.setAdjLabelList(adjLabelList);
                }
            }
        }
        LOG.info("syncIntfLael end");
        return null;
    }

    @Override
    public String delIntfLabel(String routerId, String localAddress, String remoteAddress, String labelVal) {
        return null;
    }

    @Override
    public Device getDevice(String routerId) {
        Device device = deviceManager.getDevice(routerId);
        return device;
    }
}
