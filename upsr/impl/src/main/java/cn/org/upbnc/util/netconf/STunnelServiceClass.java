package cn.org.upbnc.util.netconf;

public class STunnelServiceClass {
    private boolean defaultServiceClassEnable;
    private boolean beServiceClassEnable;
    private boolean af1ServiceClassEnable;
    private boolean af2ServiceClassEnable;
    private boolean af3ServiceClassEnable;
    private boolean af4ServiceClassEnable;
    private boolean efServiceClassEnable;
    private boolean cs6ServiceClassEnable;
    private boolean cs7ServiceClassEnable;

    public STunnelServiceClass() {
        this.defaultServiceClassEnable = false;
        this.beServiceClassEnable = false;
        this.af1ServiceClassEnable = false;
        this.af2ServiceClassEnable = false;
        this.af3ServiceClassEnable = false;
        this.af4ServiceClassEnable = false;
        this.efServiceClassEnable = false;
        this.cs6ServiceClassEnable = false;
        this.cs7ServiceClassEnable = false;
    }

    public boolean isDefaultServiceClassEnable() {
        return defaultServiceClassEnable;
    }

    public void setDefaultServiceClassEnable(boolean defaultServiceClassEnable) {
        this.defaultServiceClassEnable = defaultServiceClassEnable;
    }

    public boolean isBeServiceClassEnable() {
        return beServiceClassEnable;
    }

    public void setBeServiceClassEnable(boolean beServiceClassEnable) {
        this.beServiceClassEnable = beServiceClassEnable;
    }

    public boolean isAf1ServiceClassEnable() {
        return af1ServiceClassEnable;
    }

    public void setAf1ServiceClassEnable(boolean af1ServiceClassEnable) {
        this.af1ServiceClassEnable = af1ServiceClassEnable;
    }

    public boolean isAf2ServiceClassEnable() {
        return af2ServiceClassEnable;
    }

    public void setAf2ServiceClassEnable(boolean af2ServiceClassEnable) {
        this.af2ServiceClassEnable = af2ServiceClassEnable;
    }

    public boolean isAf3ServiceClassEnable() {
        return af3ServiceClassEnable;
    }

    public void setAf3ServiceClassEnable(boolean af3ServiceClassEnable) {
        this.af3ServiceClassEnable = af3ServiceClassEnable;
    }

    public boolean isAf4ServiceClassEnable() {
        return af4ServiceClassEnable;
    }

    public void setAf4ServiceClassEnable(boolean af4ServiceClassEnable) {
        this.af4ServiceClassEnable = af4ServiceClassEnable;
    }

    public boolean isCs6ServiceClassEnable() {
        return cs6ServiceClassEnable;
    }

    public void setCs6ServiceClassEnable(boolean cs6ServiceClassEnable) {
        this.cs6ServiceClassEnable = cs6ServiceClassEnable;
    }

    public boolean isCs7ServiceClassEnable() {
        return cs7ServiceClassEnable;
    }

    public void setCs7ServiceClassEnable(boolean cs7ServiceClassEnable) {
        this.cs7ServiceClassEnable = cs7ServiceClassEnable;
    }

    public boolean isEfServiceClassEnable() {
        return efServiceClassEnable;
    }

    public void setEfServiceClassEnable(boolean efServiceClassEnable) {
        this.efServiceClassEnable = efServiceClassEnable;
    }

    @Override
    public String toString() {
        return "STunnelServiceClass{" +
                "defaultServiceClassEnable=" + defaultServiceClassEnable +
                ", beServiceClassEnable=" + beServiceClassEnable +
                ", af1ServiceClassEnable=" + af1ServiceClassEnable +
                ", af2ServiceClassEnable=" + af2ServiceClassEnable +
                ", af3ServiceClassEnable=" + af3ServiceClassEnable +
                ", af4ServiceClassEnable=" + af4ServiceClassEnable +
                ", efServiceClassEnable=" + efServiceClassEnable +
                ", cs6ServiceClassEnable=" + cs6ServiceClassEnable +
                ", cs7ServiceClassEnable=" + cs7ServiceClassEnable +
                '}';
    }
}
