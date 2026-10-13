package org.bouncycastle.crypto.constraints;

import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/constraints/Utils.class */
class Utils {
    Utils() {
    }

    static void addAliases(Set<String> set) {
        if (set.contains("RC4")) {
            set.add("ARC4");
        } else if (set.contains("ARC4")) {
            set.add("RC4");
        }
    }
}
