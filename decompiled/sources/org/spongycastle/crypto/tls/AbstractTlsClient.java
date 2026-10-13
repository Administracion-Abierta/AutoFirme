package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/AbstractTlsClient.class */
public abstract class AbstractTlsClient extends AbstractTlsPeer implements TlsClient {
    protected TlsCipherFactory cipherFactory;
    protected TlsClientContext context;
    protected Vector supportedSignatureAlgorithms;
    protected int[] namedCurves;
    protected short[] clientECPointFormats;
    protected short[] serverECPointFormats;
    protected int selectedCipherSuite;
    protected short selectedCompressionMethod;

    public AbstractTlsClient() {
        this(new DefaultTlsCipherFactory());
    }

    public AbstractTlsClient(TlsCipherFactory cipherFactory) {
        this.cipherFactory = cipherFactory;
    }

    protected boolean allowUnexpectedServerExtension(Integer extensionType, byte[] extensionData) throws IOException {
        switch (extensionType.intValue()) {
            case 10:
                TlsECCUtils.readSupportedEllipticCurvesExtension(extensionData);
                return true;
            default:
                return false;
        }
    }

    protected void checkForUnexpectedServerExtension(Hashtable serverExtensions, Integer extensionType) throws IOException {
        byte[] extensionData = TlsUtils.getExtensionData(serverExtensions, extensionType);
        if (extensionData != null && !allowUnexpectedServerExtension(extensionType, extensionData)) {
            throw new TlsFatalAlert((short) 47);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public void init(TlsClientContext context) {
        this.context = context;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public TlsSession getSessionToResume() {
        return null;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public ProtocolVersion getClientHelloRecordLayerVersion() {
        return getClientVersion();
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public ProtocolVersion getClientVersion() {
        return ProtocolVersion.TLSv12;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public boolean isFallback() {
        return false;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public Hashtable getClientExtensions() throws IOException {
        Hashtable clientExtensions = null;
        ProtocolVersion clientVersion = this.context.getClientVersion();
        if (TlsUtils.isSignatureAlgorithmsExtensionAllowed(clientVersion)) {
            this.supportedSignatureAlgorithms = TlsUtils.getDefaultSupportedSignatureAlgorithms();
            clientExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(null);
            TlsUtils.addSignatureAlgorithmsExtension(clientExtensions, this.supportedSignatureAlgorithms);
        }
        if (TlsECCUtils.containsECCCipherSuites(getCipherSuites())) {
            this.namedCurves = new int[]{23, 24};
            this.clientECPointFormats = new short[]{0, 1, 2};
            clientExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(clientExtensions);
            TlsECCUtils.addSupportedEllipticCurvesExtension(clientExtensions, this.namedCurves);
            TlsECCUtils.addSupportedPointFormatsExtension(clientExtensions, this.clientECPointFormats);
        }
        return clientExtensions;
    }

    public ProtocolVersion getMinimumVersion() {
        return ProtocolVersion.TLSv10;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public void notifyServerVersion(ProtocolVersion serverVersion) throws IOException {
        if (!getMinimumVersion().isEqualOrEarlierVersionOf(serverVersion)) {
            throw new TlsFatalAlert((short) 70);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public short[] getCompressionMethods() {
        return new short[]{0};
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public void notifySessionID(byte[] sessionID) {
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public void notifySelectedCipherSuite(int selectedCipherSuite) {
        this.selectedCipherSuite = selectedCipherSuite;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public void notifySelectedCompressionMethod(short selectedCompressionMethod) {
        this.selectedCompressionMethod = selectedCompressionMethod;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public void processServerExtensions(Hashtable serverExtensions) throws IOException {
        if (serverExtensions != null) {
            checkForUnexpectedServerExtension(serverExtensions, TlsUtils.EXT_signature_algorithms);
            checkForUnexpectedServerExtension(serverExtensions, TlsECCUtils.EXT_elliptic_curves);
            if (TlsECCUtils.isECCCipherSuite(this.selectedCipherSuite)) {
                this.serverECPointFormats = TlsECCUtils.getSupportedPointFormatsExtension(serverExtensions);
            } else {
                checkForUnexpectedServerExtension(serverExtensions, TlsECCUtils.EXT_ec_point_formats);
            }
            checkForUnexpectedServerExtension(serverExtensions, TlsExtensionsUtils.EXT_padding);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public void processServerSupplementalData(Vector serverSupplementalData) throws IOException {
        if (serverSupplementalData != null) {
            throw new TlsFatalAlert((short) 10);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public Vector getClientSupplementalData() throws IOException {
        return null;
    }

    @Override // org.spongycastle.crypto.tls.TlsPeer
    public TlsCompression getCompression() throws IOException {
        switch (this.selectedCompressionMethod) {
            case 0:
                return new TlsNullCompression();
            default:
                throw new TlsFatalAlert((short) 80);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsPeer
    public TlsCipher getCipher() throws IOException {
        int encryptionAlgorithm = TlsUtils.getEncryptionAlgorithm(this.selectedCipherSuite);
        int macAlgorithm = TlsUtils.getMACAlgorithm(this.selectedCipherSuite);
        return this.cipherFactory.createCipher(this.context, encryptionAlgorithm, macAlgorithm);
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public void notifyNewSessionTicket(NewSessionTicket newSessionTicket) throws IOException {
    }
}
