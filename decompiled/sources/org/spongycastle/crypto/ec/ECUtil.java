package org.spongycastle.crypto.ec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.math.ec.ECConstants;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/ec/ECUtil.class */
class ECUtil {
    ECUtil() {
    }

    static BigInteger generateK(BigInteger n, SecureRandom random) {
        int nBitLength = n.bitLength();
        while (true) {
            BigInteger k = new BigInteger(nBitLength, random);
            if (!k.equals(ECConstants.ZERO) && k.compareTo(n) < 0) {
                return k;
            }
        }
    }
}
