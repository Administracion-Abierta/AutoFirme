package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/interfaces/DilithiumKey.class */
public interface DilithiumKey extends Key {
    DilithiumParameterSpec getParameterSpec();
}
