package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Vector;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.agreement.srp.SRP6Client;
import org.spongycastle.crypto.agreement.srp.SRP6Server;
import org.spongycastle.crypto.agreement.srp.SRP6Util;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.SRP6GroupParameters;
import org.spongycastle.crypto.util.PublicKeyFactory;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.BigIntegers;
import org.spongycastle.util.io.TeeInputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsSRPKeyExchange.class */
public class TlsSRPKeyExchange extends AbstractTlsKeyExchange {
    protected TlsSigner tlsSigner;
    protected TlsSRPGroupVerifier groupVerifier;
    protected byte[] identity;
    protected byte[] password;
    protected AsymmetricKeyParameter serverPublicKey;
    protected SRP6GroupParameters srpGroup;
    protected SRP6Client srpClient;
    protected SRP6Server srpServer;
    protected BigInteger srpPeerCredentials;
    protected BigInteger srpVerifier;
    protected byte[] srpSalt;
    protected TlsSignerCredentials serverCredentials;

    protected static TlsSigner createSigner(int keyExchange) {
        switch (keyExchange) {
            case 21:
                return null;
            case 22:
                return new TlsDSSSigner();
            case 23:
                return new TlsRSASigner();
            default:
                throw new IllegalArgumentException("unsupported key exchange algorithm");
        }
    }

    public TlsSRPKeyExchange(int keyExchange, Vector supportedSignatureAlgorithms, byte[] identity, byte[] password) {
        this(keyExchange, supportedSignatureAlgorithms, new DefaultTlsSRPGroupVerifier(), identity, password);
    }

    public TlsSRPKeyExchange(int keyExchange, Vector supportedSignatureAlgorithms, TlsSRPGroupVerifier groupVerifier, byte[] identity, byte[] password) {
        super(keyExchange, supportedSignatureAlgorithms);
        this.serverPublicKey = null;
        this.srpGroup = null;
        this.srpClient = null;
        this.srpServer = null;
        this.srpPeerCredentials = null;
        this.srpVerifier = null;
        this.srpSalt = null;
        this.serverCredentials = null;
        this.tlsSigner = createSigner(keyExchange);
        this.groupVerifier = groupVerifier;
        this.identity = identity;
        this.password = password;
        this.srpClient = new SRP6Client();
    }

    public TlsSRPKeyExchange(int keyExchange, Vector supportedSignatureAlgorithms, byte[] identity, TlsSRPLoginParameters loginParameters) {
        super(keyExchange, supportedSignatureAlgorithms);
        this.serverPublicKey = null;
        this.srpGroup = null;
        this.srpClient = null;
        this.srpServer = null;
        this.srpPeerCredentials = null;
        this.srpVerifier = null;
        this.srpSalt = null;
        this.serverCredentials = null;
        this.tlsSigner = createSigner(keyExchange);
        this.identity = identity;
        this.srpServer = new SRP6Server();
        this.srpGroup = loginParameters.getGroup();
        this.srpVerifier = loginParameters.getVerifier();
        this.srpSalt = loginParameters.getSalt();
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void init(TlsContext context) {
        super.init(context);
        if (this.tlsSigner != null) {
            this.tlsSigner.init(context);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void skipServerCredentials() throws IOException {
        if (this.tlsSigner != null) {
            throw new TlsFatalAlert((short) 10);
        }
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerCertificate(Certificate serverCertificate) throws IOException {
        if (this.tlsSigner == null) {
            throw new TlsFatalAlert((short) 10);
        }
        if (serverCertificate.isEmpty()) {
            throw new TlsFatalAlert((short) 42);
        }
        org.spongycastle.asn1.x509.Certificate x509Cert = serverCertificate.getCertificateAt(0);
        SubjectPublicKeyInfo keyInfo = x509Cert.getSubjectPublicKeyInfo();
        try {
            this.serverPublicKey = PublicKeyFactory.createKey(keyInfo);
            if (!this.tlsSigner.isValidPublicKey(this.serverPublicKey)) {
                throw new TlsFatalAlert((short) 46);
            }
            TlsUtils.validateKeyUsage(x509Cert, 128);
            super.processServerCertificate(serverCertificate);
        } catch (RuntimeException e) {
            throw new TlsFatalAlert((short) 43, e);
        }
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerCredentials(TlsCredentials serverCredentials) throws IOException {
        if (this.keyExchange == 21 || !(serverCredentials instanceof TlsSignerCredentials)) {
            throw new TlsFatalAlert((short) 80);
        }
        processServerCertificate(serverCredentials.getCertificate());
        this.serverCredentials = (TlsSignerCredentials) serverCredentials;
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public boolean requiresServerKeyExchange() {
        return true;
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public byte[] generateServerKeyExchange() throws IOException {
        this.srpServer.init(this.srpGroup, this.srpVerifier, TlsUtils.createHash((short) 2), this.context.getSecureRandom());
        BigInteger B = this.srpServer.generateServerCredentials();
        ServerSRPParams srpParams = new ServerSRPParams(this.srpGroup.getN(), this.srpGroup.getG(), this.srpSalt, B);
        DigestInputBuffer buf = new DigestInputBuffer();
        srpParams.encode(buf);
        if (this.serverCredentials != null) {
            SignatureAndHashAlgorithm signatureAndHashAlgorithm = TlsUtils.getSignatureAndHashAlgorithm(this.context, this.serverCredentials);
            Digest d = TlsUtils.createHash(signatureAndHashAlgorithm);
            SecurityParameters securityParameters = this.context.getSecurityParameters();
            d.update(securityParameters.clientRandom, 0, securityParameters.clientRandom.length);
            d.update(securityParameters.serverRandom, 0, securityParameters.serverRandom.length);
            buf.updateDigest(d);
            byte[] hash = new byte[d.getDigestSize()];
            d.doFinal(hash, 0);
            byte[] signature = this.serverCredentials.generateCertificateSignature(hash);
            DigitallySigned signed_params = new DigitallySigned(signatureAndHashAlgorithm, signature);
            signed_params.encode(buf);
        }
        return buf.toByteArray();
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerKeyExchange(InputStream input) throws IOException {
        SecurityParameters securityParameters = this.context.getSecurityParameters();
        SignerInputBuffer buf = null;
        InputStream teeIn = input;
        if (this.tlsSigner != null) {
            buf = new SignerInputBuffer();
            teeIn = new TeeInputStream(input, buf);
        }
        ServerSRPParams srpParams = ServerSRPParams.parse(teeIn);
        if (buf != null) {
            DigitallySigned signed_params = parseSignature(input);
            Signer signer = initVerifyer(this.tlsSigner, signed_params.getAlgorithm(), securityParameters);
            buf.updateSigner(signer);
            if (!signer.verifySignature(signed_params.getSignature())) {
                throw new TlsFatalAlert((short) 51);
            }
        }
        this.srpGroup = new SRP6GroupParameters(srpParams.getN(), srpParams.getG());
        if (!this.groupVerifier.accept(this.srpGroup)) {
            throw new TlsFatalAlert((short) 71);
        }
        this.srpSalt = srpParams.getS();
        try {
            this.srpPeerCredentials = SRP6Util.validatePublicValue(this.srpGroup.getN(), srpParams.getB());
            this.srpClient.init(this.srpGroup, TlsUtils.createHash((short) 2), this.context.getSecureRandom());
        } catch (CryptoException e) {
            throw new TlsFatalAlert((short) 47, e);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void validateCertificateRequest(CertificateRequest certificateRequest) throws IOException {
        throw new TlsFatalAlert((short) 10);
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void processClientCredentials(TlsCredentials clientCredentials) throws IOException {
        throw new TlsFatalAlert((short) 80);
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void generateClientKeyExchange(OutputStream output) throws IOException {
        BigInteger A = this.srpClient.generateClientCredentials(this.srpSalt, this.identity, this.password);
        TlsSRPUtils.writeSRPParameter(A, output);
        this.context.getSecurityParameters().srpIdentity = Arrays.clone(this.identity);
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void processClientKeyExchange(InputStream input) throws IOException {
        try {
            this.srpPeerCredentials = SRP6Util.validatePublicValue(this.srpGroup.getN(), TlsSRPUtils.readSRPParameter(input));
            this.context.getSecurityParameters().srpIdentity = Arrays.clone(this.identity);
        } catch (CryptoException e) {
            throw new TlsFatalAlert((short) 47, e);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public byte[] generatePremasterSecret() throws IOException {
        BigInteger bigIntegerCalculateSecret;
        try {
            if (this.srpServer != null) {
                bigIntegerCalculateSecret = this.srpServer.calculateSecret(this.srpPeerCredentials);
            } else {
                bigIntegerCalculateSecret = this.srpClient.calculateSecret(this.srpPeerCredentials);
            }
            BigInteger S = bigIntegerCalculateSecret;
            return BigIntegers.asUnsignedByteArray(S);
        } catch (CryptoException e) {
            throw new TlsFatalAlert((short) 47, e);
        }
    }

    protected Signer initVerifyer(TlsSigner tlsSigner, SignatureAndHashAlgorithm algorithm, SecurityParameters securityParameters) {
        Signer signer = tlsSigner.createVerifyer(algorithm, this.serverPublicKey);
        signer.update(securityParameters.clientRandom, 0, securityParameters.clientRandom.length);
        signer.update(securityParameters.serverRandom, 0, securityParameters.serverRandom.length);
        return signer;
    }
}
