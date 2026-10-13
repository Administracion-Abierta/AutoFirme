package org.bouncycastle.oer;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.asn1.ASN1Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/OEREncoder.class */
public class OEREncoder {
    public static byte[] toByteArray(ASN1Encodable aSN1Encodable, Element element) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new OEROutputStream(byteArrayOutputStream).write(aSN1Encodable, element);
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
