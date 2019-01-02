package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.TunnelApi;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.entity.ExplicitPathServiceEntity;
import cn.org.upbnc.service.entity.TunnelServiceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class TunnelApiImpl implements TunnelApi {
    private static final Logger LOG = LoggerFactory.getLogger(TunnelApiImpl.class);
    private ServiceInterface serviceInterface;
    public static TunnelApi ourInstance = new TunnelApiImpl();

    public static TunnelApi getInstance() {
        return ourInstance;
    }

    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        boolean ret = true;
        try {
            if (this.serviceInterface == null) {
                this.serviceInterface = serviceInterface;
            }
        } catch (Exception e) {
            ret = false;
            LOG.info(e.toString());
        }
        return ret;
    }

    @Override
    public Map<String, Object> updateTunnel(TunnelServiceEntity tunnelServiceEntity, ExplicitPathServiceEntity explicitPathServiceEntity) {
        return null;
    }

    @Override
    public Map<String, Object> deleteTunnel(String routerId, String tunnelId) {
        return null;
    }

    @Override
    public Map<String, Object> getAllTunnel() {
        return null;
    }
}
