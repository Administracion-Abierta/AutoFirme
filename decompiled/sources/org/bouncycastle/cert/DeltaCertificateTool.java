package org.bouncycastle.cert;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.DeltaCertificateDescriptor;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Validity;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/DeltaCertificateTool.class */
public class DeltaCertificateTool {
    public static Extension makeDeltaCertificateExtension(boolean z, Certificate certificate) throws IOException {
        return new Extension(Extension.deltaCertificateDescriptor, z, new DEROctetString(new DeltaCertificateDescriptor(certificate.getSerialNumber(), certificate.getSignatureAlgorithm(), certificate.getIssuer(), certificate.getValidity(), certificate.getSubject(), certificate.getSubjectPublicKeyInfo(), certificate.getExtensions(), certificate.getSignature()).getEncoded("DER")));
    }

    public static Extension makeDeltaCertificateExtension(boolean z, X509CertificateHolder x509CertificateHolder) throws IOException {
        return makeDeltaCertificateExtension(z, x509CertificateHolder.toASN1Structure());
    }

    public static Certificate extractDeltaCertificate(TBSCertificate tBSCertificate) {
        Extensions extensions = tBSCertificate.getExtensions();
        Extension extension = extensions.getExtension(Extension.deltaCertificateDescriptor);
        if (extension == null) {
            throw new IllegalStateException("no deltaCertificateDescriptor present");
        }
        DeltaCertificateDescriptor deltaCertificateDescriptor = DeltaCertificateDescriptor.getInstance(extension.getParsedValue());
        ASN1Integer version = tBSCertificate.getVersion();
        ASN1Integer serialNumber = deltaCertificateDescriptor.getSerialNumber();
        AlgorithmIdentifier signature = deltaCertificateDescriptor.getSignature();
        if (signature == null) {
            signature = tBSCertificate.getSignature();
        }
        X500Name issuer = deltaCertificateDescriptor.getIssuer();
        if (issuer == null) {
            issuer = tBSCertificate.getIssuer();
        }
        Validity validityObject = deltaCertificateDescriptor.getValidityObject();
        if (validityObject == null) {
            validityObject = tBSCertificate.getValidity();
        }
        X500Name subject = deltaCertificateDescriptor.getSubject();
        if (subject == null) {
            subject = tBSCertificate.getSubject();
        }
        return new Certificate(new TBSCertificate(version, serialNumber, signature, issuer, validityObject, subject, deltaCertificateDescriptor.getSubjectPublicKeyInfo(), null, null, extractDeltaExtensions(deltaCertificateDescriptor.getExtensions(), extensions)), signature, deltaCertificateDescriptor.getSignatureValue());
    }

    public static X509CertificateHolder extractDeltaCertificate(X509CertificateHolder x509CertificateHolder) {
        return new X509CertificateHolder(extractDeltaCertificate(x509CertificateHolder.getTBSCertificate()));
    }

    public static DeltaCertificateDescriptor trimDeltaCertificateDescriptor(DeltaCertificateDescriptor deltaCertificateDescriptor, TBSCertificate tBSCertificate, Extensions extensions) {
        return deltaCertificateDescriptor.trimTo(tBSCertificate, extensions);
    }

    private static Extensions extractDeltaExtensions(Extensions extensions, Extensions extensions2) {
        ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
        Enumeration enumerationOids = extensions2.oids();
        while (enumerationOids.hasMoreElements()) {
            ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier) enumerationOids.nextElement();
            if (!Extension.deltaCertificateDescriptor.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
                extensionsGenerator.addExtension(extensions2.getExtension(aSN1ObjectIdentifier));
            }
        }
        if (extensions != null) {
            Enumeration enumerationOids2 = extensions.oids();
            while (enumerationOids2.hasMoreElements()) {
                extensionsGenerator.replaceExtension(extensions.getExtension((ASN1ObjectIdentifier) enumerationOids2.nextElement()));
            }
        }
        if (extensionsGenerator.isEmpty()) {
            return null;
        }
        return extensionsGenerator.generate();
    }
}
