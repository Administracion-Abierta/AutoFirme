package org.bouncycastle.jcajce.provider.asymmetric.dstu;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ua.DSTU4145Params;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSTU4145Signer;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.interfaces.ECKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dstu/SignatureSpi.class */
public class SignatureSpi extends java.security.SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
    private Digest digest;
    private DSAExt signer = new DSTU4145Signer();

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        AsymmetricKeyParameter asymmetricKeyParameterGeneratePublicKeyParameter;
        if (publicKey instanceof BCDSTU4145PublicKey) {
            asymmetricKeyParameterGeneratePublicKeyParameter = ((BCDSTU4145PublicKey) publicKey).engineGetKeyParameters();
            this.digest = new GOST3411Digest(expandSbox(((BCDSTU4145PublicKey) publicKey).getSbox()));
        } else {
            asymmetricKeyParameterGeneratePublicKeyParameter = ECUtil.generatePublicKeyParameter(publicKey);
            this.digest = new GOST3411Digest(expandSbox(DSTU4145Params.getDefaultDKE()));
        }
        this.signer.init(false, asymmetricKeyParameterGeneratePublicKeyParameter);
    }

    byte[] expandSbox(byte[] bArr) {
        byte[] bArr2 = new byte[128];
        for (int i = 0; i < bArr.length; i++) {
            bArr2[i * 2] = (byte) ((bArr[i] >> 4) & 15);
            bArr2[(i * 2) + 1] = (byte) (bArr[i] & 15);
        }
        return bArr2;
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        AsymmetricKeyParameter asymmetricKeyParameterGeneratePrivateKeyParameter = null;
        if ((privateKey instanceof BCDSTU4145PrivateKey) || (privateKey instanceof ECKey)) {
            asymmetricKeyParameterGeneratePrivateKeyParameter = ECUtil.generatePrivateKeyParameter(privateKey);
            this.digest = new GOST3411Digest(expandSbox(DSTU4145Params.getDefaultDKE()));
        }
        if (this.appRandom != null) {
            this.signer.init(true, new ParametersWithRandom(asymmetricKeyParameterGeneratePrivateKeyParameter, this.appRandom));
        } else {
            this.signer.init(true, asymmetricKeyParameterGeneratePrivateKeyParameter);
        }
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
            byte[] byteArray = bigIntegerArrGenerateSignature[0].toByteArray();
            byte[] byteArray2 = bigIntegerArrGenerateSignature[1].toByteArray();
            byte[] bArr2 = new byte[byteArray.length > byteArray2.length ? byteArray.length * 2 : byteArray2.length * 2];
            System.arraycopy(byteArray2, 0, bArr2, (bArr2.length / 2) - byteArray2.length, byteArray2.length);
            System.arraycopy(byteArray, 0, bArr2, bArr2.length - byteArray.length, byteArray.length);
            return new DEROctetString(bArr2).getEncoded();
        } catch (Exception e) {
            throw new SignatureException(e.toString());
        }
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] bArr) throws SignatureException {
        byte[] bArr2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(bArr2, 0);
        try {
            byte[] octets = ((ASN1OctetString) ASN1OctetString.fromByteArray(bArr)).getOctets();
            byte[] bArr3 = new byte[octets.length / 2];
            byte[] bArr4 = new byte[octets.length / 2];
            System.arraycopy(octets, 0, bArr4, 0, octets.length / 2);
            System.arraycopy(octets, octets.length / 2, bArr3, 0, octets.length / 2);
            BigInteger[] bigIntegerArr = {new BigInteger(1, bArr3), new BigInteger(1, bArr4)};
            return this.signer.verifySignature(bArr2, bigIntegerArr[0], bigIntegerArr[1]);
        } catch (Exception e) {
            throw new SignatureException("error decoding signature bytes.");
        }
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(AlgorithmParameterSpec algorithmParameterSpec) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(String str, Object obj) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected Object engineGetParameter(String str) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }
}
