package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.jcajce.CompositePublicKey;
import org.bouncycastle.jcajce.io.OutputStreamFactory;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/x509/X509CRLImpl.class */
abstract class X509CRLImpl extends X509CRL {
    protected JcaJceHelper bcHelper;
    protected CertificateList c;
    protected String sigAlgName;
    protected byte[] sigAlgParams;
    protected boolean isIndirect;

    X509CRLImpl(JcaJceHelper jcaJceHelper, CertificateList certificateList, String str, byte[] bArr, boolean z) {
        this.bcHelper = jcaJceHelper;
        this.c = certificateList;
        this.sigAlgName = str;
        this.sigAlgParams = bArr;
        this.isIndirect = z;
    }

    @Override // java.security.cert.X509Extension
    public boolean hasUnsupportedCriticalExtension() {
        Set criticalExtensionOIDs = getCriticalExtensionOIDs();
        if (criticalExtensionOIDs == null) {
            return false;
        }
        criticalExtensionOIDs.remove(Extension.issuingDistributionPoint.getId());
        criticalExtensionOIDs.remove(Extension.deltaCRLIndicator.getId());
        return !criticalExtensionOIDs.isEmpty();
    }

    private Set getExtensionOIDs(boolean z) {
        Extensions extensions;
        if (getVersion() != 2 || (extensions = this.c.getTBSCertList().getExtensions()) == null) {
            return null;
        }
        HashSet hashSet = new HashSet();
        Enumeration enumerationOids = extensions.oids();
        while (enumerationOids.hasMoreElements()) {
            ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier) enumerationOids.nextElement();
            if (z == extensions.getExtension(aSN1ObjectIdentifier).isCritical()) {
                hashSet.add(aSN1ObjectIdentifier.getId());
            }
        }
        return hashSet;
    }

    @Override // java.security.cert.X509Extension
    public Set getCriticalExtensionOIDs() {
        return getExtensionOIDs(true);
    }

    @Override // java.security.cert.X509Extension
    public Set getNonCriticalExtensionOIDs() {
        return getExtensionOIDs(false);
    }

    @Override // java.security.cert.X509Extension
    public byte[] getExtensionValue(String str) {
        ASN1ObjectIdentifier aSN1ObjectIdentifierTryFromID;
        ASN1OctetString extensionValue;
        if (str == null || (aSN1ObjectIdentifierTryFromID = ASN1ObjectIdentifier.tryFromID(str)) == null || null == (extensionValue = getExtensionValue(this.c, aSN1ObjectIdentifierTryFromID))) {
            return null;
        }
        try {
            return extensionValue.getEncoded();
        } catch (Exception e) {
            throw Exceptions.illegalStateException("error parsing " + e.getMessage(), e);
        }
    }

    @Override // java.security.cert.X509CRL
    public void verify(PublicKey publicKey) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CRLException, NoSuchProviderException {
        doVerify(publicKey, new SignatureCreator() { // from class: org.bouncycastle.jcajce.provider.asymmetric.x509.X509CRLImpl.1
            @Override // org.bouncycastle.jcajce.provider.asymmetric.x509.SignatureCreator
            public Signature createSignature(String str) throws NoSuchAlgorithmException, NoSuchProviderException {
                try {
                    return X509CRLImpl.this.bcHelper.createSignature(str);
                } catch (Exception e) {
                    return Signature.getInstance(str);
                }
            }
        });
    }

    @Override // java.security.cert.X509CRL
    public void verify(PublicKey publicKey, final String str) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CRLException, NoSuchProviderException {
        doVerify(publicKey, new SignatureCreator() { // from class: org.bouncycastle.jcajce.provider.asymmetric.x509.X509CRLImpl.2
            @Override // org.bouncycastle.jcajce.provider.asymmetric.x509.SignatureCreator
            public Signature createSignature(String str2) throws NoSuchAlgorithmException, NoSuchProviderException {
                return str != null ? Signature.getInstance(str2, str) : Signature.getInstance(str2);
            }
        });
    }

    @Override // java.security.cert.X509CRL
    public void verify(PublicKey publicKey, final Provider provider) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CRLException {
        try {
            doVerify(publicKey, new SignatureCreator() { // from class: org.bouncycastle.jcajce.provider.asymmetric.x509.X509CRLImpl.3
                @Override // org.bouncycastle.jcajce.provider.asymmetric.x509.SignatureCreator
                public Signature createSignature(String str) throws NoSuchAlgorithmException, NoSuchProviderException {
                    return provider != null ? Signature.getInstance(X509CRLImpl.this.getSigAlgName(), provider) : Signature.getInstance(X509CRLImpl.this.getSigAlgName());
                }
            });
        } catch (NoSuchProviderException e) {
            throw new NoSuchAlgorithmException("provider issue: " + e.getMessage());
        }
    }

    private void doVerify(PublicKey publicKey, SignatureCreator signatureCreator) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CRLException, NoSuchProviderException {
        if (!this.c.getSignatureAlgorithm().equals(this.c.getTBSCertList().getSignature())) {
            throw new CRLException("Signature algorithm on CertificateList does not match TBSCertList.");
        }
        if ((publicKey instanceof CompositePublicKey) && X509SignatureUtil.isCompositeAlgorithm(this.c.getSignatureAlgorithm())) {
            List<PublicKey> publicKeys = ((CompositePublicKey) publicKey).getPublicKeys();
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(this.c.getSignatureAlgorithm().getParameters());
            ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(this.c.getSignature().getOctets());
            boolean z = false;
            for (int i = 0; i != publicKeys.size(); i++) {
                if (publicKeys.get(i) != null) {
                    AlgorithmIdentifier algorithmIdentifier = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(i));
                    SignatureException signatureException = null;
                    try {
                        checkSignature(publicKeys.get(i), signatureCreator.createSignature(X509SignatureUtil.getSignatureName(algorithmIdentifier)), algorithmIdentifier.getParameters(), ASN1BitString.getInstance(aSN1Sequence2.getObjectAt(i)).getOctets());
                        z = true;
                    } catch (SignatureException e) {
                        signatureException = e;
                    }
                    if (signatureException != null) {
                        throw signatureException;
                    }
                }
            }
            if (!z) {
                throw new InvalidKeyException("no matching key found");
            }
            return;
        }
        if (!X509SignatureUtil.isCompositeAlgorithm(this.c.getSignatureAlgorithm())) {
            Signature signatureCreateSignature = signatureCreator.createSignature(getSigAlgName());
            if (this.sigAlgParams == null) {
                checkSignature(publicKey, signatureCreateSignature, null, getSignature());
                return;
            }
            try {
                checkSignature(publicKey, signatureCreateSignature, ASN1Primitive.fromByteArray(this.sigAlgParams), getSignature());
                return;
            } catch (IOException e2) {
                throw new SignatureException("cannot decode signature parameters: " + e2.getMessage());
            }
        }
        ASN1Sequence aSN1Sequence3 = ASN1Sequence.getInstance(this.c.getSignatureAlgorithm().getParameters());
        ASN1Sequence aSN1Sequence4 = ASN1Sequence.getInstance(this.c.getSignature().getOctets());
        boolean z2 = false;
        for (int i2 = 0; i2 != aSN1Sequence4.size(); i2++) {
            AlgorithmIdentifier algorithmIdentifier2 = AlgorithmIdentifier.getInstance(aSN1Sequence3.getObjectAt(i2));
            SignatureException signatureException2 = null;
            try {
                checkSignature(publicKey, signatureCreator.createSignature(X509SignatureUtil.getSignatureName(algorithmIdentifier2)), algorithmIdentifier2.getParameters(), ASN1BitString.getInstance(aSN1Sequence4.getObjectAt(i2)).getOctets());
                z2 = true;
            } catch (InvalidKeyException e3) {
            } catch (NoSuchAlgorithmException e4) {
            } catch (SignatureException e5) {
                signatureException2 = e5;
            }
            if (signatureException2 != null) {
                throw signatureException2;
            }
        }
        if (!z2) {
            throw new InvalidKeyException("no matching key found");
        }
    }

    private void checkSignature(PublicKey publicKey, Signature signature, ASN1Encodable aSN1Encodable, byte[] bArr) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CRLException {
        if (!X509SignatureUtil.areEquivalentAlgorithms(this.c.getSignatureAlgorithm(), this.c.getTBSCertList().getSignature())) {
            throw new CRLException("Signature algorithm on CertificateList does not match TbsCertList.");
        }
        X509SignatureUtil.setSignatureParameters(signature, aSN1Encodable);
        signature.initVerify(publicKey);
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(OutputStreamFactory.createStream(signature), 512);
            this.c.getTBSCertList().encodeTo(bufferedOutputStream, "DER");
            bufferedOutputStream.close();
            if (!signature.verify(bArr)) {
                throw new SignatureException("CRL does not verify with supplied public key.");
            }
        } catch (IOException e) {
            throw new CRLException(e.toString());
        }
    }

    @Override // java.security.cert.X509CRL
    public int getVersion() {
        return this.c.getVersionNumber();
    }

    @Override // java.security.cert.X509CRL
    public Principal getIssuerDN() {
        return new X509Principal(X500Name.getInstance(this.c.getIssuer().toASN1Primitive()));
    }

    @Override // java.security.cert.X509CRL
    public X500Principal getIssuerX500Principal() {
        try {
            return new X500Principal(this.c.getIssuer().getEncoded());
        } catch (IOException e) {
            throw new IllegalStateException("can't encode issuer DN");
        }
    }

    @Override // java.security.cert.X509CRL
    public Date getThisUpdate() {
        return this.c.getThisUpdate().getDate();
    }

    @Override // java.security.cert.X509CRL
    public Date getNextUpdate() {
        Time nextUpdate = this.c.getNextUpdate();
        if (null == nextUpdate) {
            return null;
        }
        return nextUpdate.getDate();
    }

    private Set loadCRLEntries() {
        Extension extension;
        HashSet hashSet = new HashSet();
        Enumeration revokedCertificateEnumeration = this.c.getRevokedCertificateEnumeration();
        X500Name x500Name = null;
        while (revokedCertificateEnumeration.hasMoreElements()) {
            TBSCertList.CRLEntry cRLEntry = (TBSCertList.CRLEntry) revokedCertificateEnumeration.nextElement();
            hashSet.add(new X509CRLEntryObject(cRLEntry, this.isIndirect, x500Name));
            if (this.isIndirect && cRLEntry.hasExtensions() && (extension = cRLEntry.getExtensions().getExtension(Extension.certificateIssuer)) != null) {
                x500Name = X500Name.getInstance(GeneralNames.getInstance(extension.getParsedValue()).getNames()[0].getName());
            }
        }
        return hashSet;
    }

    @Override // java.security.cert.X509CRL
    public X509CRLEntry getRevokedCertificate(BigInteger bigInteger) {
        Extension extension;
        Enumeration revokedCertificateEnumeration = this.c.getRevokedCertificateEnumeration();
        X500Name x500Name = null;
        while (revokedCertificateEnumeration.hasMoreElements()) {
            TBSCertList.CRLEntry cRLEntry = (TBSCertList.CRLEntry) revokedCertificateEnumeration.nextElement();
            if (cRLEntry.getUserCertificate().hasValue(bigInteger)) {
                return new X509CRLEntryObject(cRLEntry, this.isIndirect, x500Name);
            }
            if (this.isIndirect && cRLEntry.hasExtensions() && (extension = cRLEntry.getExtensions().getExtension(Extension.certificateIssuer)) != null) {
                x500Name = X500Name.getInstance(GeneralNames.getInstance(extension.getParsedValue()).getNames()[0].getName());
            }
        }
        return null;
    }

    @Override // java.security.cert.X509CRL
    public Set getRevokedCertificates() {
        Set setLoadCRLEntries = loadCRLEntries();
        if (setLoadCRLEntries.isEmpty()) {
            return null;
        }
        return Collections.unmodifiableSet(setLoadCRLEntries);
    }

    @Override // java.security.cert.X509CRL
    public byte[] getTBSCertList() throws CRLException {
        try {
            return this.c.getTBSCertList().getEncoded("DER");
        } catch (IOException e) {
            throw new CRLException(e.toString());
        }
    }

    @Override // java.security.cert.X509CRL
    public byte[] getSignature() {
        return this.c.getSignature().getOctets();
    }

    @Override // java.security.cert.X509CRL
    public String getSigAlgName() {
        return this.sigAlgName;
    }

    @Override // java.security.cert.X509CRL
    public String getSigAlgOID() {
        return this.c.getSignatureAlgorithm().getAlgorithm().getId();
    }

    @Override // java.security.cert.X509CRL
    public byte[] getSigAlgParams() {
        return Arrays.clone(this.sigAlgParams);
    }

    @Override // java.security.cert.CRL
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        String strLineSeparator = Strings.lineSeparator();
        stringBuffer.append("              Version: ").append(getVersion()).append(strLineSeparator);
        stringBuffer.append("             IssuerDN: ").append(getIssuerDN()).append(strLineSeparator);
        stringBuffer.append("          This update: ").append(getThisUpdate()).append(strLineSeparator);
        stringBuffer.append("          Next update: ").append(getNextUpdate()).append(strLineSeparator);
        stringBuffer.append("  Signature Algorithm: ").append(getSigAlgName()).append(strLineSeparator);
        X509SignatureUtil.prettyPrintSignature(getSignature(), stringBuffer, strLineSeparator);
        Extensions extensions = this.c.getTBSCertList().getExtensions();
        if (extensions != null) {
            Enumeration enumerationOids = extensions.oids();
            if (enumerationOids.hasMoreElements()) {
                stringBuffer.append("           Extensions: ").append(strLineSeparator);
            }
            while (enumerationOids.hasMoreElements()) {
                ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier) enumerationOids.nextElement();
                Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
                if (extension.getExtnValue() != null) {
                    ASN1InputStream aSN1InputStream = new ASN1InputStream(extension.getExtnValue().getOctets());
                    stringBuffer.append("                       critical(").append(extension.isCritical()).append(") ");
                    try {
                        if (aSN1ObjectIdentifier.equals((ASN1Primitive) Extension.cRLNumber)) {
                            stringBuffer.append(new CRLNumber(ASN1Integer.getInstance(aSN1InputStream.readObject()).getPositiveValue())).append(strLineSeparator);
                        } else if (aSN1ObjectIdentifier.equals((ASN1Primitive) Extension.deltaCRLIndicator)) {
                            stringBuffer.append("Base CRL: " + new CRLNumber(ASN1Integer.getInstance(aSN1InputStream.readObject()).getPositiveValue())).append(strLineSeparator);
                        } else if (aSN1ObjectIdentifier.equals((ASN1Primitive) Extension.issuingDistributionPoint)) {
                            stringBuffer.append(IssuingDistributionPoint.getInstance(aSN1InputStream.readObject())).append(strLineSeparator);
                        } else if (aSN1ObjectIdentifier.equals((ASN1Primitive) Extension.cRLDistributionPoints) || aSN1ObjectIdentifier.equals((ASN1Primitive) Extension.freshestCRL)) {
                            stringBuffer.append(CRLDistPoint.getInstance(aSN1InputStream.readObject())).append(strLineSeparator);
                        } else {
                            stringBuffer.append(aSN1ObjectIdentifier.getId());
                            stringBuffer.append(" value = ").append(ASN1Dump.dumpAsString(aSN1InputStream.readObject())).append(strLineSeparator);
                        }
                    } catch (Exception e) {
                        stringBuffer.append(aSN1ObjectIdentifier.getId());
                        stringBuffer.append(" value = ").append("*****").append(strLineSeparator);
                    }
                } else {
                    stringBuffer.append(strLineSeparator);
                }
            }
        }
        Set revokedCertificates = getRevokedCertificates();
        if (revokedCertificates != null) {
            Iterator it = revokedCertificates.iterator();
            while (it.hasNext()) {
                stringBuffer.append(it.next());
                stringBuffer.append(strLineSeparator);
            }
        }
        return stringBuffer.toString();
    }

    @Override // java.security.cert.CRL
    public boolean isRevoked(Certificate certificate) {
        X500Name issuer;
        Extension extension;
        if (!certificate.getType().equals(XMLX509Certificate.JCA_CERT_ID)) {
            throw new IllegalArgumentException("X.509 CRL used with non X.509 Cert");
        }
        Enumeration revokedCertificateEnumeration = this.c.getRevokedCertificateEnumeration();
        X500Name issuer2 = this.c.getIssuer();
        if (!revokedCertificateEnumeration.hasMoreElements()) {
            return false;
        }
        BigInteger serialNumber = ((X509Certificate) certificate).getSerialNumber();
        while (revokedCertificateEnumeration.hasMoreElements()) {
            TBSCertList.CRLEntry cRLEntry = TBSCertList.CRLEntry.getInstance(revokedCertificateEnumeration.nextElement());
            if (this.isIndirect && cRLEntry.hasExtensions() && (extension = cRLEntry.getExtensions().getExtension(Extension.certificateIssuer)) != null) {
                issuer2 = X500Name.getInstance(GeneralNames.getInstance(extension.getParsedValue()).getNames()[0].getName());
            }
            if (cRLEntry.getUserCertificate().hasValue(serialNumber)) {
                if (certificate instanceof X509Certificate) {
                    issuer = X500Name.getInstance(((X509Certificate) certificate).getIssuerX500Principal().getEncoded());
                } else {
                    try {
                        issuer = org.bouncycastle.asn1.x509.Certificate.getInstance(certificate.getEncoded()).getIssuer();
                    } catch (CertificateEncodingException e) {
                        throw new IllegalArgumentException("Cannot process certificate: " + e.getMessage());
                    }
                }
                return issuer2.equals(issuer);
            }
        }
        return false;
    }

    static byte[] getExtensionOctets(CertificateList certificateList, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        ASN1OctetString extensionValue = getExtensionValue(certificateList, aSN1ObjectIdentifier);
        if (null != extensionValue) {
            return extensionValue.getOctets();
        }
        return null;
    }

    static ASN1OctetString getExtensionValue(CertificateList certificateList, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        Extension extension;
        Extensions extensions = certificateList.getTBSCertList().getExtensions();
        if (null == extensions || null == (extension = extensions.getExtension(aSN1ObjectIdentifier))) {
            return null;
        }
        return extension.getExtnValue();
    }
}
