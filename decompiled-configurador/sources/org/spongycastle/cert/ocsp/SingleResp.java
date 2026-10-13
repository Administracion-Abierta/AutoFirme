package org.spongycastle.cert.ocsp;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ocsp.CertStatus;
import org.spongycastle.asn1.ocsp.RevokedInfo;
import org.spongycastle.asn1.ocsp.SingleResponse;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.Extensions;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/ocsp/SingleResp.class */
public class SingleResp {
    private SingleResponse resp;
    private Extensions extensions;

    public SingleResp(SingleResponse resp) {
        this.resp = resp;
        this.extensions = resp.getSingleExtensions();
    }

    public CertificateID getCertID() {
        return new CertificateID(this.resp.getCertID());
    }

    public CertificateStatus getCertStatus() {
        CertStatus s = this.resp.getCertStatus();
        if (s.getTagNo() == 0) {
            return null;
        }
        if (s.getTagNo() == 1) {
            return new RevokedStatus(RevokedInfo.getInstance(s.getStatus()));
        }
        return new UnknownStatus();
    }

    public Date getThisUpdate() {
        return OCSPUtils.extractDate(this.resp.getThisUpdate());
    }

    public Date getNextUpdate() {
        if (this.resp.getNextUpdate() == null) {
            return null;
        }
        return OCSPUtils.extractDate(this.resp.getNextUpdate());
    }

    public boolean hasExtensions() {
        return this.extensions != null;
    }

    public Extension getExtension(ASN1ObjectIdentifier oid) {
        if (this.extensions != null) {
            return this.extensions.getExtension(oid);
        }
        return null;
    }

    public List getExtensionOIDs() {
        return OCSPUtils.getExtensionOIDs(this.extensions);
    }

    public Set getCriticalExtensionOIDs() {
        return OCSPUtils.getCriticalExtensionOIDs(this.extensions);
    }

    public Set getNonCriticalExtensionOIDs() {
        return OCSPUtils.getNonCriticalExtensionOIDs(this.extensions);
    }
}
