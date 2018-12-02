package cn.org.upbnc.api;

import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.NodeLabel;
import cn.org.upbnc.service.ServiceInterface;

public interface SrLabelApi {
    boolean setServiceInterface(ServiceInterface serviceInterface);
    String updateNodeLabel(String routerId, String labelVal);
    String updateNodeLabelRange(String routerId, String labelBegin, String labelEnd);
    String updateIntfLabel(String routerId, String localAddress, String remoteAddress, String labelVal);
    String delIntfLabel(String routerId, String localAddress, String remoteAddress, String labelVal);
    Device getDevice(String routerId);
}
