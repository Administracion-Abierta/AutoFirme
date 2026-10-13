package org.spongycastle.jcajce.provider.asymmetric.dstu;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.X509ObjectIdentifiers;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DSA;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.GOST3411Digest;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.signers.DSTU4145Signer;
import org.spongycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.spongycastle.jce.interfaces.ECKey;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dstu/SignatureSpi.class */
public class SignatureSpi extends java.security.SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
    private Digest digest;
    private DSA signer = new DSTU4145Signer();
    private static byte[] DEFAULT_SBOX = {10, 9, 13, 6, 14, 11, 4, 5, 15, 1, 3, 12, 7, 0, 8, 2, 8, 0, 12, 4, 9, 6, 7, 11, 2, 3, 1, 15, 5, 14, 10, 13, 15, 6, 5, 8, 14, 11, 10, 4, 12, 0, 3, 7, 2, 9, 1, 13, 3, 8, 13, 9, 6, 11, 15, 0, 2, 5, 12, 10, 4, 14, 1, 7, 15, 8, 14, 9, 7, 2, 0, 13, 12, 6, 1, 5, 11, 4, 3, 10, 2, 8, 9, 7, 5, 15, 0, 11, 12, 1, 13, 14, 10, 3, 6, 4, 3, 8, 11, 5, 6, 4, 14, 10, 2, 12, 1, 7, 9, 15, 13, 0, 1, 2, 3, 14, 6, 13, 11, 8, 15, 10, 12, 5, 7, 9, 0, 4};

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        CipherParameters param;
        if (publicKey instanceof BCDSTU4145PublicKey) {
            param = ((BCDSTU4145PublicKey) publicKey).engineGetKeyParameters();
        } else {
            param = ECUtil.generatePublicKeyParameter(publicKey);
        }
        this.digest = new GOST3411Digest(expandSbox(((BCDSTU4145PublicKey) publicKey).getSbox()));
        this.signer.init(false, param);
    }

    byte[] expandSbox(byte[] compressed) {
        byte[] expanded = new byte[128];
        for (int i = 0; i < compressed.length; i++) {
            expanded[i * 2] = (byte) ((compressed[i] >> 4) & 15);
            expanded[(i * 2) + 1] = (byte) (compressed[i] & 15);
        }
        return expanded;
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        CipherParameters param = null;
        if (privateKey instanceof ECKey) {
            param = ECUtil.generatePrivateKeyParameter(privateKey);
        }
        this.digest = new GOST3411Digest(DEFAULT_SBOX);
        if (this.appRandom != null) {
            this.signer.init(true, new ParametersWithRandom(param, this.appRandom));
        } else {
            this.signer.init(true, param);
        }
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
            byte[] r = sig[0].toByteArray();
            byte[] s = sig[1].toByteArray();
            byte[] sigBytes = new byte[r.length > s.length ? r.length * 2 : s.length * 2];
            System.arraycopy(s, 0, sigBytes, (sigBytes.length / 2) - s.length, s.length);
            System.arraycopy(r, 0, sigBytes, sigBytes.length - r.length, r.length);
            return new DEROctetString(sigBytes).getEncoded();
        } catch (Exception e) {
            throw new SignatureException(e.toString());
        }
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        try {
            byte[] bytes = ((ASN1OctetString) ASN1OctetString.fromByteArray(sigBytes)).getOctets();
            byte[] r = new byte[bytes.length / 2];
            byte[] s = new byte[bytes.length / 2];
            System.arraycopy(bytes, 0, s, 0, bytes.length / 2);
            System.arraycopy(bytes, bytes.length / 2, r, 0, bytes.length / 2);
            BigInteger[] sig = {new BigInteger(1, r), new BigInteger(1, s)};
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
}
