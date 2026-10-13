package org.bouncycastle.cert;

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
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AltSignatureAlgorithm;
import org.bouncycastle.asn1.x509.AltSignatureValue;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.util.Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/X509CRLHolder.class */
public class X509CRLHolder implements Encodable, Serializable {
    private static final long serialVersionUID = 20170722001L;
    private transient CertificateList x509CRL;
    private transient boolean isIndirect;
    private transient Extensions extensions;
    private transient GeneralNames issuerName;

    private static CertificateList parseStream(InputStream inputStream) throws IOException {
        try {
            ASN1Primitive object = new ASN1InputStream(inputStream, true).readObject();
            if (object == null) {
                throw new IOException("no content found");
            }
            return CertificateList.getInstance(object);
        } catch (ClassCastException e) {
            throw new CertIOException("malformed data: " + e.getMessage(), e);
        } catch (IllegalArgumentException e2) {
            throw new CertIOException("malformed data: " + e2.getMessage(), e2);
        }
    }

    private static boolean isIndirectCRL(Extensions extensions) {
        Extension extension;
        return (extensions == null || (extension = extensions.getExtension(Extension.issuingDistributionPoint)) == null || !IssuingDistributionPoint.getInstance(extension.getParsedValue()).isIndirectCRL()) ? false : true;
    }

    public X509CRLHolder(byte[] bArr) throws IOException {
        this(parseStream(new ByteArrayInputStream(bArr)));
    }

    public X509CRLHolder(InputStream inputStream) throws IOException {
        this(parseStream(inputStream));
    }

    public X509CRLHolder(CertificateList certificateList) {
        init(certificateList);
    }

    private void init(CertificateList certificateList) {
        this.x509CRL = certificateList;
        this.extensions = certificateList.getTBSCertList().getExtensions();
        this.isIndirect = isIndirectCRL(this.extensions);
        this.issuerName = new GeneralNames(new GeneralName(certificateList.getIssuer()));
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return this.x509CRL.getEncoded();
    }

    public X500Name getIssuer() {
        return X500Name.getInstance(this.x509CRL.getIssuer());
    }

    public Date getThisUpdate() {
        return this.x509CRL.getThisUpdate().getDate();
    }

    public Date getNextUpdate() {
        Time nextUpdate = this.x509CRL.getNextUpdate();
        if (nextUpdate != null) {
            return nextUpdate.getDate();
        }
        return null;
    }

    public X509CRLEntryHolder getRevokedCertificate(BigInteger bigInteger) {
        Extension extension;
        GeneralNames generalNames = this.issuerName;
        Enumeration revokedCertificateEnumeration = this.x509CRL.getRevokedCertificateEnumeration();
        while (revokedCertificateEnumeration.hasMoreElements()) {
            TBSCertList.CRLEntry cRLEntry = (TBSCertList.CRLEntry) revokedCertificateEnumeration.nextElement();
            if (cRLEntry.getUserCertificate().hasValue(bigInteger)) {
                return new X509CRLEntryHolder(cRLEntry, this.isIndirect, generalNames);
            }
            if (this.isIndirect && cRLEntry.hasExtensions() && (extension = cRLEntry.getExtensions().getExtension(Extension.certificateIssuer)) != null) {
                generalNames = GeneralNames.getInstance(extension.getParsedValue());
            }
        }
        return null;
    }

    public Collection getRevokedCertificates() {
        ArrayList arrayList = new ArrayList(this.x509CRL.getRevokedCertificates().length);
        GeneralNames certificateIssuer = this.issuerName;
        Enumeration revokedCertificateEnumeration = this.x509CRL.getRevokedCertificateEnumeration();
        while (revokedCertificateEnumeration.hasMoreElements()) {
            X509CRLEntryHolder x509CRLEntryHolder = new X509CRLEntryHolder((TBSCertList.CRLEntry) revokedCertificateEnumeration.nextElement(), this.isIndirect, certificateIssuer);
            arrayList.add(x509CRLEntryHolder);
            certificateIssuer = x509CRLEntryHolder.getCertificateIssuer();
        }
        return arrayList;
    }

    public boolean hasExtensions() {
        return this.extensions != null;
    }

    public Extension getExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (this.extensions != null) {
            return this.extensions.getExtension(aSN1ObjectIdentifier);
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

    public boolean isSignatureValid(ContentVerifierProvider contentVerifierProvider) throws CertException {
        TBSCertList tBSCertList = this.x509CRL.getTBSCertList();
        if (!CertUtils.isAlgIdEqual(tBSCertList.getSignature(), this.x509CRL.getSignatureAlgorithm())) {
            throw new CertException("signature invalid - algorithm identifier mismatch");
        }
        try {
            ContentVerifier contentVerifier = contentVerifierProvider.get(tBSCertList.getSignature());
            OutputStream outputStream = contentVerifier.getOutputStream();
            tBSCertList.encodeTo(outputStream, "DER");
            outputStream.close();
            return contentVerifier.verify(this.x509CRL.getSignature().getOctets());
        } catch (Exception e) {
            throw new CertException("unable to process signature: " + e.getMessage(), e);
        }
    }

    public boolean isAlternativeSignatureValid(ContentVerifierProvider contentVerifierProvider) throws CertException {
        TBSCertList tBSCertList = this.x509CRL.getTBSCertList();
        AltSignatureAlgorithm altSignatureAlgorithmFromExtensions = AltSignatureAlgorithm.fromExtensions(tBSCertList.getExtensions());
        AltSignatureValue altSignatureValueFromExtensions = AltSignatureValue.fromExtensions(tBSCertList.getExtensions());
        try {
            ContentVerifier contentVerifier = contentVerifierProvider.get(AlgorithmIdentifier.getInstance(altSignatureAlgorithmFromExtensions.toASN1Primitive()));
            OutputStream outputStream = contentVerifier.getOutputStream();
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(tBSCertList.toASN1Primitive());
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            int i = 1;
            if (aSN1Sequence.getObjectAt(0) instanceof ASN1Integer) {
                aSN1EncodableVector.add(aSN1Sequence.getObjectAt(0));
                i = 1 + 1;
            }
            for (int i2 = i; i2 != aSN1Sequence.size() - 1; i2++) {
                aSN1EncodableVector.add(aSN1Sequence.getObjectAt(i2));
            }
            aSN1EncodableVector.add(CertUtils.trimExtensions(0, tBSCertList.getExtensions()));
            new DERSequence(aSN1EncodableVector).encodeTo(outputStream, "DER");
            outputStream.close();
            return contentVerifier.verify(altSignatureValueFromExtensions.getSignature().getOctets());
        } catch (Exception e) {
            throw new CertException("unable to process signature: " + e.getMessage(), e);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof X509CRLHolder) {
            return this.x509CRL.equals(((X509CRLHolder) obj).x509CRL);
        }
        return false;
    }

    public int hashCode() {
        return this.x509CRL.hashCode();
    }

    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        init(CertificateList.getInstance(objectInputStream.readObject()));
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(getEncoded());
    }
}
