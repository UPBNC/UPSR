/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.SrLabelApi;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.SrLabelErrorCodeEnum;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.SrLabelService;
import cn.org.upbnc.util.xml.SrLabelXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SrLabelApiImpl implements SrLabelApi {
    public static final int MAX_NODE_LABEL_RANGE = 65534;
    private static final Logger LOG = LoggerFactory.getLogger(SrLabelApiImpl.class);
    private static SrLabelApi ourInstance = new SrLabelApiImpl();
    private ServiceInterface serviceInterface;
    private SrLabelService srLabelService;
    private NetconfSessionApiImpl netconfSessionApi;

    public static SrLabelApi getInstance() {
        return ourInstance;
    }

    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        boolean ret = true;
        try {
            if (this.serviceInterface == null) {
                this.serviceInterface = serviceInterface;
                this.srLabelService = serviceInterface.getSrLabelService();
            }
        } catch (Exception e) {
            ret = false;
            LOG.info(e.toString());
        }
        return ret;
    }

    @Override
    public Map<String, Object> updateNodeLabel(String routerId, String labelBegin, String labelValAbs, String action) {
        Map<String, Object> resultMap = new HashMap<>();
        if (action.equals(SrLabelXml.ncOperationMerge) &&
                (((labelBegin == null) && (labelValAbs == null)) || (labelBegin.equals("") && labelValAbs.equals("")))) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), CodeEnum.SUCCESS.getMessage());
            return resultMap;
        }
        if (action.equals(SrLabelXml.ncOperationMerge) &&
                ((labelBegin == null) || (labelValAbs == null) || labelBegin.equals("") || labelValAbs.equals(""))) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), SrLabelErrorCodeEnum.INPUT_INVALID.getMessage());
            return resultMap;
        }
        if(null == serviceInterface.getNetconfSessionService().getNetconfClient(routerId)){
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), SrLabelErrorCodeEnum.NETCONF_INVALID.getMessage());
            return resultMap;
        }
        if (action.equals(SrLabelXml.ncOperationMerge) && (Integer.parseInt(labelValAbs) <= Integer.parseInt(labelBegin))) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), SrLabelErrorCodeEnum.LABEL_INVALID.getMessage());
            return resultMap;
        }
        return srLabelService.updateNodeLabel(routerId, labelValAbs, action);
    }

    @Override
    public Map<String, Object> updateNodeLabelRange(String routerId, String labelBegin, String labelEnd, String action) {
        Map<String, Object> resultMap = new HashMap<>();
        if (action.equals(SrLabelXml.ncOperationMerge) &&
                (((labelBegin == null) && (labelEnd == null)) || (labelBegin.equals("") && labelEnd.equals("")))) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), CodeEnum.SUCCESS.getMessage());
            return resultMap;
        }
        if (action.equals(SrLabelXml.ncOperationMerge) &&
                ((labelBegin == null) || (labelEnd == null) || labelBegin.equals("") || labelEnd.equals(""))) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), SrLabelErrorCodeEnum.INPUT_INVALID.getMessage());
            return resultMap;
        }
        if(null == serviceInterface.getNetconfSessionService().getNetconfClient(routerId)){
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), SrLabelErrorCodeEnum.NETCONF_INVALID.getMessage());
            return resultMap;
        }
        return srLabelService.updateNodeLabelRange(routerId, labelBegin, labelEnd, action);
    }

    @Override
    public Map<String, Object> updateIntfLabel(String routerId, String ifAddress, String labelVal, String action) {
        Map<String, Object> resultMap = new HashMap<>();
        if(null == serviceInterface.getNetconfSessionService().getNetconfClient(routerId)){
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.MESSAGE.getName(), SrLabelErrorCodeEnum.NETCONF_INVALID.getMessage());
            return resultMap;
        }
        return srLabelService.updateIntfLabel(routerId, ifAddress, labelVal, action);
    }

    @Override
    public Map<String, Object> checkIntfLabel(String routerId, String ifAddress, String labelVal) {
        return srLabelService.checkIntfLabel(routerId, ifAddress, labelVal);
    }

    public Map<String, Object> getSrgbLabel(String routerId) {
        return srLabelService.getSrgbLabel(routerId);
    }
}
