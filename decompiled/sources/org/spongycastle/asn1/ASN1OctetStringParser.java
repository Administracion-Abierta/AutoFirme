package org.spongycastle.asn1;

import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/ASN1OctetStringParser.class */
public interface ASN1OctetStringParser extends ASN1Encodable, InMemoryRepresentable {
    InputStream getOctetStream();
}
