package org.bouncycastle.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.jcajce.spec.SLHDSAParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/interfaces/SLHDSAKey.class */
public interface SLHDSAKey extends Key {
    SLHDSAParameterSpec getParameterSpec();
}
