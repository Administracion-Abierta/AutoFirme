package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1Object.class */
public abstract class ASN1Object implements ASN1Encodable, Encodable {
    public void encodeTo(OutputStream outputStream) throws IOException {
        toASN1Primitive().encodeTo(outputStream);
    }

    public void encodeTo(OutputStream outputStream, String str) throws IOException {
        toASN1Primitive().encodeTo(outputStream, str);
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        toASN1Primitive().encodeTo(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] getEncoded(String str) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        toASN1Primitive().encodeTo(byteArrayOutputStream, str);
        return byteArrayOutputStream.toByteArray();
    }

    public int hashCode() {
        return toASN1Primitive().hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ASN1Encodable) {
            return toASN1Primitive().equals(((ASN1Encodable) obj).toASN1Primitive());
        }
        return false;
    }

    protected static boolean hasEncodedTagValue(Object obj, int i) {
        return (obj instanceof byte[]) && ((byte[]) obj)[0] == i;
    }

    @Override // org.bouncycastle.asn1.ASN1Encodable
    public abstract ASN1Primitive toASN1Primitive();
}
