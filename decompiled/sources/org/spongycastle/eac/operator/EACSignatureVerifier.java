package org.spongycastle.eac.operator;

import java.io.OutputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/eac/operator/EACSignatureVerifier.class */
public interface EACSignatureVerifier {
    ASN1ObjectIdentifier getUsageIdentifier();

    OutputStream getOutputStream();

    boolean verify(byte[] bArr);
}
