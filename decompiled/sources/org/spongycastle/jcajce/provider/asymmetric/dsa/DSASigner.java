package org.spongycastle.jcajce.provider.asymmetric.dsa;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.X509ObjectIdentifiers;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DSA;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.NullDigest;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.signers.HMacDSAKCalculator;
import org.spongycastle.crypto.util.DigestFactory;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner.class */
public class DSASigner extends SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
    private Digest digest;
    private DSA signer;
    private SecureRandom random;

    protected DSASigner(Digest digest, DSA signer) {
        this.digest = digest;
        this.signer = signer;
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        CipherParameters param = DSAUtil.generatePublicKeyParameter(publicKey);
        this.digest.reset();
        this.signer.init(false, param);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey, SecureRandom random) throws InvalidKeyException {
        this.random = random;
        engineInitSign(privateKey);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        CipherParameters param = DSAUtil.generatePrivateKeyParameter(privateKey);
        if (this.random != null) {
            param = new ParametersWithRandom(param, this.random);
        }
        this.digest.reset();
        this.signer.init(true, param);
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
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        try {
            BigInteger[] sig = this.signer.generateSignature(hash);
            return derEncode(sig[0], sig[1]);
        } catch (Exception e) {
            throw new SignatureException(e.toString());
        }
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        try {
            BigInteger[] sig = derDecode(sigBytes);
            return this.signer.verifySignature(hash, sig[0], sig[1]);
        } catch (Exception e) {
            throw new SignatureException("error decoding signature bytes.");
        }
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

    private byte[] derEncode(BigInteger r, BigInteger s) throws IOException {
        ASN1Integer[] rs = {new ASN1Integer(r), new ASN1Integer(s)};
        return new DERSequence(rs).getEncoded("DER");
    }

    private BigInteger[] derDecode(byte[] encoding) throws IOException {
        ASN1Sequence s = (ASN1Sequence) ASN1Primitive.fromByteArray(encoding);
        if (s.size() != 2) {
            throw new IOException("malformed signature");
        }
        if (!Arrays.areEqual(encoding, s.getEncoded("DER"))) {
            throw new IOException("malformed signature");
        }
        return new BigInteger[]{((ASN1Integer) s.getObjectAt(0)).getValue(), ((ASN1Integer) s.getObjectAt(1)).getValue()};
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$stdDSA.class */
    public static class stdDSA extends DSASigner {
        public stdDSA() {
            super(DigestFactory.createSHA1(), new org.spongycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSA.class */
    public static class detDSA extends DSASigner {
        public detDSA() {
            super(DigestFactory.createSHA1(), new org.spongycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA1())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsa224.class */
    public static class dsa224 extends DSASigner {
        public dsa224() {
            super(DigestFactory.createSHA224(), new org.spongycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSA224.class */
    public static class detDSA224 extends DSASigner {
        public detDSA224() {
            super(DigestFactory.createSHA224(), new org.spongycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA224())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsa256.class */
    public static class dsa256 extends DSASigner {
        public dsa256() {
            super(DigestFactory.createSHA256(), new org.spongycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSA256.class */
    public static class detDSA256 extends DSASigner {
        public detDSA256() {
            super(DigestFactory.createSHA256(), new org.spongycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA256())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsa384.class */
    public static class dsa384 extends DSASigner {
        public dsa384() {
            super(DigestFactory.createSHA384(), new org.spongycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSA384.class */
    public static class detDSA384 extends DSASigner {
        public detDSA384() {
            super(DigestFactory.createSHA384(), new org.spongycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA384())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsa512.class */
    public static class dsa512 extends DSASigner {
        public dsa512() {
            super(DigestFactory.createSHA512(), new org.spongycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSA512.class */
    public static class detDSA512 extends DSASigner {
        public detDSA512() {
            super(DigestFactory.createSHA512(), new org.spongycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA512())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsaSha3_224.class */
    public static class dsaSha3_224 extends DSASigner {
        public dsaSha3_224() {
            super(DigestFactory.createSHA3_224(), new org.spongycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSASha3_224.class */
    public static class detDSASha3_224 extends DSASigner {
        public detDSASha3_224() {
            super(DigestFactory.createSHA3_224(), new org.spongycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_224())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsaSha3_256.class */
    public static class dsaSha3_256 extends DSASigner {
        public dsaSha3_256() {
            super(DigestFactory.createSHA3_256(), new org.spongycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSASha3_256.class */
    public static class detDSASha3_256 extends DSASigner {
        public detDSASha3_256() {
            super(DigestFactory.createSHA3_256(), new org.spongycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_256())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsaSha3_384.class */
    public static class dsaSha3_384 extends DSASigner {
        public dsaSha3_384() {
            super(DigestFactory.createSHA3_384(), new org.spongycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSASha3_384.class */
    public static class detDSASha3_384 extends DSASigner {
        public detDSASha3_384() {
            super(DigestFactory.createSHA3_384(), new org.spongycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_384())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$dsaSha3_512.class */
    public static class dsaSha3_512 extends DSASigner {
        public dsaSha3_512() {
            super(DigestFactory.createSHA3_512(), new org.spongycastle.crypto.signers.DSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$detDSASha3_512.class */
    public static class detDSASha3_512 extends DSASigner {
        public detDSASha3_512() {
            super(DigestFactory.createSHA3_512(), new org.spongycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_512())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/DSASigner$noneDSA.class */
    public static class noneDSA extends DSASigner {
        public noneDSA() {
            super(new NullDigest(), new org.spongycastle.crypto.signers.DSASigner());
        }
    }
}
