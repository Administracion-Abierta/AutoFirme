package org.bouncycastle.asn1.cmp;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.Certificate;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmp/OOBCert.class */
public class OOBCert extends CMPCertificate {
    public OOBCert(AttributeCertificate attributeCertificate) {
        super(1, attributeCertificate);
    }

    public OOBCert(int i, ASN1Object aSN1Object) {
        super(i, aSN1Object);
    }

    public OOBCert(Certificate certificate) {
        super(certificate);
    }

    public static OOBCert getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        if (aSN1TaggedObject == null) {
            return null;
        }
        if (z) {
            return getInstance((Object) aSN1TaggedObject.getExplicitBaseObject());
        }
        throw new IllegalArgumentException("tag must be explicit");
    }

    public static OOBCert getInstance(Object obj) {
        if (obj == null || (obj instanceof OOBCert)) {
            return (OOBCert) obj;
        }
        if (obj instanceof CMPCertificate) {
            try {
                return getInstance((Object) ((CMPCertificate) obj).getEncoded());
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
        if (obj instanceof byte[]) {
            try {
                obj = ASN1Primitive.fromByteArray((byte[]) obj);
            } catch (IOException e2) {
                throw new IllegalArgumentException("Invalid encoding in OOBCert");
            }
        }
        if (obj instanceof ASN1Sequence) {
            return new OOBCert(Certificate.getInstance(obj));
        }
        if (!(obj instanceof ASN1TaggedObject)) {
            throw new IllegalArgumentException("Invalid object: " + obj.getClass().getName());
        }
        ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(obj, 128);
        return new OOBCert(aSN1TaggedObject.getTagNo(), aSN1TaggedObject.getExplicitBaseObject());
    }
}
