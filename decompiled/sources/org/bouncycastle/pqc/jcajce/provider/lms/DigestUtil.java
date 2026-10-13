package org.bouncycastle.pqc.jcajce.provider.lms;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/lms/DigestUtil.class */
class DigestUtil {
    DigestUtil() {
    }

    public static byte[] getDigestResult(Digest digest) {
        byte[] bArr = new byte[digest.getDigestSize()];
        if (digest instanceof Xof) {
            ((Xof) digest).doFinal(bArr, 0, bArr.length);
        } else {
            digest.doFinal(bArr, 0);
        }
        return bArr;
    }
}
