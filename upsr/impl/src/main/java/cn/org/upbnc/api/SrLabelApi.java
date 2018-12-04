package cn.org.upbnc.api;

import cn.org.upbnc.entity.Device;
import cn.org.upbnc.service.ServiceInterface;

public interface SrLabelApi {
    boolean setServiceInterface(ServiceInterface serviceInterface);
    String updateNodeLabel(String routerId, String labelVal, String action);
    String updateNodeLabelRange(String routerId, String labelBegin, String labelEnd, String action);
    String updateIntfLabel(String routerId, String localAddress, String remoteAddress, String labelVal, String action);
    String delIntfLabel(String routerId, String localAddress, String remoteAddress, String labelVal);
    Device getDevice(String routerId);
}
