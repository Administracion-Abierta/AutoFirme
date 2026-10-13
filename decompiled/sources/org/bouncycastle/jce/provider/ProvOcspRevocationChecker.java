package org.bouncycastle.jce.provider;

import com.sun.jna.platform.win32.WinCrypt;
import es.gob.afirma.core.signers.AOSignConstants;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.Extension;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.CertID;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.ocsp.ResponderID;
import org.bouncycastle.asn1.ocsp.ResponseBytes;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.RevokedInfo;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStrictStyle;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.internal.asn1.bsi.BSIObjectIdentifiers;
import org.bouncycastle.internal.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.internal.asn1.isara.IsaraObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.jcajce.PKIXCertRevocationChecker;
import org.bouncycastle.jcajce.PKIXCertRevocationCheckerParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/provider/ProvOcspRevocationChecker.class */
class ProvOcspRevocationChecker implements PKIXCertRevocationChecker {
    private static final int DEFAULT_OCSP_TIMEOUT = 15000;
    private static final int DEFAULT_OCSP_MAX_RESPONSE_SIZE = 32768;
    private static final Map oids = new HashMap();
    private final ProvRevocationChecker parent;
    private final JcaJceHelper helper;
    private PKIXCertRevocationCheckerParameters parameters;
    private boolean isEnabledOCSP;
    private String ocspURL;

    public ProvOcspRevocationChecker(ProvRevocationChecker provRevocationChecker, JcaJceHelper jcaJceHelper) {
        this.parent = provRevocationChecker;
        this.helper = jcaJceHelper;
    }

    @Override // org.bouncycastle.jcajce.PKIXCertRevocationChecker
    public void setParameter(String str, Object obj) {
    }

    @Override // org.bouncycastle.jcajce.PKIXCertRevocationChecker
    public void initialize(PKIXCertRevocationCheckerParameters pKIXCertRevocationCheckerParameters) {
        this.parameters = pKIXCertRevocationCheckerParameters;
        this.isEnabledOCSP = Properties.isOverrideSet("ocsp.enable");
        this.ocspURL = Properties.getPropertyValue("ocsp.responderURL");
    }

    public List<CertPathValidatorException> getSoftFailExceptions() {
        return null;
    }

    public void init(boolean z) throws CertPathValidatorException {
        if (z) {
            throw new CertPathValidatorException("forward checking not supported");
        }
        this.parameters = null;
        this.isEnabledOCSP = Properties.isOverrideSet("ocsp.enable");
        this.ocspURL = Properties.getPropertyValue("ocsp.responderURL");
    }

    public boolean isForwardCheckingSupported() {
        return false;
    }

    public Set<String> getSupportedExtensions() {
        return null;
    }

    @Override // org.bouncycastle.jcajce.PKIXCertRevocationChecker
    public void check(Certificate certificate) throws CertPathValidatorException {
        X509Certificate x509Certificate = (X509Certificate) certificate;
        Map<X509Certificate, byte[]> ocspResponses = this.parent.getOcspResponses();
        URI ocspResponder = this.parent.getOcspResponder();
        if (ocspResponder == null) {
            if (this.ocspURL != null) {
                try {
                    ocspResponder = new URI(this.ocspURL);
                } catch (URISyntaxException e) {
                    throw new CertPathValidatorException("configuration error: " + e.getMessage(), e, this.parameters.getCertPath(), this.parameters.getIndex());
                }
            } else {
                ocspResponder = getOcspResponderURI(x509Certificate);
            }
        }
        byte[] bArr = null;
        boolean z = false;
        if (ocspResponses.get(x509Certificate) != null || ocspResponder == null) {
            List<Extension> ocspExtensions = this.parent.getOcspExtensions();
            for (int i = 0; i != ocspExtensions.size(); i++) {
                Extension extension = ocspExtensions.get(i);
                byte[] value = extension.getValue();
                if (OCSPObjectIdentifiers.id_pkix_ocsp_nonce.getId().equals(extension.getId())) {
                    bArr = value;
                }
            }
        } else {
            if (this.ocspURL == null && this.parent.getOcspResponder() == null && !this.isEnabledOCSP) {
                throw new RecoverableCertPathValidatorException("OCSP disabled by \"ocsp.enable\" setting", null, this.parameters.getCertPath(), this.parameters.getIndex());
            }
            try {
                ocspResponses.put(x509Certificate, OcspCache.getOcspResponse(createCertID(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1), extractCert(), new ASN1Integer(x509Certificate.getSerialNumber())), this.parameters, ocspResponder, this.parent.getOcspResponderCert(), this.parent.getOcspExtensions(), this.helper).getEncoded());
                z = true;
            } catch (IOException e2) {
                throw new CertPathValidatorException("unable to encode OCSP response", e2, this.parameters.getCertPath(), this.parameters.getIndex());
            }
        }
        if (ocspResponses.isEmpty()) {
            throw new RecoverableCertPathValidatorException("no OCSP response found for any certificate", null, this.parameters.getCertPath(), this.parameters.getIndex());
        }
        OCSPResponse oCSPResponse = OCSPResponse.getInstance(ocspResponses.get(x509Certificate));
        ASN1Integer aSN1Integer = new ASN1Integer(x509Certificate.getSerialNumber());
        if (oCSPResponse == null) {
            throw new RecoverableCertPathValidatorException("no OCSP response found for certificate", null, this.parameters.getCertPath(), this.parameters.getIndex());
        }
        if (0 != oCSPResponse.getResponseStatus().getIntValue()) {
            throw new CertPathValidatorException("OCSP response failed: " + oCSPResponse.getResponseStatus().getValue(), null, this.parameters.getCertPath(), this.parameters.getIndex());
        }
        ResponseBytes responseBytes = ResponseBytes.getInstance(oCSPResponse.getResponseBytes());
        if (responseBytes.getResponseType().equals((ASN1Primitive) OCSPObjectIdentifiers.id_pkix_ocsp_basic)) {
            try {
                BasicOCSPResponse basicOCSPResponse = BasicOCSPResponse.getInstance(responseBytes.getResponse().getOctets());
                if (z || validatedOcspResponse(basicOCSPResponse, this.parameters, bArr, this.parent.getOcspResponderCert(), this.helper)) {
                    ASN1Sequence responses = ResponseData.getInstance(basicOCSPResponse.getTbsResponseData()).getResponses();
                    CertID certIDCreateCertID = null;
                    for (int i2 = 0; i2 != responses.size(); i2++) {
                        SingleResponse singleResponse = SingleResponse.getInstance(responses.getObjectAt(i2));
                        if (aSN1Integer.equals((ASN1Primitive) singleResponse.getCertID().getSerialNumber())) {
                            ASN1GeneralizedTime nextUpdate = singleResponse.getNextUpdate();
                            if (nextUpdate != null && this.parameters.getValidDate().after(nextUpdate.getDate())) {
                                throw new ExtCertPathValidatorException("OCSP response expired");
                            }
                            if (certIDCreateCertID == null || !isEqualAlgId(certIDCreateCertID.getHashAlgorithm(), singleResponse.getCertID().getHashAlgorithm())) {
                                certIDCreateCertID = createCertID(singleResponse.getCertID(), extractCert(), aSN1Integer);
                            }
                            if (certIDCreateCertID.equals(singleResponse.getCertID())) {
                                if (singleResponse.getCertStatus().getTagNo() == 0) {
                                    return;
                                }
                                if (singleResponse.getCertStatus().getTagNo() != 1) {
                                    throw new CertPathValidatorException("certificate revoked, details unknown", null, this.parameters.getCertPath(), this.parameters.getIndex());
                                }
                                RevokedInfo revokedInfo = RevokedInfo.getInstance(singleResponse.getCertStatus().getStatus());
                                throw new CertPathValidatorException("certificate revoked, reason=(" + revokedInfo.getRevocationReason() + "), date=" + revokedInfo.getRevocationTime().getDate(), null, this.parameters.getCertPath(), this.parameters.getIndex());
                            }
                        }
                    }
                }
            } catch (CertPathValidatorException e3) {
                throw e3;
            } catch (Exception e4) {
                throw new CertPathValidatorException("unable to process OCSP response", e4, this.parameters.getCertPath(), this.parameters.getIndex());
            }
        }
    }

    private static boolean isEqualAlgId(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) {
        if (algorithmIdentifier == algorithmIdentifier2 || algorithmIdentifier.equals(algorithmIdentifier2)) {
            return true;
        }
        if (!algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) algorithmIdentifier2.getAlgorithm())) {
            return false;
        }
        ASN1Encodable parameters = algorithmIdentifier.getParameters();
        ASN1Encodable parameters2 = algorithmIdentifier2.getParameters();
        if (parameters == parameters2) {
            return true;
        }
        if (parameters == null) {
            return DERNull.INSTANCE.equals(parameters2);
        }
        if (DERNull.INSTANCE.equals(parameters) && parameters2 == null) {
            return true;
        }
        return parameters.equals(parameters2);
    }

    static URI getOcspResponderURI(X509Certificate x509Certificate) {
        byte[] extensionValue = x509Certificate.getExtensionValue(org.bouncycastle.asn1.x509.Extension.authorityInfoAccess.getId());
        if (extensionValue == null) {
            return null;
        }
        AccessDescription[] accessDescriptions = AuthorityInformationAccess.getInstance(ASN1OctetString.getInstance(extensionValue).getOctets()).getAccessDescriptions();
        for (int i = 0; i != accessDescriptions.length; i++) {
            AccessDescription accessDescription = accessDescriptions[i];
            if (AccessDescription.id_ad_ocsp.equals((ASN1Primitive) accessDescription.getAccessMethod())) {
                GeneralName accessLocation = accessDescription.getAccessLocation();
                if (accessLocation.getTagNo() == 6) {
                    try {
                        return new URI(((ASN1String) accessLocation.getName()).getString());
                    } catch (URISyntaxException e) {
                    }
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    static boolean validatedOcspResponse(BasicOCSPResponse basicOCSPResponse, PKIXCertRevocationCheckerParameters pKIXCertRevocationCheckerParameters, byte[] bArr, X509Certificate x509Certificate, JcaJceHelper jcaJceHelper) throws CertPathValidatorException {
        try {
            ASN1Sequence certs = basicOCSPResponse.getCerts();
            Signature signatureCreateSignature = jcaJceHelper.createSignature(getSignatureName(basicOCSPResponse.getSignatureAlgorithm()));
            X509Certificate signerCert = getSignerCert(basicOCSPResponse, pKIXCertRevocationCheckerParameters.getSigningCert(), x509Certificate, jcaJceHelper);
            if (signerCert == null && certs == null) {
                throw new CertPathValidatorException("OCSP responder certificate not found");
            }
            if (signerCert != null) {
                signatureCreateSignature.initVerify(signerCert.getPublicKey());
            } else {
                X509Certificate x509Certificate2 = (X509Certificate) jcaJceHelper.createCertificateFactory(XMLX509Certificate.JCA_CERT_ID).generateCertificate(new ByteArrayInputStream(certs.getObjectAt(0).toASN1Primitive().getEncoded()));
                x509Certificate2.verify(pKIXCertRevocationCheckerParameters.getSigningCert().getPublicKey());
                x509Certificate2.checkValidity(pKIXCertRevocationCheckerParameters.getValidDate());
                if (!responderMatches(basicOCSPResponse.getTbsResponseData().getResponderID(), x509Certificate2, jcaJceHelper)) {
                    throw new CertPathValidatorException("responder certificate does not match responderID", null, pKIXCertRevocationCheckerParameters.getCertPath(), pKIXCertRevocationCheckerParameters.getIndex());
                }
                List<String> extendedKeyUsage = x509Certificate2.getExtendedKeyUsage();
                if (extendedKeyUsage == null || !extendedKeyUsage.contains(KeyPurposeId.id_kp_OCSPSigning.getId())) {
                    throw new CertPathValidatorException("responder certificate not valid for signing OCSP responses", null, pKIXCertRevocationCheckerParameters.getCertPath(), pKIXCertRevocationCheckerParameters.getIndex());
                }
                signatureCreateSignature.initVerify(x509Certificate2);
            }
            signatureCreateSignature.update(basicOCSPResponse.getTbsResponseData().getEncoded("DER"));
            if (!signatureCreateSignature.verify(basicOCSPResponse.getSignature().getOctets())) {
                return false;
            }
            if (bArr == null || Arrays.areEqual(bArr, basicOCSPResponse.getTbsResponseData().getResponseExtensions().getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce).getExtnValue().getOctets())) {
                return true;
            }
            throw new CertPathValidatorException("nonce mismatch in OCSP response", null, pKIXCertRevocationCheckerParameters.getCertPath(), pKIXCertRevocationCheckerParameters.getIndex());
        } catch (IOException e) {
            throw new CertPathValidatorException("OCSP response failure: " + e.getMessage(), e, pKIXCertRevocationCheckerParameters.getCertPath(), pKIXCertRevocationCheckerParameters.getIndex());
        } catch (CertPathValidatorException e2) {
            throw e2;
        } catch (GeneralSecurityException e3) {
            throw new CertPathValidatorException("OCSP response failure: " + e3.getMessage(), e3, pKIXCertRevocationCheckerParameters.getCertPath(), pKIXCertRevocationCheckerParameters.getIndex());
        }
    }

    private static X509Certificate getSignerCert(BasicOCSPResponse basicOCSPResponse, X509Certificate x509Certificate, X509Certificate x509Certificate2, JcaJceHelper jcaJceHelper) throws NoSuchAlgorithmException, NoSuchProviderException {
        ResponderID responderID = basicOCSPResponse.getTbsResponseData().getResponderID();
        byte[] keyHash = responderID.getKeyHash();
        if (keyHash != null) {
            MessageDigest messageDigestCreateMessageDigest = jcaJceHelper.createMessageDigest(AOSignConstants.DIGEST_ALGORITHM_SHA1);
            if (x509Certificate2 != null && Arrays.areEqual(keyHash, calcKeyHash(messageDigestCreateMessageDigest, x509Certificate2.getPublicKey()))) {
                return x509Certificate2;
            }
            if (x509Certificate == null || !Arrays.areEqual(keyHash, calcKeyHash(messageDigestCreateMessageDigest, x509Certificate.getPublicKey()))) {
                return null;
            }
            return x509Certificate;
        }
        X500Name x500Name = X500Name.getInstance(BCStrictStyle.INSTANCE, responderID.getName());
        if (x509Certificate2 != null && x500Name.equals(X500Name.getInstance(BCStrictStyle.INSTANCE, x509Certificate2.getSubjectX500Principal().getEncoded()))) {
            return x509Certificate2;
        }
        if (x509Certificate == null || !x500Name.equals(X500Name.getInstance(BCStrictStyle.INSTANCE, x509Certificate.getSubjectX500Principal().getEncoded()))) {
            return null;
        }
        return x509Certificate;
    }

    private static boolean responderMatches(ResponderID responderID, X509Certificate x509Certificate, JcaJceHelper jcaJceHelper) throws NoSuchAlgorithmException, NoSuchProviderException {
        byte[] keyHash = responderID.getKeyHash();
        return keyHash != null ? Arrays.areEqual(keyHash, calcKeyHash(jcaJceHelper.createMessageDigest(AOSignConstants.DIGEST_ALGORITHM_SHA1), x509Certificate.getPublicKey())) : X500Name.getInstance(BCStrictStyle.INSTANCE, responderID.getName()).equals(X500Name.getInstance(BCStrictStyle.INSTANCE, x509Certificate.getSubjectX500Principal().getEncoded()));
    }

    private static byte[] calcKeyHash(MessageDigest messageDigest, PublicKey publicKey) {
        return messageDigest.digest(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()).getPublicKeyData().getBytes());
    }

    private org.bouncycastle.asn1.x509.Certificate extractCert() throws CertPathValidatorException {
        try {
            return org.bouncycastle.asn1.x509.Certificate.getInstance(this.parameters.getSigningCert().getEncoded());
        } catch (Exception e) {
            throw new CertPathValidatorException("cannot process signing cert: " + e.getMessage(), e, this.parameters.getCertPath(), this.parameters.getIndex());
        }
    }

    private CertID createCertID(CertID certID, org.bouncycastle.asn1.x509.Certificate certificate, ASN1Integer aSN1Integer) throws CertPathValidatorException {
        return createCertID(certID.getHashAlgorithm(), certificate, aSN1Integer);
    }

    private CertID createCertID(AlgorithmIdentifier algorithmIdentifier, org.bouncycastle.asn1.x509.Certificate certificate, ASN1Integer aSN1Integer) throws CertPathValidatorException {
        try {
            MessageDigest messageDigestCreateMessageDigest = this.helper.createMessageDigest(MessageDigestUtils.getDigestName(algorithmIdentifier.getAlgorithm()));
            return new CertID(algorithmIdentifier, new DEROctetString(messageDigestCreateMessageDigest.digest(certificate.getSubject().getEncoded("DER"))), new DEROctetString(messageDigestCreateMessageDigest.digest(certificate.getSubjectPublicKeyInfo().getPublicKeyData().getBytes())), aSN1Integer);
        } catch (Exception e) {
            throw new CertPathValidatorException("problem creating ID: " + e, e);
        }
    }

    private static String getDigestName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String digestName = MessageDigestUtils.getDigestName(aSN1ObjectIdentifier);
        int iIndexOf = digestName.indexOf(45);
        return (iIndexOf <= 0 || digestName.startsWith("SHA3")) ? digestName : digestName.substring(0, iIndexOf) + digestName.substring(iIndexOf + 1);
    }

    private static String getSignatureName(AlgorithmIdentifier algorithmIdentifier) {
        ASN1Encodable parameters = algorithmIdentifier.getParameters();
        if (parameters == null || DERNull.INSTANCE.equals(parameters) || !algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            return oids.containsKey(algorithmIdentifier.getAlgorithm()) ? (String) oids.get(algorithmIdentifier.getAlgorithm()) : algorithmIdentifier.getAlgorithm().getId();
        }
        return getDigestName(RSASSAPSSparams.getInstance(parameters).getHashAlgorithm().getAlgorithm()) + "WITHRSAANDMGF1";
    }

    static {
        oids.put(new ASN1ObjectIdentifier(WinCrypt.szOID_RSA_SHA1RSA), "SHA1WITHRSA");
        oids.put(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224WITHRSA");
        oids.put(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256WITHRSA");
        oids.put(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384WITHRSA");
        oids.put(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512WITHRSA");
        oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3411WITHGOST3410");
        oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "GOST3411WITHECGOST3410");
        oids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "GOST3411-2012-256WITHECGOST3410-2012-256");
        oids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "GOST3411-2012-512WITHECGOST3410-2012-512");
        oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA1, "SHA1WITHPLAIN-ECDSA");
        oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA224, "SHA224WITHPLAIN-ECDSA");
        oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA256, "SHA256WITHPLAIN-ECDSA");
        oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA384, "SHA384WITHPLAIN-ECDSA");
        oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA512, "SHA512WITHPLAIN-ECDSA");
        oids.put(BSIObjectIdentifiers.ecdsa_plain_RIPEMD160, "RIPEMD160WITHPLAIN-ECDSA");
        oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, "SHA1WITHCVC-ECDSA");
        oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, "SHA224WITHCVC-ECDSA");
        oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, "SHA256WITHCVC-ECDSA");
        oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, "SHA384WITHCVC-ECDSA");
        oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, "SHA512WITHCVC-ECDSA");
        oids.put(IsaraObjectIdentifiers.id_alg_xmss, "XMSS");
        oids.put(IsaraObjectIdentifiers.id_alg_xmssmt, "XMSSMT");
        oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"), "MD5WITHRSA");
        oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.2"), "MD2WITHRSA");
        oids.put(new ASN1ObjectIdentifier("1.2.840.10040.4.3"), "SHA1WITHDSA");
        oids.put(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1WITHECDSA");
        oids.put(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224WITHECDSA");
        oids.put(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256WITHECDSA");
        oids.put(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384WITHECDSA");
        oids.put(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512WITHECDSA");
        oids.put(OIWObjectIdentifiers.sha1WithRSA, "SHA1WITHRSA");
        oids.put(OIWObjectIdentifiers.dsaWithSHA1, "SHA1WITHDSA");
        oids.put(NISTObjectIdentifiers.dsa_with_sha224, "SHA224WITHDSA");
        oids.put(NISTObjectIdentifiers.dsa_with_sha256, "SHA256WITHDSA");
    }
}
