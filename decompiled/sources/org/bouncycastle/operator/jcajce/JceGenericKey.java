package org.bouncycastle.operator.jcajce;

import java.security.Key;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.GenericKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/jcajce/JceGenericKey.class */
public class JceGenericKey extends GenericKey {
    private static Object getRepresentation(Key key) {
        byte[] encoded = key.getEncoded();
        return encoded != null ? encoded : key;
    }

    public JceGenericKey(AlgorithmIdentifier algorithmIdentifier, Key key) {
        super(algorithmIdentifier, getRepresentation(key));
    }
}
