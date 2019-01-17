package cn.org.upbnc.util.netconf;

public class SExplicitPathHop {
    public static final String SIDLABEL_TYPE_ADJACENCY = "adjacency";
    public static final String SIDLABEL_TYPE_PREFIX = "prefix";
    private String mplsTunnelHopIndex;
    private String mplsTunnelHopMode = "SID_LABEL";
    private String mplsTunnelHopSidLabel;
    private String mplsTunnelHopSidLabelType = SIDLABEL_TYPE_ADJACENCY;

    public String getMplsTunnelHopIndex() {
        return mplsTunnelHopIndex;
    }

    public void setMplsTunnelHopIndex(String mplsTunnelHopIndex) {
        this.mplsTunnelHopIndex = mplsTunnelHopIndex;
    }

    public String getMplsTunnelHopMode() {
        return mplsTunnelHopMode;
    }

    public void setMplsTunnelHopMode(String mplsTunnelHopMode) {
        this.mplsTunnelHopMode = mplsTunnelHopMode;
    }

    public String getMplsTunnelHopSidLabel() {
        return mplsTunnelHopSidLabel;
    }

    public void setMplsTunnelHopSidLabel(String mplsTunnelHopSidLabel) {
        this.mplsTunnelHopSidLabel = mplsTunnelHopSidLabel;
    }

    public String getMplsTunnelHopSidLabelType() {
        return mplsTunnelHopSidLabelType;
    }

    public void setMplsTunnelHopSidLabelType(String mplsTunnelHopSidLabelType) {
        this.mplsTunnelHopSidLabelType = mplsTunnelHopSidLabelType;
    }

    @Override
    public String toString() {
        return "SExplicitPathHop{" +
                "mplsTunnelHopIndex='" + mplsTunnelHopIndex + '\'' +
                ", mplsTunnelHopMode='" + mplsTunnelHopMode + '\'' +
                ", mplsTunnelHopSidLabel='" + mplsTunnelHopSidLabel + '\'' +
                ", mplsTunnelHopSidLabelType='" + mplsTunnelHopSidLabelType + '\'' +
                '}';
    }
}
