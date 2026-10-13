package org.spongycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/LazyEncodedSequence.class */
class LazyEncodedSequence extends ASN1Sequence {
    private byte[] encoded;

    LazyEncodedSequence(byte[] encoded) throws IOException {
        this.encoded = encoded;
    }

    private void parse() {
        Enumeration en = new LazyConstructionEnumeration(this.encoded);
        while (en.hasMoreElements()) {
            this.seq.addElement(en.nextElement());
        }
        this.encoded = null;
    }

    @Override // org.spongycastle.asn1.ASN1Sequence
    public synchronized ASN1Encodable getObjectAt(int index) {
        if (this.encoded != null) {
            parse();
        }
        return super.getObjectAt(index);
    }

    @Override // org.spongycastle.asn1.ASN1Sequence
    public synchronized Enumeration getObjects() {
        if (this.encoded == null) {
            return super.getObjects();
        }
        return new LazyConstructionEnumeration(this.encoded);
    }

    @Override // org.spongycastle.asn1.ASN1Sequence
    public synchronized int size() {
        if (this.encoded != null) {
            parse();
        }
        return super.size();
    }

    @Override // org.spongycastle.asn1.ASN1Sequence, org.spongycastle.asn1.ASN1Primitive
    ASN1Primitive toDERObject() {
        if (this.encoded != null) {
            parse();
        }
        return super.toDERObject();
    }

    @Override // org.spongycastle.asn1.ASN1Sequence, org.spongycastle.asn1.ASN1Primitive
    ASN1Primitive toDLObject() {
        if (this.encoded != null) {
            parse();
        }
        return super.toDLObject();
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    int encodedLength() throws IOException {
        if (this.encoded != null) {
            return 1 + StreamUtil.calculateBodyLength(this.encoded.length) + this.encoded.length;
        }
        return super.toDLObject().encodedLength();
    }

    @Override // org.spongycastle.asn1.ASN1Sequence, org.spongycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream out) throws IOException {
        if (this.encoded != null) {
            out.writeEncoded(48, this.encoded);
        } else {
            super.toDLObject().encode(out);
        }
    }
}
