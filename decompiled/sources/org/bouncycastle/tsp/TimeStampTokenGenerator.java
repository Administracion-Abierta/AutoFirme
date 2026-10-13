package org.bouncycastle.tsp;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.LocaleUtil;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ess.SigningCertificate;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.Accuracy;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSAttributeTableGenerationException;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/TimeStampTokenGenerator.class */
public class TimeStampTokenGenerator {
    public static final int R_SECONDS = 0;
    public static final int R_TENTHS_OF_SECONDS = 1;
    public static final int R_HUNDREDTHS_OF_SECONDS = 2;
    public static final int R_MICROSECONDS = 2;
    public static final int R_MILLISECONDS = 3;
    private int resolution;
    private Locale locale;
    private int accuracySeconds;
    private int accuracyMillis;
    private int accuracyMicros;
    boolean ordering;
    GeneralName tsa;
    private ASN1ObjectIdentifier tsaPolicyOID;
    private List certs;
    private List crls;
    private List attrCerts;
    private Map otherRevoc;
    private SignerInfoGenerator signerInfoGen;

    public TimeStampTokenGenerator(SignerInfoGenerator signerInfoGenerator, DigestCalculator digestCalculator, ASN1ObjectIdentifier aSN1ObjectIdentifier) throws TSPException, IllegalArgumentException {
        this(signerInfoGenerator, digestCalculator, aSN1ObjectIdentifier, false);
    }

    public TimeStampTokenGenerator(final SignerInfoGenerator signerInfoGenerator, DigestCalculator digestCalculator, ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean z) throws TSPException, IllegalArgumentException {
        this.resolution = 0;
        this.locale = null;
        this.accuracySeconds = -1;
        this.accuracyMillis = -1;
        this.accuracyMicros = -1;
        this.ordering = false;
        this.tsa = null;
        this.certs = new ArrayList();
        this.crls = new ArrayList();
        this.attrCerts = new ArrayList();
        this.otherRevoc = new HashMap();
        this.signerInfoGen = signerInfoGenerator;
        this.tsaPolicyOID = aSN1ObjectIdentifier;
        if (!signerInfoGenerator.hasAssociatedCertificate()) {
            throw new IllegalArgumentException("SignerInfoGenerator must have an associated certificate");
        }
        X509CertificateHolder associatedCertificate = signerInfoGenerator.getAssociatedCertificate();
        TSPUtil.validateCertificate(associatedCertificate);
        ASN1ObjectIdentifier algorithm = digestCalculator.getAlgorithmIdentifier().getAlgorithm();
        try {
            OutputStream outputStream = digestCalculator.getOutputStream();
            outputStream.write(associatedCertificate.getEncoded());
            outputStream.close();
            DEROctetString dEROctetString = new DEROctetString(digestCalculator.getDigest());
            IssuerSerial issuerSerial = z ? new IssuerSerial(new GeneralNames(new GeneralName(associatedCertificate.getIssuer())), associatedCertificate.toASN1Structure().getSerialNumber()) : null;
            if (OIWObjectIdentifiers.idSHA1.equals((ASN1Primitive) algorithm)) {
                final ESSCertID eSSCertID = new ESSCertID(dEROctetString, issuerSerial);
                this.signerInfoGen = new SignerInfoGenerator(signerInfoGenerator, new CMSAttributeTableGenerator() { // from class: org.bouncycastle.tsp.TimeStampTokenGenerator.1
                    @Override // org.bouncycastle.cms.CMSAttributeTableGenerator
                    public AttributeTable getAttributes(Map map) throws CMSAttributeTableGenerationException {
                        AttributeTable attributes = signerInfoGenerator.getSignedAttributeTableGenerator().getAttributes(map);
                        return attributes.get(PKCSObjectIdentifiers.id_aa_signingCertificate) == null ? attributes.add(PKCSObjectIdentifiers.id_aa_signingCertificate, new SigningCertificate(eSSCertID)) : attributes;
                    }
                }, signerInfoGenerator.getUnsignedAttributeTableGenerator());
            } else {
                final ESSCertIDv2 eSSCertIDv2 = new ESSCertIDv2(new AlgorithmIdentifier(algorithm), dEROctetString, issuerSerial);
                this.signerInfoGen = new SignerInfoGenerator(signerInfoGenerator, new CMSAttributeTableGenerator() { // from class: org.bouncycastle.tsp.TimeStampTokenGenerator.2
                    @Override // org.bouncycastle.cms.CMSAttributeTableGenerator
                    public AttributeTable getAttributes(Map map) throws CMSAttributeTableGenerationException {
                        AttributeTable attributes = signerInfoGenerator.getSignedAttributeTableGenerator().getAttributes(map);
                        return attributes.get(PKCSObjectIdentifiers.id_aa_signingCertificateV2) == null ? attributes.add(PKCSObjectIdentifiers.id_aa_signingCertificateV2, new SigningCertificateV2(eSSCertIDv2)) : attributes;
                    }
                }, signerInfoGenerator.getUnsignedAttributeTableGenerator());
            }
        } catch (IOException e) {
            throw new TSPException("Exception processing certificate.", e);
        }
    }

    public void addCertificates(Store store) {
        this.certs.addAll(store.getMatches(null));
    }

    public void addCRLs(Store store) {
        this.crls.addAll(store.getMatches(null));
    }

    public void addAttributeCertificates(Store store) {
        this.attrCerts.addAll(store.getMatches(null));
    }

    public void addOtherRevocationInfo(ASN1ObjectIdentifier aSN1ObjectIdentifier, Store store) {
        this.otherRevoc.put(aSN1ObjectIdentifier, store.getMatches(null));
    }

    public void setResolution(int i) {
        this.resolution = i;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setAccuracySeconds(int i) {
        this.accuracySeconds = i;
    }

    public void setAccuracyMillis(int i) {
        this.accuracyMillis = i;
    }

    public void setAccuracyMicros(int i) {
        this.accuracyMicros = i;
    }

    public void setOrdering(boolean z) {
        this.ordering = z;
    }

    public void setTSA(GeneralName generalName) {
        this.tsa = generalName;
    }

    public TimeStampToken generate(TimeStampRequest timeStampRequest, BigInteger bigInteger, Date date) throws TSPException {
        return generate(timeStampRequest, bigInteger, date, null);
    }

    public TimeStampToken generate(TimeStampRequest timeStampRequest, BigInteger bigInteger, Date date, Extensions extensions) throws TSPException {
        MessageImprint messageImprint = new MessageImprint(timeStampRequest.getMessageImprintAlgID(), timeStampRequest.getMessageImprintDigest());
        Accuracy accuracy = null;
        if (this.accuracySeconds > 0 || this.accuracyMillis > 0 || this.accuracyMicros > 0) {
            accuracy = new Accuracy(this.accuracySeconds > 0 ? new ASN1Integer(this.accuracySeconds) : null, this.accuracyMillis > 0 ? new ASN1Integer(this.accuracyMillis) : null, this.accuracyMicros > 0 ? new ASN1Integer(this.accuracyMicros) : null);
        }
        ASN1Boolean aSN1Boolean = this.ordering ? ASN1Boolean.getInstance(this.ordering) : null;
        ASN1Integer aSN1Integer = timeStampRequest.getNonce() != null ? new ASN1Integer(timeStampRequest.getNonce()) : null;
        ASN1ObjectIdentifier reqPolicy = this.tsaPolicyOID;
        if (timeStampRequest.getReqPolicy() != null) {
            reqPolicy = timeStampRequest.getReqPolicy();
        }
        Extensions extensions2 = timeStampRequest.getExtensions();
        if (extensions != null) {
            ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
            if (extensions2 != null) {
                Enumeration enumerationOids = extensions2.oids();
                while (enumerationOids.hasMoreElements()) {
                    extensionsGenerator.addExtension(extensions2.getExtension(ASN1ObjectIdentifier.getInstance(enumerationOids.nextElement())));
                }
            }
            Enumeration enumerationOids2 = extensions.oids();
            while (enumerationOids2.hasMoreElements()) {
                extensionsGenerator.addExtension(extensions.getExtension(ASN1ObjectIdentifier.getInstance(enumerationOids2.nextElement())));
            }
            extensions2 = extensionsGenerator.generate();
        }
        TSTInfo tSTInfo = new TSTInfo(reqPolicy, messageImprint, new ASN1Integer(bigInteger), this.resolution == 0 ? this.locale == null ? new ASN1GeneralizedTime(date) : new ASN1GeneralizedTime(date, this.locale) : createGeneralizedTime(date), accuracy, aSN1Boolean, aSN1Integer, this.tsa, extensions2);
        try {
            CMSSignedDataGenerator cMSSignedDataGenerator = new CMSSignedDataGenerator();
            if (timeStampRequest.getCertReq()) {
                cMSSignedDataGenerator.addCertificates(new CollectionStore(this.certs));
                cMSSignedDataGenerator.addAttributeCertificates(new CollectionStore(this.attrCerts));
            }
            cMSSignedDataGenerator.addCRLs(new CollectionStore(this.crls));
            if (!this.otherRevoc.isEmpty()) {
                for (ASN1ObjectIdentifier aSN1ObjectIdentifier : this.otherRevoc.keySet()) {
                    cMSSignedDataGenerator.addOtherRevocationInfo(aSN1ObjectIdentifier, new CollectionStore((Collection) this.otherRevoc.get(aSN1ObjectIdentifier)));
                }
            }
            cMSSignedDataGenerator.addSignerInfoGenerator(this.signerInfoGen);
            return new TimeStampToken(cMSSignedDataGenerator.generate(new CMSProcessableByteArray(PKCSObjectIdentifiers.id_ct_TSTInfo, tSTInfo.getEncoded("DER")), true));
        } catch (IOException e) {
            throw new TSPException("Exception encoding info", e);
        } catch (CMSException e2) {
            throw new TSPException("Error generating time-stamp token", e2);
        }
    }

    private ASN1GeneralizedTime createGeneralizedTime(Date date) throws TSPException {
        SimpleDateFormat simpleDateFormat = this.locale == null ? new SimpleDateFormat("yyyyMMddHHmmss.SSS", LocaleUtil.EN_Locale) : new SimpleDateFormat("yyyyMMddHHmmss.SSS", this.locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        StringBuilder sb = new StringBuilder(simpleDateFormat.format(date));
        int iIndexOf = sb.indexOf(".");
        if (iIndexOf < 0) {
            sb.append("Z");
            return new ASN1GeneralizedTime(sb.toString());
        }
        switch (this.resolution) {
            case 1:
                if (sb.length() > iIndexOf + 2) {
                    sb.delete(iIndexOf + 2, sb.length());
                }
                break;
            case 2:
                if (sb.length() > iIndexOf + 3) {
                    sb.delete(iIndexOf + 3, sb.length());
                }
                break;
            case 3:
                break;
            default:
                throw new TSPException("unknown time-stamp resolution: " + this.resolution);
        }
        while (sb.charAt(sb.length() - 1) == '0') {
            sb.deleteCharAt(sb.length() - 1);
        }
        if (sb.length() - 1 == iIndexOf) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("Z");
        return new ASN1GeneralizedTime(sb.toString());
    }
}
