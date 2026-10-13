package org.spongycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/DEROutputStream.class */
public class DEROutputStream extends ASN1OutputStream {
    public DEROutputStream(OutputStream os) {
        super(os);
    }

    @Override // org.spongycastle.asn1.ASN1OutputStream
    public void writeObject(ASN1Encodable obj) throws IOException {
        if (obj != null) {
            obj.toASN1Primitive().toDERObject().encode(this);
            return;
        }
        throw new IOException("null object detected");
    }

    @Override // org.spongycastle.asn1.ASN1OutputStream
    ASN1OutputStream getDERSubStream() {
        return this;
    }

    @Override // org.spongycastle.asn1.ASN1OutputStream
    ASN1OutputStream getDLSubStream() {
        return this;
    }
}
