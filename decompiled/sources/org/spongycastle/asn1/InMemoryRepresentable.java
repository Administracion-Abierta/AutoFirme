package org.spongycastle.asn1;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/InMemoryRepresentable.class */
public interface InMemoryRepresentable {
    ASN1Primitive getLoadedObject() throws IOException;
}
