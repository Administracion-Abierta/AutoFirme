package org.bouncycastle.operator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Integers;
import org.spongycastle.cms.CMSEnvelopedGenerator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/DefaultSecretKeySizeProvider.class */
public class DefaultSecretKeySizeProvider implements SecretKeySizeProvider {
    public static final SecretKeySizeProvider INSTANCE = new DefaultSecretKeySizeProvider();
    private static final Map KEY_SIZES;

    @Override // org.bouncycastle.operator.SecretKeySizeProvider
    public int getKeySize(AlgorithmIdentifier algorithmIdentifier) {
        int keySize = getKeySize(algorithmIdentifier.getAlgorithm());
        if (keySize > 0) {
            return keySize;
        }
        return -1;
    }

    @Override // org.bouncycastle.operator.SecretKeySizeProvider
    public int getKeySize(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        Integer num = (Integer) KEY_SIZES.get(aSN1ObjectIdentifier);
        if (num != null) {
            return num.intValue();
        }
        return -1;
    }

    static {
        HashMap map = new HashMap();
        map.put(new ASN1ObjectIdentifier(CMSEnvelopedGenerator.CAST5_CBC), Integers.valueOf(128));
        map.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
        map.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, Integers.valueOf(192));
        map.put(PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, Integers.valueOf(64));
        map.put(PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, Integers.valueOf(64));
        map.put(NISTObjectIdentifiers.id_aes128_CBC, Integers.valueOf(128));
        map.put(NISTObjectIdentifiers.id_aes192_CBC, Integers.valueOf(192));
        map.put(NISTObjectIdentifiers.id_aes256_CBC, Integers.valueOf(256));
        map.put(NISTObjectIdentifiers.id_aes128_GCM, Integers.valueOf(128));
        map.put(NISTObjectIdentifiers.id_aes192_GCM, Integers.valueOf(192));
        map.put(NISTObjectIdentifiers.id_aes256_GCM, Integers.valueOf(256));
        map.put(NISTObjectIdentifiers.id_aes128_CCM, Integers.valueOf(128));
        map.put(NISTObjectIdentifiers.id_aes192_CCM, Integers.valueOf(192));
        map.put(NISTObjectIdentifiers.id_aes256_CCM, Integers.valueOf(256));
        map.put(NISTObjectIdentifiers.id_aes128_wrap, Integers.valueOf(128));
        map.put(NISTObjectIdentifiers.id_aes192_wrap, Integers.valueOf(192));
        map.put(NISTObjectIdentifiers.id_aes256_wrap, Integers.valueOf(256));
        map.put(NISTObjectIdentifiers.id_aes128_wrap_pad, Integers.valueOf(128));
        map.put(NISTObjectIdentifiers.id_aes192_wrap_pad, Integers.valueOf(192));
        map.put(NISTObjectIdentifiers.id_aes256_wrap_pad, Integers.valueOf(256));
        map.put(NTTObjectIdentifiers.id_camellia128_cbc, Integers.valueOf(128));
        map.put(NTTObjectIdentifiers.id_camellia192_cbc, Integers.valueOf(192));
        map.put(NTTObjectIdentifiers.id_camellia256_cbc, Integers.valueOf(256));
        map.put(NTTObjectIdentifiers.id_camellia128_wrap, Integers.valueOf(128));
        map.put(NTTObjectIdentifiers.id_camellia192_wrap, Integers.valueOf(192));
        map.put(NTTObjectIdentifiers.id_camellia256_wrap, Integers.valueOf(256));
        map.put(KISAObjectIdentifiers.id_seedCBC, Integers.valueOf(128));
        map.put(OIWObjectIdentifiers.desCBC, Integers.valueOf(64));
        map.put(CryptoProObjectIdentifiers.gostR28147_gcfb, Integers.valueOf(256));
        KEY_SIZES = Collections.unmodifiableMap(map);
    }
}
