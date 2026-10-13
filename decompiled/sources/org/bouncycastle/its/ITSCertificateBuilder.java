package org.bouncycastle.its;

import org.bouncycastle.oer.its.ieee1609dot2.PsidGroupPermissions;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfPsidGroupPermissions;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CrlSeries;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PsidSsp;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfPsidSsp;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/ITSCertificateBuilder.class */
public class ITSCertificateBuilder {
    protected final ToBeSignedCertificate.Builder tbsCertificateBuilder;
    protected final ITSCertificate issuer;
    protected UINT8 version;
    protected HashedId3 cracaId;
    protected CrlSeries crlSeries;

    public ITSCertificateBuilder(ToBeSignedCertificate.Builder builder) {
        this(null, builder);
    }

    public ITSCertificateBuilder(ITSCertificate iTSCertificate, ToBeSignedCertificate.Builder builder) {
        this.version = new UINT8(3);
        this.cracaId = new HashedId3(new byte[3]);
        this.crlSeries = new CrlSeries(0);
        this.issuer = iTSCertificate;
        this.tbsCertificateBuilder = builder;
        this.tbsCertificateBuilder.setCracaId(this.cracaId);
        this.tbsCertificateBuilder.setCrlSeries(this.crlSeries);
    }

    public ITSCertificate getIssuer() {
        return this.issuer;
    }

    public ITSCertificateBuilder setVersion(int i) {
        this.version = new UINT8(i);
        return this;
    }

    public ITSCertificateBuilder setCracaId(byte[] bArr) {
        this.cracaId = new HashedId3(bArr);
        this.tbsCertificateBuilder.setCracaId(this.cracaId);
        return this;
    }

    public ITSCertificateBuilder setCrlSeries(int i) {
        this.crlSeries = new CrlSeries(i);
        this.tbsCertificateBuilder.setCrlSeries(this.crlSeries);
        return this;
    }

    public ITSCertificateBuilder setValidityPeriod(ITSValidityPeriod iTSValidityPeriod) {
        this.tbsCertificateBuilder.setValidityPeriod(iTSValidityPeriod.toASN1Structure());
        return this;
    }

    public ITSCertificateBuilder setCertIssuePermissions(PsidGroupPermissions... psidGroupPermissionsArr) {
        this.tbsCertificateBuilder.setCertIssuePermissions(SequenceOfPsidGroupPermissions.builder().addGroupPermission(psidGroupPermissionsArr).createSequenceOfPsidGroupPermissions());
        return this;
    }

    public ITSCertificateBuilder setAppPermissions(PsidSsp... psidSspArr) {
        SequenceOfPsidSsp.Builder builder = SequenceOfPsidSsp.builder();
        for (int i = 0; i != psidSspArr.length; i++) {
            builder.setItem(psidSspArr[i]);
        }
        this.tbsCertificateBuilder.setAppPermissions(builder.createSequenceOfPsidSsp());
        return this;
    }
}
