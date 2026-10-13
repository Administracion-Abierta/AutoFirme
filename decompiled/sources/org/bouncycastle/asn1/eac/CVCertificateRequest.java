package org.bouncycastle.asn1.eac;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/eac/CVCertificateRequest.class */
public class CVCertificateRequest extends ASN1Object {
    private final ASN1TaggedObject original;
    private CertificateBody certificateBody;
    private byte[] innerSignature = null;
    private byte[] outerSignature;
    private static final int bodyValid = 1;
    private static final int signValid = 2;

    private CVCertificateRequest(ASN1TaggedObject aSN1TaggedObject) throws IOException {
        this.outerSignature = null;
        this.original = aSN1TaggedObject;
        if (!aSN1TaggedObject.hasTag(64, 7)) {
            initCertBody(aSN1TaggedObject);
            return;
        }
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1TaggedObject.getBaseUniversal(false, 16));
        initCertBody(ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(0), 64));
        this.outerSignature = ASN1OctetString.getInstance(ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(aSN1Sequence.size() - 1)).getBaseUniversal(false, 4)).getOctets();
    }

    private void initCertBody(ASN1TaggedObject aSN1TaggedObject) throws IOException {
        if (!aSN1TaggedObject.hasTag(64, 33)) {
            throw new IOException("not a CARDHOLDER_CERTIFICATE in request:" + aSN1TaggedObject.getTagNo());
        }
        int i = 0;
        Enumeration objects = ASN1Sequence.getInstance(aSN1TaggedObject.getBaseUniversal(false, 16)).getObjects();
        while (objects.hasMoreElements()) {
            ASN1TaggedObject aSN1TaggedObject2 = ASN1TaggedObject.getInstance(objects.nextElement(), 64);
            switch (aSN1TaggedObject2.getTagNo()) {
                case 55:
                    this.innerSignature = ASN1OctetString.getInstance(aSN1TaggedObject2.getBaseUniversal(false, 4)).getOctets();
                    i |= 2;
                    break;
                case 78:
                    this.certificateBody = CertificateBody.getInstance(aSN1TaggedObject2);
                    i |= 1;
                    break;
                default:
                    throw new IOException("Invalid tag, not an CV Certificate Request element:" + aSN1TaggedObject2.getTagNo());
            }
        }
        if ((i & 3) == 0) {
            throw new IOException("Invalid CARDHOLDER_CERTIFICATE in request:" + aSN1TaggedObject.getTagNo());
        }
    }

    public static CVCertificateRequest getInstance(Object obj) {
        if (obj instanceof CVCertificateRequest) {
            return (CVCertificateRequest) obj;
        }
        if (obj == null) {
            return null;
        }
        try {
            return new CVCertificateRequest(ASN1TaggedObject.getInstance(obj, 64));
        } catch (IOException e) {
            throw new ASN1ParsingException("unable to parse data: " + e.getMessage(), e);
        }
    }

    public CertificateBody getCertificateBody() {
        return this.certificateBody;
    }

    public PublicKeyDataObject getPublicKey() {
        return this.certificateBody.getPublicKey();
    }

    public byte[] getInnerSignature() {
        return Arrays.clone(this.innerSignature);
    }

    public byte[] getOuterSignature() {
        return Arrays.clone(this.outerSignature);
    }

    public boolean hasOuterSignature() {
        return this.outerSignature != null;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.original != null ? this.original : EACTagged.create(33, new DERSequence(this.certificateBody, EACTagged.create(55, this.innerSignature)));
    }
}
