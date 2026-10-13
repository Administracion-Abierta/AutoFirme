package org.bouncycastle.jce.interfaces;

import java.security.PublicKey;
import org.bouncycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/interfaces/ECPublicKey.class */
public interface ECPublicKey extends ECKey, PublicKey {
    ECPoint getQ();
}
