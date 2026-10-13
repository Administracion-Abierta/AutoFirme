package org.spongycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.spongycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/DTLSReliableHandshake.class */
class DTLSReliableHandshake {
    private static final int MAX_RECEIVE_AHEAD = 16;
    private static final int MESSAGE_HEADER_LENGTH = 12;
    private DTLSRecordLayer recordLayer;
    private Hashtable currentInboundFlight = new Hashtable();
    private Hashtable previousInboundFlight = null;
    private Vector outboundFlight = new Vector();
    private boolean sending = true;
    private int message_seq = 0;
    private int next_receive_seq = 0;
    private TlsHandshakeHash handshakeHash = new DeferredHash();

    DTLSReliableHandshake(TlsContext context, DTLSRecordLayer transport) {
        this.recordLayer = transport;
        this.handshakeHash.init(context);
    }

    void notifyHelloComplete() {
        this.handshakeHash = this.handshakeHash.notifyPRFDetermined();
    }

    TlsHandshakeHash getHandshakeHash() {
        return this.handshakeHash;
    }

    TlsHandshakeHash prepareToFinish() {
        TlsHandshakeHash result = this.handshakeHash;
        this.handshakeHash = this.handshakeHash.stopTracking();
        return result;
    }

    void sendMessage(short msg_type, byte[] body) throws IOException {
        TlsUtils.checkUint24(body.length);
        if (!this.sending) {
            checkInboundFlight();
            this.sending = true;
            this.outboundFlight.removeAllElements();
        }
        int i = this.message_seq;
        this.message_seq = i + 1;
        Message message = new Message(i, msg_type, body);
        this.outboundFlight.addElement(message);
        writeMessage(message);
        updateHandshakeMessagesDigest(message);
    }

    byte[] receiveMessageBody(short msg_type) throws IOException {
        Message message = receiveMessage();
        if (message.getType() != msg_type) {
            throw new TlsFatalAlert((short) 10);
        }
        return message.getBody();
    }

    Message receiveMessage() throws IOException {
        if (this.sending) {
            this.sending = false;
            prepareInboundFlight(new Hashtable());
        }
        byte[] buf = null;
        int readTimeoutMillis = 1000;
        while (true) {
            try {
                Message pending = getPendingMessage();
                if (pending != null) {
                    return pending;
                }
                int receiveLimit = this.recordLayer.getReceiveLimit();
                if (buf == null || buf.length < receiveLimit) {
                    buf = new byte[receiveLimit];
                }
                int received = this.recordLayer.receive(buf, 0, receiveLimit, readTimeoutMillis);
                if (received >= 0) {
                    boolean resentOutbound = processRecord(16, this.recordLayer.getReadEpoch(), buf, 0, received);
                    if (resentOutbound) {
                        readTimeoutMillis = backOff(readTimeoutMillis);
                    }
                } else {
                    resendOutboundFlight();
                    readTimeoutMillis = backOff(readTimeoutMillis);
                }
            } catch (IOException e) {
            }
        }
    }

    void finish() {
        DTLSHandshakeRetransmit retransmit = null;
        if (!this.sending) {
            checkInboundFlight();
        } else {
            prepareInboundFlight(null);
            if (this.previousInboundFlight != null) {
                retransmit = new DTLSHandshakeRetransmit() { // from class: org.spongycastle.crypto.tls.DTLSReliableHandshake.1
                    @Override // org.spongycastle.crypto.tls.DTLSHandshakeRetransmit
                    public void receivedHandshakeRecord(int epoch, byte[] buf, int off, int len) throws IOException {
                        DTLSReliableHandshake.this.processRecord(0, epoch, buf, off, len);
                    }
                };
            }
        }
        this.recordLayer.handshakeSuccessful(retransmit);
    }

    void resetHandshakeMessagesDigest() {
        this.handshakeHash.reset();
    }

    private int backOff(int timeoutMillis) {
        return Math.min(timeoutMillis * 2, 60000);
    }

    /* JADX WARN: Code duplicated, block: B:5:0x0011  */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    private void checkInboundFlight() {
        /*
            r3 = this;
            r0 = r3
            java.util.Hashtable r0 = r0.currentInboundFlight
            java.util.Enumeration r0 = r0.keys()
            r4 = r0
        L8:
            r0 = r4
            boolean r0 = r0.hasMoreElements()
            if (r0 == 0) goto L29
            r0 = r4
            java.lang.Object r0 = r0.nextElement()
            java.lang.Integer r0 = (java.lang.Integer) r0
            r5 = r0
            r0 = r5
            int r0 = r0.intValue()
            r1 = r3
            int r1 = r1.next_receive_seq
            if (r0 < r1) goto L26
        L26:
            goto L8
        L29:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.spongycastle.crypto.tls.DTLSReliableHandshake.checkInboundFlight():void");
    }

    private Message getPendingMessage() throws IOException {
        byte[] body;
        DTLSReassembler next = (DTLSReassembler) this.currentInboundFlight.get(Integers.valueOf(this.next_receive_seq));
        if (next != null && (body = next.getBodyIfComplete()) != null) {
            this.previousInboundFlight = null;
            int i = this.next_receive_seq;
            this.next_receive_seq = i + 1;
            return updateHandshakeMessagesDigest(new Message(i, next.getMsgType(), body));
        }
        return null;
    }

    private void prepareInboundFlight(Hashtable nextFlight) {
        resetAll(this.currentInboundFlight);
        this.previousInboundFlight = this.currentInboundFlight;
        this.currentInboundFlight = nextFlight;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean processRecord(int windowSize, int epoch, byte[] buf, int off, int len) throws IOException {
        int fragment_length;
        int message_length;
        DTLSReassembler reassembler;
        boolean checkPreviousFlight = false;
        while (len >= 12 && len >= (message_length = (fragment_length = TlsUtils.readUint24(buf, off + 9)) + 12)) {
            int length = TlsUtils.readUint24(buf, off + 1);
            int fragment_offset = TlsUtils.readUint24(buf, off + 6);
            if (fragment_offset + fragment_length > length) {
                break;
            }
            short msg_type = TlsUtils.readUint8(buf, off + 0);
            int expectedEpoch = msg_type == 20 ? 1 : 0;
            if (epoch != expectedEpoch) {
                break;
            }
            int message_seq = TlsUtils.readUint16(buf, off + 4);
            if (message_seq < this.next_receive_seq + windowSize) {
                if (message_seq >= this.next_receive_seq) {
                    DTLSReassembler reassembler2 = (DTLSReassembler) this.currentInboundFlight.get(Integers.valueOf(message_seq));
                    if (reassembler2 == null) {
                        reassembler2 = new DTLSReassembler(msg_type, length);
                        this.currentInboundFlight.put(Integers.valueOf(message_seq), reassembler2);
                    }
                    reassembler2.contributeFragment(msg_type, length, buf, off + 12, fragment_offset, fragment_length);
                } else if (this.previousInboundFlight != null && (reassembler = (DTLSReassembler) this.previousInboundFlight.get(Integers.valueOf(message_seq))) != null) {
                    reassembler.contributeFragment(msg_type, length, buf, off + 12, fragment_offset, fragment_length);
                    checkPreviousFlight = true;
                }
            }
            off += message_length;
            len -= message_length;
        }
        boolean result = checkPreviousFlight && checkAll(this.previousInboundFlight);
        if (result) {
            resendOutboundFlight();
            resetAll(this.previousInboundFlight);
        }
        return result;
    }

    private void resendOutboundFlight() throws IOException {
        this.recordLayer.resetWriteEpoch();
        for (int i = 0; i < this.outboundFlight.size(); i++) {
            writeMessage((Message) this.outboundFlight.elementAt(i));
        }
    }

    private Message updateHandshakeMessagesDigest(Message message) throws IOException {
        if (message.getType() != 0) {
            byte[] body = message.getBody();
            byte[] buf = new byte[12];
            TlsUtils.writeUint8(message.getType(), buf, 0);
            TlsUtils.writeUint24(body.length, buf, 1);
            TlsUtils.writeUint16(message.getSeq(), buf, 4);
            TlsUtils.writeUint24(0, buf, 6);
            TlsUtils.writeUint24(body.length, buf, 9);
            this.handshakeHash.update(buf, 0, buf.length);
            this.handshakeHash.update(body, 0, body.length);
        }
        return message;
    }

    private void writeMessage(Message message) throws IOException {
        int sendLimit = this.recordLayer.getSendLimit();
        int fragmentLimit = sendLimit - 12;
        if (fragmentLimit < 1) {
            throw new TlsFatalAlert((short) 80);
        }
        int length = message.getBody().length;
        int fragment_offset = 0;
        do {
            int fragment_length = Math.min(length - fragment_offset, fragmentLimit);
            writeHandshakeFragment(message, fragment_offset, fragment_length);
            fragment_offset += fragment_length;
        } while (fragment_offset < length);
    }

    private void writeHandshakeFragment(Message message, int fragment_offset, int fragment_length) throws IOException {
        RecordLayerBuffer fragment = new RecordLayerBuffer(12 + fragment_length);
        TlsUtils.writeUint8(message.getType(), (OutputStream) fragment);
        TlsUtils.writeUint24(message.getBody().length, fragment);
        TlsUtils.writeUint16(message.getSeq(), fragment);
        TlsUtils.writeUint24(fragment_offset, fragment);
        TlsUtils.writeUint24(fragment_length, fragment);
        fragment.write(message.getBody(), fragment_offset, fragment_length);
        fragment.sendToRecordLayer(this.recordLayer);
    }

    private static boolean checkAll(Hashtable inboundFlight) {
        Enumeration e = inboundFlight.elements();
        while (e.hasMoreElements()) {
            if (((DTLSReassembler) e.nextElement()).getBodyIfComplete() == null) {
                return false;
            }
        }
        return true;
    }

    private static void resetAll(Hashtable inboundFlight) {
        Enumeration e = inboundFlight.elements();
        while (e.hasMoreElements()) {
            ((DTLSReassembler) e.nextElement()).reset();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/DTLSReliableHandshake$Message.class */
    static class Message {
        private final int message_seq;
        private final short msg_type;
        private final byte[] body;

        private Message(int message_seq, short msg_type, byte[] body) {
            this.message_seq = message_seq;
            this.msg_type = msg_type;
            this.body = body;
        }

        public int getSeq() {
            return this.message_seq;
        }

        public short getType() {
            return this.msg_type;
        }

        public byte[] getBody() {
            return this.body;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/DTLSReliableHandshake$RecordLayerBuffer.class */
    static class RecordLayerBuffer extends ByteArrayOutputStream {
        RecordLayerBuffer(int size) {
            super(size);
        }

        void sendToRecordLayer(DTLSRecordLayer recordLayer) throws IOException {
            recordLayer.send(this.buf, 0, this.count);
            this.buf = null;
        }
    }
}
