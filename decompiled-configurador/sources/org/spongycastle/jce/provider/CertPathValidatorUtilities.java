package org.spongycastle.jce.provider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.PolicyQualifierInfo;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Enumerated;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1OutputStream;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.isismtt.ISISMTTObjectIdentifiers;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x500.style.RFC4519Style;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.AuthorityKeyIdentifier;
import org.spongycastle.asn1.x509.CRLDistPoint;
import org.spongycastle.asn1.x509.DistributionPoint;
import org.spongycastle.asn1.x509.DistributionPointName;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.asn1.x509.GeneralNames;
import org.spongycastle.asn1.x509.PolicyInformation;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.jcajce.PKIXCRLStore;
import org.spongycastle.jcajce.PKIXCRLStoreSelector;
import org.spongycastle.jcajce.PKIXCertStore;
import org.spongycastle.jcajce.PKIXCertStoreSelector;
import org.spongycastle.jcajce.PKIXExtendedParameters;
import org.spongycastle.jcajce.util.JcaJceHelper;
import org.spongycastle.jce.exception.ExtCertPathValidatorException;
import org.spongycastle.util.Store;
import org.spongycastle.util.StoreException;
import org.spongycastle.x509.X509AttributeCertificate;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/provider/CertPathValidatorUtilities.class */
class CertPathValidatorUtilities {
    protected static final String ANY_POLICY = "2.5.29.32.0";
    protected static final int KEY_CERT_SIGN = 5;
    protected static final int CRL_SIGN = 6;
    protected static final PKIXCRLUtil CRL_UTIL = new PKIXCRLUtil();
    protected static final String CERTIFICATE_POLICIES = Extension.certificatePolicies.getId();
    protected static final String BASIC_CONSTRAINTS = Extension.basicConstraints.getId();
    protected static final String POLICY_MAPPINGS = Extension.policyMappings.getId();
    protected static final String SUBJECT_ALTERNATIVE_NAME = Extension.subjectAlternativeName.getId();
    protected static final String NAME_CONSTRAINTS = Extension.nameConstraints.getId();
    protected static final String KEY_USAGE = Extension.keyUsage.getId();
    protected static final String INHIBIT_ANY_POLICY = Extension.inhibitAnyPolicy.getId();
    protected static final String ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();
    protected static final String DELTA_CRL_INDICATOR = Extension.deltaCRLIndicator.getId();
    protected static final String POLICY_CONSTRAINTS = Extension.policyConstraints.getId();
    protected static final String FRESHEST_CRL = Extension.freshestCRL.getId();
    protected static final String CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
    protected static final String AUTHORITY_KEY_IDENTIFIER = Extension.authorityKeyIdentifier.getId();
    protected static final String CRL_NUMBER = Extension.cRLNumber.getId();
    protected static final String[] crlReasons = {"unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", "unknown", "removeFromCRL", "privilegeWithdrawn", "aACompromise"};

    CertPathValidatorUtilities() {
    }

    protected static TrustAnchor findTrustAnchor(X509Certificate cert, Set trustAnchors) throws AnnotatedException {
        return findTrustAnchor(cert, trustAnchors, null);
    }

    protected static TrustAnchor findTrustAnchor(X509Certificate cert, Set trustAnchors, String sigProvider) throws AnnotatedException {
        TrustAnchor trust = null;
        PublicKey trustPublicKey = null;
        Exception invalidKeyEx = null;
        X509CertSelector certSelectX509 = new X509CertSelector();
        X500Name certIssuer = PrincipalUtils.getEncodedIssuerPrincipal(cert);
        try {
            certSelectX509.setSubject(certIssuer.getEncoded());
            Iterator iter = trustAnchors.iterator();
            while (iter.hasNext() && trust == null) {
                trust = (TrustAnchor) iter.next();
                if (trust.getTrustedCert() != null) {
                    if (certSelectX509.match(trust.getTrustedCert())) {
                        trustPublicKey = trust.getTrustedCert().getPublicKey();
                    } else {
                        trust = null;
                    }
                } else if (trust.getCAName() != null && trust.getCAPublicKey() != null) {
                    try {
                        X500Name caName = PrincipalUtils.getCA(trust);
                        if (certIssuer.equals(caName)) {
                            trustPublicKey = trust.getCAPublicKey();
                        } else {
                            trust = null;
                        }
                    } catch (IllegalArgumentException e) {
                        trust = null;
                    }
                } else {
                    trust = null;
                }
                if (trustPublicKey != null) {
                    try {
                        verifyX509Certificate(cert, trustPublicKey, sigProvider);
                    } catch (Exception ex) {
                        invalidKeyEx = ex;
                        trust = null;
                        trustPublicKey = null;
                    }
                }
            }
            if (trust == null && invalidKeyEx != null) {
                throw new AnnotatedException("TrustAnchor found but certificate validation failed.", invalidKeyEx);
            }
            return trust;
        } catch (IOException ex2) {
            throw new AnnotatedException("Cannot set subject search criteria for trust anchor.", ex2);
        }
    }

    static boolean isIssuerTrustAnchor(X509Certificate cert, Set trustAnchors, String sigProvider) throws AnnotatedException {
        try {
            return findTrustAnchor(cert, trustAnchors, sigProvider) != null;
        } catch (Exception e) {
            return false;
        }
    }

    static List<PKIXCertStore> getAdditionalStoresFromAltNames(byte[] issuerAlternativeName, Map<GeneralName, PKIXCertStore> altNameCertStoreMap) throws CertificateParsingException {
        if (issuerAlternativeName != null) {
            GeneralNames issuerAltName = GeneralNames.getInstance(ASN1OctetString.getInstance(issuerAlternativeName).getOctets());
            GeneralName[] names = issuerAltName.getNames();
            List<PKIXCertStore> stores = new ArrayList<>();
            for (int i = 0; i != names.length; i++) {
                GeneralName altName = names[i];
                PKIXCertStore altStore = altNameCertStoreMap.get(altName);
                if (altStore != null) {
                    stores.add(altStore);
                }
            }
            return stores;
        }
        return Collections.EMPTY_LIST;
    }

    protected static Date getValidDate(PKIXExtendedParameters paramsPKIX) {
        Date validDate = paramsPKIX.getDate();
        if (validDate == null) {
            validDate = new Date();
        }
        return validDate;
    }

    protected static boolean isSelfIssued(X509Certificate cert) {
        return cert.getSubjectDN().equals(cert.getIssuerDN());
    }

    protected static ASN1Primitive getExtensionValue(X509Extension ext, String oid) throws AnnotatedException {
        byte[] bytes = ext.getExtensionValue(oid);
        if (bytes == null) {
            return null;
        }
        return getObject(oid, bytes);
    }

    private static ASN1Primitive getObject(String oid, byte[] ext) throws AnnotatedException {
        try {
            ASN1InputStream aIn = new ASN1InputStream(ext);
            ASN1OctetString octs = (ASN1OctetString) aIn.readObject();
            ASN1InputStream aIn2 = new ASN1InputStream(octs.getOctets());
            return aIn2.readObject();
        } catch (Exception e) {
            throw new AnnotatedException("exception processing extension " + oid, e);
        }
    }

    protected static AlgorithmIdentifier getAlgorithmIdentifier(PublicKey key) throws CertPathValidatorException {
        try {
            ASN1InputStream aIn = new ASN1InputStream(key.getEncoded());
            SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(aIn.readObject());
            return info.getAlgorithm();
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Subject public key cannot be decoded.", e);
        }
    }

    protected static final Set getQualifierSet(ASN1Sequence qualifiers) throws CertPathValidatorException {
        Set pq = new HashSet();
        if (qualifiers == null) {
            return pq;
        }
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ASN1OutputStream aOut = new ASN1OutputStream(bOut);
        Enumeration e = qualifiers.getObjects();
        while (e.hasMoreElements()) {
            try {
                aOut.writeObject((ASN1Encodable) e.nextElement());
                pq.add(new PolicyQualifierInfo(bOut.toByteArray()));
                bOut.reset();
            } catch (IOException ex) {
                throw new ExtCertPathValidatorException("Policy qualifier info cannot be decoded.", ex);
            }
        }
        return pq;
    }

    protected static PKIXPolicyNode removePolicyNode(PKIXPolicyNode validPolicyTree, List[] policyNodes, PKIXPolicyNode _node) {
        PKIXPolicyNode _parent = (PKIXPolicyNode) _node.getParent();
        if (validPolicyTree == null) {
            return null;
        }
        if (_parent == null) {
            for (int j = 0; j < policyNodes.length; j++) {
                policyNodes[j] = new ArrayList();
            }
            return null;
        }
        _parent.removeChild(_node);
        removePolicyNodeRecurse(policyNodes, _node);
        return validPolicyTree;
    }

    private static void removePolicyNodeRecurse(List[] policyNodes, PKIXPolicyNode _node) {
        policyNodes[_node.getDepth()].remove(_node);
        if (_node.hasChildren()) {
            Iterator _iter = _node.getChildren();
            while (_iter.hasNext()) {
                PKIXPolicyNode _child = (PKIXPolicyNode) _iter.next();
                removePolicyNodeRecurse(policyNodes, _child);
            }
        }
    }

    protected static boolean processCertD1i(int index, List[] policyNodes, ASN1ObjectIdentifier pOid, Set pq) {
        List policyNodeVec = policyNodes[index - 1];
        for (int j = 0; j < policyNodeVec.size(); j++) {
            PKIXPolicyNode node = (PKIXPolicyNode) policyNodeVec.get(j);
            Set expectedPolicies = node.getExpectedPolicies();
            if (expectedPolicies.contains(pOid.getId())) {
                Set childExpectedPolicies = new HashSet();
                childExpectedPolicies.add(pOid.getId());
                PKIXPolicyNode child = new PKIXPolicyNode(new ArrayList(), index, childExpectedPolicies, node, pq, pOid.getId(), false);
                node.addChild(child);
                policyNodes[index].add(child);
                return true;
            }
        }
        return false;
    }

    protected static void processCertD1ii(int index, List[] policyNodes, ASN1ObjectIdentifier _poid, Set _pq) {
        List policyNodeVec = policyNodes[index - 1];
        for (int j = 0; j < policyNodeVec.size(); j++) {
            PKIXPolicyNode _node = (PKIXPolicyNode) policyNodeVec.get(j);
            if ("2.5.29.32.0".equals(_node.getValidPolicy())) {
                Set _childExpectedPolicies = new HashSet();
                _childExpectedPolicies.add(_poid.getId());
                PKIXPolicyNode _child = new PKIXPolicyNode(new ArrayList(), index, _childExpectedPolicies, _node, _pq, _poid.getId(), false);
                _node.addChild(_child);
                policyNodes[index].add(_child);
                return;
            }
        }
    }

    protected static void prepareNextCertB1(int i, List[] policyNodes, String id_p, Map m_idp, X509Certificate cert) throws CertPathValidatorException, AnnotatedException {
        boolean idp_found = false;
        for (PKIXPolicyNode node : policyNodes[i]) {
            if (node.getValidPolicy().equals(id_p)) {
                idp_found = true;
                node.expectedPolicies = (Set) m_idp.get(id_p);
                break;
            }
        }
        if (!idp_found) {
            for (PKIXPolicyNode node2 : policyNodes[i]) {
                if ("2.5.29.32.0".equals(node2.getValidPolicy())) {
                    Set pq = null;
                    try {
                        ASN1Sequence policies = DERSequence.getInstance(getExtensionValue(cert, CERTIFICATE_POLICIES));
                        Enumeration e = policies.getObjects();
                        while (e.hasMoreElements()) {
                            try {
                                PolicyInformation pinfo = PolicyInformation.getInstance(e.nextElement());
                                if ("2.5.29.32.0".equals(pinfo.getPolicyIdentifier().getId())) {
                                    try {
                                        pq = getQualifierSet(pinfo.getPolicyQualifiers());
                                        break;
                                    } catch (CertPathValidatorException ex) {
                                        throw new ExtCertPathValidatorException("Policy qualifier info set could not be built.", ex);
                                    }
                                }
                            } catch (Exception ex2) {
                                throw new AnnotatedException("Policy information cannot be decoded.", ex2);
                            }
                        }
                        boolean ci = false;
                        if (cert.getCriticalExtensionOIDs() != null) {
                            ci = cert.getCriticalExtensionOIDs().contains(CERTIFICATE_POLICIES);
                        }
                        PKIXPolicyNode p_node = (PKIXPolicyNode) node2.getParent();
                        if ("2.5.29.32.0".equals(p_node.getValidPolicy())) {
                            PKIXPolicyNode c_node = new PKIXPolicyNode(new ArrayList(), i, (Set) m_idp.get(id_p), p_node, pq, id_p, ci);
                            p_node.addChild(c_node);
                            policyNodes[i].add(c_node);
                            return;
                        }
                        return;
                    } catch (Exception e2) {
                        throw new AnnotatedException("Certificate policies cannot be decoded.", e2);
                    }
                }
            }
        }
    }

    protected static PKIXPolicyNode prepareNextCertB2(int i, List[] policyNodes, String id_p, PKIXPolicyNode validPolicyTree) {
        Iterator nodes_i = policyNodes[i].iterator();
        while (nodes_i.hasNext()) {
            PKIXPolicyNode node = (PKIXPolicyNode) nodes_i.next();
            if (node.getValidPolicy().equals(id_p)) {
                PKIXPolicyNode p_node = (PKIXPolicyNode) node.getParent();
                p_node.removeChild(node);
                nodes_i.remove();
                for (int k = i - 1; k >= 0; k--) {
                    List nodes = policyNodes[k];
                    for (int l = 0; l < nodes.size(); l++) {
                        PKIXPolicyNode node2 = (PKIXPolicyNode) nodes.get(l);
                        if (!node2.hasChildren()) {
                            validPolicyTree = removePolicyNode(validPolicyTree, policyNodes, node2);
                            if (validPolicyTree == null) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return validPolicyTree;
    }

    protected static boolean isAnyPolicy(Set policySet) {
        return policySet == null || policySet.contains("2.5.29.32.0") || policySet.isEmpty();
    }

    protected static Collection findCertificates(PKIXCertStoreSelector certSelect, List certStores) throws AnnotatedException {
        Set certs = new LinkedHashSet();
        for (Object obj : certStores) {
            if (obj instanceof Store) {
                Store certStore = (Store) obj;
                try {
                    certs.addAll(certStore.getMatches(certSelect));
                } catch (StoreException e) {
                    throw new AnnotatedException("Problem while picking certificates from X.509 store.", e);
                }
            } else {
                CertStore certStore2 = (CertStore) obj;
                try {
                    certs.addAll(PKIXCertStoreSelector.getCertificates(certSelect, certStore2));
                } catch (CertStoreException e2) {
                    throw new AnnotatedException("Problem while picking certificates from certificate store.", e2);
                }
            }
        }
        return certs;
    }

    static List<PKIXCRLStore> getAdditionalStoresFromCRLDistributionPoint(CRLDistPoint crldp, Map<GeneralName, PKIXCRLStore> namedCRLStoreMap) throws AnnotatedException {
        if (crldp != null) {
            try {
                DistributionPoint[] dps = crldp.getDistributionPoints();
                List<PKIXCRLStore> stores = new ArrayList<>();
                for (DistributionPoint distributionPoint : dps) {
                    DistributionPointName dpn = distributionPoint.getDistributionPoint();
                    if (dpn != null && dpn.getType() == 0) {
                        GeneralName[] genNames = GeneralNames.getInstance(dpn.getName()).getNames();
                        for (GeneralName generalName : genNames) {
                            PKIXCRLStore store = namedCRLStoreMap.get(generalName);
                            if (store != null) {
                                stores.add(store);
                            }
                        }
                    }
                }
                return stores;
            } catch (Exception e) {
                throw new AnnotatedException("Distribution points could not be read.", e);
            }
        }
        return Collections.EMPTY_LIST;
    }

    protected static void getCRLIssuersFromDistributionPoint(DistributionPoint dp, Collection issuerPrincipals, X509CRLSelector selector) throws AnnotatedException {
        List issuers = new ArrayList();
        if (dp.getCRLIssuer() != null) {
            GeneralName[] genNames = dp.getCRLIssuer().getNames();
            for (int j = 0; j < genNames.length; j++) {
                if (genNames[j].getTagNo() == 4) {
                    try {
                        issuers.add(X500Name.getInstance(genNames[j].getName().toASN1Primitive().getEncoded()));
                    } catch (IOException e) {
                        throw new AnnotatedException("CRL issuer information from distribution point cannot be decoded.", e);
                    }
                }
            }
        } else {
            if (dp.getDistributionPoint() == null) {
                throw new AnnotatedException("CRL issuer is omitted from distribution point but no distributionPoint field present.");
            }
            Iterator it = issuerPrincipals.iterator();
            while (it.hasNext()) {
                issuers.add(it.next());
            }
        }
        Iterator it2 = issuers.iterator();
        while (it2.hasNext()) {
            try {
                selector.addIssuerName(((X500Name) it2.next()).getEncoded());
            } catch (IOException ex) {
                throw new AnnotatedException("Cannot decode CRL issuer information.", ex);
            }
        }
    }

    private static BigInteger getSerialNumber(Object cert) {
        return ((X509Certificate) cert).getSerialNumber();
    }

    protected static void getCertStatus(Date validDate, X509CRL crl, Object cert, CertStatus certStatus) throws AnnotatedException {
        X509CRLEntry crl_entry;
        X500Name certIssuer;
        try {
            boolean isIndirect = X509CRLObject.isIndirectCRL(crl);
            if (isIndirect) {
                crl_entry = crl.getRevokedCertificate(getSerialNumber(cert));
                if (crl_entry == null) {
                    return;
                }
                X500Principal certificateIssuer = crl_entry.getCertificateIssuer();
                if (certificateIssuer == null) {
                    certIssuer = PrincipalUtils.getIssuerPrincipal(crl);
                } else {
                    certIssuer = X500Name.getInstance(certificateIssuer.getEncoded());
                }
                if (!PrincipalUtils.getEncodedIssuerPrincipal(cert).equals(certIssuer)) {
                    return;
                }
            } else {
                if (!PrincipalUtils.getEncodedIssuerPrincipal(cert).equals(PrincipalUtils.getIssuerPrincipal(crl))) {
                    return;
                }
                crl_entry = crl.getRevokedCertificate(getSerialNumber(cert));
                if (crl_entry == null) {
                    return;
                }
            }
            ASN1Enumerated reasonCode = null;
            if (crl_entry.hasExtensions()) {
                try {
                    reasonCode = ASN1Enumerated.getInstance(getExtensionValue(crl_entry, Extension.reasonCode.getId()));
                } catch (Exception e) {
                    throw new AnnotatedException("Reason code CRL entry extension could not be decoded.", e);
                }
            }
            if (validDate.getTime() >= crl_entry.getRevocationDate().getTime() || reasonCode == null || reasonCode.getValue().intValue() == 0 || reasonCode.getValue().intValue() == 1 || reasonCode.getValue().intValue() == 2 || reasonCode.getValue().intValue() == 8) {
                if (reasonCode != null) {
                    certStatus.setCertStatus(reasonCode.getValue().intValue());
                } else {
                    certStatus.setCertStatus(0);
                }
                certStatus.setRevocationDate(crl_entry.getRevocationDate());
            }
        } catch (CRLException exception) {
            throw new AnnotatedException("Failed check for indirect CRL.", exception);
        }
    }

    protected static Set getDeltaCRLs(Date validityDate, X509CRL completeCRL, List<CertStore> certStores, List<PKIXCRLStore> pkixCrlStores) throws AnnotatedException {
        X509CRLSelector baseDeltaSelect = new X509CRLSelector();
        try {
            baseDeltaSelect.addIssuerName(PrincipalUtils.getIssuerPrincipal(completeCRL).getEncoded());
            BigInteger completeCRLNumber = null;
            try {
                ASN1Primitive derObject = getExtensionValue(completeCRL, CRL_NUMBER);
                if (derObject != null) {
                    completeCRLNumber = ASN1Integer.getInstance(derObject).getPositiveValue();
                }
                try {
                    byte[] idp = completeCRL.getExtensionValue(ISSUING_DISTRIBUTION_POINT);
                    baseDeltaSelect.setMinCRLNumber(completeCRLNumber == null ? null : completeCRLNumber.add(BigInteger.valueOf(1L)));
                    PKIXCRLStoreSelector.Builder selBuilder = new PKIXCRLStoreSelector.Builder(baseDeltaSelect);
                    selBuilder.setIssuingDistributionPoint(idp);
                    selBuilder.setIssuingDistributionPointEnabled(true);
                    selBuilder.setMaxBaseCRLNumber(completeCRLNumber);
                    Set<X509CRL> temp = CRL_UTIL.findCRLs(selBuilder.build(), validityDate, certStores, pkixCrlStores);
                    Set result = new HashSet();
                    for (X509CRL crl : temp) {
                        if (isDeltaCRL(crl)) {
                            result.add(crl);
                        }
                    }
                    return result;
                } catch (Exception e) {
                    throw new AnnotatedException("Issuing distribution point extension value could not be read.", e);
                }
            } catch (Exception e2) {
                throw new AnnotatedException("CRL number extension could not be extracted from CRL.", e2);
            }
        } catch (IOException e3) {
            throw new AnnotatedException("Cannot extract issuer from CRL.", e3);
        }
    }

    private static boolean isDeltaCRL(X509CRL crl) {
        Set<String> criticalExtensionOIDs = crl.getCriticalExtensionOIDs();
        if (criticalExtensionOIDs == null) {
            return false;
        }
        return criticalExtensionOIDs.contains(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
    }

    protected static Set getCompleteCRLs(DistributionPoint dp, Object cert, Date currentDate, PKIXExtendedParameters paramsPKIX) throws AnnotatedException {
        X509CRLSelector baseCrlSelect = new X509CRLSelector();
        try {
            Set issuers = new HashSet();
            issuers.add(PrincipalUtils.getEncodedIssuerPrincipal(cert));
            getCRLIssuersFromDistributionPoint(dp, issuers, baseCrlSelect);
            if (cert instanceof X509Certificate) {
                baseCrlSelect.setCertificateChecking((X509Certificate) cert);
            }
            PKIXCRLStoreSelector<? extends CRL> pKIXCRLStoreSelectorBuild = new PKIXCRLStoreSelector.Builder(baseCrlSelect).setCompleteCRLEnabled(true).build();
            Date validityDate = currentDate;
            if (paramsPKIX.getDate() != null) {
                validityDate = paramsPKIX.getDate();
            }
            Set crls = CRL_UTIL.findCRLs(pKIXCRLStoreSelectorBuild, validityDate, paramsPKIX.getCertStores(), paramsPKIX.getCRLStores());
            checkCRLsNotEmpty(crls, cert);
            return crls;
        } catch (AnnotatedException e) {
            throw new AnnotatedException("Could not get issuer information from distribution point.", e);
        }
    }

    protected static Date getValidCertDateFromValidityModel(PKIXExtendedParameters paramsPKIX, CertPath certPath, int index) throws AnnotatedException {
        if (paramsPKIX.getValidityModel() == 1) {
            if (index <= 0) {
                return getValidDate(paramsPKIX);
            }
            if (index - 1 == 0) {
                ASN1GeneralizedTime dateOfCertgen = null;
                try {
                    byte[] extBytes = ((X509Certificate) certPath.getCertificates().get(index - 1)).getExtensionValue(ISISMTTObjectIdentifiers.id_isismtt_at_dateOfCertGen.getId());
                    if (extBytes != null) {
                        dateOfCertgen = ASN1GeneralizedTime.getInstance(ASN1Primitive.fromByteArray(extBytes));
                    }
                    if (dateOfCertgen != null) {
                        try {
                            return dateOfCertgen.getDate();
                        } catch (ParseException e) {
                            throw new AnnotatedException("Date from date of cert gen extension could not be parsed.", e);
                        }
                    }
                    return ((X509Certificate) certPath.getCertificates().get(index - 1)).getNotBefore();
                } catch (IOException e2) {
                    throw new AnnotatedException("Date of cert gen extension could not be read.");
                } catch (IllegalArgumentException e3) {
                    throw new AnnotatedException("Date of cert gen extension could not be read.");
                }
            }
            return ((X509Certificate) certPath.getCertificates().get(index - 1)).getNotBefore();
        }
        return getValidDate(paramsPKIX);
    }

    protected static PublicKey getNextWorkingKey(List certs, int index, JcaJceHelper helper) throws CertPathValidatorException {
        Certificate cert = (Certificate) certs.get(index);
        PublicKey pubKey = cert.getPublicKey();
        if (!(pubKey instanceof DSAPublicKey)) {
            return pubKey;
        }
        DSAPublicKey dsaPubKey = (DSAPublicKey) pubKey;
        if (dsaPubKey.getParams() != null) {
            return dsaPubKey;
        }
        for (int i = index + 1; i < certs.size(); i++) {
            X509Certificate parentCert = (X509Certificate) certs.get(i);
            PublicKey pubKey2 = parentCert.getPublicKey();
            if (!(pubKey2 instanceof DSAPublicKey)) {
                throw new CertPathValidatorException("DSA parameters cannot be inherited from previous certificate.");
            }
            DSAPublicKey prevDSAPubKey = (DSAPublicKey) pubKey2;
            if (prevDSAPubKey.getParams() != null) {
                DSAParams dsaParams = prevDSAPubKey.getParams();
                DSAPublicKeySpec dsaPubKeySpec = new DSAPublicKeySpec(dsaPubKey.getY(), dsaParams.getP(), dsaParams.getQ(), dsaParams.getG());
                try {
                    KeyFactory keyFactory = helper.createKeyFactory("DSA");
                    return keyFactory.generatePublic(dsaPubKeySpec);
                } catch (Exception exception) {
                    throw new RuntimeException(exception.getMessage());
                }
            }
        }
        throw new CertPathValidatorException("DSA parameters cannot be inherited from previous certificate.");
    }

    static Collection findIssuerCerts(X509Certificate cert, List<CertStore> certStores, List<PKIXCertStore> pkixCertStores) throws AnnotatedException {
        X509CertSelector selector = new X509CertSelector();
        try {
            selector.setSubject(PrincipalUtils.getIssuerPrincipal(cert).getEncoded());
            try {
                byte[] akiExtensionValue = cert.getExtensionValue(AUTHORITY_KEY_IDENTIFIER);
                if (akiExtensionValue != null) {
                    ASN1OctetString aki = ASN1OctetString.getInstance(akiExtensionValue);
                    byte[] authorityKeyIdentifier = AuthorityKeyIdentifier.getInstance(aki.getOctets()).getKeyIdentifier();
                    if (authorityKeyIdentifier != null) {
                        selector.setSubjectKeyIdentifier(new DEROctetString(authorityKeyIdentifier).getEncoded());
                    }
                }
            } catch (Exception e) {
            }
            PKIXCertStoreSelector<? extends Certificate> pKIXCertStoreSelectorBuild = new PKIXCertStoreSelector.Builder(selector).build();
            Set certs = new LinkedHashSet();
            try {
                List<X509Certificate> matches = new ArrayList();
                matches.addAll(findCertificates(pKIXCertStoreSelectorBuild, certStores));
                matches.addAll(findCertificates(pKIXCertStoreSelectorBuild, pkixCertStores));
                for (X509Certificate issuer : matches) {
                    certs.add(issuer);
                }
                return certs;
            } catch (AnnotatedException e2) {
                throw new AnnotatedException("Issuer certificate cannot be searched.", e2);
            }
        } catch (IOException e3) {
            throw new AnnotatedException("Subject criteria for certificate selector to find issuer certificate could not be set.", e3);
        }
    }

    protected static void verifyX509Certificate(X509Certificate cert, PublicKey publicKey, String sigProvider) throws GeneralSecurityException {
        if (sigProvider == null) {
            cert.verify(publicKey);
        } else {
            cert.verify(publicKey, sigProvider);
        }
    }

    static void checkCRLsNotEmpty(Set crls, Object cert) throws AnnotatedException {
        if (crls.isEmpty()) {
            if (cert instanceof X509AttributeCertificate) {
                X509AttributeCertificate aCert = (X509AttributeCertificate) cert;
                throw new AnnotatedException("No CRLs found for issuer \"" + aCert.getIssuer().getPrincipals()[0] + "\"");
            }
            X509Certificate xCert = (X509Certificate) cert;
            throw new AnnotatedException("No CRLs found for issuer \"" + RFC4519Style.INSTANCE.toString(PrincipalUtils.getIssuerPrincipal(xCert)) + "\"");
        }
    }
}
