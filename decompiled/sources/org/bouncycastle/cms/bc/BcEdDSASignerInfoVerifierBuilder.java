package org.bouncycastle.cms.bc;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.SignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcEdDSAContentVerifierProviderBuilder;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/bc/BcEdDSASignerInfoVerifierBuilder.class */
public class BcEdDSASignerInfoVerifierBuilder {
    private BcEdDSAContentVerifierProviderBuilder contentVerifierProviderBuilder = new BcEdDSAContentVerifierProviderBuilder();
    private DigestCalculatorProvider digestCalculatorProvider;
    private CMSSignatureAlgorithmNameGenerator sigAlgNameGen;
    private SignatureAlgorithmIdentifierFinder sigAlgIdFinder;

    public BcEdDSASignerInfoVerifierBuilder(CMSSignatureAlgorithmNameGenerator cMSSignatureAlgorithmNameGenerator, SignatureAlgorithmIdentifierFinder signatureAlgorithmIdentifierFinder, DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder, DigestCalculatorProvider digestCalculatorProvider) {
        this.sigAlgNameGen = cMSSignatureAlgorithmNameGenerator;
        this.sigAlgIdFinder = signatureAlgorithmIdentifierFinder;
        this.digestCalculatorProvider = digestCalculatorProvider;
    }

    public SignerInformationVerifier build(X509CertificateHolder x509CertificateHolder) throws OperatorCreationException {
        return new SignerInformationVerifier(this.sigAlgNameGen, this.sigAlgIdFinder, this.contentVerifierProviderBuilder.build(x509CertificateHolder), this.digestCalculatorProvider);
    }

    public SignerInformationVerifier build(AsymmetricKeyParameter asymmetricKeyParameter) throws OperatorCreationException {
        return new SignerInformationVerifier(this.sigAlgNameGen, this.sigAlgIdFinder, this.contentVerifierProviderBuilder.build(asymmetricKeyParameter), this.digestCalculatorProvider);
    }
}
