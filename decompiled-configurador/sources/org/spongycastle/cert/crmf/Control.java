package org.spongycastle.cert.crmf;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/crmf/Control.class */
public interface Control {
    ASN1ObjectIdentifier getType();

    ASN1Encodable getValue();
}
