package org.bouncycastle.eac.operator.jcajce;

import es.gob.afirma.core.signers.AOSignConstants;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.eac.operator.EACSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OperatorStreamException;
import org.bouncycastle.operator.RuntimeOperatorException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/eac/operator/jcajce/JcaEACSignerBuilder.class */
public class JcaEACSignerBuilder {
    private static final Hashtable sigNames = new Hashtable();
    private EACHelper helper = new DefaultEACHelper();

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/eac/operator/jcajce/JcaEACSignerBuilder$SignatureOutputStream.class */
    private static class SignatureOutputStream extends OutputStream {
        private Signature sig;

        SignatureOutputStream(Signature signature) {
            this.sig = signature;
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr, int i, int i2) throws IOException {
            try {
                this.sig.update(bArr, i, i2);
            } catch (SignatureException e) {
                throw new OperatorStreamException("exception in content signer: " + e.getMessage(), e);
            }
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr) throws IOException {
            try {
                this.sig.update(bArr);
            } catch (SignatureException e) {
                throw new OperatorStreamException("exception in content signer: " + e.getMessage(), e);
            }
        }

        @Override // java.io.OutputStream
        public void write(int i) throws IOException {
            try {
                this.sig.update((byte) i);
            } catch (SignatureException e) {
                throw new OperatorStreamException("exception in content signer: " + e.getMessage(), e);
            }
        }

        byte[] getSignature() throws SignatureException {
            return this.sig.sign();
        }
    }

    public JcaEACSignerBuilder setProvider(String str) {
        this.helper = new NamedEACHelper(str);
        return this;
    }

    public JcaEACSignerBuilder setProvider(Provider provider) {
        this.helper = new ProviderEACHelper(provider);
        return this;
    }

    public EACSigner build(String str, PrivateKey privateKey) throws OperatorCreationException {
        return build((ASN1ObjectIdentifier) sigNames.get(str), privateKey);
    }

    public EACSigner build(final ASN1ObjectIdentifier aSN1ObjectIdentifier, PrivateKey privateKey) throws OperatorCreationException {
        try {
            Signature signature = this.helper.getSignature(aSN1ObjectIdentifier);
            signature.initSign(privateKey);
            final SignatureOutputStream signatureOutputStream = new SignatureOutputStream(signature);
            return new EACSigner() { // from class: org.bouncycastle.eac.operator.jcajce.JcaEACSignerBuilder.1
                @Override // org.bouncycastle.eac.operator.EACSigner
                public ASN1ObjectIdentifier getUsageIdentifier() {
                    return aSN1ObjectIdentifier;
                }

                @Override // org.bouncycastle.eac.operator.EACSigner
                public OutputStream getOutputStream() {
                    return signatureOutputStream;
                }

                @Override // org.bouncycastle.eac.operator.EACSigner
                public byte[] getSignature() {
                    try {
                        byte[] signature2 = signatureOutputStream.getSignature();
                        return aSN1ObjectIdentifier.on(EACObjectIdentifiers.id_TA_ECDSA) ? JcaEACSignerBuilder.reencode(signature2) : signature2;
                    } catch (SignatureException e) {
                        throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
                    }
                }
            };
        } catch (InvalidKeyException e) {
            throw new OperatorCreationException("invalid key: " + e.getMessage(), e);
        } catch (NoSuchAlgorithmException e2) {
            throw new OperatorCreationException("unable to find algorithm: " + e2.getMessage(), e2);
        } catch (NoSuchProviderException e3) {
            throw new OperatorCreationException("unable to find provider: " + e3.getMessage(), e3);
        }
    }

    public static int max(int i, int i2) {
        return i > i2 ? i : i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] reencode(byte[] bArr) {
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(bArr);
        BigInteger value = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).getValue();
        BigInteger value2 = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1)).getValue();
        byte[] byteArray = value.toByteArray();
        byte[] byteArray2 = value2.toByteArray();
        int iUnsignedIntLength = unsignedIntLength(byteArray);
        int iUnsignedIntLength2 = unsignedIntLength(byteArray2);
        int iMax = max(iUnsignedIntLength, iUnsignedIntLength2);
        if ((iMax & 1) != 0) {
            iMax++;
        }
        byte[] bArr2 = new byte[iMax * 2];
        Arrays.fill(bArr2, (byte) 0);
        copyUnsignedInt(byteArray, bArr2, iMax - iUnsignedIntLength);
        copyUnsignedInt(byteArray2, bArr2, (2 * iMax) - iUnsignedIntLength2);
        return bArr2;
    }

    private static int unsignedIntLength(byte[] bArr) {
        int length = bArr.length;
        if (bArr[0] == 0) {
            length--;
        }
        return length;
    }

    private static void copyUnsignedInt(byte[] bArr, byte[] bArr2, int i) {
        int length = bArr.length;
        int i2 = 0;
        if (bArr[0] == 0) {
            length--;
            i2 = 1;
        }
        System.arraycopy(bArr, i2, bArr2, i, length);
    }

    static {
        sigNames.put(AOSignConstants.SIGN_ALGORITHM_SHA1WITHRSA, EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_1);
        sigNames.put(AOSignConstants.SIGN_ALGORITHM_SHA256WITHRSA, EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_256);
        sigNames.put("SHA1withRSAandMGF1", EACObjectIdentifiers.id_TA_RSA_PSS_SHA_1);
        sigNames.put("SHA256withRSAandMGF1", EACObjectIdentifiers.id_TA_RSA_PSS_SHA_256);
        sigNames.put("SHA512withRSA", EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_512);
        sigNames.put("SHA512withRSAandMGF1", EACObjectIdentifiers.id_TA_RSA_PSS_SHA_512);
        sigNames.put(AOSignConstants.SIGN_ALGORITHM_SHA1WITHECDSA, EACObjectIdentifiers.id_TA_ECDSA_SHA_1);
        sigNames.put(AOSignConstants.SIGN_ALGORITHM_SHA224WITHECDSA, EACObjectIdentifiers.id_TA_ECDSA_SHA_224);
        sigNames.put(AOSignConstants.SIGN_ALGORITHM_SHA256WITHECDSA, EACObjectIdentifiers.id_TA_ECDSA_SHA_256);
        sigNames.put(AOSignConstants.SIGN_ALGORITHM_SHA384WITHECDSA, EACObjectIdentifiers.id_TA_ECDSA_SHA_384);
        sigNames.put(AOSignConstants.SIGN_ALGORITHM_SHA512WITHECDSA, EACObjectIdentifiers.id_TA_ECDSA_SHA_512);
    }
}
