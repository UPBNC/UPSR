/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.cli.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import cn.org.upbnc.cli.api.UpsrCliCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpsrCliCommandsImpl implements UpsrCliCommands {

    private static final Logger LOG = LoggerFactory.getLogger(UpsrCliCommandsImpl.class);
    private final DataBroker dataBroker;

    public UpsrCliCommandsImpl(final DataBroker db) {
        this.dataBroker = db;
        LOG.info("UpsrCliCommandImpl initialized");
    }

    @Override
    public Object testCommand(Object testArgument) {
        return "This is a test implementation of test-command";
    }
}
