package cn.org.upbnc.util.netconf;

import java.util.List;

public class SExplicitPath {
    private String explicitPathName;
    private List<SExplicitPathHop> explicitPathHops;

    public String getExplicitPathName() {
        return explicitPathName;
    }

    public void setExplicitPathName(String explicitPathName) {
        this.explicitPathName = explicitPathName;
    }

    public List<SExplicitPathHop> getExplicitPathHops() {
        return explicitPathHops;
    }

    public void setExplicitPathHops(List<SExplicitPathHop> explicitPathHops) {
        this.explicitPathHops = explicitPathHops;
    }

    @Override
    public String toString() {
        return "SExplicitPath{" +
                "explicitPathName='" + explicitPathName + '\'' +
                ", explicitPathHops=" + explicitPathHops +
                '}';
    }
}
