package org.spongycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/ec/ECMultiplier.class */
public interface ECMultiplier {
    ECPoint multiply(ECPoint eCPoint, BigInteger bigInteger);
}
