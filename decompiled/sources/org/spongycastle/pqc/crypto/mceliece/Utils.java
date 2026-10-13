package org.spongycastle.pqc.crypto.mceliece;

import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SHA1Digest;
import org.spongycastle.crypto.digests.SHA224Digest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.digests.SHA384Digest;
import org.spongycastle.crypto.digests.SHA512Digest;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/mceliece/Utils.class */
class Utils {
    Utils() {
    }

    static Digest getDigest(String digestName) {
        if (digestName.equals("SHA-1")) {
            return new SHA1Digest();
        }
        if (digestName.equals("SHA-224")) {
            return new SHA224Digest();
        }
        if (digestName.equals("SHA-256")) {
            return new SHA256Digest();
        }
        if (digestName.equals("SHA-384")) {
            return new SHA384Digest();
        }
        if (digestName.equals("SHA-512")) {
            return new SHA512Digest();
        }
        throw new IllegalArgumentException("unrecognised digest algorithm: " + digestName);
    }
}
