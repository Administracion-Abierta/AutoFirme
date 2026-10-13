package org.bouncycastle.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/interfaces/MLDSAKey.class */
public interface MLDSAKey extends Key {
    MLDSAParameterSpec getParameterSpec();
}
