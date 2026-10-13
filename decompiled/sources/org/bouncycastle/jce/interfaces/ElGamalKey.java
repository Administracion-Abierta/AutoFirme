package org.bouncycastle.jce.interfaces;

import javax.crypto.interfaces.DHKey;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/interfaces/ElGamalKey.class */
public interface ElGamalKey extends DHKey {
    ElGamalParameterSpec getParameters();
}
