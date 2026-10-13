package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/ExtendedKeyUsage.class */
public class ExtendedKeyUsage extends ASN1Object {
    Hashtable usageTable = new Hashtable();
    ASN1Sequence seq;

    public static ExtendedKeyUsage getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public static ExtendedKeyUsage getInstance(Object obj) {
        if (obj instanceof ExtendedKeyUsage) {
            return (ExtendedKeyUsage) obj;
        }
        if (obj != null) {
            return new ExtendedKeyUsage(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static ExtendedKeyUsage fromExtensions(Extensions extensions) {
        return getInstance(Extensions.getExtensionParsedValue(extensions, Extension.extendedKeyUsage));
    }

    public ExtendedKeyUsage(KeyPurposeId keyPurposeId) {
        this.seq = new DERSequence(keyPurposeId);
        this.usageTable.put(keyPurposeId, keyPurposeId);
    }

    private ExtendedKeyUsage(ASN1Sequence aSN1Sequence) {
        this.seq = aSN1Sequence;
        Enumeration objects = aSN1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            ASN1Encodable aSN1Encodable = (ASN1Encodable) objects.nextElement();
            if (!(aSN1Encodable.toASN1Primitive() instanceof ASN1ObjectIdentifier)) {
                throw new IllegalArgumentException("Only ASN1ObjectIdentifiers allowed in ExtendedKeyUsage.");
            }
            this.usageTable.put(aSN1Encodable, aSN1Encodable);
        }
    }

    public ExtendedKeyUsage(KeyPurposeId[] keyPurposeIdArr) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(keyPurposeIdArr.length);
        for (int i = 0; i != keyPurposeIdArr.length; i++) {
            aSN1EncodableVector.add(keyPurposeIdArr[i]);
            this.usageTable.put(keyPurposeIdArr[i], keyPurposeIdArr[i]);
        }
        this.seq = new DERSequence(aSN1EncodableVector);
    }

    public ExtendedKeyUsage(Vector vector) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(vector.size());
        Enumeration enumerationElements = vector.elements();
        while (enumerationElements.hasMoreElements()) {
            KeyPurposeId keyPurposeId = KeyPurposeId.getInstance(enumerationElements.nextElement());
            aSN1EncodableVector.add(keyPurposeId);
            this.usageTable.put(keyPurposeId, keyPurposeId);
        }
        this.seq = new DERSequence(aSN1EncodableVector);
    }

    public boolean hasKeyPurposeId(KeyPurposeId keyPurposeId) {
        return this.usageTable.get(keyPurposeId) != null;
    }

    public KeyPurposeId[] getUsages() {
        KeyPurposeId[] keyPurposeIdArr = new KeyPurposeId[this.seq.size()];
        int i = 0;
        Enumeration objects = this.seq.getObjects();
        while (objects.hasMoreElements()) {
            int i2 = i;
            i++;
            keyPurposeIdArr[i2] = KeyPurposeId.getInstance(objects.nextElement());
        }
        return keyPurposeIdArr;
    }

    public int size() {
        return this.usageTable.size();
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}
