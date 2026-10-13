package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.RIPEMD256Digest;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi.class */
public class DigestSignatureSpi extends SignatureSpi {
    private Digest digest;
    private AsymmetricBlockCipher cipher;
    private AlgorithmIdentifier algId;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$MD2.class */
    public static class MD2 extends DigestSignatureSpi {
        public MD2() {
            super(PKCSObjectIdentifiers.md2, new MD2Digest(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$MD4.class */
    public static class MD4 extends DigestSignatureSpi {
        public MD4() {
            super(PKCSObjectIdentifiers.md4, new MD4Digest(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$MD5.class */
    public static class MD5 extends DigestSignatureSpi {
        public MD5() {
            super(PKCSObjectIdentifiers.md5, DigestFactory.createMD5(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$RIPEMD128.class */
    public static class RIPEMD128 extends DigestSignatureSpi {
        public RIPEMD128() {
            super(TeleTrusTObjectIdentifiers.ripemd128, new RIPEMD128Digest(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$RIPEMD160.class */
    public static class RIPEMD160 extends DigestSignatureSpi {
        public RIPEMD160() {
            super(TeleTrusTObjectIdentifiers.ripemd160, new RIPEMD160Digest(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$RIPEMD256.class */
    public static class RIPEMD256 extends DigestSignatureSpi {
        public RIPEMD256() {
            super(TeleTrusTObjectIdentifiers.ripemd256, new RIPEMD256Digest(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$SHA1.class */
    public static class SHA1 extends DigestSignatureSpi {
        public SHA1() {
            super(OIWObjectIdentifiers.idSHA1, DigestFactory.createSHA1(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$SHA224.class */
    public static class SHA224 extends DigestSignatureSpi {
        public SHA224() {
            super(NISTObjectIdentifiers.id_sha224, DigestFactory.createSHA224(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$SHA256.class */
    public static class SHA256 extends DigestSignatureSpi {
        public SHA256() {
            super(NISTObjectIdentifiers.id_sha256, DigestFactory.createSHA256(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$SHA384.class */
    public static class SHA384 extends DigestSignatureSpi {
        public SHA384() {
            super(NISTObjectIdentifiers.id_sha384, DigestFactory.createSHA384(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$SHA3_224.class */
    public static class SHA3_224 extends DigestSignatureSpi {
        public SHA3_224() {
            super(NISTObjectIdentifiers.id_sha3_224, DigestFactory.createSHA3_224(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$SHA3_256.class */
    public static class SHA3_256 extends DigestSignatureSpi {
        public SHA3_256() {
            super(NISTObjectIdentifiers.id_sha3_256, DigestFactory.createSHA3_256(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$SHA3_384.class */
    public static class SHA3_384 extends DigestSignatureSpi {
        public SHA3_384() {
            super(NISTObjectIdentifiers.id_sha3_384, DigestFactory.createSHA3_384(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$SHA3_512.class */
    public static class SHA3_512 extends DigestSignatureSpi {
        public SHA3_512() {
            super(NISTObjectIdentifiers.id_sha3_512, DigestFactory.createSHA3_512(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$SHA512.class */
    public static class SHA512 extends DigestSignatureSpi {
        public SHA512() {
            super(NISTObjectIdentifiers.id_sha512, DigestFactory.createSHA512(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$SHA512_224.class */
    public static class SHA512_224 extends DigestSignatureSpi {
        public SHA512_224() {
            super(NISTObjectIdentifiers.id_sha512_224, DigestFactory.createSHA512_224(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$SHA512_256.class */
    public static class SHA512_256 extends DigestSignatureSpi {
        public SHA512_256() {
            super(NISTObjectIdentifiers.id_sha512_256, DigestFactory.createSHA512_256(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/DigestSignatureSpi$noneRSA.class */
    public static class noneRSA extends DigestSignatureSpi {
        public noneRSA() {
            super(new NullDigest(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    protected DigestSignatureSpi(Digest digest, AsymmetricBlockCipher asymmetricBlockCipher) {
        this.digest = digest;
        this.cipher = asymmetricBlockCipher;
        this.algId = null;
    }

    protected DigestSignatureSpi(ASN1ObjectIdentifier aSN1ObjectIdentifier, Digest digest, AsymmetricBlockCipher asymmetricBlockCipher) {
        this.digest = digest;
        this.cipher = asymmetricBlockCipher;
        this.algId = new AlgorithmIdentifier(aSN1ObjectIdentifier, DERNull.INSTANCE);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof RSAPublicKey)) {
            throw new InvalidKeyException("Supplied key (" + getType(publicKey) + ") is not a RSAPublicKey instance");
        }
        RSAKeyParameters rSAKeyParametersGeneratePublicKeyParameter = RSAUtil.generatePublicKeyParameter((RSAPublicKey) publicKey);
        this.digest.reset();
        this.cipher.init(false, rSAKeyParametersGeneratePublicKeyParameter);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (!(privateKey instanceof RSAPrivateKey)) {
            throw new InvalidKeyException("Supplied key (" + getType(privateKey) + ") is not a RSAPrivateKey instance");
        }
        RSAKeyParameters rSAKeyParametersGeneratePrivateKeyParameter = RSAUtil.generatePrivateKeyParameter((RSAPrivateKey) privateKey);
        this.digest.reset();
        this.cipher.init(true, rSAKeyParametersGeneratePrivateKeyParameter);
    }

    private String getType(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.getClass().getName();
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
            byte[] bArrDerEncode = derEncode(bArr);
            return this.cipher.processBlock(bArrDerEncode, 0, bArrDerEncode.length);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SignatureException("key too small for signature type");
        } catch (Exception e2) {
            throw new SignatureException(e2.toString());
        }
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] bArr) throws SignatureException {
        byte[] bArr2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(bArr2, 0);
        try {
            byte[] bArrProcessBlock = this.cipher.processBlock(bArr, 0, bArr.length);
            byte[] bArrDerEncode = derEncode(bArr2);
            if (bArrProcessBlock.length == bArrDerEncode.length) {
                return Arrays.constantTimeAreEqual(bArrProcessBlock, bArrDerEncode);
            }
            if (bArrProcessBlock.length != bArrDerEncode.length - 2) {
                Arrays.constantTimeAreEqual(bArrDerEncode, bArrDerEncode);
                return false;
            }
            bArrDerEncode[1] = (byte) (bArrDerEncode[1] - 2);
            bArrDerEncode[3] = (byte) (bArrDerEncode[3] - 2);
            int i = 4 + bArrDerEncode[3];
            int i2 = i + 2;
            int i3 = 0;
            for (int i4 = 0; i4 < bArrDerEncode.length - i2; i4++) {
                i3 |= bArrProcessBlock[i + i4] ^ bArrDerEncode[i2 + i4];
            }
            for (int i5 = 0; i5 < i; i5++) {
                i3 |= bArrProcessBlock[i5] ^ bArrDerEncode[i5];
            }
            return i3 == 0;
        } catch (Exception e) {
            return false;
        }
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
        return null;
    }

    @Override // java.security.SignatureSpi
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }

    private byte[] derEncode(byte[] bArr) throws IOException {
        return this.algId == null ? bArr : new DigestInfo(this.algId, bArr).getEncoded("DER");
    }
}
