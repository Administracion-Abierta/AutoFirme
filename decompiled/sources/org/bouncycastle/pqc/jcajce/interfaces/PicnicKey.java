package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.pqc.jcajce.spec.PicnicParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/interfaces/PicnicKey.class */
public interface PicnicKey extends Key {
    PicnicParameterSpec getParameterSpec();
}
