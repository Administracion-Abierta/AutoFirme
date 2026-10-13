package org.spongycastle.math.ec.endo;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/endo/GLVEndomorphism.class */
public interface GLVEndomorphism extends ECEndomorphism {
    BigInteger[] decomposeScalar(BigInteger bigInteger);
}
