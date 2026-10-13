package org.spongycastle.operator.bc;

import java.io.OutputStream;
import java.security.SecureRandom;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.RuntimeOperatorException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/bc/BcContentSignerBuilder.class */
public abstract class BcContentSignerBuilder {
    private SecureRandom random;
    private AlgorithmIdentifier sigAlgId;
    private AlgorithmIdentifier digAlgId;
    protected BcDigestProvider digestProvider = BcDefaultDigestProvider.INSTANCE;

    protected abstract Signer createSigner(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) throws OperatorCreationException;

    public BcContentSignerBuilder(AlgorithmIdentifier sigAlgId, AlgorithmIdentifier digAlgId) {
        this.sigAlgId = sigAlgId;
        this.digAlgId = digAlgId;
    }

    public BcContentSignerBuilder setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public ContentSigner build(AsymmetricKeyParameter privateKey) throws OperatorCreationException {
        final Signer sig = createSigner(this.sigAlgId, this.digAlgId);
        if (this.random != null) {
            sig.init(true, new ParametersWithRandom(privateKey, this.random));
        } else {
            sig.init(true, privateKey);
        }
        return new ContentSigner() { // from class: org.spongycastle.operator.bc.BcContentSignerBuilder.1
            private BcSignerOutputStream stream;

            {
                this.stream = new BcSignerOutputStream(sig);
            }

            @Override // org.spongycastle.operator.ContentSigner
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return BcContentSignerBuilder.this.sigAlgId;
            }

            @Override // org.spongycastle.operator.ContentSigner
            public OutputStream getOutputStream() {
                return this.stream;
            }

            @Override // org.spongycastle.operator.ContentSigner
            public byte[] getSignature() {
                try {
                    return this.stream.getSignature();
                } catch (CryptoException e) {
                    throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
                }
            }
        };
    }
}
