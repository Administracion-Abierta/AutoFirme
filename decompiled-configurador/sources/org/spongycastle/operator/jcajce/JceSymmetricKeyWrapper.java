package org.spongycastle.operator.jcajce;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.kisa.KISAObjectIdentifiers;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.ntt.NTTObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.jcajce.util.DefaultJcaJceHelper;
import org.spongycastle.jcajce.util.NamedJcaJceHelper;
import org.spongycastle.jcajce.util.ProviderJcaJceHelper;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.OperatorException;
import org.spongycastle.operator.SymmetricKeyWrapper;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/jcajce/JceSymmetricKeyWrapper.class */
public class JceSymmetricKeyWrapper extends SymmetricKeyWrapper {
    private OperatorHelper helper;
    private SecureRandom random;
    private SecretKey wrappingKey;

    public JceSymmetricKeyWrapper(SecretKey wrappingKey) {
        super(determineKeyEncAlg(wrappingKey));
        this.helper = new OperatorHelper(new DefaultJcaJceHelper());
        this.wrappingKey = wrappingKey;
    }

    public JceSymmetricKeyWrapper setProvider(Provider provider) {
        this.helper = new OperatorHelper(new ProviderJcaJceHelper(provider));
        return this;
    }

    public JceSymmetricKeyWrapper setProvider(String providerName) {
        this.helper = new OperatorHelper(new NamedJcaJceHelper(providerName));
        return this;
    }

    public JceSymmetricKeyWrapper setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    @Override // org.spongycastle.operator.KeyWrapper
    public byte[] generateWrappedKey(GenericKey encryptionKey) throws OperatorException {
        Key contentEncryptionKeySpec = OperatorUtils.getJceKey(encryptionKey);
        Cipher keyEncryptionCipher = this.helper.createSymmetricWrapper(getAlgorithmIdentifier().getAlgorithm());
        try {
            keyEncryptionCipher.init(3, this.wrappingKey, this.random);
            return keyEncryptionCipher.wrap(contentEncryptionKeySpec);
        } catch (GeneralSecurityException e) {
            throw new OperatorException("cannot wrap key: " + e.getMessage(), e);
        }
    }

    private static AlgorithmIdentifier determineKeyEncAlg(SecretKey key) {
        return determineKeyEncAlg(key.getAlgorithm(), key.getEncoded().length * 8);
    }

    static AlgorithmIdentifier determineKeyEncAlg(String algorithm, int keySizeInBits) {
        ASN1ObjectIdentifier wrapOid;
        ASN1ObjectIdentifier wrapOid2;
        if (algorithm.startsWith("DES") || algorithm.startsWith("TripleDES")) {
            return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, DERNull.INSTANCE);
        }
        if (algorithm.startsWith("RC2")) {
            return new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.1.9.16.3.7"), new ASN1Integer(58L));
        }
        if (algorithm.startsWith("AES")) {
            if (keySizeInBits == 128) {
                wrapOid2 = NISTObjectIdentifiers.id_aes128_wrap;
            } else if (keySizeInBits == 192) {
                wrapOid2 = NISTObjectIdentifiers.id_aes192_wrap;
            } else if (keySizeInBits == 256) {
                wrapOid2 = NISTObjectIdentifiers.id_aes256_wrap;
            } else {
                throw new IllegalArgumentException("illegal keysize in AES");
            }
            return new AlgorithmIdentifier(wrapOid2);
        }
        if (algorithm.startsWith("SEED")) {
            return new AlgorithmIdentifier(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap);
        }
        if (algorithm.startsWith("Camellia")) {
            if (keySizeInBits == 128) {
                wrapOid = NTTObjectIdentifiers.id_camellia128_wrap;
            } else if (keySizeInBits == 192) {
                wrapOid = NTTObjectIdentifiers.id_camellia192_wrap;
            } else if (keySizeInBits == 256) {
                wrapOid = NTTObjectIdentifiers.id_camellia256_wrap;
            } else {
                throw new IllegalArgumentException("illegal keysize in Camellia");
            }
            return new AlgorithmIdentifier(wrapOid);
        }
        throw new IllegalArgumentException("unknown algorithm");
    }
}
