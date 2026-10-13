package org.bouncycastle.eac.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/eac/operator/EACSignatureVerifier.class */
public interface EACSignatureVerifier {
    ASN1ObjectIdentifier getUsageIdentifier();

    OutputStream getOutputStream();

    boolean verify(byte[] bArr);
}
