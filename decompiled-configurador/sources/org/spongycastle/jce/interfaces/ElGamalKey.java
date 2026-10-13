package org.spongycastle.jce.interfaces;

import javax.crypto.interfaces.DHKey;
import org.spongycastle.jce.spec.ElGamalParameterSpec;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/interfaces/ElGamalKey.class */
public interface ElGamalKey extends DHKey {
    ElGamalParameterSpec getParameters();
}
