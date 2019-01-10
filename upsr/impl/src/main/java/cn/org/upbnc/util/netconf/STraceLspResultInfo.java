package cn.org.upbnc.util.netconf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class STraceLspResultInfo {
    public static final String TRACE_SUCCESS = "success";
    public static final String TRACE_FAILED = "failed";
    String tunnelName;
    String status;
    String errorType;
    List<STraceLspHopInfo> sTraceLspHopInfoList;

    public STraceLspResultInfo() {
        this.sTraceLspHopInfoList = new ArrayList<>();
    }

    public String getTunnelName() {
        return tunnelName;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public List<STraceLspHopInfo> getsTraceLspHopInfoList() {
        return sTraceLspHopInfoList;
    }

    public void setsTraceLspHopInfoList(List<STraceLspHopInfo> sTraceLspHopInfoList) {
        this.sTraceLspHopInfoList = sTraceLspHopInfoList;
    }

    public void addSTraceLspHopInfoList(STraceLspHopInfo sTraceLspHopInfo) {
        this.sTraceLspHopInfoList.add(sTraceLspHopInfo);
    }

    @Override
    public String toString() {
        String ret = "Trace result: " + " \n " +
                "tunnelName : " + this.tunnelName + "; \n " +
                "status     : " + this.status + "; \n " +
                "errorType  : " + this.errorType + "; \n ";
        Iterator<STraceLspHopInfo> sTraceLspHopInfoIterator = this.sTraceLspHopInfoList.iterator();
        while (sTraceLspHopInfoIterator.hasNext()) {
            STraceLspHopInfo sTraceLspHopInfo = sTraceLspHopInfoIterator.next();
            ret = ret + "{ " + sTraceLspHopInfo.hopIndex + ": " + sTraceLspHopInfo.dsIpAddr + " }\n";
        }
        return ret;
    }
}
