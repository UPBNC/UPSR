package cn.org.upbnc.util.netconf.bgp;

public class ImportRoute {
    private String importProtocol;
    private String importProcessId;

    public ImportRoute(String importProtocol, String importProcessId){
        this.importProcessId=importProcessId;
        this.importProtocol=importProtocol;
    }

    public String getImportProcessId() {
        return importProcessId;
    }

    public String getImportProtocol() {
        return importProtocol;
    }

    public void setImportProcessId(String importProcessId) {
        this.importProcessId = importProcessId;
    }

    public void setImportProtocol(String importProtocol) {
        this.importProtocol = importProtocol;
    }
}
