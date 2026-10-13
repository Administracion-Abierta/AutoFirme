package org.bouncycastle.eac.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/eac/operator/EACSigner.class */
public interface EACSigner {
    ASN1ObjectIdentifier getUsageIdentifier();

    OutputStream getOutputStream();

    byte[] getSignature();
}
