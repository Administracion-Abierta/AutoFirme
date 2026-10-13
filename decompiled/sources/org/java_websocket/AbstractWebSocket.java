package org.java_websocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.java_websocket.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/AbstractWebSocket.class */
public abstract class AbstractWebSocket extends WebSocketAdapter {
    private boolean tcpNoDelay;
    private boolean reuseAddr;
    private ScheduledExecutorService connectionLostCheckerService;
    private ScheduledFuture<?> connectionLostCheckerFuture;
    protected static int DEFAULT_READ_BUFFER_SIZE = 65536;
    private final Logger log = LoggerFactory.getLogger((Class<?>) AbstractWebSocket.class);
    private long connectionLostTimeout = TimeUnit.SECONDS.toNanos(60);
    private boolean websocketRunning = false;
    private boolean daemon = false;
    private final Object syncConnectionLost = new Object();
    private int receiveBufferSize = 0;

    protected abstract Collection<WebSocket> getConnections();

    public int getConnectionLostTimeout() {
        int seconds;
        synchronized (this.syncConnectionLost) {
            seconds = (int) TimeUnit.NANOSECONDS.toSeconds(this.connectionLostTimeout);
        }
        return seconds;
    }

    public void setConnectionLostTimeout(int connectionLostTimeout) {
        synchronized (this.syncConnectionLost) {
            this.connectionLostTimeout = TimeUnit.SECONDS.toNanos(connectionLostTimeout);
            if (this.connectionLostTimeout <= 0) {
                this.log.trace("Connection lost timer stopped");
                cancelConnectionLostTimer();
                return;
            }
            if (this.websocketRunning) {
                this.log.trace("Connection lost timer restarted");
                try {
                    ArrayList<WebSocket> connections = new ArrayList<>(getConnections());
                    for (WebSocket conn : connections) {
                        if (conn instanceof WebSocketImpl) {
                            WebSocketImpl webSocketImpl = (WebSocketImpl) conn;
                            webSocketImpl.updateLastPong();
                        }
                    }
                } catch (Exception e) {
                    this.log.error("Exception during connection lost restart", (Throwable) e);
                }
                restartConnectionLostTimer();
            }
        }
    }

    protected void stopConnectionLostTimer() {
        synchronized (this.syncConnectionLost) {
            if (this.connectionLostCheckerService != null || this.connectionLostCheckerFuture != null) {
                this.websocketRunning = false;
                this.log.trace("Connection lost timer stopped");
                cancelConnectionLostTimer();
            }
        }
    }

    protected void startConnectionLostTimer() {
        synchronized (this.syncConnectionLost) {
            if (this.connectionLostTimeout <= 0) {
                this.log.trace("Connection lost timer deactivated");
                return;
            }
            this.log.trace("Connection lost timer started");
            this.websocketRunning = true;
            restartConnectionLostTimer();
        }
    }

    private void restartConnectionLostTimer() {
        cancelConnectionLostTimer();
        this.connectionLostCheckerService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("WebSocketConnectionLostChecker", this.daemon));
        Runnable connectionLostChecker = new Runnable() { // from class: org.java_websocket.AbstractWebSocket.1
            private ArrayList<WebSocket> connections = new ArrayList<>();

            @Override // java.lang.Runnable
            public void run() {
                long minimumPongTime;
                this.connections.clear();
                try {
                    this.connections.addAll(AbstractWebSocket.this.getConnections());
                    synchronized (AbstractWebSocket.this.syncConnectionLost) {
                        minimumPongTime = (long) (System.nanoTime() - (AbstractWebSocket.this.connectionLostTimeout * 1.5d));
                    }
                    for (WebSocket conn : this.connections) {
                        AbstractWebSocket.this.executeConnectionLostDetection(conn, minimumPongTime);
                    }
                } catch (Exception e) {
                }
                this.connections.clear();
            }
        };
        this.connectionLostCheckerFuture = this.connectionLostCheckerService.scheduleAtFixedRate(connectionLostChecker, this.connectionLostTimeout, this.connectionLostTimeout, TimeUnit.NANOSECONDS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void executeConnectionLostDetection(WebSocket webSocket, long minimumPongTime) {
        if (!(webSocket instanceof WebSocketImpl)) {
            return;
        }
        WebSocketImpl webSocketImpl = (WebSocketImpl) webSocket;
        if (webSocketImpl.getLastPong() < minimumPongTime) {
            this.log.trace("Closing connection due to no pong received: {}", webSocketImpl);
            webSocketImpl.closeConnection(1006, "The connection was closed because the other endpoint did not respond with a pong in time. For more information check: https://github.com/TooTallNate/Java-WebSocket/wiki/Lost-connection-detection");
        } else if (webSocketImpl.isOpen()) {
            webSocketImpl.sendPing();
        } else {
            this.log.trace("Trying to ping a non open connection: {}", webSocketImpl);
        }
    }

    private void cancelConnectionLostTimer() {
        if (this.connectionLostCheckerService != null) {
            this.connectionLostCheckerService.shutdownNow();
            this.connectionLostCheckerService = null;
        }
        if (this.connectionLostCheckerFuture != null) {
            this.connectionLostCheckerFuture.cancel(false);
            this.connectionLostCheckerFuture = null;
        }
    }

    public boolean isTcpNoDelay() {
        return this.tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public boolean isReuseAddr() {
        return this.reuseAddr;
    }

    public void setReuseAddr(boolean reuseAddr) {
        this.reuseAddr = reuseAddr;
    }

    public boolean isDaemon() {
        return this.daemon;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public int getReceiveBufferSize() {
        return this.receiveBufferSize;
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        if (receiveBufferSize < 0) {
            throw new IllegalArgumentException("buffer size < 0");
        }
        this.receiveBufferSize = receiveBufferSize;
    }
}
