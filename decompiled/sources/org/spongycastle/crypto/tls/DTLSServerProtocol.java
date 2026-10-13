package org.spongycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.Vector;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.util.PublicKeyFactory;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/DTLSServerProtocol.class */
public class DTLSServerProtocol extends DTLSProtocol {
    protected boolean verifyRequests;

    public DTLSServerProtocol(SecureRandom secureRandom) {
        super(secureRandom);
        this.verifyRequests = true;
    }

    public boolean getVerifyRequests() {
        return this.verifyRequests;
    }

    public void setVerifyRequests(boolean verifyRequests) {
        this.verifyRequests = verifyRequests;
    }

    public DTLSTransport accept(TlsServer server, DatagramTransport transport) throws IOException {
        if (server == null) {
            throw new IllegalArgumentException("'server' cannot be null");
        }
        if (transport == null) {
            throw new IllegalArgumentException("'transport' cannot be null");
        }
        SecurityParameters securityParameters = new SecurityParameters();
        securityParameters.entity = 0;
        ServerHandshakeState state = new ServerHandshakeState();
        state.server = server;
        state.serverContext = new TlsServerContextImpl(this.secureRandom, securityParameters);
        securityParameters.serverRandom = TlsProtocol.createRandomBlock(server.shouldUseGMTUnixTime(), state.serverContext.getNonceRandomGenerator());
        server.init(state.serverContext);
        DTLSRecordLayer recordLayer = new DTLSRecordLayer(transport, state.serverContext, server, (short) 22);
        try {
            try {
                try {
                    try {
                        DTLSTransport dTLSTransportServerHandshake = serverHandshake(state, recordLayer);
                        securityParameters.clear();
                        return dTLSTransportServerHandshake;
                    } catch (RuntimeException e) {
                        abortServerHandshake(state, recordLayer, (short) 80);
                        throw new TlsFatalAlert((short) 80, e);
                    }
                } catch (TlsFatalAlert fatalAlert) {
                    abortServerHandshake(state, recordLayer, fatalAlert.getAlertDescription());
                    throw fatalAlert;
                }
            } catch (IOException e2) {
                abortServerHandshake(state, recordLayer, (short) 80);
                throw e2;
            }
        } catch (Throwable th) {
            securityParameters.clear();
            throw th;
        }
    }

    protected void abortServerHandshake(ServerHandshakeState state, DTLSRecordLayer recordLayer, short alertDescription) {
        recordLayer.fail(alertDescription);
        invalidateSession(state);
    }

    protected DTLSTransport serverHandshake(ServerHandshakeState state, DTLSRecordLayer recordLayer) throws IOException {
        CertificateStatus certificateStatus;
        SecurityParameters securityParameters = state.serverContext.getSecurityParameters();
        DTLSReliableHandshake handshake = new DTLSReliableHandshake(state.serverContext, recordLayer);
        DTLSReliableHandshake.Message clientMessage = handshake.receiveMessage();
        if (clientMessage.getType() == 1) {
            processClientHello(state, clientMessage.getBody());
            byte[] serverHelloBody = generateServerHello(state);
            applyMaxFragmentLengthExtension(recordLayer, securityParameters.maxFragmentLength);
            ProtocolVersion recordLayerVersion = state.serverContext.getServerVersion();
            recordLayer.setReadVersion(recordLayerVersion);
            recordLayer.setWriteVersion(recordLayerVersion);
            handshake.sendMessage((short) 2, serverHelloBody);
            handshake.notifyHelloComplete();
            Vector serverSupplementalData = state.server.getServerSupplementalData();
            if (serverSupplementalData != null) {
                byte[] supplementalDataBody = generateSupplementalData(serverSupplementalData);
                handshake.sendMessage((short) 23, supplementalDataBody);
            }
            state.keyExchange = state.server.getKeyExchange();
            state.keyExchange.init(state.serverContext);
            state.serverCredentials = state.server.getCredentials();
            Certificate serverCertificate = null;
            if (state.serverCredentials == null) {
                state.keyExchange.skipServerCredentials();
            } else {
                state.keyExchange.processServerCredentials(state.serverCredentials);
                serverCertificate = state.serverCredentials.getCertificate();
                byte[] certificateBody = generateCertificate(serverCertificate);
                handshake.sendMessage((short) 11, certificateBody);
            }
            if (serverCertificate == null || serverCertificate.isEmpty()) {
                state.allowCertificateStatus = false;
            }
            if (state.allowCertificateStatus && (certificateStatus = state.server.getCertificateStatus()) != null) {
                byte[] certificateStatusBody = generateCertificateStatus(state, certificateStatus);
                handshake.sendMessage((short) 22, certificateStatusBody);
            }
            byte[] serverKeyExchange = state.keyExchange.generateServerKeyExchange();
            if (serverKeyExchange != null) {
                handshake.sendMessage((short) 12, serverKeyExchange);
            }
            if (state.serverCredentials != null) {
                state.certificateRequest = state.server.getCertificateRequest();
                if (state.certificateRequest != null) {
                    if (TlsUtils.isTLSv12(state.serverContext) != (state.certificateRequest.getSupportedSignatureAlgorithms() != null)) {
                        throw new TlsFatalAlert((short) 80);
                    }
                    state.keyExchange.validateCertificateRequest(state.certificateRequest);
                    byte[] certificateRequestBody = generateCertificateRequest(state, state.certificateRequest);
                    handshake.sendMessage((short) 13, certificateRequestBody);
                    TlsUtils.trackHashAlgorithms(handshake.getHandshakeHash(), state.certificateRequest.getSupportedSignatureAlgorithms());
                }
            }
            handshake.sendMessage((short) 14, TlsUtils.EMPTY_BYTES);
            handshake.getHandshakeHash().sealHashAlgorithms();
            DTLSReliableHandshake.Message clientMessage2 = handshake.receiveMessage();
            if (clientMessage2.getType() == 23) {
                processClientSupplementalData(state, clientMessage2.getBody());
                clientMessage2 = handshake.receiveMessage();
            } else {
                state.server.processClientSupplementalData(null);
            }
            if (state.certificateRequest == null) {
                state.keyExchange.skipClientCredentials();
            } else if (clientMessage2.getType() == 11) {
                processClientCertificate(state, clientMessage2.getBody());
                clientMessage2 = handshake.receiveMessage();
            } else {
                if (TlsUtils.isTLSv12(state.serverContext)) {
                    throw new TlsFatalAlert((short) 10);
                }
                notifyClientCertificate(state, Certificate.EMPTY_CHAIN);
            }
            if (clientMessage2.getType() == 16) {
                processClientKeyExchange(state, clientMessage2.getBody());
                TlsHandshakeHash prepareFinishHash = handshake.prepareToFinish();
                securityParameters.sessionHash = TlsProtocol.getCurrentPRFHash(state.serverContext, prepareFinishHash, null);
                TlsProtocol.establishMasterSecret(state.serverContext, state.keyExchange);
                recordLayer.initPendingEpoch(state.server.getCipher());
                if (expectCertificateVerifyMessage(state)) {
                    byte[] certificateVerifyBody = handshake.receiveMessageBody((short) 15);
                    processCertificateVerify(state, certificateVerifyBody, prepareFinishHash);
                }
                byte[] expectedClientVerifyData = TlsUtils.calculateVerifyData(state.serverContext, ExporterLabel.client_finished, TlsProtocol.getCurrentPRFHash(state.serverContext, handshake.getHandshakeHash(), null));
                processFinished(handshake.receiveMessageBody((short) 20), expectedClientVerifyData);
                if (state.expectSessionTicket) {
                    NewSessionTicket newSessionTicket = state.server.getNewSessionTicket();
                    byte[] newSessionTicketBody = generateNewSessionTicket(state, newSessionTicket);
                    handshake.sendMessage((short) 4, newSessionTicketBody);
                }
                byte[] serverVerifyData = TlsUtils.calculateVerifyData(state.serverContext, ExporterLabel.server_finished, TlsProtocol.getCurrentPRFHash(state.serverContext, handshake.getHandshakeHash(), null));
                handshake.sendMessage((short) 20, serverVerifyData);
                handshake.finish();
                state.server.notifyHandshakeComplete();
                return new DTLSTransport(recordLayer);
            }
            throw new TlsFatalAlert((short) 10);
        }
        throw new TlsFatalAlert((short) 10);
    }

    protected byte[] generateCertificateRequest(ServerHandshakeState state, CertificateRequest certificateRequest) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        certificateRequest.encode(buf);
        return buf.toByteArray();
    }

    protected byte[] generateCertificateStatus(ServerHandshakeState state, CertificateStatus certificateStatus) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        certificateStatus.encode(buf);
        return buf.toByteArray();
    }

    protected byte[] generateNewSessionTicket(ServerHandshakeState state, NewSessionTicket newSessionTicket) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        newSessionTicket.encode(buf);
        return buf.toByteArray();
    }

    protected byte[] generateServerHello(ServerHandshakeState state) throws IOException {
        SecurityParameters securityParameters = state.serverContext.getSecurityParameters();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ProtocolVersion server_version = state.server.getServerVersion();
        if (!server_version.isEqualOrEarlierVersionOf(state.serverContext.getClientVersion())) {
            throw new TlsFatalAlert((short) 80);
        }
        state.serverContext.setServerVersion(server_version);
        TlsUtils.writeVersion(state.serverContext.getServerVersion(), buf);
        buf.write(securityParameters.getServerRandom());
        TlsUtils.writeOpaque8(TlsUtils.EMPTY_BYTES, buf);
        int selectedCipherSuite = state.server.getSelectedCipherSuite();
        if (!Arrays.contains(state.offeredCipherSuites, selectedCipherSuite) || selectedCipherSuite == 0 || CipherSuite.isSCSV(selectedCipherSuite) || !TlsUtils.isValidCipherSuiteForVersion(selectedCipherSuite, state.serverContext.getServerVersion())) {
            throw new TlsFatalAlert((short) 80);
        }
        validateSelectedCipherSuite(selectedCipherSuite, (short) 80);
        securityParameters.cipherSuite = selectedCipherSuite;
        short selectedCompressionMethod = state.server.getSelectedCompressionMethod();
        if (!Arrays.contains(state.offeredCompressionMethods, selectedCompressionMethod)) {
            throw new TlsFatalAlert((short) 80);
        }
        securityParameters.compressionAlgorithm = selectedCompressionMethod;
        TlsUtils.writeUint16(selectedCipherSuite, buf);
        TlsUtils.writeUint8(selectedCompressionMethod, (OutputStream) buf);
        state.serverExtensions = state.server.getServerExtensions();
        if (state.secure_renegotiation) {
            byte[] renegExtData = TlsUtils.getExtensionData(state.serverExtensions, TlsProtocol.EXT_RenegotiationInfo);
            boolean noRenegExt = null == renegExtData;
            if (noRenegExt) {
                state.serverExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(state.serverExtensions);
                state.serverExtensions.put(TlsProtocol.EXT_RenegotiationInfo, TlsProtocol.createRenegotiationInfo(TlsUtils.EMPTY_BYTES));
            }
        }
        if (securityParameters.extendedMasterSecret) {
            state.serverExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(state.serverExtensions);
            TlsExtensionsUtils.addExtendedMasterSecretExtension(state.serverExtensions);
        }
        if (state.serverExtensions != null) {
            securityParameters.encryptThenMAC = TlsExtensionsUtils.hasEncryptThenMACExtension(state.serverExtensions);
            securityParameters.maxFragmentLength = evaluateMaxFragmentLengthExtension(state.resumedSession, state.clientExtensions, state.serverExtensions, (short) 80);
            securityParameters.truncatedHMac = TlsExtensionsUtils.hasTruncatedHMacExtension(state.serverExtensions);
            state.allowCertificateStatus = !state.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(state.serverExtensions, TlsExtensionsUtils.EXT_status_request, (short) 80);
            state.expectSessionTicket = !state.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(state.serverExtensions, TlsProtocol.EXT_SessionTicket, (short) 80);
            TlsProtocol.writeExtensions(buf, state.serverExtensions);
        }
        securityParameters.prfAlgorithm = TlsProtocol.getPRFAlgorithm(state.serverContext, securityParameters.getCipherSuite());
        securityParameters.verifyDataLength = 12;
        return buf.toByteArray();
    }

    protected void invalidateSession(ServerHandshakeState state) {
        if (state.sessionParameters != null) {
            state.sessionParameters.clear();
            state.sessionParameters = null;
        }
        if (state.tlsSession != null) {
            state.tlsSession.invalidate();
            state.tlsSession = null;
        }
    }

    protected void notifyClientCertificate(ServerHandshakeState state, Certificate clientCertificate) throws IOException {
        if (state.certificateRequest == null) {
            throw new IllegalStateException();
        }
        if (state.clientCertificate != null) {
            throw new TlsFatalAlert((short) 10);
        }
        state.clientCertificate = clientCertificate;
        if (clientCertificate.isEmpty()) {
            state.keyExchange.skipClientCredentials();
        } else {
            state.clientCertificateType = TlsUtils.getClientCertificateType(clientCertificate, state.serverCredentials.getCertificate());
            state.keyExchange.processClientCertificate(clientCertificate);
        }
        state.server.notifyClientCertificate(clientCertificate);
    }

    protected void processClientCertificate(ServerHandshakeState state, byte[] body) throws IOException {
        ByteArrayInputStream buf = new ByteArrayInputStream(body);
        Certificate clientCertificate = Certificate.parse(buf);
        TlsProtocol.assertEmpty(buf);
        notifyClientCertificate(state, clientCertificate);
    }

    protected void processCertificateVerify(ServerHandshakeState state, byte[] body, TlsHandshakeHash prepareFinishHash) throws IOException {
        byte[] hash;
        if (state.certificateRequest == null) {
            throw new IllegalStateException();
        }
        ByteArrayInputStream buf = new ByteArrayInputStream(body);
        TlsServerContextImpl context = state.serverContext;
        DigitallySigned clientCertificateVerify = DigitallySigned.parse(context, buf);
        TlsProtocol.assertEmpty(buf);
        try {
            SignatureAndHashAlgorithm signatureAlgorithm = clientCertificateVerify.getAlgorithm();
            if (TlsUtils.isTLSv12(context)) {
                TlsUtils.verifySupportedSignatureAlgorithm(state.certificateRequest.getSupportedSignatureAlgorithms(), signatureAlgorithm);
                hash = prepareFinishHash.getFinalHash(signatureAlgorithm.getHash());
            } else {
                hash = context.getSecurityParameters().getSessionHash();
            }
            org.spongycastle.asn1.x509.Certificate x509Cert = state.clientCertificate.getCertificateAt(0);
            SubjectPublicKeyInfo keyInfo = x509Cert.getSubjectPublicKeyInfo();
            AsymmetricKeyParameter publicKey = PublicKeyFactory.createKey(keyInfo);
            TlsSigner tlsSigner = TlsUtils.createTlsSigner(state.clientCertificateType);
            tlsSigner.init(context);
            if (!tlsSigner.verifyRawSignature(signatureAlgorithm, clientCertificateVerify.getSignature(), publicKey, hash)) {
                throw new TlsFatalAlert((short) 51);
            }
        } catch (TlsFatalAlert e) {
            throw e;
        } catch (Exception e2) {
            throw new TlsFatalAlert((short) 51, e2);
        }
    }

    protected void processClientHello(ServerHandshakeState state, byte[] body) throws IOException {
        ByteArrayInputStream buf = new ByteArrayInputStream(body);
        ProtocolVersion client_version = TlsUtils.readVersion(buf);
        if (!client_version.isDTLS()) {
            throw new TlsFatalAlert((short) 47);
        }
        byte[] client_random = TlsUtils.readFully(32, buf);
        byte[] sessionID = TlsUtils.readOpaque8(buf);
        if (sessionID.length > 32) {
            throw new TlsFatalAlert((short) 47);
        }
        TlsUtils.readOpaque8(buf);
        int cipher_suites_length = TlsUtils.readUint16(buf);
        if (cipher_suites_length < 2 || (cipher_suites_length & 1) != 0) {
            throw new TlsFatalAlert((short) 50);
        }
        state.offeredCipherSuites = TlsUtils.readUint16Array(cipher_suites_length / 2, buf);
        int compression_methods_length = TlsUtils.readUint8(buf);
        if (compression_methods_length < 1) {
            throw new TlsFatalAlert((short) 47);
        }
        state.offeredCompressionMethods = TlsUtils.readUint8Array(compression_methods_length, buf);
        state.clientExtensions = TlsProtocol.readExtensions(buf);
        TlsServerContextImpl context = state.serverContext;
        SecurityParameters securityParameters = context.getSecurityParameters();
        securityParameters.extendedMasterSecret = TlsExtensionsUtils.hasExtendedMasterSecretExtension(state.clientExtensions);
        context.setClientVersion(client_version);
        state.server.notifyClientVersion(client_version);
        state.server.notifyFallback(Arrays.contains(state.offeredCipherSuites, CipherSuite.TLS_FALLBACK_SCSV));
        securityParameters.clientRandom = client_random;
        state.server.notifyOfferedCipherSuites(state.offeredCipherSuites);
        state.server.notifyOfferedCompressionMethods(state.offeredCompressionMethods);
        if (Arrays.contains(state.offeredCipherSuites, 255)) {
            state.secure_renegotiation = true;
        }
        byte[] renegExtData = TlsUtils.getExtensionData(state.clientExtensions, TlsProtocol.EXT_RenegotiationInfo);
        if (renegExtData != null) {
            state.secure_renegotiation = true;
            if (!Arrays.constantTimeAreEqual(renegExtData, TlsProtocol.createRenegotiationInfo(TlsUtils.EMPTY_BYTES))) {
                throw new TlsFatalAlert((short) 40);
            }
        }
        state.server.notifySecureRenegotiation(state.secure_renegotiation);
        if (state.clientExtensions != null) {
            TlsExtensionsUtils.getPaddingExtension(state.clientExtensions);
            state.server.processClientExtensions(state.clientExtensions);
        }
    }

    protected void processClientKeyExchange(ServerHandshakeState state, byte[] body) throws IOException {
        ByteArrayInputStream buf = new ByteArrayInputStream(body);
        state.keyExchange.processClientKeyExchange(buf);
        TlsProtocol.assertEmpty(buf);
    }

    protected void processClientSupplementalData(ServerHandshakeState state, byte[] body) throws IOException {
        ByteArrayInputStream buf = new ByteArrayInputStream(body);
        Vector clientSupplementalData = TlsProtocol.readSupplementalDataMessage(buf);
        state.server.processClientSupplementalData(clientSupplementalData);
    }

    protected boolean expectCertificateVerifyMessage(ServerHandshakeState state) {
        return state.clientCertificateType >= 0 && TlsUtils.hasSigningCapability(state.clientCertificateType);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/DTLSServerProtocol$ServerHandshakeState.class */
    protected static class ServerHandshakeState {
        TlsServer server = null;
        TlsServerContextImpl serverContext = null;
        TlsSession tlsSession = null;
        SessionParameters sessionParameters = null;
        SessionParameters.Builder sessionParametersBuilder = null;
        int[] offeredCipherSuites = null;
        short[] offeredCompressionMethods = null;
        Hashtable clientExtensions = null;
        Hashtable serverExtensions = null;
        boolean resumedSession = false;
        boolean secure_renegotiation = false;
        boolean allowCertificateStatus = false;
        boolean expectSessionTicket = false;
        TlsKeyExchange keyExchange = null;
        TlsCredentials serverCredentials = null;
        CertificateRequest certificateRequest = null;
        short clientCertificateType = -1;
        Certificate clientCertificate = null;

        protected ServerHandshakeState() {
        }
    }
}
