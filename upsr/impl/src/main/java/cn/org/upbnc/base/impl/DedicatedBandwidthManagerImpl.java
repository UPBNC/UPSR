package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.DedicatedBandwidthManager;

import java.util.HashMap;
import java.util.Map;

public class DedicatedBandwidthManagerImpl implements DedicatedBandwidthManager {
    private Map<String,Map<String,String>> dedicatedBandwidth;
    private static DedicatedBandwidthManager instance = null;
    private DedicatedBandwidthManagerImpl() {
        dedicatedBandwidth = new HashMap<>();
    }

    public static DedicatedBandwidthManager getInstance() {
        if (null == instance) {
            instance = new DedicatedBandwidthManagerImpl();
        }
        return instance;
    }
}
