package org.spongycastle.eac.operator;

import java.io.OutputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/eac/operator/EACSigner.class */
public interface EACSigner {
    ASN1ObjectIdentifier getUsageIdentifier();

    OutputStream getOutputStream();

    byte[] getSignature();
}
