package org.spongycastle.operator.jcajce;

import java.security.Key;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.GenericKey;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/operator/jcajce/JceGenericKey.class */
public class JceGenericKey extends GenericKey {
    private static Object getRepresentation(Key key) {
        byte[] keyBytes = key.getEncoded();
        if (keyBytes != null) {
            return keyBytes;
        }
        return key;
    }

    public JceGenericKey(AlgorithmIdentifier algorithmIdentifier, Key representation) {
        super(algorithmIdentifier, getRepresentation(representation));
    }
}
