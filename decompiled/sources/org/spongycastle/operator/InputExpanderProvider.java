package org.spongycastle.operator;

import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/operator/InputExpanderProvider.class */
public interface InputExpanderProvider {
    InputExpander get(AlgorithmIdentifier algorithmIdentifier);
}
