package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1Set;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/AuthAttributesProvider.class */
interface AuthAttributesProvider {
    ASN1Set getAuthAttributes();

    boolean isAead();
}
