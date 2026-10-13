package org.spongycastle.operator.bc;

import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.signers.RSADigestSigner;
import org.spongycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/bc/BcRSAContentSignerBuilder.class */
public class BcRSAContentSignerBuilder extends BcContentSignerBuilder {
    public BcRSAContentSignerBuilder(AlgorithmIdentifier sigAlgId, AlgorithmIdentifier digAlgId) {
        super(sigAlgId, digAlgId);
    }

    @Override // org.spongycastle.operator.bc.BcContentSignerBuilder
    protected Signer createSigner(AlgorithmIdentifier sigAlgId, AlgorithmIdentifier digAlgId) throws OperatorCreationException {
        Digest dig = this.digestProvider.get(digAlgId);
        return new RSADigestSigner(dig);
    }
}
