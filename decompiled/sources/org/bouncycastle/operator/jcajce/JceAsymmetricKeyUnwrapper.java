package org.bouncycastle.operator.jcajce;

import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
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
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/jcajce/JceAsymmetricKeyUnwrapper.class */
public class JceAsymmetricKeyUnwrapper extends AsymmetricKeyUnwrapper {
    private OperatorHelper helper;
    private Map extraMappings;
    private PrivateKey privKey;
    private boolean unwrappedKeyMustBeEncodable;

    public JceAsymmetricKeyUnwrapper(AlgorithmIdentifier algorithmIdentifier, PrivateKey privateKey) {
        super(algorithmIdentifier);
        this.helper = new OperatorHelper(new DefaultJcaJceHelper());
        this.extraMappings = new HashMap();
        this.privKey = privateKey;
    }

    public JceAsymmetricKeyUnwrapper setProvider(Provider provider) {
        this.helper = new OperatorHelper(new ProviderJcaJceHelper(provider));
        return this;
    }

    public JceAsymmetricKeyUnwrapper setProvider(String str) {
        this.helper = new OperatorHelper(new NamedJcaJceHelper(str));
        return this;
    }

    public JceAsymmetricKeyUnwrapper setMustProduceEncodableUnwrappedKey(boolean z) {
        this.unwrappedKeyMustBeEncodable = z;
        return this;
    }

    public JceAsymmetricKeyUnwrapper setAlgorithmMapping(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str) {
        this.extraMappings.put(aSN1ObjectIdentifier, str);
        return this;
    }

    /* JADX WARN: Code duplicated, block: B:15:0x007e  */
    /* JADX WARN: Code duplicated, block: B:48:0x006b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:8:0x0047 A[Catch: GeneralSecurityException -> 0x008a, IllegalStateException -> 0x008f, UnsupportedOperationException -> 0x0094, ProviderException -> 0x0099, InvalidKeyException -> 0x00dc, IllegalBlockSizeException -> 0x00fc, BadPaddingException -> 0x011c, InvalidAlgorithmParameterException -> 0x013c, TryCatch #5 {IllegalStateException -> 0x008f, UnsupportedOperationException -> 0x0094, GeneralSecurityException -> 0x008a, ProviderException -> 0x0099, blocks: (B:5:0x0028, B:7:0x0038, B:9:0x0051, B:11:0x006b, B:13:0x0078, B:8:0x0047), top: B:50:0x0028 }] */
    @Override // org.bouncycastle.operator.KeyUnwrapper
    public GenericKey generateUnwrappedKey(AlgorithmIdentifier algorithmIdentifier, byte[] bArr) throws OperatorException {
        byte[] encoded;
        try {
            Key secretKeySpec = null;
            Cipher cipherCreateAsymmetricWrapper = this.helper.createAsymmetricWrapper(getAlgorithmIdentifier().getAlgorithm(), this.extraMappings);
            AlgorithmParameters algorithmParametersCreateAlgorithmParameters = this.helper.createAlgorithmParameters(getAlgorithmIdentifier());
            if (algorithmParametersCreateAlgorithmParameters != null) {
                try {
                    if (getAlgorithmIdentifier().getAlgorithm().equals((ASN1Primitive) OIWObjectIdentifiers.elGamalAlgorithm)) {
                        cipherCreateAsymmetricWrapper.init(4, this.privKey);
                    } else {
                        cipherCreateAsymmetricWrapper.init(4, this.privKey, algorithmParametersCreateAlgorithmParameters);
                    }
                    secretKeySpec = cipherCreateAsymmetricWrapper.unwrap(bArr, this.helper.getKeyAlgorithmName(algorithmIdentifier.getAlgorithm()), 3);
                    if (this.unwrappedKeyMustBeEncodable) {
                        try {
                            encoded = secretKeySpec.getEncoded();
                            if (encoded != null || encoded.length == 0) {
                                secretKeySpec = null;
                            }
                        } catch (Exception e) {
                            secretKeySpec = null;
                        }
                    }
                } catch (IllegalStateException e2) {
                } catch (UnsupportedOperationException e3) {
                } catch (GeneralSecurityException e4) {
                } catch (ProviderException e5) {
                }
            } else {
                cipherCreateAsymmetricWrapper.init(4, this.privKey);
                secretKeySpec = cipherCreateAsymmetricWrapper.unwrap(bArr, this.helper.getKeyAlgorithmName(algorithmIdentifier.getAlgorithm()), 3);
                if (this.unwrappedKeyMustBeEncodable) {
                    encoded = secretKeySpec.getEncoded();
                    if (encoded != null) {
                        secretKeySpec = null;
                    } else {
                        secretKeySpec = null;
                    }
                }
            }
            if (secretKeySpec == null) {
                if (algorithmParametersCreateAlgorithmParameters != null) {
                    cipherCreateAsymmetricWrapper.init(2, this.privKey, algorithmParametersCreateAlgorithmParameters);
                } else {
                    cipherCreateAsymmetricWrapper.init(2, this.privKey);
                }
                secretKeySpec = new SecretKeySpec(cipherCreateAsymmetricWrapper.doFinal(bArr), algorithmIdentifier.getAlgorithm().getId());
            }
            return new JceGenericKey(algorithmIdentifier, secretKeySpec);
        } catch (InvalidAlgorithmParameterException e6) {
            throw new OperatorException("invalid algorithm parameters: " + e6.getMessage(), e6);
        } catch (InvalidKeyException e7) {
            throw new OperatorException("key invalid: " + e7.getMessage(), e7);
        } catch (BadPaddingException e8) {
            throw new OperatorException("bad padding: " + e8.getMessage(), e8);
        } catch (IllegalBlockSizeException e9) {
            throw new OperatorException("illegal blocksize: " + e9.getMessage(), e9);
        }
    }
}
