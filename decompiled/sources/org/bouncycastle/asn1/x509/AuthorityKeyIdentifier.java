package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/AuthorityKeyIdentifier.class */
public class AuthorityKeyIdentifier extends ASN1Object {
    ASN1OctetString keyidentifier;
    GeneralNames certissuer;
    ASN1Integer certserno;

    public static AuthorityKeyIdentifier getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public static AuthorityKeyIdentifier getInstance(Object obj) {
        if (obj instanceof AuthorityKeyIdentifier) {
            return (AuthorityKeyIdentifier) obj;
        }
        if (obj != null) {
            return new AuthorityKeyIdentifier(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static AuthorityKeyIdentifier fromExtensions(Extensions extensions) {
        return getInstance(Extensions.getExtensionParsedValue(extensions, Extension.authorityKeyIdentifier));
    }

    protected AuthorityKeyIdentifier(ASN1Sequence aSN1Sequence) {
        this.keyidentifier = null;
        this.certissuer = null;
        this.certserno = null;
        Enumeration objects = aSN1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(objects.nextElement());
            switch (aSN1TaggedObject.getTagNo()) {
                case 0:
                    this.keyidentifier = ASN1OctetString.getInstance(aSN1TaggedObject, false);
                    break;
                case 1:
                    this.certissuer = GeneralNames.getInstance(aSN1TaggedObject, false);
                    break;
                case 2:
                    this.certserno = ASN1Integer.getInstance(aSN1TaggedObject, false);
                    break;
                default:
                    throw new IllegalArgumentException("illegal tag");
            }
        }
    }

    public AuthorityKeyIdentifier(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this(subjectPublicKeyInfo, (GeneralNames) null, (BigInteger) null);
    }

    public AuthorityKeyIdentifier(SubjectPublicKeyInfo subjectPublicKeyInfo, GeneralNames generalNames, BigInteger bigInteger) {
        this.keyidentifier = null;
        this.certissuer = null;
        this.certserno = null;
        SHA1Digest sHA1Digest = new SHA1Digest();
        byte[] bArr = new byte[sHA1Digest.getDigestSize()];
        byte[] bytes = subjectPublicKeyInfo.getPublicKeyData().getBytes();
        sHA1Digest.update(bytes, 0, bytes.length);
        sHA1Digest.doFinal(bArr, 0);
        this.keyidentifier = new DEROctetString(bArr);
        this.certissuer = generalNames;
        this.certserno = bigInteger != null ? new ASN1Integer(bigInteger) : null;
    }

    public AuthorityKeyIdentifier(GeneralNames generalNames, BigInteger bigInteger) {
        this((byte[]) null, generalNames, bigInteger);
    }

    public AuthorityKeyIdentifier(byte[] bArr) {
        this(bArr, (GeneralNames) null, (BigInteger) null);
    }

    public AuthorityKeyIdentifier(byte[] bArr, GeneralNames generalNames, BigInteger bigInteger) {
        this.keyidentifier = null;
        this.certissuer = null;
        this.certserno = null;
        this.keyidentifier = bArr != null ? new DEROctetString(Arrays.clone(bArr)) : null;
        this.certissuer = generalNames;
        this.certserno = bigInteger != null ? new ASN1Integer(bigInteger) : null;
    }

    public byte[] getKeyIdentifier() {
        if (this.keyidentifier != null) {
            return this.keyidentifier.getOctets();
        }
        return null;
    }

    public GeneralNames getAuthorityCertIssuer() {
        return this.certissuer;
    }

    public BigInteger getAuthorityCertSerialNumber() {
        if (this.certserno != null) {
            return this.certserno.getValue();
        }
        return null;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        if (this.keyidentifier != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 0, (ASN1Encodable) this.keyidentifier));
        }
        if (this.certissuer != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 1, (ASN1Encodable) this.certissuer));
        }
        if (this.certserno != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 2, (ASN1Encodable) this.certserno));
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public String toString() {
        return "AuthorityKeyIdentifier: KeyID(" + (this.keyidentifier != null ? Hex.toHexString(this.keyidentifier.getOctets()) : "null") + ")";
    }
}
