package org.spongycastle.jce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertPathValidatorSpi;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.TBSCertificate;
import org.spongycastle.jcajce.PKIXExtendedBuilderParameters;
import org.spongycastle.jcajce.PKIXExtendedParameters;
import org.spongycastle.jcajce.util.BCJcaJceHelper;
import org.spongycastle.jcajce.util.JcaJceHelper;
import org.spongycastle.jce.exception.ExtCertPathValidatorException;
import org.spongycastle.x509.ExtendedPKIXParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/provider/PKIXCertPathValidatorSpi.class */
public class PKIXCertPathValidatorSpi extends CertPathValidatorSpi {
    private final JcaJceHelper helper = new BCJcaJceHelper();

    @Override // java.security.cert.CertPathValidatorSpi
    public CertPathValidatorResult engineValidate(CertPath certPath, CertPathParameters params) throws CertPathValidatorException, InvalidAlgorithmParameterException {
        PKIXExtendedParameters paramsPKIX;
        int explicitPolicy;
        int inhibitAnyPolicy;
        int policyMapping;
        X500Name workingIssuerName;
        PublicKey workingPublicKey;
        Set criticalExtensions;
        Set criticalExtensions2;
        if (params instanceof PKIXParameters) {
            PKIXExtendedParameters.Builder paramsPKIXBldr = new PKIXExtendedParameters.Builder((PKIXParameters) params);
            if (params instanceof ExtendedPKIXParameters) {
                ExtendedPKIXParameters extPKIX = (ExtendedPKIXParameters) params;
                paramsPKIXBldr.setUseDeltasEnabled(extPKIX.isUseDeltasEnabled());
                paramsPKIXBldr.setValidityModel(extPKIX.getValidityModel());
            }
            paramsPKIX = paramsPKIXBldr.build();
        } else if (params instanceof PKIXExtendedBuilderParameters) {
            paramsPKIX = ((PKIXExtendedBuilderParameters) params).getBaseParameters();
        } else if (params instanceof PKIXExtendedParameters) {
            paramsPKIX = (PKIXExtendedParameters) params;
        } else {
            throw new InvalidAlgorithmParameterException("Parameters must be a " + PKIXParameters.class.getName() + " instance.");
        }
        if (paramsPKIX.getTrustAnchors() == null) {
            throw new InvalidAlgorithmParameterException("trustAnchors is null, this is not allowed for certification path validation.");
        }
        List<? extends Certificate> certificates = certPath.getCertificates();
        int n = certificates.size();
        if (certificates.isEmpty()) {
            throw new CertPathValidatorException("Certification path is empty.", null, certPath, -1);
        }
        Set userInitialPolicySet = paramsPKIX.getInitialPolicies();
        try {
            TrustAnchor trust = CertPathValidatorUtilities.findTrustAnchor((X509Certificate) certificates.get(certificates.size() - 1), paramsPKIX.getTrustAnchors(), paramsPKIX.getSigProvider());
            if (trust == null) {
                throw new CertPathValidatorException("Trust anchor for certification path not found.", null, certPath, -1);
            }
            checkCertificate(trust.getTrustedCert());
            PKIXExtendedParameters paramsPKIX2 = new PKIXExtendedParameters.Builder(paramsPKIX).setTrustAnchor(trust).build();
            List[] policyNodes = new ArrayList[n + 1];
            for (int j = 0; j < policyNodes.length; j++) {
                policyNodes[j] = new ArrayList();
            }
            Set policySet = new HashSet();
            policySet.add(RFC3280CertPathUtilities.ANY_POLICY);
            PKIXPolicyNode validPolicyTree = new PKIXPolicyNode(new ArrayList(), 0, policySet, null, new HashSet(), RFC3280CertPathUtilities.ANY_POLICY, false);
            policyNodes[0].add(validPolicyTree);
            PKIXNameConstraintValidator nameConstraintValidator = new PKIXNameConstraintValidator();
            Set acceptablePolicies = new HashSet();
            if (paramsPKIX2.isExplicitPolicyRequired()) {
                explicitPolicy = 0;
            } else {
                explicitPolicy = n + 1;
            }
            if (paramsPKIX2.isAnyPolicyInhibited()) {
                inhibitAnyPolicy = 0;
            } else {
                inhibitAnyPolicy = n + 1;
            }
            if (paramsPKIX2.isPolicyMappingInhibited()) {
                policyMapping = 0;
            } else {
                policyMapping = n + 1;
            }
            X509Certificate sign = trust.getTrustedCert();
            try {
                if (sign != null) {
                    workingIssuerName = PrincipalUtils.getSubjectPrincipal(sign);
                    workingPublicKey = sign.getPublicKey();
                } else {
                    workingIssuerName = PrincipalUtils.getCA(trust);
                    workingPublicKey = trust.getCAPublicKey();
                }
                try {
                    AlgorithmIdentifier workingAlgId = CertPathValidatorUtilities.getAlgorithmIdentifier(workingPublicKey);
                    workingAlgId.getAlgorithm();
                    workingAlgId.getParameters();
                    int maxPathLength = n;
                    if (paramsPKIX2.getTargetConstraints() != null && !paramsPKIX2.getTargetConstraints().match((Certificate) certificates.get(0))) {
                        throw new ExtCertPathValidatorException("Target certificate in certification path does not match targetConstraints.", null, certPath, 0);
                    }
                    List pathCheckers = paramsPKIX2.getCertPathCheckers();
                    Iterator certIter = pathCheckers.iterator();
                    while (certIter.hasNext()) {
                        ((PKIXCertPathChecker) certIter.next()).init(false);
                    }
                    X509Certificate cert = null;
                    int index = certificates.size() - 1;
                    while (index >= 0) {
                        int i = n - index;
                        cert = (X509Certificate) certificates.get(index);
                        boolean verificationAlreadyPerformed = index == certificates.size() - 1;
                        try {
                            checkCertificate(cert);
                            RFC3280CertPathUtilities.processCertA(certPath, paramsPKIX2, index, workingPublicKey, verificationAlreadyPerformed, workingIssuerName, sign, this.helper);
                            RFC3280CertPathUtilities.processCertBC(certPath, index, nameConstraintValidator);
                            validPolicyTree = RFC3280CertPathUtilities.processCertE(certPath, index, RFC3280CertPathUtilities.processCertD(certPath, index, acceptablePolicies, validPolicyTree, policyNodes, inhibitAnyPolicy));
                            RFC3280CertPathUtilities.processCertF(certPath, index, validPolicyTree, explicitPolicy);
                            if (i != n) {
                                if (cert != null && cert.getVersion() == 1) {
                                    if (i != 1 || !cert.equals(trust.getTrustedCert())) {
                                        throw new CertPathValidatorException("Version 1 certificates can't be used as CA ones.", null, certPath, index);
                                    }
                                } else {
                                    RFC3280CertPathUtilities.prepareNextCertA(certPath, index);
                                    validPolicyTree = RFC3280CertPathUtilities.prepareCertB(certPath, index, policyNodes, validPolicyTree, policyMapping);
                                    RFC3280CertPathUtilities.prepareNextCertG(certPath, index, nameConstraintValidator);
                                    int explicitPolicy2 = RFC3280CertPathUtilities.prepareNextCertH1(certPath, index, explicitPolicy);
                                    int policyMapping2 = RFC3280CertPathUtilities.prepareNextCertH2(certPath, index, policyMapping);
                                    int inhibitAnyPolicy2 = RFC3280CertPathUtilities.prepareNextCertH3(certPath, index, inhibitAnyPolicy);
                                    explicitPolicy = RFC3280CertPathUtilities.prepareNextCertI1(certPath, index, explicitPolicy2);
                                    policyMapping = RFC3280CertPathUtilities.prepareNextCertI2(certPath, index, policyMapping2);
                                    inhibitAnyPolicy = RFC3280CertPathUtilities.prepareNextCertJ(certPath, index, inhibitAnyPolicy2);
                                    RFC3280CertPathUtilities.prepareNextCertK(certPath, index);
                                    maxPathLength = RFC3280CertPathUtilities.prepareNextCertM(certPath, index, RFC3280CertPathUtilities.prepareNextCertL(certPath, index, maxPathLength));
                                    RFC3280CertPathUtilities.prepareNextCertN(certPath, index);
                                    Set<String> criticalExtensionOIDs = cert.getCriticalExtensionOIDs();
                                    if (criticalExtensionOIDs != null) {
                                        criticalExtensions2 = new HashSet(criticalExtensionOIDs);
                                        criticalExtensions2.remove(RFC3280CertPathUtilities.KEY_USAGE);
                                        criticalExtensions2.remove(RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
                                        criticalExtensions2.remove(RFC3280CertPathUtilities.POLICY_MAPPINGS);
                                        criticalExtensions2.remove(RFC3280CertPathUtilities.INHIBIT_ANY_POLICY);
                                        criticalExtensions2.remove(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT);
                                        criticalExtensions2.remove(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
                                        criticalExtensions2.remove(RFC3280CertPathUtilities.POLICY_CONSTRAINTS);
                                        criticalExtensions2.remove(RFC3280CertPathUtilities.BASIC_CONSTRAINTS);
                                        criticalExtensions2.remove(RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME);
                                        criticalExtensions2.remove(RFC3280CertPathUtilities.NAME_CONSTRAINTS);
                                    } else {
                                        criticalExtensions2 = new HashSet();
                                    }
                                    RFC3280CertPathUtilities.prepareNextCertO(certPath, index, criticalExtensions2, pathCheckers);
                                    sign = cert;
                                    workingIssuerName = PrincipalUtils.getSubjectPrincipal(sign);
                                    try {
                                        workingPublicKey = CertPathValidatorUtilities.getNextWorkingKey(certPath.getCertificates(), index, this.helper);
                                        AlgorithmIdentifier workingAlgId2 = CertPathValidatorUtilities.getAlgorithmIdentifier(workingPublicKey);
                                        workingAlgId2.getAlgorithm();
                                        workingAlgId2.getParameters();
                                    } catch (CertPathValidatorException e) {
                                        throw new CertPathValidatorException("Next working key could not be retrieved.", e, certPath, index);
                                    }
                                }
                            }
                            index--;
                        } catch (AnnotatedException e2) {
                            throw new CertPathValidatorException(e2.getMessage(), e2.getUnderlyingException(), certPath, index);
                        }
                    }
                    int explicitPolicy3 = RFC3280CertPathUtilities.wrapupCertB(certPath, index + 1, RFC3280CertPathUtilities.wrapupCertA(explicitPolicy, cert));
                    Set<String> criticalExtensionOIDs2 = cert.getCriticalExtensionOIDs();
                    if (criticalExtensionOIDs2 != null) {
                        criticalExtensions = new HashSet(criticalExtensionOIDs2);
                        criticalExtensions.remove(RFC3280CertPathUtilities.KEY_USAGE);
                        criticalExtensions.remove(RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
                        criticalExtensions.remove(RFC3280CertPathUtilities.POLICY_MAPPINGS);
                        criticalExtensions.remove(RFC3280CertPathUtilities.INHIBIT_ANY_POLICY);
                        criticalExtensions.remove(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT);
                        criticalExtensions.remove(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
                        criticalExtensions.remove(RFC3280CertPathUtilities.POLICY_CONSTRAINTS);
                        criticalExtensions.remove(RFC3280CertPathUtilities.BASIC_CONSTRAINTS);
                        criticalExtensions.remove(RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME);
                        criticalExtensions.remove(RFC3280CertPathUtilities.NAME_CONSTRAINTS);
                        criticalExtensions.remove(RFC3280CertPathUtilities.CRL_DISTRIBUTION_POINTS);
                        criticalExtensions.remove(Extension.extendedKeyUsage.getId());
                    } else {
                        criticalExtensions = new HashSet();
                    }
                    RFC3280CertPathUtilities.wrapupCertF(certPath, index + 1, pathCheckers, criticalExtensions);
                    PKIXPolicyNode intersection = RFC3280CertPathUtilities.wrapupCertG(certPath, paramsPKIX2, userInitialPolicySet, index + 1, policyNodes, validPolicyTree, acceptablePolicies);
                    if (explicitPolicy3 > 0 || intersection != null) {
                        return new PKIXCertPathValidatorResult(trust, intersection, cert.getPublicKey());
                    }
                    throw new CertPathValidatorException("Path processing failed on policy.", null, certPath, index);
                } catch (CertPathValidatorException e3) {
                    throw new ExtCertPathValidatorException("Algorithm identifier of public key of trust anchor could not be read.", e3, certPath, -1);
                }
            } catch (IllegalArgumentException ex) {
                throw new ExtCertPathValidatorException("Subject of trust anchor could not be (re)encoded.", ex, certPath, -1);
            }
        } catch (AnnotatedException e4) {
            throw new CertPathValidatorException(e4.getMessage(), e4.getUnderlyingException(), certPath, certificates.size() - 1);
        }
    }

    static void checkCertificate(X509Certificate cert) throws AnnotatedException {
        try {
            TBSCertificate.getInstance(cert.getTBSCertificate());
        } catch (IllegalArgumentException e) {
            throw new AnnotatedException(e.getMessage());
        } catch (CertificateEncodingException e2) {
            throw new AnnotatedException("unable to process TBSCertificate");
        }
    }
}
