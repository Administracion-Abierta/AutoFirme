package org.bouncycastle.cms;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.AsymmetricKeyWrapper;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/KEMKeyWrapper.class */
public abstract class KEMKeyWrapper extends AsymmetricKeyWrapper {
    protected KEMKeyWrapper(AlgorithmIdentifier algorithmIdentifier) {
        super(algorithmIdentifier);
    }

    public abstract byte[] getEncapsulation();

    public abstract AlgorithmIdentifier getKdfAlgorithmIdentifier();

    public abstract int getKekLength();

    public abstract AlgorithmIdentifier getWrapAlgorithmIdentifier();
}
