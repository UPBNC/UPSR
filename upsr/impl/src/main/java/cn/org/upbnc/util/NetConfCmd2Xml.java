/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util;

import cn.org.upbnc.enumtype.ManufactureEnum;
import cn.org.upbnc.util.xml.SRXml;
import org.dom4j.Document;

public interface NetConfCmd2Xml {
    // Init xml
    void initXml();

    // Get xml
    SRXml getXMLByManufacture(ManufactureEnum manufacture, String cmd);
}
