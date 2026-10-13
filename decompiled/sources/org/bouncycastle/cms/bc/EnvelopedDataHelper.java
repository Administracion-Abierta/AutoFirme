package org.bouncycastle.cms.bc;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.AlgorithmIdentifierFactory;
import org.bouncycastle.crypto.util.CipherFactory;
import org.bouncycastle.crypto.util.CipherKeyGeneratorFactory;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/bc/EnvelopedDataHelper.class */
class EnvelopedDataHelper {
    protected static final Map BASE_CIPHER_NAMES = new HashMap();
    protected static final Map MAC_ALG_NAMES = new HashMap();
    private static final Set authEnvelopedAlgorithms = new HashSet();
    private static final Map prfs = createTable();

    private static Map createTable() {
        HashMap map = new HashMap();
        map.put(PKCSObjectIdentifiers.id_hmacWithSHA1, new BcDigestProvider() { // from class: org.bouncycastle.cms.bc.EnvelopedDataHelper.1
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA1Digest();
            }
        });
        map.put(PKCSObjectIdentifiers.id_hmacWithSHA224, new BcDigestProvider() { // from class: org.bouncycastle.cms.bc.EnvelopedDataHelper.2
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA224Digest();
            }
        });
        map.put(PKCSObjectIdentifiers.id_hmacWithSHA256, new BcDigestProvider() { // from class: org.bouncycastle.cms.bc.EnvelopedDataHelper.3
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return SHA256Digest.newInstance();
            }
        });
        map.put(PKCSObjectIdentifiers.id_hmacWithSHA384, new BcDigestProvider() { // from class: org.bouncycastle.cms.bc.EnvelopedDataHelper.4
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA384Digest();
            }
        });
        map.put(PKCSObjectIdentifiers.id_hmacWithSHA512, new BcDigestProvider() { // from class: org.bouncycastle.cms.bc.EnvelopedDataHelper.5
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA512Digest();
            }
        });
        return Collections.unmodifiableMap(map);
    }

    EnvelopedDataHelper() {
    }

    static ExtendedDigest getPRF(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
        return ((BcDigestProvider) prfs.get(algorithmIdentifier.getAlgorithm())).get(null);
    }

    static Wrapper createRFC3211Wrapper(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CMSException {
        if (NISTObjectIdentifiers.id_aes128_CBC.equals((ASN1Primitive) aSN1ObjectIdentifier) || NISTObjectIdentifiers.id_aes192_CBC.equals((ASN1Primitive) aSN1ObjectIdentifier) || NISTObjectIdentifiers.id_aes256_CBC.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
            return new RFC3211WrapEngine(AESEngine.newInstance());
        }
        if (PKCSObjectIdentifiers.des_EDE3_CBC.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
            return new RFC3211WrapEngine(new DESedeEngine());
        }
        if (OIWObjectIdentifiers.desCBC.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
            return new RFC3211WrapEngine(new DESEngine());
        }
        if (PKCSObjectIdentifiers.RC2_CBC.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
            return new RFC3211WrapEngine(new RC2Engine());
        }
        throw new CMSException("cannot recognise wrapper: " + aSN1ObjectIdentifier);
    }

    static Object createContentCipher(boolean z, CipherParameters cipherParameters, AlgorithmIdentifier algorithmIdentifier) throws CMSException {
        try {
            return CipherFactory.createContentCipher(z, cipherParameters, algorithmIdentifier);
        } catch (IllegalArgumentException e) {
            throw new CMSException(e.getMessage(), e);
        }
    }

    AlgorithmIdentifier generateEncryptionAlgID(ASN1ObjectIdentifier aSN1ObjectIdentifier, KeyParameter keyParameter, SecureRandom secureRandom) throws CMSException {
        try {
            return AlgorithmIdentifierFactory.generateEncryptionAlgID(aSN1ObjectIdentifier, keyParameter.getKey().length * 8, secureRandom);
        } catch (IllegalArgumentException e) {
            throw new CMSException(e.getMessage(), e);
        }
    }

    CipherKeyGenerator createKeyGenerator(ASN1ObjectIdentifier aSN1ObjectIdentifier, int i, SecureRandom secureRandom) throws CMSException {
        try {
            return CipherKeyGeneratorFactory.createKeyGenerator(aSN1ObjectIdentifier, secureRandom);
        } catch (IllegalArgumentException e) {
            throw new CMSException(e.getMessage(), e);
        }
    }

    boolean isAuthEnveloped(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return authEnvelopedAlgorithms.contains(aSN1ObjectIdentifier);
    }

    static {
        BASE_CIPHER_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDE");
        BASE_CIPHER_NAMES.put(CMSAlgorithm.AES128_CBC, "AES");
        BASE_CIPHER_NAMES.put(CMSAlgorithm.AES192_CBC, "AES");
        BASE_CIPHER_NAMES.put(CMSAlgorithm.AES256_CBC, "AES");
        MAC_ALG_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDEMac");
        MAC_ALG_NAMES.put(CMSAlgorithm.AES128_CBC, "AESMac");
        MAC_ALG_NAMES.put(CMSAlgorithm.AES192_CBC, "AESMac");
        MAC_ALG_NAMES.put(CMSAlgorithm.AES256_CBC, "AESMac");
        MAC_ALG_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2Mac");
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes128_GCM);
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes192_GCM);
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes256_GCM);
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes128_CCM);
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes192_CCM);
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes256_CCM);
    }
}
