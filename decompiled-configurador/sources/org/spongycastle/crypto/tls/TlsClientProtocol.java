package org.spongycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsClientProtocol.class */
public class TlsClientProtocol extends TlsProtocol {
    protected TlsClient tlsClient;
    TlsClientContextImpl tlsClientContext;
    protected byte[] selectedSessionID;
    protected TlsKeyExchange keyExchange;
    protected TlsAuthentication authentication;
    protected CertificateStatus certificateStatus;
    protected CertificateRequest certificateRequest;

    public TlsClientProtocol(InputStream input, OutputStream output, SecureRandom secureRandom) {
        super(input, output, secureRandom);
        this.tlsClient = null;
        this.tlsClientContext = null;
        this.selectedSessionID = null;
        this.keyExchange = null;
        this.authentication = null;
        this.certificateStatus = null;
        this.certificateRequest = null;
    }

    public TlsClientProtocol(SecureRandom secureRandom) {
        super(secureRandom);
        this.tlsClient = null;
        this.tlsClientContext = null;
        this.selectedSessionID = null;
        this.keyExchange = null;
        this.authentication = null;
        this.certificateStatus = null;
        this.certificateRequest = null;
    }

    public void connect(TlsClient tlsClient) throws IOException {
        SessionParameters sessionParameters;
        if (tlsClient == null) {
            throw new IllegalArgumentException("'tlsClient' cannot be null");
        }
        if (this.tlsClient != null) {
            throw new IllegalStateException("'connect' can only be called once");
        }
        this.tlsClient = tlsClient;
        this.securityParameters = new SecurityParameters();
        this.securityParameters.entity = 1;
        this.tlsClientContext = new TlsClientContextImpl(this.secureRandom, this.securityParameters);
        this.securityParameters.clientRandom = createRandomBlock(tlsClient.shouldUseGMTUnixTime(), this.tlsClientContext.getNonceRandomGenerator());
        this.tlsClient.init(this.tlsClientContext);
        this.recordStream.init(this.tlsClientContext);
        TlsSession sessionToResume = tlsClient.getSessionToResume();
        if (sessionToResume != null && sessionToResume.isResumable() && (sessionParameters = sessionToResume.exportSessionParameters()) != null) {
            this.tlsSession = sessionToResume;
            this.sessionParameters = sessionParameters;
        }
        sendClientHelloMessage();
        this.connection_state = (short) 1;
        blockForHandshake();
    }

    @Override // org.spongycastle.crypto.tls.TlsProtocol
    protected void cleanupHandshake() {
        super.cleanupHandshake();
        this.selectedSessionID = null;
        this.keyExchange = null;
        this.authentication = null;
        this.certificateStatus = null;
        this.certificateRequest = null;
    }

    @Override // org.spongycastle.crypto.tls.TlsProtocol
    protected TlsContext getContext() {
        return this.tlsClientContext;
    }

    @Override // org.spongycastle.crypto.tls.TlsProtocol
    AbstractTlsContext getContextAdmin() {
        return this.tlsClientContext;
    }

    @Override // org.spongycastle.crypto.tls.TlsProtocol
    protected TlsPeer getPeer() {
        return this.tlsClient;
    }

    @Override // org.spongycastle.crypto.tls.TlsProtocol
    protected void handleHandshakeMessage(short type, ByteArrayInputStream buf) throws IOException {
        byte[] hash;
        if (this.resumedSession) {
            if (type != 20 || this.connection_state != 2) {
                throw new TlsFatalAlert((short) 10);
            }
            processFinishedMessage(buf);
            this.connection_state = (short) 15;
            sendFinishedMessage();
            this.connection_state = (short) 13;
            completeHandshake();
            return;
        }
        switch (type) {
            case 0:
                assertEmpty(buf);
                if (this.connection_state == 16) {
                    refuseRenegotiation();
                    return;
                }
                return;
            case 1:
            case 3:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 21:
            default:
                throw new TlsFatalAlert((short) 10);
            case 2:
                switch (this.connection_state) {
                    case 1:
                        receiveServerHelloMessage(buf);
                        this.connection_state = (short) 2;
                        this.recordStream.notifyHelloComplete();
                        applyMaxFragmentLengthExtension();
                        if (this.resumedSession) {
                            this.securityParameters.masterSecret = Arrays.clone(this.sessionParameters.getMasterSecret());
                            this.recordStream.setPendingConnectionState(getPeer().getCompression(), getPeer().getCipher());
                            sendChangeCipherSpecMessage();
                            return;
                        }
                        invalidateSession();
                        if (this.selectedSessionID.length > 0) {
                            this.tlsSession = new TlsSessionImpl(this.selectedSessionID, null);
                            return;
                        }
                        return;
                    default:
                        throw new TlsFatalAlert((short) 10);
                }
            case 4:
                switch (this.connection_state) {
                    case 13:
                        if (!this.expectSessionTicket) {
                            throw new TlsFatalAlert((short) 10);
                        }
                        invalidateSession();
                        receiveNewSessionTicketMessage(buf);
                        this.connection_state = (short) 14;
                        return;
                    default:
                        throw new TlsFatalAlert((short) 10);
                }
            case 11:
                switch (this.connection_state) {
                    case 2:
                        handleSupplementalData(null);
                        break;
                    case 3:
                        break;
                    default:
                        throw new TlsFatalAlert((short) 10);
                }
                this.peerCertificate = Certificate.parse(buf);
                assertEmpty(buf);
                if (this.peerCertificate == null || this.peerCertificate.isEmpty()) {
                    this.allowCertificateStatus = false;
                }
                this.keyExchange.processServerCertificate(this.peerCertificate);
                this.authentication = this.tlsClient.getAuthentication();
                this.authentication.notifyServerCertificate(this.peerCertificate);
                this.connection_state = (short) 4;
                return;
            case 12:
                switch (this.connection_state) {
                    case 2:
                        handleSupplementalData(null);
                    case 3:
                        this.keyExchange.skipServerCredentials();
                        this.authentication = null;
                    case 4:
                    case 5:
                        this.keyExchange.processServerKeyExchange(buf);
                        assertEmpty(buf);
                        this.connection_state = (short) 6;
                        return;
                    default:
                        throw new TlsFatalAlert((short) 10);
                }
                break;
            case 13:
                switch (this.connection_state) {
                    case 4:
                    case 5:
                        this.keyExchange.skipServerKeyExchange();
                        break;
                    case 6:
                        break;
                    default:
                        throw new TlsFatalAlert((short) 10);
                }
                if (this.authentication == null) {
                    throw new TlsFatalAlert((short) 40);
                }
                this.certificateRequest = CertificateRequest.parse(getContext(), buf);
                assertEmpty(buf);
                this.keyExchange.validateCertificateRequest(this.certificateRequest);
                TlsUtils.trackHashAlgorithms(this.recordStream.getHandshakeHash(), this.certificateRequest.getSupportedSignatureAlgorithms());
                this.connection_state = (short) 7;
                return;
            case 14:
                switch (this.connection_state) {
                    case 2:
                        handleSupplementalData(null);
                    case 3:
                        this.keyExchange.skipServerCredentials();
                        this.authentication = null;
                    case 4:
                    case 5:
                        this.keyExchange.skipServerKeyExchange();
                    case 6:
                    case 7:
                        assertEmpty(buf);
                        this.connection_state = (short) 8;
                        this.recordStream.getHandshakeHash().sealHashAlgorithms();
                        Vector clientSupplementalData = this.tlsClient.getClientSupplementalData();
                        if (clientSupplementalData != null) {
                            sendSupplementalDataMessage(clientSupplementalData);
                        }
                        this.connection_state = (short) 9;
                        TlsCredentials clientCreds = null;
                        if (this.certificateRequest == null) {
                            this.keyExchange.skipClientCredentials();
                        } else {
                            clientCreds = this.authentication.getClientCredentials(this.certificateRequest);
                            if (clientCreds == null) {
                                this.keyExchange.skipClientCredentials();
                                sendCertificateMessage(Certificate.EMPTY_CHAIN);
                            } else {
                                this.keyExchange.processClientCredentials(clientCreds);
                                sendCertificateMessage(clientCreds.getCertificate());
                            }
                        }
                        this.connection_state = (short) 10;
                        sendClientKeyExchangeMessage();
                        this.connection_state = (short) 11;
                        if (TlsUtils.isSSL(getContext())) {
                            establishMasterSecret(getContext(), this.keyExchange);
                        }
                        TlsHandshakeHash prepareFinishHash = this.recordStream.prepareToFinish();
                        this.securityParameters.sessionHash = getCurrentPRFHash(getContext(), prepareFinishHash, null);
                        if (!TlsUtils.isSSL(getContext())) {
                            establishMasterSecret(getContext(), this.keyExchange);
                        }
                        this.recordStream.setPendingConnectionState(getPeer().getCompression(), getPeer().getCipher());
                        if (clientCreds != null && (clientCreds instanceof TlsSignerCredentials)) {
                            TlsSignerCredentials signerCredentials = (TlsSignerCredentials) clientCreds;
                            SignatureAndHashAlgorithm signatureAndHashAlgorithm = TlsUtils.getSignatureAndHashAlgorithm(getContext(), signerCredentials);
                            if (signatureAndHashAlgorithm == null) {
                                hash = this.securityParameters.getSessionHash();
                            } else {
                                hash = prepareFinishHash.getFinalHash(signatureAndHashAlgorithm.getHash());
                            }
                            byte[] signature = signerCredentials.generateCertificateSignature(hash);
                            DigitallySigned certificateVerify = new DigitallySigned(signatureAndHashAlgorithm, signature);
                            sendCertificateVerifyMessage(certificateVerify);
                            this.connection_state = (short) 12;
                        }
                        sendChangeCipherSpecMessage();
                        sendFinishedMessage();
                        this.connection_state = (short) 13;
                        return;
                    default:
                        throw new TlsFatalAlert((short) 10);
                }
                break;
            case 20:
                switch (this.connection_state) {
                    case 13:
                        if (this.expectSessionTicket) {
                            throw new TlsFatalAlert((short) 10);
                        }
                        break;
                    case 14:
                        break;
                    default:
                        throw new TlsFatalAlert((short) 10);
                }
                processFinishedMessage(buf);
                this.connection_state = (short) 15;
                completeHandshake();
                return;
            case 22:
                switch (this.connection_state) {
                    case 4:
                        if (!this.allowCertificateStatus) {
                            throw new TlsFatalAlert((short) 10);
                        }
                        this.certificateStatus = CertificateStatus.parse(buf);
                        assertEmpty(buf);
                        this.connection_state = (short) 5;
                        return;
                    default:
                        throw new TlsFatalAlert((short) 10);
                }
            case 23:
                switch (this.connection_state) {
                    case 2:
                        handleSupplementalData(readSupplementalDataMessage(buf));
                        return;
                    default:
                        throw new TlsFatalAlert((short) 10);
                }
        }
    }

    protected void handleSupplementalData(Vector serverSupplementalData) throws IOException {
        this.tlsClient.processServerSupplementalData(serverSupplementalData);
        this.connection_state = (short) 3;
        this.keyExchange = this.tlsClient.getKeyExchange();
        this.keyExchange.init(getContext());
    }

    protected void receiveNewSessionTicketMessage(ByteArrayInputStream buf) throws IOException {
        NewSessionTicket newSessionTicket = NewSessionTicket.parse(buf);
        assertEmpty(buf);
        this.tlsClient.notifyNewSessionTicket(newSessionTicket);
    }

    protected void receiveServerHelloMessage(ByteArrayInputStream buf) throws IOException {
        ProtocolVersion server_version = TlsUtils.readVersion(buf);
        if (server_version.isDTLS()) {
            throw new TlsFatalAlert((short) 47);
        }
        if (!server_version.equals(this.recordStream.getReadVersion())) {
            throw new TlsFatalAlert((short) 47);
        }
        ProtocolVersion client_version = getContext().getClientVersion();
        if (!server_version.isEqualOrEarlierVersionOf(client_version)) {
            throw new TlsFatalAlert((short) 47);
        }
        this.recordStream.setWriteVersion(server_version);
        getContextAdmin().setServerVersion(server_version);
        this.tlsClient.notifyServerVersion(server_version);
        this.securityParameters.serverRandom = TlsUtils.readFully(32, buf);
        this.selectedSessionID = TlsUtils.readOpaque8(buf);
        if (this.selectedSessionID.length > 32) {
            throw new TlsFatalAlert((short) 47);
        }
        this.tlsClient.notifySessionID(this.selectedSessionID);
        this.resumedSession = this.selectedSessionID.length > 0 && this.tlsSession != null && Arrays.areEqual(this.selectedSessionID, this.tlsSession.getSessionID());
        int selectedCipherSuite = TlsUtils.readUint16(buf);
        if (!Arrays.contains(this.offeredCipherSuites, selectedCipherSuite) || selectedCipherSuite == 0 || CipherSuite.isSCSV(selectedCipherSuite) || !TlsUtils.isValidCipherSuiteForVersion(selectedCipherSuite, getContext().getServerVersion())) {
            throw new TlsFatalAlert((short) 47);
        }
        this.tlsClient.notifySelectedCipherSuite(selectedCipherSuite);
        short selectedCompressionMethod = TlsUtils.readUint8(buf);
        if (!Arrays.contains(this.offeredCompressionMethods, selectedCompressionMethod)) {
            throw new TlsFatalAlert((short) 47);
        }
        this.tlsClient.notifySelectedCompressionMethod(selectedCompressionMethod);
        this.serverExtensions = readExtensions(buf);
        if (this.serverExtensions != null) {
            Enumeration e = this.serverExtensions.keys();
            while (e.hasMoreElements()) {
                Integer extType = (Integer) e.nextElement();
                if (!extType.equals(EXT_RenegotiationInfo)) {
                    if (null == TlsUtils.getExtensionData(this.clientExtensions, extType)) {
                        throw new TlsFatalAlert((short) 110);
                    }
                    if (this.resumedSession) {
                    }
                }
            }
        }
        byte[] renegExtData = TlsUtils.getExtensionData(this.serverExtensions, EXT_RenegotiationInfo);
        if (renegExtData != null) {
            this.secure_renegotiation = true;
            if (!Arrays.constantTimeAreEqual(renegExtData, createRenegotiationInfo(TlsUtils.EMPTY_BYTES))) {
                throw new TlsFatalAlert((short) 40);
            }
        }
        this.tlsClient.notifySecureRenegotiation(this.secure_renegotiation);
        Hashtable sessionClientExtensions = this.clientExtensions;
        Hashtable sessionServerExtensions = this.serverExtensions;
        if (this.resumedSession) {
            if (selectedCipherSuite != this.sessionParameters.getCipherSuite() || selectedCompressionMethod != this.sessionParameters.getCompressionAlgorithm()) {
                throw new TlsFatalAlert((short) 47);
            }
            sessionClientExtensions = null;
            sessionServerExtensions = this.sessionParameters.readServerExtensions();
        }
        this.securityParameters.cipherSuite = selectedCipherSuite;
        this.securityParameters.compressionAlgorithm = selectedCompressionMethod;
        if (sessionServerExtensions != null) {
            boolean serverSentEncryptThenMAC = TlsExtensionsUtils.hasEncryptThenMACExtension(sessionServerExtensions);
            if (serverSentEncryptThenMAC && !TlsUtils.isBlockCipherSuite(selectedCipherSuite)) {
                throw new TlsFatalAlert((short) 47);
            }
            this.securityParameters.encryptThenMAC = serverSentEncryptThenMAC;
            this.securityParameters.extendedMasterSecret = TlsExtensionsUtils.hasExtendedMasterSecretExtension(sessionServerExtensions);
            this.securityParameters.maxFragmentLength = processMaxFragmentLengthExtension(sessionClientExtensions, sessionServerExtensions, (short) 47);
            this.securityParameters.truncatedHMac = TlsExtensionsUtils.hasTruncatedHMacExtension(sessionServerExtensions);
            this.allowCertificateStatus = !this.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(sessionServerExtensions, TlsExtensionsUtils.EXT_status_request, (short) 47);
            this.expectSessionTicket = !this.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(sessionServerExtensions, TlsProtocol.EXT_SessionTicket, (short) 47);
        }
        if (sessionClientExtensions != null) {
            this.tlsClient.processServerExtensions(sessionServerExtensions);
        }
        this.securityParameters.prfAlgorithm = getPRFAlgorithm(getContext(), this.securityParameters.getCipherSuite());
        this.securityParameters.verifyDataLength = 12;
    }

    protected void sendCertificateVerifyMessage(DigitallySigned certificateVerify) throws IOException {
        TlsProtocol.HandshakeMessage message = new TlsProtocol.HandshakeMessage(this, (short) 15);
        certificateVerify.encode(message);
        message.writeToRecordStream();
    }

    protected void sendClientHelloMessage() throws IOException {
        this.recordStream.setWriteVersion(this.tlsClient.getClientHelloRecordLayerVersion());
        ProtocolVersion client_version = this.tlsClient.getClientVersion();
        if (client_version.isDTLS()) {
            throw new TlsFatalAlert((short) 80);
        }
        getContextAdmin().setClientVersion(client_version);
        byte[] session_id = TlsUtils.EMPTY_BYTES;
        if (this.tlsSession != null) {
            session_id = this.tlsSession.getSessionID();
            if (session_id == null || session_id.length > 32) {
                session_id = TlsUtils.EMPTY_BYTES;
            }
        }
        boolean fallback = this.tlsClient.isFallback();
        this.offeredCipherSuites = this.tlsClient.getCipherSuites();
        this.offeredCompressionMethods = this.tlsClient.getCompressionMethods();
        if (session_id.length > 0 && this.sessionParameters != null && (!Arrays.contains(this.offeredCipherSuites, this.sessionParameters.getCipherSuite()) || !Arrays.contains(this.offeredCompressionMethods, this.sessionParameters.getCompressionAlgorithm()))) {
            session_id = TlsUtils.EMPTY_BYTES;
        }
        this.clientExtensions = this.tlsClient.getClientExtensions();
        TlsProtocol.HandshakeMessage message = new TlsProtocol.HandshakeMessage(this, (short) 1);
        TlsUtils.writeVersion(client_version, message);
        message.write(this.securityParameters.getClientRandom());
        TlsUtils.writeOpaque8(session_id, message);
        byte[] renegExtData = TlsUtils.getExtensionData(this.clientExtensions, EXT_RenegotiationInfo);
        boolean noRenegExt = null == renegExtData;
        boolean noRenegSCSV = !Arrays.contains(this.offeredCipherSuites, 255);
        if (noRenegExt && noRenegSCSV) {
            this.offeredCipherSuites = Arrays.append(this.offeredCipherSuites, 255);
        }
        if (fallback && !Arrays.contains(this.offeredCipherSuites, CipherSuite.TLS_FALLBACK_SCSV)) {
            this.offeredCipherSuites = Arrays.append(this.offeredCipherSuites, CipherSuite.TLS_FALLBACK_SCSV);
        }
        TlsUtils.writeUint16ArrayWithUint16Length(this.offeredCipherSuites, message);
        TlsUtils.writeUint8ArrayWithUint8Length(this.offeredCompressionMethods, message);
        if (this.clientExtensions != null) {
            writeExtensions(message, this.clientExtensions);
        }
        message.writeToRecordStream();
    }

    protected void sendClientKeyExchangeMessage() throws IOException {
        TlsProtocol.HandshakeMessage message = new TlsProtocol.HandshakeMessage(this, (short) 16);
        this.keyExchange.generateClientKeyExchange(message);
        message.writeToRecordStream();
    }
}
