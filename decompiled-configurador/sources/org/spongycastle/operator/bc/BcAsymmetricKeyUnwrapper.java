package org.spongycastle.operator.bc;

import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.AsymmetricBlockCipher;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.operator.AsymmetricKeyUnwrapper;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.OperatorException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/bc/BcAsymmetricKeyUnwrapper.class */
public abstract class BcAsymmetricKeyUnwrapper extends AsymmetricKeyUnwrapper {
    private AsymmetricKeyParameter privateKey;

    protected abstract AsymmetricBlockCipher createAsymmetricUnwrapper(ASN1ObjectIdentifier aSN1ObjectIdentifier);

    public BcAsymmetricKeyUnwrapper(AlgorithmIdentifier encAlgId, AsymmetricKeyParameter privateKey) {
        super(encAlgId);
        this.privateKey = privateKey;
    }

    @Override // org.spongycastle.operator.KeyUnwrapper
    public GenericKey generateUnwrappedKey(AlgorithmIdentifier encryptedKeyAlgorithm, byte[] encryptedKey) throws OperatorException {
        AsymmetricBlockCipher keyCipher = createAsymmetricUnwrapper(getAlgorithmIdentifier().getAlgorithm());
        keyCipher.init(false, this.privateKey);
        try {
            byte[] key = keyCipher.processBlock(encryptedKey, 0, encryptedKey.length);
            if (encryptedKeyAlgorithm.getAlgorithm().equals(PKCSObjectIdentifiers.des_EDE3_CBC)) {
                return new GenericKey(encryptedKeyAlgorithm, key);
            }
            return new GenericKey(encryptedKeyAlgorithm, key);
        } catch (InvalidCipherTextException e) {
            throw new OperatorException("unable to recover secret key: " + e.getMessage(), e);
        }
    }
}
