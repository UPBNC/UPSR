package cn.org.upbnc.util.netconf;

import java.util.List;

public class SBgpVrfAF {
    private String preferenceExternal;
    private String preferenceInternal;
    private String preferenceLocal;
    private List<SPeerAF> peerAFs;

    public String getPreferenceExternal() {
        return preferenceExternal;
    }

    public void setPreferenceExternal(String preferenceExternal) {
        this.preferenceExternal = (preferenceExternal == null || preferenceExternal.equals(""))? "255":preferenceExternal;
    }

    public String getPreferenceInternal() {
        return preferenceInternal;
    }

    public void setPreferenceInternal(String preferenceInternal) {
        this.preferenceInternal = (preferenceInternal == null || preferenceInternal.equals(""))? "255":preferenceInternal;
    }

    public String getPreferenceLocal() {
        return preferenceLocal;
    }

    public void setPreferenceLocal(String preferenceLocal) {
        this.preferenceLocal = (preferenceLocal == null || preferenceLocal.equals(""))? "255":preferenceLocal;
    }

    public List<SPeerAF> getPeerAFs() {
        return peerAFs;
    }

    public void setPeerAFs(List<SPeerAF> peerAFs) {
        this.peerAFs = peerAFs;
    }

    @Override
    public String toString() {
        return "SBgpVrfAF{" +
                "preferenceExternal='" + preferenceExternal + '\'' +
                ", preferenceInternal='" + preferenceInternal + '\'' +
                ", preferenceLocal='" + preferenceLocal + '\'' +
                ", peerAFs=" + peerAFs +
                '}';
    }
}
