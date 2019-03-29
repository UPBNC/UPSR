package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.service.StatisticService;
import cn.org.upbnc.service.entity.StatisticsEntity;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.NetconfDevice;
import cn.org.upbnc.util.netconf.SIfClearedStat;
import cn.org.upbnc.util.xml.IfClearedStatXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticServiceImpl implements StatisticService {
    private static final Logger LOG = LoggerFactory.getLogger(VPNServiceImpl.class);
    private static StatisticService ourInstance = null;
    private BaseInterface baseInterface;
    private NetConfManager netConfManager;
    private DeviceManager deviceManager;
    public static NetconfDevice netconfController = new NetconfDevice();

    public static StatisticService getInstance() {
        if (null == ourInstance) {
            ourInstance = new StatisticServiceImpl();
        }
        return ourInstance;
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
    public List<StatisticsEntity> getStatisticsMap(String routerId, String ifName) {
        List<StatisticsEntity> statisticsEntityList = new ArrayList<>();
        if (!("").equals(routerId)) {
            NetconfClient netconfClient = this.netConfManager.getNetconClient(routerId);
            String sendMsg = IfClearedStatXml.getIfClearedStatXml("");
            String result = null;
            result = netconfController.sendMessage(netconfClient, sendMsg);
            List<SIfClearedStat> sIfClearedStats = IfClearedStatXml.getIfClearedStatFromXml(result);
            for (SIfClearedStat sIfClearedStat : sIfClearedStats) {
                LOG.info(sIfClearedStat.toString());
            }
        }
        return statisticsEntityList;
    }
}
