package org.spongycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.spongycastle.util.io.SimpleOutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/RecordStream.class */
class RecordStream {
    private static int DEFAULT_PLAINTEXT_LIMIT = 16384;
    static final int TLS_HEADER_SIZE = 5;
    static final int TLS_HEADER_TYPE_OFFSET = 0;
    static final int TLS_HEADER_VERSION_OFFSET = 1;
    static final int TLS_HEADER_LENGTH_OFFSET = 3;
    private TlsProtocol handler;
    private InputStream input;
    private OutputStream output;
    private TlsCompression readCompression;
    private TlsCompression writeCompression;
    private int plaintextLimit;
    private int compressedLimit;
    private int ciphertextLimit;
    private TlsCompression pendingCompression = null;
    private TlsCipher pendingCipher = null;
    private TlsCipher readCipher = null;
    private TlsCipher writeCipher = null;
    private SequenceNumber readSeqNo = new SequenceNumber();
    private SequenceNumber writeSeqNo = new SequenceNumber();
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private TlsHandshakeHash handshakeHash = null;
    private SimpleOutputStream handshakeHashUpdater = new SimpleOutputStream() { // from class: org.spongycastle.crypto.tls.RecordStream.1
        @Override // java.io.OutputStream
        public void write(byte[] buf, int off, int len) throws IOException {
            RecordStream.this.handshakeHash.update(buf, off, len);
        }
    };
    private ProtocolVersion readVersion = null;
    private ProtocolVersion writeVersion = null;
    private boolean restrictReadVersion = true;

    RecordStream(TlsProtocol handler, InputStream input, OutputStream output) {
        this.readCompression = null;
        this.writeCompression = null;
        this.handler = handler;
        this.input = input;
        this.output = output;
        this.readCompression = new TlsNullCompression();
        this.writeCompression = this.readCompression;
    }

    void init(TlsContext context) {
        this.readCipher = new TlsNullCipher(context);
        this.writeCipher = this.readCipher;
        this.handshakeHash = new DeferredHash();
        this.handshakeHash.init(context);
        setPlaintextLimit(DEFAULT_PLAINTEXT_LIMIT);
    }

    int getPlaintextLimit() {
        return this.plaintextLimit;
    }

    void setPlaintextLimit(int plaintextLimit) {
        this.plaintextLimit = plaintextLimit;
        this.compressedLimit = this.plaintextLimit + 1024;
        this.ciphertextLimit = this.compressedLimit + 1024;
    }

    ProtocolVersion getReadVersion() {
        return this.readVersion;
    }

    void setReadVersion(ProtocolVersion readVersion) {
        this.readVersion = readVersion;
    }

    void setWriteVersion(ProtocolVersion writeVersion) {
        this.writeVersion = writeVersion;
    }

    void setRestrictReadVersion(boolean enabled) {
        this.restrictReadVersion = enabled;
    }

    void setPendingConnectionState(TlsCompression tlsCompression, TlsCipher tlsCipher) {
        this.pendingCompression = tlsCompression;
        this.pendingCipher = tlsCipher;
    }

    void sentWriteCipherSpec() throws IOException {
        if (this.pendingCompression == null || this.pendingCipher == null) {
            throw new TlsFatalAlert((short) 40);
        }
        this.writeCompression = this.pendingCompression;
        this.writeCipher = this.pendingCipher;
        this.writeSeqNo = new SequenceNumber();
    }

    void receivedReadCipherSpec() throws IOException {
        if (this.pendingCompression == null || this.pendingCipher == null) {
            throw new TlsFatalAlert((short) 40);
        }
        this.readCompression = this.pendingCompression;
        this.readCipher = this.pendingCipher;
        this.readSeqNo = new SequenceNumber();
    }

    void finaliseHandshake() throws IOException {
        if (this.readCompression != this.pendingCompression || this.writeCompression != this.pendingCompression || this.readCipher != this.pendingCipher || this.writeCipher != this.pendingCipher) {
            throw new TlsFatalAlert((short) 40);
        }
        this.pendingCompression = null;
        this.pendingCipher = null;
    }

    void checkRecordHeader(byte[] recordHeader) throws IOException {
        short type = TlsUtils.readUint8(recordHeader, 0);
        checkType(type, (short) 10);
        if (!this.restrictReadVersion) {
            int version = TlsUtils.readVersionRaw(recordHeader, 1);
            if ((version & (-256)) != 768) {
                throw new TlsFatalAlert((short) 47);
            }
        } else {
            ProtocolVersion version2 = TlsUtils.readVersion(recordHeader, 1);
            if (this.readVersion != null && !version2.equals(this.readVersion)) {
                throw new TlsFatalAlert((short) 47);
            }
        }
        int length = TlsUtils.readUint16(recordHeader, 3);
        checkLength(length, this.ciphertextLimit, (short) 22);
    }

    boolean readRecord() throws IOException {
        byte[] recordHeader = TlsUtils.readAllOrNothing(5, this.input);
        if (recordHeader == null) {
            return false;
        }
        short type = TlsUtils.readUint8(recordHeader, 0);
        checkType(type, (short) 10);
        if (!this.restrictReadVersion) {
            if ((TlsUtils.readVersionRaw(recordHeader, 1) & (-256)) != 768) {
                throw new TlsFatalAlert((short) 47);
            }
        } else {
            ProtocolVersion version = TlsUtils.readVersion(recordHeader, 1);
            if (this.readVersion == null) {
                this.readVersion = version;
            } else if (!version.equals(this.readVersion)) {
                throw new TlsFatalAlert((short) 47);
            }
        }
        int length = TlsUtils.readUint16(recordHeader, 3);
        checkLength(length, this.ciphertextLimit, (short) 22);
        byte[] plaintext = decodeAndVerify(type, this.input, length);
        this.handler.processRecord(type, plaintext, 0, plaintext.length);
        return true;
    }

    byte[] decodeAndVerify(short type, InputStream input, int len) throws IOException {
        byte[] buf = TlsUtils.readFully(len, input);
        long seqNo = this.readSeqNo.nextValue((short) 10);
        byte[] decoded = this.readCipher.decodeCiphertext(seqNo, type, buf, 0, buf.length);
        checkLength(decoded.length, this.compressedLimit, (short) 22);
        OutputStream cOut = this.readCompression.decompress(this.buffer);
        if (cOut != this.buffer) {
            cOut.write(decoded, 0, decoded.length);
            cOut.flush();
            decoded = getBufferContents();
        }
        checkLength(decoded.length, this.plaintextLimit, (short) 30);
        if (decoded.length < 1 && type != 23) {
            throw new TlsFatalAlert((short) 47);
        }
        return decoded;
    }

    void writeRecord(short type, byte[] plaintext, int plaintextOffset, int plaintextLength) throws IOException {
        byte[] ciphertext;
        if (this.writeVersion == null) {
            return;
        }
        checkType(type, (short) 80);
        checkLength(plaintextLength, this.plaintextLimit, (short) 80);
        if (plaintextLength < 1 && type != 23) {
            throw new TlsFatalAlert((short) 80);
        }
        OutputStream cOut = this.writeCompression.compress(this.buffer);
        long seqNo = this.writeSeqNo.nextValue((short) 80);
        if (cOut == this.buffer) {
            ciphertext = this.writeCipher.encodePlaintext(seqNo, type, plaintext, plaintextOffset, plaintextLength);
        } else {
            cOut.write(plaintext, plaintextOffset, plaintextLength);
            cOut.flush();
            byte[] compressed = getBufferContents();
            checkLength(compressed.length, plaintextLength + 1024, (short) 80);
            ciphertext = this.writeCipher.encodePlaintext(seqNo, type, compressed, 0, compressed.length);
        }
        checkLength(ciphertext.length, this.ciphertextLimit, (short) 80);
        byte[] record = new byte[ciphertext.length + 5];
        TlsUtils.writeUint8(type, record, 0);
        TlsUtils.writeVersion(this.writeVersion, record, 1);
        TlsUtils.writeUint16(ciphertext.length, record, 3);
        System.arraycopy(ciphertext, 0, record, 5, ciphertext.length);
        this.output.write(record);
        this.output.flush();
    }

    void notifyHelloComplete() {
        this.handshakeHash = this.handshakeHash.notifyPRFDetermined();
    }

    TlsHandshakeHash getHandshakeHash() {
        return this.handshakeHash;
    }

    OutputStream getHandshakeHashUpdater() {
        return this.handshakeHashUpdater;
    }

    TlsHandshakeHash prepareToFinish() {
        TlsHandshakeHash result = this.handshakeHash;
        this.handshakeHash = this.handshakeHash.stopTracking();
        return result;
    }

    void safeClose() {
        try {
            this.input.close();
        } catch (IOException e) {
        }
        try {
            this.output.close();
        } catch (IOException e2) {
        }
    }

    void flush() throws IOException {
        this.output.flush();
    }

    private byte[] getBufferContents() {
        byte[] contents = this.buffer.toByteArray();
        this.buffer.reset();
        return contents;
    }

    private static void checkType(short type, short alertDescription) throws IOException {
        switch (type) {
            case 20:
            case 21:
            case 22:
            case 23:
                return;
            default:
                throw new TlsFatalAlert(alertDescription);
        }
    }

    private static void checkLength(int length, int limit, short alertDescription) throws IOException {
        if (length > limit) {
            throw new TlsFatalAlert(alertDescription);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/RecordStream$SequenceNumber.class */
    private static class SequenceNumber {
        private long value;
        private boolean exhausted;

        private SequenceNumber() {
            this.value = 0L;
            this.exhausted = false;
        }

        synchronized long nextValue(short alertDescription) throws TlsFatalAlert {
            if (this.exhausted) {
                throw new TlsFatalAlert(alertDescription);
            }
            long result = this.value;
            long j = this.value + 1;
            this.value = j;
            if (j == 0) {
                this.exhausted = true;
            }
            return result;
        }
    }
}
