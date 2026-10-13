package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Objects;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1External.class */
public abstract class ASN1External extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1External.class, 8) { // from class: org.bouncycastle.asn1.ASN1External.1
        @Override // org.bouncycastle.asn1.ASN1UniversalType
        ASN1Primitive fromImplicitConstructed(ASN1Sequence aSN1Sequence) {
            return aSN1Sequence.toASN1External();
        }
    };
    ASN1ObjectIdentifier directReference;
    ASN1Integer indirectReference;
    ASN1Primitive dataValueDescriptor;
    int encoding;
    ASN1Primitive externalContent;

    public static ASN1External getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1External)) {
            return (ASN1External) obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable) obj).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1External) {
                return (ASN1External) aSN1Primitive;
            }
        } else if (obj instanceof byte[]) {
            try {
                return (ASN1External) TYPE.fromByteArray((byte[]) obj);
            } catch (IOException e) {
                throw new IllegalArgumentException("failed to construct external from byte[]: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1External getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return (ASN1External) TYPE.getContextInstance(aSN1TaggedObject, z);
    }

    ASN1External(ASN1Sequence aSN1Sequence) {
        int i = 0;
        ASN1Primitive objFromSequence = getObjFromSequence(aSN1Sequence, 0);
        if (objFromSequence instanceof ASN1ObjectIdentifier) {
            this.directReference = (ASN1ObjectIdentifier) objFromSequence;
            i = 0 + 1;
            objFromSequence = getObjFromSequence(aSN1Sequence, i);
        }
        if (objFromSequence instanceof ASN1Integer) {
            this.indirectReference = (ASN1Integer) objFromSequence;
            i++;
            objFromSequence = getObjFromSequence(aSN1Sequence, i);
        }
        if (!(objFromSequence instanceof ASN1TaggedObject)) {
            this.dataValueDescriptor = objFromSequence;
            i++;
            objFromSequence = getObjFromSequence(aSN1Sequence, i);
        }
        if (aSN1Sequence.size() != i + 1) {
            throw new IllegalArgumentException("input sequence too large");
        }
        if (!(objFromSequence instanceof ASN1TaggedObject)) {
            throw new IllegalArgumentException("No tagged object found in sequence. Structure doesn't seem to be of type External");
        }
        ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) objFromSequence;
        this.encoding = checkEncoding(aSN1TaggedObject.getTagNo());
        this.externalContent = getExternalContent(aSN1TaggedObject);
    }

    ASN1External(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Integer aSN1Integer, ASN1Primitive aSN1Primitive, DERTaggedObject dERTaggedObject) {
        this.directReference = aSN1ObjectIdentifier;
        this.indirectReference = aSN1Integer;
        this.dataValueDescriptor = aSN1Primitive;
        this.encoding = checkEncoding(dERTaggedObject.getTagNo());
        this.externalContent = getExternalContent(dERTaggedObject);
    }

    ASN1External(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Integer aSN1Integer, ASN1Primitive aSN1Primitive, int i, ASN1Primitive aSN1Primitive2) {
        this.directReference = aSN1ObjectIdentifier;
        this.indirectReference = aSN1Integer;
        this.dataValueDescriptor = aSN1Primitive;
        this.encoding = checkEncoding(i);
        this.externalContent = checkExternalContent(i, aSN1Primitive2);
    }

    abstract ASN1Sequence buildSequence();

    @Override // org.bouncycastle.asn1.ASN1Primitive
    int encodedLength(boolean z) throws IOException {
        return buildSequence().encodedLength(z);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException {
        aSN1OutputStream.writeIdentifier(z, 40);
        buildSequence().encode(aSN1OutputStream, false);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    ASN1Primitive toDERObject() {
        return new DERExternal(this.directReference, this.indirectReference, this.dataValueDescriptor, this.encoding, this.externalContent);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    ASN1Primitive toDLObject() {
        return new DLExternal(this.directReference, this.indirectReference, this.dataValueDescriptor, this.encoding, this.externalContent);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive, org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        return (((Objects.hashCode(this.directReference) ^ Objects.hashCode(this.indirectReference)) ^ Objects.hashCode(this.dataValueDescriptor)) ^ this.encoding) ^ this.externalContent.hashCode();
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    boolean encodeConstructed() {
        return true;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (this == aSN1Primitive) {
            return true;
        }
        if (!(aSN1Primitive instanceof ASN1External)) {
            return false;
        }
        ASN1External aSN1External = (ASN1External) aSN1Primitive;
        return Objects.areEqual(this.directReference, aSN1External.directReference) && Objects.areEqual(this.indirectReference, aSN1External.indirectReference) && Objects.areEqual(this.dataValueDescriptor, aSN1External.dataValueDescriptor) && this.encoding == aSN1External.encoding && this.externalContent.equals(aSN1External.externalContent);
    }

    public ASN1Primitive getDataValueDescriptor() {
        return this.dataValueDescriptor;
    }

    public ASN1ObjectIdentifier getDirectReference() {
        return this.directReference;
    }

    public int getEncoding() {
        return this.encoding;
    }

    public ASN1Primitive getExternalContent() {
        return this.externalContent;
    }

    public ASN1Integer getIndirectReference() {
        return this.indirectReference;
    }

    private static int checkEncoding(int i) {
        if (i < 0 || i > 2) {
            throw new IllegalArgumentException("invalid encoding value: " + i);
        }
        return i;
    }

    private static ASN1Primitive checkExternalContent(int i, ASN1Primitive aSN1Primitive) {
        switch (i) {
            case 1:
                return ASN1OctetString.TYPE.checkedCast(aSN1Primitive);
            case 2:
                return ASN1BitString.TYPE.checkedCast(aSN1Primitive);
            default:
                return aSN1Primitive;
        }
    }

    private static ASN1Primitive getExternalContent(ASN1TaggedObject aSN1TaggedObject) {
        ASN1Util.checkContextTagClass(aSN1TaggedObject);
        switch (aSN1TaggedObject.getTagNo()) {
            case 0:
                return aSN1TaggedObject.getExplicitBaseObject().toASN1Primitive();
            case 1:
                return ASN1OctetString.getInstance(aSN1TaggedObject, false);
            case 2:
                return ASN1BitString.getInstance(aSN1TaggedObject, false);
            default:
                throw new IllegalArgumentException("invalid tag: " + ASN1Util.getTagText(aSN1TaggedObject));
        }
    }

    private static ASN1Primitive getObjFromSequence(ASN1Sequence aSN1Sequence, int i) {
        if (aSN1Sequence.size() <= i) {
            throw new IllegalArgumentException("too few objects in input sequence");
        }
        return aSN1Sequence.getObjectAt(i).toASN1Primitive();
    }
}
