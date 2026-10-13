package org.spongycastle.operator.bc;

import java.security.SecureRandom;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.Wrapper;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.OperatorException;
import org.spongycastle.operator.SymmetricKeyWrapper;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/operator/bc/BcSymmetricKeyWrapper.class */
public class BcSymmetricKeyWrapper extends SymmetricKeyWrapper {
    private SecureRandom random;
    private Wrapper wrapper;
    private KeyParameter wrappingKey;

    public BcSymmetricKeyWrapper(AlgorithmIdentifier wrappingAlgorithm, Wrapper wrapper, KeyParameter wrappingKey) {
        super(wrappingAlgorithm);
        this.wrapper = wrapper;
        this.wrappingKey = wrappingKey;
    }

    public BcSymmetricKeyWrapper setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    @Override // org.spongycastle.operator.KeyWrapper
    public byte[] generateWrappedKey(GenericKey encryptionKey) throws OperatorException {
        byte[] contentEncryptionKeySpec = OperatorUtils.getKeyBytes(encryptionKey);
        if (this.random == null) {
            this.wrapper.init(true, this.wrappingKey);
        } else {
            this.wrapper.init(true, new ParametersWithRandom(this.wrappingKey, this.random));
        }
        return this.wrapper.wrap(contentEncryptionKeySpec, 0, contentEncryptionKeySpec.length);
    }
}
