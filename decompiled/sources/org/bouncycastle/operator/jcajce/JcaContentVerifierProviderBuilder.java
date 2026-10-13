package org.bouncycastle.operator.jcajce;

import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jcajce.CompositePublicKey;
import org.bouncycastle.jcajce.io.OutputStreamFactory;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.RawContentVerifier;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.util.io.TeeOutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/jcajce/JcaContentVerifierProviderBuilder.class */
public class JcaContentVerifierProviderBuilder {
    private OperatorHelper helper = new OperatorHelper(new DefaultJcaJceHelper());

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/jcajce/JcaContentVerifierProviderBuilder$CompositeVerifier.class */
    private static class CompositeVerifier implements ContentVerifier {
        private Signature[] sigs;
        private OutputStream stream;

        public CompositeVerifier(Signature[] signatureArr) throws OperatorCreationException {
            this.sigs = signatureArr;
            int i = 0;
            while (i < signatureArr.length && signatureArr[i] == null) {
                i++;
            }
            if (i == signatureArr.length) {
                throw new OperatorCreationException("no matching signature found in composite");
            }
            this.stream = OutputStreamFactory.createStream(signatureArr[i]);
            for (int i2 = i + 1; i2 != signatureArr.length; i2++) {
                if (signatureArr[i2] != null) {
                    this.stream = new TeeOutputStream(this.stream, OutputStreamFactory.createStream(signatureArr[i2]));
                }
            }
        }

        @Override // org.bouncycastle.operator.ContentVerifier
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(MiscObjectIdentifiers.id_alg_composite);
        }

        @Override // org.bouncycastle.operator.ContentVerifier
        public OutputStream getOutputStream() {
            return this.stream;
        }

        @Override // org.bouncycastle.operator.ContentVerifier
        public boolean verify(byte[] bArr) {
            try {
                ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(bArr);
                boolean z = false;
                for (int i = 0; i != aSN1Sequence.size(); i++) {
                    if (this.sigs[i] != null && !this.sigs[i].verify(ASN1BitString.getInstance(aSN1Sequence.getObjectAt(i)).getOctets())) {
                        z = true;
                    }
                }
                return !z;
            } catch (SignatureException e) {
                throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/jcajce/JcaContentVerifierProviderBuilder$RawSigVerifier.class */
    private static class RawSigVerifier extends SigVerifier implements RawContentVerifier {
        private Signature rawSignature;

        RawSigVerifier(AlgorithmIdentifier algorithmIdentifier, Signature signature, Signature signature2) {
            super(algorithmIdentifier, signature);
            this.rawSignature = signature2;
        }

        @Override // org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder.SigVerifier, org.bouncycastle.operator.ContentVerifier
        public boolean verify(byte[] bArr) {
            try {
                return super.verify(bArr);
            } finally {
                try {
                    this.rawSignature.verify(bArr);
                } catch (Exception e) {
                }
            }
        }

        @Override // org.bouncycastle.operator.RawContentVerifier
        public boolean verify(byte[] bArr, byte[] bArr2) {
            try {
                try {
                    this.rawSignature.update(bArr);
                    boolean zVerify = this.rawSignature.verify(bArr2);
                    try {
                        this.rawSignature.verify(bArr2);
                    } catch (Exception e) {
                    }
                    return zVerify;
                } catch (Throwable th) {
                    try {
                        this.rawSignature.verify(bArr2);
                    } catch (Exception e2) {
                    }
                    throw th;
                }
            } catch (SignatureException e3) {
                throw new RuntimeOperatorException("exception obtaining raw signature: " + e3.getMessage(), e3);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/jcajce/JcaContentVerifierProviderBuilder$SigVerifier.class */
    private static class SigVerifier implements ContentVerifier {
        private final AlgorithmIdentifier algorithm;
        private final Signature signature;
        protected final OutputStream stream;

        SigVerifier(AlgorithmIdentifier algorithmIdentifier, Signature signature) {
            this.algorithm = algorithmIdentifier;
            this.signature = signature;
            this.stream = OutputStreamFactory.createStream(signature);
        }

        @Override // org.bouncycastle.operator.ContentVerifier
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithm;
        }

        @Override // org.bouncycastle.operator.ContentVerifier
        public OutputStream getOutputStream() {
            if (this.stream == null) {
                throw new IllegalStateException("verifier not initialised");
            }
            return this.stream;
        }

        @Override // org.bouncycastle.operator.ContentVerifier
        public boolean verify(byte[] bArr) {
            try {
                return this.signature.verify(bArr);
            } catch (SignatureException e) {
                throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
            }
        }
    }

    public JcaContentVerifierProviderBuilder setProvider(Provider provider) {
        this.helper = new OperatorHelper(new ProviderJcaJceHelper(provider));
        return this;
    }

    public JcaContentVerifierProviderBuilder setProvider(String str) {
        this.helper = new OperatorHelper(new NamedJcaJceHelper(str));
        return this;
    }

    public ContentVerifierProvider build(X509CertificateHolder x509CertificateHolder) throws OperatorCreationException, CertificateException {
        return build(this.helper.convertCertificate(x509CertificateHolder));
    }

    public ContentVerifierProvider build(final X509Certificate x509Certificate) throws OperatorCreationException {
        try {
            final JcaX509CertificateHolder jcaX509CertificateHolder = new JcaX509CertificateHolder(x509Certificate);
            return new ContentVerifierProvider() { // from class: org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder.1
                @Override // org.bouncycastle.operator.ContentVerifierProvider
                public boolean hasAssociatedCertificate() {
                    return true;
                }

                @Override // org.bouncycastle.operator.ContentVerifierProvider
                public X509CertificateHolder getAssociatedCertificate() {
                    return jcaX509CertificateHolder;
                }

                @Override // org.bouncycastle.operator.ContentVerifierProvider
                public ContentVerifier get(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                    if (algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) MiscObjectIdentifiers.id_alg_composite)) {
                        return JcaContentVerifierProviderBuilder.this.createCompositeVerifier(algorithmIdentifier, x509Certificate.getPublicKey());
                    }
                    try {
                        Signature signatureCreateSignature = JcaContentVerifierProviderBuilder.this.helper.createSignature(algorithmIdentifier);
                        signatureCreateSignature.initVerify(x509Certificate.getPublicKey());
                        Signature signatureCreateRawSig = JcaContentVerifierProviderBuilder.this.createRawSig(algorithmIdentifier, x509Certificate.getPublicKey());
                        return signatureCreateRawSig != null ? new RawSigVerifier(algorithmIdentifier, signatureCreateSignature, signatureCreateRawSig) : new SigVerifier(algorithmIdentifier, signatureCreateSignature);
                    } catch (GeneralSecurityException e) {
                        throw new OperatorCreationException("exception on setup: " + e, e);
                    }
                }
            };
        } catch (CertificateEncodingException e) {
            throw new OperatorCreationException("cannot process certificate: " + e.getMessage(), e);
        }
    }

    public ContentVerifierProvider build(final PublicKey publicKey) throws OperatorCreationException {
        return new ContentVerifierProvider() { // from class: org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder.2
            @Override // org.bouncycastle.operator.ContentVerifierProvider
            public boolean hasAssociatedCertificate() {
                return false;
            }

            @Override // org.bouncycastle.operator.ContentVerifierProvider
            public X509CertificateHolder getAssociatedCertificate() {
                return null;
            }

            @Override // org.bouncycastle.operator.ContentVerifierProvider
            public ContentVerifier get(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                if (algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) MiscObjectIdentifiers.id_alg_composite)) {
                    return JcaContentVerifierProviderBuilder.this.createCompositeVerifier(algorithmIdentifier, publicKey);
                }
                if (!(publicKey instanceof CompositePublicKey) || !((CompositePublicKey) publicKey).getAlgorithmIdentifier().equals((ASN1Primitive) MiscObjectIdentifiers.id_composite_key)) {
                    Signature signatureCreateSignature = JcaContentVerifierProviderBuilder.this.createSignature(algorithmIdentifier, publicKey);
                    Signature signatureCreateRawSig = JcaContentVerifierProviderBuilder.this.createRawSig(algorithmIdentifier, publicKey);
                    return signatureCreateRawSig != null ? new RawSigVerifier(algorithmIdentifier, signatureCreateSignature, signatureCreateRawSig) : new SigVerifier(algorithmIdentifier, signatureCreateSignature);
                }
                List<PublicKey> publicKeys = ((CompositePublicKey) publicKey).getPublicKeys();
                for (int i = 0; i != publicKeys.size(); i++) {
                    try {
                        Signature signatureCreateSignature2 = JcaContentVerifierProviderBuilder.this.createSignature(algorithmIdentifier, publicKeys.get(i));
                        Signature signatureCreateRawSig2 = JcaContentVerifierProviderBuilder.this.createRawSig(algorithmIdentifier, publicKeys.get(i));
                        return signatureCreateRawSig2 != null ? new RawSigVerifier(algorithmIdentifier, signatureCreateSignature2, signatureCreateRawSig2) : new SigVerifier(algorithmIdentifier, signatureCreateSignature2);
                    } catch (OperatorCreationException e) {
                    }
                }
                throw new OperatorCreationException("no matching algorithm found for key");
            }
        };
    }

    public ContentVerifierProvider build(SubjectPublicKeyInfo subjectPublicKeyInfo) throws OperatorCreationException {
        return build(this.helper.convertPublicKey(subjectPublicKeyInfo));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ContentVerifier createCompositeVerifier(AlgorithmIdentifier algorithmIdentifier, PublicKey publicKey) throws OperatorCreationException {
        if (!(publicKey instanceof CompositePublicKey)) {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(algorithmIdentifier.getParameters());
            Signature[] signatureArr = new Signature[aSN1Sequence.size()];
            for (int i = 0; i != aSN1Sequence.size(); i++) {
                try {
                    signatureArr[i] = createSignature(AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(i)), publicKey);
                } catch (Exception e) {
                    signatureArr[i] = null;
                }
            }
            return new CompositeVerifier(signatureArr);
        }
        List<PublicKey> publicKeys = ((CompositePublicKey) publicKey).getPublicKeys();
        ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(algorithmIdentifier.getParameters());
        Signature[] signatureArr2 = new Signature[aSN1Sequence2.size()];
        for (int i2 = 0; i2 != aSN1Sequence2.size(); i2++) {
            AlgorithmIdentifier algorithmIdentifier2 = AlgorithmIdentifier.getInstance(aSN1Sequence2.getObjectAt(i2));
            if (publicKeys.get(i2) != null) {
                signatureArr2[i2] = createSignature(algorithmIdentifier2, publicKeys.get(i2));
            } else {
                signatureArr2[i2] = null;
            }
        }
        return new CompositeVerifier(signatureArr2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Signature createSignature(AlgorithmIdentifier algorithmIdentifier, PublicKey publicKey) throws OperatorCreationException {
        try {
            Signature signatureCreateSignature = this.helper.createSignature(algorithmIdentifier);
            signatureCreateSignature.initVerify(publicKey);
            return signatureCreateSignature;
        } catch (GeneralSecurityException e) {
            throw new OperatorCreationException("exception on setup: " + e, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Signature createRawSig(AlgorithmIdentifier algorithmIdentifier, PublicKey publicKey) {
        Signature signatureCreateRawSignature;
        try {
            signatureCreateRawSignature = this.helper.createRawSignature(algorithmIdentifier);
            if (signatureCreateRawSignature != null) {
                signatureCreateRawSignature.initVerify(publicKey);
            }
        } catch (Exception e) {
            signatureCreateRawSignature = null;
        }
        return signatureCreateRawSignature;
    }
}
