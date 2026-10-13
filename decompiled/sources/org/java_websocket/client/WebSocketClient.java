package org.java_websocket.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.java_websocket.AbstractWebSocket;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.enums.Opcode;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.HandshakeImpl1Client;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.protocols.IProtocol;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/client/WebSocketClient.class */
public abstract class WebSocketClient extends AbstractWebSocket implements Runnable, WebSocket {
    protected URI uri;
    private WebSocketImpl engine;
    private Socket socket;
    private SocketFactory socketFactory;
    private OutputStream ostream;
    private Proxy proxy;
    private Thread writeThread;
    private Thread connectReadThread;
    private Draft draft;
    private Map<String, String> headers;
    private CountDownLatch connectLatch;
    private CountDownLatch closeLatch;
    private int connectTimeout;
    private DnsResolver dnsResolver;

    public abstract void onOpen(ServerHandshake serverHandshake);

    public abstract void onMessage(String str);

    public abstract void onClose(int i, String str, boolean z);

    public abstract void onError(Exception exc);

    public WebSocketClient(URI serverUri) {
        this(serverUri, new Draft_6455());
    }

    public WebSocketClient(URI serverUri, Draft protocolDraft) {
        this(serverUri, protocolDraft, null, 0);
    }

    public WebSocketClient(URI serverUri, Map<String, String> httpHeaders) {
        this(serverUri, new Draft_6455(), httpHeaders);
    }

    public WebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders) {
        this(serverUri, protocolDraft, httpHeaders, 0);
    }

    public WebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
        this.uri = null;
        this.engine = null;
        this.socket = null;
        this.socketFactory = null;
        this.proxy = Proxy.NO_PROXY;
        this.connectLatch = new CountDownLatch(1);
        this.closeLatch = new CountDownLatch(1);
        this.connectTimeout = 0;
        this.dnsResolver = null;
        if (serverUri == null) {
            throw new IllegalArgumentException();
        }
        if (protocolDraft == null) {
            throw new IllegalArgumentException("null as draft is permitted for `WebSocketServer` only!");
        }
        this.uri = serverUri;
        this.draft = protocolDraft;
        this.dnsResolver = new DnsResolver() { // from class: org.java_websocket.client.WebSocketClient.1
            @Override // org.java_websocket.client.DnsResolver
            public InetAddress resolve(URI uri) throws UnknownHostException {
                return InetAddress.getByName(uri.getHost());
            }
        };
        if (httpHeaders != null) {
            this.headers = new TreeMap(String.CASE_INSENSITIVE_ORDER);
            this.headers.putAll(httpHeaders);
        }
        this.connectTimeout = connectTimeout;
        setTcpNoDelay(false);
        setReuseAddr(false);
        this.engine = new WebSocketImpl(this, protocolDraft);
    }

    public URI getURI() {
        return this.uri;
    }

    @Override // org.java_websocket.WebSocket
    public Draft getDraft() {
        return this.draft;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void addHeader(String key, String value) {
        if (this.headers == null) {
            this.headers = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        }
        this.headers.put(key, value);
    }

    public String removeHeader(String key) {
        if (this.headers == null) {
            return null;
        }
        return this.headers.remove(key);
    }

    public void clearHeaders() {
        this.headers = null;
    }

    public void setDnsResolver(DnsResolver dnsResolver) {
        this.dnsResolver = dnsResolver;
    }

    public void reconnect() {
        reset();
        connect();
    }

    public boolean reconnectBlocking() throws InterruptedException {
        reset();
        return connectBlocking();
    }

    private void reset() {
        Thread current = Thread.currentThread();
        if (current == this.writeThread || current == this.connectReadThread) {
            throw new IllegalStateException("You cannot initialize a reconnect out of the websocket thread. Use reconnect in another thread to ensure a successful cleanup.");
        }
        try {
            if (this.engine.getReadyState() == ReadyState.NOT_YET_CONNECTED && this.socket != null) {
                this.socket.close();
            }
            closeBlocking();
            if (this.writeThread != null) {
                this.writeThread.interrupt();
                this.writeThread.join();
                this.writeThread = null;
            }
            if (this.connectReadThread != null) {
                this.connectReadThread.interrupt();
                this.connectReadThread.join();
                this.connectReadThread = null;
            }
            this.draft.reset();
            if (this.socket != null) {
                this.socket.close();
                this.socket = null;
            }
            this.connectLatch = new CountDownLatch(1);
            this.closeLatch = new CountDownLatch(1);
            this.engine = new WebSocketImpl(this, this.draft);
        } catch (Exception e) {
            onError(e);
            this.engine.closeConnection(1006, e.getMessage());
        }
    }

    public void connect() {
        if (this.connectReadThread != null) {
            throw new IllegalStateException("WebSocketClient objects are not reuseable");
        }
        this.connectReadThread = new Thread(this);
        this.connectReadThread.setDaemon(isDaemon());
        this.connectReadThread.setName("WebSocketConnectReadThread-" + this.connectReadThread.getId());
        this.connectReadThread.start();
    }

    public boolean connectBlocking() throws InterruptedException {
        connect();
        this.connectLatch.await();
        return this.engine.isOpen();
    }

    public boolean connectBlocking(long timeout, TimeUnit timeUnit) throws InterruptedException {
        connect();
        boolean connected = this.connectLatch.await(timeout, timeUnit);
        if (!connected) {
            reset();
        }
        return connected && this.engine.isOpen();
    }

    @Override // org.java_websocket.WebSocket
    public void close() {
        if (this.writeThread != null) {
            this.engine.close(1000);
        }
    }

    public void closeBlocking() throws InterruptedException {
        close();
        this.closeLatch.await();
    }

    @Override // org.java_websocket.WebSocket
    public void send(String text) {
        this.engine.send(text);
    }

    @Override // org.java_websocket.WebSocket
    public void send(byte[] data) {
        this.engine.send(data);
    }

    @Override // org.java_websocket.WebSocket
    public <T> T getAttachment() {
        return (T) this.engine.getAttachment();
    }

    @Override // org.java_websocket.WebSocket
    public <T> void setAttachment(T attachment) {
        this.engine.setAttachment(attachment);
    }

    @Override // org.java_websocket.AbstractWebSocket
    protected Collection<WebSocket> getConnections() {
        return Collections.singletonList(this.engine);
    }

    @Override // org.java_websocket.WebSocket
    public void sendPing() {
        this.engine.sendPing();
    }

    @Override // java.lang.Runnable
    public void run() {
        int readBytes;
        try {
            boolean upgradeSocketToSSLSocket = prepareSocket();
            this.socket.setTcpNoDelay(isTcpNoDelay());
            this.socket.setReuseAddress(isReuseAddr());
            int receiveBufferSize = getReceiveBufferSize();
            if (receiveBufferSize > 0) {
                this.socket.setReceiveBufferSize(receiveBufferSize);
            }
            if (!this.socket.isConnected()) {
                InetSocketAddress addr = this.dnsResolver == null ? InetSocketAddress.createUnresolved(this.uri.getHost(), getPort()) : new InetSocketAddress(this.dnsResolver.resolve(this.uri), getPort());
                this.socket.connect(addr, this.connectTimeout);
            }
            if (upgradeSocketToSSLSocket && "wss".equals(this.uri.getScheme())) {
                upgradeSocketToSSL();
            }
            if (this.socket instanceof SSLSocket) {
                SSLSocket sslSocket = (SSLSocket) this.socket;
                SSLParameters sslParameters = sslSocket.getSSLParameters();
                onSetSSLParameters(sslParameters);
                sslSocket.setSSLParameters(sslParameters);
            }
            InputStream istream = this.socket.getInputStream();
            this.ostream = this.socket.getOutputStream();
            sendHandshake();
            if (this.writeThread != null) {
                this.writeThread.interrupt();
                try {
                    this.writeThread.join();
                } catch (InterruptedException e) {
                }
            }
            this.writeThread = new Thread(new WebsocketWriteThread(this));
            this.writeThread.setDaemon(isDaemon());
            this.writeThread.start();
            int receiveBufferSize2 = getReceiveBufferSize();
            byte[] rawbuffer = new byte[receiveBufferSize2 > 0 ? receiveBufferSize2 : DEFAULT_READ_BUFFER_SIZE];
            while (!isClosing() && !isClosed() && (readBytes = istream.read(rawbuffer)) != -1) {
                try {
                    this.engine.decode(ByteBuffer.wrap(rawbuffer, 0, readBytes));
                } catch (IOException e2) {
                    handleIOException(e2);
                    return;
                } catch (RuntimeException e3) {
                    onError(e3);
                    this.engine.closeConnection(1006, e3.getMessage());
                    return;
                }
            }
            this.engine.eot();
        } catch (Exception e4) {
            onWebsocketError(this.engine, e4);
            this.engine.closeConnection(-1, e4.getMessage());
        } catch (InternalError e5) {
            if ((e5.getCause() instanceof InvocationTargetException) && (e5.getCause().getCause() instanceof IOException)) {
                IOException cause = (IOException) e5.getCause().getCause();
                onWebsocketError(this.engine, cause);
                this.engine.closeConnection(-1, cause.getMessage());
                return;
            }
            throw e5;
        }
    }

    private void upgradeSocketToSSL() throws NoSuchAlgorithmException, IOException, KeyManagementException {
        SSLSocketFactory factory;
        if (this.socketFactory instanceof SSLSocketFactory) {
            factory = (SSLSocketFactory) this.socketFactory;
        } else {
            factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        }
        this.socket = factory.createSocket(this.socket, this.uri.getHost(), getPort(), true);
    }

    private boolean prepareSocket() throws IOException {
        boolean upgradeSocketToSSLSocket = false;
        if (this.proxy != Proxy.NO_PROXY) {
            this.socket = new Socket(this.proxy);
            upgradeSocketToSSLSocket = true;
        } else if (this.socketFactory != null) {
            this.socket = this.socketFactory.createSocket();
        } else if (this.socket == null) {
            this.socket = new Socket(this.proxy);
            upgradeSocketToSSLSocket = true;
        } else if (this.socket.isClosed()) {
            throw new IOException();
        }
        return upgradeSocketToSSLSocket;
    }

    protected void onSetSSLParameters(SSLParameters sslParameters) {
        sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
    }

    private int getPort() {
        int port = this.uri.getPort();
        String scheme = this.uri.getScheme();
        if ("wss".equals(scheme)) {
            return port == -1 ? WebSocketImpl.DEFAULT_WSS_PORT : port;
        }
        if ("ws".equals(scheme)) {
            if (port == -1) {
                return 80;
            }
            return port;
        }
        throw new IllegalArgumentException("unknown scheme: " + scheme);
    }

    private void sendHandshake() throws InvalidHandshakeException {
        String path;
        String part1 = this.uri.getRawPath();
        String part2 = this.uri.getRawQuery();
        if (part1 == null || part1.length() == 0) {
            path = "/";
        } else {
            path = part1;
        }
        if (part2 != null) {
            path = path + '?' + part2;
        }
        int port = getPort();
        String host = this.uri.getHost() + ((port == 80 || port == 443) ? "" : ":" + port);
        HandshakeImpl1Client handshake = new HandshakeImpl1Client();
        handshake.setResourceDescriptor(path);
        handshake.put("Host", host);
        if (this.headers != null) {
            for (Map.Entry<String, String> kv : this.headers.entrySet()) {
                handshake.put(kv.getKey(), kv.getValue());
            }
        }
        this.engine.startHandshake(handshake);
    }

    @Override // org.java_websocket.WebSocket
    public ReadyState getReadyState() {
        return this.engine.getReadyState();
    }

    @Override // org.java_websocket.WebSocketListener
    public final void onWebsocketMessage(WebSocket conn, String message) {
        onMessage(message);
    }

    @Override // org.java_websocket.WebSocketListener
    public final void onWebsocketMessage(WebSocket conn, ByteBuffer blob) {
        onMessage(blob);
    }

    @Override // org.java_websocket.WebSocketListener
    public final void onWebsocketOpen(WebSocket conn, Handshakedata handshake) {
        startConnectionLostTimer();
        onOpen((ServerHandshake) handshake);
        this.connectLatch.countDown();
    }

    @Override // org.java_websocket.WebSocketListener
    public final void onWebsocketClose(WebSocket conn, int code, String reason, boolean remote) {
        stopConnectionLostTimer();
        if (this.writeThread != null) {
            this.writeThread.interrupt();
        }
        onClose(code, reason, remote);
        this.connectLatch.countDown();
        this.closeLatch.countDown();
    }

    @Override // org.java_websocket.WebSocketListener
    public final void onWebsocketError(WebSocket conn, Exception ex) {
        onError(ex);
    }

    @Override // org.java_websocket.WebSocketListener
    public final void onWriteDemand(WebSocket conn) {
    }

    @Override // org.java_websocket.WebSocketListener
    public void onWebsocketCloseInitiated(WebSocket conn, int code, String reason) {
        onCloseInitiated(code, reason);
    }

    @Override // org.java_websocket.WebSocketListener
    public void onWebsocketClosing(WebSocket conn, int code, String reason, boolean remote) {
        onClosing(code, reason, remote);
    }

    public void onCloseInitiated(int code, String reason) {
    }

    public void onClosing(int code, String reason, boolean remote) {
    }

    public WebSocket getConnection() {
        return this.engine;
    }

    @Override // org.java_websocket.WebSocketListener
    public InetSocketAddress getLocalSocketAddress(WebSocket conn) {
        if (this.socket != null) {
            return (InetSocketAddress) this.socket.getLocalSocketAddress();
        }
        return null;
    }

    @Override // org.java_websocket.WebSocketListener
    public InetSocketAddress getRemoteSocketAddress(WebSocket conn) {
        if (this.socket != null) {
            return (InetSocketAddress) this.socket.getRemoteSocketAddress();
        }
        return null;
    }

    public void onMessage(ByteBuffer bytes) {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/java_websocket/client/WebSocketClient$WebsocketWriteThread.class */
    private class WebsocketWriteThread implements Runnable {
        private final WebSocketClient webSocketClient;

        WebsocketWriteThread(WebSocketClient webSocketClient) {
            this.webSocketClient = webSocketClient;
        }

        @Override // java.lang.Runnable
        public void run() {
            Thread.currentThread().setName("WebSocketWriteThread-" + Thread.currentThread().getId());
            try {
                runWriteData();
            } catch (IOException e) {
                WebSocketClient.this.handleIOException(e);
            } finally {
                closeSocket();
            }
        }

        private void runWriteData() throws IOException {
            while (!Thread.interrupted()) {
                try {
                    ByteBuffer buffer = WebSocketClient.this.engine.outQueue.take();
                    WebSocketClient.this.ostream.write(buffer.array(), 0, buffer.limit());
                    WebSocketClient.this.ostream.flush();
                } catch (InterruptedException e) {
                    for (ByteBuffer buffer2 : WebSocketClient.this.engine.outQueue) {
                        WebSocketClient.this.ostream.write(buffer2.array(), 0, buffer2.limit());
                        WebSocketClient.this.ostream.flush();
                    }
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        private void closeSocket() {
            try {
                if (WebSocketClient.this.socket != null) {
                    WebSocketClient.this.socket.close();
                }
            } catch (IOException ex) {
                WebSocketClient.this.onWebsocketError(this.webSocketClient, ex);
            }
        }
    }

    public void setProxy(Proxy proxy) {
        if (proxy == null) {
            throw new IllegalArgumentException();
        }
        this.proxy = proxy;
    }

    @Deprecated
    public void setSocket(Socket socket) {
        if (this.socket != null) {
            throw new IllegalStateException("socket has already been set");
        }
        this.socket = socket;
    }

    public void setSocketFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    @Override // org.java_websocket.WebSocket
    public void sendFragmentedFrame(Opcode op, ByteBuffer buffer, boolean fin) {
        this.engine.sendFragmentedFrame(op, buffer, fin);
    }

    @Override // org.java_websocket.WebSocket
    public boolean isOpen() {
        return this.engine.isOpen();
    }

    @Override // org.java_websocket.WebSocket
    public boolean isFlushAndClose() {
        return this.engine.isFlushAndClose();
    }

    @Override // org.java_websocket.WebSocket
    public boolean isClosed() {
        return this.engine.isClosed();
    }

    @Override // org.java_websocket.WebSocket
    public boolean isClosing() {
        return this.engine.isClosing();
    }

    @Override // org.java_websocket.WebSocket
    public boolean hasBufferedData() {
        return this.engine.hasBufferedData();
    }

    @Override // org.java_websocket.WebSocket
    public void close(int code) {
        this.engine.close(code);
    }

    @Override // org.java_websocket.WebSocket
    public void close(int code, String message) {
        this.engine.close(code, message);
    }

    @Override // org.java_websocket.WebSocket
    public void closeConnection(int code, String message) {
        this.engine.closeConnection(code, message);
    }

    @Override // org.java_websocket.WebSocket
    public void send(ByteBuffer bytes) {
        this.engine.send(bytes);
    }

    @Override // org.java_websocket.WebSocket
    public void sendFrame(Framedata framedata) {
        this.engine.sendFrame(framedata);
    }

    @Override // org.java_websocket.WebSocket
    public void sendFrame(Collection<Framedata> frames) {
        this.engine.sendFrame(frames);
    }

    @Override // org.java_websocket.WebSocket
    public InetSocketAddress getLocalSocketAddress() {
        return this.engine.getLocalSocketAddress();
    }

    @Override // org.java_websocket.WebSocket
    public InetSocketAddress getRemoteSocketAddress() {
        return this.engine.getRemoteSocketAddress();
    }

    @Override // org.java_websocket.WebSocket
    public String getResourceDescriptor() {
        return this.uri.getPath();
    }

    @Override // org.java_websocket.WebSocket
    public boolean hasSSLSupport() {
        return this.socket instanceof SSLSocket;
    }

    @Override // org.java_websocket.WebSocket
    public SSLSession getSSLSession() {
        if (!hasSSLSupport()) {
            throw new IllegalArgumentException("This websocket uses ws instead of wss. No SSLSession available.");
        }
        return ((SSLSocket) this.socket).getSession();
    }

    @Override // org.java_websocket.WebSocket
    public IProtocol getProtocol() {
        return this.engine.getProtocol();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleIOException(IOException e) {
        if (e instanceof SSLException) {
            onError(e);
        }
        this.engine.eot();
    }
}
