package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSAEncoding;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.crypto.signers.StandardDSAEncoding;
import org.bouncycastle.crypto.util.DigestFactory;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner.class */
public class DSASigner extends SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
    private Digest digest;
    private DSAExt signer;
    private DSAEncoding encoding = StandardDSAEncoding.INSTANCE;
    private SecureRandom random;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSA.class */
    public static class detDSA extends DSASigner {
        public detDSA() {
            super(DigestFactory.createSHA1(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA1())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSA224.class */
    public static class detDSA224 extends DSASigner {
        public detDSA224() {
            super(DigestFactory.createSHA224(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA224())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSA256.class */
    public static class detDSA256 extends DSASigner {
        public detDSA256() {
            super(DigestFactory.createSHA256(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA256())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSA384.class */
    public static class detDSA384 extends DSASigner {
        public detDSA384() {
            super(DigestFactory.createSHA384(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA384())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSA512.class */
    public static class detDSA512 extends DSASigner {
        public detDSA512() {
            super(DigestFactory.createSHA512(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA512())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSASha3_224.class */
    public static class detDSASha3_224 extends DSASigner {
        public detDSASha3_224() {
            super(DigestFactory.createSHA3_224(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_224())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSASha3_256.class */
    public static class detDSASha3_256 extends DSASigner {
        public detDSASha3_256() {
            super(DigestFactory.createSHA3_256(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_256())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSASha3_384.class */
    public static class detDSASha3_384 extends DSASigner {
        public detDSASha3_384() {
            super(DigestFactory.createSHA3_384(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_384())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSASha3_512.class */
    public static class detDSASha3_512 extends DSASigner {
        public detDSASha3_512() {
            super(DigestFactory.createSHA3_512(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_512())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsa224.class */
    public static class dsa224 extends DSASigner {
        public dsa224() {
            super(DigestFactory.createSHA224(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsa256.class */
    public static class dsa256 extends DSASigner {
        public dsa256() {
            super(DigestFactory.createSHA256(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsa384.class */
    public static class dsa384 extends DSASigner {
        public dsa384() {
            super(DigestFactory.createSHA384(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsa512.class */
    public static class dsa512 extends DSASigner {
        public dsa512() {
            super(DigestFactory.createSHA512(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsaRMD160.class */
    public static class dsaRMD160 extends DSASigner {
        public dsaRMD160() {
            super(new RIPEMD160Digest(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsaSha3_224.class */
    public static class dsaSha3_224 extends DSASigner {
        public dsaSha3_224() {
            super(DigestFactory.createSHA3_224(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsaSha3_256.class */
    public static class dsaSha3_256 extends DSASigner {
        public dsaSha3_256() {
            super(DigestFactory.createSHA3_256(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsaSha3_384.class */
    public static class dsaSha3_384 extends DSASigner {
        public dsaSha3_384() {
            super(DigestFactory.createSHA3_384(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsaSha3_512.class */
    public static class dsaSha3_512 extends DSASigner {
        public dsaSha3_512() {
            super(DigestFactory.createSHA3_512(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$noneDSA.class */
    public static class noneDSA extends DSASigner {
        public noneDSA() {
            super(new NullDigest(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dsa/DSASigner$stdDSA.class */
    public static class stdDSA extends DSASigner {
        public stdDSA() {
            super(DigestFactory.createSHA1(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    protected DSASigner(Digest digest, DSAExt dSAExt) {
        this.digest = digest;
        this.signer = dSAExt;
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        AsymmetricKeyParameter asymmetricKeyParameterGeneratePublicKeyParameter = DSAUtil.generatePublicKeyParameter(publicKey);
        this.digest.reset();
        this.signer.init(false, asymmetricKeyParameterGeneratePublicKeyParameter);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.random = secureRandom;
        engineInitSign(privateKey);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        CipherParameters cipherParametersGeneratePrivateKeyParameter = DSAUtil.generatePrivateKeyParameter(privateKey);
        if (this.random != null) {
            cipherParametersGeneratePrivateKeyParameter = new ParametersWithRandom(cipherParametersGeneratePrivateKeyParameter, this.random);
        }
        this.digest.reset();
        this.signer.init(true, cipherParametersGeneratePrivateKeyParameter);
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
        byte[] bArr = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(bArr, 0);
        try {
            BigInteger[] bigIntegerArrGenerateSignature = this.signer.generateSignature(bArr);
            return this.encoding.encode(this.signer.getOrder(), bigIntegerArrGenerateSignature[0], bigIntegerArrGenerateSignature[1]);
        } catch (Exception e) {
            throw new SignatureException(e.toString());
        }
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] bArr) throws SignatureException {
        byte[] bArr2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(bArr2, 0);
        try {
            BigInteger[] bigIntegerArrDecode = this.encoding.decode(this.signer.getOrder(), bArr);
            return this.signer.verifySignature(bArr2, bigIntegerArrDecode[0], bigIntegerArrDecode[1]);
        } catch (Exception e) {
            throw new SignatureException("error decoding signature bytes.");
        }
    }

    @Override // java.security.SignatureSpi
    protected AlgorithmParameters engineGetParameters() {
        return null;
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
        throw new UnsupportedOperationException("engineGetParameter unsupported");
    }
}
