package org.spongycastle.operator.bc;

import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ntt.NTTObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/operator/bc/CamelliaUtil.class */
class CamelliaUtil {
    CamelliaUtil() {
    }

    static AlgorithmIdentifier determineKeyEncAlg(KeyParameter key) {
        ASN1ObjectIdentifier wrapOid;
        int length = key.getKey().length * 8;
        if (length == 128) {
            wrapOid = NTTObjectIdentifiers.id_camellia128_wrap;
        } else if (length == 192) {
            wrapOid = NTTObjectIdentifiers.id_camellia192_wrap;
        } else if (length == 256) {
            wrapOid = NTTObjectIdentifiers.id_camellia256_wrap;
        } else {
            throw new IllegalArgumentException("illegal keysize in Camellia");
        }
        return new AlgorithmIdentifier(wrapOid);
    }
}
