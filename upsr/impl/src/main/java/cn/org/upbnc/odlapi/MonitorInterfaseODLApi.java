package cn.org.upbnc.odlapi;

import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.CodeEnum;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrmonitor.rev190923.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrmonitor.rev190923.getifstatistics.output.RouterListIf;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrmonitor.rev190923.getifstatistics.output.RouterListIfBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrmonitor.rev190923.getifstatistics.output.routerlistif.IfList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrmonitor.rev190923.getifstatistics.output.routerlistif.IfListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrmonitor.rev190923.gettunnelstatistics.output.RouterListTunnel;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrmonitor.rev190923.gettunnelstatistics.output.RouterListTunnelBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrmonitor.rev190923.gettunnelstatistics.output.routerlisttunnel.TunnelList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrmonitor.rev190923.gettunnelstatistics.output.routerlisttunnel.TunnelListBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class MonitorInterfaseODLApi implements UpsrMonitorService {
    private static final Logger LOG = LoggerFactory.getLogger(MonitorInterfaseODLApi.class);
    Session session;

    public MonitorInterfaseODLApi (Session session) {
        this.session = session;
    }

    @Override
    public Future<RpcResult<GetTunnelStatisticsOutput>> getTunnelStatistics(GetTunnelStatisticsInput input) {
        LOG.info("getTunnelStatistics begin");
        GetTunnelStatisticsOutputBuilder getTunnelStatisticsOutputBuilder = new GetTunnelStatisticsOutputBuilder();
        getTunnelStatisticsOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        List<RouterListTunnel> routerListList = new ArrayList<>();
        for (int j = 1; j < 3; j++ ) {
            RouterListTunnelBuilder routerListTunnelBuilder = new RouterListTunnelBuilder();
            routerListTunnelBuilder.setRouterId("1.1.1." + j);
            List<TunnelList> tunnelListList = new ArrayList<>();
            for (int i = 1; i < 5; i++) {
                TunnelListBuilder tunnelListBuilder = new TunnelListBuilder();
                tunnelListBuilder.setIfName("Tunnel_" + i);
                tunnelListBuilder.setPhyStatus("2");
                tunnelListBuilder.setProStatus("1");
                tunnelListBuilder.setTotalBandwidth("1000");
                tunnelListBuilder.setUsedBandwidth("800");
                tunnelListBuilder.setLinkMainStatus("2");
                tunnelListBuilder.setLinkBackStatus("1");
                tunnelListList.add(tunnelListBuilder.build());
            }
            routerListTunnelBuilder.setTunnelList(tunnelListList);
            routerListList.add(routerListTunnelBuilder.build());
        }
        getTunnelStatisticsOutputBuilder.setRouterListTunnel(routerListList);
        LOG.info("getTunnelStatistics end");
        return RpcResultBuilder.success(getTunnelStatisticsOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetIfStatisticsOutput>> getIfStatistics(GetIfStatisticsInput input) {
        LOG.info("getIfStatistics begin");
        GetIfStatisticsOutputBuilder getIfStatisticsOutputBuilder = new GetIfStatisticsOutputBuilder();
        getIfStatisticsOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        List<RouterListIf> routerListList = new ArrayList<>();
        for (int j = 1; j < 3; j++ ) {
            RouterListIfBuilder routerListBuilder = new RouterListIfBuilder();
            routerListBuilder.setRouterId("1.1.1." + j);
            List<IfList> ifListList = new ArrayList<>();
            for (int i = 1; i < 5; i++) {
                IfListBuilder ifListBuilder = new IfListBuilder();
                ifListBuilder.setIfName("interface GigabitEthernet0/0/" + i);
                ifListBuilder.setPhyStatus("1");
                ifListBuilder.setProStatus("2");
                ifListBuilder.setTotalBandwidth("2000");
                ifListBuilder.setUsedBandwidth("1800");
                ifListList.add(ifListBuilder.build());
            }
            routerListBuilder.setIfList(ifListList);
            routerListList.add(routerListBuilder.build());
        }
        getIfStatisticsOutputBuilder.setRouterListIf(routerListList);
        LOG.info("getIfStatistics end");
        return RpcResultBuilder.success(getIfStatisticsOutputBuilder.build()).buildFuture();
    }
}
