package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.DedicatedBandwidthManager;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;

public class DedicatedBandwidthManagerImpl implements DedicatedBandwidthManager {
    private static DedicatedBandwidthManager instance = null;
    private Ini band_ini = null;
    private DedicatedBandwidthManagerImpl() {
        File bandFile = new File("./dedicated_band/dedicated_band.ini");
        band_ini = new Ini();
        band_ini.setFile(bandFile);
        try {
            band_ini.load();
        } catch (Exception e) {

        }
    }

    public static DedicatedBandwidthManager getInstance() {
        if (null == instance) {
            instance = new DedicatedBandwidthManagerImpl();
        }
        return instance;
    }

    @Override
    public String getIfBand(String routerId, String ifName) {
        String ifBand = null;
        if ((routerId == null) || (ifName == null)) {
            return null;
        }
        if (band_ini != null) {
            for (String key : band_ini.keySet()) {
                Profile.Section sectionCfg = band_ini.get(key);
                if (routerId.equals(sectionCfg.get("routerId"))) {
                    ifBand = sectionCfg.get(ifName);
                    break;
                }
            }
        }
        return ifBand;
    }
}
