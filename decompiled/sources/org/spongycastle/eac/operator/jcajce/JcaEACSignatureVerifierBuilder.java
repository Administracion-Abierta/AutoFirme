package org.spongycastle.eac.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.eac.EACObjectIdentifiers;
import org.spongycastle.eac.operator.EACSignatureVerifier;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.OperatorStreamException;
import org.spongycastle.operator.RuntimeOperatorException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/eac/operator/jcajce/JcaEACSignatureVerifierBuilder.class */
public class JcaEACSignatureVerifierBuilder {
    private EACHelper helper = new DefaultEACHelper();

    public JcaEACSignatureVerifierBuilder setProvider(String providerName) {
        this.helper = new NamedEACHelper(providerName);
        return this;
    }

    public JcaEACSignatureVerifierBuilder setProvider(Provider provider) {
        this.helper = new ProviderEACHelper(provider);
        return this;
    }

    public EACSignatureVerifier build(final ASN1ObjectIdentifier usageOid, PublicKey pubKey) throws OperatorCreationException {
        try {
            Signature sig = this.helper.getSignature(usageOid);
            sig.initVerify(pubKey);
            final SignatureOutputStream sigStream = new SignatureOutputStream(sig);
            return new EACSignatureVerifier() { // from class: org.spongycastle.eac.operator.jcajce.JcaEACSignatureVerifierBuilder.1
                @Override // org.spongycastle.eac.operator.EACSignatureVerifier
                public ASN1ObjectIdentifier getUsageIdentifier() {
                    return usageOid;
                }

                @Override // org.spongycastle.eac.operator.EACSignatureVerifier
                public OutputStream getOutputStream() {
                    return sigStream;
                }

                @Override // org.spongycastle.eac.operator.EACSignatureVerifier
                public boolean verify(byte[] expected) {
                    try {
                        if (usageOid.on(EACObjectIdentifiers.id_TA_ECDSA)) {
                            try {
                                byte[] reencoded = JcaEACSignatureVerifierBuilder.derEncode(expected);
                                return sigStream.verify(reencoded);
                            } catch (Exception e) {
                                return false;
                            }
                        }
                        return sigStream.verify(expected);
                    } catch (SignatureException e2) {
                        throw new RuntimeOperatorException("exception obtaining signature: " + e2.getMessage(), e2);
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

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] derEncode(byte[] rawSign) throws IOException {
        int len = rawSign.length / 2;
        byte[] r = new byte[len];
        byte[] s = new byte[len];
        System.arraycopy(rawSign, 0, r, 0, len);
        System.arraycopy(rawSign, len, s, 0, len);
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(new BigInteger(1, r)));
        v.add(new ASN1Integer(new BigInteger(1, s)));
        DERSequence seq = new DERSequence(v);
        return seq.getEncoded();
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/eac/operator/jcajce/JcaEACSignatureVerifierBuilder$SignatureOutputStream.class */
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

        boolean verify(byte[] expected) throws SignatureException {
            return this.sig.verify(expected);
        }
    }
}
