package org.spongycastle.dvcs;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.cms.ContentInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/dvcs/DVCSMessage.class */
public abstract class DVCSMessage {
    private final ContentInfo contentInfo;

    public abstract ASN1Encodable getContent();

    protected DVCSMessage(ContentInfo contentInfo) {
        this.contentInfo = contentInfo;
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.contentInfo.getContentType();
    }
}
