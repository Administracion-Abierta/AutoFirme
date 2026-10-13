package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/InnerAtResponse.class */
public class InnerAtResponse extends ASN1Object {
    private final ASN1OctetString requestHash;
    private final AuthorizationResponseCode responseCode;
    private final EtsiTs103097Certificate certificate;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/InnerAtResponse$Builder.class */
    public static class Builder {
        private ASN1OctetString requestHash;
        private AuthorizationResponseCode responseCode;
        private EtsiTs103097Certificate certificate;

        public Builder setRequestHash(ASN1OctetString aSN1OctetString) {
            this.requestHash = aSN1OctetString;
            return this;
        }

        public Builder setRequestHash(byte[] bArr) {
            this.requestHash = new DEROctetString(Arrays.clone(bArr));
            return this;
        }

        public Builder setResponseCode(AuthorizationResponseCode authorizationResponseCode) {
            this.responseCode = authorizationResponseCode;
            return this;
        }

        public Builder setCertificate(EtsiTs103097Certificate etsiTs103097Certificate) {
            this.certificate = etsiTs103097Certificate;
            return this;
        }

        public InnerAtResponse createInnerAtResponse() {
            return new InnerAtResponse(this.requestHash, this.responseCode, this.certificate);
        }
    }

    public InnerAtResponse(ASN1OctetString aSN1OctetString, AuthorizationResponseCode authorizationResponseCode, EtsiTs103097Certificate etsiTs103097Certificate) {
        this.requestHash = aSN1OctetString;
        this.responseCode = authorizationResponseCode;
        this.certificate = etsiTs103097Certificate;
    }

    private InnerAtResponse(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 3) {
            throw new IllegalArgumentException("expected sequence size of 3");
        }
        this.requestHash = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0));
        this.responseCode = AuthorizationResponseCode.getInstance((Object) aSN1Sequence.getObjectAt(1));
        this.certificate = (EtsiTs103097Certificate) OEROptional.getValue(EtsiTs103097Certificate.class, aSN1Sequence.getObjectAt(2));
    }

    public static InnerAtResponse getInstance(Object obj) {
        if (obj instanceof InnerAtResponse) {
            return (InnerAtResponse) obj;
        }
        if (obj != null) {
            return new InnerAtResponse(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1OctetString getRequestHash() {
        return this.requestHash;
    }

    public AuthorizationResponseCode getResponseCode() {
        return this.responseCode;
    }

    public EtsiTs103097Certificate getCertificate() {
        return this.certificate;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.requestHash, this.responseCode, OEROptional.getInstance(this.certificate)});
    }

    public static Builder builder() {
        return new Builder();
    }
}
