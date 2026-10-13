package org.spongycastle.asn1;

import java.io.IOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/ASN1TaggedObjectParser.class */
public interface ASN1TaggedObjectParser extends ASN1Encodable, InMemoryRepresentable {
    int getTagNo();

    ASN1Encodable getObjectParser(int i, boolean z) throws IOException;
}
