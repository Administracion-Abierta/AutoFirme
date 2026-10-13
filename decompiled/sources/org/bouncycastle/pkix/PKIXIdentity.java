package org.bouncycastle.pkix;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.RecipientId;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkix/PKIXIdentity.class */
public class PKIXIdentity {
    private final PrivateKeyInfo privateKeyInfo;
    private final X509CertificateHolder[] certificateHolders;

    public PKIXIdentity(PrivateKeyInfo privateKeyInfo, X509CertificateHolder[] x509CertificateHolderArr) {
        this.privateKeyInfo = privateKeyInfo;
        this.certificateHolders = new X509CertificateHolder[x509CertificateHolderArr.length];
        System.arraycopy(x509CertificateHolderArr, 0, this.certificateHolders, 0, x509CertificateHolderArr.length);
    }

    public PKIXIdentity(PrivateKeyInfo privateKeyInfo, X509CertificateHolder x509CertificateHolder) {
        this(privateKeyInfo, new X509CertificateHolder[]{x509CertificateHolder});
    }

    public PrivateKeyInfo getPrivateKeyInfo() {
        return this.privateKeyInfo;
    }

    public X509CertificateHolder getCertificate() {
        return this.certificateHolders[0];
    }

    public X509CertificateHolder[] getCertificateChain() {
        X509CertificateHolder[] x509CertificateHolderArr = new X509CertificateHolder[this.certificateHolders.length];
        System.arraycopy(this.certificateHolders, 0, x509CertificateHolderArr, 0, x509CertificateHolderArr.length);
        return x509CertificateHolderArr;
    }

    public RecipientId getRecipientId() {
        return new KeyTransRecipientId(this.certificateHolders[0].getIssuer(), this.certificateHolders[0].getSerialNumber(), getSubjectKeyIdentifier());
    }

    private byte[] getSubjectKeyIdentifier() {
        SubjectKeyIdentifier subjectKeyIdentifierFromExtensions = SubjectKeyIdentifier.fromExtensions(this.certificateHolders[0].getExtensions());
        if (subjectKeyIdentifierFromExtensions == null) {
            return null;
        }
        return subjectKeyIdentifierFromExtensions.getKeyIdentifier();
    }
}
