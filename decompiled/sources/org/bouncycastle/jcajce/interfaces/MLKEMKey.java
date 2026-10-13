package org.bouncycastle.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/interfaces/MLKEMKey.class */
public interface MLKEMKey extends Key {
    MLKEMParameterSpec getParameterSpec();
}
