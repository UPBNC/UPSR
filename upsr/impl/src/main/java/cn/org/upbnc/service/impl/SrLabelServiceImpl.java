package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.entity.AdjLabel;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.NodeLabel;
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
        return null;
    }

    @Override
    public String updateNodeLabelRange(String routerId, String labelBegin, String labelEnd) {
        return null;
    }

    @Override
    public String syncNodeLabel(String routerId) {
        Device device = null;
        LOG.info("syncNodeLabel begin");
        device = deviceManager.getDevice(routerId);
        NetconfClient netconfClient = netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        String commandGetSrNodeLabelXml = SrLabelXml.getSrNodeLabelXml();
        LOG.info("command xml: " + commandGetSrNodeLabelXml);
        String outPutXml = netconfController.sendMessage(netconfClient,commandGetSrNodeLabelXml);
        LOG.info("output xml: " + outPutXml);
        NetconfSrLabelInfo netconfSrLabelInfo = SrLabelXml.getSrNodeLabelFromgSrNodeLabelXml(outPutXml);
        NodeLabel nodeLabel = new NodeLabel();
        nodeLabel.setValue(Integer.parseInt(netconfSrLabelInfo.getPrefixLabel()));
        device.setNodeLabel(nodeLabel);

        String commandGetSrNodeLabelRangeXml = SrLabelXml.getSrNodeLabelRangeXml();
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
    public String syncIntfLabel(String routerId) {
        Device device = null;
        LOG.info("syncIntfLael begin");
        device = deviceManager.getDevice(routerId);
        NetconfClient netconfClient = this.netConfManager.getNetconClient(device.getNetConf().getIp().getAddress());
        String commandGetSrAdjLabelXml = SrLabelXml.getSrAdjLabelXml();
        LOG.info("command xml: " + commandGetSrAdjLabelXml);
        String outPutXml = netconfController.sendMessage(netconfClient,commandGetSrAdjLabelXml);
        LOG.info("output xml: " + outPutXml);
        if (CheckXml.checkOk(outPutXml).equals("ok")){
            List<AdjLabel> adjLabelList = SrLabelXml.getSrAdjLabelFromSrAdjLabelXml(outPutXml);
            device.setAdjLabelList(adjLabelList);
        }
        LOG.info("syncIntfLael end");
        return null;
    }

    @Override
    public String delIntfLabel(String routerId, String localAddress, String remoteAddress, String labelVal) {
        return null;
    }
}
