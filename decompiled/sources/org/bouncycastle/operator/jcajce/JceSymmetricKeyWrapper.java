package org.bouncycastle.operator.jcajce;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyWrapper;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/jcajce/JceSymmetricKeyWrapper.class */
public class JceSymmetricKeyWrapper extends SymmetricKeyWrapper {
    private OperatorHelper helper;
    private SecureRandom random;
    private SecretKey wrappingKey;

    public JceSymmetricKeyWrapper(SecretKey secretKey) {
        super(determineKeyEncAlg(secretKey));
        this.helper = new OperatorHelper(new DefaultJcaJceHelper());
        this.wrappingKey = secretKey;
    }

    public JceSymmetricKeyWrapper setProvider(Provider provider) {
        this.helper = new OperatorHelper(new ProviderJcaJceHelper(provider));
        return this;
    }

    public JceSymmetricKeyWrapper setProvider(String str) {
        this.helper = new OperatorHelper(new NamedJcaJceHelper(str));
        return this;
    }

    public JceSymmetricKeyWrapper setSecureRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }

    @Override // org.bouncycastle.operator.KeyWrapper
    public byte[] generateWrappedKey(GenericKey genericKey) throws OperatorException {
        Key jceKey = OperatorUtils.getJceKey(genericKey);
        Cipher cipherCreateSymmetricWrapper = this.helper.createSymmetricWrapper(getAlgorithmIdentifier().getAlgorithm());
        try {
            cipherCreateSymmetricWrapper.init(3, this.wrappingKey, this.random);
            return cipherCreateSymmetricWrapper.wrap(jceKey);
        } catch (GeneralSecurityException e) {
            throw new OperatorException("cannot wrap key: " + e.getMessage(), e);
        }
    }

    private static AlgorithmIdentifier determineKeyEncAlg(SecretKey secretKey) {
        return determineKeyEncAlg(secretKey.getAlgorithm(), secretKey.getEncoded().length * 8);
    }

    static AlgorithmIdentifier determineKeyEncAlg(String str, int i) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier;
        ASN1ObjectIdentifier aSN1ObjectIdentifier2;
        if (str.startsWith("DES") || str.startsWith("TripleDES")) {
            return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, DERNull.INSTANCE);
        }
        if (str.startsWith("RC2")) {
            return new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.1.9.16.3.7"), new ASN1Integer(58L));
        }
        if (str.startsWith("AES") || str.startsWith(NISTObjectIdentifiers.aes.getId())) {
            if (i == 128) {
                aSN1ObjectIdentifier = NISTObjectIdentifiers.id_aes128_wrap;
            } else if (i == 192) {
                aSN1ObjectIdentifier = NISTObjectIdentifiers.id_aes192_wrap;
            } else {
                if (i != 256) {
                    throw new IllegalArgumentException("illegal keysize in AES");
                }
                aSN1ObjectIdentifier = NISTObjectIdentifiers.id_aes256_wrap;
            }
            return new AlgorithmIdentifier(aSN1ObjectIdentifier);
        }
        if (str.startsWith("SEED")) {
            return new AlgorithmIdentifier(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap);
        }
        if (!str.startsWith("Camellia")) {
            throw new IllegalArgumentException("unknown algorithm");
        }
        if (i == 128) {
            aSN1ObjectIdentifier2 = NTTObjectIdentifiers.id_camellia128_wrap;
        } else if (i == 192) {
            aSN1ObjectIdentifier2 = NTTObjectIdentifiers.id_camellia192_wrap;
        } else {
            if (i != 256) {
                throw new IllegalArgumentException("illegal keysize in Camellia");
            }
            aSN1ObjectIdentifier2 = NTTObjectIdentifiers.id_camellia256_wrap;
        }
        return new AlgorithmIdentifier(aSN1ObjectIdentifier2);
    }
}
