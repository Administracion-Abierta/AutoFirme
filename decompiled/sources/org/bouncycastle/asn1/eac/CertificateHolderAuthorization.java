package org.bouncycastle.asn1.eac;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/eac/CertificateHolderAuthorization.class */
public class CertificateHolderAuthorization extends ASN1Object {
    public static final int CVCA = 192;
    public static final int DV_DOMESTIC = 128;
    public static final int DV_FOREIGN = 64;
    public static final int IS = 0;
    public static final int RADG4 = 2;
    public static final int RADG3 = 1;
    private ASN1ObjectIdentifier oid;
    private byte accessRights;
    public static final ASN1ObjectIdentifier id_role_EAC = EACObjectIdentifiers.bsi_de.branch("3.1.2.1");
    static Map RightsDecodeMap = new HashMap();
    static BidirectionalMap AuthorizationRole = new BidirectionalMap();

    public static String getRoleDescription(int i) {
        return (String) AuthorizationRole.get(Integers.valueOf(i));
    }

    public static int getFlag(String str) {
        Integer num = (Integer) AuthorizationRole.getReverse(str);
        if (num == null) {
            throw new IllegalArgumentException("Unknown value " + str);
        }
        return num.intValue();
    }

    private void setPrivateData(ASN1Sequence aSN1Sequence) {
        ASN1Primitive aSN1Primitive = (ASN1Primitive) aSN1Sequence.getObjectAt(0);
        if (!(aSN1Primitive instanceof ASN1ObjectIdentifier)) {
            throw new IllegalArgumentException("no Oid in CerticateHolderAuthorization");
        }
        this.oid = (ASN1ObjectIdentifier) aSN1Primitive;
        ASN1Primitive aSN1Primitive2 = (ASN1Primitive) aSN1Sequence.getObjectAt(1);
        if (!(aSN1Primitive2 instanceof ASN1TaggedObject)) {
            throw new IllegalArgumentException("No access rights in CerticateHolderAuthorization");
        }
        this.accessRights = ASN1OctetString.getInstance(ASN1TaggedObject.getInstance(aSN1Primitive2, 64, 19).getBaseUniversal(false, 4)).getOctets()[0];
    }

    public CertificateHolderAuthorization(ASN1ObjectIdentifier aSN1ObjectIdentifier, int i) throws IOException {
        setOid(aSN1ObjectIdentifier);
        setAccessRights((byte) i);
    }

    public CertificateHolderAuthorization(ASN1TaggedObject aSN1TaggedObject) throws IOException {
        if (!aSN1TaggedObject.hasTag(64, 76)) {
            throw new IllegalArgumentException("Unrecognized object in CerticateHolderAuthorization");
        }
        setPrivateData(ASN1Sequence.getInstance(aSN1TaggedObject.getBaseUniversal(false, 16)));
    }

    public int getAccessRights() {
        return this.accessRights & 255;
    }

    private void setAccessRights(byte b) {
        this.accessRights = b;
    }

    public ASN1ObjectIdentifier getOid() {
        return this.oid;
    }

    private void setOid(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.oid = aSN1ObjectIdentifier;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return EACTagged.create(76, new DERSequence(this.oid, EACTagged.create(19, new byte[]{this.accessRights})));
    }

    static {
        RightsDecodeMap.put(Integers.valueOf(2), "RADG4");
        RightsDecodeMap.put(Integers.valueOf(1), "RADG3");
        AuthorizationRole.put(Integers.valueOf(192), "CVCA");
        AuthorizationRole.put(Integers.valueOf(128), "DV_DOMESTIC");
        AuthorizationRole.put(Integers.valueOf(64), "DV_FOREIGN");
        AuthorizationRole.put(Integers.valueOf(0), "IS");
    }
}
