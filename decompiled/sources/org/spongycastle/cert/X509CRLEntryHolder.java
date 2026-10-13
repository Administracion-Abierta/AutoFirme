package org.spongycastle.cert;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.Extensions;
import org.spongycastle.asn1.x509.GeneralNames;
import org.spongycastle.asn1.x509.TBSCertList;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/X509CRLEntryHolder.class */
public class X509CRLEntryHolder {
    private TBSCertList.CRLEntry entry;
    private GeneralNames ca;

    X509CRLEntryHolder(TBSCertList.CRLEntry entry, boolean isIndirect, GeneralNames previousCA) {
        Extension currentCaName;
        this.entry = entry;
        this.ca = previousCA;
        if (isIndirect && entry.hasExtensions() && (currentCaName = entry.getExtensions().getExtension(Extension.certificateIssuer)) != null) {
            this.ca = GeneralNames.getInstance(currentCaName.getParsedValue());
        }
    }

    public BigInteger getSerialNumber() {
        return this.entry.getUserCertificate().getValue();
    }

    public Date getRevocationDate() {
        return this.entry.getRevocationDate().getDate();
    }

    public boolean hasExtensions() {
        return this.entry.hasExtensions();
    }

    public GeneralNames getCertificateIssuer() {
        return this.ca;
    }

    public Extension getExtension(ASN1ObjectIdentifier oid) {
        Extensions extensions = this.entry.getExtensions();
        if (extensions != null) {
            return extensions.getExtension(oid);
        }
        return null;
    }

    public Extensions getExtensions() {
        return this.entry.getExtensions();
    }

    public List getExtensionOIDs() {
        return CertUtils.getExtensionOIDs(this.entry.getExtensions());
    }

    public Set getCriticalExtensionOIDs() {
        return CertUtils.getCriticalExtensionOIDs(this.entry.getExtensions());
    }

    public Set getNonCriticalExtensionOIDs() {
        return CertUtils.getNonCriticalExtensionOIDs(this.entry.getExtensions());
    }
}
