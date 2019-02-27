package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.*;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.TunnelPolicy.TunnelPolicy;
import cn.org.upbnc.service.TunnelPolicyService;
import cn.org.upbnc.service.VPNService;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.TunnelPolicy.STunnelPolicy;
import cn.org.upbnc.util.xml.TunnelPolicyXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class TunnelPolicyServiceImpl implements TunnelPolicyService {
    private static final Logger LOG = LoggerFactory.getLogger(VPNServiceImpl.class);
    private static TunnelPolicyService ourInstance = null;
    private BaseInterface baseInterface;
    private TunnelPolicyManager tunnelPolicyManager;
    private NetConfManager netConfManager ;
    private DeviceManager deviceManager;

    public static TunnelPolicyService getInstance() {
        if (null == ourInstance) {
            ourInstance = new TunnelPolicyServiceImpl();
        }
        return ourInstance;
    }

    private TunnelPolicyServiceImpl() {
        this.tunnelPolicyManager = null;
        this.netConfManager = null;
        this.baseInterface = null;
        this.deviceManager=null;
    }

    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        if (null != baseInterface) {
            this.netConfManager = this.baseInterface.getNetConfManager();
            this.tunnelPolicyManager = this.baseInterface.getTunnelPolicyManager();
            this.deviceManager=this.baseInterface.getDeviceManager();
        }
        return true;
    }

    @Override
    public boolean syncTunnelPolicyConf(){
        boolean ret=true;
        for(Device device:deviceManager.getDeviceList()){
            if(!syncTunnelPolicyConf(device.getRouterId())){
                LOG.info("syncTunnelPolicyConf failed,routerId:"+device.getRouterId());
                ret=false;
            }
        }
        return ret;
    }

    public boolean syncTunnelPolicyConf(String routerID) {
        NetconfClient netconfClient = netConfManager.getNetconClient(routerID);
        if(null==netconfClient){
            return  false;
        }
        return tunnelPolicyManager.syncTunnelPolicyConf(netconfClient,routerID);
    }
}
