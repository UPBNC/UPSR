/*
 * Copyright © 2017 vcmy and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.netconf;

import cn.org.upbnc.util.netconf.NetconfClient;
import com.google.common.base.Optional;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.HashedWheelTimer;
import org.opendaylight.controller.config.util.xml.XmlUtil;
import org.opendaylight.netconf.api.NetconfMessage;
import org.opendaylight.netconf.client.NetconfClientDispatcherImpl;
import org.opendaylight.netconf.nettyutil.handler.ssh.authentication.LoginPasswordHandler;
import org.w3c.dom.Document;

public class NetconfDevice {

    public NetconfClient createClient(String host, int port, String ip, String loginName, String loginPwd) {
        NetconfClient netconfClient = null;
        try {
            HashedWheelTimer hashedWheelTimer = new HashedWheelTimer();
            NioEventLoopGroup nettyGroup = new NioEventLoopGroup();
            NetconfClientDispatcherImpl netconfClientDispatcher = new NetconfClientDispatcherImpl(nettyGroup,
                    nettyGroup, hashedWheelTimer);

            LoginPasswordHandler authHandler = new LoginPasswordHandler(loginName, loginPwd);
            netconfClient = new NetconfClient(host, netconfClientDispatcher,
                    NetconfClient.getClientConfig(ip, port, true, Optional.of(authHandler)));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return netconfClient;
    }

    public String sendMessage(NetconfClient netconfClient, String rpc) {
        String result = null;
        try {
            Document doc = XmlUtil.readXmlToDocument(rpc);
            NetconfMessage message = netconfClient.sendMessage(new NetconfMessage(doc));
            result = XmlUtil.toString(message.getDocument());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}