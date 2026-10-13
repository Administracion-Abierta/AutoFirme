package org.java_websocket.drafts;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.enums.CloseHandshakeType;
import org.java_websocket.enums.HandshakeState;
import org.java_websocket.enums.Opcode;
import org.java_websocket.enums.Role;
import org.java_websocket.exceptions.IncompleteHandshakeException;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.framing.BinaryFrame;
import org.java_websocket.framing.ContinuousFrame;
import org.java_websocket.framing.DataFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.TextFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ClientHandshakeBuilder;
import org.java_websocket.handshake.HandshakeBuilder;
import org.java_websocket.handshake.HandshakeImpl1Client;
import org.java_websocket.handshake.HandshakeImpl1Server;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.util.Charsetfunctions;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/drafts/Draft.class */
public abstract class Draft {
    protected Role role = null;
    protected Opcode continuousFrameType = null;

    public abstract HandshakeState acceptHandshakeAsClient(ClientHandshake clientHandshake, ServerHandshake serverHandshake) throws InvalidHandshakeException;

    public abstract HandshakeState acceptHandshakeAsServer(ClientHandshake clientHandshake) throws InvalidHandshakeException;

    public abstract ByteBuffer createBinaryFrame(Framedata framedata);

    public abstract List<Framedata> createFrames(ByteBuffer byteBuffer, boolean z);

    public abstract List<Framedata> createFrames(String str, boolean z);

    public abstract void processFrame(WebSocketImpl webSocketImpl, Framedata framedata) throws InvalidDataException;

    public abstract void reset();

    public abstract ClientHandshakeBuilder postProcessHandshakeRequestAsClient(ClientHandshakeBuilder clientHandshakeBuilder) throws InvalidHandshakeException;

    public abstract HandshakeBuilder postProcessHandshakeResponseAsServer(ClientHandshake clientHandshake, ServerHandshakeBuilder serverHandshakeBuilder) throws InvalidHandshakeException;

    public abstract List<Framedata> translateFrame(ByteBuffer byteBuffer) throws InvalidDataException;

    public abstract CloseHandshakeType getCloseHandshakeType();

    public abstract Draft copyInstance();

    public static ByteBuffer readLine(ByteBuffer buf) {
        ByteBuffer sbuf = ByteBuffer.allocate(buf.remaining());
        byte cur = 48;
        while (buf.hasRemaining()) {
            byte prev = cur;
            cur = buf.get();
            sbuf.put(cur);
            if (prev == 13 && cur == 10) {
                sbuf.limit(sbuf.position() - 2);
                sbuf.position(0);
                return sbuf;
            }
        }
        buf.position(buf.position() - sbuf.position());
        return null;
    }

    public static String readStringLine(ByteBuffer buf) {
        ByteBuffer b = readLine(buf);
        if (b == null) {
            return null;
        }
        return Charsetfunctions.stringAscii(b.array(), 0, b.limit());
    }

    public static HandshakeBuilder translateHandshakeHttp(ByteBuffer buf, Role role) throws InvalidHandshakeException {
        HandshakeBuilder handshake;
        String line;
        String line2 = readStringLine(buf);
        if (line2 == null) {
            throw new IncompleteHandshakeException(buf.capacity() + 128);
        }
        String[] firstLineTokens = line2.split(" ", 3);
        if (firstLineTokens.length != 3) {
            throw new InvalidHandshakeException();
        }
        if (role == Role.CLIENT) {
            handshake = translateHandshakeHttpClient(firstLineTokens, line2);
        } else {
            handshake = translateHandshakeHttpServer(firstLineTokens, line2);
        }
        String stringLine = readStringLine(buf);
        while (true) {
            line = stringLine;
            if (line == null || line.length() <= 0) {
                break;
            }
            String[] pair = line.split(":", 2);
            if (pair.length != 2) {
                throw new InvalidHandshakeException("not an http header");
            }
            if (handshake.hasFieldValue(pair[0])) {
                handshake.put(pair[0], handshake.getFieldValue(pair[0]) + "; " + pair[1].replaceFirst("^ +", ""));
            } else {
                handshake.put(pair[0], pair[1].replaceFirst("^ +", ""));
            }
            stringLine = readStringLine(buf);
        }
        if (line == null) {
            throw new IncompleteHandshakeException();
        }
        return handshake;
    }

    private static HandshakeBuilder translateHandshakeHttpServer(String[] firstLineTokens, String line) throws InvalidHandshakeException {
        if (!"GET".equalsIgnoreCase(firstLineTokens[0])) {
            throw new InvalidHandshakeException(String.format("Invalid request method received: %s Status line: %s", firstLineTokens[0], line));
        }
        if (!"HTTP/1.1".equalsIgnoreCase(firstLineTokens[2])) {
            throw new InvalidHandshakeException(String.format("Invalid status line received: %s Status line: %s", firstLineTokens[2], line));
        }
        ClientHandshakeBuilder clienthandshake = new HandshakeImpl1Client();
        clienthandshake.setResourceDescriptor(firstLineTokens[1]);
        return clienthandshake;
    }

    private static HandshakeBuilder translateHandshakeHttpClient(String[] firstLineTokens, String line) throws InvalidHandshakeException {
        if (!"101".equals(firstLineTokens[1])) {
            throw new InvalidHandshakeException(String.format("Invalid status code received: %s Status line: %s", firstLineTokens[1], line));
        }
        if (!"HTTP/1.1".equalsIgnoreCase(firstLineTokens[0])) {
            throw new InvalidHandshakeException(String.format("Invalid status line received: %s Status line: %s", firstLineTokens[0], line));
        }
        ServerHandshakeBuilder handshake = new HandshakeImpl1Server();
        ServerHandshakeBuilder serverhandshake = handshake;
        serverhandshake.setHttpStatus(Short.parseShort(firstLineTokens[1]));
        serverhandshake.setHttpStatusMessage(firstLineTokens[2]);
        return handshake;
    }

    protected boolean basicAccept(Handshakedata handshakedata) {
        return handshakedata.getFieldValue("Upgrade").equalsIgnoreCase("websocket") && handshakedata.getFieldValue("Connection").toLowerCase(Locale.ENGLISH).contains("upgrade");
    }

    public List<Framedata> continuousFrame(Opcode op, ByteBuffer buffer, boolean fin) {
        if (op != Opcode.BINARY && op != Opcode.TEXT) {
            throw new IllegalArgumentException("Only Opcode.BINARY or  Opcode.TEXT are allowed");
        }
        DataFrame bui = null;
        if (this.continuousFrameType != null) {
            bui = new ContinuousFrame();
        } else {
            this.continuousFrameType = op;
            if (op == Opcode.BINARY) {
                bui = new BinaryFrame();
            } else if (op == Opcode.TEXT) {
                bui = new TextFrame();
            }
        }
        bui.setPayload(buffer);
        bui.setFin(fin);
        try {
            bui.isValid();
            if (fin) {
                this.continuousFrameType = null;
            } else {
                this.continuousFrameType = op;
            }
            return Collections.singletonList(bui);
        } catch (InvalidDataException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Deprecated
    public List<ByteBuffer> createHandshake(Handshakedata handshakedata, Role ownrole) {
        return createHandshake(handshakedata);
    }

    public List<ByteBuffer> createHandshake(Handshakedata handshakedata) {
        return createHandshake(handshakedata, true);
    }

    @Deprecated
    public List<ByteBuffer> createHandshake(Handshakedata handshakedata, Role ownrole, boolean withcontent) {
        return createHandshake(handshakedata, withcontent);
    }

    public List<ByteBuffer> createHandshake(Handshakedata handshakedata, boolean withcontent) {
        StringBuilder bui = new StringBuilder(100);
        if (handshakedata instanceof ClientHandshake) {
            bui.append("GET ").append(((ClientHandshake) handshakedata).getResourceDescriptor()).append(" HTTP/1.1");
        } else if (handshakedata instanceof ServerHandshake) {
            bui.append("HTTP/1.1 101 ").append(((ServerHandshake) handshakedata).getHttpStatusMessage());
        } else {
            throw new IllegalArgumentException("unknown role");
        }
        bui.append("\r\n");
        Iterator<String> it = handshakedata.iterateHttpFields();
        while (it.hasNext()) {
            String fieldname = it.next();
            String fieldvalue = handshakedata.getFieldValue(fieldname);
            bui.append(fieldname);
            bui.append(": ");
            bui.append(fieldvalue);
            bui.append("\r\n");
        }
        bui.append("\r\n");
        byte[] httpheader = Charsetfunctions.asciiBytes(bui.toString());
        byte[] content = withcontent ? handshakedata.getContent() : null;
        ByteBuffer bytebuffer = ByteBuffer.allocate((content == null ? 0 : content.length) + httpheader.length);
        bytebuffer.put(httpheader);
        if (content != null) {
            bytebuffer.put(content);
        }
        bytebuffer.flip();
        return Collections.singletonList(bytebuffer);
    }

    public Handshakedata translateHandshake(ByteBuffer buf) throws InvalidHandshakeException {
        return translateHandshakeHttp(buf, this.role);
    }

    public int checkAlloc(int bytecount) throws InvalidDataException {
        if (bytecount < 0) {
            throw new InvalidDataException(1002, "Negative count");
        }
        return bytecount;
    }

    int readVersion(Handshakedata handshakedata) {
        String vers = handshakedata.getFieldValue("Sec-WebSocket-Version");
        if (vers.length() > 0) {
            try {
                int v = Integer.parseInt(vers.trim());
                return v;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    public void setParseMode(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return this.role;
    }

    public String toString() {
        return getClass().getSimpleName();
    }
}
