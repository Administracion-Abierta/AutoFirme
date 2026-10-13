package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Psid;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfHashedId3;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.ThreeDLocation;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time64;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/HeaderInfo.class */
public class HeaderInfo extends ASN1Object {
    private final Psid psid;
    private final Time64 generationTime;
    private final Time64 expiryTime;
    private final ThreeDLocation generationLocation;
    private final HashedId3 p2pcdLearningRequest;
    private final MissingCrlIdentifier missingCrlIdentifier;
    private final EncryptionKey encryptionKey;
    private final SequenceOfHashedId3 inlineP2pcdRequest;
    private final Certificate requestedCertificate;
    private final PduFunctionalType pduFunctionalType;
    private final ContributedExtensionBlocks contributedExtensions;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/HeaderInfo$Builder.class */
    public static class Builder {
        private Psid psid;
        private Time64 generationTime;
        private Time64 expiryTime;
        private ThreeDLocation generationLocation;
        private HashedId3 p2pcdLearningRequest;
        private MissingCrlIdentifier missingCrlIdentifier;
        private EncryptionKey encryptionKey;
        private SequenceOfHashedId3 inlineP2pcdRequest;
        private Certificate requestedCertificate;
        private PduFunctionalType pduFunctionalType;
        private ContributedExtensionBlocks contributedExtensions;

        public Builder setPsid(Psid psid) {
            this.psid = psid;
            return this;
        }

        public Builder setGenerationTime(Time64 time64) {
            this.generationTime = time64;
            return this;
        }

        public Builder setExpiryTime(Time64 time64) {
            this.expiryTime = time64;
            return this;
        }

        public Builder setGenerationLocation(ThreeDLocation threeDLocation) {
            this.generationLocation = threeDLocation;
            return this;
        }

        public Builder setP2pcdLearningRequest(HashedId3 hashedId3) {
            this.p2pcdLearningRequest = hashedId3;
            return this;
        }

        public Builder setEncryptionKey(EncryptionKey encryptionKey) {
            this.encryptionKey = encryptionKey;
            return this;
        }

        public Builder setMissingCrlIdentifier(MissingCrlIdentifier missingCrlIdentifier) {
            this.missingCrlIdentifier = missingCrlIdentifier;
            return this;
        }

        public Builder setInlineP2pcdRequest(SequenceOfHashedId3 sequenceOfHashedId3) {
            this.inlineP2pcdRequest = sequenceOfHashedId3;
            return this;
        }

        public Builder setRequestedCertificate(Certificate certificate) {
            this.requestedCertificate = certificate;
            return this;
        }

        public Builder setPduFunctionalType(PduFunctionalType pduFunctionalType) {
            this.pduFunctionalType = pduFunctionalType;
            return this;
        }

        public Builder setContributedExtensions(ContributedExtensionBlocks contributedExtensionBlocks) {
            this.contributedExtensions = contributedExtensionBlocks;
            return this;
        }

        public HeaderInfo createHeaderInfo() {
            return new HeaderInfo(this.psid, this.generationTime, this.expiryTime, this.generationLocation, this.p2pcdLearningRequest, this.missingCrlIdentifier, this.encryptionKey, this.inlineP2pcdRequest, this.requestedCertificate, this.pduFunctionalType, this.contributedExtensions);
        }
    }

    private HeaderInfo(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 11 && aSN1Sequence.size() != 7) {
            throw new IllegalArgumentException("expected sequence size of 11 or 7");
        }
        Iterator<ASN1Encodable> it = aSN1Sequence.iterator();
        this.psid = Psid.getInstance(it.next());
        this.generationTime = (Time64) OEROptional.getValue(Time64.class, it.next());
        this.expiryTime = (Time64) OEROptional.getValue(Time64.class, it.next());
        this.generationLocation = (ThreeDLocation) OEROptional.getValue(ThreeDLocation.class, it.next());
        this.p2pcdLearningRequest = (HashedId3) OEROptional.getValue(HashedId3.class, it.next());
        this.missingCrlIdentifier = (MissingCrlIdentifier) OEROptional.getValue(MissingCrlIdentifier.class, it.next());
        this.encryptionKey = (EncryptionKey) OEROptional.getValue(EncryptionKey.class, it.next());
        if (aSN1Sequence.size() > 7) {
            this.inlineP2pcdRequest = (SequenceOfHashedId3) OEROptional.getValue(SequenceOfHashedId3.class, it.next());
            this.requestedCertificate = (Certificate) OEROptional.getValue(Certificate.class, it.next());
            this.pduFunctionalType = (PduFunctionalType) OEROptional.getValue(PduFunctionalType.class, it.next());
            this.contributedExtensions = (ContributedExtensionBlocks) OEROptional.getValue(ContributedExtensionBlocks.class, it.next());
            return;
        }
        this.inlineP2pcdRequest = null;
        this.requestedCertificate = null;
        this.pduFunctionalType = null;
        this.contributedExtensions = null;
    }

    public HeaderInfo(Psid psid, Time64 time64, Time64 time65, ThreeDLocation threeDLocation, HashedId3 hashedId3, MissingCrlIdentifier missingCrlIdentifier, EncryptionKey encryptionKey, SequenceOfHashedId3 sequenceOfHashedId3, Certificate certificate, PduFunctionalType pduFunctionalType, ContributedExtensionBlocks contributedExtensionBlocks) {
        this.psid = psid;
        this.generationTime = time64;
        this.expiryTime = time65;
        this.generationLocation = threeDLocation;
        this.p2pcdLearningRequest = hashedId3;
        this.missingCrlIdentifier = missingCrlIdentifier;
        this.encryptionKey = encryptionKey;
        this.inlineP2pcdRequest = sequenceOfHashedId3;
        this.requestedCertificate = certificate;
        this.pduFunctionalType = pduFunctionalType;
        this.contributedExtensions = contributedExtensionBlocks;
    }

    public static HeaderInfo getInstance(Object obj) {
        if (obj instanceof HeaderInfo) {
            return (HeaderInfo) obj;
        }
        if (obj != null) {
            return new HeaderInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public Psid getPsid() {
        return this.psid;
    }

    public Time64 getGenerationTime() {
        return this.generationTime;
    }

    public Time64 getExpiryTime() {
        return this.expiryTime;
    }

    public ThreeDLocation getGenerationLocation() {
        return this.generationLocation;
    }

    public HashedId3 getP2pcdLearningRequest() {
        return this.p2pcdLearningRequest;
    }

    public MissingCrlIdentifier getMissingCrlIdentifier() {
        return this.missingCrlIdentifier;
    }

    public EncryptionKey getEncryptionKey() {
        return this.encryptionKey;
    }

    public SequenceOfHashedId3 getInlineP2pcdRequest() {
        return this.inlineP2pcdRequest;
    }

    public Certificate getRequestedCertificate() {
        return this.requestedCertificate;
    }

    public PduFunctionalType getPduFunctionalType() {
        return this.pduFunctionalType;
    }

    public ContributedExtensionBlocks getContributedExtensions() {
        return this.contributedExtensions;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.psid, OEROptional.getInstance(this.generationTime), OEROptional.getInstance(this.expiryTime), OEROptional.getInstance(this.generationLocation), OEROptional.getInstance(this.p2pcdLearningRequest), OEROptional.getInstance(this.missingCrlIdentifier), OEROptional.getInstance(this.encryptionKey), OEROptional.getInstance(this.inlineP2pcdRequest), OEROptional.getInstance(this.requestedCertificate), OEROptional.getInstance(this.pduFunctionalType), OEROptional.getInstance(this.contributedExtensions)});
    }

    public static Builder builder() {
        return new Builder();
    }
}
