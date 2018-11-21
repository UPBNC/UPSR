package cn.org.upbnc.util.netconf;

import cn.org.upbnc.base.impl.NetConfManagerImpl;
import cn.org.upbnc.enumtype.NetConfStatusEnum;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import org.opendaylight.netconf.api.NetconfMessage;
import org.opendaylight.netconf.api.NetconfTerminationReason;
import org.opendaylight.netconf.client.NetconfClientSession;
import org.opendaylight.netconf.client.NetconfClientSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class SessionListener implements NetconfClientSessionListener {
    private static final class RequestEntry {
        private final Promise<NetconfMessage> promise;
        private final NetconfMessage request;

        RequestEntry(Promise<NetconfMessage> future, NetconfMessage request) {
            this.promise = Preconditions.checkNotNull(future);
            this.request = Preconditions.checkNotNull(request);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(SessionListener.class);
    public static List<Long> sessionList = new ArrayList<>();
    @GuardedBy("this")
    private final Queue<RequestEntry> requests = new ArrayDeque<>();

    @GuardedBy("this")
    private NetconfClientSession clientSession;

    @GuardedBy("this")
    private void dispatchRequest() {
        while (!requests.isEmpty()) {
            final RequestEntry e = requests.peek();
            if (e.promise.setUncancellable()) {
                LOG.debug("Sending message {}", e.request);
                clientSession.sendMessage(e.request);
                break;
            }
            LOG.debug("Message {} has been cancelled, skipping it", e.request);
            requests.poll();
        }
    }

    @Override
    @SuppressWarnings("checkstyle:hiddenField")
    public final synchronized void onSessionUp(NetconfClientSession clientSession) {
        this.clientSession = Preconditions.checkNotNull(clientSession);
        dispatchRequest();
        sessionList.add(clientSession.getSessionId());
    }

    private synchronized void tearDown(final Exception cause) {
        final RequestEntry e = requests.poll();
        if (e != null) {
            e.promise.setFailure(cause);
        }
        this.clientSession = null;
    }

    @Override
    @SuppressWarnings("checkstyle:hiddenField")
    public final void onSessionDown(NetconfClientSession clientSession, Exception exception) {
        tearDown(exception);
        String clientSessionKey = null;
        for (String key : NetConfManagerImpl.netconfClientMap.keySet()) {
            clientSessionKey = key;
            if (clientSession.getSessionId() == NetConfManagerImpl.netconfClientMap.get(key).getSessionId()) {
                NetConfManagerImpl.netconfClientMap.get(key).setFlag(false);
                sessionList.remove(clientSession.getSessionId());
                break;
            }
        }
        if (null != clientSessionKey) {
            if (NetConfManagerImpl.netConfMap.containsKey(clientSessionKey)) {
                NetConfManagerImpl.netConfMap.get(clientSessionKey).setStatus(NetConfStatusEnum.Disconnect);
            }
        }
    }

    @Override
    @SuppressWarnings("checkstyle:hiddenField")
    public final void onSessionTerminated(NetconfClientSession clientSession,
                                          NetconfTerminationReason netconfTerminationReason) {
        tearDown(new RuntimeException(netconfTerminationReason.getErrorMessage()));
    }

    @Override
    public synchronized void onMessage(NetconfClientSession session, NetconfMessage message) {
        LOG.info("New message arrived: {}", message);

        final RequestEntry e = requests.poll();
        if (e != null) {
            e.promise.setSuccess(message);
            dispatchRequest();
        } else {
            LOG.info("Ignoring unsolicited message {}", message);
        }
    }

    public final synchronized Future<NetconfMessage> sendRequest(NetconfMessage message) {
        final RequestEntry req = new RequestEntry(GlobalEventExecutor.INSTANCE.<NetconfMessage>newPromise(), message);
        requests.add(req);
        if (clientSession != null) {
            dispatchRequest();
        }
        return req.promise;
    }

}
