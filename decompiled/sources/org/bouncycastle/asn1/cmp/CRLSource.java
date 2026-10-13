package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralNames;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmp/CRLSource.class */
public class CRLSource extends ASN1Object implements ASN1Choice {
    private final DistributionPointName dpn;
    private final GeneralNames issuer;

    private CRLSource(ASN1TaggedObject aSN1TaggedObject) {
        if (aSN1TaggedObject.hasContextTag(0)) {
            this.dpn = DistributionPointName.getInstance(aSN1TaggedObject, true);
            this.issuer = null;
        } else {
            if (!aSN1TaggedObject.hasContextTag(1)) {
                throw new IllegalArgumentException("unknown tag " + ASN1Util.getTagText(aSN1TaggedObject));
            }
            this.dpn = null;
            this.issuer = GeneralNames.getInstance(aSN1TaggedObject, true);
        }
    }

    public CRLSource(DistributionPointName distributionPointName, GeneralNames generalNames) {
        if ((distributionPointName == null) == (generalNames == null)) {
            throw new IllegalArgumentException("either dpn or issuer must be set");
        }
        this.dpn = distributionPointName;
        this.issuer = generalNames;
    }

    public static CRLSource getInstance(Object obj) {
        if (obj instanceof CRLSource) {
            return (CRLSource) obj;
        }
        if (obj != null) {
            return new CRLSource(ASN1TaggedObject.getInstance(obj));
        }
        return null;
    }

    public DistributionPointName getDpn() {
        return this.dpn;
    }

    public GeneralNames getIssuer() {
        return this.issuer;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.dpn != null ? new DERTaggedObject(true, 0, (ASN1Encodable) this.dpn) : new DERTaggedObject(true, 1, (ASN1Encodable) this.issuer);
    }
}
