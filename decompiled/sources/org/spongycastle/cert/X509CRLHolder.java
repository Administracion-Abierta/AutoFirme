package org.spongycastle.cert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DEROutputStream;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.CertificateList;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.Extensions;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.asn1.x509.GeneralNames;
import org.spongycastle.asn1.x509.IssuingDistributionPoint;
import org.spongycastle.asn1.x509.TBSCertList;
import org.spongycastle.operator.ContentVerifier;
import org.spongycastle.operator.ContentVerifierProvider;
import org.spongycastle.util.Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/X509CRLHolder.class */
public class X509CRLHolder implements Encodable, Serializable {
    private static final long serialVersionUID = 20170722001L;
    private transient CertificateList x509CRL;
    private transient boolean isIndirect;
    private transient Extensions extensions;
    private transient GeneralNames issuerName;

    private static CertificateList parseStream(InputStream stream) throws IOException {
        try {
            ASN1Primitive obj = new ASN1InputStream(stream, true).readObject();
            if (obj == null) {
                throw new IOException("no content found");
            }
            return CertificateList.getInstance(obj);
        } catch (ClassCastException e) {
            throw new CertIOException("malformed data: " + e.getMessage(), e);
        } catch (IllegalArgumentException e2) {
            throw new CertIOException("malformed data: " + e2.getMessage(), e2);
        }
    }

    private static boolean isIndirectCRL(Extensions extensions) {
        Extension ext;
        return (extensions == null || (ext = extensions.getExtension(Extension.issuingDistributionPoint)) == null || !IssuingDistributionPoint.getInstance(ext.getParsedValue()).isIndirectCRL()) ? false : true;
    }

    public X509CRLHolder(byte[] crlEncoding) throws IOException {
        this(parseStream(new ByteArrayInputStream(crlEncoding)));
    }

    public X509CRLHolder(InputStream crlStream) throws IOException {
        this(parseStream(crlStream));
    }

    public X509CRLHolder(CertificateList x509CRL) {
        init(x509CRL);
    }

    private void init(CertificateList x509CRL) {
        this.x509CRL = x509CRL;
        this.extensions = x509CRL.getTBSCertList().getExtensions();
        this.isIndirect = isIndirectCRL(this.extensions);
        this.issuerName = new GeneralNames(new GeneralName(x509CRL.getIssuer()));
    }

    @Override // org.spongycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return this.x509CRL.getEncoded();
    }

    public X500Name getIssuer() {
        return X500Name.getInstance(this.x509CRL.getIssuer());
    }

    public X509CRLEntryHolder getRevokedCertificate(BigInteger serialNumber) {
        Extension currentCaName;
        GeneralNames currentCA = this.issuerName;
        Enumeration en = this.x509CRL.getRevokedCertificateEnumeration();
        while (en.hasMoreElements()) {
            TBSCertList.CRLEntry entry = (TBSCertList.CRLEntry) en.nextElement();
            if (entry.getUserCertificate().getValue().equals(serialNumber)) {
                return new X509CRLEntryHolder(entry, this.isIndirect, currentCA);
            }
            if (this.isIndirect && entry.hasExtensions() && (currentCaName = entry.getExtensions().getExtension(Extension.certificateIssuer)) != null) {
                currentCA = GeneralNames.getInstance(currentCaName.getParsedValue());
            }
        }
        return null;
    }

    public Collection getRevokedCertificates() {
        TBSCertList.CRLEntry[] entries = this.x509CRL.getRevokedCertificates();
        List l = new ArrayList(entries.length);
        GeneralNames currentCA = this.issuerName;
        Enumeration en = this.x509CRL.getRevokedCertificateEnumeration();
        while (en.hasMoreElements()) {
            TBSCertList.CRLEntry entry = (TBSCertList.CRLEntry) en.nextElement();
            X509CRLEntryHolder crlEntry = new X509CRLEntryHolder(entry, this.isIndirect, currentCA);
            l.add(crlEntry);
            currentCA = crlEntry.getCertificateIssuer();
        }
        return l;
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

    public Extensions getExtensions() {
        return this.extensions;
    }

    public List getExtensionOIDs() {
        return CertUtils.getExtensionOIDs(this.extensions);
    }

    public Set getCriticalExtensionOIDs() {
        return CertUtils.getCriticalExtensionOIDs(this.extensions);
    }

    public Set getNonCriticalExtensionOIDs() {
        return CertUtils.getNonCriticalExtensionOIDs(this.extensions);
    }

    public CertificateList toASN1Structure() {
        return this.x509CRL;
    }

    public boolean isSignatureValid(ContentVerifierProvider verifierProvider) throws CertException {
        TBSCertList tbsCRL = this.x509CRL.getTBSCertList();
        if (!CertUtils.isAlgIdEqual(tbsCRL.getSignature(), this.x509CRL.getSignatureAlgorithm())) {
            throw new CertException("signature invalid - algorithm identifier mismatch");
        }
        try {
            ContentVerifier verifier = verifierProvider.get(tbsCRL.getSignature());
            OutputStream sOut = verifier.getOutputStream();
            DEROutputStream dOut = new DEROutputStream(sOut);
            dOut.writeObject(tbsCRL);
            sOut.close();
            return verifier.verify(this.x509CRL.getSignature().getOctets());
        } catch (Exception e) {
            throw new CertException("unable to process signature: " + e.getMessage(), e);
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof X509CRLHolder)) {
            return false;
        }
        X509CRLHolder other = (X509CRLHolder) o;
        return this.x509CRL.equals(other.x509CRL);
    }

    public int hashCode() {
        return this.x509CRL.hashCode();
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        init(CertificateList.getInstance(in.readObject()));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(getEncoded());
    }
}
