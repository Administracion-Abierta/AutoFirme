package org.spongycastle.asn1.eac;

import java.io.IOException;
import java.util.Enumeration;
import org.spongycastle.asn1.ASN1ApplicationSpecific;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ParsingException;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERApplicationSpecific;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/eac/CVCertificateRequest.class */
public class CVCertificateRequest extends ASN1Object {
    private final ASN1ApplicationSpecific original;
    private CertificateBody certificateBody;
    private byte[] innerSignature = null;
    private byte[] outerSignature;
    private static final int bodyValid = 1;
    private static final int signValid = 2;

    private CVCertificateRequest(ASN1ApplicationSpecific request) throws IOException {
        this.outerSignature = null;
        this.original = request;
        if (request.isConstructed() && request.getApplicationTag() == 7) {
            ASN1Sequence seq = ASN1Sequence.getInstance(request.getObject(16));
            initCertBody(ASN1ApplicationSpecific.getInstance(seq.getObjectAt(0)));
            this.outerSignature = ASN1ApplicationSpecific.getInstance(seq.getObjectAt(seq.size() - 1)).getContents();
            return;
        }
        initCertBody(request);
    }

    private void initCertBody(ASN1ApplicationSpecific request) throws IOException {
        if (request.getApplicationTag() == 33) {
            int valid = 0;
            ASN1Sequence seq = ASN1Sequence.getInstance(request.getObject(16));
            Enumeration en = seq.getObjects();
            while (en.hasMoreElements()) {
                ASN1ApplicationSpecific obj = ASN1ApplicationSpecific.getInstance(en.nextElement());
                switch (obj.getApplicationTag()) {
                    case 55:
                        this.innerSignature = obj.getContents();
                        valid |= 2;
                        break;
                    case 78:
                        this.certificateBody = CertificateBody.getInstance(obj);
                        valid |= 1;
                        break;
                    default:
                        throw new IOException("Invalid tag, not an CV Certificate Request element:" + obj.getApplicationTag());
                }
            }
            if ((valid & 3) == 0) {
                throw new IOException("Invalid CARDHOLDER_CERTIFICATE in request:" + request.getApplicationTag());
            }
            return;
        }
        throw new IOException("not a CARDHOLDER_CERTIFICATE in request:" + request.getApplicationTag());
    }

    public static CVCertificateRequest getInstance(Object obj) {
        if (obj instanceof CVCertificateRequest) {
            return (CVCertificateRequest) obj;
        }
        if (obj != null) {
            try {
                return new CVCertificateRequest(ASN1ApplicationSpecific.getInstance(obj));
            } catch (IOException e) {
                throw new ASN1ParsingException("unable to parse data: " + e.getMessage(), e);
            }
        }
        return null;
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

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        if (this.original != null) {
            return this.original;
        }
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.certificateBody);
        try {
            v.add(new DERApplicationSpecific(false, 55, (ASN1Encodable) new DEROctetString(this.innerSignature)));
            return new DERApplicationSpecific(33, v);
        } catch (IOException e) {
            throw new IllegalStateException("unable to convert signature!");
        }
    }
}
