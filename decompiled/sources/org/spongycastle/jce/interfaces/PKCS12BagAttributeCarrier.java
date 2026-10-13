package org.spongycastle.jce.interfaces;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/interfaces/PKCS12BagAttributeCarrier.class */
public interface PKCS12BagAttributeCarrier {
    void setBagAttribute(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Encodable aSN1Encodable);

    ASN1Encodable getBagAttribute(ASN1ObjectIdentifier aSN1ObjectIdentifier);

    Enumeration getBagAttributeKeys();
}
