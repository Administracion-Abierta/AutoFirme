package org.spongycastle.operator.bc;

import java.security.Key;
import org.spongycastle.operator.GenericKey;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/bc/OperatorUtils.class */
class OperatorUtils {
    OperatorUtils() {
    }

    static byte[] getKeyBytes(GenericKey key) {
        if (key.getRepresentation() instanceof Key) {
            return ((Key) key.getRepresentation()).getEncoded();
        }
        if (key.getRepresentation() instanceof byte[]) {
            return (byte[]) key.getRepresentation();
        }
        throw new IllegalArgumentException("unknown generic key type");
    }
}
