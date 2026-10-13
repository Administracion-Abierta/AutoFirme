package org.spongycastle.operator;

import java.io.OutputStream;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/OutputCompressor.class */
public interface OutputCompressor {
    AlgorithmIdentifier getAlgorithmIdentifier();

    OutputStream getOutputStream(OutputStream outputStream);
}
