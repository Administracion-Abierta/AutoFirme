package org.java_websocket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Collection;
import javax.net.ssl.SSLSession;
import org.java_websocket.drafts.Draft;
import org.java_websocket.enums.Opcode;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.framing.Framedata;
import org.java_websocket.protocols.IProtocol;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/WebSocket.class */
public interface WebSocket {
    void close(int i, String str);

    void close(int i);

    void close();

    void closeConnection(int i, String str);

    void send(String str);

    void send(ByteBuffer byteBuffer);

    void send(byte[] bArr);

    void sendFrame(Framedata framedata);

    void sendFrame(Collection<Framedata> collection);

    void sendPing();

    void sendFragmentedFrame(Opcode opcode, ByteBuffer byteBuffer, boolean z);

    boolean hasBufferedData();

    InetSocketAddress getRemoteSocketAddress();

    InetSocketAddress getLocalSocketAddress();

    boolean isOpen();

    boolean isClosing();

    boolean isFlushAndClose();

    boolean isClosed();

    Draft getDraft();

    ReadyState getReadyState();

    String getResourceDescriptor();

    <T> void setAttachment(T t);

    <T> T getAttachment();

    boolean hasSSLSupport();

    SSLSession getSSLSession() throws IllegalArgumentException;

    IProtocol getProtocol();
}
