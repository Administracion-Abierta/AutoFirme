package org.spongycastle.operator.bc;

import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/operator/bc/AESUtil.class */
class AESUtil {
    AESUtil() {
    }

    static AlgorithmIdentifier determineKeyEncAlg(KeyParameter key) {
        ASN1ObjectIdentifier wrapOid;
        int length = key.getKey().length * 8;
        if (length == 128) {
            wrapOid = NISTObjectIdentifiers.id_aes128_wrap;
        } else if (length == 192) {
            wrapOid = NISTObjectIdentifiers.id_aes192_wrap;
        } else if (length == 256) {
            wrapOid = NISTObjectIdentifiers.id_aes256_wrap;
        } else {
            throw new IllegalArgumentException("illegal keysize in AES");
        }
        return new AlgorithmIdentifier(wrapOid);
    }
}
