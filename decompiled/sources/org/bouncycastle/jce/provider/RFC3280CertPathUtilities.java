package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderSpi;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.NameConstraints;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.jcajce.PKIXCertRevocationChecker;
import org.bouncycastle.jcajce.PKIXCertRevocationCheckerParameters;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/provider/RFC3280CertPathUtilities.class */
class RFC3280CertPathUtilities {
    public static final String ANY_POLICY = "2.5.29.32.0";
    protected static final int KEY_CERT_SIGN = 5;
    protected static final int CRL_SIGN = 6;
    private static final Class revChkClass = ClassUtil.loadClass(RFC3280CertPathUtilities.class, "java.security.cert.PKIXRevocationChecker");
    public static final String CERTIFICATE_POLICIES = Extension.certificatePolicies.getId();
    public static final String POLICY_MAPPINGS = Extension.policyMappings.getId();
    public static final String INHIBIT_ANY_POLICY = Extension.inhibitAnyPolicy.getId();
    public static final String ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();
    public static final String FRESHEST_CRL = Extension.freshestCRL.getId();
    public static final String DELTA_CRL_INDICATOR = Extension.deltaCRLIndicator.getId();
    public static final String POLICY_CONSTRAINTS = Extension.policyConstraints.getId();
    public static final String BASIC_CONSTRAINTS = Extension.basicConstraints.getId();
    public static final String CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
    public static final String SUBJECT_ALTERNATIVE_NAME = Extension.subjectAlternativeName.getId();
    public static final String NAME_CONSTRAINTS = Extension.nameConstraints.getId();
    public static final String AUTHORITY_KEY_IDENTIFIER = Extension.authorityKeyIdentifier.getId();
    public static final String KEY_USAGE = Extension.keyUsage.getId();
    public static final String CRL_NUMBER = Extension.cRLNumber.getId();
    protected static final String[] crlReasons = {"unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", "unknown", "removeFromCRL", "privilegeWithdrawn", "aACompromise"};

    RFC3280CertPathUtilities() {
    }

    protected static void processCRLB2(DistributionPoint distributionPoint, Object obj, X509CRL x509crl) throws AnnotatedException {
        try {
            IssuingDistributionPoint issuingDistributionPoint = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509crl, ISSUING_DISTRIBUTION_POINT));
            if (issuingDistributionPoint != null) {
                if (issuingDistributionPoint.getDistributionPoint() != null) {
                    DistributionPointName distributionPoint2 = IssuingDistributionPoint.getInstance(issuingDistributionPoint).getDistributionPoint();
                    ArrayList arrayList = new ArrayList();
                    if (distributionPoint2.getType() == 0) {
                        for (GeneralName generalName : GeneralNames.getInstance(distributionPoint2.getName()).getNames()) {
                            arrayList.add(generalName);
                        }
                    }
                    if (distributionPoint2.getType() == 1) {
                        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                        try {
                            Enumeration objects = ASN1Sequence.getInstance(PrincipalUtils.getIssuerPrincipal(x509crl)).getObjects();
                            while (objects.hasMoreElements()) {
                                aSN1EncodableVector.add((ASN1Encodable) objects.nextElement());
                            }
                            aSN1EncodableVector.add(distributionPoint2.getName());
                            arrayList.add(new GeneralName(X500Name.getInstance(new DERSequence(aSN1EncodableVector))));
                        } catch (Exception e) {
                            throw new AnnotatedException("Could not read CRL issuer.", e);
                        }
                    }
                    boolean z = false;
                    if (distributionPoint.getDistributionPoint() != null) {
                        DistributionPointName distributionPoint3 = distributionPoint.getDistributionPoint();
                        GeneralName[] names = distributionPoint3.getType() == 0 ? GeneralNames.getInstance(distributionPoint3.getName()).getNames() : null;
                        if (distributionPoint3.getType() == 1) {
                            if (distributionPoint.getCRLIssuer() != null) {
                                names = distributionPoint.getCRLIssuer().getNames();
                            } else {
                                names = new GeneralName[1];
                                try {
                                    names[0] = new GeneralName(PrincipalUtils.getEncodedIssuerPrincipal(obj));
                                } catch (Exception e2) {
                                    throw new AnnotatedException("Could not read certificate issuer.", e2);
                                }
                            }
                            for (int i = 0; i < names.length; i++) {
                                Enumeration objects2 = ASN1Sequence.getInstance(names[i].getName().toASN1Primitive()).getObjects();
                                ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
                                while (objects2.hasMoreElements()) {
                                    aSN1EncodableVector2.add((ASN1Encodable) objects2.nextElement());
                                }
                                aSN1EncodableVector2.add(distributionPoint3.getName());
                                names[i] = new GeneralName(X500Name.getInstance(new DERSequence(aSN1EncodableVector2)));
                            }
                        }
                        if (names != null) {
                            for (GeneralName generalName2 : names) {
                                if (arrayList.contains(generalName2)) {
                                    z = true;
                                    break;
                                }
                            }
                        }
                        if (!z) {
                            throw new AnnotatedException("No match for certificate CRL issuing distribution point name to cRLIssuer CRL distribution point.");
                        }
                    } else {
                        if (distributionPoint.getCRLIssuer() == null) {
                            throw new AnnotatedException("Either the cRLIssuer or the distributionPoint field must be contained in DistributionPoint.");
                        }
                        for (GeneralName generalName3 : distributionPoint.getCRLIssuer().getNames()) {
                            if (arrayList.contains(generalName3)) {
                                z = true;
                                break;
                            }
                        }
                        if (!z) {
                            throw new AnnotatedException("No match for certificate CRL issuing distribution point name to cRLIssuer CRL distribution point.");
                        }
                    }
                }
                try {
                    BasicConstraints basicConstraints = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Extension) obj, BASIC_CONSTRAINTS));
                    if (obj instanceof X509Certificate) {
                        if (issuingDistributionPoint.onlyContainsUserCerts() && basicConstraints != null && basicConstraints.isCA()) {
                            throw new AnnotatedException("CA Cert CRL only contains user certificates.");
                        }
                        if (issuingDistributionPoint.onlyContainsCACerts() && (basicConstraints == null || !basicConstraints.isCA())) {
                            throw new AnnotatedException("End CRL only contains CA certificates.");
                        }
                    }
                    if (issuingDistributionPoint.onlyContainsAttributeCerts()) {
                        throw new AnnotatedException("onlyContainsAttributeCerts boolean is asserted.");
                    }
                } catch (Exception e3) {
                    throw new AnnotatedException("Basic constraints extension could not be decoded.", e3);
                }
            }
        } catch (Exception e4) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", e4);
        }
    }

    protected static void processCRLB1(DistributionPoint distributionPoint, Object obj, X509CRL x509crl) throws AnnotatedException {
        ASN1Primitive extensionValue = CertPathValidatorUtilities.getExtensionValue(x509crl, ISSUING_DISTRIBUTION_POINT);
        boolean z = false;
        if (extensionValue != null && IssuingDistributionPoint.getInstance(extensionValue).isIndirectCRL()) {
            z = true;
        }
        try {
            byte[] encoded = PrincipalUtils.getIssuerPrincipal(x509crl).getEncoded();
            boolean z2 = false;
            if (distributionPoint.getCRLIssuer() != null) {
                GeneralName[] names = distributionPoint.getCRLIssuer().getNames();
                for (int i = 0; i < names.length; i++) {
                    if (names[i].getTagNo() == 4) {
                        try {
                            if (Arrays.areEqual(names[i].getName().toASN1Primitive().getEncoded(), encoded)) {
                                z2 = true;
                            }
                        } catch (IOException e) {
                            throw new AnnotatedException("CRL issuer information from distribution point cannot be decoded.", e);
                        }
                    }
                }
                if (z2 && !z) {
                    throw new AnnotatedException("Distribution point contains cRLIssuer field but CRL is not indirect.");
                }
                if (!z2) {
                    throw new AnnotatedException("CRL issuer of CRL does not match CRL issuer of distribution point.");
                }
            } else if (PrincipalUtils.getIssuerPrincipal(x509crl).equals(PrincipalUtils.getEncodedIssuerPrincipal(obj))) {
                z2 = true;
            }
            if (!z2) {
                throw new AnnotatedException("Cannot find matching CRL issuer for certificate.");
            }
        } catch (IOException e2) {
            throw new AnnotatedException("Exception encoding CRL issuer: " + e2.getMessage(), e2);
        }
    }

    protected static ReasonsMask processCRLD(X509CRL x509crl, DistributionPoint distributionPoint) throws AnnotatedException {
        try {
            IssuingDistributionPoint issuingDistributionPoint = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509crl, ISSUING_DISTRIBUTION_POINT));
            if (issuingDistributionPoint != null && issuingDistributionPoint.getOnlySomeReasons() != null && distributionPoint.getReasons() != null) {
                return new ReasonsMask(distributionPoint.getReasons()).intersect(new ReasonsMask(issuingDistributionPoint.getOnlySomeReasons()));
            }
            if ((issuingDistributionPoint == null || issuingDistributionPoint.getOnlySomeReasons() == null) && distributionPoint.getReasons() == null) {
                return ReasonsMask.allReasons;
            }
            return (distributionPoint.getReasons() == null ? ReasonsMask.allReasons : new ReasonsMask(distributionPoint.getReasons())).intersect(issuingDistributionPoint == null ? ReasonsMask.allReasons : new ReasonsMask(issuingDistributionPoint.getOnlySomeReasons()));
        } catch (Exception e) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", e);
        }
    }

    protected static Set processCRLF(X509CRL x509crl, Object obj, X509Certificate x509Certificate, PublicKey publicKey, PKIXExtendedParameters pKIXExtendedParameters, List list, JcaJceHelper jcaJceHelper) throws AnnotatedException {
        X509CertSelector x509CertSelector = new X509CertSelector();
        try {
            x509CertSelector.setSubject(PrincipalUtils.getIssuerPrincipal(x509crl).getEncoded());
            PKIXCertStoreSelector<? extends Certificate> pKIXCertStoreSelectorBuild = new PKIXCertStoreSelector.Builder(x509CertSelector).build();
            LinkedHashSet<X509Certificate> linkedHashSet = new LinkedHashSet();
            try {
                CertPathValidatorUtilities.findCertificates(linkedHashSet, pKIXCertStoreSelectorBuild, pKIXExtendedParameters.getCertificateStores());
                CertPathValidatorUtilities.findCertificates(linkedHashSet, pKIXCertStoreSelectorBuild, pKIXExtendedParameters.getCertStores());
                linkedHashSet.add(x509Certificate);
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                for (X509Certificate x509Certificate2 : linkedHashSet) {
                    if (x509Certificate2.equals(x509Certificate)) {
                        arrayList.add(x509Certificate2);
                        arrayList2.add(publicKey);
                    } else {
                        try {
                            CertPathBuilderSpi pKIXCertPathBuilderSpi_8 = revChkClass != null ? new PKIXCertPathBuilderSpi_8(true) : new PKIXCertPathBuilderSpi(true);
                            X509CertSelector x509CertSelector2 = new X509CertSelector();
                            x509CertSelector2.setCertificate(x509Certificate2);
                            PKIXExtendedParameters.Builder targetConstraints = new PKIXExtendedParameters.Builder(pKIXExtendedParameters).setTargetConstraints(new PKIXCertStoreSelector.Builder(x509CertSelector2).build());
                            if (list.contains(x509Certificate2)) {
                                targetConstraints.setRevocationEnabled(false);
                            } else {
                                targetConstraints.setRevocationEnabled(true);
                            }
                            List<? extends Certificate> certificates = pKIXCertPathBuilderSpi_8.engineBuild(new PKIXExtendedBuilderParameters.Builder(targetConstraints.build()).build()).getCertPath().getCertificates();
                            arrayList.add(x509Certificate2);
                            arrayList2.add(CertPathValidatorUtilities.getNextWorkingKey(certificates, 0, jcaJceHelper));
                        } catch (CertPathBuilderException e) {
                            throw new AnnotatedException("CertPath for CRL signer failed to validate.", e);
                        } catch (CertPathValidatorException e2) {
                            throw new AnnotatedException("Public key of issuer certificate of CRL could not be retrieved.", e2);
                        } catch (Exception e3) {
                            throw new AnnotatedException(e3.getMessage());
                        }
                    }
                }
                HashSet hashSet = new HashSet();
                AnnotatedException annotatedException = null;
                for (int i = 0; i < arrayList.size(); i++) {
                    boolean[] keyUsage = ((X509Certificate) arrayList.get(i)).getKeyUsage();
                    if (keyUsage == null) {
                        if (Properties.isOverrideSet("org.bouncycastle.x509.allow_ca_without_crl_sign", true)) {
                            hashSet.add(arrayList2.get(i));
                        } else {
                            annotatedException = new AnnotatedException("No key usage extension on CRL issuer certificate.");
                        }
                    } else if (keyUsage.length <= 6 || !keyUsage[6]) {
                        annotatedException = new AnnotatedException("Issuer certificate key usage extension does not permit CRL signing.");
                    } else {
                        hashSet.add(arrayList2.get(i));
                    }
                }
                if (hashSet.isEmpty() && annotatedException == null) {
                    throw new AnnotatedException("Cannot find a valid issuer certificate.");
                }
                if (!hashSet.isEmpty() || annotatedException == null) {
                    return hashSet;
                }
                throw annotatedException;
            } catch (AnnotatedException e4) {
                throw new AnnotatedException("Issuer certificate for CRL cannot be searched.", e4);
            }
        } catch (IOException e5) {
            throw new AnnotatedException("Subject criteria for certificate selector to find issuer certificate for CRL could not be set.", e5);
        }
    }

    protected static PublicKey processCRLG(X509CRL x509crl, Set set) throws AnnotatedException {
        Exception exc = null;
        Iterator it = set.iterator();
        while (it.hasNext()) {
            PublicKey publicKey = (PublicKey) it.next();
            try {
                x509crl.verify(publicKey);
                return publicKey;
            } catch (Exception e) {
                exc = e;
            }
        }
        throw new AnnotatedException("Cannot verify CRL.", exc);
    }

    protected static X509CRL processCRLH(Set set, PublicKey publicKey) throws AnnotatedException {
        Exception exc = null;
        Iterator it = set.iterator();
        while (it.hasNext()) {
            X509CRL x509crl = (X509CRL) it.next();
            try {
                x509crl.verify(publicKey);
                return x509crl;
            } catch (Exception e) {
                exc = e;
            }
        }
        if (exc != null) {
            throw new AnnotatedException("Cannot verify delta CRL.", exc);
        }
        return null;
    }

    protected static void processCRLC(X509CRL x509crl, X509CRL x509crl2, PKIXExtendedParameters pKIXExtendedParameters) throws AnnotatedException {
        if (x509crl == null) {
            return;
        }
        if (x509crl.hasUnsupportedCriticalExtension()) {
            throw new AnnotatedException("delta CRL has unsupported critical extensions");
        }
        try {
            IssuingDistributionPoint issuingDistributionPoint = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509crl2, ISSUING_DISTRIBUTION_POINT));
            if (pKIXExtendedParameters.isUseDeltasEnabled()) {
                if (!PrincipalUtils.getIssuerPrincipal(x509crl).equals(PrincipalUtils.getIssuerPrincipal(x509crl2))) {
                    throw new AnnotatedException("Complete CRL issuer does not match delta CRL issuer.");
                }
                try {
                    IssuingDistributionPoint issuingDistributionPoint2 = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509crl, ISSUING_DISTRIBUTION_POINT));
                    boolean z = false;
                    if (issuingDistributionPoint == null) {
                        if (issuingDistributionPoint2 == null) {
                            z = true;
                        }
                    } else if (issuingDistributionPoint.equals(issuingDistributionPoint2)) {
                        z = true;
                    }
                    if (!z) {
                        throw new AnnotatedException("Issuing distribution point extension from delta CRL and complete CRL does not match.");
                    }
                    try {
                        ASN1Primitive extensionValue = CertPathValidatorUtilities.getExtensionValue(x509crl2, AUTHORITY_KEY_IDENTIFIER);
                        try {
                            ASN1Primitive extensionValue2 = CertPathValidatorUtilities.getExtensionValue(x509crl, AUTHORITY_KEY_IDENTIFIER);
                            if (extensionValue == null) {
                                throw new AnnotatedException("CRL authority key identifier is null.");
                            }
                            if (extensionValue2 == null) {
                                throw new AnnotatedException("Delta CRL authority key identifier is null.");
                            }
                            if (!extensionValue.equals(extensionValue2)) {
                                throw new AnnotatedException("Delta CRL authority key identifier does not match complete CRL authority key identifier.");
                            }
                        } catch (AnnotatedException e) {
                            throw new AnnotatedException("Authority key identifier extension could not be extracted from delta CRL.", e);
                        }
                    } catch (AnnotatedException e2) {
                        throw new AnnotatedException("Authority key identifier extension could not be extracted from complete CRL.", e2);
                    }
                } catch (Exception e3) {
                    throw new AnnotatedException("Issuing distribution point extension from delta CRL could not be decoded.", e3);
                }
            }
        } catch (Exception e4) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", e4);
        }
    }

    protected static void processCRLI(Date date, X509CRL x509crl, Object obj, CertStatus certStatus, PKIXExtendedParameters pKIXExtendedParameters) throws AnnotatedException {
        if (!pKIXExtendedParameters.isUseDeltasEnabled() || x509crl == null) {
            return;
        }
        CertPathValidatorUtilities.getCertStatus(date, x509crl, obj, certStatus);
    }

    protected static void processCRLJ(Date date, X509CRL x509crl, Object obj, CertStatus certStatus) throws AnnotatedException {
        if (certStatus.getCertStatus() == 11) {
            CertPathValidatorUtilities.getCertStatus(date, x509crl, obj, certStatus);
        }
    }

    protected static PKIXPolicyNode prepareCertB(CertPath certPath, int i, List[] listArr, PKIXPolicyNode pKIXPolicyNode, int i2) throws CertPathValidatorException {
        List<? extends Certificate> certificates = certPath.getCertificates();
        X509Certificate x509Certificate = (X509Certificate) certificates.get(i);
        int size = certificates.size() - i;
        try {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, POLICY_MAPPINGS));
            PKIXPolicyNode pKIXPolicyNodeRemovePolicyNode = pKIXPolicyNode;
            if (aSN1Sequence != null) {
                HashMap map = new HashMap();
                HashSet<String> hashSet = new HashSet();
                for (int i3 = 0; i3 < aSN1Sequence.size(); i3++) {
                    ASN1Sequence aSN1Sequence2 = (ASN1Sequence) aSN1Sequence.getObjectAt(i3);
                    String id = ((ASN1ObjectIdentifier) aSN1Sequence2.getObjectAt(0)).getId();
                    String id2 = ((ASN1ObjectIdentifier) aSN1Sequence2.getObjectAt(1)).getId();
                    if (map.containsKey(id)) {
                        ((Set) map.get(id)).add(id2);
                    } else {
                        HashSet hashSet2 = new HashSet();
                        hashSet2.add(id2);
                        map.put(id, hashSet2);
                        hashSet.add(id);
                    }
                }
                for (String str : hashSet) {
                    if (i2 > 0) {
                        boolean z = false;
                        for (PKIXPolicyNode pKIXPolicyNode2 : listArr[size]) {
                            if (pKIXPolicyNode2.getValidPolicy().equals(str)) {
                                z = true;
                                pKIXPolicyNode2.expectedPolicies = (Set) map.get(str);
                                break;
                            }
                        }
                        if (z) {
                            continue;
                        } else {
                            for (PKIXPolicyNode pKIXPolicyNode3 : listArr[size]) {
                                if ("2.5.29.32.0".equals(pKIXPolicyNode3.getValidPolicy())) {
                                    Set qualifierSet = null;
                                    try {
                                        Enumeration objects = ((ASN1Sequence) CertPathValidatorUtilities.getExtensionValue(x509Certificate, CERTIFICATE_POLICIES)).getObjects();
                                        while (objects.hasMoreElements()) {
                                            try {
                                                PolicyInformation policyInformation = PolicyInformation.getInstance(objects.nextElement());
                                                if ("2.5.29.32.0".equals(policyInformation.getPolicyIdentifier().getId())) {
                                                    try {
                                                        qualifierSet = CertPathValidatorUtilities.getQualifierSet(policyInformation.getPolicyQualifiers());
                                                        break;
                                                    } catch (CertPathValidatorException e) {
                                                        throw new ExtCertPathValidatorException("Policy qualifier info set could not be decoded.", e, certPath, i);
                                                    }
                                                }
                                            } catch (Exception e2) {
                                                throw new CertPathValidatorException("Policy information could not be decoded.", e2, certPath, i);
                                            }
                                        }
                                        boolean zContains = x509Certificate.getCriticalExtensionOIDs() != null ? x509Certificate.getCriticalExtensionOIDs().contains(CERTIFICATE_POLICIES) : false;
                                        PKIXPolicyNode pKIXPolicyNode4 = (PKIXPolicyNode) pKIXPolicyNode3.getParent();
                                        if (!"2.5.29.32.0".equals(pKIXPolicyNode4.getValidPolicy())) {
                                            break;
                                        }
                                        PKIXPolicyNode pKIXPolicyNode5 = new PKIXPolicyNode(new ArrayList(), size, (Set) map.get(str), pKIXPolicyNode4, qualifierSet, str, zContains);
                                        pKIXPolicyNode4.addChild(pKIXPolicyNode5);
                                        listArr[size].add(pKIXPolicyNode5);
                                        break;
                                    } catch (AnnotatedException e3) {
                                        throw new ExtCertPathValidatorException("Certificate policies extension could not be decoded.", e3, certPath, i);
                                    }
                                }
                            }
                        }
                    } else if (i2 <= 0) {
                        Iterator it = listArr[size].iterator();
                        while (it.hasNext()) {
                            PKIXPolicyNode pKIXPolicyNode6 = (PKIXPolicyNode) it.next();
                            if (pKIXPolicyNode6.getValidPolicy().equals(str)) {
                                ((PKIXPolicyNode) pKIXPolicyNode6.getParent()).removeChild(pKIXPolicyNode6);
                                it.remove();
                                for (int i4 = size - 1; i4 >= 0; i4--) {
                                    List list = listArr[i4];
                                    for (int i5 = 0; i5 < list.size(); i5++) {
                                        PKIXPolicyNode pKIXPolicyNode7 = (PKIXPolicyNode) list.get(i5);
                                        if (!pKIXPolicyNode7.hasChildren()) {
                                            pKIXPolicyNodeRemovePolicyNode = CertPathValidatorUtilities.removePolicyNode(pKIXPolicyNodeRemovePolicyNode, listArr, pKIXPolicyNode7);
                                            if (pKIXPolicyNodeRemovePolicyNode == null) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return pKIXPolicyNodeRemovePolicyNode;
        } catch (AnnotatedException e4) {
            throw new ExtCertPathValidatorException("Policy mappings extension could not be decoded.", e4, certPath, i);
        }
    }

    protected static void prepareNextCertA(CertPath certPath, int i) throws CertPathValidatorException {
        try {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), POLICY_MAPPINGS));
            if (aSN1Sequence != null) {
                for (int i2 = 0; i2 < aSN1Sequence.size(); i2++) {
                    try {
                        ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(i2));
                        ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(aSN1Sequence2.getObjectAt(0));
                        ASN1ObjectIdentifier aSN1ObjectIdentifier2 = ASN1ObjectIdentifier.getInstance(aSN1Sequence2.getObjectAt(1));
                        if ("2.5.29.32.0".equals(aSN1ObjectIdentifier.getId())) {
                            throw new CertPathValidatorException("IssuerDomainPolicy is anyPolicy", null, certPath, i);
                        }
                        if ("2.5.29.32.0".equals(aSN1ObjectIdentifier2.getId())) {
                            throw new CertPathValidatorException("SubjectDomainPolicy is anyPolicy", null, certPath, i);
                        }
                    } catch (Exception e) {
                        throw new ExtCertPathValidatorException("Policy mappings extension contents could not be decoded.", e, certPath, i);
                    }
                }
            }
        } catch (AnnotatedException e2) {
            throw new ExtCertPathValidatorException("Policy mappings extension could not be decoded.", e2, certPath, i);
        }
    }

    protected static void processCertF(CertPath certPath, int i, PKIXPolicyNode pKIXPolicyNode, int i2) throws CertPathValidatorException {
        if (i2 <= 0 && pKIXPolicyNode == null) {
            throw new ExtCertPathValidatorException("No valid policy tree found when one expected.", null, certPath, i);
        }
    }

    protected static PKIXPolicyNode processCertE(CertPath certPath, int i, PKIXPolicyNode pKIXPolicyNode) throws CertPathValidatorException {
        try {
            if (ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), CERTIFICATE_POLICIES)) == null) {
                pKIXPolicyNode = null;
            }
            return pKIXPolicyNode;
        } catch (AnnotatedException e) {
            throw new ExtCertPathValidatorException("Could not read certificate policies extension from certificate.", e, certPath, i);
        }
    }

    protected static void processCertBC(CertPath certPath, int i, PKIXNameConstraintValidator pKIXNameConstraintValidator, boolean z) throws CertPathValidatorException {
        List<? extends Certificate> certificates = certPath.getCertificates();
        X509Certificate x509Certificate = (X509Certificate) certificates.get(i);
        int size = certificates.size();
        int i2 = size - i;
        if (!CertPathValidatorUtilities.isSelfIssued(x509Certificate) || (i2 >= size && !z)) {
            try {
                ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(PrincipalUtils.getSubjectPrincipal(x509Certificate));
                try {
                    pKIXNameConstraintValidator.checkPermittedDN(aSN1Sequence);
                    pKIXNameConstraintValidator.checkExcludedDN(aSN1Sequence);
                    try {
                        GeneralNames generalNames = GeneralNames.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, SUBJECT_ALTERNATIVE_NAME));
                        RDN[] rDNs = X500Name.getInstance(aSN1Sequence).getRDNs(BCStyle.EmailAddress);
                        for (int i3 = 0; i3 != rDNs.length; i3++) {
                            GeneralName generalName = new GeneralName(1, ((ASN1String) rDNs[i3].getFirst().getValue()).getString());
                            try {
                                pKIXNameConstraintValidator.checkPermitted(generalName);
                                pKIXNameConstraintValidator.checkExcluded(generalName);
                            } catch (PKIXNameConstraintValidatorException e) {
                                throw new CertPathValidatorException("Subtree check for certificate subject alternative email failed.", e, certPath, i);
                            }
                        }
                        if (generalNames != null) {
                            try {
                                GeneralName[] names = generalNames.getNames();
                                for (int i4 = 0; i4 < names.length; i4++) {
                                    try {
                                        pKIXNameConstraintValidator.checkPermitted(names[i4]);
                                        pKIXNameConstraintValidator.checkExcluded(names[i4]);
                                    } catch (PKIXNameConstraintValidatorException e2) {
                                        throw new CertPathValidatorException("Subtree check for certificate subject alternative name failed.", e2, certPath, i);
                                    }
                                }
                            } catch (Exception e3) {
                                throw new CertPathValidatorException("Subject alternative name contents could not be decoded.", e3, certPath, i);
                            }
                        }
                    } catch (Exception e4) {
                        throw new CertPathValidatorException("Subject alternative name extension could not be decoded.", e4, certPath, i);
                    }
                } catch (PKIXNameConstraintValidatorException e5) {
                    throw new CertPathValidatorException("Subtree check for certificate subject failed.", e5, certPath, i);
                }
            } catch (Exception e6) {
                throw new CertPathValidatorException("Exception extracting subject name when checking subtrees.", e6, certPath, i);
            }
        }
    }

    protected static PKIXPolicyNode processCertD(CertPath certPath, int i, Set set, PKIXPolicyNode pKIXPolicyNode, List[] listArr, int i2, boolean z) throws CertPathValidatorException {
        String id;
        List<? extends Certificate> certificates = certPath.getCertificates();
        X509Certificate x509Certificate = (X509Certificate) certificates.get(i);
        int size = certificates.size();
        int i3 = size - i;
        try {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, CERTIFICATE_POLICIES));
            if (aSN1Sequence == null || pKIXPolicyNode == null) {
                return null;
            }
            Enumeration objects = aSN1Sequence.getObjects();
            HashSet hashSet = new HashSet();
            while (objects.hasMoreElements()) {
                PolicyInformation policyInformation = PolicyInformation.getInstance(objects.nextElement());
                ASN1ObjectIdentifier policyIdentifier = policyInformation.getPolicyIdentifier();
                hashSet.add(policyIdentifier.getId());
                if (!"2.5.29.32.0".equals(policyIdentifier.getId())) {
                    try {
                        Set qualifierSet = CertPathValidatorUtilities.getQualifierSet(policyInformation.getPolicyQualifiers());
                        if (!CertPathValidatorUtilities.processCertD1i(i3, listArr, policyIdentifier, qualifierSet)) {
                            CertPathValidatorUtilities.processCertD1ii(i3, listArr, policyIdentifier, qualifierSet);
                        }
                    } catch (CertPathValidatorException e) {
                        throw new ExtCertPathValidatorException("Policy qualifier info set could not be build.", e, certPath, i);
                    }
                }
            }
            if (set.isEmpty() || set.contains("2.5.29.32.0")) {
                set.clear();
                set.addAll(hashSet);
            } else {
                HashSet hashSet2 = new HashSet();
                for (Object obj : set) {
                    if (hashSet.contains(obj)) {
                        hashSet2.add(obj);
                    }
                }
                set.clear();
                set.addAll(hashSet2);
            }
            if (i2 > 0 || ((i3 < size || z) && CertPathValidatorUtilities.isSelfIssued(x509Certificate))) {
                Enumeration objects2 = aSN1Sequence.getObjects();
                while (objects2.hasMoreElements()) {
                    PolicyInformation policyInformation2 = PolicyInformation.getInstance(objects2.nextElement());
                    if ("2.5.29.32.0".equals(policyInformation2.getPolicyIdentifier().getId())) {
                        Set qualifierSet2 = CertPathValidatorUtilities.getQualifierSet(policyInformation2.getPolicyQualifiers());
                        List list = listArr[i3 - 1];
                        for (int i4 = 0; i4 < list.size(); i4++) {
                            PKIXPolicyNode pKIXPolicyNode2 = (PKIXPolicyNode) list.get(i4);
                            for (Object obj2 : pKIXPolicyNode2.getExpectedPolicies()) {
                                if (obj2 instanceof String) {
                                    id = (String) obj2;
                                } else if (obj2 instanceof ASN1ObjectIdentifier) {
                                    id = ((ASN1ObjectIdentifier) obj2).getId();
                                }
                                boolean z2 = false;
                                Iterator children = pKIXPolicyNode2.getChildren();
                                while (children.hasNext()) {
                                    if (id.equals(((PKIXPolicyNode) children.next()).getValidPolicy())) {
                                        z2 = true;
                                    }
                                }
                                if (!z2) {
                                    HashSet hashSet3 = new HashSet();
                                    hashSet3.add(id);
                                    PKIXPolicyNode pKIXPolicyNode3 = new PKIXPolicyNode(new ArrayList(), i3, hashSet3, pKIXPolicyNode2, qualifierSet2, id, false);
                                    pKIXPolicyNode2.addChild(pKIXPolicyNode3);
                                    listArr[i3].add(pKIXPolicyNode3);
                                }
                            }
                        }
                        break;
                    }
                }
            }
            PKIXPolicyNode pKIXPolicyNodeRemovePolicyNode = pKIXPolicyNode;
            for (int i5 = i3 - 1; i5 >= 0; i5--) {
                List list2 = listArr[i5];
                for (int i6 = 0; i6 < list2.size(); i6++) {
                    PKIXPolicyNode pKIXPolicyNode4 = (PKIXPolicyNode) list2.get(i6);
                    if (!pKIXPolicyNode4.hasChildren()) {
                        pKIXPolicyNodeRemovePolicyNode = CertPathValidatorUtilities.removePolicyNode(pKIXPolicyNodeRemovePolicyNode, listArr, pKIXPolicyNode4);
                        if (pKIXPolicyNodeRemovePolicyNode == null) {
                            break;
                        }
                    }
                }
            }
            Set<String> criticalExtensionOIDs = x509Certificate.getCriticalExtensionOIDs();
            if (criticalExtensionOIDs != null) {
                boolean zContains = criticalExtensionOIDs.contains(CERTIFICATE_POLICIES);
                List list3 = listArr[i3];
                for (int i7 = 0; i7 < list3.size(); i7++) {
                    ((PKIXPolicyNode) list3.get(i7)).setCritical(zContains);
                }
            }
            return pKIXPolicyNodeRemovePolicyNode;
        } catch (AnnotatedException e2) {
            throw new ExtCertPathValidatorException("Could not read certificate policies extension from certificate.", e2, certPath, i);
        }
    }

    protected static void processCertA(CertPath certPath, PKIXExtendedParameters pKIXExtendedParameters, Date date, PKIXCertRevocationChecker pKIXCertRevocationChecker, int i, PublicKey publicKey, boolean z, X500Name x500Name, X509Certificate x509Certificate) throws CertPathValidatorException {
        X509Certificate x509Certificate2 = (X509Certificate) certPath.getCertificates().get(i);
        if (!z) {
            try {
                CertPathValidatorUtilities.verifyX509Certificate(x509Certificate2, publicKey, pKIXExtendedParameters.getSigProvider());
            } catch (GeneralSecurityException e) {
                throw new ExtCertPathValidatorException("Could not validate certificate signature.", e, certPath, i);
            }
        }
        try {
            Date validCertDateFromValidityModel = CertPathValidatorUtilities.getValidCertDateFromValidityModel(date, pKIXExtendedParameters.getValidityModel(), certPath, i);
            try {
                x509Certificate2.checkValidity(validCertDateFromValidityModel);
                if (pKIXCertRevocationChecker != null) {
                    pKIXCertRevocationChecker.initialize(new PKIXCertRevocationCheckerParameters(pKIXExtendedParameters, validCertDateFromValidityModel, certPath, i, x509Certificate, publicKey));
                    pKIXCertRevocationChecker.check(x509Certificate2);
                }
                X500Name issuerPrincipal = PrincipalUtils.getIssuerPrincipal(x509Certificate2);
                if (!issuerPrincipal.equals(x500Name)) {
                    throw new ExtCertPathValidatorException("IssuerName(" + issuerPrincipal + ") does not match SubjectName(" + x500Name + ") of signing certificate.", null, certPath, i);
                }
            } catch (CertificateExpiredException e2) {
                throw new ExtCertPathValidatorException("Could not validate certificate: " + e2.getMessage(), e2, certPath, i);
            } catch (CertificateNotYetValidException e3) {
                throw new ExtCertPathValidatorException("Could not validate certificate: " + e3.getMessage(), e3, certPath, i);
            }
        } catch (AnnotatedException e4) {
            throw new ExtCertPathValidatorException("Could not validate time of certificate.", e4, certPath, i);
        }
    }

    protected static int prepareNextCertI1(CertPath certPath, int i, int i2) throws CertPathValidatorException {
        try {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), POLICY_CONSTRAINTS));
            if (aSN1Sequence != null) {
                Enumeration objects = aSN1Sequence.getObjects();
                while (objects.hasMoreElements()) {
                    try {
                        ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(objects.nextElement());
                        if (aSN1TaggedObject.getTagNo() == 0) {
                            int iIntValueExact = ASN1Integer.getInstance(aSN1TaggedObject, false).intValueExact();
                            if (iIntValueExact >= i2) {
                                break;
                            }
                            return iIntValueExact;
                        }
                    } catch (IllegalArgumentException e) {
                        throw new ExtCertPathValidatorException("Policy constraints extension contents cannot be decoded.", e, certPath, i);
                    }
                }
            }
            return i2;
        } catch (Exception e2) {
            throw new ExtCertPathValidatorException("Policy constraints extension cannot be decoded.", e2, certPath, i);
        }
    }

    protected static int prepareNextCertI2(CertPath certPath, int i, int i2) throws CertPathValidatorException {
        try {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), POLICY_CONSTRAINTS));
            if (aSN1Sequence != null) {
                Enumeration objects = aSN1Sequence.getObjects();
                while (objects.hasMoreElements()) {
                    try {
                        ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(objects.nextElement());
                        if (aSN1TaggedObject.getTagNo() == 1) {
                            int iIntValueExact = ASN1Integer.getInstance(aSN1TaggedObject, false).intValueExact();
                            if (iIntValueExact >= i2) {
                                break;
                            }
                            return iIntValueExact;
                        }
                    } catch (IllegalArgumentException e) {
                        throw new ExtCertPathValidatorException("Policy constraints extension contents cannot be decoded.", e, certPath, i);
                    }
                }
            }
            return i2;
        } catch (Exception e2) {
            throw new ExtCertPathValidatorException("Policy constraints extension cannot be decoded.", e2, certPath, i);
        }
    }

    protected static void prepareNextCertG(CertPath certPath, int i, PKIXNameConstraintValidator pKIXNameConstraintValidator) throws CertPathValidatorException {
        try {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), NAME_CONSTRAINTS));
            NameConstraints nameConstraints = aSN1Sequence != null ? NameConstraints.getInstance(aSN1Sequence) : null;
            if (nameConstraints != null) {
                GeneralSubtree[] permittedSubtrees = nameConstraints.getPermittedSubtrees();
                if (permittedSubtrees != null) {
                    try {
                        pKIXNameConstraintValidator.intersectPermittedSubtree(permittedSubtrees);
                    } catch (Exception e) {
                        throw new ExtCertPathValidatorException("Permitted subtrees cannot be build from name constraints extension.", e, certPath, i);
                    }
                }
                GeneralSubtree[] excludedSubtrees = nameConstraints.getExcludedSubtrees();
                if (excludedSubtrees != null) {
                    for (int i2 = 0; i2 != excludedSubtrees.length; i2++) {
                        try {
                            pKIXNameConstraintValidator.addExcludedSubtree(excludedSubtrees[i2]);
                        } catch (Exception e2) {
                            throw new ExtCertPathValidatorException("Excluded subtrees cannot be build from name constraints extension.", e2, certPath, i);
                        }
                    }
                }
            }
        } catch (Exception e3) {
            throw new ExtCertPathValidatorException("Name constraints extension could not be decoded.", e3, certPath, i);
        }
    }

    private static void checkCRL(PKIXCertRevocationCheckerParameters pKIXCertRevocationCheckerParameters, DistributionPoint distributionPoint, PKIXExtendedParameters pKIXExtendedParameters, Date date, Date date2, X509Certificate x509Certificate, X509Certificate x509Certificate2, PublicKey publicKey, CertStatus certStatus, ReasonsMask reasonsMask, List list, JcaJceHelper jcaJceHelper) throws AnnotatedException, RecoverableCertPathValidatorException {
        Set<String> criticalExtensionOIDs;
        if (date == null) {
        }
        if (date2.getTime() > date.getTime()) {
            throw new AnnotatedException("Validation time is in future.");
        }
        boolean z = false;
        AnnotatedException annotatedException = null;
        Iterator it = CertPathValidatorUtilities.getCompleteCRLs(pKIXCertRevocationCheckerParameters, distributionPoint, x509Certificate, pKIXExtendedParameters, date2).iterator();
        while (it.hasNext() && certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons()) {
            try {
                X509CRL x509crl = (X509CRL) it.next();
                ReasonsMask reasonsMaskProcessCRLD = processCRLD(x509crl, distributionPoint);
                if (reasonsMaskProcessCRLD.hasNewReasons(reasonsMask)) {
                    PublicKey publicKeyProcessCRLG = processCRLG(x509crl, processCRLF(x509crl, x509Certificate, x509Certificate2, publicKey, pKIXExtendedParameters, list, jcaJceHelper));
                    X509CRL x509crlProcessCRLH = null;
                    if (pKIXExtendedParameters.isUseDeltasEnabled()) {
                        x509crlProcessCRLH = processCRLH(CertPathValidatorUtilities.getDeltaCRLs(date2, x509crl, pKIXExtendedParameters.getCertStores(), pKIXExtendedParameters.getCRLStores(), jcaJceHelper), publicKeyProcessCRLG);
                    }
                    if (pKIXExtendedParameters.getValidityModel() != 1 && x509Certificate.getNotAfter().getTime() < x509crl.getThisUpdate().getTime()) {
                        throw new AnnotatedException("No valid CRL for current time found.");
                    }
                    processCRLB1(distributionPoint, x509Certificate, x509crl);
                    processCRLB2(distributionPoint, x509Certificate, x509crl);
                    processCRLC(x509crlProcessCRLH, x509crl, pKIXExtendedParameters);
                    processCRLI(date2, x509crlProcessCRLH, x509Certificate, certStatus, pKIXExtendedParameters);
                    processCRLJ(date2, x509crl, x509Certificate, certStatus);
                    if (certStatus.getCertStatus() == 8) {
                        certStatus.setCertStatus(11);
                    }
                    reasonsMask.addReasons(reasonsMaskProcessCRLD);
                    Set<String> criticalExtensionOIDs2 = x509crl.getCriticalExtensionOIDs();
                    if (criticalExtensionOIDs2 != null) {
                        HashSet hashSet = new HashSet(criticalExtensionOIDs2);
                        hashSet.remove(Extension.issuingDistributionPoint.getId());
                        hashSet.remove(Extension.deltaCRLIndicator.getId());
                        if (!hashSet.isEmpty()) {
                            throw new AnnotatedException("CRL contains unsupported critical extensions.");
                        }
                    }
                    if (x509crlProcessCRLH != null && (criticalExtensionOIDs = x509crlProcessCRLH.getCriticalExtensionOIDs()) != null) {
                        HashSet hashSet2 = new HashSet(criticalExtensionOIDs);
                        hashSet2.remove(Extension.issuingDistributionPoint.getId());
                        hashSet2.remove(Extension.deltaCRLIndicator.getId());
                        if (!hashSet2.isEmpty()) {
                            throw new AnnotatedException("Delta CRL contains unsupported critical extension.");
                        }
                    }
                    z = true;
                }
            } catch (AnnotatedException e) {
                annotatedException = e;
            }
        }
        if (!z) {
            throw annotatedException;
        }
    }

    protected static void checkCRLs(PKIXCertRevocationCheckerParameters pKIXCertRevocationCheckerParameters, PKIXExtendedParameters pKIXExtendedParameters, Date date, Date date2, X509Certificate x509Certificate, X509Certificate x509Certificate2, PublicKey publicKey, List list, JcaJceHelper jcaJceHelper) throws AnnotatedException, RecoverableCertPathValidatorException {
        AnnotatedException annotatedException = null;
        try {
            CRLDistPoint cRLDistPoint = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, CRL_DISTRIBUTION_POINTS));
            PKIXExtendedParameters.Builder builder = new PKIXExtendedParameters.Builder(pKIXExtendedParameters);
            try {
                Iterator<PKIXCRLStore> it = CertPathValidatorUtilities.getAdditionalStoresFromCRLDistributionPoint(cRLDistPoint, pKIXExtendedParameters.getNamedCRLStoreMap(), date2, jcaJceHelper).iterator();
                while (it.hasNext()) {
                    builder.addCRLStore(it.next());
                }
                CertStatus certStatus = new CertStatus();
                ReasonsMask reasonsMask = new ReasonsMask();
                PKIXExtendedParameters pKIXExtendedParametersBuild = builder.build();
                boolean z = false;
                if (cRLDistPoint != null) {
                    try {
                        DistributionPoint[] distributionPoints = cRLDistPoint.getDistributionPoints();
                        if (distributionPoints != null) {
                            for (int i = 0; i < distributionPoints.length && certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons(); i++) {
                                try {
                                    checkCRL(pKIXCertRevocationCheckerParameters, distributionPoints[i], pKIXExtendedParametersBuild, date, date2, x509Certificate, x509Certificate2, publicKey, certStatus, reasonsMask, list, jcaJceHelper);
                                    z = true;
                                } catch (AnnotatedException e) {
                                    annotatedException = e;
                                }
                            }
                        }
                    } catch (Exception e2) {
                        throw new AnnotatedException("Distribution points could not be read.", e2);
                    }
                }
                if (certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons()) {
                    try {
                        try {
                            checkCRL(pKIXCertRevocationCheckerParameters, new DistributionPoint(new DistributionPointName(0, new GeneralNames(new GeneralName(4, PrincipalUtils.getIssuerPrincipal(x509Certificate)))), null, null), (PKIXExtendedParameters) pKIXExtendedParameters.clone(), date, date2, x509Certificate, x509Certificate2, publicKey, certStatus, reasonsMask, list, jcaJceHelper);
                            z = true;
                        } catch (RuntimeException e3) {
                            throw new AnnotatedException("Issuer from certificate for CRL could not be reencoded.", e3);
                        }
                    } catch (AnnotatedException e4) {
                        annotatedException = e4;
                    }
                }
                if (!z) {
                    if (!(annotatedException instanceof AnnotatedException)) {
                        throw new AnnotatedException("No valid CRL found.", annotatedException);
                    }
                    throw annotatedException;
                }
                if (certStatus.getCertStatus() != 11) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    throw new AnnotatedException(("Certificate revocation after " + simpleDateFormat.format(certStatus.getRevocationDate())) + ", reason: " + crlReasons[certStatus.getCertStatus()]);
                }
                if (!reasonsMask.isAllReasons() && certStatus.getCertStatus() == 11) {
                    certStatus.setCertStatus(12);
                }
                if (certStatus.getCertStatus() == 12) {
                    throw new AnnotatedException("Certificate status could not be determined.");
                }
            } catch (AnnotatedException e5) {
                throw new AnnotatedException("No additional CRL locations could be decoded from CRL distribution point extension.", e5);
            }
        } catch (Exception e6) {
            throw new AnnotatedException("CRL distribution point extension could not be read.", e6);
        }
    }

    protected static int prepareNextCertJ(CertPath certPath, int i, int i2) throws CertPathValidatorException {
        int iIntValueExact;
        try {
            ASN1Integer aSN1Integer = ASN1Integer.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), INHIBIT_ANY_POLICY));
            return (aSN1Integer == null || (iIntValueExact = aSN1Integer.intValueExact()) >= i2) ? i2 : iIntValueExact;
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Inhibit any-policy extension cannot be decoded.", e, certPath, i);
        }
    }

    protected static void prepareNextCertK(CertPath certPath, int i) throws CertPathValidatorException {
        try {
            BasicConstraints basicConstraints = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), BASIC_CONSTRAINTS));
            if (basicConstraints == null) {
                throw new CertPathValidatorException("Intermediate certificate lacks BasicConstraints", null, certPath, i);
            }
            if (!basicConstraints.isCA()) {
                throw new CertPathValidatorException("Not a CA certificate", null, certPath, i);
            }
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Basic constraints extension cannot be decoded.", e, certPath, i);
        }
    }

    protected static int prepareNextCertL(CertPath certPath, int i, int i2) throws CertPathValidatorException {
        if (CertPathValidatorUtilities.isSelfIssued((X509Certificate) certPath.getCertificates().get(i))) {
            return i2;
        }
        if (i2 <= 0) {
            throw new ExtCertPathValidatorException("Max path length not greater than zero", null, certPath, i);
        }
        return i2 - 1;
    }

    protected static int prepareNextCertM(CertPath certPath, int i, int i2) throws CertPathValidatorException {
        ASN1Integer pathLenConstraintInteger;
        try {
            BasicConstraints basicConstraints = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), BASIC_CONSTRAINTS));
            if (basicConstraints != null && basicConstraints.isCA() && (pathLenConstraintInteger = basicConstraints.getPathLenConstraintInteger()) != null) {
                i2 = Math.min(i2, pathLenConstraintInteger.intPositiveValueExact());
            }
            return i2;
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Basic constraints extension cannot be decoded.", e, certPath, i);
        }
    }

    protected static void prepareNextCertN(CertPath certPath, int i) throws CertPathValidatorException {
        boolean[] keyUsage = ((X509Certificate) certPath.getCertificates().get(i)).getKeyUsage();
        if (keyUsage != null) {
            if (keyUsage.length <= 5 || !keyUsage[5]) {
                throw new ExtCertPathValidatorException("Issuer certificate keyusage extension is critical and does not permit key signing.", null, certPath, i);
            }
        }
    }

    protected static void prepareNextCertO(CertPath certPath, int i, Set set, List list) throws CertPathValidatorException {
        X509Certificate x509Certificate = (X509Certificate) certPath.getCertificates().get(i);
        Iterator it = list.iterator();
        while (it.hasNext()) {
            try {
                ((PKIXCertPathChecker) it.next()).check(x509Certificate, set);
            } catch (CertPathValidatorException e) {
                throw new CertPathValidatorException(e.getMessage(), e.getCause(), certPath, i);
            }
        }
        if (!set.isEmpty()) {
            throw new ExtCertPathValidatorException("Certificate has unsupported critical extension: " + set, null, certPath, i);
        }
    }

    protected static int prepareNextCertH1(CertPath certPath, int i, int i2) {
        return (CertPathValidatorUtilities.isSelfIssued((X509Certificate) certPath.getCertificates().get(i)) || i2 == 0) ? i2 : i2 - 1;
    }

    protected static int prepareNextCertH2(CertPath certPath, int i, int i2) {
        return (CertPathValidatorUtilities.isSelfIssued((X509Certificate) certPath.getCertificates().get(i)) || i2 == 0) ? i2 : i2 - 1;
    }

    protected static int prepareNextCertH3(CertPath certPath, int i, int i2) {
        return (CertPathValidatorUtilities.isSelfIssued((X509Certificate) certPath.getCertificates().get(i)) || i2 == 0) ? i2 : i2 - 1;
    }

    protected static int wrapupCertA(int i, X509Certificate x509Certificate) {
        if (!CertPathValidatorUtilities.isSelfIssued(x509Certificate) && i != 0) {
            i--;
        }
        return i;
    }

    protected static int wrapupCertB(CertPath certPath, int i, int i2) throws CertPathValidatorException {
        try {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Certificate) certPath.getCertificates().get(i), POLICY_CONSTRAINTS));
            if (aSN1Sequence != null) {
                Enumeration objects = aSN1Sequence.getObjects();
                while (objects.hasMoreElements()) {
                    ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) objects.nextElement();
                    switch (aSN1TaggedObject.getTagNo()) {
                        case 0:
                            try {
                                if (ASN1Integer.getInstance(aSN1TaggedObject, false).intValueExact() == 0) {
                                    return 0;
                                }
                            } catch (Exception e) {
                                throw new ExtCertPathValidatorException("Policy constraints requireExplicitPolicy field could not be decoded.", e, certPath, i);
                            }
                            break;
                    }
                }
            }
            return i2;
        } catch (AnnotatedException e2) {
            throw new ExtCertPathValidatorException("Policy constraints could not be decoded.", e2, certPath, i);
        }
    }

    protected static void wrapupCertF(CertPath certPath, int i, List list, Set set) throws CertPathValidatorException {
        X509Certificate x509Certificate = (X509Certificate) certPath.getCertificates().get(i);
        Iterator it = list.iterator();
        while (it.hasNext()) {
            try {
                ((PKIXCertPathChecker) it.next()).check(x509Certificate, set);
            } catch (CertPathValidatorException e) {
                throw new ExtCertPathValidatorException(e.getMessage(), e, certPath, i);
            } catch (Exception e2) {
                throw new CertPathValidatorException("Additional certificate path checker failed.", e2, certPath, i);
            }
        }
        if (!set.isEmpty()) {
            throw new ExtCertPathValidatorException("Certificate has unsupported critical extension: " + set, null, certPath, i);
        }
    }

    /* JADX WARN: Code duplicated, block: B:34:0x00d9  */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    protected static org.bouncycastle.jce.provider.PKIXPolicyNode wrapupCertG(java.security.cert.CertPath r7, org.bouncycastle.jcajce.PKIXExtendedParameters r8, java.util.Set r9, int r10, java.util.List[] r11, org.bouncycastle.jce.provider.PKIXPolicyNode r12, java.util.Set r13) throws java.security.cert.CertPathValidatorException {
        /*
            Method dump skipped, instruction units count: 632
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jce.provider.RFC3280CertPathUtilities.wrapupCertG(java.security.cert.CertPath, org.bouncycastle.jcajce.PKIXExtendedParameters, java.util.Set, int, java.util.List[], org.bouncycastle.jce.provider.PKIXPolicyNode, java.util.Set):org.bouncycastle.jce.provider.PKIXPolicyNode");
    }
}
