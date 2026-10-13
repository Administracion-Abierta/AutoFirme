package org.spongycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.prng.RandomGenerator;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Integers;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsProtocol.class */
public abstract class TlsProtocol {
    protected static final Integer EXT_RenegotiationInfo = Integers.valueOf(65281);
    protected static final Integer EXT_SessionTicket = Integers.valueOf(35);
    protected static final short CS_START = 0;
    protected static final short CS_CLIENT_HELLO = 1;
    protected static final short CS_SERVER_HELLO = 2;
    protected static final short CS_SERVER_SUPPLEMENTAL_DATA = 3;
    protected static final short CS_SERVER_CERTIFICATE = 4;
    protected static final short CS_CERTIFICATE_STATUS = 5;
    protected static final short CS_SERVER_KEY_EXCHANGE = 6;
    protected static final short CS_CERTIFICATE_REQUEST = 7;
    protected static final short CS_SERVER_HELLO_DONE = 8;
    protected static final short CS_CLIENT_SUPPLEMENTAL_DATA = 9;
    protected static final short CS_CLIENT_CERTIFICATE = 10;
    protected static final short CS_CLIENT_KEY_EXCHANGE = 11;
    protected static final short CS_CERTIFICATE_VERIFY = 12;
    protected static final short CS_CLIENT_FINISHED = 13;
    protected static final short CS_SERVER_SESSION_TICKET = 14;
    protected static final short CS_SERVER_FINISHED = 15;
    protected static final short CS_END = 16;
    protected static final short ADS_MODE_1_Nsub1 = 0;
    protected static final short ADS_MODE_0_N = 1;
    protected static final short ADS_MODE_0_N_FIRSTONLY = 2;
    private ByteQueue applicationDataQueue;
    private ByteQueue alertQueue;
    private ByteQueue handshakeQueue;
    RecordStream recordStream;
    protected SecureRandom secureRandom;
    private TlsInputStream tlsInputStream;
    private TlsOutputStream tlsOutputStream;
    private volatile boolean closed;
    private volatile boolean failedWithError;
    private volatile boolean appDataReady;
    private volatile boolean appDataSplitEnabled;
    private volatile int appDataSplitMode;
    private byte[] expected_verify_data;
    protected TlsSession tlsSession;
    protected SessionParameters sessionParameters;
    protected SecurityParameters securityParameters;
    protected Certificate peerCertificate;
    protected int[] offeredCipherSuites;
    protected short[] offeredCompressionMethods;
    protected Hashtable clientExtensions;
    protected Hashtable serverExtensions;
    protected short connection_state;
    protected boolean resumedSession;
    protected boolean receivedChangeCipherSpec;
    protected boolean secure_renegotiation;
    protected boolean allowCertificateStatus;
    protected boolean expectSessionTicket;
    protected boolean blocking;
    protected ByteQueueInputStream inputBuffers;
    protected ByteQueueOutputStream outputBuffer;

    protected abstract TlsContext getContext();

    abstract AbstractTlsContext getContextAdmin();

    protected abstract TlsPeer getPeer();

    protected abstract void handleHandshakeMessage(short s, ByteArrayInputStream byteArrayInputStream) throws IOException;

    public TlsProtocol(InputStream input, OutputStream output, SecureRandom secureRandom) {
        this.applicationDataQueue = new ByteQueue(0);
        this.alertQueue = new ByteQueue(2);
        this.handshakeQueue = new ByteQueue(0);
        this.tlsInputStream = null;
        this.tlsOutputStream = null;
        this.closed = false;
        this.failedWithError = false;
        this.appDataReady = false;
        this.appDataSplitEnabled = true;
        this.appDataSplitMode = 0;
        this.expected_verify_data = null;
        this.tlsSession = null;
        this.sessionParameters = null;
        this.securityParameters = null;
        this.peerCertificate = null;
        this.offeredCipherSuites = null;
        this.offeredCompressionMethods = null;
        this.clientExtensions = null;
        this.serverExtensions = null;
        this.connection_state = (short) 0;
        this.resumedSession = false;
        this.receivedChangeCipherSpec = false;
        this.secure_renegotiation = false;
        this.allowCertificateStatus = false;
        this.expectSessionTicket = false;
        this.blocking = true;
        this.recordStream = new RecordStream(this, input, output);
        this.secureRandom = secureRandom;
    }

    public TlsProtocol(SecureRandom secureRandom) {
        this.applicationDataQueue = new ByteQueue(0);
        this.alertQueue = new ByteQueue(2);
        this.handshakeQueue = new ByteQueue(0);
        this.tlsInputStream = null;
        this.tlsOutputStream = null;
        this.closed = false;
        this.failedWithError = false;
        this.appDataReady = false;
        this.appDataSplitEnabled = true;
        this.appDataSplitMode = 0;
        this.expected_verify_data = null;
        this.tlsSession = null;
        this.sessionParameters = null;
        this.securityParameters = null;
        this.peerCertificate = null;
        this.offeredCipherSuites = null;
        this.offeredCompressionMethods = null;
        this.clientExtensions = null;
        this.serverExtensions = null;
        this.connection_state = (short) 0;
        this.resumedSession = false;
        this.receivedChangeCipherSpec = false;
        this.secure_renegotiation = false;
        this.allowCertificateStatus = false;
        this.expectSessionTicket = false;
        this.blocking = false;
        this.inputBuffers = new ByteQueueInputStream();
        this.outputBuffer = new ByteQueueOutputStream();
        this.recordStream = new RecordStream(this, this.inputBuffers, this.outputBuffer);
        this.secureRandom = secureRandom;
    }

    protected void handleAlertMessage(short alertLevel, short alertDescription) throws IOException {
        getPeer().notifyAlertReceived(alertLevel, alertDescription);
        if (alertLevel == 1) {
            handleAlertWarningMessage(alertDescription);
        } else {
            handleFailure();
            throw new TlsFatalAlertReceived(alertDescription);
        }
    }

    protected void handleAlertWarningMessage(short alertDescription) throws IOException {
        if (alertDescription == 0) {
            if (!this.appDataReady) {
                throw new TlsFatalAlert((short) 40);
            }
            handleClose(false);
        }
    }

    protected void handleChangeCipherSpecMessage() throws IOException {
    }

    protected void handleClose(boolean user_canceled) throws IOException {
        if (!this.closed) {
            this.closed = true;
            if (user_canceled && !this.appDataReady) {
                raiseAlertWarning((short) 90, "User canceled handshake");
            }
            raiseAlertWarning((short) 0, "Connection closed");
            this.recordStream.safeClose();
            if (!this.appDataReady) {
                cleanupHandshake();
            }
        }
    }

    protected void handleException(short alertDescription, String message, Throwable cause) throws IOException {
        if (!this.closed) {
            raiseAlertFatal(alertDescription, message, cause);
            handleFailure();
        }
    }

    protected void handleFailure() {
        this.closed = true;
        this.failedWithError = true;
        invalidateSession();
        this.recordStream.safeClose();
        if (!this.appDataReady) {
            cleanupHandshake();
        }
    }

    protected void applyMaxFragmentLengthExtension() throws IOException {
        if (this.securityParameters.maxFragmentLength >= 0) {
            if (!MaxFragmentLength.isValid(this.securityParameters.maxFragmentLength)) {
                throw new TlsFatalAlert((short) 80);
            }
            int plainTextLimit = 1 << (8 + this.securityParameters.maxFragmentLength);
            this.recordStream.setPlaintextLimit(plainTextLimit);
        }
    }

    protected void checkReceivedChangeCipherSpec(boolean expected) throws IOException {
        if (expected != this.receivedChangeCipherSpec) {
            throw new TlsFatalAlert((short) 10);
        }
    }

    protected void cleanupHandshake() {
        if (this.expected_verify_data != null) {
            Arrays.fill(this.expected_verify_data, (byte) 0);
            this.expected_verify_data = null;
        }
        this.securityParameters.clear();
        this.peerCertificate = null;
        this.offeredCipherSuites = null;
        this.offeredCompressionMethods = null;
        this.clientExtensions = null;
        this.serverExtensions = null;
        this.resumedSession = false;
        this.receivedChangeCipherSpec = false;
        this.secure_renegotiation = false;
        this.allowCertificateStatus = false;
        this.expectSessionTicket = false;
    }

    protected void blockForHandshake() throws IOException {
        if (this.blocking) {
            while (this.connection_state != 16) {
                if (this.closed) {
                    throw new TlsFatalAlert((short) 80);
                }
                safeReadRecord();
            }
        }
    }

    protected void completeHandshake() throws IOException {
        try {
            this.connection_state = (short) 16;
            this.alertQueue.shrink();
            this.handshakeQueue.shrink();
            this.recordStream.finaliseHandshake();
            this.appDataSplitEnabled = !TlsUtils.isTLSv11(getContext());
            if (!this.appDataReady) {
                this.appDataReady = true;
                if (this.blocking) {
                    this.tlsInputStream = new TlsInputStream(this);
                    this.tlsOutputStream = new TlsOutputStream(this);
                }
            }
            if (this.tlsSession != null) {
                if (this.sessionParameters == null) {
                    this.sessionParameters = new SessionParameters.Builder().setCipherSuite(this.securityParameters.getCipherSuite()).setCompressionAlgorithm(this.securityParameters.getCompressionAlgorithm()).setMasterSecret(this.securityParameters.getMasterSecret()).setPeerCertificate(this.peerCertificate).setPSKIdentity(this.securityParameters.getPSKIdentity()).setSRPIdentity(this.securityParameters.getSRPIdentity()).setServerExtensions(this.serverExtensions).build();
                    this.tlsSession = new TlsSessionImpl(this.tlsSession.getSessionID(), this.sessionParameters);
                }
                getContextAdmin().setResumableSession(this.tlsSession);
            }
            getPeer().notifyHandshakeComplete();
        } finally {
            cleanupHandshake();
        }
    }

    protected void processRecord(short protocol, byte[] buf, int off, int len) throws IOException {
        switch (protocol) {
            case 20:
                processChangeCipherSpec(buf, off, len);
                return;
            case 21:
                this.alertQueue.addData(buf, off, len);
                processAlertQueue();
                return;
            case 22:
                if (this.handshakeQueue.available() > 0) {
                    this.handshakeQueue.addData(buf, off, len);
                    processHandshakeQueue(this.handshakeQueue);
                    return;
                }
                ByteQueue tmpQueue = new ByteQueue(buf, off, len);
                processHandshakeQueue(tmpQueue);
                int remaining = tmpQueue.available();
                if (remaining > 0) {
                    this.handshakeQueue.addData(buf, (off + len) - remaining, remaining);
                    return;
                }
                return;
            case 23:
                if (!this.appDataReady) {
                    throw new TlsFatalAlert((short) 10);
                }
                this.applicationDataQueue.addData(buf, off, len);
                processApplicationDataQueue();
                return;
            default:
                throw new TlsFatalAlert((short) 80);
        }
    }

    private void processHandshakeQueue(ByteQueue queue) throws IOException {
        while (queue.available() >= 4) {
            byte[] beginning = new byte[4];
            queue.read(beginning, 0, 4, 0);
            short type = TlsUtils.readUint8(beginning, 0);
            int length = TlsUtils.readUint24(beginning, 1);
            int totalLength = 4 + length;
            if (queue.available() >= totalLength) {
                checkReceivedChangeCipherSpec(this.connection_state == 16 || type == 20);
                switch (type) {
                    case 0:
                        continue;
                        queue.removeData(4);
                        ByteArrayInputStream buf = queue.readFrom(length);
                        handleHandshakeMessage(type, buf);
                        break;
                    case 20:
                        TlsContext ctx = getContext();
                        if (this.expected_verify_data == null && ctx.getSecurityParameters().getMasterSecret() != null) {
                            this.expected_verify_data = createVerifyData(!ctx.isServer());
                        }
                        break;
                }
                queue.copyTo(this.recordStream.getHandshakeHashUpdater(), totalLength);
                queue.removeData(4);
                ByteArrayInputStream buf2 = queue.readFrom(length);
                handleHandshakeMessage(type, buf2);
            } else {
                return;
            }
        }
    }

    private void processApplicationDataQueue() {
    }

    private void processAlertQueue() throws IOException {
        while (this.alertQueue.available() >= 2) {
            byte[] alert = this.alertQueue.removeData(2, 0);
            short alertLevel = alert[0];
            short alertDescription = alert[1];
            handleAlertMessage(alertLevel, alertDescription);
        }
    }

    private void processChangeCipherSpec(byte[] buf, int off, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            short message = TlsUtils.readUint8(buf, off + i);
            if (message != 1) {
                throw new TlsFatalAlert((short) 50);
            }
            if (this.receivedChangeCipherSpec || this.alertQueue.available() > 0 || this.handshakeQueue.available() > 0) {
                throw new TlsFatalAlert((short) 10);
            }
            this.recordStream.receivedReadCipherSpec();
            this.receivedChangeCipherSpec = true;
            handleChangeCipherSpecMessage();
        }
    }

    protected int applicationDataAvailable() {
        return this.applicationDataQueue.available();
    }

    protected int readApplicationData(byte[] buf, int offset, int len) throws IOException {
        if (len < 1) {
            return 0;
        }
        while (this.applicationDataQueue.available() == 0) {
            if (this.closed) {
                if (this.failedWithError) {
                    throw new IOException("Cannot read application data on failed TLS connection");
                }
                if (!this.appDataReady) {
                    throw new IllegalStateException("Cannot read application data until initial handshake completed.");
                }
                return -1;
            }
            safeReadRecord();
        }
        int len2 = Math.min(len, this.applicationDataQueue.available());
        this.applicationDataQueue.removeData(buf, offset, len2, 0);
        return len2;
    }

    protected void safeCheckRecordHeader(byte[] recordHeader) throws IOException {
        try {
            this.recordStream.checkRecordHeader(recordHeader);
        } catch (TlsFatalAlert e) {
            handleException(e.getAlertDescription(), "Failed to read record", e);
            throw e;
        } catch (IOException e2) {
            handleException((short) 80, "Failed to read record", e2);
            throw e2;
        } catch (RuntimeException e3) {
            handleException((short) 80, "Failed to read record", e3);
            throw new TlsFatalAlert((short) 80, e3);
        }
    }

    protected void safeReadRecord() throws IOException {
        try {
            if (this.recordStream.readRecord()) {
                return;
            }
            if (!this.appDataReady) {
                throw new TlsFatalAlert((short) 40);
            }
            handleFailure();
            throw new TlsNoCloseNotifyException();
        } catch (TlsFatalAlert e) {
            handleException(e.getAlertDescription(), "Failed to read record", e);
            throw e;
        } catch (TlsFatalAlertReceived e2) {
            throw e2;
        } catch (IOException e3) {
            handleException((short) 80, "Failed to read record", e3);
            throw e3;
        } catch (RuntimeException e4) {
            handleException((short) 80, "Failed to read record", e4);
            throw new TlsFatalAlert((short) 80, e4);
        }
    }

    protected void safeWriteRecord(short type, byte[] buf, int offset, int len) throws IOException {
        try {
            this.recordStream.writeRecord(type, buf, offset, len);
        } catch (RuntimeException e) {
            handleException((short) 80, "Failed to write record", e);
            throw new TlsFatalAlert((short) 80, e);
        } catch (TlsFatalAlert e2) {
            handleException(e2.getAlertDescription(), "Failed to write record", e2);
            throw e2;
        } catch (IOException e3) {
            handleException((short) 80, "Failed to write record", e3);
            throw e3;
        }
    }

    protected void writeData(byte[] buf, int offset, int len) throws IOException {
        if (this.closed) {
            throw new IOException("Cannot write application data on closed/failed TLS connection");
        }
        while (len > 0) {
            if (this.appDataSplitEnabled) {
                switch (this.appDataSplitMode) {
                    case 0:
                    default:
                        safeWriteRecord((short) 23, buf, offset, 1);
                        offset++;
                        len--;
                        break;
                    case 2:
                        this.appDataSplitEnabled = false;
                    case 1:
                        safeWriteRecord((short) 23, TlsUtils.EMPTY_BYTES, 0, 0);
                        break;
                }
            }
            if (len > 0) {
                int toWrite = Math.min(len, this.recordStream.getPlaintextLimit());
                safeWriteRecord((short) 23, buf, offset, toWrite);
                offset += toWrite;
                len -= toWrite;
            }
        }
    }

    protected void setAppDataSplitMode(int appDataSplitMode) {
        if (appDataSplitMode < 0 || appDataSplitMode > 2) {
            throw new IllegalArgumentException("Illegal appDataSplitMode mode: " + appDataSplitMode);
        }
        this.appDataSplitMode = appDataSplitMode;
    }

    protected void writeHandshakeMessage(byte[] buf, int off, int len) throws IOException {
        if (len < 4) {
            throw new TlsFatalAlert((short) 80);
        }
        short type = TlsUtils.readUint8(buf, off);
        if (type != 0) {
            this.recordStream.getHandshakeHashUpdater().write(buf, off, len);
        }
        int total = 0;
        do {
            int toWrite = Math.min(len - total, this.recordStream.getPlaintextLimit());
            safeWriteRecord((short) 22, buf, off + total, toWrite);
            total += toWrite;
        } while (total < len);
    }

    public OutputStream getOutputStream() {
        if (!this.blocking) {
            throw new IllegalStateException("Cannot use OutputStream in non-blocking mode! Use offerOutput() instead.");
        }
        return this.tlsOutputStream;
    }

    public InputStream getInputStream() {
        if (!this.blocking) {
            throw new IllegalStateException("Cannot use InputStream in non-blocking mode! Use offerInput() instead.");
        }
        return this.tlsInputStream;
    }

    public void closeInput() throws IOException {
        if (this.blocking) {
            throw new IllegalStateException("Cannot use closeInput() in blocking mode!");
        }
        if (this.closed) {
            return;
        }
        if (this.inputBuffers.available() > 0) {
            throw new EOFException();
        }
        if (!this.appDataReady) {
            throw new TlsFatalAlert((short) 40);
        }
        throw new TlsNoCloseNotifyException();
    }

    public void offerInput(byte[] input) throws IOException {
        if (this.blocking) {
            throw new IllegalStateException("Cannot use offerInput() in blocking mode! Use getInputStream() instead.");
        }
        if (this.closed) {
            throw new IOException("Connection is closed, cannot accept any more input");
        }
        this.inputBuffers.addBytes(input);
        while (this.inputBuffers.available() >= 5) {
            byte[] recordHeader = new byte[5];
            this.inputBuffers.peek(recordHeader);
            int totalLength = TlsUtils.readUint16(recordHeader, 3) + 5;
            if (this.inputBuffers.available() < totalLength) {
                safeCheckRecordHeader(recordHeader);
                return;
            }
            safeReadRecord();
            if (this.closed) {
                if (this.connection_state != 16) {
                    throw new TlsFatalAlert((short) 80);
                }
                return;
            }
        }
    }

    public int getAvailableInputBytes() {
        if (this.blocking) {
            throw new IllegalStateException("Cannot use getAvailableInputBytes() in blocking mode! Use getInputStream().available() instead.");
        }
        return applicationDataAvailable();
    }

    public int readInput(byte[] buffer, int offset, int length) {
        if (this.blocking) {
            throw new IllegalStateException("Cannot use readInput() in blocking mode! Use getInputStream() instead.");
        }
        try {
            return readApplicationData(buffer, offset, Math.min(length, applicationDataAvailable()));
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public void offerOutput(byte[] buffer, int offset, int length) throws IOException {
        if (this.blocking) {
            throw new IllegalStateException("Cannot use offerOutput() in blocking mode! Use getOutputStream() instead.");
        }
        if (!this.appDataReady) {
            throw new IOException("Application data cannot be sent until the handshake is complete!");
        }
        writeData(buffer, offset, length);
    }

    public int getAvailableOutputBytes() {
        if (this.blocking) {
            throw new IllegalStateException("Cannot use getAvailableOutputBytes() in blocking mode! Use getOutputStream() instead.");
        }
        return this.outputBuffer.getBuffer().available();
    }

    public int readOutput(byte[] buffer, int offset, int length) {
        if (this.blocking) {
            throw new IllegalStateException("Cannot use readOutput() in blocking mode! Use getOutputStream() instead.");
        }
        int bytesToRead = Math.min(getAvailableOutputBytes(), length);
        this.outputBuffer.getBuffer().removeData(buffer, offset, bytesToRead, 0);
        return bytesToRead;
    }

    protected void invalidateSession() {
        if (this.sessionParameters != null) {
            this.sessionParameters.clear();
            this.sessionParameters = null;
        }
        if (this.tlsSession != null) {
            this.tlsSession.invalidate();
            this.tlsSession = null;
        }
    }

    protected void processFinishedMessage(ByteArrayInputStream buf) throws IOException {
        if (this.expected_verify_data == null) {
            throw new TlsFatalAlert((short) 80);
        }
        byte[] verify_data = TlsUtils.readFully(this.expected_verify_data.length, buf);
        assertEmpty(buf);
        if (!Arrays.constantTimeAreEqual(this.expected_verify_data, verify_data)) {
            throw new TlsFatalAlert((short) 51);
        }
    }

    protected void raiseAlertFatal(short alertDescription, String message, Throwable cause) throws IOException {
        getPeer().notifyAlertRaised((short) 2, alertDescription, message, cause);
        byte[] alert = {2, (byte) alertDescription};
        try {
            this.recordStream.writeRecord((short) 21, alert, 0, 2);
        } catch (Exception e) {
        }
    }

    protected void raiseAlertWarning(short alertDescription, String message) throws IOException {
        getPeer().notifyAlertRaised((short) 1, alertDescription, message, null);
        byte[] alert = {1, (byte) alertDescription};
        safeWriteRecord((short) 21, alert, 0, 2);
    }

    protected void sendCertificateMessage(Certificate certificate) throws IOException {
        if (certificate == null) {
            certificate = Certificate.EMPTY_CHAIN;
        }
        if (certificate.isEmpty()) {
            TlsContext context = getContext();
            if (!context.isServer()) {
                ProtocolVersion serverVersion = getContext().getServerVersion();
                if (serverVersion.isSSL()) {
                    String errorMessage = serverVersion.toString() + " client didn't provide credentials";
                    raiseAlertWarning((short) 41, errorMessage);
                    return;
                }
            }
        }
        HandshakeMessage message = new HandshakeMessage(this, (short) 11);
        certificate.encode(message);
        message.writeToRecordStream();
    }

    protected void sendChangeCipherSpecMessage() throws IOException {
        byte[] message = {1};
        safeWriteRecord((short) 20, message, 0, message.length);
        this.recordStream.sentWriteCipherSpec();
    }

    protected void sendFinishedMessage() throws IOException {
        byte[] verify_data = createVerifyData(getContext().isServer());
        HandshakeMessage message = new HandshakeMessage((short) 20, verify_data.length);
        message.write(verify_data);
        message.writeToRecordStream();
    }

    protected void sendSupplementalDataMessage(Vector supplementalData) throws IOException {
        HandshakeMessage message = new HandshakeMessage(this, (short) 23);
        writeSupplementalData(message, supplementalData);
        message.writeToRecordStream();
    }

    protected byte[] createVerifyData(boolean isServer) {
        TlsContext context = getContext();
        String asciiLabel = isServer ? ExporterLabel.server_finished : ExporterLabel.client_finished;
        byte[] sslSender = isServer ? TlsUtils.SSL_SERVER : TlsUtils.SSL_CLIENT;
        byte[] hash = getCurrentPRFHash(context, this.recordStream.getHandshakeHash(), sslSender);
        return TlsUtils.calculateVerifyData(context, asciiLabel, hash);
    }

    public void close() throws IOException {
        handleClose(true);
    }

    protected void flush() throws IOException {
        this.recordStream.flush();
    }

    public boolean isClosed() {
        return this.closed;
    }

    protected short processMaxFragmentLengthExtension(Hashtable clientExtensions, Hashtable serverExtensions, short alertDescription) throws IOException {
        short maxFragmentLength = TlsExtensionsUtils.getMaxFragmentLengthExtension(serverExtensions);
        if (maxFragmentLength >= 0 && (!MaxFragmentLength.isValid(maxFragmentLength) || (!this.resumedSession && maxFragmentLength != TlsExtensionsUtils.getMaxFragmentLengthExtension(clientExtensions)))) {
            throw new TlsFatalAlert(alertDescription);
        }
        return maxFragmentLength;
    }

    protected void refuseRenegotiation() throws IOException {
        if (TlsUtils.isSSL(getContext())) {
            throw new TlsFatalAlert((short) 40);
        }
        raiseAlertWarning((short) 100, "Renegotiation not supported");
    }

    protected static void assertEmpty(ByteArrayInputStream buf) throws IOException {
        if (buf.available() > 0) {
            throw new TlsFatalAlert((short) 50);
        }
    }

    protected static byte[] createRandomBlock(boolean useGMTUnixTime, RandomGenerator randomGenerator) {
        byte[] result = new byte[32];
        randomGenerator.nextBytes(result);
        if (useGMTUnixTime) {
            TlsUtils.writeGMTUnixTime(result, 0);
        }
        return result;
    }

    protected static byte[] createRenegotiationInfo(byte[] renegotiated_connection) throws IOException {
        return TlsUtils.encodeOpaque8(renegotiated_connection);
    }

    protected static void establishMasterSecret(TlsContext context, TlsKeyExchange keyExchange) throws IOException {
        byte[] pre_master_secret = keyExchange.generatePremasterSecret();
        try {
            context.getSecurityParameters().masterSecret = TlsUtils.calculateMasterSecret(context, pre_master_secret);
        } finally {
            if (pre_master_secret != null) {
                Arrays.fill(pre_master_secret, (byte) 0);
            }
        }
    }

    protected static byte[] getCurrentPRFHash(TlsContext context, TlsHandshakeHash handshakeHash, byte[] sslSender) {
        Digest d = handshakeHash.forkPRFHash();
        if (sslSender != null && TlsUtils.isSSL(context)) {
            d.update(sslSender, 0, sslSender.length);
        }
        byte[] bs = new byte[d.getDigestSize()];
        d.doFinal(bs, 0);
        return bs;
    }

    protected static Hashtable readExtensions(ByteArrayInputStream input) throws IOException {
        if (input.available() < 1) {
            return null;
        }
        byte[] extBytes = TlsUtils.readOpaque16(input);
        assertEmpty(input);
        ByteArrayInputStream buf = new ByteArrayInputStream(extBytes);
        Hashtable extensions = new Hashtable();
        while (buf.available() > 0) {
            Integer extension_type = Integers.valueOf(TlsUtils.readUint16(buf));
            byte[] extension_data = TlsUtils.readOpaque16(buf);
            if (null != extensions.put(extension_type, extension_data)) {
                throw new TlsFatalAlert((short) 47);
            }
        }
        return extensions;
    }

    protected static Vector readSupplementalDataMessage(ByteArrayInputStream input) throws IOException {
        byte[] supp_data = TlsUtils.readOpaque24(input);
        assertEmpty(input);
        ByteArrayInputStream buf = new ByteArrayInputStream(supp_data);
        Vector supplementalData = new Vector();
        while (buf.available() > 0) {
            int supp_data_type = TlsUtils.readUint16(buf);
            byte[] data = TlsUtils.readOpaque16(buf);
            supplementalData.addElement(new SupplementalDataEntry(supp_data_type, data));
        }
        return supplementalData;
    }

    protected static void writeExtensions(OutputStream output, Hashtable extensions) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        writeSelectedExtensions(buf, extensions, true);
        writeSelectedExtensions(buf, extensions, false);
        byte[] extBytes = buf.toByteArray();
        TlsUtils.writeOpaque16(extBytes, output);
    }

    protected static void writeSelectedExtensions(OutputStream output, Hashtable extensions, boolean selectEmpty) throws IOException {
        Enumeration keys = extensions.keys();
        while (keys.hasMoreElements()) {
            Integer key = (Integer) keys.nextElement();
            int extension_type = key.intValue();
            byte[] extension_data = (byte[]) extensions.get(key);
            if (selectEmpty == (extension_data.length == 0)) {
                TlsUtils.checkUint16(extension_type);
                TlsUtils.writeUint16(extension_type, output);
                TlsUtils.writeOpaque16(extension_data, output);
            }
        }
    }

    protected static void writeSupplementalData(OutputStream output, Vector supplementalData) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        for (int i = 0; i < supplementalData.size(); i++) {
            SupplementalDataEntry entry = (SupplementalDataEntry) supplementalData.elementAt(i);
            int supp_data_type = entry.getDataType();
            TlsUtils.checkUint16(supp_data_type);
            TlsUtils.writeUint16(supp_data_type, buf);
            TlsUtils.writeOpaque16(entry.getData(), buf);
        }
        byte[] supp_data = buf.toByteArray();
        TlsUtils.writeOpaque24(supp_data, output);
    }

    protected static int getPRFAlgorithm(TlsContext context, int ciphersuite) throws IOException {
        boolean isTLSv12 = TlsUtils.isTLSv12(context);
        switch (ciphersuite) {
            case 59:
            case 60:
            case 61:
            case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
            case 63:
            case 64:
            case 103:
            case 104:
            case CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA256 /* 105 */:
            case CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA256 /* 106 */:
            case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA256 /* 107 */:
            case CipherSuite.TLS_DH_anon_WITH_AES_128_CBC_SHA256 /* 108 */:
            case CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA256 /* 109 */:
            case CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256 /* 156 */:
            case CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256 /* 158 */:
            case CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256 /* 160 */:
            case CipherSuite.TLS_DHE_DSS_WITH_AES_128_GCM_SHA256 /* 162 */:
            case CipherSuite.TLS_DH_DSS_WITH_AES_128_GCM_SHA256 /* 164 */:
            case CipherSuite.TLS_DH_anon_WITH_AES_128_GCM_SHA256 /* 166 */:
            case CipherSuite.TLS_PSK_WITH_AES_128_GCM_SHA256 /* 168 */:
            case CipherSuite.TLS_DHE_PSK_WITH_AES_128_GCM_SHA256 /* 170 */:
            case CipherSuite.TLS_RSA_PSK_WITH_AES_128_GCM_SHA256 /* 172 */:
            case CipherSuite.TLS_RSA_WITH_CAMELLIA_128_CBC_SHA256 /* 186 */:
            case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA256 /* 187 */:
            case 188:
            case CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA256 /* 189 */:
            case CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA256 /* 190 */:
            case CipherSuite.TLS_DH_anon_WITH_CAMELLIA_128_CBC_SHA256 /* 191 */:
            case 192:
            case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA256 /* 193 */:
            case CipherSuite.TLS_DH_RSA_WITH_CAMELLIA_256_CBC_SHA256 /* 194 */:
            case CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA256 /* 195 */:
            case CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA256 /* 196 */:
            case CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA256 /* 197 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256 /* 49187 */:
            case CipherSuite.TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256 /* 49189 */:
            case CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256 /* 49191 */:
            case CipherSuite.TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256 /* 49193 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256 /* 49195 */:
            case CipherSuite.TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256 /* 49197 */:
            case CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256 /* 49199 */:
            case CipherSuite.TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256 /* 49201 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_CAMELLIA_128_CBC_SHA256 /* 49266 */:
            case CipherSuite.TLS_ECDH_ECDSA_WITH_CAMELLIA_128_CBC_SHA256 /* 49268 */:
            case CipherSuite.TLS_ECDHE_RSA_WITH_CAMELLIA_128_CBC_SHA256 /* 49270 */:
            case CipherSuite.TLS_ECDH_RSA_WITH_CAMELLIA_128_CBC_SHA256 /* 49272 */:
            case CipherSuite.TLS_RSA_WITH_CAMELLIA_128_GCM_SHA256 /* 49274 */:
            case CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_128_GCM_SHA256 /* 49276 */:
            case CipherSuite.TLS_DH_RSA_WITH_CAMELLIA_128_GCM_SHA256 /* 49278 */:
            case CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_128_GCM_SHA256 /* 49280 */:
            case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_128_GCM_SHA256 /* 49282 */:
            case CipherSuite.TLS_DH_anon_WITH_CAMELLIA_128_GCM_SHA256 /* 49284 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_CAMELLIA_128_GCM_SHA256 /* 49286 */:
            case CipherSuite.TLS_ECDH_ECDSA_WITH_CAMELLIA_128_GCM_SHA256 /* 49288 */:
            case CipherSuite.TLS_ECDHE_RSA_WITH_CAMELLIA_128_GCM_SHA256 /* 49290 */:
            case CipherSuite.TLS_ECDH_RSA_WITH_CAMELLIA_128_GCM_SHA256 /* 49292 */:
            case CipherSuite.TLS_PSK_WITH_CAMELLIA_128_GCM_SHA256 /* 49294 */:
            case CipherSuite.TLS_DHE_PSK_WITH_CAMELLIA_128_GCM_SHA256 /* 49296 */:
            case CipherSuite.TLS_RSA_PSK_WITH_CAMELLIA_128_GCM_SHA256 /* 49298 */:
            case CipherSuite.TLS_RSA_WITH_AES_128_CCM /* 49308 */:
            case CipherSuite.TLS_RSA_WITH_AES_256_CCM /* 49309 */:
            case CipherSuite.TLS_DHE_RSA_WITH_AES_128_CCM /* 49310 */:
            case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CCM /* 49311 */:
            case CipherSuite.TLS_RSA_WITH_AES_128_CCM_8 /* 49312 */:
            case CipherSuite.TLS_RSA_WITH_AES_256_CCM_8 /* 49313 */:
            case CipherSuite.TLS_DHE_RSA_WITH_AES_128_CCM_8 /* 49314 */:
            case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CCM_8 /* 49315 */:
            case CipherSuite.TLS_PSK_WITH_AES_128_CCM /* 49316 */:
            case CipherSuite.TLS_PSK_WITH_AES_256_CCM /* 49317 */:
            case CipherSuite.TLS_DHE_PSK_WITH_AES_128_CCM /* 49318 */:
            case CipherSuite.TLS_DHE_PSK_WITH_AES_256_CCM /* 49319 */:
            case CipherSuite.TLS_PSK_WITH_AES_128_CCM_8 /* 49320 */:
            case CipherSuite.TLS_PSK_WITH_AES_256_CCM_8 /* 49321 */:
            case CipherSuite.TLS_PSK_DHE_WITH_AES_128_CCM_8 /* 49322 */:
            case CipherSuite.TLS_PSK_DHE_WITH_AES_256_CCM_8 /* 49323 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CCM /* 49324 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CCM /* 49325 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CCM_8 /* 49326 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CCM_8 /* 49327 */:
            case CipherSuite.DRAFT_TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256 /* 52392 */:
            case CipherSuite.DRAFT_TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256 /* 52393 */:
            case CipherSuite.DRAFT_TLS_DHE_RSA_WITH_CHACHA20_POLY1305_SHA256 /* 52394 */:
            case CipherSuite.DRAFT_TLS_PSK_WITH_CHACHA20_POLY1305_SHA256 /* 52395 */:
            case CipherSuite.DRAFT_TLS_ECDHE_PSK_WITH_CHACHA20_POLY1305_SHA256 /* 52396 */:
            case CipherSuite.DRAFT_TLS_DHE_PSK_WITH_CHACHA20_POLY1305_SHA256 /* 52397 */:
            case CipherSuite.DRAFT_TLS_RSA_PSK_WITH_CHACHA20_POLY1305_SHA256 /* 52398 */:
            case CipherSuite.DRAFT_TLS_DHE_RSA_WITH_AES_128_OCB /* 65280 */:
            case 65281:
            case 65282:
            case CipherSuite.DRAFT_TLS_ECDHE_RSA_WITH_AES_256_OCB /* 65283 */:
            case CipherSuite.DRAFT_TLS_ECDHE_ECDSA_WITH_AES_128_OCB /* 65284 */:
            case CipherSuite.DRAFT_TLS_ECDHE_ECDSA_WITH_AES_256_OCB /* 65285 */:
            case CipherSuite.DRAFT_TLS_PSK_WITH_AES_128_OCB /* 65296 */:
            case CipherSuite.DRAFT_TLS_PSK_WITH_AES_256_OCB /* 65297 */:
            case CipherSuite.DRAFT_TLS_DHE_PSK_WITH_AES_128_OCB /* 65298 */:
            case CipherSuite.DRAFT_TLS_DHE_PSK_WITH_AES_256_OCB /* 65299 */:
            case CipherSuite.DRAFT_TLS_ECDHE_PSK_WITH_AES_128_OCB /* 65300 */:
            case CipherSuite.DRAFT_TLS_ECDHE_PSK_WITH_AES_256_OCB /* 65301 */:
                if (isTLSv12) {
                    return 1;
                }
                throw new TlsFatalAlert((short) 47);
            case CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384 /* 157 */:
            case CipherSuite.TLS_DHE_RSA_WITH_AES_256_GCM_SHA384 /* 159 */:
            case CipherSuite.TLS_DH_RSA_WITH_AES_256_GCM_SHA384 /* 161 */:
            case CipherSuite.TLS_DHE_DSS_WITH_AES_256_GCM_SHA384 /* 163 */:
            case CipherSuite.TLS_DH_DSS_WITH_AES_256_GCM_SHA384 /* 165 */:
            case CipherSuite.TLS_DH_anon_WITH_AES_256_GCM_SHA384 /* 167 */:
            case CipherSuite.TLS_PSK_WITH_AES_256_GCM_SHA384 /* 169 */:
            case CipherSuite.TLS_DHE_PSK_WITH_AES_256_GCM_SHA384 /* 171 */:
            case CipherSuite.TLS_RSA_PSK_WITH_AES_256_GCM_SHA384 /* 173 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384 /* 49188 */:
            case CipherSuite.TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384 /* 49190 */:
            case CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384 /* 49192 */:
            case CipherSuite.TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384 /* 49194 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384 /* 49196 */:
            case CipherSuite.TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384 /* 49198 */:
            case CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384 /* 49200 */:
            case CipherSuite.TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384 /* 49202 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_CAMELLIA_256_CBC_SHA384 /* 49267 */:
            case CipherSuite.TLS_ECDH_ECDSA_WITH_CAMELLIA_256_CBC_SHA384 /* 49269 */:
            case CipherSuite.TLS_ECDHE_RSA_WITH_CAMELLIA_256_CBC_SHA384 /* 49271 */:
            case CipherSuite.TLS_ECDH_RSA_WITH_CAMELLIA_256_CBC_SHA384 /* 49273 */:
            case CipherSuite.TLS_RSA_WITH_CAMELLIA_256_GCM_SHA384 /* 49275 */:
            case CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_256_GCM_SHA384 /* 49277 */:
            case CipherSuite.TLS_DH_RSA_WITH_CAMELLIA_256_GCM_SHA384 /* 49279 */:
            case CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_256_GCM_SHA384 /* 49281 */:
            case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_256_GCM_SHA384 /* 49283 */:
            case CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_GCM_SHA384 /* 49285 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_CAMELLIA_256_GCM_SHA384 /* 49287 */:
            case CipherSuite.TLS_ECDH_ECDSA_WITH_CAMELLIA_256_GCM_SHA384 /* 49289 */:
            case CipherSuite.TLS_ECDHE_RSA_WITH_CAMELLIA_256_GCM_SHA384 /* 49291 */:
            case CipherSuite.TLS_ECDH_RSA_WITH_CAMELLIA_256_GCM_SHA384 /* 49293 */:
            case CipherSuite.TLS_PSK_WITH_CAMELLIA_256_GCM_SHA384 /* 49295 */:
            case CipherSuite.TLS_DHE_PSK_WITH_CAMELLIA_256_GCM_SHA384 /* 49297 */:
            case CipherSuite.TLS_RSA_PSK_WITH_CAMELLIA_256_GCM_SHA384 /* 49299 */:
                if (isTLSv12) {
                    return 2;
                }
                throw new TlsFatalAlert((short) 47);
            case CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA384 /* 175 */:
            case CipherSuite.TLS_PSK_WITH_NULL_SHA384 /* 177 */:
            case CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA384 /* 179 */:
            case CipherSuite.TLS_DHE_PSK_WITH_NULL_SHA384 /* 181 */:
            case CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA384 /* 183 */:
            case CipherSuite.TLS_RSA_PSK_WITH_NULL_SHA384 /* 185 */:
            case CipherSuite.TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA384 /* 49208 */:
            case CipherSuite.TLS_ECDHE_PSK_WITH_NULL_SHA384 /* 49211 */:
            case CipherSuite.TLS_PSK_WITH_CAMELLIA_256_CBC_SHA384 /* 49301 */:
            case CipherSuite.TLS_DHE_PSK_WITH_CAMELLIA_256_CBC_SHA384 /* 49303 */:
            case CipherSuite.TLS_RSA_PSK_WITH_CAMELLIA_256_CBC_SHA384 /* 49305 */:
            case CipherSuite.TLS_ECDHE_PSK_WITH_CAMELLIA_256_CBC_SHA384 /* 49307 */:
                if (isTLSv12) {
                    return 2;
                }
                return 0;
            default:
                if (isTLSv12) {
                    return 1;
                }
                return 0;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsProtocol$HandshakeMessage.class */
    class HandshakeMessage extends ByteArrayOutputStream {
        HandshakeMessage(TlsProtocol this$0, short handshakeType) throws IOException {
            this(handshakeType, 60);
        }

        HandshakeMessage(short handshakeType, int length) throws IOException {
            super(length + 4);
            TlsUtils.writeUint8(handshakeType, (OutputStream) this);
            this.count += 3;
        }

        void writeToRecordStream() throws IOException {
            int length = this.count - 4;
            TlsUtils.checkUint24(length);
            TlsUtils.writeUint24(length, this.buf, 1);
            TlsProtocol.this.writeHandshakeMessage(this.buf, 0, this.count);
            this.buf = null;
        }
    }
}
