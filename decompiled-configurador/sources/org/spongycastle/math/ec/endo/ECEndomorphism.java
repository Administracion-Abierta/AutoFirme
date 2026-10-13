package org.spongycastle.math.ec.endo;

import org.spongycastle.math.ec.ECPointMap;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/ec/endo/ECEndomorphism.class */
public interface ECEndomorphism {
    ECPointMap getPointMap();

    boolean hasEfficientPointMap();
}
