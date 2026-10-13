package org.bouncycastle.crypto.signers;

import java.io.IOException;
import java.util.Hashtable;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/signers/RSADigestSigner.class */
public class RSADigestSigner implements Signer {
    private final AsymmetricBlockCipher rsaEngine;
    private final AlgorithmIdentifier algId;
    private final Digest digest;
    private boolean forSigning;
    private static final Hashtable oidMap = new Hashtable();

    public RSADigestSigner(Digest digest) {
        this(digest, (ASN1ObjectIdentifier) oidMap.get(digest.getAlgorithmName()));
    }

    public RSADigestSigner(Digest digest, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.rsaEngine = new PKCS1Encoding(new RSABlindedEngine());
        this.digest = digest;
        if (aSN1ObjectIdentifier != null) {
            this.algId = new AlgorithmIdentifier(aSN1ObjectIdentifier, DERNull.INSTANCE);
        } else {
            this.algId = null;
        }
    }

    public String getAlgorithmName() {
        return this.digest.getAlgorithmName() + "withRSA";
    }

    @Override // org.bouncycastle.crypto.Signer
    public void init(boolean z, CipherParameters cipherParameters) {
        this.forSigning = z;
        AsymmetricKeyParameter asymmetricKeyParameter = cipherParameters instanceof ParametersWithRandom ? (AsymmetricKeyParameter) ((ParametersWithRandom) cipherParameters).getParameters() : (AsymmetricKeyParameter) cipherParameters;
        if (z && !asymmetricKeyParameter.isPrivate()) {
            throw new IllegalArgumentException("signing requires private key");
        }
        if (!z && asymmetricKeyParameter.isPrivate()) {
            throw new IllegalArgumentException("verification requires public key");
        }
        reset();
        this.rsaEngine.init(z, cipherParameters);
    }

    @Override // org.bouncycastle.crypto.Signer
    public void update(byte b) {
        this.digest.update(b);
    }

    @Override // org.bouncycastle.crypto.Signer
    public void update(byte[] bArr, int i, int i2) {
        this.digest.update(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Signer
    public byte[] generateSignature() throws DataLengthException, CryptoException {
        if (!this.forSigning) {
            throw new IllegalStateException("RSADigestSigner not initialised for signature generation.");
        }
        byte[] bArr = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(bArr, 0);
        try {
            byte[] bArrDerEncode = derEncode(bArr);
            return this.rsaEngine.processBlock(bArrDerEncode, 0, bArrDerEncode.length);
        } catch (IOException e) {
            throw new CryptoException("unable to encode signature: " + e.getMessage(), e);
        }
    }

    @Override // org.bouncycastle.crypto.Signer
    public boolean verifySignature(byte[] bArr) {
        if (this.forSigning) {
            throw new IllegalStateException("RSADigestSigner not initialised for verification");
        }
        byte[] bArr2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(bArr2, 0);
        try {
            byte[] bArrProcessBlock = this.rsaEngine.processBlock(bArr, 0, bArr.length);
            byte[] bArrDerEncode = derEncode(bArr2);
            if (bArrProcessBlock.length == bArrDerEncode.length) {
                return Arrays.constantTimeAreEqual(bArrProcessBlock, bArrDerEncode);
            }
            if (bArrProcessBlock.length != bArrDerEncode.length - 2) {
                Arrays.constantTimeAreEqual(bArrDerEncode, bArrDerEncode);
                return false;
            }
            int length = (bArrProcessBlock.length - bArr2.length) - 2;
            int length2 = (bArrDerEncode.length - bArr2.length) - 2;
            bArrDerEncode[1] = (byte) (bArrDerEncode[1] - 2);
            bArrDerEncode[3] = (byte) (bArrDerEncode[3] - 2);
            int i = 0;
            for (int i2 = 0; i2 < bArr2.length; i2++) {
                i |= bArrProcessBlock[length + i2] ^ bArrDerEncode[length2 + i2];
            }
            for (int i3 = 0; i3 < length; i3++) {
                i |= bArrProcessBlock[i3] ^ bArrDerEncode[i3];
            }
            return i == 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override // org.bouncycastle.crypto.Signer
    public void reset() {
        this.digest.reset();
    }

    private byte[] derEncode(byte[] bArr) throws IOException {
        if (this.algId != null) {
            return new DigestInfo(this.algId, bArr).getEncoded("DER");
        }
        try {
            DigestInfo.getInstance(bArr);
            return bArr;
        } catch (IllegalArgumentException e) {
            throw new IOException("malformed DigestInfo for NONEwithRSA hash: " + e.getMessage());
        }
    }

    static {
        oidMap.put("RIPEMD128", TeleTrusTObjectIdentifiers.ripemd128);
        oidMap.put("RIPEMD160", TeleTrusTObjectIdentifiers.ripemd160);
        oidMap.put("RIPEMD256", TeleTrusTObjectIdentifiers.ripemd256);
        oidMap.put("SHA-1", X509ObjectIdentifiers.id_SHA1);
        oidMap.put("SHA-224", NISTObjectIdentifiers.id_sha224);
        oidMap.put("SHA-256", NISTObjectIdentifiers.id_sha256);
        oidMap.put("SHA-384", NISTObjectIdentifiers.id_sha384);
        oidMap.put("SHA-512", NISTObjectIdentifiers.id_sha512);
        oidMap.put(MessageDigestAlgorithms.SHA_512_224, NISTObjectIdentifiers.id_sha512_224);
        oidMap.put("SHA-512/256", NISTObjectIdentifiers.id_sha512_256);
        oidMap.put(MessageDigestAlgorithms.SHA3_224, NISTObjectIdentifiers.id_sha3_224);
        oidMap.put("SHA3-256", NISTObjectIdentifiers.id_sha3_256);
        oidMap.put(MessageDigestAlgorithms.SHA3_384, NISTObjectIdentifiers.id_sha3_384);
        oidMap.put(MessageDigestAlgorithms.SHA3_512, NISTObjectIdentifiers.id_sha3_512);
        oidMap.put(MessageDigestAlgorithms.MD2, PKCSObjectIdentifiers.md2);
        oidMap.put("MD4", PKCSObjectIdentifiers.md4);
        oidMap.put(MessageDigestAlgorithms.MD5, PKCSObjectIdentifiers.md5);
    }
}
