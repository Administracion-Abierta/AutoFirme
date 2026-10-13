package org.spongycastle.asn1.esf;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.DERUTF8String;
import org.spongycastle.asn1.x500.DirectoryString;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/esf/SignerLocation.class */
public class SignerLocation extends ASN1Object {
    private DirectoryString countryName;
    private DirectoryString localityName;
    private ASN1Sequence postalAddress;

    private SignerLocation(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            ASN1TaggedObject o = (ASN1TaggedObject) e.nextElement();
            switch (o.getTagNo()) {
                case 0:
                    this.countryName = DirectoryString.getInstance(o, true);
                    break;
                case 1:
                    this.localityName = DirectoryString.getInstance(o, true);
                    break;
                case 2:
                    if (o.isExplicit()) {
                        this.postalAddress = ASN1Sequence.getInstance(o, true);
                    } else {
                        this.postalAddress = ASN1Sequence.getInstance(o, false);
                    }
                    if (this.postalAddress != null && this.postalAddress.size() > 6) {
                        throw new IllegalArgumentException("postal address must contain less than 6 strings");
                    }
                    break;
                    break;
                default:
                    throw new IllegalArgumentException("illegal tag");
            }
        }
    }

    private SignerLocation(DirectoryString countryName, DirectoryString localityName, ASN1Sequence postalAddress) {
        if (postalAddress != null && postalAddress.size() > 6) {
            throw new IllegalArgumentException("postal address must contain less than 6 strings");
        }
        this.countryName = countryName;
        this.localityName = localityName;
        this.postalAddress = postalAddress;
    }

    public SignerLocation(DirectoryString countryName, DirectoryString localityName, DirectoryString[] postalAddress) {
        this(countryName, localityName, new DERSequence(postalAddress));
    }

    public SignerLocation(DERUTF8String countryName, DERUTF8String localityName, ASN1Sequence postalAddress) {
        this(DirectoryString.getInstance(countryName), DirectoryString.getInstance(localityName), postalAddress);
    }

    public static SignerLocation getInstance(Object obj) {
        if (obj == null || (obj instanceof SignerLocation)) {
            return (SignerLocation) obj;
        }
        return new SignerLocation(ASN1Sequence.getInstance(obj));
    }

    public DirectoryString getCountry() {
        return this.countryName;
    }

    public DirectoryString getLocality() {
        return this.localityName;
    }

    public DirectoryString[] getPostal() {
        if (this.postalAddress == null) {
            return null;
        }
        DirectoryString[] dirStrings = new DirectoryString[this.postalAddress.size()];
        for (int i = 0; i != dirStrings.length; i++) {
            dirStrings[i] = DirectoryString.getInstance(this.postalAddress.getObjectAt(i));
        }
        return dirStrings;
    }

    public DERUTF8String getCountryName() {
        if (this.countryName == null) {
            return null;
        }
        return new DERUTF8String(getCountry().getString());
    }

    public DERUTF8String getLocalityName() {
        if (this.localityName == null) {
            return null;
        }
        return new DERUTF8String(getLocality().getString());
    }

    public ASN1Sequence getPostalAddress() {
        return this.postalAddress;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this.countryName != null) {
            v.add(new DERTaggedObject(true, 0, this.countryName));
        }
        if (this.localityName != null) {
            v.add(new DERTaggedObject(true, 1, this.localityName));
        }
        if (this.postalAddress != null) {
            v.add(new DERTaggedObject(true, 2, this.postalAddress));
        }
        return new DERSequence(v);
    }
}
