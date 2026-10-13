package org.java_websocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.net.ssl.SSLSession;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.enums.CloseHandshakeType;
import org.java_websocket.enums.HandshakeState;
import org.java_websocket.enums.Opcode;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.enums.Role;
import org.java_websocket.exceptions.IncompleteHandshakeException;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.exceptions.LimitExceededException;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.PingFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ClientHandshakeBuilder;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.interfaces.ISSLChannel;
import org.java_websocket.protocols.IProtocol;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.util.Charsetfunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/WebSocketImpl.class */
public class WebSocketImpl implements WebSocket {
    public static final int DEFAULT_PORT = 80;
    public static final int DEFAULT_WSS_PORT = 443;
    private final Logger log;
    public final BlockingQueue<ByteBuffer> outQueue;
    public final BlockingQueue<ByteBuffer> inQueue;
    private final WebSocketListener wsl;
    private SelectionKey key;
    private ByteChannel channel;
    private WebSocketServer.WebSocketWorker workerThread;
    private boolean flushandclosestate;
    private volatile ReadyState readyState;
    private List<Draft> knownDrafts;
    private Draft draft;
    private Role role;
    private ByteBuffer tmpHandshakeBytes;
    private ClientHandshake handshakerequest;
    private String closemessage;
    private Integer closecode;
    private Boolean closedremotely;
    private String resourceDescriptor;
    private long lastPong;
    private final Object synchronizeWriteObject;
    private Object attachment;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !WebSocketImpl.class.desiredAssertionStatus();
    }

    public WebSocketImpl(WebSocketListener listener, List<Draft> drafts) {
        this(listener, (Draft) null);
        this.role = Role.SERVER;
        if (drafts == null || drafts.isEmpty()) {
            this.knownDrafts = new ArrayList();
            this.knownDrafts.add(new Draft_6455());
        } else {
            this.knownDrafts = drafts;
        }
    }

    public WebSocketImpl(WebSocketListener listener, Draft draft) {
        this.log = LoggerFactory.getLogger((Class<?>) WebSocketImpl.class);
        this.flushandclosestate = false;
        this.readyState = ReadyState.NOT_YET_CONNECTED;
        this.draft = null;
        this.tmpHandshakeBytes = ByteBuffer.allocate(0);
        this.handshakerequest = null;
        this.closemessage = null;
        this.closecode = null;
        this.closedremotely = null;
        this.resourceDescriptor = null;
        this.lastPong = System.nanoTime();
        this.synchronizeWriteObject = new Object();
        if (listener == null || (draft == null && this.role == Role.SERVER)) {
            throw new IllegalArgumentException("parameters must not be null");
        }
        this.outQueue = new LinkedBlockingQueue();
        this.inQueue = new LinkedBlockingQueue();
        this.wsl = listener;
        this.role = Role.CLIENT;
        if (draft != null) {
            this.draft = draft.copyInstance();
        }
    }

    public void decode(ByteBuffer socketBuffer) {
        if (!$assertionsDisabled && !socketBuffer.hasRemaining()) {
            throw new AssertionError();
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace("process({}): ({})", Integer.valueOf(socketBuffer.remaining()), socketBuffer.remaining() > 1000 ? "too big to display" : new String(socketBuffer.array(), socketBuffer.position(), socketBuffer.remaining()));
        }
        if (this.readyState != ReadyState.NOT_YET_CONNECTED) {
            if (this.readyState == ReadyState.OPEN) {
                decodeFrames(socketBuffer);
            }
        } else if (decodeHandshake(socketBuffer) && !isClosing() && !isClosed()) {
            if (!$assertionsDisabled && this.tmpHandshakeBytes.hasRemaining() == socketBuffer.hasRemaining() && socketBuffer.hasRemaining()) {
                throw new AssertionError();
            }
            if (socketBuffer.hasRemaining()) {
                decodeFrames(socketBuffer);
            } else if (this.tmpHandshakeBytes.hasRemaining()) {
                decodeFrames(this.tmpHandshakeBytes);
            }
        }
    }

    private boolean decodeHandshake(ByteBuffer socketBufferNew) {
        ByteBuffer socketBuffer;
        if (this.tmpHandshakeBytes.capacity() == 0) {
            socketBuffer = socketBufferNew;
        } else {
            if (this.tmpHandshakeBytes.remaining() < socketBufferNew.remaining()) {
                ByteBuffer buf = ByteBuffer.allocate(this.tmpHandshakeBytes.capacity() + socketBufferNew.remaining());
                this.tmpHandshakeBytes.flip();
                buf.put(this.tmpHandshakeBytes);
                this.tmpHandshakeBytes = buf;
            }
            this.tmpHandshakeBytes.put(socketBufferNew);
            this.tmpHandshakeBytes.flip();
            socketBuffer = this.tmpHandshakeBytes;
        }
        socketBuffer.mark();
        try {
            try {
                if (this.role == Role.SERVER) {
                    if (this.draft == null) {
                        Iterator<Draft> it = this.knownDrafts.iterator();
                        while (it.hasNext()) {
                            Draft d = it.next().copyInstance();
                            try {
                                d.setParseMode(this.role);
                                socketBuffer.reset();
                                Handshakedata tmphandshake = d.translateHandshake(socketBuffer);
                                if (!(tmphandshake instanceof ClientHandshake)) {
                                    this.log.trace("Closing due to wrong handshake");
                                    closeConnectionDueToWrongHandshake(new InvalidDataException(1002, "wrong http function"));
                                    return false;
                                }
                                ClientHandshake handshake = (ClientHandshake) tmphandshake;
                                HandshakeState handshakestate = d.acceptHandshakeAsServer(handshake);
                                if (handshakestate == HandshakeState.MATCHED) {
                                    this.resourceDescriptor = handshake.getResourceDescriptor();
                                    try {
                                        ServerHandshakeBuilder response = this.wsl.onWebsocketHandshakeReceivedAsServer(this, d, handshake);
                                        write(d.createHandshake(d.postProcessHandshakeResponseAsServer(handshake, response)));
                                        this.draft = d;
                                        open(handshake);
                                        return true;
                                    } catch (RuntimeException e) {
                                        this.log.error("Closing due to internal server error", (Throwable) e);
                                        this.wsl.onWebsocketError(this, e);
                                        closeConnectionDueToInternalServerError(e);
                                        return false;
                                    } catch (InvalidDataException e2) {
                                        this.log.trace("Closing due to wrong handshake. Possible handshake rejection", (Throwable) e2);
                                        closeConnectionDueToWrongHandshake(e2);
                                        return false;
                                    }
                                }
                            } catch (InvalidHandshakeException e3) {
                            }
                        }
                        if (this.draft == null) {
                            this.log.trace("Closing due to protocol error: no draft matches");
                            closeConnectionDueToWrongHandshake(new InvalidDataException(1002, "no draft matches"));
                            return false;
                        }
                        return false;
                    }
                    Handshakedata tmphandshake2 = this.draft.translateHandshake(socketBuffer);
                    if (!(tmphandshake2 instanceof ClientHandshake)) {
                        this.log.trace("Closing due to protocol error: wrong http function");
                        flushAndClose(1002, "wrong http function", false);
                        return false;
                    }
                    ClientHandshake handshake2 = (ClientHandshake) tmphandshake2;
                    HandshakeState handshakestate2 = this.draft.acceptHandshakeAsServer(handshake2);
                    if (handshakestate2 == HandshakeState.MATCHED) {
                        open(handshake2);
                        return true;
                    }
                    this.log.trace("Closing due to protocol error: the handshake did finally not match");
                    close(1002, "the handshake did finally not match");
                    return false;
                }
                if (this.role == Role.CLIENT) {
                    this.draft.setParseMode(this.role);
                    Handshakedata tmphandshake3 = this.draft.translateHandshake(socketBuffer);
                    if (!(tmphandshake3 instanceof ServerHandshake)) {
                        this.log.trace("Closing due to protocol error: wrong http function");
                        flushAndClose(1002, "wrong http function", false);
                        return false;
                    }
                    ServerHandshake handshake3 = (ServerHandshake) tmphandshake3;
                    HandshakeState handshakestate3 = this.draft.acceptHandshakeAsClient(this.handshakerequest, handshake3);
                    if (handshakestate3 == HandshakeState.MATCHED) {
                        try {
                            this.wsl.onWebsocketHandshakeReceivedAsClient(this, this.handshakerequest, handshake3);
                            open(handshake3);
                            return true;
                        } catch (RuntimeException e4) {
                            this.log.error("Closing since client was never connected", (Throwable) e4);
                            this.wsl.onWebsocketError(this, e4);
                            flushAndClose(-1, e4.getMessage(), false);
                            return false;
                        } catch (InvalidDataException e5) {
                            this.log.trace("Closing due to invalid data exception. Possible handshake rejection", (Throwable) e5);
                            flushAndClose(e5.getCloseCode(), e5.getMessage(), false);
                            return false;
                        }
                    }
                    this.log.trace("Closing due to protocol error: draft {} refuses handshake", this.draft);
                    close(1002, "draft " + this.draft + " refuses handshake");
                }
                return false;
            } catch (InvalidHandshakeException e6) {
                this.log.trace("Closing due to invalid handshake", (Throwable) e6);
                close(e6);
            }
        } catch (IncompleteHandshakeException e7) {
            if (this.tmpHandshakeBytes.capacity() == 0) {
                socketBuffer.reset();
                int newsize = e7.getPreferredSize();
                if (newsize == 0) {
                    newsize = socketBuffer.capacity() + 16;
                } else if (!$assertionsDisabled && e7.getPreferredSize() < socketBuffer.remaining()) {
                    throw new AssertionError();
                }
                this.tmpHandshakeBytes = ByteBuffer.allocate(newsize);
                this.tmpHandshakeBytes.put(socketBufferNew);
                return false;
            }
            this.tmpHandshakeBytes.position(this.tmpHandshakeBytes.limit());
            this.tmpHandshakeBytes.limit(this.tmpHandshakeBytes.capacity());
            return false;
        }
    }

    private void decodeFrames(ByteBuffer socketBuffer) {
        try {
            List<Framedata> frames = this.draft.translateFrame(socketBuffer);
            for (Framedata f : frames) {
                this.log.trace("matched frame: {}", f);
                this.draft.processFrame(this, f);
            }
        } catch (LinkageError | ThreadDeath | VirtualMachineError e) {
            this.log.error("Got fatal error during frame processing");
            throw e;
        } catch (Error e2) {
            this.log.error("Closing web socket due to an error during frame processing");
            Exception exception = new Exception(e2);
            this.wsl.onWebsocketError(this, exception);
            String errorMessage = "Got error " + e2.getClass().getName();
            close(1011, errorMessage);
        } catch (LimitExceededException e3) {
            if (e3.getLimit() == Integer.MAX_VALUE) {
                this.log.error("Closing due to invalid size of frame", (Throwable) e3);
                this.wsl.onWebsocketError(this, e3);
            }
            close(e3);
        } catch (InvalidDataException e4) {
            this.log.error("Closing due to invalid data in frame", (Throwable) e4);
            this.wsl.onWebsocketError(this, e4);
            close(e4);
        }
    }

    private void closeConnectionDueToWrongHandshake(InvalidDataException exception) {
        write(generateHttpResponseDueToError(404));
        flushAndClose(exception.getCloseCode(), exception.getMessage(), false);
    }

    private void closeConnectionDueToInternalServerError(RuntimeException exception) {
        write(generateHttpResponseDueToError(500));
        flushAndClose(-1, exception.getMessage(), false);
    }

    private ByteBuffer generateHttpResponseDueToError(int errorCode) {
        String errorCodeDescription;
        switch (errorCode) {
            case 404:
                errorCodeDescription = "404 WebSocket Upgrade Failure";
                break;
            case 500:
            default:
                errorCodeDescription = "500 Internal Server Error";
                break;
        }
        return ByteBuffer.wrap(Charsetfunctions.asciiBytes("HTTP/1.1 " + errorCodeDescription + "\r\nContent-Type: text/html\r\nServer: TooTallNate Java-WebSocket\r\nContent-Length: " + (48 + errorCodeDescription.length()) + "\r\n\r\n<html><head></head><body><h1>" + errorCodeDescription + "</h1></body></html>"));
    }

    public synchronized void close(int code, String message, boolean remote) {
        if (this.readyState != ReadyState.CLOSING && this.readyState != ReadyState.CLOSED) {
            if (this.readyState == ReadyState.OPEN) {
                if (code == 1006) {
                    if (!$assertionsDisabled && remote) {
                        throw new AssertionError();
                    }
                    this.readyState = ReadyState.CLOSING;
                    flushAndClose(code, message, false);
                    return;
                }
                if (this.draft.getCloseHandshakeType() != CloseHandshakeType.NONE) {
                    if (!remote) {
                        try {
                            try {
                                this.wsl.onWebsocketCloseInitiated(this, code, message);
                            } catch (RuntimeException e) {
                                this.wsl.onWebsocketError(this, e);
                            }
                        } catch (InvalidDataException e2) {
                            this.log.error("generated frame is invalid", (Throwable) e2);
                            this.wsl.onWebsocketError(this, e2);
                            flushAndClose(1006, "generated frame is invalid", false);
                        }
                    }
                    if (isOpen()) {
                        CloseFrame closeFrame = new CloseFrame();
                        closeFrame.setReason(message);
                        closeFrame.setCode(code);
                        closeFrame.isValid();
                        sendFrame(closeFrame);
                    }
                }
                flushAndClose(code, message, remote);
            } else if (code == -3) {
                if (!$assertionsDisabled && !remote) {
                    throw new AssertionError();
                }
                flushAndClose(-3, message, true);
            } else if (code == 1002) {
                flushAndClose(code, message, remote);
            } else {
                flushAndClose(-1, message, false);
            }
            this.readyState = ReadyState.CLOSING;
            this.tmpHandshakeBytes = null;
        }
    }

    @Override // org.java_websocket.WebSocket
    public void close(int code, String message) {
        close(code, message, false);
    }

    public synchronized void closeConnection(int code, String message, boolean remote) {
        if (this.readyState == ReadyState.CLOSED) {
            return;
        }
        if (this.readyState == ReadyState.OPEN && code == 1006) {
            this.readyState = ReadyState.CLOSING;
        }
        if (this.key != null) {
            this.key.cancel();
        }
        if (this.channel != null) {
            try {
                this.channel.close();
            } catch (IOException e) {
                if (e.getMessage() != null && e.getMessage().equals("Broken pipe")) {
                    this.log.trace("Caught IOException: Broken pipe during closeConnection()", (Throwable) e);
                } else {
                    this.log.error("Exception during channel.close()", (Throwable) e);
                    this.wsl.onWebsocketError(this, e);
                }
            }
        }
        try {
            this.wsl.onWebsocketClose(this, code, message, remote);
        } catch (RuntimeException e2) {
            this.wsl.onWebsocketError(this, e2);
        }
        if (this.draft != null) {
            this.draft.reset();
        }
        this.handshakerequest = null;
        this.readyState = ReadyState.CLOSED;
    }

    protected void closeConnection(int code, boolean remote) {
        closeConnection(code, "", remote);
    }

    public void closeConnection() {
        if (this.closedremotely == null) {
            throw new IllegalStateException("this method must be used in conjunction with flushAndClose");
        }
        closeConnection(this.closecode.intValue(), this.closemessage, this.closedremotely.booleanValue());
    }

    @Override // org.java_websocket.WebSocket
    public void closeConnection(int code, String message) {
        closeConnection(code, message, false);
    }

    public synchronized void flushAndClose(int code, String message, boolean remote) {
        if (this.flushandclosestate) {
            return;
        }
        this.closecode = Integer.valueOf(code);
        this.closemessage = message;
        this.closedremotely = Boolean.valueOf(remote);
        this.flushandclosestate = true;
        this.wsl.onWriteDemand(this);
        try {
            this.wsl.onWebsocketClosing(this, code, message, remote);
        } catch (RuntimeException e) {
            this.log.error("Exception in onWebsocketClosing", (Throwable) e);
            this.wsl.onWebsocketError(this, e);
        }
        if (this.draft != null) {
            this.draft.reset();
        }
        this.handshakerequest = null;
    }

    public void eot() {
        if (this.readyState == ReadyState.NOT_YET_CONNECTED) {
            closeConnection(-1, true);
            return;
        }
        if (this.flushandclosestate) {
            closeConnection(this.closecode.intValue(), this.closemessage, this.closedremotely.booleanValue());
            return;
        }
        if (this.draft.getCloseHandshakeType() == CloseHandshakeType.NONE) {
            closeConnection(1000, true);
            return;
        }
        if (this.draft.getCloseHandshakeType() == CloseHandshakeType.ONEWAY) {
            if (this.role == Role.SERVER) {
                closeConnection(1006, true);
                return;
            } else {
                closeConnection(1000, true);
                return;
            }
        }
        closeConnection(1006, true);
    }

    @Override // org.java_websocket.WebSocket
    public void close(int code) {
        close(code, "", false);
    }

    public void close(InvalidDataException e) {
        close(e.getCloseCode(), e.getMessage(), false);
    }

    @Override // org.java_websocket.WebSocket
    public void send(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Cannot send 'null' data to a WebSocketImpl.");
        }
        send(this.draft.createFrames(text, this.role == Role.CLIENT));
    }

    @Override // org.java_websocket.WebSocket
    public void send(ByteBuffer bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Cannot send 'null' data to a WebSocketImpl.");
        }
        send(this.draft.createFrames(bytes, this.role == Role.CLIENT));
    }

    @Override // org.java_websocket.WebSocket
    public void send(byte[] bytes) {
        send(ByteBuffer.wrap(bytes));
    }

    private void send(Collection<Framedata> frames) {
        if (!isOpen()) {
            throw new WebsocketNotConnectedException();
        }
        if (frames == null) {
            throw new IllegalArgumentException();
        }
        ArrayList<ByteBuffer> outgoingFrames = new ArrayList<>();
        for (Framedata f : frames) {
            this.log.trace("send frame: {}", f);
            outgoingFrames.add(this.draft.createBinaryFrame(f));
        }
        write(outgoingFrames);
    }

    @Override // org.java_websocket.WebSocket
    public void sendFragmentedFrame(Opcode op, ByteBuffer buffer, boolean fin) {
        send(this.draft.continuousFrame(op, buffer, fin));
    }

    @Override // org.java_websocket.WebSocket
    public void sendFrame(Collection<Framedata> frames) {
        send(frames);
    }

    @Override // org.java_websocket.WebSocket
    public void sendFrame(Framedata framedata) {
        send(Collections.singletonList(framedata));
    }

    @Override // org.java_websocket.WebSocket
    public void sendPing() throws NullPointerException {
        PingFrame pingFrame = this.wsl.onPreparePing(this);
        if (pingFrame == null) {
            throw new NullPointerException("onPreparePing(WebSocket) returned null. PingFrame to sent can't be null.");
        }
        sendFrame(pingFrame);
    }

    @Override // org.java_websocket.WebSocket
    public boolean hasBufferedData() {
        return !this.outQueue.isEmpty();
    }

    public void startHandshake(ClientHandshakeBuilder handshakedata) throws InvalidHandshakeException {
        this.handshakerequest = this.draft.postProcessHandshakeRequestAsClient(handshakedata);
        this.resourceDescriptor = handshakedata.getResourceDescriptor();
        if (!$assertionsDisabled && this.resourceDescriptor == null) {
            throw new AssertionError();
        }
        try {
            this.wsl.onWebsocketHandshakeSentAsClient(this, this.handshakerequest);
            write(this.draft.createHandshake(this.handshakerequest));
        } catch (RuntimeException e) {
            this.log.error("Exception in startHandshake", (Throwable) e);
            this.wsl.onWebsocketError(this, e);
            throw new InvalidHandshakeException("rejected because of " + e);
        } catch (InvalidDataException e2) {
            throw new InvalidHandshakeException("Handshake data rejected by client.");
        }
    }

    private void write(ByteBuffer buf) {
        this.log.trace("write({}): {}", Integer.valueOf(buf.remaining()), buf.remaining() > 1000 ? "too big to display" : new String(buf.array()));
        this.outQueue.add(buf);
        this.wsl.onWriteDemand(this);
    }

    private void write(List<ByteBuffer> bufs) {
        synchronized (this.synchronizeWriteObject) {
            for (ByteBuffer b : bufs) {
                write(b);
            }
        }
    }

    private void open(Handshakedata d) {
        this.log.trace("open using draft: {}", this.draft);
        this.readyState = ReadyState.OPEN;
        updateLastPong();
        try {
            this.wsl.onWebsocketOpen(this, d);
        } catch (RuntimeException e) {
            this.wsl.onWebsocketError(this, e);
        }
    }

    @Override // org.java_websocket.WebSocket
    public boolean isOpen() {
        return this.readyState == ReadyState.OPEN;
    }

    @Override // org.java_websocket.WebSocket
    public boolean isClosing() {
        return this.readyState == ReadyState.CLOSING;
    }

    @Override // org.java_websocket.WebSocket
    public boolean isFlushAndClose() {
        return this.flushandclosestate;
    }

    @Override // org.java_websocket.WebSocket
    public boolean isClosed() {
        return this.readyState == ReadyState.CLOSED;
    }

    @Override // org.java_websocket.WebSocket
    public ReadyState getReadyState() {
        return this.readyState;
    }

    public void setSelectionKey(SelectionKey key) {
        this.key = key;
    }

    public SelectionKey getSelectionKey() {
        return this.key;
    }

    public String toString() {
        return super.toString();
    }

    @Override // org.java_websocket.WebSocket
    public InetSocketAddress getRemoteSocketAddress() {
        return this.wsl.getRemoteSocketAddress(this);
    }

    @Override // org.java_websocket.WebSocket
    public InetSocketAddress getLocalSocketAddress() {
        return this.wsl.getLocalSocketAddress(this);
    }

    @Override // org.java_websocket.WebSocket
    public Draft getDraft() {
        return this.draft;
    }

    @Override // org.java_websocket.WebSocket
    public void close() {
        close(1000);
    }

    @Override // org.java_websocket.WebSocket
    public String getResourceDescriptor() {
        return this.resourceDescriptor;
    }

    long getLastPong() {
        return this.lastPong;
    }

    public void updateLastPong() {
        this.lastPong = System.nanoTime();
    }

    public WebSocketListener getWebSocketListener() {
        return this.wsl;
    }

    @Override // org.java_websocket.WebSocket
    public <T> T getAttachment() {
        return (T) this.attachment;
    }

    @Override // org.java_websocket.WebSocket
    public boolean hasSSLSupport() {
        return this.channel instanceof ISSLChannel;
    }

    @Override // org.java_websocket.WebSocket
    public SSLSession getSSLSession() {
        if (!hasSSLSupport()) {
            throw new IllegalArgumentException("This websocket uses ws instead of wss. No SSLSession available.");
        }
        return ((ISSLChannel) this.channel).getSSLEngine().getSession();
    }

    @Override // org.java_websocket.WebSocket
    public IProtocol getProtocol() {
        if (this.draft == null) {
            return null;
        }
        if (!(this.draft instanceof Draft_6455)) {
            throw new IllegalArgumentException("This draft does not support Sec-WebSocket-Protocol");
        }
        return ((Draft_6455) this.draft).getProtocol();
    }

    @Override // org.java_websocket.WebSocket
    public <T> void setAttachment(T attachment) {
        this.attachment = attachment;
    }

    public ByteChannel getChannel() {
        return this.channel;
    }

    public void setChannel(ByteChannel channel) {
        this.channel = channel;
    }

    public WebSocketServer.WebSocketWorker getWorkerThread() {
        return this.workerThread;
    }

    public void setWorkerThread(WebSocketServer.WebSocketWorker workerThread) {
        this.workerThread = workerThread;
    }
}
