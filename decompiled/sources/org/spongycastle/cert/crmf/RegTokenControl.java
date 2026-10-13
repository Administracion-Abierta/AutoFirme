package org.spongycastle.cert.crmf;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERUTF8String;
import org.spongycastle.asn1.crmf.CRMFObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/crmf/RegTokenControl.class */
public class RegTokenControl implements Control {
    private static final ASN1ObjectIdentifier type = CRMFObjectIdentifiers.id_regCtrl_regToken;
    private final DERUTF8String token;

    public RegTokenControl(DERUTF8String token) {
        this.token = token;
    }

    public RegTokenControl(String token) {
        this.token = new DERUTF8String(token);
    }

    @Override // org.spongycastle.cert.crmf.Control
    public ASN1ObjectIdentifier getType() {
        return type;
    }

    @Override // org.spongycastle.cert.crmf.Control
    public ASN1Encodable getValue() {
        return this.token;
    }
}
