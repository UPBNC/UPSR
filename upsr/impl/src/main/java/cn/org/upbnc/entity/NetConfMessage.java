package cn.org.upbnc.entity;

import java.util.ArrayList;
import java.util.List;

public class NetConfMessage {
    boolean isOK;
    String type;
    String tag;
    String path;
    String message;
    String code;
    List<String> paras;

    public boolean isOK() {
        return isOK;
    }

    public void setOK(boolean OK) {
        isOK = OK;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getParas() {
        return paras;
    }

    public void setParas(List<String> paras) {
        this.paras = paras;
    }

    public void addPara(String para){
        if( null != para) {
            if (this.paras == null) {
                this.paras = new ArrayList<String>();
            }

            this.paras.add(para);
        }
    }

}
