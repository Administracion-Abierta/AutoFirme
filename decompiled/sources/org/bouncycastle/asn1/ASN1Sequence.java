package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Iterable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1Sequence.class */
public abstract class ASN1Sequence extends ASN1Primitive implements Iterable<ASN1Encodable> {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Sequence.class, 16) { // from class: org.bouncycastle.asn1.ASN1Sequence.1
        @Override // org.bouncycastle.asn1.ASN1UniversalType
        ASN1Primitive fromImplicitConstructed(ASN1Sequence aSN1Sequence) {
            return aSN1Sequence;
        }
    };
    ASN1Encodable[] elements;

    public static ASN1Sequence getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1Sequence)) {
            return (ASN1Sequence) obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable) obj).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1Sequence) {
                return (ASN1Sequence) aSN1Primitive;
            }
        } else if (obj instanceof byte[]) {
            try {
                return (ASN1Sequence) TYPE.fromByteArray((byte[]) obj);
            } catch (IOException e) {
                throw new IllegalArgumentException("failed to construct sequence from byte[]: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1Sequence getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return (ASN1Sequence) TYPE.getContextInstance(aSN1TaggedObject, z);
    }

    protected ASN1Sequence() {
        this.elements = ASN1EncodableVector.EMPTY_ELEMENTS;
    }

    protected ASN1Sequence(ASN1Encodable aSN1Encodable) {
        if (null == aSN1Encodable) {
            throw new NullPointerException("'element' cannot be null");
        }
        this.elements = new ASN1Encodable[]{aSN1Encodable};
    }

    protected ASN1Sequence(ASN1Encodable aSN1Encodable, ASN1Encodable aSN1Encodable2) {
        if (null == aSN1Encodable) {
            throw new NullPointerException("'element1' cannot be null");
        }
        if (null == aSN1Encodable2) {
            throw new NullPointerException("'element2' cannot be null");
        }
        this.elements = new ASN1Encodable[]{aSN1Encodable, aSN1Encodable2};
    }

    protected ASN1Sequence(ASN1EncodableVector aSN1EncodableVector) {
        if (null == aSN1EncodableVector) {
            throw new NullPointerException("'elementVector' cannot be null");
        }
        this.elements = aSN1EncodableVector.takeElements();
    }

    protected ASN1Sequence(ASN1Encodable[] aSN1EncodableArr) {
        if (Arrays.isNullOrContainsNull(aSN1EncodableArr)) {
            throw new NullPointerException("'elements' cannot be null, or contain null");
        }
        this.elements = ASN1EncodableVector.cloneElements(aSN1EncodableArr);
    }

    ASN1Sequence(ASN1Encodable[] aSN1EncodableArr, boolean z) {
        this.elements = z ? ASN1EncodableVector.cloneElements(aSN1EncodableArr) : aSN1EncodableArr;
    }

    public ASN1Encodable[] toArray() {
        return ASN1EncodableVector.cloneElements(this.elements);
    }

    ASN1Encodable[] toArrayInternal() {
        return this.elements;
    }

    public Enumeration getObjects() {
        return new Enumeration() { // from class: org.bouncycastle.asn1.ASN1Sequence.2
            private int pos = 0;

            @Override // java.util.Enumeration
            public boolean hasMoreElements() {
                return this.pos < ASN1Sequence.this.elements.length;
            }

            @Override // java.util.Enumeration
            public Object nextElement() {
                if (this.pos >= ASN1Sequence.this.elements.length) {
                    throw new NoSuchElementException();
                }
                ASN1Encodable[] aSN1EncodableArr = ASN1Sequence.this.elements;
                int i = this.pos;
                this.pos = i + 1;
                return aSN1EncodableArr[i];
            }
        };
    }

    public ASN1SequenceParser parser() {
        final int size = size();
        return new ASN1SequenceParser() { // from class: org.bouncycastle.asn1.ASN1Sequence.3
            private int pos = 0;

            @Override // org.bouncycastle.asn1.ASN1SequenceParser
            public ASN1Encodable readObject() throws IOException {
                if (size == this.pos) {
                    return null;
                }
                ASN1Encodable[] aSN1EncodableArr = ASN1Sequence.this.elements;
                int i = this.pos;
                this.pos = i + 1;
                ASN1Encodable aSN1Encodable = aSN1EncodableArr[i];
                if (aSN1Encodable instanceof ASN1Sequence) {
                    return ((ASN1Sequence) aSN1Encodable).parser();
                }
                return aSN1Encodable instanceof ASN1Set ? ((ASN1Set) aSN1Encodable).parser() : aSN1Encodable;
            }

            @Override // org.bouncycastle.asn1.InMemoryRepresentable
            public ASN1Primitive getLoadedObject() {
                return ASN1Sequence.this;
            }

            @Override // org.bouncycastle.asn1.ASN1Encodable
            public ASN1Primitive toASN1Primitive() {
                return ASN1Sequence.this;
            }
        };
    }

    public ASN1Encodable getObjectAt(int i) {
        return this.elements[i];
    }

    public int size() {
        return this.elements.length;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive, org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        int length = this.elements.length;
        int iHashCode = length + 1;
        while (true) {
            int i = iHashCode;
            length--;
            if (length < 0) {
                return i;
            }
            iHashCode = (i * 257) ^ this.elements[length].toASN1Primitive().hashCode();
        }
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1Sequence)) {
            return false;
        }
        ASN1Sequence aSN1Sequence = (ASN1Sequence) aSN1Primitive;
        int size = size();
        if (aSN1Sequence.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            ASN1Primitive aSN1Primitive2 = this.elements[i].toASN1Primitive();
            ASN1Primitive aSN1Primitive3 = aSN1Sequence.elements[i].toASN1Primitive();
            if (aSN1Primitive2 != aSN1Primitive3 && !aSN1Primitive2.asn1Equals(aSN1Primitive3)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    ASN1Primitive toDERObject() {
        return new DERSequence(this.elements, false);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    ASN1Primitive toDLObject() {
        return new DLSequence(this.elements, false);
    }

    abstract ASN1BitString toASN1BitString();

    abstract ASN1External toASN1External();

    abstract ASN1OctetString toASN1OctetString();

    abstract ASN1Set toASN1Set();

    @Override // org.bouncycastle.asn1.ASN1Primitive
    boolean encodeConstructed() {
        return true;
    }

    public String toString() {
        int size = size();
        if (0 == size) {
            return "[]";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('[');
        int i = 0;
        while (true) {
            stringBuffer.append(this.elements[i]);
            i++;
            if (i >= size) {
                stringBuffer.append(']');
                return stringBuffer.toString();
            }
            stringBuffer.append(", ");
        }
    }

    @Override // org.bouncycastle.util.Iterable, java.lang.Iterable
    public Iterator<ASN1Encodable> iterator() {
        return new Arrays.Iterator(this.elements);
    }

    ASN1BitString[] getConstructedBitStrings() {
        int size = size();
        ASN1BitString[] aSN1BitStringArr = new ASN1BitString[size];
        for (int i = 0; i < size; i++) {
            aSN1BitStringArr[i] = ASN1BitString.getInstance(this.elements[i]);
        }
        return aSN1BitStringArr;
    }

    ASN1OctetString[] getConstructedOctetStrings() {
        int size = size();
        ASN1OctetString[] aSN1OctetStringArr = new ASN1OctetString[size];
        for (int i = 0; i < size; i++) {
            aSN1OctetStringArr[i] = ASN1OctetString.getInstance(this.elements[i]);
        }
        return aSN1OctetStringArr;
    }
}
