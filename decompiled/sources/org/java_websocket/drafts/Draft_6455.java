package org.java_websocket.drafts;

import com.sun.jna.platform.win32.WinNT;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.der.DerValue;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.enums.CloseHandshakeType;
import org.java_websocket.enums.HandshakeState;
import org.java_websocket.enums.Opcode;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.enums.Role;
import org.java_websocket.exceptions.IncompleteException;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidFrameException;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.exceptions.LimitExceededException;
import org.java_websocket.exceptions.NotSendableException;
import org.java_websocket.extensions.DefaultExtension;
import org.java_websocket.extensions.IExtension;
import org.java_websocket.framing.BinaryFrame;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.FramedataImpl1;
import org.java_websocket.framing.TextFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ClientHandshakeBuilder;
import org.java_websocket.handshake.HandshakeBuilder;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.protocols.IProtocol;
import org.java_websocket.protocols.Protocol;
import org.java_websocket.util.Base64;
import org.java_websocket.util.Charsetfunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/drafts/Draft_6455.class */
public class Draft_6455 extends Draft {
    private static final String SEC_WEB_SOCKET_KEY = "Sec-WebSocket-Key";
    private static final String SEC_WEB_SOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
    private static final String SEC_WEB_SOCKET_EXTENSIONS = "Sec-WebSocket-Extensions";
    private static final String SEC_WEB_SOCKET_ACCEPT = "Sec-WebSocket-Accept";
    private static final String UPGRADE = "Upgrade";
    private static final String CONNECTION = "Connection";
    private final Logger log;
    private IExtension negotiatedExtension;
    private IExtension defaultExtension;
    private List<IExtension> knownExtensions;
    private IExtension currentDecodingExtension;
    private IProtocol protocol;
    private List<IProtocol> knownProtocols;
    private Framedata currentContinuousFrame;
    private final List<ByteBuffer> byteBufferList;
    private ByteBuffer incompleteframe;
    private final SecureRandom reuseableRandom;
    private int maxFrameSize;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !Draft_6455.class.desiredAssertionStatus();
    }

    public Draft_6455() {
        this((List<IExtension>) Collections.emptyList());
    }

    public Draft_6455(IExtension inputExtension) {
        this((List<IExtension>) Collections.singletonList(inputExtension));
    }

    public Draft_6455(List<IExtension> inputExtensions) {
        this(inputExtensions, (List<IProtocol>) Collections.singletonList(new Protocol("")));
    }

    public Draft_6455(List<IExtension> inputExtensions, List<IProtocol> inputProtocols) {
        this(inputExtensions, inputProtocols, WinNT.MAXLONG);
    }

    public Draft_6455(List<IExtension> inputExtensions, int inputMaxFrameSize) {
        this(inputExtensions, Collections.singletonList(new Protocol("")), inputMaxFrameSize);
    }

    public Draft_6455(List<IExtension> inputExtensions, List<IProtocol> inputProtocols, int inputMaxFrameSize) {
        this.log = LoggerFactory.getLogger((Class<?>) Draft_6455.class);
        this.negotiatedExtension = new DefaultExtension();
        this.defaultExtension = new DefaultExtension();
        this.reuseableRandom = new SecureRandom();
        if (inputExtensions == null || inputProtocols == null || inputMaxFrameSize < 1) {
            throw new IllegalArgumentException();
        }
        this.knownExtensions = new ArrayList(inputExtensions.size());
        this.knownProtocols = new ArrayList(inputProtocols.size());
        boolean hasDefault = false;
        this.byteBufferList = new ArrayList();
        for (IExtension inputExtension : inputExtensions) {
            if (inputExtension.getClass().equals(DefaultExtension.class)) {
                hasDefault = true;
            }
        }
        this.knownExtensions.addAll(inputExtensions);
        if (!hasDefault) {
            this.knownExtensions.add(this.knownExtensions.size(), this.negotiatedExtension);
        }
        this.knownProtocols.addAll(inputProtocols);
        this.maxFrameSize = inputMaxFrameSize;
        this.currentDecodingExtension = null;
    }

    @Override // org.java_websocket.drafts.Draft
    public HandshakeState acceptHandshakeAsServer(ClientHandshake handshakedata) throws InvalidHandshakeException {
        int v = readVersion(handshakedata);
        if (v != 13) {
            this.log.trace("acceptHandshakeAsServer - Wrong websocket version.");
            return HandshakeState.NOT_MATCHED;
        }
        HandshakeState extensionState = HandshakeState.NOT_MATCHED;
        String requestedExtension = handshakedata.getFieldValue(SEC_WEB_SOCKET_EXTENSIONS);
        for (IExtension knownExtension : this.knownExtensions) {
            if (knownExtension.acceptProvidedExtensionAsServer(requestedExtension)) {
                this.negotiatedExtension = knownExtension;
                extensionState = HandshakeState.MATCHED;
                this.log.trace("acceptHandshakeAsServer - Matching extension found: {}", this.negotiatedExtension);
                break;
            }
        }
        HandshakeState protocolState = containsRequestedProtocol(handshakedata.getFieldValue(SEC_WEB_SOCKET_PROTOCOL));
        if (protocolState == HandshakeState.MATCHED && extensionState == HandshakeState.MATCHED) {
            return HandshakeState.MATCHED;
        }
        this.log.trace("acceptHandshakeAsServer - No matching extension or protocol found.");
        return HandshakeState.NOT_MATCHED;
    }

    private HandshakeState containsRequestedProtocol(String requestedProtocol) {
        for (IProtocol knownProtocol : this.knownProtocols) {
            if (knownProtocol.acceptProvidedProtocol(requestedProtocol)) {
                this.protocol = knownProtocol;
                this.log.trace("acceptHandshake - Matching protocol found: {}", this.protocol);
                return HandshakeState.MATCHED;
            }
        }
        return HandshakeState.NOT_MATCHED;
    }

    @Override // org.java_websocket.drafts.Draft
    public HandshakeState acceptHandshakeAsClient(ClientHandshake request, ServerHandshake response) throws InvalidHandshakeException {
        if (!basicAccept(response)) {
            this.log.trace("acceptHandshakeAsClient - Missing/wrong upgrade or connection in handshake.");
            return HandshakeState.NOT_MATCHED;
        }
        if (!request.hasFieldValue(SEC_WEB_SOCKET_KEY) || !response.hasFieldValue(SEC_WEB_SOCKET_ACCEPT)) {
            this.log.trace("acceptHandshakeAsClient - Missing Sec-WebSocket-Key or Sec-WebSocket-Accept");
            return HandshakeState.NOT_MATCHED;
        }
        String seckeyAnswer = response.getFieldValue(SEC_WEB_SOCKET_ACCEPT);
        String seckeyChallenge = request.getFieldValue(SEC_WEB_SOCKET_KEY);
        if (!generateFinalKey(seckeyChallenge).equals(seckeyAnswer)) {
            this.log.trace("acceptHandshakeAsClient - Wrong key for Sec-WebSocket-Key.");
            return HandshakeState.NOT_MATCHED;
        }
        HandshakeState extensionState = HandshakeState.NOT_MATCHED;
        String requestedExtension = response.getFieldValue(SEC_WEB_SOCKET_EXTENSIONS);
        for (IExtension knownExtension : this.knownExtensions) {
            if (knownExtension.acceptProvidedExtensionAsClient(requestedExtension)) {
                this.negotiatedExtension = knownExtension;
                extensionState = HandshakeState.MATCHED;
                this.log.trace("acceptHandshakeAsClient - Matching extension found: {}", this.negotiatedExtension);
                break;
            }
        }
        HandshakeState protocolState = containsRequestedProtocol(response.getFieldValue(SEC_WEB_SOCKET_PROTOCOL));
        if (protocolState == HandshakeState.MATCHED && extensionState == HandshakeState.MATCHED) {
            return HandshakeState.MATCHED;
        }
        this.log.trace("acceptHandshakeAsClient - No matching extension or protocol found.");
        return HandshakeState.NOT_MATCHED;
    }

    public IExtension getExtension() {
        return this.negotiatedExtension;
    }

    public List<IExtension> getKnownExtensions() {
        return this.knownExtensions;
    }

    public IProtocol getProtocol() {
        return this.protocol;
    }

    public int getMaxFrameSize() {
        return this.maxFrameSize;
    }

    public List<IProtocol> getKnownProtocols() {
        return this.knownProtocols;
    }

    @Override // org.java_websocket.drafts.Draft
    public ClientHandshakeBuilder postProcessHandshakeRequestAsClient(ClientHandshakeBuilder request) {
        request.put(UPGRADE, "websocket");
        request.put(CONNECTION, UPGRADE);
        byte[] random = new byte[16];
        this.reuseableRandom.nextBytes(random);
        request.put(SEC_WEB_SOCKET_KEY, Base64.encodeBytes(random));
        request.put("Sec-WebSocket-Version", "13");
        StringBuilder requestedExtensions = new StringBuilder();
        for (IExtension knownExtension : this.knownExtensions) {
            if (knownExtension.getProvidedExtensionAsClient() != null && knownExtension.getProvidedExtensionAsClient().length() != 0) {
                if (requestedExtensions.length() > 0) {
                    requestedExtensions.append(", ");
                }
                requestedExtensions.append(knownExtension.getProvidedExtensionAsClient());
            }
        }
        if (requestedExtensions.length() != 0) {
            request.put(SEC_WEB_SOCKET_EXTENSIONS, requestedExtensions.toString());
        }
        StringBuilder requestedProtocols = new StringBuilder();
        for (IProtocol knownProtocol : this.knownProtocols) {
            if (knownProtocol.getProvidedProtocol().length() != 0) {
                if (requestedProtocols.length() > 0) {
                    requestedProtocols.append(", ");
                }
                requestedProtocols.append(knownProtocol.getProvidedProtocol());
            }
        }
        if (requestedProtocols.length() != 0) {
            request.put(SEC_WEB_SOCKET_PROTOCOL, requestedProtocols.toString());
        }
        return request;
    }

    @Override // org.java_websocket.drafts.Draft
    public HandshakeBuilder postProcessHandshakeResponseAsServer(ClientHandshake request, ServerHandshakeBuilder response) throws InvalidHandshakeException {
        response.put(UPGRADE, "websocket");
        response.put(CONNECTION, request.getFieldValue(CONNECTION));
        String seckey = request.getFieldValue(SEC_WEB_SOCKET_KEY);
        if (seckey == null || "".equals(seckey)) {
            throw new InvalidHandshakeException("missing Sec-WebSocket-Key");
        }
        response.put(SEC_WEB_SOCKET_ACCEPT, generateFinalKey(seckey));
        if (getExtension().getProvidedExtensionAsServer().length() != 0) {
            response.put(SEC_WEB_SOCKET_EXTENSIONS, getExtension().getProvidedExtensionAsServer());
        }
        if (getProtocol() != null && getProtocol().getProvidedProtocol().length() != 0) {
            response.put(SEC_WEB_SOCKET_PROTOCOL, getProtocol().getProvidedProtocol());
        }
        response.setHttpStatusMessage("Web Socket Protocol Handshake");
        response.put("Server", "TooTallNate Java-WebSocket");
        response.put("Date", getServerTime());
        return response;
    }

    @Override // org.java_websocket.drafts.Draft
    public Draft copyInstance() {
        ArrayList<IExtension> newExtensions = new ArrayList<>();
        for (IExtension knownExtension : getKnownExtensions()) {
            newExtensions.add(knownExtension.copyInstance());
        }
        ArrayList<IProtocol> newProtocols = new ArrayList<>();
        for (IProtocol knownProtocol : getKnownProtocols()) {
            newProtocols.add(knownProtocol.copyInstance());
        }
        return new Draft_6455(newExtensions, newProtocols, this.maxFrameSize);
    }

    @Override // org.java_websocket.drafts.Draft
    public ByteBuffer createBinaryFrame(Framedata framedata) {
        getExtension().encodeFrame(framedata);
        if (this.log.isTraceEnabled()) {
            this.log.trace("afterEnconding({}): {}", Integer.valueOf(framedata.getPayloadData().remaining()), framedata.getPayloadData().remaining() > 1000 ? "too big to display" : new String(framedata.getPayloadData().array()));
        }
        return createByteBufferFromFramedata(framedata);
    }

    private ByteBuffer createByteBufferFromFramedata(Framedata framedata) {
        ByteBuffer mes = framedata.getPayloadData();
        boolean mask = this.role == Role.CLIENT;
        int sizebytes = getSizeBytes(mes);
        ByteBuffer buf = ByteBuffer.allocate(1 + (sizebytes > 1 ? sizebytes + 1 : sizebytes) + (mask ? 4 : 0) + mes.remaining());
        byte optcode = fromOpcode(framedata.getOpcode());
        byte one = (byte) (((byte) (framedata.isFin() ? DerValue.TAG_CONTEXT : 0)) | optcode);
        if (framedata.isRSV1()) {
            one = (byte) (one | getRSVByte(1));
        }
        if (framedata.isRSV2()) {
            one = (byte) (one | getRSVByte(2));
        }
        if (framedata.isRSV3()) {
            one = (byte) (one | getRSVByte(3));
        }
        buf.put(one);
        byte[] payloadlengthbytes = toByteArray(mes.remaining(), sizebytes);
        if (!$assertionsDisabled && payloadlengthbytes.length != sizebytes) {
            throw new AssertionError();
        }
        if (sizebytes == 1) {
            buf.put((byte) (payloadlengthbytes[0] | getMaskByte(mask)));
        } else if (sizebytes == 2) {
            buf.put((byte) (126 | getMaskByte(mask)));
            buf.put(payloadlengthbytes);
        } else if (sizebytes == 8) {
            buf.put((byte) (127 | getMaskByte(mask)));
            buf.put(payloadlengthbytes);
        } else {
            throw new IllegalStateException("Size representation not supported/specified");
        }
        if (mask) {
            ByteBuffer maskkey = ByteBuffer.allocate(4);
            maskkey.putInt(this.reuseableRandom.nextInt());
            buf.put(maskkey.array());
            int i = 0;
            while (mes.hasRemaining()) {
                buf.put((byte) (mes.get() ^ maskkey.get(i % 4)));
                i++;
            }
        } else {
            buf.put(mes);
            mes.flip();
        }
        if (!$assertionsDisabled && buf.remaining() != 0) {
            throw new AssertionError(buf.remaining());
        }
        buf.flip();
        return buf;
    }

    private Framedata translateSingleFrame(ByteBuffer buffer) throws IncompleteException, InvalidDataException {
        if (buffer == null) {
            throw new IllegalArgumentException();
        }
        int maxpacketsize = buffer.remaining();
        int realpacketsize = 2;
        translateSingleFrameCheckPacketSize(maxpacketsize, 2);
        byte b1 = buffer.get();
        boolean fin = (b1 >> 8) != 0;
        boolean rsv1 = (b1 & 64) != 0;
        boolean rsv2 = (b1 & 32) != 0;
        boolean rsv3 = (b1 & 16) != 0;
        byte b2 = buffer.get();
        boolean mask = (b2 & (-128)) != 0;
        int payloadlength = (byte) (b2 & 127);
        Opcode optcode = toOpcode((byte) (b1 & 15));
        if (payloadlength < 0 || payloadlength > 125) {
            TranslatedPayloadMetaData payloadData = translateSingleFramePayloadLength(buffer, optcode, payloadlength, maxpacketsize, 2);
            payloadlength = payloadData.getPayloadLength();
            realpacketsize = payloadData.getRealPackageSize();
        }
        translateSingleFrameCheckLengthLimit(payloadlength);
        translateSingleFrameCheckPacketSize(maxpacketsize, realpacketsize + (mask ? 4 : 0) + payloadlength);
        ByteBuffer payload = ByteBuffer.allocate(checkAlloc(payloadlength));
        if (mask) {
            byte[] maskskey = new byte[4];
            buffer.get(maskskey);
            for (int i = 0; i < payloadlength; i++) {
                payload.put((byte) (buffer.get() ^ maskskey[i % 4]));
            }
        } else {
            payload.put(buffer.array(), buffer.position(), payload.limit());
            buffer.position(buffer.position() + payload.limit());
        }
        FramedataImpl1 frame = FramedataImpl1.get(optcode);
        frame.setFin(fin);
        frame.setRSV1(rsv1);
        frame.setRSV2(rsv2);
        frame.setRSV3(rsv3);
        payload.flip();
        frame.setPayload(payload);
        if (frame.getOpcode() != Opcode.CONTINUOUS) {
            if (frame.isRSV1() || frame.isRSV2() || frame.isRSV3()) {
                this.currentDecodingExtension = getExtension();
            } else {
                this.currentDecodingExtension = this.defaultExtension;
            }
        }
        if (this.currentDecodingExtension == null) {
            this.currentDecodingExtension = this.defaultExtension;
        }
        this.currentDecodingExtension.isFrameValid(frame);
        this.currentDecodingExtension.decodeFrame(frame);
        if (this.log.isTraceEnabled()) {
            this.log.trace("afterDecoding({}): {}", Integer.valueOf(frame.getPayloadData().remaining()), frame.getPayloadData().remaining() > 1000 ? "too big to display" : new String(frame.getPayloadData().array()));
        }
        frame.isValid();
        return frame;
    }

    private TranslatedPayloadMetaData translateSingleFramePayloadLength(ByteBuffer buffer, Opcode optcode, int oldPayloadlength, int maxpacketsize, int oldRealpacketsize) throws IncompleteException, LimitExceededException, InvalidFrameException {
        int realpacketsize;
        int payloadlength;
        if (optcode == Opcode.PING || optcode == Opcode.PONG || optcode == Opcode.CLOSING) {
            this.log.trace("Invalid frame: more than 125 octets");
            throw new InvalidFrameException("more than 125 octets");
        }
        if (oldPayloadlength == 126) {
            realpacketsize = oldRealpacketsize + 2;
            translateSingleFrameCheckPacketSize(maxpacketsize, realpacketsize);
            byte[] sizebytes = {0, buffer.get(), buffer.get()};
            payloadlength = new BigInteger(sizebytes).intValue();
        } else {
            realpacketsize = oldRealpacketsize + 8;
            translateSingleFrameCheckPacketSize(maxpacketsize, realpacketsize);
            byte[] bytes = new byte[8];
            for (int i = 0; i < 8; i++) {
                bytes[i] = buffer.get();
            }
            long length = new BigInteger(bytes).longValue();
            translateSingleFrameCheckLengthLimit(length);
            payloadlength = (int) length;
        }
        return new TranslatedPayloadMetaData(payloadlength, realpacketsize);
    }

    private void translateSingleFrameCheckLengthLimit(long length) throws LimitExceededException {
        if (length > 2147483647L) {
            this.log.trace("Limit exedeed: Payloadsize is to big...");
            throw new LimitExceededException("Payloadsize is to big...");
        }
        if (length > this.maxFrameSize) {
            this.log.trace("Payload limit reached. Allowed: {} Current: {}", Integer.valueOf(this.maxFrameSize), Long.valueOf(length));
            throw new LimitExceededException("Payload limit reached.", this.maxFrameSize);
        }
        if (length < 0) {
            this.log.trace("Limit underflow: Payloadsize is to little...");
            throw new LimitExceededException("Payloadsize is to little...");
        }
    }

    private void translateSingleFrameCheckPacketSize(int maxpacketsize, int realpacketsize) throws IncompleteException {
        if (maxpacketsize < realpacketsize) {
            this.log.trace("Incomplete frame: maxpacketsize < realpacketsize");
            throw new IncompleteException(realpacketsize);
        }
    }

    private byte getRSVByte(int rsv) {
        switch (rsv) {
            case 1:
                return (byte) 64;
            case 2:
                return (byte) 32;
            case 3:
                return (byte) 16;
            default:
                return (byte) 0;
        }
    }

    private byte getMaskByte(boolean mask) {
        return mask ? (byte) -128 : (byte) 0;
    }

    private int getSizeBytes(ByteBuffer mes) {
        if (mes.remaining() <= 125) {
            return 1;
        }
        if (mes.remaining() <= 65535) {
            return 2;
        }
        return 8;
    }

    @Override // org.java_websocket.drafts.Draft
    public List<Framedata> translateFrame(ByteBuffer buffer) throws InvalidDataException {
        List<Framedata> frames;
        while (true) {
            frames = new LinkedList<>();
            if (this.incompleteframe == null) {
                break;
            }
            try {
                buffer.mark();
                int availableNextByteCount = buffer.remaining();
                int expectedNextByteCount = this.incompleteframe.remaining();
                if (expectedNextByteCount > availableNextByteCount) {
                    this.incompleteframe.put(buffer.array(), buffer.position(), availableNextByteCount);
                    buffer.position(buffer.position() + availableNextByteCount);
                    return Collections.emptyList();
                }
                this.incompleteframe.put(buffer.array(), buffer.position(), expectedNextByteCount);
                buffer.position(buffer.position() + expectedNextByteCount);
                Framedata cur = translateSingleFrame((ByteBuffer) this.incompleteframe.duplicate().position(0));
                frames.add(cur);
                this.incompleteframe = null;
                break;
            } catch (IncompleteException e) {
                ByteBuffer extendedframe = ByteBuffer.allocate(checkAlloc(e.getPreferredSize()));
                if (!$assertionsDisabled && extendedframe.limit() <= this.incompleteframe.limit()) {
                    throw new AssertionError();
                }
                this.incompleteframe.rewind();
                extendedframe.put(this.incompleteframe);
                this.incompleteframe = extendedframe;
            }
        }
        while (buffer.hasRemaining()) {
            buffer.mark();
            try {
                Framedata cur2 = translateSingleFrame(buffer);
                frames.add(cur2);
            } catch (IncompleteException e2) {
                buffer.reset();
                int pref = e2.getPreferredSize();
                this.incompleteframe = ByteBuffer.allocate(checkAlloc(pref));
                this.incompleteframe.put(buffer);
            }
        }
        return frames;
    }

    @Override // org.java_websocket.drafts.Draft
    public List<Framedata> createFrames(ByteBuffer binary, boolean mask) {
        BinaryFrame curframe = new BinaryFrame();
        curframe.setPayload(binary);
        curframe.setTransferemasked(mask);
        try {
            curframe.isValid();
            return Collections.singletonList(curframe);
        } catch (InvalidDataException e) {
            throw new NotSendableException(e);
        }
    }

    @Override // org.java_websocket.drafts.Draft
    public List<Framedata> createFrames(String text, boolean mask) {
        TextFrame curframe = new TextFrame();
        curframe.setPayload(ByteBuffer.wrap(Charsetfunctions.utf8Bytes(text)));
        curframe.setTransferemasked(mask);
        try {
            curframe.isValid();
            return Collections.singletonList(curframe);
        } catch (InvalidDataException e) {
            throw new NotSendableException(e);
        }
    }

    @Override // org.java_websocket.drafts.Draft
    public void reset() {
        this.incompleteframe = null;
        if (this.negotiatedExtension != null) {
            this.negotiatedExtension.reset();
        }
        this.negotiatedExtension = new DefaultExtension();
        this.protocol = null;
    }

    private String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    private String generateFinalKey(String in) {
        String seckey = in.trim();
        String acc = seckey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        try {
            MessageDigest sh1 = MessageDigest.getInstance(AOSignConstants.DIGEST_ALGORITHM_SHA1);
            return Base64.encodeBytes(sh1.digest(acc.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private byte[] toByteArray(long val, int bytecount) {
        byte[] buffer = new byte[bytecount];
        int highest = (8 * bytecount) - 8;
        for (int i = 0; i < bytecount; i++) {
            buffer[i] = (byte) (val >>> (highest - (8 * i)));
        }
        return buffer;
    }

    private byte fromOpcode(Opcode opcode) {
        if (opcode == Opcode.CONTINUOUS) {
            return (byte) 0;
        }
        if (opcode == Opcode.TEXT) {
            return (byte) 1;
        }
        if (opcode == Opcode.BINARY) {
            return (byte) 2;
        }
        if (opcode == Opcode.CLOSING) {
            return (byte) 8;
        }
        if (opcode == Opcode.PING) {
            return (byte) 9;
        }
        if (opcode == Opcode.PONG) {
            return (byte) 10;
        }
        throw new IllegalArgumentException("Don't know how to handle " + opcode.toString());
    }

    private Opcode toOpcode(byte opcode) throws InvalidFrameException {
        switch (opcode) {
            case 0:
                return Opcode.CONTINUOUS;
            case 1:
                return Opcode.TEXT;
            case 2:
                return Opcode.BINARY;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            default:
                throw new InvalidFrameException("Unknown opcode " + ((int) opcode));
            case 8:
                return Opcode.CLOSING;
            case 9:
                return Opcode.PING;
            case 10:
                return Opcode.PONG;
        }
    }

    @Override // org.java_websocket.drafts.Draft
    public void processFrame(WebSocketImpl webSocketImpl, Framedata frame) throws InvalidDataException {
        Opcode curop = frame.getOpcode();
        if (curop == Opcode.CLOSING) {
            processFrameClosing(webSocketImpl, frame);
            return;
        }
        if (curop == Opcode.PING) {
            webSocketImpl.getWebSocketListener().onWebsocketPing(webSocketImpl, frame);
            return;
        }
        if (curop == Opcode.PONG) {
            webSocketImpl.updateLastPong();
            webSocketImpl.getWebSocketListener().onWebsocketPong(webSocketImpl, frame);
            return;
        }
        if (!frame.isFin() || curop == Opcode.CONTINUOUS) {
            processFrameContinuousAndNonFin(webSocketImpl, frame, curop);
            return;
        }
        if (this.currentContinuousFrame != null) {
            this.log.error("Protocol error: Continuous frame sequence not completed.");
            throw new InvalidDataException(1002, "Continuous frame sequence not completed.");
        }
        if (curop == Opcode.TEXT) {
            processFrameText(webSocketImpl, frame);
        } else if (curop == Opcode.BINARY) {
            processFrameBinary(webSocketImpl, frame);
        } else {
            this.log.error("non control or continious frame expected");
            throw new InvalidDataException(1002, "non control or continious frame expected");
        }
    }

    private void processFrameContinuousAndNonFin(WebSocketImpl webSocketImpl, Framedata frame, Opcode curop) throws InvalidDataException {
        if (curop != Opcode.CONTINUOUS) {
            processFrameIsNotFin(frame);
        } else if (frame.isFin()) {
            processFrameIsFin(webSocketImpl, frame);
        } else if (this.currentContinuousFrame == null) {
            this.log.error("Protocol error: Continuous frame sequence was not started.");
            throw new InvalidDataException(1002, "Continuous frame sequence was not started.");
        }
        if (curop == Opcode.TEXT && !Charsetfunctions.isValidUTF8(frame.getPayloadData())) {
            this.log.error("Protocol error: Payload is not UTF8");
            throw new InvalidDataException(1007);
        }
        if (curop == Opcode.CONTINUOUS && this.currentContinuousFrame != null) {
            addToBufferList(frame.getPayloadData());
        }
    }

    private void processFrameBinary(WebSocketImpl webSocketImpl, Framedata frame) {
        try {
            webSocketImpl.getWebSocketListener().onWebsocketMessage(webSocketImpl, frame.getPayloadData());
        } catch (RuntimeException e) {
            logRuntimeException(webSocketImpl, e);
        }
    }

    private void logRuntimeException(WebSocketImpl webSocketImpl, RuntimeException e) {
        this.log.error("Runtime exception during onWebsocketMessage", (Throwable) e);
        webSocketImpl.getWebSocketListener().onWebsocketError(webSocketImpl, e);
    }

    private void processFrameText(WebSocketImpl webSocketImpl, Framedata frame) throws InvalidDataException {
        try {
            webSocketImpl.getWebSocketListener().onWebsocketMessage(webSocketImpl, Charsetfunctions.stringUtf8(frame.getPayloadData()));
        } catch (RuntimeException e) {
            logRuntimeException(webSocketImpl, e);
        }
    }

    private void processFrameIsFin(WebSocketImpl webSocketImpl, Framedata frame) throws InvalidDataException {
        if (this.currentContinuousFrame == null) {
            this.log.trace("Protocol error: Previous continuous frame sequence not completed.");
            throw new InvalidDataException(1002, "Continuous frame sequence was not started.");
        }
        addToBufferList(frame.getPayloadData());
        checkBufferLimit();
        if (this.currentContinuousFrame.getOpcode() == Opcode.TEXT) {
            ((FramedataImpl1) this.currentContinuousFrame).setPayload(getPayloadFromByteBufferList());
            ((FramedataImpl1) this.currentContinuousFrame).isValid();
            try {
                webSocketImpl.getWebSocketListener().onWebsocketMessage(webSocketImpl, Charsetfunctions.stringUtf8(this.currentContinuousFrame.getPayloadData()));
            } catch (RuntimeException e) {
                logRuntimeException(webSocketImpl, e);
            }
        } else if (this.currentContinuousFrame.getOpcode() == Opcode.BINARY) {
            ((FramedataImpl1) this.currentContinuousFrame).setPayload(getPayloadFromByteBufferList());
            ((FramedataImpl1) this.currentContinuousFrame).isValid();
            try {
                webSocketImpl.getWebSocketListener().onWebsocketMessage(webSocketImpl, this.currentContinuousFrame.getPayloadData());
            } catch (RuntimeException e2) {
                logRuntimeException(webSocketImpl, e2);
            }
        }
        this.currentContinuousFrame = null;
        clearBufferList();
    }

    private void processFrameIsNotFin(Framedata frame) throws InvalidDataException {
        if (this.currentContinuousFrame != null) {
            this.log.trace("Protocol error: Previous continuous frame sequence not completed.");
            throw new InvalidDataException(1002, "Previous continuous frame sequence not completed.");
        }
        this.currentContinuousFrame = frame;
        addToBufferList(frame.getPayloadData());
        checkBufferLimit();
    }

    private void processFrameClosing(WebSocketImpl webSocketImpl, Framedata frame) {
        int code = 1005;
        String reason = "";
        if (frame instanceof CloseFrame) {
            CloseFrame cf = (CloseFrame) frame;
            code = cf.getCloseCode();
            reason = cf.getMessage();
        }
        if (webSocketImpl.getReadyState() == ReadyState.CLOSING) {
            webSocketImpl.closeConnection(code, reason, true);
        } else if (getCloseHandshakeType() == CloseHandshakeType.TWOWAY) {
            webSocketImpl.close(code, reason, true);
        } else {
            webSocketImpl.flushAndClose(code, reason, false);
        }
    }

    private void clearBufferList() {
        synchronized (this.byteBufferList) {
            this.byteBufferList.clear();
        }
    }

    private void addToBufferList(ByteBuffer payloadData) {
        synchronized (this.byteBufferList) {
            this.byteBufferList.add(payloadData);
        }
    }

    private void checkBufferLimit() throws LimitExceededException {
        long totalSize = getByteBufferListSize();
        if (totalSize > this.maxFrameSize) {
            clearBufferList();
            this.log.trace("Payload limit reached. Allowed: {} Current: {}", Integer.valueOf(this.maxFrameSize), Long.valueOf(totalSize));
            throw new LimitExceededException(this.maxFrameSize);
        }
    }

    @Override // org.java_websocket.drafts.Draft
    public CloseHandshakeType getCloseHandshakeType() {
        return CloseHandshakeType.TWOWAY;
    }

    @Override // org.java_websocket.drafts.Draft
    public String toString() {
        String result = super.toString();
        if (getExtension() != null) {
            result = result + " extension: " + getExtension().toString();
        }
        if (getProtocol() != null) {
            result = result + " protocol: " + getProtocol().toString();
        }
        return result + " max frame size: " + this.maxFrameSize;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Draft_6455 that = (Draft_6455) o;
        if (this.maxFrameSize != that.getMaxFrameSize()) {
            return false;
        }
        if (this.negotiatedExtension != null) {
            if (!this.negotiatedExtension.equals(that.getExtension())) {
                return false;
            }
        } else if (that.getExtension() != null) {
            return false;
        }
        if (this.protocol != null) {
            return this.protocol.equals(that.getProtocol());
        }
        return that.getProtocol() == null;
    }

    public int hashCode() {
        int result = this.negotiatedExtension != null ? this.negotiatedExtension.hashCode() : 0;
        return (31 * ((31 * result) + (this.protocol != null ? this.protocol.hashCode() : 0))) + (this.maxFrameSize ^ (this.maxFrameSize >>> 32));
    }

    private ByteBuffer getPayloadFromByteBufferList() throws LimitExceededException {
        ByteBuffer resultingByteBuffer;
        long totalSize = 0;
        synchronized (this.byteBufferList) {
            for (ByteBuffer buffer : this.byteBufferList) {
                totalSize += (long) buffer.limit();
            }
            checkBufferLimit();
            resultingByteBuffer = ByteBuffer.allocate((int) totalSize);
            for (ByteBuffer buffer2 : this.byteBufferList) {
                resultingByteBuffer.put(buffer2);
            }
        }
        resultingByteBuffer.flip();
        return resultingByteBuffer;
    }

    private long getByteBufferListSize() {
        long totalSize = 0;
        synchronized (this.byteBufferList) {
            for (ByteBuffer buffer : this.byteBufferList) {
                totalSize += (long) buffer.limit();
            }
        }
        return totalSize;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/java_websocket/drafts/Draft_6455$TranslatedPayloadMetaData.class */
    private class TranslatedPayloadMetaData {
        private int payloadLength;
        private int realPackageSize;

        /* JADX INFO: Access modifiers changed from: private */
        public int getPayloadLength() {
            return this.payloadLength;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getRealPackageSize() {
            return this.realPackageSize;
        }

        TranslatedPayloadMetaData(int newPayloadLength, int newRealPackageSize) {
            this.payloadLength = newPayloadLength;
            this.realPackageSize = newRealPackageSize;
        }
    }
}
