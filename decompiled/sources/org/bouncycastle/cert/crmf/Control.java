package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/Control.class */
public interface Control {
    ASN1ObjectIdentifier getType();

    ASN1Encodable getValue();
}
