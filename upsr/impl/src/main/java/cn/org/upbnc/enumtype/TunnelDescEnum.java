package cn.org.upbnc.enumtype;

public enum TunnelDescEnum {
    End(1,"Create_By_UPSR"),
    TunnelBegin(2,"Tunnel"),
    VPNBegin(3,"VPN");
    private static final String split = "_";
    private int code;
    private String name;

    TunnelDescEnum(int code ,String name){
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String createTunnelDescription(String tunnelName,String vpnName, TunnelDescEnum typeBegin, TunnelDescEnum typeEnd) {
        if (vpnName != null) {
            return  tunnelName + split + typeBegin.getName() + split + vpnName + split + typeEnd.getName();
        } else {
            return  tunnelName + split + typeBegin.getName() + split +  "*#06#" + split + typeEnd.getName();
        }
    }

    public static String getVpnDescription(String description) {
        if (description == null) {
            return null;
        }
        String[] desc = description.split(split);
        if(desc == null){
            return null;
        }
        if(desc.length == 4 && desc[1].equals(VPNBegin.getName())) {
             if (desc[2].equals("*#06#")) {
                return null;
            } else {
                return desc[2];
            }
        }else {
            return null;
        }
    }
}
