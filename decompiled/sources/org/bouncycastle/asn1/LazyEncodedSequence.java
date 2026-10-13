package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/LazyEncodedSequence.class */
class LazyEncodedSequence extends ASN1Sequence {
    private byte[] encoded;

    LazyEncodedSequence(byte[] bArr) throws IOException {
        if (null == bArr) {
            throw new NullPointerException("'encoded' cannot be null");
        }
        this.encoded = bArr;
    }

    @Override // org.bouncycastle.asn1.ASN1Sequence
    public ASN1Encodable getObjectAt(int i) {
        force();
        return super.getObjectAt(i);
    }

    @Override // org.bouncycastle.asn1.ASN1Sequence
    public Enumeration getObjects() {
        byte[] contents = getContents();
        return null != contents ? new LazyConstructionEnumeration(contents) : super.getObjects();
    }

    @Override // org.bouncycastle.asn1.ASN1Sequence, org.bouncycastle.asn1.ASN1Primitive, org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        force();
        return super.hashCode();
    }

    @Override // org.bouncycastle.asn1.ASN1Sequence, org.bouncycastle.util.Iterable, java.lang.Iterable
    public Iterator<ASN1Encodable> iterator() {
        force();
        return super.iterator();
    }

    @Override // org.bouncycastle.asn1.ASN1Sequence
    public int size() {
        force();
        return super.size();
    }

    @Override // org.bouncycastle.asn1.ASN1Sequence
    public ASN1Encodable[] toArray() {
        force();
        return super.toArray();
    }

    @Override // org.bouncycastle.asn1.ASN1Sequence
    ASN1Encodable[] toArrayInternal() {
        force();
        return super.toArrayInternal();
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    int encodedLength(boolean z) throws IOException {
        byte[] contents = getContents();
        return null != contents ? ASN1OutputStream.getLengthOfEncodingDL(z, contents.length) : super.toDLObject().encodedLength(z);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException {
        byte[] contents = getContents();
        if (null != contents) {
            aSN1OutputStream.writeEncodingDL(z, 48, contents);
        } else {
            super.toDLObject().encode(aSN1OutputStream, z);
        }
    }

    @Override // org.bouncycastle.asn1.ASN1Sequence
    ASN1BitString toASN1BitString() {
        return ((ASN1Sequence) toDLObject()).toASN1BitString();
    }

    @Override // org.bouncycastle.asn1.ASN1Sequence
    ASN1External toASN1External() {
        return ((ASN1Sequence) toDLObject()).toASN1External();
    }

    @Override // org.bouncycastle.asn1.ASN1Sequence
    ASN1OctetString toASN1OctetString() {
        return ((ASN1Sequence) toDLObject()).toASN1OctetString();
    }

    @Override // org.bouncycastle.asn1.ASN1Sequence
    ASN1Set toASN1Set() {
        return ((ASN1Sequence) toDLObject()).toASN1Set();
    }

    @Override // org.bouncycastle.asn1.ASN1Sequence, org.bouncycastle.asn1.ASN1Primitive
    ASN1Primitive toDERObject() {
        force();
        return super.toDERObject();
    }

    @Override // org.bouncycastle.asn1.ASN1Sequence, org.bouncycastle.asn1.ASN1Primitive
    ASN1Primitive toDLObject() {
        force();
        return super.toDLObject();
    }

    private synchronized void force() {
        if (null != this.encoded) {
            ASN1InputStream aSN1InputStream = new ASN1InputStream(this.encoded, true);
            try {
                ASN1EncodableVector vector = aSN1InputStream.readVector();
                aSN1InputStream.close();
                this.elements = vector.takeElements();
                this.encoded = null;
            } catch (IOException e) {
                throw new ASN1ParsingException("malformed ASN.1: " + e, e);
            }
        }
    }

    private synchronized byte[] getContents() {
        return this.encoded;
    }
}
