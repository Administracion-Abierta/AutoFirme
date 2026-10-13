package org.bouncycastle.oer;

import org.bouncycastle.asn1.ASN1Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/Switch.class */
public interface Switch {
    Element result(SwitchIndexer switchIndexer);

    ASN1Encodable[] keys();
}
