package org.bouncycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECMultiplier.class */
public interface ECMultiplier {
    ECPoint multiply(ECPoint eCPoint, BigInteger bigInteger);
}
