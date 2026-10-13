package org.spongycastle.cert.cmp;

import java.math.BigInteger;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.cmp.RevDetails;
import org.spongycastle.asn1.crmf.CertTemplateBuilder;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/cmp/RevocationDetailsBuilder.class */
public class RevocationDetailsBuilder {
    private CertTemplateBuilder templateBuilder = new CertTemplateBuilder();

    public RevocationDetailsBuilder setPublicKey(SubjectPublicKeyInfo publicKey) {
        if (publicKey != null) {
            this.templateBuilder.setPublicKey(publicKey);
        }
        return this;
    }

    public RevocationDetailsBuilder setIssuer(X500Name issuer) {
        if (issuer != null) {
            this.templateBuilder.setIssuer(issuer);
        }
        return this;
    }

    public RevocationDetailsBuilder setSerialNumber(BigInteger serialNumber) {
        if (serialNumber != null) {
            this.templateBuilder.setSerialNumber(new ASN1Integer(serialNumber));
        }
        return this;
    }

    public RevocationDetailsBuilder setSubject(X500Name subject) {
        if (subject != null) {
            this.templateBuilder.setSubject(subject);
        }
        return this;
    }

    public RevocationDetails build() {
        return new RevocationDetails(new RevDetails(this.templateBuilder.build()));
    }
}
