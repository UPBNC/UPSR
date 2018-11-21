/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.TopoInfoApi;
import cn.org.upbnc.service.ServiceInterface;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.linkinfo.Links;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.linkinfo.LinksBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.linkinfo.links.DestBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.linkinfo.links.SourceBuilder;

import java.util.ArrayList;
import java.util.List;

public class TopoInfoApiImpl implements TopoInfoApi {
    private static TopoInfoApi ourInstance = new TopoInfoApiImpl();
    public static TopoInfoApi getInstance() {
        return ourInstance;
    }

    private TopoInfoApiImpl() {
    }
    @Override
    public List<Links> getLinks() {
        List<Links> links=new ArrayList<>();
        LinksBuilder linksBuilder = new LinksBuilder();
        linksBuilder.setLinkId("suntest");
        SourceBuilder sourceBuilder = new SourceBuilder();
        sourceBuilder.setRouterId("1.1.1.1");
        sourceBuilder.setIfAddress("12.1.1.1");
        linksBuilder.setSource(sourceBuilder.build());
        DestBuilder destBuilder = new DestBuilder();
        destBuilder.setRouterId("2.2.2.2");
        destBuilder.setIfAddress("12.1.1.2");
        linksBuilder.setDest(destBuilder.build());
        links.add(linksBuilder.build());
        return links;
    }
}
