package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.Version;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/CtlFormat.class */
public class CtlFormat extends ASN1Object {
    private final Version version;
    private final Time32 nextUpdate;
    private final ASN1Boolean isFullCtl;
    private final UINT8 ctlSequence;
    private final SequenceOfCtlCommand ctlCommands;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/CtlFormat$Builder.class */
    public static class Builder {
        private Version version;
        private Time32 nextUpdate;
        private ASN1Boolean isFullCtl;
        private UINT8 ctlSequence;
        private SequenceOfCtlCommand ctlCommands;

        public Builder setVersion(Version version) {
            this.version = version;
            return this;
        }

        public Builder setNextUpdate(Time32 time32) {
            this.nextUpdate = time32;
            return this;
        }

        public Builder setIsFullCtl(ASN1Boolean aSN1Boolean) {
            this.isFullCtl = aSN1Boolean;
            return this;
        }

        public Builder setCtlSequence(UINT8 uint8) {
            this.ctlSequence = uint8;
            return this;
        }

        public Builder setCtlSequence(ASN1Integer aSN1Integer) {
            this.ctlSequence = new UINT8(aSN1Integer.getValue());
            return this;
        }

        public Builder setCtlCommands(SequenceOfCtlCommand sequenceOfCtlCommand) {
            this.ctlCommands = sequenceOfCtlCommand;
            return this;
        }

        public CtlFormat createCtlFormat() {
            return new CtlFormat(this.version, this.nextUpdate, this.isFullCtl, this.ctlSequence, this.ctlCommands);
        }

        public DeltaCtl createDeltaCtl() {
            if (this.isFullCtl == null || !ASN1Boolean.TRUE.equals((ASN1Primitive) this.isFullCtl)) {
                return new DeltaCtl(this.version, this.nextUpdate, this.ctlSequence, this.ctlCommands);
            }
            throw new IllegalArgumentException("isFullCtl must be false for DeltaCtl");
        }

        public FullCtl createFullCtl() {
            return new FullCtl(this.version, this.nextUpdate, this.isFullCtl, this.ctlSequence, this.ctlCommands);
        }

        public ToBeSignedRcaCtl createToBeSignedRcaCtl() {
            return new ToBeSignedRcaCtl(this.version, this.nextUpdate, this.isFullCtl, this.ctlSequence, this.ctlCommands);
        }
    }

    public CtlFormat(Version version, Time32 time32, ASN1Boolean aSN1Boolean, UINT8 uint8, SequenceOfCtlCommand sequenceOfCtlCommand) {
        this.version = version;
        this.nextUpdate = time32;
        this.isFullCtl = aSN1Boolean;
        this.ctlSequence = uint8;
        this.ctlCommands = sequenceOfCtlCommand;
    }

    protected CtlFormat(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 5) {
            throw new IllegalArgumentException("expected sequence size of 5");
        }
        this.version = Version.getInstance(aSN1Sequence.getObjectAt(0));
        this.nextUpdate = Time32.getInstance((Object) aSN1Sequence.getObjectAt(1));
        this.isFullCtl = ASN1Boolean.getInstance(aSN1Sequence.getObjectAt(2));
        this.ctlSequence = UINT8.getInstance(aSN1Sequence.getObjectAt(3));
        this.ctlCommands = SequenceOfCtlCommand.getInstance(aSN1Sequence.getObjectAt(4));
    }

    public static CtlFormat getInstance(Object obj) {
        if (obj instanceof CtlFormat) {
            return (CtlFormat) obj;
        }
        if (obj != null) {
            return new CtlFormat(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public Version getVersion() {
        return this.version;
    }

    public Time32 getNextUpdate() {
        return this.nextUpdate;
    }

    public ASN1Boolean getIsFullCtl() {
        return this.isFullCtl;
    }

    public UINT8 getCtlSequence() {
        return this.ctlSequence;
    }

    public SequenceOfCtlCommand getCtlCommands() {
        return this.ctlCommands;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.version, this.nextUpdate, this.isFullCtl, this.ctlSequence, this.ctlCommands});
    }

    public static Builder builder() {
        return new Builder();
    }
}
