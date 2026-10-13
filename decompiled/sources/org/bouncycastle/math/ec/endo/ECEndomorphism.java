package org.bouncycastle.math.ec.endo;

import org.bouncycastle.math.ec.ECPointMap;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/endo/ECEndomorphism.class */
public interface ECEndomorphism {
    ECPointMap getPointMap();

    boolean hasEfficientPointMap();
}
