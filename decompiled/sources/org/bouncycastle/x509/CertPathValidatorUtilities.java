package org.bouncycastle.x509;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.PKIXParameters;
import java.security.cert.PolicyQualifierInfo;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.jce.provider.AnnotatedException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.PKIXPolicyNode;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/x509/CertPathValidatorUtilities.class */
class CertPathValidatorUtilities {
    protected static final String ANY_POLICY = "2.5.29.32.0";
    protected static final int KEY_CERT_SIGN = 5;
    protected static final int CRL_SIGN = 6;
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
    protected static final String CRL_NUMBER = Extension.cRLNumber.getId();
    protected static final String[] crlReasons = {"unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", "unknown", "removeFromCRL", "privilegeWithdrawn", "aACompromise"};

    CertPathValidatorUtilities() {
    }

    protected static X500Principal getEncodedIssuerPrincipal(Object obj) {
        return obj instanceof X509Certificate ? ((X509Certificate) obj).getIssuerX500Principal() : (X500Principal) ((X509AttributeCertificate) obj).getIssuer().getPrincipals()[0];
    }

    protected static Date getValidityDate(PKIXParameters pKIXParameters, Date date) {
        Date date2 = pKIXParameters.getDate();
        return null == date2 ? date : date2;
    }

    protected static X500Principal getSubjectPrincipal(X509Certificate x509Certificate) {
        return x509Certificate.getSubjectX500Principal();
    }

    protected static boolean isSelfIssued(X509Certificate x509Certificate) {
        return x509Certificate.getSubjectDN().equals(x509Certificate.getIssuerDN());
    }

    protected static ASN1Primitive getExtensionValue(X509Extension x509Extension, String str) throws AnnotatedException {
        byte[] extensionValue = x509Extension.getExtensionValue(str);
        if (extensionValue == null) {
            return null;
        }
        return getObject(str, extensionValue);
    }

    private static ASN1Primitive getObject(String str, byte[] bArr) throws AnnotatedException {
        try {
            return new ASN1InputStream(((ASN1OctetString) new ASN1InputStream(bArr).readObject()).getOctets()).readObject();
        } catch (Exception e) {
            throw new AnnotatedException("exception processing extension " + str, e);
        }
    }

    protected static X500Principal getIssuerPrincipal(X509CRL x509crl) {
        return x509crl.getIssuerX500Principal();
    }

    protected static AlgorithmIdentifier getAlgorithmIdentifier(PublicKey publicKey) throws CertPathValidatorException {
        try {
            return SubjectPublicKeyInfo.getInstance(new ASN1InputStream(publicKey.getEncoded()).readObject()).getAlgorithmId();
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Subject public key cannot be decoded.", e);
        }
    }

    protected static final Set getQualifierSet(ASN1Sequence aSN1Sequence) throws CertPathValidatorException {
        HashSet hashSet = new HashSet();
        if (aSN1Sequence == null) {
            return hashSet;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ASN1OutputStream aSN1OutputStreamCreate = ASN1OutputStream.create(byteArrayOutputStream);
        Enumeration objects = aSN1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            try {
                aSN1OutputStreamCreate.writeObject((ASN1Encodable) objects.nextElement());
                hashSet.add(new PolicyQualifierInfo(byteArrayOutputStream.toByteArray()));
                byteArrayOutputStream.reset();
            } catch (IOException e) {
                throw new ExtCertPathValidatorException("Policy qualifier info cannot be decoded.", e);
            }
        }
        return hashSet;
    }

    protected static PKIXPolicyNode removePolicyNode(PKIXPolicyNode pKIXPolicyNode, List[] listArr, PKIXPolicyNode pKIXPolicyNode2) {
        PKIXPolicyNode pKIXPolicyNode3 = (PKIXPolicyNode) pKIXPolicyNode2.getParent();
        if (pKIXPolicyNode == null) {
            return null;
        }
        if (pKIXPolicyNode3 != null) {
            pKIXPolicyNode3.removeChild(pKIXPolicyNode2);
            removePolicyNodeRecurse(listArr, pKIXPolicyNode2);
            return pKIXPolicyNode;
        }
        for (int i = 0; i < listArr.length; i++) {
            listArr[i] = new ArrayList();
        }
        return null;
    }

    private static void removePolicyNodeRecurse(List[] listArr, PKIXPolicyNode pKIXPolicyNode) {
        listArr[pKIXPolicyNode.getDepth()].remove(pKIXPolicyNode);
        if (pKIXPolicyNode.hasChildren()) {
            Iterator children = pKIXPolicyNode.getChildren();
            while (children.hasNext()) {
                removePolicyNodeRecurse(listArr, (PKIXPolicyNode) children.next());
            }
        }
    }

    protected static boolean processCertD1i(int i, List[] listArr, ASN1ObjectIdentifier aSN1ObjectIdentifier, Set set) {
        List list = listArr[i - 1];
        for (int i2 = 0; i2 < list.size(); i2++) {
            PKIXPolicyNode pKIXPolicyNode = (PKIXPolicyNode) list.get(i2);
            if (pKIXPolicyNode.getExpectedPolicies().contains(aSN1ObjectIdentifier.getId())) {
                HashSet hashSet = new HashSet();
                hashSet.add(aSN1ObjectIdentifier.getId());
                PKIXPolicyNode pKIXPolicyNode2 = new PKIXPolicyNode(new ArrayList(), i, hashSet, pKIXPolicyNode, set, aSN1ObjectIdentifier.getId(), false);
                pKIXPolicyNode.addChild(pKIXPolicyNode2);
                listArr[i].add(pKIXPolicyNode2);
                return true;
            }
        }
        return false;
    }

    protected static void processCertD1ii(int i, List[] listArr, ASN1ObjectIdentifier aSN1ObjectIdentifier, Set set) {
        List list = listArr[i - 1];
        for (int i2 = 0; i2 < list.size(); i2++) {
            PKIXPolicyNode pKIXPolicyNode = (PKIXPolicyNode) list.get(i2);
            if ("2.5.29.32.0".equals(pKIXPolicyNode.getValidPolicy())) {
                HashSet hashSet = new HashSet();
                hashSet.add(aSN1ObjectIdentifier.getId());
                PKIXPolicyNode pKIXPolicyNode2 = new PKIXPolicyNode(new ArrayList(), i, hashSet, pKIXPolicyNode, set, aSN1ObjectIdentifier.getId(), false);
                pKIXPolicyNode.addChild(pKIXPolicyNode2);
                listArr[i].add(pKIXPolicyNode2);
                return;
            }
        }
    }

    protected static void prepareNextCertB1(int i, List[] listArr, String str, Map map, X509Certificate x509Certificate) throws AnnotatedException, CertPathValidatorException {
        boolean z = false;
        for (PKIXPolicyNode pKIXPolicyNode : listArr[i]) {
            if (pKIXPolicyNode.getValidPolicy().equals(str)) {
                z = true;
                pKIXPolicyNode.setExpectedPolicies((Set) map.get(str));
                break;
            }
        }
        if (z) {
            return;
        }
        for (PKIXPolicyNode pKIXPolicyNode2 : listArr[i]) {
            if ("2.5.29.32.0".equals(pKIXPolicyNode2.getValidPolicy())) {
                Set qualifierSet = null;
                try {
                    Enumeration objects = DERSequence.getInstance(getExtensionValue(x509Certificate, CERTIFICATE_POLICIES)).getObjects();
                    while (objects.hasMoreElements()) {
                        try {
                            PolicyInformation policyInformation = PolicyInformation.getInstance(objects.nextElement());
                            if ("2.5.29.32.0".equals(policyInformation.getPolicyIdentifier().getId())) {
                                try {
                                    qualifierSet = getQualifierSet(policyInformation.getPolicyQualifiers());
                                    break;
                                } catch (CertPathValidatorException e) {
                                    throw new ExtCertPathValidatorException("Policy qualifier info set could not be built.", e);
                                }
                            }
                        } catch (Exception e2) {
                            throw new AnnotatedException("Policy information cannot be decoded.", e2);
                        }
                    }
                    boolean zContains = x509Certificate.getCriticalExtensionOIDs() != null ? x509Certificate.getCriticalExtensionOIDs().contains(CERTIFICATE_POLICIES) : false;
                    PKIXPolicyNode pKIXPolicyNode3 = (PKIXPolicyNode) pKIXPolicyNode2.getParent();
                    if ("2.5.29.32.0".equals(pKIXPolicyNode3.getValidPolicy())) {
                        PKIXPolicyNode pKIXPolicyNode4 = new PKIXPolicyNode(new ArrayList(), i, (Set) map.get(str), pKIXPolicyNode3, qualifierSet, str, zContains);
                        pKIXPolicyNode3.addChild(pKIXPolicyNode4);
                        listArr[i].add(pKIXPolicyNode4);
                        return;
                    }
                    return;
                } catch (Exception e3) {
                    throw new AnnotatedException("Certificate policies cannot be decoded.", e3);
                }
            }
        }
    }

    protected static PKIXPolicyNode prepareNextCertB2(int i, List[] listArr, String str, PKIXPolicyNode pKIXPolicyNode) {
        Iterator it = listArr[i].iterator();
        while (it.hasNext()) {
            PKIXPolicyNode pKIXPolicyNode2 = (PKIXPolicyNode) it.next();
            if (pKIXPolicyNode2.getValidPolicy().equals(str)) {
                ((PKIXPolicyNode) pKIXPolicyNode2.getParent()).removeChild(pKIXPolicyNode2);
                it.remove();
                for (int i2 = i - 1; i2 >= 0; i2--) {
                    List list = listArr[i2];
                    for (int i3 = 0; i3 < list.size(); i3++) {
                        PKIXPolicyNode pKIXPolicyNode3 = (PKIXPolicyNode) list.get(i3);
                        if (!pKIXPolicyNode3.hasChildren()) {
                            pKIXPolicyNode = removePolicyNode(pKIXPolicyNode, listArr, pKIXPolicyNode3);
                            if (pKIXPolicyNode == null) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return pKIXPolicyNode;
    }

    protected static boolean isAnyPolicy(Set set) {
        return set == null || set.contains("2.5.29.32.0") || set.isEmpty();
    }

    protected static Collection findCertificates(X509CertStoreSelector x509CertStoreSelector, List list) throws AnnotatedException {
        HashSet hashSet = new HashSet();
        CertificateFactory certificateFactory = new CertificateFactory();
        for (Object obj : list) {
            if (obj instanceof Store) {
                try {
                    for (Object obj2 : ((Store) obj).getMatches(x509CertStoreSelector)) {
                        if (obj2 instanceof Encodable) {
                            hashSet.add(certificateFactory.engineGenerateCertificate(new ByteArrayInputStream(((Encodable) obj2).getEncoded())));
                        } else {
                            if (!(obj2 instanceof Certificate)) {
                                throw new AnnotatedException("Unknown object found in certificate store.");
                            }
                            hashSet.add(obj2);
                        }
                    }
                } catch (IOException e) {
                    throw new AnnotatedException("Problem while extracting certificates from X.509 store.", e);
                } catch (CertificateException e2) {
                    throw new AnnotatedException("Problem while extracting certificates from X.509 store.", e2);
                } catch (StoreException e3) {
                    throw new AnnotatedException("Problem while picking certificates from X.509 store.", e3);
                }
            } else {
                try {
                    hashSet.addAll(((CertStore) obj).getCertificates(x509CertStoreSelector));
                } catch (CertStoreException e4) {
                    throw new AnnotatedException("Problem while picking certificates from certificate store.", e4);
                }
            }
        }
        return hashSet;
    }

    protected static Collection findCertificates(PKIXCertStoreSelector pKIXCertStoreSelector, List list) throws AnnotatedException {
        HashSet hashSet = new HashSet();
        for (Object obj : list) {
            if (obj instanceof Store) {
                try {
                    hashSet.addAll(((Store) obj).getMatches(pKIXCertStoreSelector));
                } catch (StoreException e) {
                    throw new AnnotatedException("Problem while picking certificates from X.509 store.", e);
                }
            } else {
                try {
                    hashSet.addAll(PKIXCertStoreSelector.getCertificates(pKIXCertStoreSelector, (CertStore) obj));
                } catch (CertStoreException e2) {
                    throw new AnnotatedException("Problem while picking certificates from certificate store.", e2);
                }
            }
        }
        return hashSet;
    }

    protected static Collection findCertificates(X509AttributeCertStoreSelector x509AttributeCertStoreSelector, List list) throws AnnotatedException {
        HashSet hashSet = new HashSet();
        for (Object obj : list) {
            if (obj instanceof X509Store) {
                try {
                    hashSet.addAll(((X509Store) obj).getMatches(x509AttributeCertStoreSelector));
                } catch (StoreException e) {
                    throw new AnnotatedException("Problem while picking certificates from X.509 store.", e);
                }
            }
        }
        return hashSet;
    }

    private static BigInteger getSerialNumber(Object obj) {
        return obj instanceof X509Certificate ? ((X509Certificate) obj).getSerialNumber() : ((X509AttributeCertificate) obj).getSerialNumber();
    }

    protected static void getCertStatus(Date date, X509CRL x509crl, Object obj, CertStatus certStatus) throws AnnotatedException {
        X509CRLEntry revokedCertificate;
        try {
            if (isIndirectCRL(x509crl)) {
                revokedCertificate = x509crl.getRevokedCertificate(getSerialNumber(obj));
                if (revokedCertificate == null) {
                    return;
                }
                X500Principal certificateIssuer = revokedCertificate.getCertificateIssuer();
                if (certificateIssuer == null) {
                    certificateIssuer = getIssuerPrincipal(x509crl);
                }
                if (!getEncodedIssuerPrincipal(obj).equals(certificateIssuer)) {
                    return;
                }
            } else {
                if (!getEncodedIssuerPrincipal(obj).equals(getIssuerPrincipal(x509crl))) {
                    return;
                }
                revokedCertificate = x509crl.getRevokedCertificate(getSerialNumber(obj));
                if (revokedCertificate == null) {
                    return;
                }
            }
            ASN1Enumerated aSN1Enumerated = null;
            if (revokedCertificate.hasExtensions()) {
                try {
                    aSN1Enumerated = ASN1Enumerated.getInstance(getExtensionValue(revokedCertificate, org.bouncycastle.asn1.x509.X509Extension.reasonCode.getId()));
                } catch (Exception e) {
                    throw new AnnotatedException("Reason code CRL entry extension could not be decoded.", e);
                }
            }
            int iIntValueExact = null == aSN1Enumerated ? 0 : aSN1Enumerated.intValueExact();
            if (date.getTime() >= revokedCertificate.getRevocationDate().getTime() || iIntValueExact == 0 || iIntValueExact == 1 || iIntValueExact == 2 || iIntValueExact == 10) {
                certStatus.setCertStatus(iIntValueExact);
                certStatus.setRevocationDate(revokedCertificate.getRevocationDate());
            }
        } catch (CRLException e2) {
            throw new AnnotatedException("Failed check for indirect CRL.", e2);
        }
    }

    protected static PublicKey getNextWorkingKey(List list, int i) throws CertPathValidatorException {
        PublicKey publicKey = ((Certificate) list.get(i)).getPublicKey();
        if (!(publicKey instanceof DSAPublicKey)) {
            return publicKey;
        }
        DSAPublicKey dSAPublicKey = (DSAPublicKey) publicKey;
        if (dSAPublicKey.getParams() != null) {
            return dSAPublicKey;
        }
        for (int i2 = i + 1; i2 < list.size(); i2++) {
            PublicKey publicKey2 = ((X509Certificate) list.get(i2)).getPublicKey();
            if (!(publicKey2 instanceof DSAPublicKey)) {
                throw new CertPathValidatorException("DSA parameters cannot be inherited from previous certificate.");
            }
            DSAPublicKey dSAPublicKey2 = (DSAPublicKey) publicKey2;
            if (dSAPublicKey2.getParams() != null) {
                DSAParams params = dSAPublicKey2.getParams();
                try {
                    return KeyFactory.getInstance("DSA", BouncyCastleProvider.PROVIDER_NAME).generatePublic(new DSAPublicKeySpec(dSAPublicKey.getY(), params.getP(), params.getQ(), params.getG()));
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        throw new CertPathValidatorException("DSA parameters cannot be inherited from previous certificate.");
    }

    protected static void verifyX509Certificate(X509Certificate x509Certificate, PublicKey publicKey, String str) throws GeneralSecurityException {
        if (str == null) {
            x509Certificate.verify(publicKey);
        } else {
            x509Certificate.verify(publicKey, str);
        }
    }

    static boolean isIndirectCRL(X509CRL x509crl) throws CRLException {
        try {
            byte[] extensionValue = x509crl.getExtensionValue(Extension.issuingDistributionPoint.getId());
            return extensionValue != null && IssuingDistributionPoint.getInstance(ASN1OctetString.getInstance(extensionValue).getOctets()).isIndirectCRL();
        } catch (Exception e) {
            throw new CRLException("Exception reading IssuingDistributionPoint: " + e);
        }
    }
}
