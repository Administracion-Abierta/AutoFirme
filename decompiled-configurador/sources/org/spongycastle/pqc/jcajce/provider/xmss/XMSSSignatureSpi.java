package org.spongycastle.pqc.jcajce.provider.xmss;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.crypto.digests.SHAKEDigest;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.spongycastle.pqc.crypto.xmss.XMSSSigner;
import org.spongycastle.pqc.jcajce.interfaces.StateAwareSignature;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/xmss/XMSSSignatureSpi.class */
public class XMSSSignatureSpi extends Signature implements StateAwareSignature {
    private Digest digest;
    private XMSSSigner signer;
    private SecureRandom random;
    private ASN1ObjectIdentifier treeDigest;

    protected XMSSSignatureSpi(String algorithm) {
        super(algorithm);
    }

    protected XMSSSignatureSpi(String sigName, Digest digest, XMSSSigner signer) {
        super(sigName);
        this.digest = digest;
        this.signer = signer;
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof BCXMSSPublicKey) {
            CipherParameters param = ((BCXMSSPublicKey) publicKey).getKeyParams();
            this.treeDigest = null;
            this.digest.reset();
            this.signer.init(false, param);
            return;
        }
        throw new InvalidKeyException("unknown public key passed to XMSS");
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey, SecureRandom random) throws InvalidKeyException {
        this.random = random;
        engineInitSign(privateKey);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof BCXMSSPrivateKey) {
            CipherParameters param = ((BCXMSSPrivateKey) privateKey).getKeyParams();
            this.treeDigest = ((BCXMSSPrivateKey) privateKey).getTreeDigestOID();
            if (this.random != null) {
                param = new ParametersWithRandom(param, this.random);
            }
            this.digest.reset();
            this.signer.init(true, param);
            return;
        }
        throw new InvalidKeyException("unknown private key passed to XMSS");
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte b) throws SignatureException {
        this.digest.update(b);
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte[] b, int off, int len) throws SignatureException {
        this.digest.update(b, off, len);
    }

    @Override // java.security.SignatureSpi
    protected byte[] engineSign() throws SignatureException {
        byte[] hash = DigestUtil.getDigestResult(this.digest);
        try {
            byte[] sig = this.signer.generateSignature(hash);
            return sig;
        } catch (Exception e) {
            if (e instanceof IllegalStateException) {
                throw new SignatureException(e.getMessage());
            }
            throw new SignatureException(e.toString());
        }
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
        byte[] hash = DigestUtil.getDigestResult(this.digest);
        return this.signer.verifySignature(hash, sigBytes);
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(AlgorithmParameterSpec params) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(String param, Object value) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected Object engineGetParameter(String param) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override // org.spongycastle.pqc.jcajce.interfaces.StateAwareSignature
    public PrivateKey getUpdatedPrivateKey() {
        if (this.treeDigest == null) {
            throw new IllegalStateException("signature object not in a signing state");
        }
        PrivateKey rKey = new BCXMSSPrivateKey(this.treeDigest, (XMSSPrivateKeyParameters) this.signer.getUpdatedPrivateKey());
        this.treeDigest = null;
        return rKey;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/xmss/XMSSSignatureSpi$withSha256.class */
    public static class withSha256 extends XMSSSignatureSpi {
        public withSha256() {
            super("SHA256withXMSS", new SHA256Digest(), new XMSSSigner());
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/xmss/XMSSSignatureSpi$withShake128.class */
    public static class withShake128 extends XMSSSignatureSpi {
        public withShake128() {
            super("SHAKE128withXMSSMT", new SHAKEDigest(128), new XMSSSigner());
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/xmss/XMSSSignatureSpi$withSha512.class */
    public static class withSha512 extends XMSSSignatureSpi {
        public withSha512() {
            super("SHA512withXMSS", new SHA512Digest(), new XMSSSigner());
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/xmss/XMSSSignatureSpi$withShake256.class */
    public static class withShake256 extends XMSSSignatureSpi {
        public withShake256() {
            super("SHAKE256withXMSS", new SHAKEDigest(256), new XMSSSigner());
        }
    }
}
