package org.spongycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/DLOutputStream.class */
public class DLOutputStream extends ASN1OutputStream {
    public DLOutputStream(OutputStream os) {
        super(os);
    }

    @Override // org.spongycastle.asn1.ASN1OutputStream
    public void writeObject(ASN1Encodable obj) throws IOException {
        if (obj != null) {
            obj.toASN1Primitive().toDLObject().encode(this);
            return;
        }
        throw new IOException("null object detected");
    }
}
