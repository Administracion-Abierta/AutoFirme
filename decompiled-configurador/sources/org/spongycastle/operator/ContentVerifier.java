package org.spongycastle.operator;

import java.io.OutputStream;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/ContentVerifier.class */
public interface ContentVerifier {
    AlgorithmIdentifier getAlgorithmIdentifier();

    OutputStream getOutputStream();

    boolean verify(byte[] bArr);
}
