package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.etsi102941.InnerEcRequestSignedForPop;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Data;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataEncrypted;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataEncryptedUnicast;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSigned;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSignedAndEncrypted;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSignedAndEncryptedUnicast;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSignedExternalPayload;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataUnsecured;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/Ieee1609Dot2Data.class */
public class Ieee1609Dot2Data extends ASN1Object {
    private final UINT8 protocolVersion;
    private final Ieee1609Dot2Content content;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/Ieee1609Dot2Data$Builder.class */
    public static class Builder {
        private UINT8 protocolVersion;
        private Ieee1609Dot2Content content;

        public Builder setProtocolVersion(UINT8 uint8) {
            this.protocolVersion = uint8;
            return this;
        }

        public Builder setContent(Ieee1609Dot2Content ieee1609Dot2Content) {
            this.content = ieee1609Dot2Content;
            return this;
        }

        public Ieee1609Dot2Data createIeee1609Dot2Data() {
            return new Ieee1609Dot2Data(this.protocolVersion, this.content);
        }

        public CounterSignature createCounterSignature() {
            return new CounterSignature(this.protocolVersion, this.content);
        }

        public EtsiTs103097Data createEtsiTs103097Data() {
            return new EtsiTs103097Data(this.protocolVersion, this.content);
        }

        public EtsiTs103097DataSigned createEtsiTs103097DataSigned() {
            return new EtsiTs103097DataSigned(this.content);
        }

        public EtsiTs103097DataSignedExternalPayload createEtsiTs103097DataSignedExternalPayload() {
            return new EtsiTs103097DataSignedExternalPayload(this.content);
        }

        public EtsiTs103097DataEncrypted createEtsiTs103097DataEncrypted() {
            return new EtsiTs103097DataEncrypted(this.content);
        }

        public EtsiTs103097DataSignedAndEncrypted createEtsiTs103097DataSignedAndEncrypted() {
            return new EtsiTs103097DataSignedAndEncrypted(this.content);
        }

        public EtsiTs103097DataEncryptedUnicast createEtsiTs103097DataEncryptedUnicast() {
            return new EtsiTs103097DataEncryptedUnicast(this.content);
        }

        public EtsiTs103097DataSignedAndEncryptedUnicast createEtsiTs103097DataSignedAndEncryptedUnicast() {
            return new EtsiTs103097DataSignedAndEncryptedUnicast(this.content);
        }

        public EtsiTs103097DataUnsecured createEtsiTs103097DataUnsecured() {
            return new EtsiTs103097DataUnsecured(this.content);
        }

        public InnerEcRequestSignedForPop createInnerEcRequestSignedForPop() {
            return new InnerEcRequestSignedForPop(this.content);
        }
    }

    public Ieee1609Dot2Data(UINT8 uint8, Ieee1609Dot2Content ieee1609Dot2Content) {
        this.protocolVersion = uint8;
        this.content = ieee1609Dot2Content;
    }

    protected Ieee1609Dot2Data(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        Iterator<ASN1Encodable> it = ASN1Sequence.getInstance(aSN1Sequence).iterator();
        this.protocolVersion = UINT8.getInstance(it.next());
        this.content = Ieee1609Dot2Content.getInstance(it.next());
    }

    public static Ieee1609Dot2Data getInstance(Object obj) {
        if (obj instanceof Ieee1609Dot2Data) {
            return (Ieee1609Dot2Data) obj;
        }
        if (obj != null) {
            return new Ieee1609Dot2Data(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.protocolVersion, this.content);
    }

    public UINT8 getProtocolVersion() {
        return this.protocolVersion;
    }

    public Ieee1609Dot2Content getContent() {
        return this.content;
    }

    public static Builder builder() {
        return new Builder();
    }
}
