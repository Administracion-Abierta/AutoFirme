package org.spongycastle.pkix;

import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.SubjectKeyIdentifier;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cms.KeyTransRecipientId;
import org.spongycastle.cms.RecipientId;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pkix/PKIXIdentity.class */
public class PKIXIdentity {
    private final PrivateKeyInfo privateKeyInfo;
    private final X509CertificateHolder[] certificateHolders;

    public PKIXIdentity(PrivateKeyInfo privateKeyInfo, X509CertificateHolder[] certificateHolders) {
        this.privateKeyInfo = privateKeyInfo;
        this.certificateHolders = new X509CertificateHolder[certificateHolders.length];
        System.arraycopy(certificateHolders, 0, this.certificateHolders, 0, certificateHolders.length);
    }

    public PrivateKeyInfo getPrivateKeyInfo() {
        return this.privateKeyInfo;
    }

    public X509CertificateHolder getCertificate() {
        return this.certificateHolders[0];
    }

    public RecipientId getRecipientId() {
        return new KeyTransRecipientId(this.certificateHolders[0].getIssuer(), this.certificateHolders[0].getSerialNumber(), getSubjectKeyIdentifier());
    }

    private byte[] getSubjectKeyIdentifier() {
        SubjectKeyIdentifier subId = SubjectKeyIdentifier.fromExtensions(this.certificateHolders[0].getExtensions());
        if (subId == null) {
            return null;
        }
        return subId.getKeyIdentifier();
    }
}
