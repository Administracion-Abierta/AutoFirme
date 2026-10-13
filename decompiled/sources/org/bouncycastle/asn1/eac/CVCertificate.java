package org.bouncycastle.asn1.eac;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/eac/CVCertificate.class */
public class CVCertificate extends ASN1Object {
    private CertificateBody certificateBody;
    private byte[] signature;
    private int valid;
    private static int bodyValid = 1;
    private static int signValid = 2;

    private void setPrivateData(ASN1TaggedObject aSN1TaggedObject) throws IOException {
        this.valid = 0;
        if (!aSN1TaggedObject.hasTag(64, 33)) {
            throw new IOException("not a CARDHOLDER_CERTIFICATE :" + aSN1TaggedObject.getTagNo());
        }
        Enumeration objects = ASN1Sequence.getInstance(aSN1TaggedObject.getBaseUniversal(false, 16)).getObjects();
        while (objects.hasMoreElements()) {
            Object objNextElement = objects.nextElement();
            if (!(objNextElement instanceof ASN1TaggedObject)) {
                throw new IOException("Invalid Object, not an Iso7816CertificateStructure");
            }
            ASN1TaggedObject aSN1TaggedObject2 = ASN1TaggedObject.getInstance(objNextElement, 64);
            switch (aSN1TaggedObject2.getTagNo()) {
                case 55:
                    this.signature = ASN1OctetString.getInstance(aSN1TaggedObject2.getBaseUniversal(false, 4)).getOctets();
                    this.valid |= signValid;
                    break;
                case 78:
                    this.certificateBody = CertificateBody.getInstance(aSN1TaggedObject2);
                    this.valid |= bodyValid;
                    break;
                default:
                    throw new IOException("Invalid tag, not an Iso7816CertificateStructure :" + aSN1TaggedObject2.getTagNo());
            }
        }
        if (this.valid != (signValid | bodyValid)) {
            throw new IOException("invalid CARDHOLDER_CERTIFICATE :" + aSN1TaggedObject.getTagNo());
        }
    }

    public CVCertificate(ASN1InputStream aSN1InputStream) throws IOException {
        initFrom(aSN1InputStream);
    }

    private void initFrom(ASN1InputStream aSN1InputStream) throws IOException {
        while (true) {
            ASN1Primitive object = aSN1InputStream.readObject();
            if (object == null) {
                return;
            }
            if (!(object instanceof ASN1TaggedObject)) {
                throw new IOException("Invalid Input Stream for creating an Iso7816CertificateStructure");
            }
            setPrivateData((ASN1TaggedObject) object);
        }
    }

    private CVCertificate(ASN1TaggedObject aSN1TaggedObject) throws IOException {
        setPrivateData(aSN1TaggedObject);
    }

    public CVCertificate(CertificateBody certificateBody, byte[] bArr) throws IOException {
        this.certificateBody = certificateBody;
        this.signature = Arrays.clone(bArr);
        this.valid |= bodyValid;
        this.valid |= signValid;
    }

    public static CVCertificate getInstance(Object obj) {
        if (obj instanceof CVCertificate) {
            return (CVCertificate) obj;
        }
        if (obj == null) {
            return null;
        }
        try {
            return new CVCertificate(ASN1TaggedObject.getInstance(obj, 64));
        } catch (IOException e) {
            throw new ASN1ParsingException("unable to parse data: " + e.getMessage(), e);
        }
    }

    public byte[] getSignature() {
        return Arrays.clone(this.signature);
    }

    public CertificateBody getBody() {
        return this.certificateBody;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return EACTagged.create(33, new DERSequence(this.certificateBody, EACTagged.create(55, this.signature)));
    }

    public ASN1ObjectIdentifier getHolderAuthorization() throws IOException {
        return this.certificateBody.getCertificateHolderAuthorization().getOid();
    }

    public PackedDate getEffectiveDate() throws IOException {
        return this.certificateBody.getCertificateEffectiveDate();
    }

    public int getCertificateType() {
        return this.certificateBody.getCertificateType();
    }

    public PackedDate getExpirationDate() throws IOException {
        return this.certificateBody.getCertificateExpirationDate();
    }

    public int getRole() throws IOException {
        return this.certificateBody.getCertificateHolderAuthorization().getAccessRights();
    }

    public CertificationAuthorityReference getAuthorityReference() throws IOException {
        return this.certificateBody.getCertificationAuthorityReference();
    }

    public CertificateHolderReference getHolderReference() throws IOException {
        return this.certificateBody.getCertificateHolderReference();
    }

    public int getHolderAuthorizationRole() throws IOException {
        return this.certificateBody.getCertificateHolderAuthorization().getAccessRights() & 192;
    }

    public Flags getHolderAuthorizationRights() throws IOException {
        return new Flags(this.certificateBody.getCertificateHolderAuthorization().getAccessRights() & 31);
    }
}
