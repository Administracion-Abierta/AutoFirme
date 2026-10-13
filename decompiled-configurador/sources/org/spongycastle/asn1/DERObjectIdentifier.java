package org.spongycastle.asn1;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/DERObjectIdentifier.class */
public class DERObjectIdentifier extends ASN1ObjectIdentifier {
    public DERObjectIdentifier(String identifier) {
        super(identifier);
    }

    DERObjectIdentifier(byte[] bytes) {
        super(bytes);
    }

    DERObjectIdentifier(ASN1ObjectIdentifier oid, String branch) {
        super(oid, branch);
    }
}
