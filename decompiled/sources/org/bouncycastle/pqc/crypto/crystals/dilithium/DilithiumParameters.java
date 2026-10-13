package org.bouncycastle.pqc.crypto.crystals.dilithium;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/crystals/dilithium/DilithiumParameters.class */
public class DilithiumParameters {
    public static final DilithiumParameters dilithium2 = new DilithiumParameters("dilithium2", 2, false);
    public static final DilithiumParameters dilithium3 = new DilithiumParameters("dilithium3", 3, false);
    public static final DilithiumParameters dilithium5 = new DilithiumParameters("dilithium5", 5, false);
    private final int k;
    private final String name;
    private final boolean usingAES;

    private DilithiumParameters(String str, int i, boolean z) {
        this.name = str;
        this.k = i;
        this.usingAES = z;
    }

    DilithiumEngine getEngine(SecureRandom secureRandom) {
        return new DilithiumEngine(this.k, secureRandom, this.usingAES);
    }

    public String getName() {
        return this.name;
    }
}
