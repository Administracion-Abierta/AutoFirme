package org.spongycastle.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.jcajce.JcaX509CertificateHolder;
import org.spongycastle.jcajce.util.DefaultJcaJceHelper;
import org.spongycastle.jcajce.util.NamedJcaJceHelper;
import org.spongycastle.jcajce.util.ProviderJcaJceHelper;
import org.spongycastle.operator.ContentVerifier;
import org.spongycastle.operator.ContentVerifierProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.OperatorStreamException;
import org.spongycastle.operator.RawContentVerifier;
import org.spongycastle.operator.RuntimeOperatorException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/jcajce/JcaContentVerifierProviderBuilder.class */
public class JcaContentVerifierProviderBuilder {
    private OperatorHelper helper = new OperatorHelper(new DefaultJcaJceHelper());

    public JcaContentVerifierProviderBuilder setProvider(Provider provider) {
        this.helper = new OperatorHelper(new ProviderJcaJceHelper(provider));
        return this;
    }

    public JcaContentVerifierProviderBuilder setProvider(String providerName) {
        this.helper = new OperatorHelper(new NamedJcaJceHelper(providerName));
        return this;
    }

    public ContentVerifierProvider build(X509CertificateHolder certHolder) throws OperatorCreationException, CertificateException {
        return build(this.helper.convertCertificate(certHolder));
    }

    public ContentVerifierProvider build(final X509Certificate certificate) throws OperatorCreationException {
        try {
            final X509CertificateHolder certHolder = new JcaX509CertificateHolder(certificate);
            return new ContentVerifierProvider() { // from class: org.spongycastle.operator.jcajce.JcaContentVerifierProviderBuilder.1
                private SignatureOutputStream stream;

                @Override // org.spongycastle.operator.ContentVerifierProvider
                public boolean hasAssociatedCertificate() {
                    return true;
                }

                @Override // org.spongycastle.operator.ContentVerifierProvider
                public X509CertificateHolder getAssociatedCertificate() {
                    return certHolder;
                }

                @Override // org.spongycastle.operator.ContentVerifierProvider
                public ContentVerifier get(AlgorithmIdentifier algorithm) throws OperatorCreationException {
                    try {
                        Signature sig = JcaContentVerifierProviderBuilder.this.helper.createSignature(algorithm);
                        sig.initVerify(certificate.getPublicKey());
                        this.stream = JcaContentVerifierProviderBuilder.this.new SignatureOutputStream(sig);
                        Signature rawSig = JcaContentVerifierProviderBuilder.this.createRawSig(algorithm, certificate.getPublicKey());
                        if (rawSig != null) {
                            return JcaContentVerifierProviderBuilder.this.new RawSigVerifier(algorithm, this.stream, rawSig);
                        }
                        return JcaContentVerifierProviderBuilder.this.new SigVerifier(algorithm, this.stream);
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
        return new ContentVerifierProvider() { // from class: org.spongycastle.operator.jcajce.JcaContentVerifierProviderBuilder.2
            @Override // org.spongycastle.operator.ContentVerifierProvider
            public boolean hasAssociatedCertificate() {
                return false;
            }

            @Override // org.spongycastle.operator.ContentVerifierProvider
            public X509CertificateHolder getAssociatedCertificate() {
                return null;
            }

            @Override // org.spongycastle.operator.ContentVerifierProvider
            public ContentVerifier get(AlgorithmIdentifier algorithm) throws OperatorCreationException {
                SignatureOutputStream stream = JcaContentVerifierProviderBuilder.this.createSignatureStream(algorithm, publicKey);
                Signature rawSig = JcaContentVerifierProviderBuilder.this.createRawSig(algorithm, publicKey);
                if (rawSig != null) {
                    return JcaContentVerifierProviderBuilder.this.new RawSigVerifier(algorithm, stream, rawSig);
                }
                return JcaContentVerifierProviderBuilder.this.new SigVerifier(algorithm, stream);
            }
        };
    }

    public ContentVerifierProvider build(SubjectPublicKeyInfo publicKey) throws OperatorCreationException {
        return build(this.helper.convertPublicKey(publicKey));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SignatureOutputStream createSignatureStream(AlgorithmIdentifier algorithm, PublicKey publicKey) throws OperatorCreationException {
        try {
            Signature sig = this.helper.createSignature(algorithm);
            sig.initVerify(publicKey);
            return new SignatureOutputStream(sig);
        } catch (GeneralSecurityException e) {
            throw new OperatorCreationException("exception on setup: " + e, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Signature createRawSig(AlgorithmIdentifier algorithm, PublicKey publicKey) {
        Signature rawSig;
        try {
            rawSig = this.helper.createRawSignature(algorithm);
            if (rawSig != null) {
                rawSig.initVerify(publicKey);
            }
        } catch (Exception e) {
            rawSig = null;
        }
        return rawSig;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/jcajce/JcaContentVerifierProviderBuilder$SigVerifier.class */
    private class SigVerifier implements ContentVerifier {
        private AlgorithmIdentifier algorithm;
        protected SignatureOutputStream stream;

        SigVerifier(AlgorithmIdentifier algorithm, SignatureOutputStream stream) {
            this.algorithm = algorithm;
            this.stream = stream;
        }

        @Override // org.spongycastle.operator.ContentVerifier
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithm;
        }

        @Override // org.spongycastle.operator.ContentVerifier
        public OutputStream getOutputStream() {
            if (this.stream == null) {
                throw new IllegalStateException("verifier not initialised");
            }
            return this.stream;
        }

        @Override // org.spongycastle.operator.ContentVerifier
        public boolean verify(byte[] expected) {
            try {
                return this.stream.verify(expected);
            } catch (SignatureException e) {
                throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/jcajce/JcaContentVerifierProviderBuilder$RawSigVerifier.class */
    private class RawSigVerifier extends SigVerifier implements RawContentVerifier {
        private Signature rawSignature;

        RawSigVerifier(AlgorithmIdentifier algorithm, SignatureOutputStream stream, Signature rawSignature) {
            super(algorithm, stream);
            this.rawSignature = rawSignature;
        }

        @Override // org.spongycastle.operator.jcajce.JcaContentVerifierProviderBuilder.SigVerifier, org.spongycastle.operator.ContentVerifier
        public boolean verify(byte[] expected) {
            try {
                return super.verify(expected);
            } finally {
                try {
                    this.rawSignature.verify(expected);
                } catch (Exception e) {
                }
            }
        }

        @Override // org.spongycastle.operator.RawContentVerifier
        public boolean verify(byte[] digest, byte[] expected) {
            try {
                try {
                    this.rawSignature.update(digest);
                    boolean zVerify = this.rawSignature.verify(expected);
                    try {
                        this.stream.verify(expected);
                    } catch (Exception e) {
                    }
                    return zVerify;
                } catch (Throwable th) {
                    try {
                        this.stream.verify(expected);
                    } catch (Exception e2) {
                    }
                    throw th;
                }
            } catch (SignatureException e3) {
                throw new RuntimeOperatorException("exception obtaining raw signature: " + e3.getMessage(), e3);
            }
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/jcajce/JcaContentVerifierProviderBuilder$SignatureOutputStream.class */
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
