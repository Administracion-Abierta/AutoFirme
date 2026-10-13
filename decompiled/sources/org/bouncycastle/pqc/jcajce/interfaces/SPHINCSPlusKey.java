package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.pqc.jcajce.spec.SPHINCSPlusParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/interfaces/SPHINCSPlusKey.class */
public interface SPHINCSPlusKey extends Key {
    SPHINCSPlusParameterSpec getParameterSpec();
}
