package org.spongycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/DLSequence.class */
public class DLSequence extends ASN1Sequence {
    private int bodyLength;

    public DLSequence() {
        this.bodyLength = -1;
    }

    public DLSequence(ASN1Encodable obj) {
        super(obj);
        this.bodyLength = -1;
    }

    public DLSequence(ASN1EncodableVector v) {
        super(v);
        this.bodyLength = -1;
    }

    public DLSequence(ASN1Encodable[] array) {
        super(array);
        this.bodyLength = -1;
    }

    private int getBodyLength() throws IOException {
        if (this.bodyLength < 0) {
            int length = 0;
            Enumeration e = getObjects();
            while (e.hasMoreElements()) {
                Object obj = e.nextElement();
                length += ((ASN1Encodable) obj).toASN1Primitive().toDLObject().encodedLength();
            }
            this.bodyLength = length;
        }
        return this.bodyLength;
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    int encodedLength() throws IOException {
        int length = getBodyLength();
        return 1 + StreamUtil.calculateBodyLength(length) + length;
    }

    @Override // org.spongycastle.asn1.ASN1Sequence, org.spongycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream out) throws IOException {
        ASN1OutputStream dOut = out.getDLSubStream();
        int length = getBodyLength();
        out.write(48);
        out.writeLength(length);
        Enumeration e = getObjects();
        while (e.hasMoreElements()) {
            Object obj = e.nextElement();
            dOut.writeObject((ASN1Encodable) obj);
        }
    }
}
