package org.spongycastle.asn1.eac;

import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Sequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/eac/PublicKeyDataObject.class */
public abstract class PublicKeyDataObject extends ASN1Object {
    public abstract ASN1ObjectIdentifier getUsage();

    public static PublicKeyDataObject getInstance(Object obj) {
        if (obj instanceof PublicKeyDataObject) {
            return (PublicKeyDataObject) obj;
        }
        if (obj != null) {
            ASN1Sequence seq = ASN1Sequence.getInstance(obj);
            ASN1ObjectIdentifier usage = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0));
            if (usage.on(EACObjectIdentifiers.id_TA_ECDSA)) {
                return new ECDSAPublicKey(seq);
            }
            return new RSAPublicKey(seq);
        }
        return null;
    }
}
