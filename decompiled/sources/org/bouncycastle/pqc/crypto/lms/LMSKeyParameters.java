package org.bouncycastle.pqc.crypto.lms;

import java.io.IOException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/LMSKeyParameters.class */
public abstract class LMSKeyParameters extends AsymmetricKeyParameter implements Encodable {
    protected LMSKeyParameters(boolean z) {
        super(z);
    }

    public abstract byte[] getEncoded() throws IOException;
}
