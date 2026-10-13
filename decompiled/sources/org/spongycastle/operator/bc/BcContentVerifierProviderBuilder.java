package org.spongycastle.operator.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.operator.ContentVerifier;
import org.spongycastle.operator.ContentVerifierProvider;
import org.spongycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/operator/bc/BcContentVerifierProviderBuilder.class */
public abstract class BcContentVerifierProviderBuilder {
    protected BcDigestProvider digestProvider = BcDefaultDigestProvider.INSTANCE;

    protected abstract AsymmetricKeyParameter extractKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException;

    protected abstract Signer createSigner(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException;

    public ContentVerifierProvider build(final X509CertificateHolder certHolder) throws OperatorCreationException {
        return new ContentVerifierProvider() { // from class: org.spongycastle.operator.bc.BcContentVerifierProviderBuilder.1
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
                    AsymmetricKeyParameter publicKey = BcContentVerifierProviderBuilder.this.extractKeyParameters(certHolder.getSubjectPublicKeyInfo());
                    BcSignerOutputStream stream = BcContentVerifierProviderBuilder.this.createSignatureStream(algorithm, publicKey);
                    return BcContentVerifierProviderBuilder.this.new SigVerifier(algorithm, stream);
                } catch (IOException e) {
                    throw new OperatorCreationException("exception on setup: " + e, e);
                }
            }
        };
    }

    public ContentVerifierProvider build(final AsymmetricKeyParameter publicKey) throws OperatorCreationException {
        return new ContentVerifierProvider() { // from class: org.spongycastle.operator.bc.BcContentVerifierProviderBuilder.2
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
                BcSignerOutputStream stream = BcContentVerifierProviderBuilder.this.createSignatureStream(algorithm, publicKey);
                return BcContentVerifierProviderBuilder.this.new SigVerifier(algorithm, stream);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public BcSignerOutputStream createSignatureStream(AlgorithmIdentifier algorithm, AsymmetricKeyParameter publicKey) throws OperatorCreationException {
        Signer sig = createSigner(algorithm);
        sig.init(false, publicKey);
        return new BcSignerOutputStream(sig);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/operator/bc/BcContentVerifierProviderBuilder$SigVerifier.class */
    private class SigVerifier implements ContentVerifier {
        private BcSignerOutputStream stream;
        private AlgorithmIdentifier algorithm;

        SigVerifier(AlgorithmIdentifier algorithm, BcSignerOutputStream stream) {
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
            return this.stream.verify(expected);
        }
    }
}
