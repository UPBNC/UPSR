package cn.org.upbnc.util.netconf;

public class SSrTeTunnelPath {
    private String pathType;
    private String explicitPathName;

    public String getPathType() {
        return pathType;
    }

    public void setPathType(String pathType) {
        this.pathType = pathType;
    }

    public String getExplicitPathName() {
        return explicitPathName;
    }

    public void setExplicitPathName(String explicitPathName) {
        this.explicitPathName = explicitPathName;
    }

    @Override
    public String toString() {
        return "SSrTeTunnelPath{" +
                "pathType='" + pathType + '\'' +
                ", explicitPathName='" + explicitPathName + '\'' +
                '}';
    }
}
