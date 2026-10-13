package org.spongycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Vector;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.util.PublicKeyFactory;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsServerProtocol.class */
public class TlsServerProtocol extends TlsProtocol {
    protected TlsServer tlsServer;
    TlsServerContextImpl tlsServerContext;
    protected TlsKeyExchange keyExchange;
    protected TlsCredentials serverCredentials;
    protected CertificateRequest certificateRequest;
    protected short clientCertificateType;
    protected TlsHandshakeHash prepareFinishHash;

    public TlsServerProtocol(InputStream input, OutputStream output, SecureRandom secureRandom) {
        super(input, output, secureRandom);
        this.tlsServer = null;
        this.tlsServerContext = null;
        this.keyExchange = null;
        this.serverCredentials = null;
        this.certificateRequest = null;
        this.clientCertificateType = (short) -1;
        this.prepareFinishHash = null;
    }

    public TlsServerProtocol(SecureRandom secureRandom) {
        super(secureRandom);
        this.tlsServer = null;
        this.tlsServerContext = null;
        this.keyExchange = null;
        this.serverCredentials = null;
        this.certificateRequest = null;
        this.clientCertificateType = (short) -1;
        this.prepareFinishHash = null;
    }

    public void accept(TlsServer tlsServer) throws IOException {
        if (tlsServer == null) {
            throw new IllegalArgumentException("'tlsServer' cannot be null");
        }
        if (this.tlsServer != null) {
            throw new IllegalStateException("'accept' can only be called once");
        }
        this.tlsServer = tlsServer;
        this.securityParameters = new SecurityParameters();
        this.securityParameters.entity = 0;
        this.tlsServerContext = new TlsServerContextImpl(this.secureRandom, this.securityParameters);
        this.securityParameters.serverRandom = createRandomBlock(tlsServer.shouldUseGMTUnixTime(), this.tlsServerContext.getNonceRandomGenerator());
        this.tlsServer.init(this.tlsServerContext);
        this.recordStream.init(this.tlsServerContext);
        this.recordStream.setRestrictReadVersion(false);
        blockForHandshake();
    }

    @Override // org.spongycastle.crypto.tls.TlsProtocol
    protected void cleanupHandshake() {
        super.cleanupHandshake();
        this.keyExchange = null;
        this.serverCredentials = null;
        this.certificateRequest = null;
        this.prepareFinishHash = null;
    }

    @Override // org.spongycastle.crypto.tls.TlsProtocol
    protected TlsContext getContext() {
        return this.tlsServerContext;
    }

    @Override // org.spongycastle.crypto.tls.TlsProtocol
    AbstractTlsContext getContextAdmin() {
        return this.tlsServerContext;
    }

    @Override // org.spongycastle.crypto.tls.TlsProtocol
    protected TlsPeer getPeer() {
        return this.tlsServer;
    }

    @Override // org.spongycastle.crypto.tls.TlsProtocol
    protected void handleHandshakeMessage(short type, ByteArrayInputStream buf) throws IOException {
        CertificateStatus certificateStatus;
        switch (type) {
            case 0:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 12:
            case 13:
            case 14:
            case 17:
            case 18:
            case 19:
            case 21:
            case 22:
            default:
                throw new TlsFatalAlert((short) 10);
            case 1:
                switch (this.connection_state) {
                    case 0:
                        receiveClientHelloMessage(buf);
                        this.connection_state = (short) 1;
                        sendServerHelloMessage();
                        this.connection_state = (short) 2;
                        this.recordStream.notifyHelloComplete();
                        Vector serverSupplementalData = this.tlsServer.getServerSupplementalData();
                        if (serverSupplementalData != null) {
                            sendSupplementalDataMessage(serverSupplementalData);
                        }
                        this.connection_state = (short) 3;
                        this.keyExchange = this.tlsServer.getKeyExchange();
                        this.keyExchange.init(getContext());
                        this.serverCredentials = this.tlsServer.getCredentials();
                        Certificate serverCertificate = null;
                        if (this.serverCredentials == null) {
                            this.keyExchange.skipServerCredentials();
                        } else {
                            this.keyExchange.processServerCredentials(this.serverCredentials);
                            serverCertificate = this.serverCredentials.getCertificate();
                            sendCertificateMessage(serverCertificate);
                        }
                        this.connection_state = (short) 4;
                        if (serverCertificate == null || serverCertificate.isEmpty()) {
                            this.allowCertificateStatus = false;
                        }
                        if (this.allowCertificateStatus && (certificateStatus = this.tlsServer.getCertificateStatus()) != null) {
                            sendCertificateStatusMessage(certificateStatus);
                        }
                        this.connection_state = (short) 5;
                        byte[] serverKeyExchange = this.keyExchange.generateServerKeyExchange();
                        if (serverKeyExchange != null) {
                            sendServerKeyExchangeMessage(serverKeyExchange);
                        }
                        this.connection_state = (short) 6;
                        if (this.serverCredentials != null) {
                            this.certificateRequest = this.tlsServer.getCertificateRequest();
                            if (this.certificateRequest != null) {
                                if (TlsUtils.isTLSv12(getContext()) != (this.certificateRequest.getSupportedSignatureAlgorithms() != null)) {
                                    throw new TlsFatalAlert((short) 80);
                                }
                                this.keyExchange.validateCertificateRequest(this.certificateRequest);
                                sendCertificateRequestMessage(this.certificateRequest);
                                TlsUtils.trackHashAlgorithms(this.recordStream.getHandshakeHash(), this.certificateRequest.getSupportedSignatureAlgorithms());
                            }
                        }
                        this.connection_state = (short) 7;
                        sendServerHelloDoneMessage();
                        this.connection_state = (short) 8;
                        this.recordStream.getHandshakeHash().sealHashAlgorithms();
                        return;
                    case 16:
                        refuseRenegotiation();
                        return;
                    default:
                        throw new TlsFatalAlert((short) 10);
                }
            case 11:
                switch (this.connection_state) {
                    case 8:
                        this.tlsServer.processClientSupplementalData(null);
                        break;
                    case 9:
                        break;
                    default:
                        throw new TlsFatalAlert((short) 10);
                }
                if (this.certificateRequest == null) {
                    throw new TlsFatalAlert((short) 10);
                }
                receiveCertificateMessage(buf);
                this.connection_state = (short) 10;
                return;
            case 15:
                switch (this.connection_state) {
                    case 11:
                        if (!expectCertificateVerifyMessage()) {
                            throw new TlsFatalAlert((short) 10);
                        }
                        receiveCertificateVerifyMessage(buf);
                        this.connection_state = (short) 12;
                        return;
                    default:
                        throw new TlsFatalAlert((short) 10);
                }
            case 16:
                switch (this.connection_state) {
                    case 8:
                        this.tlsServer.processClientSupplementalData(null);
                    case 9:
                        if (this.certificateRequest == null) {
                            this.keyExchange.skipClientCredentials();
                            break;
                        } else {
                            if (TlsUtils.isTLSv12(getContext())) {
                                throw new TlsFatalAlert((short) 10);
                            }
                            if (TlsUtils.isSSL(getContext())) {
                                if (this.peerCertificate == null) {
                                    throw new TlsFatalAlert((short) 10);
                                }
                            } else {
                                notifyClientCertificate(Certificate.EMPTY_CHAIN);
                            }
                            break;
                        }
                    case 10:
                        receiveClientKeyExchangeMessage(buf);
                        this.connection_state = (short) 11;
                        return;
                    default:
                        throw new TlsFatalAlert((short) 10);
                }
                break;
            case 20:
                switch (this.connection_state) {
                    case 11:
                        if (expectCertificateVerifyMessage()) {
                            throw new TlsFatalAlert((short) 10);
                        }
                        break;
                    case 12:
                        break;
                    default:
                        throw new TlsFatalAlert((short) 10);
                }
                processFinishedMessage(buf);
                this.connection_state = (short) 13;
                if (this.expectSessionTicket) {
                    sendNewSessionTicketMessage(this.tlsServer.getNewSessionTicket());
                    sendChangeCipherSpecMessage();
                }
                this.connection_state = (short) 14;
                sendFinishedMessage();
                this.connection_state = (short) 15;
                completeHandshake();
                return;
            case 23:
                switch (this.connection_state) {
                    case 8:
                        this.tlsServer.processClientSupplementalData(readSupplementalDataMessage(buf));
                        this.connection_state = (short) 9;
                        return;
                    default:
                        throw new TlsFatalAlert((short) 10);
                }
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsProtocol
    protected void handleAlertWarningMessage(short alertDescription) throws IOException {
        super.handleAlertWarningMessage(alertDescription);
        switch (alertDescription) {
            case 41:
                if (TlsUtils.isSSL(getContext()) && this.certificateRequest != null) {
                    switch (this.connection_state) {
                        case 8:
                            this.tlsServer.processClientSupplementalData(null);
                            break;
                        case 9:
                            break;
                    }
                    notifyClientCertificate(Certificate.EMPTY_CHAIN);
                    this.connection_state = (short) 10;
                    return;
                }
                throw new TlsFatalAlert((short) 10);
            default:
                return;
        }
    }

    protected void notifyClientCertificate(Certificate clientCertificate) throws IOException {
        if (this.certificateRequest == null) {
            throw new IllegalStateException();
        }
        if (this.peerCertificate != null) {
            throw new TlsFatalAlert((short) 10);
        }
        this.peerCertificate = clientCertificate;
        if (clientCertificate.isEmpty()) {
            this.keyExchange.skipClientCredentials();
        } else {
            this.clientCertificateType = TlsUtils.getClientCertificateType(clientCertificate, this.serverCredentials.getCertificate());
            this.keyExchange.processClientCertificate(clientCertificate);
        }
        this.tlsServer.notifyClientCertificate(clientCertificate);
    }

    protected void receiveCertificateMessage(ByteArrayInputStream buf) throws IOException {
        Certificate clientCertificate = Certificate.parse(buf);
        assertEmpty(buf);
        notifyClientCertificate(clientCertificate);
    }

    protected void receiveCertificateVerifyMessage(ByteArrayInputStream buf) throws IOException {
        byte[] hash;
        if (this.certificateRequest == null) {
            throw new IllegalStateException();
        }
        DigitallySigned clientCertificateVerify = DigitallySigned.parse(getContext(), buf);
        assertEmpty(buf);
        try {
            SignatureAndHashAlgorithm signatureAlgorithm = clientCertificateVerify.getAlgorithm();
            if (TlsUtils.isTLSv12(getContext())) {
                TlsUtils.verifySupportedSignatureAlgorithm(this.certificateRequest.getSupportedSignatureAlgorithms(), signatureAlgorithm);
                hash = this.prepareFinishHash.getFinalHash(signatureAlgorithm.getHash());
            } else {
                hash = this.securityParameters.getSessionHash();
            }
            org.spongycastle.asn1.x509.Certificate x509Cert = this.peerCertificate.getCertificateAt(0);
            SubjectPublicKeyInfo keyInfo = x509Cert.getSubjectPublicKeyInfo();
            AsymmetricKeyParameter publicKey = PublicKeyFactory.createKey(keyInfo);
            TlsSigner tlsSigner = TlsUtils.createTlsSigner(this.clientCertificateType);
            tlsSigner.init(getContext());
            if (!tlsSigner.verifyRawSignature(signatureAlgorithm, clientCertificateVerify.getSignature(), publicKey, hash)) {
                throw new TlsFatalAlert((short) 51);
            }
        } catch (TlsFatalAlert e) {
            throw e;
        } catch (Exception e2) {
            throw new TlsFatalAlert((short) 51, e2);
        }
    }

    protected void receiveClientHelloMessage(ByteArrayInputStream buf) throws IOException {
        ProtocolVersion client_version = TlsUtils.readVersion(buf);
        this.recordStream.setWriteVersion(client_version);
        if (client_version.isDTLS()) {
            throw new TlsFatalAlert((short) 47);
        }
        byte[] client_random = TlsUtils.readFully(32, buf);
        byte[] sessionID = TlsUtils.readOpaque8(buf);
        if (sessionID.length > 32) {
            throw new TlsFatalAlert((short) 47);
        }
        int cipher_suites_length = TlsUtils.readUint16(buf);
        if (cipher_suites_length < 2 || (cipher_suites_length & 1) != 0) {
            throw new TlsFatalAlert((short) 50);
        }
        this.offeredCipherSuites = TlsUtils.readUint16Array(cipher_suites_length / 2, buf);
        int compression_methods_length = TlsUtils.readUint8(buf);
        if (compression_methods_length < 1) {
            throw new TlsFatalAlert((short) 47);
        }
        this.offeredCompressionMethods = TlsUtils.readUint8Array(compression_methods_length, buf);
        this.clientExtensions = readExtensions(buf);
        this.securityParameters.extendedMasterSecret = TlsExtensionsUtils.hasExtendedMasterSecretExtension(this.clientExtensions);
        getContextAdmin().setClientVersion(client_version);
        this.tlsServer.notifyClientVersion(client_version);
        this.tlsServer.notifyFallback(Arrays.contains(this.offeredCipherSuites, CipherSuite.TLS_FALLBACK_SCSV));
        this.securityParameters.clientRandom = client_random;
        this.tlsServer.notifyOfferedCipherSuites(this.offeredCipherSuites);
        this.tlsServer.notifyOfferedCompressionMethods(this.offeredCompressionMethods);
        if (Arrays.contains(this.offeredCipherSuites, 255)) {
            this.secure_renegotiation = true;
        }
        byte[] renegExtData = TlsUtils.getExtensionData(this.clientExtensions, EXT_RenegotiationInfo);
        if (renegExtData != null) {
            this.secure_renegotiation = true;
            if (!Arrays.constantTimeAreEqual(renegExtData, createRenegotiationInfo(TlsUtils.EMPTY_BYTES))) {
                throw new TlsFatalAlert((short) 40);
            }
        }
        this.tlsServer.notifySecureRenegotiation(this.secure_renegotiation);
        if (this.clientExtensions != null) {
            TlsExtensionsUtils.getPaddingExtension(this.clientExtensions);
            this.tlsServer.processClientExtensions(this.clientExtensions);
        }
    }

    protected void receiveClientKeyExchangeMessage(ByteArrayInputStream buf) throws IOException {
        this.keyExchange.processClientKeyExchange(buf);
        assertEmpty(buf);
        if (TlsUtils.isSSL(getContext())) {
            establishMasterSecret(getContext(), this.keyExchange);
        }
        this.prepareFinishHash = this.recordStream.prepareToFinish();
        this.securityParameters.sessionHash = getCurrentPRFHash(getContext(), this.prepareFinishHash, null);
        if (!TlsUtils.isSSL(getContext())) {
            establishMasterSecret(getContext(), this.keyExchange);
        }
        this.recordStream.setPendingConnectionState(getPeer().getCompression(), getPeer().getCipher());
        if (!this.expectSessionTicket) {
            sendChangeCipherSpecMessage();
        }
    }

    protected void sendCertificateRequestMessage(CertificateRequest certificateRequest) throws IOException {
        TlsProtocol.HandshakeMessage message = new TlsProtocol.HandshakeMessage(this, (short) 13);
        certificateRequest.encode(message);
        message.writeToRecordStream();
    }

    protected void sendCertificateStatusMessage(CertificateStatus certificateStatus) throws IOException {
        TlsProtocol.HandshakeMessage message = new TlsProtocol.HandshakeMessage(this, (short) 22);
        certificateStatus.encode(message);
        message.writeToRecordStream();
    }

    protected void sendNewSessionTicketMessage(NewSessionTicket newSessionTicket) throws IOException {
        if (newSessionTicket == null) {
            throw new TlsFatalAlert((short) 80);
        }
        TlsProtocol.HandshakeMessage message = new TlsProtocol.HandshakeMessage(this, (short) 4);
        newSessionTicket.encode(message);
        message.writeToRecordStream();
    }

    protected void sendServerHelloMessage() throws IOException {
        TlsProtocol.HandshakeMessage message = new TlsProtocol.HandshakeMessage(this, (short) 2);
        ProtocolVersion server_version = this.tlsServer.getServerVersion();
        if (!server_version.isEqualOrEarlierVersionOf(getContext().getClientVersion())) {
            throw new TlsFatalAlert((short) 80);
        }
        this.recordStream.setReadVersion(server_version);
        this.recordStream.setWriteVersion(server_version);
        this.recordStream.setRestrictReadVersion(true);
        getContextAdmin().setServerVersion(server_version);
        TlsUtils.writeVersion(server_version, message);
        message.write(this.securityParameters.serverRandom);
        TlsUtils.writeOpaque8(TlsUtils.EMPTY_BYTES, message);
        int selectedCipherSuite = this.tlsServer.getSelectedCipherSuite();
        if (!Arrays.contains(this.offeredCipherSuites, selectedCipherSuite) || selectedCipherSuite == 0 || CipherSuite.isSCSV(selectedCipherSuite) || !TlsUtils.isValidCipherSuiteForVersion(selectedCipherSuite, getContext().getServerVersion())) {
            throw new TlsFatalAlert((short) 80);
        }
        this.securityParameters.cipherSuite = selectedCipherSuite;
        short selectedCompressionMethod = this.tlsServer.getSelectedCompressionMethod();
        if (!Arrays.contains(this.offeredCompressionMethods, selectedCompressionMethod)) {
            throw new TlsFatalAlert((short) 80);
        }
        this.securityParameters.compressionAlgorithm = selectedCompressionMethod;
        TlsUtils.writeUint16(selectedCipherSuite, message);
        TlsUtils.writeUint8(selectedCompressionMethod, (OutputStream) message);
        this.serverExtensions = this.tlsServer.getServerExtensions();
        if (this.secure_renegotiation) {
            byte[] renegExtData = TlsUtils.getExtensionData(this.serverExtensions, EXT_RenegotiationInfo);
            boolean noRenegExt = null == renegExtData;
            if (noRenegExt) {
                this.serverExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(this.serverExtensions);
                this.serverExtensions.put(EXT_RenegotiationInfo, createRenegotiationInfo(TlsUtils.EMPTY_BYTES));
            }
        }
        if (this.securityParameters.extendedMasterSecret) {
            this.serverExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(this.serverExtensions);
            TlsExtensionsUtils.addExtendedMasterSecretExtension(this.serverExtensions);
        }
        if (this.serverExtensions != null) {
            this.securityParameters.encryptThenMAC = TlsExtensionsUtils.hasEncryptThenMACExtension(this.serverExtensions);
            this.securityParameters.maxFragmentLength = processMaxFragmentLengthExtension(this.clientExtensions, this.serverExtensions, (short) 80);
            this.securityParameters.truncatedHMac = TlsExtensionsUtils.hasTruncatedHMacExtension(this.serverExtensions);
            this.allowCertificateStatus = !this.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(this.serverExtensions, TlsExtensionsUtils.EXT_status_request, (short) 80);
            this.expectSessionTicket = !this.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(this.serverExtensions, TlsProtocol.EXT_SessionTicket, (short) 80);
            writeExtensions(message, this.serverExtensions);
        }
        this.securityParameters.prfAlgorithm = getPRFAlgorithm(getContext(), this.securityParameters.getCipherSuite());
        this.securityParameters.verifyDataLength = 12;
        applyMaxFragmentLengthExtension();
        message.writeToRecordStream();
    }

    protected void sendServerHelloDoneMessage() throws IOException {
        byte[] message = new byte[4];
        TlsUtils.writeUint8((short) 14, message, 0);
        TlsUtils.writeUint24(0, message, 1);
        writeHandshakeMessage(message, 0, message.length);
    }

    protected void sendServerKeyExchangeMessage(byte[] serverKeyExchange) throws IOException {
        TlsProtocol.HandshakeMessage message = new TlsProtocol.HandshakeMessage((short) 12, serverKeyExchange.length);
        message.write(serverKeyExchange);
        message.writeToRecordStream();
    }

    protected boolean expectCertificateVerifyMessage() {
        return this.clientCertificateType >= 0 && TlsUtils.hasSigningCapability(this.clientCertificateType);
    }
}
