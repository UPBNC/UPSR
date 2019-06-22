package cn.org.upbnc.odlapi;

import cn.org.upbnc.core.Session;
import cn.org.upbnc.enumtype.CodeEnum;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrauth.rev170830.CheckUserInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrauth.rev170830.CheckUserOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrauth.rev170830.CheckUserOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrauth.rev170830.UpsrAuthService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

public class UpsrAuthODLAPI implements UpsrAuthService {
    private static final Logger LOG = LoggerFactory.getLogger(UpsrAuthODLAPI.class);
    Session session;

    public UpsrAuthODLAPI(Session session) {
        this.session = session;
    }

    @Override
    public Future<RpcResult<CheckUserOutput>> checkUser(CheckUserInput input) {
        CheckUserOutputBuilder checkUserOutputBuilder = new CheckUserOutputBuilder();
        LOG.info(input.getUser());
        LOG.info(input.getPasswd());
        checkUserOutputBuilder.setResult(CodeEnum.SUCCESS.getMessage());
        return RpcResultBuilder.success(checkUserOutputBuilder.build()).buildFuture();
    }
}
