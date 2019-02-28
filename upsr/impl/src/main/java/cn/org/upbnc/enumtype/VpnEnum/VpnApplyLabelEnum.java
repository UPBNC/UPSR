package cn.org.upbnc.enumtype.VpnEnum;

public enum VpnApplyLabelEnum {
    PER_INSTANCE(1,"per-instance","perInstance"),
    PER_NEXTHOP(2,"per-nexthop","perNextHop"),
    PER_ROURE(3,"per-route","perRoute");
    private int code;
    private String cmd;
    private String netconf;

    VpnApplyLabelEnum(int code , String cmd,String netconf){
        this.code = code;
        this.cmd = cmd;
        this.netconf = netconf;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return cmd;
    }

    public String getNetconf() {
        return netconf;
    }

    public static String cmd2netconf(String cmd){
        if (PER_INSTANCE.cmd.equals(cmd) || PER_INSTANCE.netconf.equals(cmd)) {
            return PER_INSTANCE.netconf;
        } else if (PER_NEXTHOP.cmd.equals(cmd) || PER_NEXTHOP.netconf.equals(cmd)) {
            return PER_NEXTHOP.netconf;
        } else if (PER_ROURE.cmd.equals(cmd) || PER_ROURE.netconf.equals(cmd)) {
            return PER_ROURE.netconf;
        }else {
            return null;
        }
    }

    public static String netconf2cmd(String netconf){
        if (PER_INSTANCE.cmd.equals(netconf) || PER_INSTANCE.netconf.equals(netconf)) {
            return PER_INSTANCE.cmd;
        } else if (PER_NEXTHOP.cmd.equals(netconf) || PER_NEXTHOP.netconf.equals(netconf)) {
            return PER_NEXTHOP.cmd;
        } else if (PER_ROURE.cmd.equals(netconf) || PER_ROURE.netconf.equals(netconf)) {
            return PER_ROURE.cmd;
        }else {
            return null;
        }
    }
}
