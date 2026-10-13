package org.bouncycastle.asn1.eac;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/eac/PublicKeyDataObject.class */
public abstract class PublicKeyDataObject extends ASN1Object {
    public static PublicKeyDataObject getInstance(Object obj) {
        if (obj instanceof PublicKeyDataObject) {
            return (PublicKeyDataObject) obj;
        }
        if (obj == null) {
            return null;
        }
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(obj);
        return ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(0)).on(EACObjectIdentifiers.id_TA_ECDSA) ? new ECDSAPublicKey(aSN1Sequence) : new RSAPublicKey(aSN1Sequence);
    }

    public abstract ASN1ObjectIdentifier getUsage();
}
