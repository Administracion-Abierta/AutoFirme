package org.spongycastle.tsp;

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
import org.spongycastle.asn1.ASN1Boolean;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.ess.ESSCertID;
import org.spongycastle.asn1.ess.ESSCertIDv2;
import org.spongycastle.asn1.ess.SigningCertificate;
import org.spongycastle.asn1.ess.SigningCertificateV2;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.tsp.Accuracy;
import org.spongycastle.asn1.tsp.MessageImprint;
import org.spongycastle.asn1.tsp.TSTInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.Extensions;
import org.spongycastle.asn1.x509.ExtensionsGenerator;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.asn1.x509.GeneralNames;
import org.spongycastle.asn1.x509.IssuerSerial;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cms.CMSAttributeTableGenerationException;
import org.spongycastle.cms.CMSAttributeTableGenerator;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.CMSProcessableByteArray;
import org.spongycastle.cms.CMSSignedData;
import org.spongycastle.cms.CMSSignedDataGenerator;
import org.spongycastle.cms.SignerInfoGenerator;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.util.CollectionStore;
import org.spongycastle.util.Store;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/tsp/TimeStampTokenGenerator.class */
public class TimeStampTokenGenerator {
    public static final int R_SECONDS = 0;
    public static final int R_TENTHS_OF_SECONDS = 1;
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

    public TimeStampTokenGenerator(SignerInfoGenerator signerInfoGen, DigestCalculator digestCalculator, ASN1ObjectIdentifier tsaPolicy) throws TSPException, IllegalArgumentException {
        this(signerInfoGen, digestCalculator, tsaPolicy, false);
    }

    public TimeStampTokenGenerator(final SignerInfoGenerator signerInfoGen, DigestCalculator digestCalculator, ASN1ObjectIdentifier tsaPolicy, boolean isIssuerSerialIncluded) throws TSPException, IllegalArgumentException {
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
        this.signerInfoGen = signerInfoGen;
        this.tsaPolicyOID = tsaPolicy;
        if (!signerInfoGen.hasAssociatedCertificate()) {
            throw new IllegalArgumentException("SignerInfoGenerator must have an associated certificate");
        }
        X509CertificateHolder assocCert = signerInfoGen.getAssociatedCertificate();
        TSPUtil.validateCertificate(assocCert);
        try {
            OutputStream dOut = digestCalculator.getOutputStream();
            dOut.write(assocCert.getEncoded());
            dOut.close();
            if (digestCalculator.getAlgorithmIdentifier().getAlgorithm().equals(OIWObjectIdentifiers.idSHA1)) {
                final ESSCertID essCertid = new ESSCertID(digestCalculator.getDigest(), isIssuerSerialIncluded ? new IssuerSerial(new GeneralNames(new GeneralName(assocCert.getIssuer())), assocCert.getSerialNumber()) : null);
                this.signerInfoGen = new SignerInfoGenerator(signerInfoGen, new CMSAttributeTableGenerator() { // from class: org.spongycastle.tsp.TimeStampTokenGenerator.1
                    @Override // org.spongycastle.cms.CMSAttributeTableGenerator
                    public AttributeTable getAttributes(Map parameters) throws CMSAttributeTableGenerationException {
                        AttributeTable table = signerInfoGen.getSignedAttributeTableGenerator().getAttributes(parameters);
                        if (table.get(PKCSObjectIdentifiers.id_aa_signingCertificate) == null) {
                            return table.add(PKCSObjectIdentifiers.id_aa_signingCertificate, new SigningCertificate(essCertid));
                        }
                        return table;
                    }
                }, signerInfoGen.getUnsignedAttributeTableGenerator());
            } else {
                AlgorithmIdentifier digAlgID = new AlgorithmIdentifier(digestCalculator.getAlgorithmIdentifier().getAlgorithm());
                final ESSCertIDv2 essCertid2 = new ESSCertIDv2(digAlgID, digestCalculator.getDigest(), isIssuerSerialIncluded ? new IssuerSerial(new GeneralNames(new GeneralName(assocCert.getIssuer())), new ASN1Integer(assocCert.getSerialNumber())) : null);
                this.signerInfoGen = new SignerInfoGenerator(signerInfoGen, new CMSAttributeTableGenerator() { // from class: org.spongycastle.tsp.TimeStampTokenGenerator.2
                    @Override // org.spongycastle.cms.CMSAttributeTableGenerator
                    public AttributeTable getAttributes(Map parameters) throws CMSAttributeTableGenerationException {
                        AttributeTable table = signerInfoGen.getSignedAttributeTableGenerator().getAttributes(parameters);
                        if (table.get(PKCSObjectIdentifiers.id_aa_signingCertificateV2) == null) {
                            return table.add(PKCSObjectIdentifiers.id_aa_signingCertificateV2, new SigningCertificateV2(essCertid2));
                        }
                        return table;
                    }
                }, signerInfoGen.getUnsignedAttributeTableGenerator());
            }
        } catch (IOException e) {
            throw new TSPException("Exception processing certificate.", e);
        }
    }

    public void addCertificates(Store certStore) {
        this.certs.addAll(certStore.getMatches(null));
    }

    public void addCRLs(Store crlStore) {
        this.crls.addAll(crlStore.getMatches(null));
    }

    public void addAttributeCertificates(Store attrStore) {
        this.attrCerts.addAll(attrStore.getMatches(null));
    }

    public void addOtherRevocationInfo(ASN1ObjectIdentifier otherRevocationInfoFormat, Store otherRevocationInfos) {
        this.otherRevoc.put(otherRevocationInfoFormat, otherRevocationInfos.getMatches(null));
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setAccuracySeconds(int accuracySeconds) {
        this.accuracySeconds = accuracySeconds;
    }

    public void setAccuracyMillis(int accuracyMillis) {
        this.accuracyMillis = accuracyMillis;
    }

    public void setAccuracyMicros(int accuracyMicros) {
        this.accuracyMicros = accuracyMicros;
    }

    public void setOrdering(boolean ordering) {
        this.ordering = ordering;
    }

    public void setTSA(GeneralName tsa) {
        this.tsa = tsa;
    }

    public TimeStampToken generate(TimeStampRequest request, BigInteger serialNumber, Date genTime) throws TSPException {
        return generate(request, serialNumber, genTime, null);
    }

    public TimeStampToken generate(TimeStampRequest request, BigInteger serialNumber, Date genTime, Extensions additionalExtensions) throws TSPException {
        ASN1GeneralizedTime timeStampTime;
        ASN1ObjectIdentifier digestAlgOID = request.getMessageImprintAlgOID();
        AlgorithmIdentifier algID = new AlgorithmIdentifier(digestAlgOID, DERNull.INSTANCE);
        MessageImprint messageImprint = new MessageImprint(algID, request.getMessageImprintDigest());
        Accuracy accuracy = null;
        if (this.accuracySeconds > 0 || this.accuracyMillis > 0 || this.accuracyMicros > 0) {
            ASN1Integer seconds = null;
            if (this.accuracySeconds > 0) {
                seconds = new ASN1Integer(this.accuracySeconds);
            }
            ASN1Integer millis = null;
            if (this.accuracyMillis > 0) {
                millis = new ASN1Integer(this.accuracyMillis);
            }
            ASN1Integer micros = null;
            if (this.accuracyMicros > 0) {
                micros = new ASN1Integer(this.accuracyMicros);
            }
            accuracy = new Accuracy(seconds, millis, micros);
        }
        ASN1Boolean derOrdering = null;
        if (this.ordering) {
            derOrdering = ASN1Boolean.getInstance(this.ordering);
        }
        ASN1Integer nonce = null;
        if (request.getNonce() != null) {
            nonce = new ASN1Integer(request.getNonce());
        }
        ASN1ObjectIdentifier tsaPolicy = this.tsaPolicyOID;
        if (request.getReqPolicy() != null) {
            tsaPolicy = request.getReqPolicy();
        }
        Extensions respExtensions = request.getExtensions();
        if (additionalExtensions != null) {
            ExtensionsGenerator extGen = new ExtensionsGenerator();
            if (respExtensions != null) {
                Enumeration en = respExtensions.oids();
                while (en.hasMoreElements()) {
                    extGen.addExtension(respExtensions.getExtension(ASN1ObjectIdentifier.getInstance(en.nextElement())));
                }
            }
            Enumeration en2 = additionalExtensions.oids();
            while (en2.hasMoreElements()) {
                extGen.addExtension(additionalExtensions.getExtension(ASN1ObjectIdentifier.getInstance(en2.nextElement())));
            }
            respExtensions = extGen.generate();
        }
        if (this.resolution == 0) {
            timeStampTime = this.locale == null ? new ASN1GeneralizedTime(genTime) : new ASN1GeneralizedTime(genTime, this.locale);
        } else {
            timeStampTime = createGeneralizedTime(genTime);
        }
        TSTInfo tstInfo = new TSTInfo(tsaPolicy, messageImprint, new ASN1Integer(serialNumber), timeStampTime, accuracy, derOrdering, nonce, this.tsa, respExtensions);
        try {
            CMSSignedDataGenerator signedDataGenerator = new CMSSignedDataGenerator();
            if (request.getCertReq()) {
                signedDataGenerator.addCertificates(new CollectionStore(this.certs));
                signedDataGenerator.addAttributeCertificates(new CollectionStore(this.attrCerts));
            }
            signedDataGenerator.addCRLs(new CollectionStore(this.crls));
            if (!this.otherRevoc.isEmpty()) {
                for (ASN1ObjectIdentifier format : this.otherRevoc.keySet()) {
                    signedDataGenerator.addOtherRevocationInfo(format, new CollectionStore((Collection) this.otherRevoc.get(format)));
                }
            }
            signedDataGenerator.addSignerInfoGenerator(this.signerInfoGen);
            byte[] derEncodedTSTInfo = tstInfo.getEncoded(ASN1Encoding.DER);
            CMSSignedData signedData = signedDataGenerator.generate(new CMSProcessableByteArray(PKCSObjectIdentifiers.id_ct_TSTInfo, derEncodedTSTInfo), true);
            return new TimeStampToken(signedData);
        } catch (IOException e) {
            throw new TSPException("Exception encoding info", e);
        } catch (CMSException cmsEx) {
            throw new TSPException("Error generating time-stamp token", cmsEx);
        }
    }

    private ASN1GeneralizedTime createGeneralizedTime(Date time) throws TSPException {
        SimpleDateFormat dateF = this.locale == null ? new SimpleDateFormat("yyyyMMddHHmmss.SSS") : new SimpleDateFormat("yyyyMMddHHmmss.SSS", this.locale);
        dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        StringBuilder sBuild = new StringBuilder(dateF.format(time));
        int dotIndex = sBuild.indexOf(".");
        if (dotIndex < 0) {
            sBuild.append("Z");
            return new ASN1GeneralizedTime(sBuild.toString());
        }
        switch (this.resolution) {
            case 1:
                if (sBuild.length() > dotIndex + 2) {
                    sBuild.delete(dotIndex + 2, sBuild.length());
                }
                break;
            case 2:
                if (sBuild.length() > dotIndex + 3) {
                    sBuild.delete(dotIndex + 3, sBuild.length());
                }
                break;
            case 3:
                break;
            default:
                throw new TSPException("unknown time-stamp resolution: " + this.resolution);
        }
        while (sBuild.charAt(sBuild.length() - 1) == '0') {
            sBuild.deleteCharAt(sBuild.length() - 1);
        }
        if (sBuild.length() - 1 == dotIndex) {
            sBuild.deleteCharAt(sBuild.length() - 1);
        }
        sBuild.append("Z");
        return new ASN1GeneralizedTime(sBuild.toString());
    }
}
