package cn.org.upbnc.entity;

import cn.org.upbnc.enumtype.TunnelServiceClassEnum;

public class TunnelServiceClass {
    private boolean def;
    private boolean be;
    private boolean af1;
    private boolean af2;
    private boolean af3;
    private boolean af4;
    private boolean ef;
    private boolean cs6;
    private boolean cs7;

    private final String splitChar = " ";

    public TunnelServiceClass() {
        this.def = false;
        this.be = false;
        this.af1 = false;
        this.af2 = false;
        this.af3 = false;
        this.af4 = false;
        this.ef = false;
        this.cs6 = false;
        this.cs7 = false;
    }

    public boolean isDef() {
        return def;
    }

    public void setDef(boolean def) {
        this.def = def;
    }

    public boolean isBe() {
        return be;
    }

    public void setBe(boolean be) {
        this.be = be;
    }

    public boolean isAf1() {
        return af1;
    }

    public void setAf1(boolean af1) {
        this.af1 = af1;
    }

    public boolean isAf2() {
        return af2;
    }

    public void setAf2(boolean af2) {
        this.af2 = af2;
    }

    public boolean isAf3() {
        return af3;
    }

    public void setAf3(boolean af3) {
        this.af3 = af3;
    }

    public boolean isAf4() {
        return af4;
    }

    public void setAf4(boolean af4) {
        this.af4 = af4;
    }

    public boolean isEf() {
        return ef;
    }

    public void setEf(boolean ef) {
        this.ef = ef;
    }

    public boolean isCs6() {
        return cs6;
    }

    public void setCs6(boolean cs6) {
        this.cs6 = cs6;
    }

    public boolean isCs7() {
        return cs7;
    }

    public void setCs7(boolean cs7) {
        this.cs7 = cs7;
    }

    public String getString(){
        String ret = "";
        ret += this.def? TunnelServiceClassEnum.DEF.getName()+this.splitChar:"";
        ret += this.af1? TunnelServiceClassEnum.AF1.getName()+this.splitChar:"";
        ret += this.af2? TunnelServiceClassEnum.AF2.getName()+this.splitChar:"";
        ret += this.af3? TunnelServiceClassEnum.AF3.getName()+this.splitChar:"";
        ret += this.af4? TunnelServiceClassEnum.AF4.getName()+this.splitChar:"";
        ret += this.be? TunnelServiceClassEnum.BE.getName()+this.splitChar:"";
        ret += this.ef? TunnelServiceClassEnum.EF.getName()+this.splitChar:"";
        ret += this.cs6? TunnelServiceClassEnum.CS6.getName()+this.splitChar:"";
        ret += this.cs7? TunnelServiceClassEnum.CS7.getName()+this.splitChar:"";

        return ret.trim();
    }
}
