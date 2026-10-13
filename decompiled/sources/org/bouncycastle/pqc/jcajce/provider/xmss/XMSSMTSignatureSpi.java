package org.bouncycastle.pqc.jcajce.provider.xmss;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTSigner;
import org.bouncycastle.pqc.jcajce.interfaces.StateAwareSignature;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/xmss/XMSSMTSignatureSpi.class */
public class XMSSMTSignatureSpi extends Signature implements StateAwareSignature {
    private Digest digest;
    private XMSSMTSigner signer;
    private ASN1ObjectIdentifier treeDigest;
    private SecureRandom random;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/xmss/XMSSMTSignatureSpi$generic.class */
    public static class generic extends XMSSMTSignatureSpi {
        public generic() {
            super("XMSSMT", new NullDigest(), new XMSSMTSigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/xmss/XMSSMTSignatureSpi$withSha256.class */
    public static class withSha256 extends XMSSMTSignatureSpi {
        public withSha256() {
            super("XMSSMT-SHA256", new NullDigest(), new XMSSMTSigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/xmss/XMSSMTSignatureSpi$withSha256andPrehash.class */
    public static class withSha256andPrehash extends XMSSMTSignatureSpi {
        public withSha256andPrehash() {
            super("SHA256withXMSSMT-SHA256", new SHA256Digest(), new XMSSMTSigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/xmss/XMSSMTSignatureSpi$withSha512.class */
    public static class withSha512 extends XMSSMTSignatureSpi {
        public withSha512() {
            super("XMSSMT-SHA512", new NullDigest(), new XMSSMTSigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/xmss/XMSSMTSignatureSpi$withSha512andPrehash.class */
    public static class withSha512andPrehash extends XMSSMTSignatureSpi {
        public withSha512andPrehash() {
            super("SHA512withXMSSMT-SHA512", new SHA512Digest(), new XMSSMTSigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/xmss/XMSSMTSignatureSpi$withShake128.class */
    public static class withShake128 extends XMSSMTSignatureSpi {
        public withShake128() {
            super("XMSSMT-SHAKE128", new NullDigest(), new XMSSMTSigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/xmss/XMSSMTSignatureSpi$withShake128_512andPrehash.class */
    public static class withShake128_512andPrehash extends XMSSMTSignatureSpi {
        public withShake128_512andPrehash() {
            super("SHAKE128(512)withXMSSMT-SHAKE128", new DigestUtil.DoubleDigest(new SHAKEDigest(128)), new XMSSMTSigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/xmss/XMSSMTSignatureSpi$withShake128andPrehash.class */
    public static class withShake128andPrehash extends XMSSMTSignatureSpi {
        public withShake128andPrehash() {
            super("SHAKE128withXMSSMT-SHAKE128", new SHAKEDigest(128), new XMSSMTSigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/xmss/XMSSMTSignatureSpi$withShake256.class */
    public static class withShake256 extends XMSSMTSignatureSpi {
        public withShake256() {
            super("XMSSMT-SHAKE256", new NullDigest(), new XMSSMTSigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/xmss/XMSSMTSignatureSpi$withShake256_1024andPrehash.class */
    public static class withShake256_1024andPrehash extends XMSSMTSignatureSpi {
        public withShake256_1024andPrehash() {
            super("SHAKE256(1024)withXMSSMT-SHAKE256", new DigestUtil.DoubleDigest(new SHAKEDigest(256)), new XMSSMTSigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/xmss/XMSSMTSignatureSpi$withShake256andPrehash.class */
    public static class withShake256andPrehash extends XMSSMTSignatureSpi {
        public withShake256andPrehash() {
            super("SHAKE256withXMSSMT-SHAKE256", new SHAKEDigest(256), new XMSSMTSigner());
        }
    }

    protected XMSSMTSignatureSpi(String str) {
        super(str);
    }

    protected XMSSMTSignatureSpi(String str, Digest digest, XMSSMTSigner xMSSMTSigner) {
        super(str);
        this.digest = digest;
        this.signer = xMSSMTSigner;
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof BCXMSSMTPublicKey)) {
            throw new InvalidKeyException("unknown public key passed to XMSSMT");
        }
        CipherParameters keyParams = ((BCXMSSMTPublicKey) publicKey).getKeyParams();
        this.treeDigest = null;
        this.digest.reset();
        this.signer.init(false, keyParams);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.random = secureRandom;
        engineInitSign(privateKey);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (!(privateKey instanceof BCXMSSMTPrivateKey)) {
            throw new InvalidKeyException("unknown private key passed to XMSSMT");
        }
        CipherParameters keyParams = ((BCXMSSMTPrivateKey) privateKey).getKeyParams();
        this.treeDigest = ((BCXMSSMTPrivateKey) privateKey).getTreeDigestOID();
        if (this.random != null) {
            keyParams = new ParametersWithRandom(keyParams, this.random);
        }
        this.digest.reset();
        this.signer.init(true, keyParams);
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte b) throws SignatureException {
        this.digest.update(b);
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte[] bArr, int i, int i2) throws SignatureException {
        this.digest.update(bArr, i, i2);
    }

    @Override // java.security.SignatureSpi
    protected byte[] engineSign() throws SignatureException {
        try {
            return this.signer.generateSignature(DigestUtil.getDigestResult(this.digest));
        } catch (Exception e) {
            if (e instanceof IllegalStateException) {
                throw new SignatureException(e.getMessage(), e);
            }
            throw new SignatureException(e.toString());
        }
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] bArr) throws SignatureException {
        return this.signer.verifySignature(DigestUtil.getDigestResult(this.digest), bArr);
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(AlgorithmParameterSpec algorithmParameterSpec) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(String str, Object obj) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected Object engineGetParameter(String str) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override // org.bouncycastle.pqc.jcajce.interfaces.StateAwareSignature
    public boolean isSigningCapable() {
        return (this.treeDigest == null || this.signer.getUsagesRemaining() == 0) ? false : true;
    }

    @Override // org.bouncycastle.pqc.jcajce.interfaces.StateAwareSignature
    public PrivateKey getUpdatedPrivateKey() {
        if (this.treeDigest == null) {
            throw new IllegalStateException("signature object not in a signing state");
        }
        BCXMSSMTPrivateKey bCXMSSMTPrivateKey = new BCXMSSMTPrivateKey(this.treeDigest, (XMSSMTPrivateKeyParameters) this.signer.getUpdatedPrivateKey());
        this.treeDigest = null;
        return bCXMSSMTPrivateKey;
    }
}
