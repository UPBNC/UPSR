package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.cfgcli.srlabelcli.SrlabelCli;
import cn.org.upbnc.cfgcli.tunnelcli.TunnelCli;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.service.ActionCfgService;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.xmlcompare.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.jca.GetInstance;

import java.util.HashMap;
import java.util.Map;

public class ActionCfgServiceImpl implements ActionCfgService {
    private static final Logger LOG = LoggerFactory.getLogger(ActionCfgServiceImpl.class);
    private static ActionCfgService ourInstance = null;
    private BaseInterface baseInterface;
    public static ActionCfgService getInstance() {
        if (ourInstance == null) {
            ourInstance = new ActionCfgServiceImpl();
        }
        return ourInstance;
    }

    @Override
    public Map<String, Object> getCfgChane(String routerId, String cfgType) {
        Map<String, Object> resultMap = new HashMap<>();
        String xml1 = Util.candidate();
        String xml2 = Util.modify();
        String cliString = TunnelCli.tunnelCfgCli(xml1,xml2);
        cliString = cliString + SrlabelCli.srLabelCfgCli();

        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
        resultMap.put(ResponseEnum.MESSAGE.getName(), cliString);
        return resultMap;
    }

    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        if (null != baseInterface) {

        }
        return true;
    }
}
