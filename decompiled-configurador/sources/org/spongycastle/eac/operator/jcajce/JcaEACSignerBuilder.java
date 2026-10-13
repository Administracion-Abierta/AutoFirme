package org.spongycastle.eac.operator.jcajce;

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
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.eac.EACObjectIdentifiers;
import org.spongycastle.eac.operator.EACSigner;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.OperatorStreamException;
import org.spongycastle.operator.RuntimeOperatorException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/eac/operator/jcajce/JcaEACSignerBuilder.class */
public class JcaEACSignerBuilder {
    private static final Hashtable sigNames = new Hashtable();
    private EACHelper helper = new DefaultEACHelper();

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

    public JcaEACSignerBuilder setProvider(String providerName) {
        this.helper = new NamedEACHelper(providerName);
        return this;
    }

    public JcaEACSignerBuilder setProvider(Provider provider) {
        this.helper = new ProviderEACHelper(provider);
        return this;
    }

    public EACSigner build(String algorithm, PrivateKey privKey) throws OperatorCreationException {
        return build((ASN1ObjectIdentifier) sigNames.get(algorithm), privKey);
    }

    public EACSigner build(final ASN1ObjectIdentifier usageOid, PrivateKey privKey) throws OperatorCreationException {
        try {
            Signature sig = this.helper.getSignature(usageOid);
            sig.initSign(privKey);
            final SignatureOutputStream sigStream = new SignatureOutputStream(sig);
            return new EACSigner() { // from class: org.spongycastle.eac.operator.jcajce.JcaEACSignerBuilder.1
                @Override // org.spongycastle.eac.operator.EACSigner
                public ASN1ObjectIdentifier getUsageIdentifier() {
                    return usageOid;
                }

                @Override // org.spongycastle.eac.operator.EACSigner
                public OutputStream getOutputStream() {
                    return sigStream;
                }

                @Override // org.spongycastle.eac.operator.EACSigner
                public byte[] getSignature() {
                    try {
                        byte[] signature = sigStream.getSignature();
                        if (usageOid.on(EACObjectIdentifiers.id_TA_ECDSA)) {
                            return JcaEACSignerBuilder.reencode(signature);
                        }
                        return signature;
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

    public static int max(int el1, int el2) {
        return el1 > el2 ? el1 : el2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] reencode(byte[] rawSign) {
        ASN1Sequence sData = ASN1Sequence.getInstance(rawSign);
        BigInteger r = ASN1Integer.getInstance(sData.getObjectAt(0)).getValue();
        BigInteger s = ASN1Integer.getInstance(sData.getObjectAt(1)).getValue();
        byte[] rB = r.toByteArray();
        byte[] sB = s.toByteArray();
        int rLen = unsignedIntLength(rB);
        int sLen = unsignedIntLength(sB);
        int len = max(rLen, sLen);
        byte[] ret = new byte[len * 2];
        Arrays.fill(ret, (byte) 0);
        copyUnsignedInt(rB, ret, len - rLen);
        copyUnsignedInt(sB, ret, (2 * len) - sLen);
        return ret;
    }

    private static int unsignedIntLength(byte[] i) {
        int len = i.length;
        if (i[0] == 0) {
            len--;
        }
        return len;
    }

    private static void copyUnsignedInt(byte[] src, byte[] dst, int offset) {
        int len = src.length;
        int readoffset = 0;
        if (src[0] == 0) {
            len--;
            readoffset = 1;
        }
        System.arraycopy(src, readoffset, dst, offset, len);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/eac/operator/jcajce/JcaEACSignerBuilder$SignatureOutputStream.class */
    private class SignatureOutputStream extends OutputStream {
        private Signature sig;

        SignatureOutputStream(Signature sig) {
            this.sig = sig;
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes, int off, int len) throws IOException {
            try {
                this.sig.update(bytes, off, len);
            } catch (SignatureException e) {
                throw new OperatorStreamException("exception in content signer: " + e.getMessage(), e);
            }
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes) throws IOException {
            try {
                this.sig.update(bytes);
            } catch (SignatureException e) {
                throw new OperatorStreamException("exception in content signer: " + e.getMessage(), e);
            }
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            try {
                this.sig.update((byte) b);
            } catch (SignatureException e) {
                throw new OperatorStreamException("exception in content signer: " + e.getMessage(), e);
            }
        }

        byte[] getSignature() throws SignatureException {
            return this.sig.sign();
        }
    }
}
