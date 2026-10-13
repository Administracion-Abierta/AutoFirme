package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/AbstractTlsServer.class */
public abstract class AbstractTlsServer extends AbstractTlsPeer implements TlsServer {
    protected TlsCipherFactory cipherFactory;
    protected TlsServerContext context;
    protected ProtocolVersion clientVersion;
    protected int[] offeredCipherSuites;
    protected short[] offeredCompressionMethods;
    protected Hashtable clientExtensions;
    protected boolean encryptThenMACOffered;
    protected short maxFragmentLengthOffered;
    protected boolean truncatedHMacOffered;
    protected Vector supportedSignatureAlgorithms;
    protected boolean eccCipherSuitesOffered;
    protected int[] namedCurves;
    protected short[] clientECPointFormats;
    protected short[] serverECPointFormats;
    protected ProtocolVersion serverVersion;
    protected int selectedCipherSuite;
    protected short selectedCompressionMethod;
    protected Hashtable serverExtensions;

    protected abstract int[] getCipherSuites();

    public AbstractTlsServer() {
        this(new DefaultTlsCipherFactory());
    }

    public AbstractTlsServer(TlsCipherFactory cipherFactory) {
        this.cipherFactory = cipherFactory;
    }

    protected boolean allowEncryptThenMAC() {
        return true;
    }

    protected boolean allowTruncatedHMac() {
        return false;
    }

    protected Hashtable checkServerExtensions() {
        Hashtable hashtableEnsureExtensionsInitialised = TlsExtensionsUtils.ensureExtensionsInitialised(this.serverExtensions);
        this.serverExtensions = hashtableEnsureExtensionsInitialised;
        return hashtableEnsureExtensionsInitialised;
    }

    protected short[] getCompressionMethods() {
        return new short[]{0};
    }

    protected ProtocolVersion getMaximumVersion() {
        return ProtocolVersion.TLSv11;
    }

    protected ProtocolVersion getMinimumVersion() {
        return ProtocolVersion.TLSv10;
    }

    protected boolean supportsClientECCCapabilities(int[] namedCurves, short[] ecPointFormats) {
        if (namedCurves == null) {
            return TlsECCUtils.hasAnySupportedNamedCurves();
        }
        for (int namedCurve : namedCurves) {
            if (NamedCurve.isValid(namedCurve) && (!NamedCurve.refersToASpecificNamedCurve(namedCurve) || TlsECCUtils.isSupportedNamedCurve(namedCurve))) {
                return true;
            }
        }
        return false;
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public void init(TlsServerContext context) {
        this.context = context;
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public void notifyClientVersion(ProtocolVersion clientVersion) throws IOException {
        this.clientVersion = clientVersion;
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public void notifyFallback(boolean isFallback) throws IOException {
        if (isFallback && getMaximumVersion().isLaterVersionOf(this.clientVersion)) {
            throw new TlsFatalAlert((short) 86);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public void notifyOfferedCipherSuites(int[] offeredCipherSuites) throws IOException {
        this.offeredCipherSuites = offeredCipherSuites;
        this.eccCipherSuitesOffered = TlsECCUtils.containsECCCipherSuites(this.offeredCipherSuites);
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public void notifyOfferedCompressionMethods(short[] offeredCompressionMethods) throws IOException {
        this.offeredCompressionMethods = offeredCompressionMethods;
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public void processClientExtensions(Hashtable clientExtensions) throws IOException {
        this.clientExtensions = clientExtensions;
        if (clientExtensions != null) {
            this.encryptThenMACOffered = TlsExtensionsUtils.hasEncryptThenMACExtension(clientExtensions);
            this.maxFragmentLengthOffered = TlsExtensionsUtils.getMaxFragmentLengthExtension(clientExtensions);
            if (this.maxFragmentLengthOffered >= 0 && !MaxFragmentLength.isValid(this.maxFragmentLengthOffered)) {
                throw new TlsFatalAlert((short) 47);
            }
            this.truncatedHMacOffered = TlsExtensionsUtils.hasTruncatedHMacExtension(clientExtensions);
            this.supportedSignatureAlgorithms = TlsUtils.getSignatureAlgorithmsExtension(clientExtensions);
            if (this.supportedSignatureAlgorithms != null && !TlsUtils.isSignatureAlgorithmsExtensionAllowed(this.clientVersion)) {
                throw new TlsFatalAlert((short) 47);
            }
            this.namedCurves = TlsECCUtils.getSupportedEllipticCurvesExtension(clientExtensions);
            this.clientECPointFormats = TlsECCUtils.getSupportedPointFormatsExtension(clientExtensions);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public ProtocolVersion getServerVersion() throws IOException {
        if (getMinimumVersion().isEqualOrEarlierVersionOf(this.clientVersion)) {
            ProtocolVersion maximumVersion = getMaximumVersion();
            if (this.clientVersion.isEqualOrEarlierVersionOf(maximumVersion)) {
                ProtocolVersion protocolVersion = this.clientVersion;
                this.serverVersion = protocolVersion;
                return protocolVersion;
            }
            if (this.clientVersion.isLaterVersionOf(maximumVersion)) {
                this.serverVersion = maximumVersion;
                return maximumVersion;
            }
        }
        throw new TlsFatalAlert((short) 70);
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public int getSelectedCipherSuite() throws IOException {
        Vector sigAlgs = TlsUtils.getUsableSignatureAlgorithms(this.supportedSignatureAlgorithms);
        boolean eccCipherSuitesEnabled = supportsClientECCCapabilities(this.namedCurves, this.clientECPointFormats);
        int[] cipherSuites = getCipherSuites();
        for (int cipherSuite : cipherSuites) {
            if (Arrays.contains(this.offeredCipherSuites, cipherSuite) && ((eccCipherSuitesEnabled || !TlsECCUtils.isECCCipherSuite(cipherSuite)) && TlsUtils.isValidCipherSuiteForVersion(cipherSuite, this.serverVersion) && TlsUtils.isValidCipherSuiteForSignatureAlgorithms(cipherSuite, sigAlgs))) {
                this.selectedCipherSuite = cipherSuite;
                return cipherSuite;
            }
        }
        throw new TlsFatalAlert((short) 40);
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public short getSelectedCompressionMethod() throws IOException {
        short[] compressionMethods = getCompressionMethods();
        for (int i = 0; i < compressionMethods.length; i++) {
            if (Arrays.contains(this.offeredCompressionMethods, compressionMethods[i])) {
                short s = compressionMethods[i];
                this.selectedCompressionMethod = s;
                return s;
            }
        }
        throw new TlsFatalAlert((short) 40);
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public Hashtable getServerExtensions() throws IOException {
        if (this.encryptThenMACOffered && allowEncryptThenMAC() && TlsUtils.isBlockCipherSuite(this.selectedCipherSuite)) {
            TlsExtensionsUtils.addEncryptThenMACExtension(checkServerExtensions());
        }
        if (this.maxFragmentLengthOffered >= 0 && MaxFragmentLength.isValid(this.maxFragmentLengthOffered)) {
            TlsExtensionsUtils.addMaxFragmentLengthExtension(checkServerExtensions(), this.maxFragmentLengthOffered);
        }
        if (this.truncatedHMacOffered && allowTruncatedHMac()) {
            TlsExtensionsUtils.addTruncatedHMacExtension(checkServerExtensions());
        }
        if (this.clientECPointFormats != null && TlsECCUtils.isECCCipherSuite(this.selectedCipherSuite)) {
            this.serverECPointFormats = new short[]{0, 1, 2};
            TlsECCUtils.addSupportedPointFormatsExtension(checkServerExtensions(), this.serverECPointFormats);
        }
        return this.serverExtensions;
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public Vector getServerSupplementalData() throws IOException {
        return null;
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public CertificateStatus getCertificateStatus() throws IOException {
        return null;
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public CertificateRequest getCertificateRequest() throws IOException {
        return null;
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public void processClientSupplementalData(Vector clientSupplementalData) throws IOException {
        if (clientSupplementalData != null) {
            throw new TlsFatalAlert((short) 10);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public void notifyClientCertificate(Certificate clientCertificate) throws IOException {
        throw new TlsFatalAlert((short) 80);
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

    @Override // org.spongycastle.crypto.tls.TlsServer
    public NewSessionTicket getNewSessionTicket() throws IOException {
        return new NewSessionTicket(0L, TlsUtils.EMPTY_BYTES);
    }
}
