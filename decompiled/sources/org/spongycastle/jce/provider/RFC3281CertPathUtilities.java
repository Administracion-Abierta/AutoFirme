package org.spongycastle.jce.provider;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.x509.CRLDistPoint;
import org.spongycastle.asn1.x509.DistributionPoint;
import org.spongycastle.asn1.x509.DistributionPointName;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.asn1.x509.GeneralNames;
import org.spongycastle.asn1.x509.TargetInformation;
import org.spongycastle.asn1.x509.X509Extensions;
import org.spongycastle.jcajce.PKIXCRLStore;
import org.spongycastle.jcajce.PKIXCertStoreSelector;
import org.spongycastle.jcajce.PKIXExtendedBuilderParameters;
import org.spongycastle.jcajce.PKIXExtendedParameters;
import org.spongycastle.jcajce.util.JcaJceHelper;
import org.spongycastle.jce.exception.ExtCertPathValidatorException;
import org.spongycastle.x509.PKIXAttrCertChecker;
import org.spongycastle.x509.X509AttributeCertificate;
import org.spongycastle.x509.X509CertStoreSelector;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/provider/RFC3281CertPathUtilities.class */
class RFC3281CertPathUtilities {
    private static final String TARGET_INFORMATION = Extension.targetInformation.getId();
    private static final String NO_REV_AVAIL = Extension.noRevAvail.getId();
    private static final String CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
    private static final String AUTHORITY_INFO_ACCESS = Extension.authorityInfoAccess.getId();

    RFC3281CertPathUtilities() {
    }

    protected static void processAttrCert7(X509AttributeCertificate attrCert, CertPath certPath, CertPath holderCertPath, PKIXExtendedParameters pkixParams, Set attrCertCheckers) throws CertPathValidatorException {
        Set<String> criticalExtensionOIDs = attrCert.getCriticalExtensionOIDs();
        if (criticalExtensionOIDs.contains(TARGET_INFORMATION)) {
            try {
                TargetInformation.getInstance(CertPathValidatorUtilities.getExtensionValue(attrCert, TARGET_INFORMATION));
            } catch (IllegalArgumentException e) {
                throw new ExtCertPathValidatorException("Target information extension could not be read.", e);
            } catch (AnnotatedException e2) {
                throw new ExtCertPathValidatorException("Target information extension could not be read.", e2);
            }
        }
        criticalExtensionOIDs.remove(TARGET_INFORMATION);
        Iterator it = attrCertCheckers.iterator();
        while (it.hasNext()) {
            ((PKIXAttrCertChecker) it.next()).check(attrCert, certPath, holderCertPath, criticalExtensionOIDs);
        }
        if (!criticalExtensionOIDs.isEmpty()) {
            throw new CertPathValidatorException("Attribute certificate contains unsupported critical extensions: " + criticalExtensionOIDs);
        }
    }

    protected static void checkCRLs(X509AttributeCertificate attrCert, PKIXExtendedParameters paramsPKIX, X509Certificate issuerCert, Date validDate, List certPathCerts, JcaJceHelper helper) throws CertPathValidatorException {
        if (paramsPKIX.isRevocationEnabled()) {
            if (attrCert.getExtensionValue(NO_REV_AVAIL) == null) {
                try {
                    CRLDistPoint crldp = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(attrCert, CRL_DISTRIBUTION_POINTS));
                    List crlStores = new ArrayList();
                    try {
                        crlStores.addAll(CertPathValidatorUtilities.getAdditionalStoresFromCRLDistributionPoint(crldp, paramsPKIX.getNamedCRLStoreMap()));
                        PKIXExtendedParameters.Builder bldr = new PKIXExtendedParameters.Builder(paramsPKIX);
                        Iterator it = crlStores.iterator();
                        while (it.hasNext()) {
                            bldr.addCRLStore((PKIXCRLStore) crlStores);
                        }
                        PKIXExtendedParameters paramsPKIX2 = bldr.build();
                        CertStatus certStatus = new CertStatus();
                        ReasonsMask reasonsMask = new ReasonsMask();
                        AnnotatedException lastException = null;
                        boolean validCrlFound = false;
                        if (crldp != null) {
                            try {
                                DistributionPoint[] dps = crldp.getDistributionPoints();
                                for (int i = 0; i < dps.length && certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons(); i++) {
                                    try {
                                        PKIXExtendedParameters paramsPKIXClone = (PKIXExtendedParameters) paramsPKIX2.clone();
                                        checkCRL(dps[i], attrCert, paramsPKIXClone, validDate, issuerCert, certStatus, reasonsMask, certPathCerts, helper);
                                        validCrlFound = true;
                                    } catch (AnnotatedException e) {
                                        lastException = new AnnotatedException("No valid CRL for distribution point found.", e);
                                    }
                                }
                            } catch (Exception e2) {
                                throw new ExtCertPathValidatorException("Distribution points could not be read.", e2);
                            }
                        }
                        if (certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons()) {
                            try {
                                try {
                                    ASN1Primitive issuer = new ASN1InputStream(((X500Principal) attrCert.getIssuer().getPrincipals()[0]).getEncoded()).readObject();
                                    DistributionPoint dp = new DistributionPoint(new DistributionPointName(0, new GeneralNames(new GeneralName(4, issuer))), null, null);
                                    PKIXExtendedParameters paramsPKIXClone2 = (PKIXExtendedParameters) paramsPKIX2.clone();
                                    checkCRL(dp, attrCert, paramsPKIXClone2, validDate, issuerCert, certStatus, reasonsMask, certPathCerts, helper);
                                    validCrlFound = true;
                                } catch (Exception e3) {
                                    throw new AnnotatedException("Issuer from certificate for CRL could not be reencoded.", e3);
                                }
                            } catch (AnnotatedException e4) {
                                lastException = new AnnotatedException("No valid CRL for distribution point found.", e4);
                            }
                        }
                        if (!validCrlFound) {
                            throw new ExtCertPathValidatorException("No valid CRL found.", lastException);
                        }
                        if (certStatus.getCertStatus() != 11) {
                            String message = "Attribute certificate revocation after " + certStatus.getRevocationDate();
                            throw new CertPathValidatorException(message + ", reason: " + RFC3280CertPathUtilities.crlReasons[certStatus.getCertStatus()]);
                        }
                        if (!reasonsMask.isAllReasons() && certStatus.getCertStatus() == 11) {
                            certStatus.setCertStatus(12);
                        }
                        if (certStatus.getCertStatus() == 12) {
                            throw new CertPathValidatorException("Attribute certificate status could not be determined.");
                        }
                        return;
                    } catch (AnnotatedException e5) {
                        throw new CertPathValidatorException("No additional CRL locations could be decoded from CRL distribution point extension.", e5);
                    }
                } catch (AnnotatedException e6) {
                    throw new CertPathValidatorException("CRL distribution point extension could not be read.", e6);
                }
            }
            if (attrCert.getExtensionValue(CRL_DISTRIBUTION_POINTS) != null || attrCert.getExtensionValue(AUTHORITY_INFO_ACCESS) != null) {
                throw new CertPathValidatorException("No rev avail extension is set, but also an AC revocation pointer.");
            }
        }
    }

    protected static void additionalChecks(X509AttributeCertificate attrCert, Set prohibitedACAttributes, Set necessaryACAttributes) throws CertPathValidatorException {
        Iterator it = prohibitedACAttributes.iterator();
        while (it.hasNext()) {
            String oid = (String) it.next();
            if (attrCert.getAttributes(oid) != null) {
                throw new CertPathValidatorException("Attribute certificate contains prohibited attribute: " + oid + ".");
            }
        }
        Iterator it2 = necessaryACAttributes.iterator();
        while (it2.hasNext()) {
            String oid2 = (String) it2.next();
            if (attrCert.getAttributes(oid2) == null) {
                throw new CertPathValidatorException("Attribute certificate does not contain necessary attribute: " + oid2 + ".");
            }
        }
    }

    protected static void processAttrCert5(X509AttributeCertificate attrCert, PKIXExtendedParameters pkixParams) throws CertPathValidatorException {
        try {
            attrCert.checkValidity(CertPathValidatorUtilities.getValidDate(pkixParams));
        } catch (CertificateExpiredException e) {
            throw new ExtCertPathValidatorException("Attribute certificate is not valid.", e);
        } catch (CertificateNotYetValidException e2) {
            throw new ExtCertPathValidatorException("Attribute certificate is not valid.", e2);
        }
    }

    protected static void processAttrCert4(X509Certificate acIssuerCert, Set trustedACIssuers) throws CertPathValidatorException {
        boolean trusted = false;
        Iterator it = trustedACIssuers.iterator();
        while (it.hasNext()) {
            TrustAnchor anchor = (TrustAnchor) it.next();
            if (acIssuerCert.getSubjectX500Principal().getName("RFC2253").equals(anchor.getCAName()) || acIssuerCert.equals(anchor.getTrustedCert())) {
                trusted = true;
            }
        }
        if (!trusted) {
            throw new CertPathValidatorException("Attribute certificate issuer is not directly trusted.");
        }
    }

    protected static void processAttrCert3(X509Certificate acIssuerCert, PKIXExtendedParameters pkixParams) throws CertPathValidatorException {
        if (acIssuerCert.getKeyUsage() != null && !acIssuerCert.getKeyUsage()[0] && !acIssuerCert.getKeyUsage()[1]) {
            throw new CertPathValidatorException("Attribute certificate issuer public key cannot be used to validate digital signatures.");
        }
        if (acIssuerCert.getBasicConstraints() != -1) {
            throw new CertPathValidatorException("Attribute certificate issuer is also a public key certificate issuer.");
        }
    }

    protected static CertPathValidatorResult processAttrCert2(CertPath certPath, PKIXExtendedParameters pkixParams) throws CertPathValidatorException {
        try {
            CertPathValidator validator = CertPathValidator.getInstance("PKIX", "SC");
            try {
                return validator.validate(certPath, pkixParams);
            } catch (InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e.getMessage());
            } catch (CertPathValidatorException e2) {
                throw new ExtCertPathValidatorException("Certification path for issuer certificate of attribute certificate could not be validated.", e2);
            }
        } catch (NoSuchAlgorithmException e3) {
            throw new ExtCertPathValidatorException("Support class could not be created.", e3);
        } catch (NoSuchProviderException e4) {
            throw new ExtCertPathValidatorException("Support class could not be created.", e4);
        }
    }

    protected static CertPath processAttrCert1(X509AttributeCertificate attrCert, PKIXExtendedParameters pkixParams) throws CertPathValidatorException {
        CertPathBuilderResult result = null;
        Set holderPKCs = new HashSet();
        if (attrCert.getHolder().getIssuer() != null) {
            X509CertSelector selector = new X509CertSelector();
            selector.setSerialNumber(attrCert.getHolder().getSerialNumber());
            Principal[] principals = attrCert.getHolder().getIssuer();
            for (int i = 0; i < principals.length; i++) {
                try {
                    if (principals[i] instanceof X500Principal) {
                        selector.setIssuer(((X500Principal) principals[i]).getEncoded());
                    }
                    holderPKCs.addAll(CertPathValidatorUtilities.findCertificates(new PKIXCertStoreSelector.Builder(selector).build(), pkixParams.getCertStores()));
                } catch (IOException e) {
                    throw new ExtCertPathValidatorException("Unable to encode X500 principal.", e);
                } catch (AnnotatedException e2) {
                    throw new ExtCertPathValidatorException("Public key certificate for attribute certificate cannot be searched.", e2);
                }
            }
            if (holderPKCs.isEmpty()) {
                throw new CertPathValidatorException("Public key certificate specified in base certificate ID for attribute certificate cannot be found.");
            }
        }
        if (attrCert.getHolder().getEntityNames() != null) {
            X509CertStoreSelector selector2 = new X509CertStoreSelector();
            Principal[] principals2 = attrCert.getHolder().getEntityNames();
            for (int i2 = 0; i2 < principals2.length; i2++) {
                try {
                    if (principals2[i2] instanceof X500Principal) {
                        selector2.setIssuer(((X500Principal) principals2[i2]).getEncoded());
                    }
                    holderPKCs.addAll(CertPathValidatorUtilities.findCertificates(new PKIXCertStoreSelector.Builder(selector2).build(), pkixParams.getCertStores()));
                } catch (IOException e3) {
                    throw new ExtCertPathValidatorException("Unable to encode X500 principal.", e3);
                } catch (AnnotatedException e4) {
                    throw new ExtCertPathValidatorException("Public key certificate for attribute certificate cannot be searched.", e4);
                }
            }
            if (holderPKCs.isEmpty()) {
                throw new CertPathValidatorException("Public key certificate specified in entity name for attribute certificate cannot be found.");
            }
        }
        PKIXExtendedParameters.Builder paramsBldr = new PKIXExtendedParameters.Builder(pkixParams);
        CertPathValidatorException lastException = null;
        Iterator it = holderPKCs.iterator();
        while (it.hasNext()) {
            X509CertStoreSelector selector3 = new X509CertStoreSelector();
            selector3.setCertificate((X509Certificate) it.next());
            paramsBldr.setTargetConstraints(new PKIXCertStoreSelector.Builder(selector3).build());
            try {
                CertPathBuilder builder = CertPathBuilder.getInstance("PKIX", "SC");
                try {
                    result = builder.build(new PKIXExtendedBuilderParameters.Builder(paramsBldr.build()).build());
                } catch (InvalidAlgorithmParameterException e5) {
                    throw new RuntimeException(e5.getMessage());
                } catch (CertPathBuilderException e6) {
                    lastException = new ExtCertPathValidatorException("Certification path for public key certificate of attribute certificate could not be build.", e6);
                }
            } catch (NoSuchAlgorithmException e7) {
                throw new ExtCertPathValidatorException("Support class could not be created.", e7);
            } catch (NoSuchProviderException e8) {
                throw new ExtCertPathValidatorException("Support class could not be created.", e8);
            }
        }
        if (lastException != null) {
            throw lastException;
        }
        return result.getCertPath();
    }

    private static void checkCRL(DistributionPoint dp, X509AttributeCertificate attrCert, PKIXExtendedParameters paramsPKIX, Date validDate, X509Certificate issuerCert, CertStatus certStatus, ReasonsMask reasonMask, List certPathCerts, JcaJceHelper helper) throws AnnotatedException {
        if (attrCert.getExtensionValue(X509Extensions.NoRevAvail.getId()) != null) {
            return;
        }
        Date currentDate = new Date(System.currentTimeMillis());
        if (validDate.getTime() > currentDate.getTime()) {
            throw new AnnotatedException("Validation time is in future.");
        }
        Set crls = CertPathValidatorUtilities.getCompleteCRLs(dp, attrCert, currentDate, paramsPKIX);
        boolean validCrlFound = false;
        AnnotatedException lastException = null;
        Iterator crl_iter = crls.iterator();
        while (crl_iter.hasNext() && certStatus.getCertStatus() == 11 && !reasonMask.isAllReasons()) {
            try {
                X509CRL crl = (X509CRL) crl_iter.next();
                ReasonsMask interimReasonsMask = RFC3280CertPathUtilities.processCRLD(crl, dp);
                if (interimReasonsMask.hasNewReasons(reasonMask)) {
                    Set keys = RFC3280CertPathUtilities.processCRLF(crl, attrCert, null, null, paramsPKIX, certPathCerts, helper);
                    PublicKey key = RFC3280CertPathUtilities.processCRLG(crl, keys);
                    X509CRL deltaCRL = null;
                    if (paramsPKIX.isUseDeltasEnabled()) {
                        Set deltaCRLs = CertPathValidatorUtilities.getDeltaCRLs(currentDate, crl, paramsPKIX.getCertStores(), paramsPKIX.getCRLStores());
                        deltaCRL = RFC3280CertPathUtilities.processCRLH(deltaCRLs, key);
                    }
                    if (paramsPKIX.getValidityModel() != 1 && attrCert.getNotAfter().getTime() < crl.getThisUpdate().getTime()) {
                        throw new AnnotatedException("No valid CRL for current time found.");
                    }
                    RFC3280CertPathUtilities.processCRLB1(dp, attrCert, crl);
                    RFC3280CertPathUtilities.processCRLB2(dp, attrCert, crl);
                    RFC3280CertPathUtilities.processCRLC(deltaCRL, crl, paramsPKIX);
                    RFC3280CertPathUtilities.processCRLI(validDate, deltaCRL, attrCert, certStatus, paramsPKIX);
                    RFC3280CertPathUtilities.processCRLJ(validDate, crl, attrCert, certStatus);
                    if (certStatus.getCertStatus() == 8) {
                        certStatus.setCertStatus(11);
                    }
                    reasonMask.addReasons(interimReasonsMask);
                    validCrlFound = true;
                }
            } catch (AnnotatedException e) {
                lastException = e;
            }
        }
        if (!validCrlFound) {
            throw lastException;
        }
    }
}
