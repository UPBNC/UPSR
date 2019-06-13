package cn.org.upbnc.cfgcli.srlabelcli;

public class SrlabelCli {
    public static String srLabelCfgCli(){
        return "segment-routing\n" +
                " ipv4 adjacency local-ip-addr 34.1.1.2 remote-ip-addr 34.1.1.1 sid 322008\n" +
                " ipv4 adjacency local-ip-addr 34.1.2.2 remote-ip-addr 34.1.2.1 sid 322010\n" +
                " ipv4 adjacency local-ip-addr 24.1.1.2 remote-ip-addr 24.1.1.1 sid 322012\n";
    }
}
