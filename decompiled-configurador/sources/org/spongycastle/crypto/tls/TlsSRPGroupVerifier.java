package org.spongycastle.crypto.tls;

import org.spongycastle.crypto.params.SRP6GroupParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsSRPGroupVerifier.class */
public interface TlsSRPGroupVerifier {
    boolean accept(SRP6GroupParameters sRP6GroupParameters);
}
