/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;

import java.util.Map;

public interface SrLabelService {
    // Set BaseInterface
    boolean setBaseInterface(BaseInterface baseInterface);

    Map<String, Object> updateNodeLabel(String routerId, String labelVal, String action);

    Map<String, Object> updateNodeLabelRange(String routerId, String labelBegin, String labelEnd, String action);

    Map<String, Object> updateIntfLabel(String routerId, String ifAddress, String labelVal, String action);

    boolean syncAllNodeLabel();

    boolean syncNodeLabel(String routerId);

    boolean syncAllIntfLabel();

    boolean syncIntfLabel(String routerId);
}
