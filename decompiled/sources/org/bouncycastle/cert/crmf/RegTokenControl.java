package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/RegTokenControl.class */
public class RegTokenControl implements Control {
    private static final ASN1ObjectIdentifier type = CRMFObjectIdentifiers.id_regCtrl_regToken;
    private final ASN1UTF8String token;

    public RegTokenControl(ASN1UTF8String aSN1UTF8String) {
        this.token = aSN1UTF8String;
    }

    public RegTokenControl(String str) {
        this.token = new DERUTF8String(str);
    }

    @Override // org.bouncycastle.cert.crmf.Control
    public ASN1ObjectIdentifier getType() {
        return type;
    }

    @Override // org.bouncycastle.cert.crmf.Control
    public ASN1Encodable getValue() {
        return this.token;
    }
}
