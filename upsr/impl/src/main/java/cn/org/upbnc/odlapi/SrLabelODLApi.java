package cn.org.upbnc.odlapi;

import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.impl.UpsrProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsrlabel.rev181126.*;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

public class SrLabelODLApi implements UpsrSrLabelService{
    private static final Logger LOG = LoggerFactory.getLogger(UpsrProvider.class);
    Session session;
    public SrLabelODLApi(Session session) {
        this.session = session;
    }

    @Override
    public Future<RpcResult<GetSrgbLabelOutput>> getSrgbLabel(GetSrgbLabelInput input) {
        GetSrgbLabelOutputBuilder getSrgbLabelOutputBuilder = new GetSrgbLabelOutputBuilder();
        LOG.info("getSrgbLabel begin");
        if(SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(getSrgbLabelOutputBuilder.build()).buildFuture();
        }else{

        }

        LOG.info("getSrgbLabel end");
        return RpcResultBuilder.success(getSrgbLabelOutputBuilder.build()).buildFuture();
    }
    @Override
    public Future<RpcResult<UpdateSrgbLabelOutput>> updateSrgbLabel(UpdateSrgbLabelInput input) {
        UpdateSrgbLabelOutputBuilder updateSrgbLabelOutputBuilder = new UpdateSrgbLabelOutputBuilder();
        LOG.info("updateSrgbLabel begin");
        if(SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(updateSrgbLabelOutputBuilder.build()).buildFuture();
        }else{

        }

        LOG.info("updateSrgbLabel eng");
        return RpcResultBuilder.success(updateSrgbLabelOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<UpdateIntfLabelOutput>> updateIntfLabel(UpdateIntfLabelInput input) {

        return null;
    }
    @Override
    public Future<RpcResult<DelIntfLabelOutput>> delIntfLabel(DelIntfLabelInput input) {
        return null;
    }

    @Override
    public Future<RpcResult<GetIntfLabelOutput>> getIntfLabel(GetIntfLabelInput input) {
        return null;
    }
}
