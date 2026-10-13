package org.bouncycastle.cert;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.DeltaCertificateDescriptor;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.util.Exceptions;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/X509v3CertificateBuilder.class */
public class X509v3CertificateBuilder {
    private V3TBSCertificateGenerator tbsGen;
    private ExtensionsGenerator extGenerator;

    public X509v3CertificateBuilder(X500Name x500Name, BigInteger bigInteger, Date date, Date date2, X500Name x500Name2, SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this(x500Name, bigInteger, new Time(date), new Time(date2), x500Name2, subjectPublicKeyInfo);
    }

    public X509v3CertificateBuilder(X500Name x500Name, BigInteger bigInteger, Date date, Date date2, Locale locale, X500Name x500Name2, SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this(x500Name, bigInteger, new Time(date, locale), new Time(date2, locale), x500Name2, subjectPublicKeyInfo);
    }

    public X509v3CertificateBuilder(X500Name x500Name, BigInteger bigInteger, Time time, Time time2, X500Name x500Name2, SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this.tbsGen = new V3TBSCertificateGenerator();
        this.tbsGen.setSerialNumber(new ASN1Integer(bigInteger));
        this.tbsGen.setIssuer(x500Name);
        this.tbsGen.setStartDate(time);
        this.tbsGen.setEndDate(time2);
        this.tbsGen.setSubject(x500Name2);
        this.tbsGen.setSubjectPublicKeyInfo(subjectPublicKeyInfo);
        this.extGenerator = new ExtensionsGenerator();
    }

    public X509v3CertificateBuilder(X509CertificateHolder x509CertificateHolder) {
        this.tbsGen = new V3TBSCertificateGenerator();
        this.tbsGen.setSerialNumber(new ASN1Integer(x509CertificateHolder.getSerialNumber()));
        this.tbsGen.setIssuer(x509CertificateHolder.getIssuer());
        this.tbsGen.setStartDate(new Time(x509CertificateHolder.getNotBefore()));
        this.tbsGen.setEndDate(new Time(x509CertificateHolder.getNotAfter()));
        this.tbsGen.setSubject(x509CertificateHolder.getSubject());
        this.tbsGen.setSubjectPublicKeyInfo(x509CertificateHolder.getSubjectPublicKeyInfo());
        this.extGenerator = new ExtensionsGenerator();
        Extensions extensions = x509CertificateHolder.getExtensions();
        Enumeration enumerationOids = extensions.oids();
        while (enumerationOids.hasMoreElements()) {
            ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier) enumerationOids.nextElement();
            if (!Extension.subjectAltPublicKeyInfo.equals((ASN1Primitive) aSN1ObjectIdentifier) && !Extension.altSignatureAlgorithm.equals((ASN1Primitive) aSN1ObjectIdentifier) && !Extension.altSignatureValue.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
                this.extGenerator.addExtension(extensions.getExtension(aSN1ObjectIdentifier));
            }
        }
    }

    public boolean hasExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return doGetExtension(aSN1ObjectIdentifier) != null;
    }

    public Extension getExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return doGetExtension(aSN1ObjectIdentifier);
    }

    private Extension doGetExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (this.extGenerator.isEmpty()) {
            return null;
        }
        return this.extGenerator.generate().getExtension(aSN1ObjectIdentifier);
    }

    public X509v3CertificateBuilder setSubjectUniqueID(boolean[] zArr) {
        this.tbsGen.setSubjectUniqueID(booleanToBitString(zArr));
        return this;
    }

    public X509v3CertificateBuilder setIssuerUniqueID(boolean[] zArr) {
        this.tbsGen.setIssuerUniqueID(booleanToBitString(zArr));
        return this;
    }

    public X509v3CertificateBuilder addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean z, ASN1Encodable aSN1Encodable) throws CertIOException {
        try {
            this.extGenerator.addExtension(aSN1ObjectIdentifier, z, aSN1Encodable);
            return this;
        } catch (IOException e) {
            throw new CertIOException("cannot encode extension: " + e.getMessage(), e);
        }
    }

    public X509v3CertificateBuilder addExtension(Extension extension) throws CertIOException {
        this.extGenerator.addExtension(extension);
        return this;
    }

    public X509v3CertificateBuilder addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean z, byte[] bArr) throws CertIOException {
        this.extGenerator.addExtension(aSN1ObjectIdentifier, z, bArr);
        return this;
    }

    public X509v3CertificateBuilder replaceExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean z, ASN1Encodable aSN1Encodable) throws CertIOException {
        try {
            this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(aSN1ObjectIdentifier, z, new DEROctetString(aSN1Encodable)));
            return this;
        } catch (IOException e) {
            throw new CertIOException("cannot encode extension: " + e.getMessage(), e);
        }
    }

    public X509v3CertificateBuilder replaceExtension(Extension extension) throws CertIOException {
        this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, extension);
        return this;
    }

    public X509v3CertificateBuilder replaceExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean z, byte[] bArr) throws CertIOException {
        this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(aSN1ObjectIdentifier, z, bArr));
        return this;
    }

    public X509v3CertificateBuilder removeExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.extGenerator = CertUtils.doRemoveExtension(this.extGenerator, aSN1ObjectIdentifier);
        return this;
    }

    public X509v3CertificateBuilder copyAndAddExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean z, X509CertificateHolder x509CertificateHolder) {
        Extension extension = x509CertificateHolder.toASN1Structure().getTBSCertificate().getExtensions().getExtension(aSN1ObjectIdentifier);
        if (extension == null) {
            throw new NullPointerException("extension " + aSN1ObjectIdentifier + " not present");
        }
        this.extGenerator.addExtension(aSN1ObjectIdentifier, z, extension.getExtnValue().getOctets());
        return this;
    }

    public X509CertificateHolder build(ContentSigner contentSigner) {
        AlgorithmIdentifier algorithmIdentifier = contentSigner.getAlgorithmIdentifier();
        this.tbsGen.setSignature(algorithmIdentifier);
        if (!this.extGenerator.isEmpty()) {
            Extension extension = this.extGenerator.getExtension(Extension.deltaCertificateDescriptor);
            if (extension != null) {
                try {
                    this.extGenerator.replaceExtension(Extension.deltaCertificateDescriptor, extension.isCritical(), DeltaCertificateTool.trimDeltaCertificateDescriptor(DeltaCertificateDescriptor.getInstance(extension.getParsedValue()), this.tbsGen.generateTBSCertificate(), this.extGenerator.generate()));
                } catch (IOException e) {
                    throw new IllegalStateException("unable to replace deltaCertificateDescriptor: " + e.getMessage());
                }
            }
            this.tbsGen.setExtensions(this.extGenerator.generate());
        }
        try {
            TBSCertificate tBSCertificateGenerateTBSCertificate = this.tbsGen.generateTBSCertificate();
            return new X509CertificateHolder(generateStructure(tBSCertificateGenerateTBSCertificate, algorithmIdentifier, generateSig(contentSigner, tBSCertificateGenerateTBSCertificate)));
        } catch (IOException e2) {
            throw Exceptions.illegalArgumentException("cannot produce certificate signature", e2);
        }
    }

    public X509CertificateHolder build(ContentSigner contentSigner, boolean z, ContentSigner contentSigner2) {
        AlgorithmIdentifier algorithmIdentifier = contentSigner.getAlgorithmIdentifier();
        try {
            this.extGenerator.addExtension(Extension.altSignatureAlgorithm, z, contentSigner2.getAlgorithmIdentifier());
            Extension extension = this.extGenerator.getExtension(Extension.deltaCertificateDescriptor);
            if (extension != null) {
                this.tbsGen.setSignature(algorithmIdentifier);
                try {
                    ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
                    extensionsGenerator.addExtensions(this.extGenerator.generate());
                    extensionsGenerator.addExtension(Extension.altSignatureValue, false, (ASN1Encodable) DERNull.INSTANCE);
                    this.extGenerator.replaceExtension(Extension.deltaCertificateDescriptor, extension.isCritical(), DeltaCertificateTool.trimDeltaCertificateDescriptor(DeltaCertificateDescriptor.getInstance(extension.getParsedValue()), this.tbsGen.generateTBSCertificate(), extensionsGenerator.generate()));
                } catch (IOException e) {
                    throw new IllegalStateException("unable to replace deltaCertificateDescriptor: " + e.getMessage());
                }
            }
            this.tbsGen.setSignature(null);
            this.tbsGen.setExtensions(this.extGenerator.generate());
            try {
                this.extGenerator.addExtension(Extension.altSignatureValue, z, new DERBitString(generateSig(contentSigner2, this.tbsGen.generatePreTBSCertificate())));
                this.tbsGen.setSignature(algorithmIdentifier);
                this.tbsGen.setExtensions(this.extGenerator.generate());
                TBSCertificate tBSCertificateGenerateTBSCertificate = this.tbsGen.generateTBSCertificate();
                return new X509CertificateHolder(generateStructure(tBSCertificateGenerateTBSCertificate, algorithmIdentifier, generateSig(contentSigner, tBSCertificateGenerateTBSCertificate)));
            } catch (IOException e2) {
                throw Exceptions.illegalArgumentException("cannot produce certificate signature", e2);
            }
        } catch (IOException e3) {
            throw Exceptions.illegalStateException("cannot add altSignatureAlgorithm extension", e3);
        }
    }

    private static byte[] generateSig(ContentSigner contentSigner, ASN1Object aSN1Object) throws IOException {
        OutputStream outputStream = contentSigner.getOutputStream();
        aSN1Object.encodeTo(outputStream, "DER");
        outputStream.close();
        return contentSigner.getSignature();
    }

    private static Certificate generateStructure(TBSCertificate tBSCertificate, AlgorithmIdentifier algorithmIdentifier, byte[] bArr) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(tBSCertificate);
        aSN1EncodableVector.add(algorithmIdentifier);
        aSN1EncodableVector.add(new DERBitString(bArr));
        return Certificate.getInstance(new DERSequence(aSN1EncodableVector));
    }

    static DERBitString booleanToBitString(boolean[] zArr) {
        byte[] bArr = new byte[(zArr.length + 7) / 8];
        for (int i = 0; i != zArr.length; i++) {
            int i2 = i >>> 3;
            bArr[i2] = (byte) (bArr[i2] | (zArr[i] ? (byte) (128 >> (i & 7)) : (byte) 0));
        }
        return new DERBitString(bArr, (8 - zArr.length) & 7);
    }
}
