package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.AlgorithmParameters;
import java.security.spec.AlgorithmParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/util/SpecUtil.class */
class SpecUtil {
    SpecUtil() {
    }

    static AlgorithmParameterSpec extractSpec(AlgorithmParameters algorithmParameters, Class[] clsArr) {
        try {
            return algorithmParameters.getParameterSpec(AlgorithmParameterSpec.class);
        } catch (Exception e) {
            for (int i = 0; i != clsArr.length; i++) {
                if (clsArr[i] != null) {
                    try {
                        return algorithmParameters.getParameterSpec(clsArr[i]);
                    } catch (Exception e2) {
                    }
                }
            }
            return null;
        }
    }
}
