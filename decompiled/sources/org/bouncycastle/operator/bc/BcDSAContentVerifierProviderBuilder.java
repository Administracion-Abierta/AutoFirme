package org.bouncycastle.operator.bc;

import java.io.IOException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.signers.DSASigner;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/bc/BcDSAContentVerifierProviderBuilder.class */
public class BcDSAContentVerifierProviderBuilder extends BcContentVerifierProviderBuilder {
    private DigestAlgorithmIdentifierFinder digestAlgorithmFinder;

    public BcDSAContentVerifierProviderBuilder(DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder) {
        this.digestAlgorithmFinder = digestAlgorithmIdentifierFinder;
    }

    @Override // org.bouncycastle.operator.bc.BcContentVerifierProviderBuilder
    protected Signer createSigner(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
        return new DSADigestSigner(new DSASigner(), this.digestProvider.get(this.digestAlgorithmFinder.find(algorithmIdentifier)));
    }

    @Override // org.bouncycastle.operator.bc.BcContentVerifierProviderBuilder
    protected AsymmetricKeyParameter extractKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        return PublicKeyFactory.createKey(subjectPublicKeyInfo);
    }
}
