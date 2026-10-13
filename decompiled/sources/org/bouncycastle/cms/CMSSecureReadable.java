package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSSecureReadable.class */
interface CMSSecureReadable {
    ASN1ObjectIdentifier getContentType();

    InputStream getInputStream() throws CMSException, IOException;

    ASN1Set getAuthAttrSet();

    void setAuthAttrSet(ASN1Set aSN1Set);

    boolean hasAdditionalData();
}
