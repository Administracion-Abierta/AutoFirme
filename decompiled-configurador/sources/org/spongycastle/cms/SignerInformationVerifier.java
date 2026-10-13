package org.spongycastle.cms;

import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.operator.ContentVerifier;
import org.spongycastle.operator.ContentVerifierProvider;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.SignatureAlgorithmIdentifierFinder;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/SignerInformationVerifier.class */
public class SignerInformationVerifier {
    private ContentVerifierProvider verifierProvider;
    private DigestCalculatorProvider digestProvider;
    private SignatureAlgorithmIdentifierFinder sigAlgorithmFinder;
    private CMSSignatureAlgorithmNameGenerator sigNameGenerator;

    public SignerInformationVerifier(CMSSignatureAlgorithmNameGenerator sigNameGenerator, SignatureAlgorithmIdentifierFinder sigAlgorithmFinder, ContentVerifierProvider verifierProvider, DigestCalculatorProvider digestProvider) {
        this.sigNameGenerator = sigNameGenerator;
        this.sigAlgorithmFinder = sigAlgorithmFinder;
        this.verifierProvider = verifierProvider;
        this.digestProvider = digestProvider;
    }

    public boolean hasAssociatedCertificate() {
        return this.verifierProvider.hasAssociatedCertificate();
    }

    public X509CertificateHolder getAssociatedCertificate() {
        return this.verifierProvider.getAssociatedCertificate();
    }

    public ContentVerifier getContentVerifier(AlgorithmIdentifier signingAlgorithm, AlgorithmIdentifier digestAlgorithm) throws OperatorCreationException {
        String signatureName = this.sigNameGenerator.getSignatureName(digestAlgorithm, signingAlgorithm);
        AlgorithmIdentifier baseAlgID = this.sigAlgorithmFinder.find(signatureName);
        return this.verifierProvider.get(new AlgorithmIdentifier(baseAlgID.getAlgorithm(), signingAlgorithm.getParameters()));
    }

    public DigestCalculator getDigestCalculator(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
        return this.digestProvider.get(algorithmIdentifier);
    }
}
