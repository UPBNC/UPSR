/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.odlapi;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrinterface.rev181119.*;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterfaceODLApi implements UpsrInterfaceService{
    public Future<RpcResult<GetInterfacesOutput>> getInterfaces()
    {
        GetInterfacesOutputBuilder getInterfacesOutputBuilder = new GetInterfacesOutputBuilder();
        return RpcResultBuilder.success(getInterfacesOutputBuilder.build()).buildFuture();
    }
}
