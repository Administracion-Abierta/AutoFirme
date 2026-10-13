package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.pqc.jcajce.spec.FalconParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/interfaces/FalconKey.class */
public interface FalconKey extends Key {
    FalconParameterSpec getParameterSpec();
}
