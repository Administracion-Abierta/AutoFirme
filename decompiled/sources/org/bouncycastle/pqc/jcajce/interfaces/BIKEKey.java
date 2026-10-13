package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.pqc.jcajce.spec.BIKEParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/interfaces/BIKEKey.class */
public interface BIKEKey extends Key {
    BIKEParameterSpec getParameterSpec();
}
