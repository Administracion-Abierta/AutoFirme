package org.spongycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/DTLSClientProtocol.class */
public class DTLSClientProtocol extends DTLSProtocol {
    public DTLSClientProtocol(SecureRandom secureRandom) {
        super(secureRandom);
    }

    public DTLSTransport connect(TlsClient client, DatagramTransport transport) throws IOException {
        SessionParameters sessionParameters;
        if (client == null) {
            throw new IllegalArgumentException("'client' cannot be null");
        }
        if (transport == null) {
            throw new IllegalArgumentException("'transport' cannot be null");
        }
        SecurityParameters securityParameters = new SecurityParameters();
        securityParameters.entity = 1;
        ClientHandshakeState state = new ClientHandshakeState();
        state.client = client;
        state.clientContext = new TlsClientContextImpl(this.secureRandom, securityParameters);
        securityParameters.clientRandom = TlsProtocol.createRandomBlock(client.shouldUseGMTUnixTime(), state.clientContext.getNonceRandomGenerator());
        client.init(state.clientContext);
        DTLSRecordLayer recordLayer = new DTLSRecordLayer(transport, state.clientContext, client, (short) 22);
        TlsSession sessionToResume = state.client.getSessionToResume();
        if (sessionToResume != null && sessionToResume.isResumable() && (sessionParameters = sessionToResume.exportSessionParameters()) != null) {
            state.tlsSession = sessionToResume;
            state.sessionParameters = sessionParameters;
        }
        try {
            try {
                DTLSTransport dTLSTransportClientHandshake = clientHandshake(state, recordLayer);
                securityParameters.clear();
                return dTLSTransportClientHandshake;
            } catch (RuntimeException e) {
                abortClientHandshake(state, recordLayer, (short) 80);
                throw new TlsFatalAlert((short) 80, e);
            } catch (TlsFatalAlert fatalAlert) {
                abortClientHandshake(state, recordLayer, fatalAlert.getAlertDescription());
                throw fatalAlert;
            } catch (IOException e2) {
                abortClientHandshake(state, recordLayer, (short) 80);
                throw e2;
            }
        } catch (Throwable th) {
            securityParameters.clear();
            throw th;
        }
    }

    protected void abortClientHandshake(ClientHandshakeState state, DTLSRecordLayer recordLayer, short alertDescription) {
        recordLayer.fail(alertDescription);
        invalidateSession(state);
    }

    protected DTLSTransport clientHandshake(ClientHandshakeState state, DTLSRecordLayer recordLayer) throws IOException {
        byte[] hash;
        SecurityParameters securityParameters = state.clientContext.getSecurityParameters();
        DTLSReliableHandshake handshake = new DTLSReliableHandshake(state.clientContext, recordLayer);
        byte[] clientHelloBody = generateClientHello(state, state.client);
        recordLayer.setWriteVersion(ProtocolVersion.DTLSv10);
        handshake.sendMessage((short) 1, clientHelloBody);
        DTLSReliableHandshake.Message messageReceiveMessage = handshake.receiveMessage();
        while (true) {
            DTLSReliableHandshake.Message serverMessage = messageReceiveMessage;
            if (serverMessage.getType() == 3) {
                ProtocolVersion recordLayerVersion = recordLayer.getReadVersion();
                ProtocolVersion client_version = state.clientContext.getClientVersion();
                if (!recordLayerVersion.isEqualOrEarlierVersionOf(client_version)) {
                    throw new TlsFatalAlert((short) 47);
                }
                recordLayer.setReadVersion(null);
                byte[] cookie = processHelloVerifyRequest(state, serverMessage.getBody());
                byte[] patched = patchClientHelloWithCookie(clientHelloBody, cookie);
                handshake.resetHandshakeMessagesDigest();
                handshake.sendMessage((short) 1, patched);
                messageReceiveMessage = handshake.receiveMessage();
            } else {
                if (serverMessage.getType() == 2) {
                    ProtocolVersion recordLayerVersion2 = recordLayer.getReadVersion();
                    reportServerVersion(state, recordLayerVersion2);
                    recordLayer.setWriteVersion(recordLayerVersion2);
                    processServerHello(state, serverMessage.getBody());
                    handshake.notifyHelloComplete();
                    applyMaxFragmentLengthExtension(recordLayer, securityParameters.maxFragmentLength);
                    if (state.resumedSession) {
                        securityParameters.masterSecret = Arrays.clone(state.sessionParameters.getMasterSecret());
                        recordLayer.initPendingEpoch(state.client.getCipher());
                        byte[] expectedServerVerifyData = TlsUtils.calculateVerifyData(state.clientContext, ExporterLabel.server_finished, TlsProtocol.getCurrentPRFHash(state.clientContext, handshake.getHandshakeHash(), null));
                        processFinished(handshake.receiveMessageBody((short) 20), expectedServerVerifyData);
                        byte[] clientVerifyData = TlsUtils.calculateVerifyData(state.clientContext, ExporterLabel.client_finished, TlsProtocol.getCurrentPRFHash(state.clientContext, handshake.getHandshakeHash(), null));
                        handshake.sendMessage((short) 20, clientVerifyData);
                        handshake.finish();
                        state.clientContext.setResumableSession(state.tlsSession);
                        state.client.notifyHandshakeComplete();
                        return new DTLSTransport(recordLayer);
                    }
                    invalidateSession(state);
                    if (state.selectedSessionID.length > 0) {
                        state.tlsSession = new TlsSessionImpl(state.selectedSessionID, null);
                    }
                    DTLSReliableHandshake.Message serverMessage2 = handshake.receiveMessage();
                    if (serverMessage2.getType() == 23) {
                        processServerSupplementalData(state, serverMessage2.getBody());
                        serverMessage2 = handshake.receiveMessage();
                    } else {
                        state.client.processServerSupplementalData(null);
                    }
                    state.keyExchange = state.client.getKeyExchange();
                    state.keyExchange.init(state.clientContext);
                    Certificate serverCertificate = null;
                    if (serverMessage2.getType() == 11) {
                        serverCertificate = processServerCertificate(state, serverMessage2.getBody());
                        serverMessage2 = handshake.receiveMessage();
                    } else {
                        state.keyExchange.skipServerCredentials();
                    }
                    if (serverCertificate == null || serverCertificate.isEmpty()) {
                        state.allowCertificateStatus = false;
                    }
                    if (serverMessage2.getType() == 22) {
                        processCertificateStatus(state, serverMessage2.getBody());
                        serverMessage2 = handshake.receiveMessage();
                    }
                    if (serverMessage2.getType() == 12) {
                        processServerKeyExchange(state, serverMessage2.getBody());
                        serverMessage2 = handshake.receiveMessage();
                    } else {
                        state.keyExchange.skipServerKeyExchange();
                    }
                    if (serverMessage2.getType() == 13) {
                        processCertificateRequest(state, serverMessage2.getBody());
                        TlsUtils.trackHashAlgorithms(handshake.getHandshakeHash(), state.certificateRequest.getSupportedSignatureAlgorithms());
                        serverMessage2 = handshake.receiveMessage();
                    }
                    if (serverMessage2.getType() == 14) {
                        if (serverMessage2.getBody().length != 0) {
                            throw new TlsFatalAlert((short) 50);
                        }
                        handshake.getHandshakeHash().sealHashAlgorithms();
                        Vector clientSupplementalData = state.client.getClientSupplementalData();
                        if (clientSupplementalData != null) {
                            byte[] supplementalDataBody = generateSupplementalData(clientSupplementalData);
                            handshake.sendMessage((short) 23, supplementalDataBody);
                        }
                        if (state.certificateRequest != null) {
                            state.clientCredentials = state.authentication.getClientCredentials(state.certificateRequest);
                            Certificate clientCertificate = null;
                            if (state.clientCredentials != null) {
                                clientCertificate = state.clientCredentials.getCertificate();
                            }
                            if (clientCertificate == null) {
                                clientCertificate = Certificate.EMPTY_CHAIN;
                            }
                            byte[] certificateBody = generateCertificate(clientCertificate);
                            handshake.sendMessage((short) 11, certificateBody);
                        }
                        if (state.clientCredentials != null) {
                            state.keyExchange.processClientCredentials(state.clientCredentials);
                        } else {
                            state.keyExchange.skipClientCredentials();
                        }
                        byte[] clientKeyExchangeBody = generateClientKeyExchange(state);
                        handshake.sendMessage((short) 16, clientKeyExchangeBody);
                        TlsHandshakeHash prepareFinishHash = handshake.prepareToFinish();
                        securityParameters.sessionHash = TlsProtocol.getCurrentPRFHash(state.clientContext, prepareFinishHash, null);
                        TlsProtocol.establishMasterSecret(state.clientContext, state.keyExchange);
                        recordLayer.initPendingEpoch(state.client.getCipher());
                        if (state.clientCredentials != null && (state.clientCredentials instanceof TlsSignerCredentials)) {
                            TlsSignerCredentials signerCredentials = (TlsSignerCredentials) state.clientCredentials;
                            SignatureAndHashAlgorithm signatureAndHashAlgorithm = TlsUtils.getSignatureAndHashAlgorithm(state.clientContext, signerCredentials);
                            if (signatureAndHashAlgorithm == null) {
                                hash = securityParameters.getSessionHash();
                            } else {
                                hash = prepareFinishHash.getFinalHash(signatureAndHashAlgorithm.getHash());
                            }
                            byte[] signature = signerCredentials.generateCertificateSignature(hash);
                            DigitallySigned certificateVerify = new DigitallySigned(signatureAndHashAlgorithm, signature);
                            byte[] certificateVerifyBody = generateCertificateVerify(state, certificateVerify);
                            handshake.sendMessage((short) 15, certificateVerifyBody);
                        }
                        byte[] clientVerifyData2 = TlsUtils.calculateVerifyData(state.clientContext, ExporterLabel.client_finished, TlsProtocol.getCurrentPRFHash(state.clientContext, handshake.getHandshakeHash(), null));
                        handshake.sendMessage((short) 20, clientVerifyData2);
                        if (state.expectSessionTicket) {
                            DTLSReliableHandshake.Message serverMessage3 = handshake.receiveMessage();
                            if (serverMessage3.getType() == 4) {
                                processNewSessionTicket(state, serverMessage3.getBody());
                            } else {
                                throw new TlsFatalAlert((short) 10);
                            }
                        }
                        byte[] expectedServerVerifyData2 = TlsUtils.calculateVerifyData(state.clientContext, ExporterLabel.server_finished, TlsProtocol.getCurrentPRFHash(state.clientContext, handshake.getHandshakeHash(), null));
                        processFinished(handshake.receiveMessageBody((short) 20), expectedServerVerifyData2);
                        handshake.finish();
                        if (state.tlsSession != null) {
                            state.sessionParameters = new SessionParameters.Builder().setCipherSuite(securityParameters.getCipherSuite()).setCompressionAlgorithm(securityParameters.getCompressionAlgorithm()).setMasterSecret(securityParameters.getMasterSecret()).setPeerCertificate(serverCertificate).setPSKIdentity(securityParameters.getPSKIdentity()).setSRPIdentity(securityParameters.getSRPIdentity()).setServerExtensions(state.serverExtensions).build();
                            state.tlsSession = TlsUtils.importSession(state.tlsSession.getSessionID(), state.sessionParameters);
                            state.clientContext.setResumableSession(state.tlsSession);
                        }
                        state.client.notifyHandshakeComplete();
                        return new DTLSTransport(recordLayer);
                    }
                    throw new TlsFatalAlert((short) 10);
                }
                throw new TlsFatalAlert((short) 10);
            }
        }
    }

    protected byte[] generateCertificateVerify(ClientHandshakeState state, DigitallySigned certificateVerify) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        certificateVerify.encode(buf);
        return buf.toByteArray();
    }

    protected byte[] generateClientHello(ClientHandshakeState state, TlsClient client) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ProtocolVersion client_version = client.getClientVersion();
        if (!client_version.isDTLS()) {
            throw new TlsFatalAlert((short) 80);
        }
        TlsClientContextImpl context = state.clientContext;
        context.setClientVersion(client_version);
        TlsUtils.writeVersion(client_version, buf);
        SecurityParameters securityParameters = context.getSecurityParameters();
        buf.write(securityParameters.getClientRandom());
        byte[] session_id = TlsUtils.EMPTY_BYTES;
        if (state.tlsSession != null) {
            session_id = state.tlsSession.getSessionID();
            if (session_id == null || session_id.length > 32) {
                session_id = TlsUtils.EMPTY_BYTES;
            }
        }
        TlsUtils.writeOpaque8(session_id, buf);
        TlsUtils.writeOpaque8(TlsUtils.EMPTY_BYTES, buf);
        boolean fallback = client.isFallback();
        state.offeredCipherSuites = client.getCipherSuites();
        state.clientExtensions = client.getClientExtensions();
        byte[] renegExtData = TlsUtils.getExtensionData(state.clientExtensions, TlsProtocol.EXT_RenegotiationInfo);
        boolean noRenegExt = null == renegExtData;
        boolean noRenegSCSV = !Arrays.contains(state.offeredCipherSuites, 255);
        if (noRenegExt && noRenegSCSV) {
            state.offeredCipherSuites = Arrays.append(state.offeredCipherSuites, 255);
        }
        if (fallback && !Arrays.contains(state.offeredCipherSuites, CipherSuite.TLS_FALLBACK_SCSV)) {
            state.offeredCipherSuites = Arrays.append(state.offeredCipherSuites, CipherSuite.TLS_FALLBACK_SCSV);
        }
        TlsUtils.writeUint16ArrayWithUint16Length(state.offeredCipherSuites, buf);
        state.offeredCompressionMethods = new short[]{0};
        TlsUtils.writeUint8ArrayWithUint8Length(state.offeredCompressionMethods, buf);
        if (state.clientExtensions != null) {
            TlsProtocol.writeExtensions(buf, state.clientExtensions);
        }
        return buf.toByteArray();
    }

    protected byte[] generateClientKeyExchange(ClientHandshakeState state) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        state.keyExchange.generateClientKeyExchange(buf);
        return buf.toByteArray();
    }

    protected void invalidateSession(ClientHandshakeState state) {
        if (state.sessionParameters != null) {
            state.sessionParameters.clear();
            state.sessionParameters = null;
        }
        if (state.tlsSession != null) {
            state.tlsSession.invalidate();
            state.tlsSession = null;
        }
    }

    protected void processCertificateRequest(ClientHandshakeState state, byte[] body) throws IOException {
        if (state.authentication == null) {
            throw new TlsFatalAlert((short) 40);
        }
        ByteArrayInputStream buf = new ByteArrayInputStream(body);
        state.certificateRequest = CertificateRequest.parse(state.clientContext, buf);
        TlsProtocol.assertEmpty(buf);
        state.keyExchange.validateCertificateRequest(state.certificateRequest);
    }

    protected void processCertificateStatus(ClientHandshakeState state, byte[] body) throws IOException {
        if (!state.allowCertificateStatus) {
            throw new TlsFatalAlert((short) 10);
        }
        ByteArrayInputStream buf = new ByteArrayInputStream(body);
        state.certificateStatus = CertificateStatus.parse(buf);
        TlsProtocol.assertEmpty(buf);
    }

    protected byte[] processHelloVerifyRequest(ClientHandshakeState state, byte[] body) throws IOException {
        ByteArrayInputStream buf = new ByteArrayInputStream(body);
        ProtocolVersion server_version = TlsUtils.readVersion(buf);
        byte[] cookie = TlsUtils.readOpaque8(buf);
        TlsProtocol.assertEmpty(buf);
        if (!server_version.isEqualOrEarlierVersionOf(state.clientContext.getClientVersion())) {
            throw new TlsFatalAlert((short) 47);
        }
        if (!ProtocolVersion.DTLSv12.isEqualOrEarlierVersionOf(server_version) && cookie.length > 32) {
            throw new TlsFatalAlert((short) 47);
        }
        return cookie;
    }

    protected void processNewSessionTicket(ClientHandshakeState state, byte[] body) throws IOException {
        ByteArrayInputStream buf = new ByteArrayInputStream(body);
        NewSessionTicket newSessionTicket = NewSessionTicket.parse(buf);
        TlsProtocol.assertEmpty(buf);
        state.client.notifyNewSessionTicket(newSessionTicket);
    }

    protected Certificate processServerCertificate(ClientHandshakeState state, byte[] body) throws IOException {
        ByteArrayInputStream buf = new ByteArrayInputStream(body);
        Certificate serverCertificate = Certificate.parse(buf);
        TlsProtocol.assertEmpty(buf);
        state.keyExchange.processServerCertificate(serverCertificate);
        state.authentication = state.client.getAuthentication();
        state.authentication.notifyServerCertificate(serverCertificate);
        return serverCertificate;
    }

    protected void processServerHello(ClientHandshakeState state, byte[] body) throws IOException {
        SecurityParameters securityParameters = state.clientContext.getSecurityParameters();
        ByteArrayInputStream buf = new ByteArrayInputStream(body);
        ProtocolVersion server_version = TlsUtils.readVersion(buf);
        reportServerVersion(state, server_version);
        securityParameters.serverRandom = TlsUtils.readFully(32, buf);
        state.selectedSessionID = TlsUtils.readOpaque8(buf);
        if (state.selectedSessionID.length > 32) {
            throw new TlsFatalAlert((short) 47);
        }
        state.client.notifySessionID(state.selectedSessionID);
        state.resumedSession = state.selectedSessionID.length > 0 && state.tlsSession != null && Arrays.areEqual(state.selectedSessionID, state.tlsSession.getSessionID());
        int selectedCipherSuite = TlsUtils.readUint16(buf);
        if (!Arrays.contains(state.offeredCipherSuites, selectedCipherSuite) || selectedCipherSuite == 0 || CipherSuite.isSCSV(selectedCipherSuite) || !TlsUtils.isValidCipherSuiteForVersion(selectedCipherSuite, state.clientContext.getServerVersion())) {
            throw new TlsFatalAlert((short) 47);
        }
        validateSelectedCipherSuite(selectedCipherSuite, (short) 47);
        state.client.notifySelectedCipherSuite(selectedCipherSuite);
        short selectedCompressionMethod = TlsUtils.readUint8(buf);
        if (!Arrays.contains(state.offeredCompressionMethods, selectedCompressionMethod)) {
            throw new TlsFatalAlert((short) 47);
        }
        state.client.notifySelectedCompressionMethod(selectedCompressionMethod);
        state.serverExtensions = TlsProtocol.readExtensions(buf);
        if (state.serverExtensions != null) {
            Enumeration e = state.serverExtensions.keys();
            while (e.hasMoreElements()) {
                Integer extType = (Integer) e.nextElement();
                if (!extType.equals(TlsProtocol.EXT_RenegotiationInfo)) {
                    if (null == TlsUtils.getExtensionData(state.clientExtensions, extType)) {
                        throw new TlsFatalAlert((short) 110);
                    }
                    if (state.resumedSession) {
                    }
                }
            }
        }
        byte[] renegExtData = TlsUtils.getExtensionData(state.serverExtensions, TlsProtocol.EXT_RenegotiationInfo);
        if (renegExtData != null) {
            state.secure_renegotiation = true;
            if (!Arrays.constantTimeAreEqual(renegExtData, TlsProtocol.createRenegotiationInfo(TlsUtils.EMPTY_BYTES))) {
                throw new TlsFatalAlert((short) 40);
            }
        }
        state.client.notifySecureRenegotiation(state.secure_renegotiation);
        Hashtable sessionClientExtensions = state.clientExtensions;
        Hashtable sessionServerExtensions = state.serverExtensions;
        if (state.resumedSession) {
            if (selectedCipherSuite != state.sessionParameters.getCipherSuite() || selectedCompressionMethod != state.sessionParameters.getCompressionAlgorithm()) {
                throw new TlsFatalAlert((short) 47);
            }
            sessionClientExtensions = null;
            sessionServerExtensions = state.sessionParameters.readServerExtensions();
        }
        securityParameters.cipherSuite = selectedCipherSuite;
        securityParameters.compressionAlgorithm = selectedCompressionMethod;
        if (sessionServerExtensions != null) {
            boolean serverSentEncryptThenMAC = TlsExtensionsUtils.hasEncryptThenMACExtension(sessionServerExtensions);
            if (serverSentEncryptThenMAC && !TlsUtils.isBlockCipherSuite(securityParameters.getCipherSuite())) {
                throw new TlsFatalAlert((short) 47);
            }
            securityParameters.encryptThenMAC = serverSentEncryptThenMAC;
            securityParameters.extendedMasterSecret = TlsExtensionsUtils.hasExtendedMasterSecretExtension(sessionServerExtensions);
            securityParameters.maxFragmentLength = evaluateMaxFragmentLengthExtension(state.resumedSession, sessionClientExtensions, sessionServerExtensions, (short) 47);
            securityParameters.truncatedHMac = TlsExtensionsUtils.hasTruncatedHMacExtension(sessionServerExtensions);
            state.allowCertificateStatus = !state.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(sessionServerExtensions, TlsExtensionsUtils.EXT_status_request, (short) 47);
            state.expectSessionTicket = !state.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(sessionServerExtensions, TlsProtocol.EXT_SessionTicket, (short) 47);
        }
        if (sessionClientExtensions != null) {
            state.client.processServerExtensions(sessionServerExtensions);
        }
        securityParameters.prfAlgorithm = TlsProtocol.getPRFAlgorithm(state.clientContext, securityParameters.getCipherSuite());
        securityParameters.verifyDataLength = 12;
    }

    protected void processServerKeyExchange(ClientHandshakeState state, byte[] body) throws IOException {
        ByteArrayInputStream buf = new ByteArrayInputStream(body);
        state.keyExchange.processServerKeyExchange(buf);
        TlsProtocol.assertEmpty(buf);
    }

    protected void processServerSupplementalData(ClientHandshakeState state, byte[] body) throws IOException {
        ByteArrayInputStream buf = new ByteArrayInputStream(body);
        Vector serverSupplementalData = TlsProtocol.readSupplementalDataMessage(buf);
        state.client.processServerSupplementalData(serverSupplementalData);
    }

    protected void reportServerVersion(ClientHandshakeState state, ProtocolVersion server_version) throws IOException {
        TlsClientContextImpl clientContext = state.clientContext;
        ProtocolVersion currentServerVersion = clientContext.getServerVersion();
        if (null == currentServerVersion) {
            clientContext.setServerVersion(server_version);
            state.client.notifyServerVersion(server_version);
        } else if (!currentServerVersion.equals(server_version)) {
            throw new TlsFatalAlert((short) 47);
        }
    }

    protected static byte[] patchClientHelloWithCookie(byte[] clientHelloBody, byte[] cookie) throws IOException {
        int sessionIDLength = TlsUtils.readUint8(clientHelloBody, 34);
        int cookieLengthPos = 34 + 1 + sessionIDLength;
        int cookiePos = cookieLengthPos + 1;
        byte[] patched = new byte[clientHelloBody.length + cookie.length];
        System.arraycopy(clientHelloBody, 0, patched, 0, cookieLengthPos);
        TlsUtils.checkUint8(cookie.length);
        TlsUtils.writeUint8(cookie.length, patched, cookieLengthPos);
        System.arraycopy(cookie, 0, patched, cookiePos, cookie.length);
        System.arraycopy(clientHelloBody, cookiePos, patched, cookiePos + cookie.length, clientHelloBody.length - cookiePos);
        return patched;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/DTLSClientProtocol$ClientHandshakeState.class */
    protected static class ClientHandshakeState {
        TlsClient client = null;
        TlsClientContextImpl clientContext = null;
        TlsSession tlsSession = null;
        SessionParameters sessionParameters = null;
        SessionParameters.Builder sessionParametersBuilder = null;
        int[] offeredCipherSuites = null;
        short[] offeredCompressionMethods = null;
        Hashtable clientExtensions = null;
        Hashtable serverExtensions = null;
        byte[] selectedSessionID = null;
        boolean resumedSession = false;
        boolean secure_renegotiation = false;
        boolean allowCertificateStatus = false;
        boolean expectSessionTicket = false;
        TlsKeyExchange keyExchange = null;
        TlsAuthentication authentication = null;
        CertificateStatus certificateStatus = null;
        CertificateRequest certificateRequest = null;
        TlsCredentials clientCredentials = null;

        protected ClientHandshakeState() {
        }
    }
}
