package org.spongycastle.operator.jcajce;

import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.ProviderException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.jcajce.util.DefaultJcaJceHelper;
import org.spongycastle.jcajce.util.NamedJcaJceHelper;
import org.spongycastle.jcajce.util.ProviderJcaJceHelper;
import org.spongycastle.operator.AsymmetricKeyUnwrapper;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.OperatorException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/jcajce/JceAsymmetricKeyUnwrapper.class */
public class JceAsymmetricKeyUnwrapper extends AsymmetricKeyUnwrapper {
    private OperatorHelper helper;
    private Map extraMappings;
    private PrivateKey privKey;
    private boolean unwrappedKeyMustBeEncodable;

    public JceAsymmetricKeyUnwrapper(AlgorithmIdentifier algorithmIdentifier, PrivateKey privKey) {
        super(algorithmIdentifier);
        this.helper = new OperatorHelper(new DefaultJcaJceHelper());
        this.extraMappings = new HashMap();
        this.privKey = privKey;
    }

    public JceAsymmetricKeyUnwrapper setProvider(Provider provider) {
        this.helper = new OperatorHelper(new ProviderJcaJceHelper(provider));
        return this;
    }

    public JceAsymmetricKeyUnwrapper setProvider(String providerName) {
        this.helper = new OperatorHelper(new NamedJcaJceHelper(providerName));
        return this;
    }

    public JceAsymmetricKeyUnwrapper setMustProduceEncodableUnwrappedKey(boolean unwrappedKeyMustBeEncodable) {
        this.unwrappedKeyMustBeEncodable = unwrappedKeyMustBeEncodable;
        return this;
    }

    public JceAsymmetricKeyUnwrapper setAlgorithmMapping(ASN1ObjectIdentifier algorithm, String algorithmName) {
        this.extraMappings.put(algorithm, algorithmName);
        return this;
    }

    @Override // org.spongycastle.operator.KeyUnwrapper
    public GenericKey generateUnwrappedKey(AlgorithmIdentifier encryptedKeyAlgorithm, byte[] encryptedKey) throws OperatorException {
        try {
            Key sKey = null;
            Cipher keyCipher = this.helper.createAsymmetricWrapper(getAlgorithmIdentifier().getAlgorithm(), this.extraMappings);
            AlgorithmParameters algParams = this.helper.createAlgorithmParameters(getAlgorithmIdentifier());
            try {
                if (algParams != null) {
                    keyCipher.init(4, this.privKey, algParams);
                } else {
                    keyCipher.init(4, this.privKey);
                }
                sKey = keyCipher.unwrap(encryptedKey, this.helper.getKeyAlgorithmName(encryptedKeyAlgorithm.getAlgorithm()), 3);
                if (this.unwrappedKeyMustBeEncodable) {
                    try {
                        byte[] keyBytes = sKey.getEncoded();
                        if (keyBytes == null || keyBytes.length == 0) {
                            sKey = null;
                        }
                    } catch (Exception e) {
                        sKey = null;
                    }
                }
            } catch (IllegalStateException e2) {
            } catch (UnsupportedOperationException e3) {
            } catch (GeneralSecurityException e4) {
            } catch (ProviderException e5) {
            }
            if (sKey == null) {
                keyCipher.init(2, this.privKey);
                sKey = new SecretKeySpec(keyCipher.doFinal(encryptedKey), encryptedKeyAlgorithm.getAlgorithm().getId());
            }
            return new JceGenericKey(encryptedKeyAlgorithm, sKey);
        } catch (InvalidKeyException e6) {
            throw new OperatorException("key invalid: " + e6.getMessage(), e6);
        } catch (BadPaddingException e7) {
            throw new OperatorException("bad padding: " + e7.getMessage(), e7);
        } catch (IllegalBlockSizeException e8) {
            throw new OperatorException("illegal blocksize: " + e8.getMessage(), e8);
        }
    }
}
