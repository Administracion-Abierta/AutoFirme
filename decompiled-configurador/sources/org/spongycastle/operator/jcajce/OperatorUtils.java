package org.spongycastle.operator.jcajce;

import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import org.spongycastle.operator.GenericKey;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/jcajce/OperatorUtils.class */
class OperatorUtils {
    OperatorUtils() {
    }

    static Key getJceKey(GenericKey key) {
        if (key.getRepresentation() instanceof Key) {
            return (Key) key.getRepresentation();
        }
        if (key.getRepresentation() instanceof byte[]) {
            return new SecretKeySpec((byte[]) key.getRepresentation(), "ENC");
        }
        throw new IllegalArgumentException("unknown generic key type");
    }
}
