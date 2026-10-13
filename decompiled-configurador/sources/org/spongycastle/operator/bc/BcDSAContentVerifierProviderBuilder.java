package org.spongycastle.operator.bc;

import java.io.IOException;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.signers.DSADigestSigner;
import org.spongycastle.crypto.signers.DSASigner;
import org.spongycastle.crypto.util.PublicKeyFactory;
import org.spongycastle.operator.DigestAlgorithmIdentifierFinder;
import org.spongycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/bc/BcDSAContentVerifierProviderBuilder.class */
public class BcDSAContentVerifierProviderBuilder extends BcContentVerifierProviderBuilder {
    private DigestAlgorithmIdentifierFinder digestAlgorithmFinder;

    public BcDSAContentVerifierProviderBuilder(DigestAlgorithmIdentifierFinder digestAlgorithmFinder) {
        this.digestAlgorithmFinder = digestAlgorithmFinder;
    }

    @Override // org.spongycastle.operator.bc.BcContentVerifierProviderBuilder
    protected Signer createSigner(AlgorithmIdentifier sigAlgId) throws OperatorCreationException {
        AlgorithmIdentifier digAlg = this.digestAlgorithmFinder.find(sigAlgId);
        Digest dig = this.digestProvider.get(digAlg);
        return new DSADigestSigner(new DSASigner(), dig);
    }

    @Override // org.spongycastle.operator.bc.BcContentVerifierProviderBuilder
    protected AsymmetricKeyParameter extractKeyParameters(SubjectPublicKeyInfo publicKeyInfo) throws IOException {
        return PublicKeyFactory.createKey(publicKeyInfo);
    }
}
