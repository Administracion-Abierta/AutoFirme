package org.bouncycastle.asn1;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1EncodableVector.class */
public class ASN1EncodableVector {
    static final ASN1Encodable[] EMPTY_ELEMENTS = new ASN1Encodable[0];
    private static final int DEFAULT_CAPACITY = 10;
    private ASN1Encodable[] elements;
    private int elementCount;
    private boolean copyOnWrite;

    public ASN1EncodableVector() {
        this(10);
    }

    public ASN1EncodableVector(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("'initialCapacity' must not be negative");
        }
        this.elements = i == 0 ? EMPTY_ELEMENTS : new ASN1Encodable[i];
        this.elementCount = 0;
        this.copyOnWrite = false;
    }

    public void add(ASN1Encodable aSN1Encodable) {
        if (null == aSN1Encodable) {
            throw new NullPointerException("'element' cannot be null");
        }
        int length = this.elements.length;
        int i = this.elementCount + 1;
        if ((i > length) | this.copyOnWrite) {
            reallocate(i);
        }
        this.elements[this.elementCount] = aSN1Encodable;
        this.elementCount = i;
    }

    public void addAll(ASN1Encodable[] aSN1EncodableArr) {
        if (null == aSN1EncodableArr) {
            throw new NullPointerException("'others' cannot be null");
        }
        doAddAll(aSN1EncodableArr, "'others' elements cannot be null");
    }

    public void addAll(ASN1EncodableVector aSN1EncodableVector) {
        if (null == aSN1EncodableVector) {
            throw new NullPointerException("'other' cannot be null");
        }
        doAddAll(aSN1EncodableVector.elements, "'other' elements cannot be null");
    }

    private void doAddAll(ASN1Encodable[] aSN1EncodableArr, String str) {
        int length = aSN1EncodableArr.length;
        if (length < 1) {
            return;
        }
        int length2 = this.elements.length;
        int i = this.elementCount + length;
        if ((i > length2) | this.copyOnWrite) {
            reallocate(i);
        }
        int i2 = 0;
        do {
            ASN1Encodable aSN1Encodable = aSN1EncodableArr[i2];
            if (null == aSN1Encodable) {
                throw new NullPointerException(str);
            }
            this.elements[this.elementCount + i2] = aSN1Encodable;
            i2++;
        } while (i2 < length);
        this.elementCount = i;
    }

    public ASN1Encodable get(int i) {
        if (i >= this.elementCount) {
            throw new ArrayIndexOutOfBoundsException(i + " >= " + this.elementCount);
        }
        return this.elements[i];
    }

    public int size() {
        return this.elementCount;
    }

    ASN1Encodable[] copyElements() {
        if (0 == this.elementCount) {
            return EMPTY_ELEMENTS;
        }
        ASN1Encodable[] aSN1EncodableArr = new ASN1Encodable[this.elementCount];
        System.arraycopy(this.elements, 0, aSN1EncodableArr, 0, this.elementCount);
        return aSN1EncodableArr;
    }

    ASN1Encodable[] takeElements() {
        if (0 == this.elementCount) {
            return EMPTY_ELEMENTS;
        }
        if (this.elements.length == this.elementCount) {
            this.copyOnWrite = true;
            return this.elements;
        }
        ASN1Encodable[] aSN1EncodableArr = new ASN1Encodable[this.elementCount];
        System.arraycopy(this.elements, 0, aSN1EncodableArr, 0, this.elementCount);
        return aSN1EncodableArr;
    }

    private void reallocate(int i) {
        ASN1Encodable[] aSN1EncodableArr = new ASN1Encodable[Math.max(this.elements.length, i + (i >> 1))];
        System.arraycopy(this.elements, 0, aSN1EncodableArr, 0, this.elementCount);
        this.elements = aSN1EncodableArr;
        this.copyOnWrite = false;
    }

    static ASN1Encodable[] cloneElements(ASN1Encodable[] aSN1EncodableArr) {
        return aSN1EncodableArr.length < 1 ? EMPTY_ELEMENTS : (ASN1Encodable[]) aSN1EncodableArr.clone();
    }
}
