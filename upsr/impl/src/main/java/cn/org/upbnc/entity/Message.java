package cn.org.upbnc.entity;

public class Message {
    int ecode;
    String edesc;
    int ucode;
    String udesc;
    NetConfMessage ne;

    public int getEcode() {
        return ecode;
    }

    public void setEcode(int ecode) {
        this.ecode = ecode;
    }

    public String getEdesc() {
        return edesc;
    }

    public void setEdesc(String edesc) {
        this.edesc = edesc;
    }

    public int getUcode() {
        return ucode;
    }

    public void setUcode(int ucode) {
        this.ucode = ucode;
    }

    public String getUdesc() {
        return udesc;
    }

    public void setUdesc(String udesc) {
        this.udesc = udesc;
    }

    public NetConfMessage getNe() {
        return ne;
    }

    public void setNe(NetConfMessage ne) {
        this.ne = ne;
    }
}
