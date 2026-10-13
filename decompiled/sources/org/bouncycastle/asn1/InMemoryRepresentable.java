package org.bouncycastle.asn1;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/InMemoryRepresentable.class */
public interface InMemoryRepresentable {
    ASN1Primitive getLoadedObject() throws IOException;
}
