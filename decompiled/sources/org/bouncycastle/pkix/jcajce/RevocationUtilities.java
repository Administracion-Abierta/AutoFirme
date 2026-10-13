package org.bouncycastle.pkix.jcajce;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkix/jcajce/RevocationUtilities.class */
class RevocationUtilities {
    protected static final String ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();

    RevocationUtilities() {
    }

    protected static Date getValidityDate(PKIXExtendedParameters pKIXExtendedParameters, Date date) {
        Date validityDate = pKIXExtendedParameters.getValidityDate();
        return null == validityDate ? date : validityDate;
    }

    protected static ASN1Primitive getExtensionValue(X509Extension x509Extension, ASN1ObjectIdentifier aSN1ObjectIdentifier) throws AnnotatedException {
        byte[] extensionValue = x509Extension.getExtensionValue(aSN1ObjectIdentifier.getId());
        if (null == extensionValue) {
            return null;
        }
        return getObject(aSN1ObjectIdentifier, extensionValue);
    }

    private static ASN1Primitive getObject(ASN1ObjectIdentifier aSN1ObjectIdentifier, byte[] bArr) throws AnnotatedException {
        try {
            return ASN1Primitive.fromByteArray(ASN1OctetString.getInstance(bArr).getOctets());
        } catch (Exception e) {
            throw new AnnotatedException("exception processing extension " + aSN1ObjectIdentifier, e);
        }
    }

    protected static void findCertificates(LinkedHashSet linkedHashSet, PKIXCertStoreSelector pKIXCertStoreSelector, List list) throws AnnotatedException {
        for (Object obj : list) {
            if (obj instanceof Store) {
                try {
                    linkedHashSet.addAll(((Store) obj).getMatches(pKIXCertStoreSelector));
                } catch (StoreException e) {
                    throw new AnnotatedException("Problem while picking certificates from X.509 store.", e);
                }
            } else {
                try {
                    linkedHashSet.addAll(PKIXCertStoreSelector.getCertificates(pKIXCertStoreSelector, (CertStore) obj));
                } catch (CertStoreException e2) {
                    throw new AnnotatedException("Problem while picking certificates from certificate store.", e2);
                }
            }
        }
    }

    static List<PKIXCRLStore> getAdditionalStoresFromCRLDistributionPoint(CRLDistPoint cRLDistPoint, Map<GeneralName, PKIXCRLStore> map) throws AnnotatedException {
        if (cRLDistPoint == null) {
            return Collections.emptyList();
        }
        try {
            DistributionPoint[] distributionPoints = cRLDistPoint.getDistributionPoints();
            ArrayList arrayList = new ArrayList();
            for (DistributionPoint distributionPoint : distributionPoints) {
                DistributionPointName distributionPoint2 = distributionPoint.getDistributionPoint();
                if (distributionPoint2 != null && distributionPoint2.getType() == 0) {
                    for (GeneralName generalName : GeneralNames.getInstance(distributionPoint2.getName()).getNames()) {
                        PKIXCRLStore pKIXCRLStore = map.get(generalName);
                        if (pKIXCRLStore != null) {
                            arrayList.add(pKIXCRLStore);
                        }
                    }
                }
            }
            return arrayList;
        } catch (Exception e) {
            throw new AnnotatedException("Distribution points could not be read.", e);
        }
    }

    protected static void getCRLIssuersFromDistributionPoint(DistributionPoint distributionPoint, Collection collection, X509CRLSelector x509CRLSelector) throws AnnotatedException {
        ArrayList arrayList = new ArrayList();
        if (distributionPoint.getCRLIssuer() != null) {
            GeneralName[] names = distributionPoint.getCRLIssuer().getNames();
            for (int i = 0; i < names.length; i++) {
                if (names[i].getTagNo() == 4) {
                    try {
                        arrayList.add(X500Name.getInstance(names[i].getName()));
                    } catch (IllegalArgumentException e) {
                        throw new AnnotatedException("CRL issuer information from distribution point cannot be decoded.", e);
                    }
                }
            }
        } else {
            if (distributionPoint.getDistributionPoint() == null) {
                throw new AnnotatedException("CRL issuer is omitted from distribution point but no distributionPoint field present.");
            }
            Iterator it = collection.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next());
            }
        }
        Iterator it2 = arrayList.iterator();
        while (it2.hasNext()) {
            try {
                x509CRLSelector.addIssuerName(((X500Name) it2.next()).getEncoded());
            } catch (IOException e2) {
                throw new AnnotatedException("Cannot decode CRL issuer information.", e2);
            }
        }
    }

    protected static void getCertStatus(Date date, X509CRL x509crl, Object obj, CertStatus certStatus) throws AnnotatedException {
        X509CRLEntry revokedCertificate;
        try {
            boolean zIsIndirectCRL = isIndirectCRL(x509crl);
            X509Certificate x509Certificate = (X509Certificate) obj;
            X500Name issuer = getIssuer(x509Certificate);
            if ((zIsIndirectCRL || issuer.equals(getIssuer(x509crl))) && null != (revokedCertificate = x509crl.getRevokedCertificate(x509Certificate.getSerialNumber()))) {
                if (zIsIndirectCRL) {
                    X500Principal certificateIssuer = revokedCertificate.getCertificateIssuer();
                    if (!issuer.equals(null == certificateIssuer ? getIssuer(x509crl) : getX500Name(certificateIssuer))) {
                        return;
                    }
                }
                int iIntValueExact = 0;
                if (revokedCertificate.hasExtensions()) {
                    try {
                        ASN1Enumerated aSN1Enumerated = ASN1Enumerated.getInstance(getExtensionValue(revokedCertificate, Extension.reasonCode));
                        if (null != aSN1Enumerated) {
                            iIntValueExact = aSN1Enumerated.intValueExact();
                        }
                    } catch (Exception e) {
                        throw new AnnotatedException("Reason code CRL entry extension could not be decoded.", e);
                    }
                }
                Date revocationDate = revokedCertificate.getRevocationDate();
                if (date.before(revocationDate)) {
                    switch (iIntValueExact) {
                        case 0:
                        case 1:
                        case 2:
                        case 10:
                            break;
                        default:
                            return;
                    }
                }
                certStatus.setCertStatus(iIntValueExact);
                certStatus.setRevocationDate(revocationDate);
            }
        } catch (CRLException e2) {
            throw new AnnotatedException("Failed check for indirect CRL.", e2);
        }
    }

    protected static Set getDeltaCRLs(Date date, X509CRL x509crl, List<CertStore> list, List<PKIXCRLStore> list2) throws AnnotatedException {
        X509CRLSelector x509CRLSelector = new X509CRLSelector();
        try {
            x509CRLSelector.addIssuerName(x509crl.getIssuerX500Principal().getEncoded());
            try {
                ASN1Primitive extensionValue = getExtensionValue(x509crl, Extension.cRLNumber);
                BigInteger positiveValue = extensionValue != null ? ASN1Integer.getInstance(extensionValue).getPositiveValue() : null;
                try {
                    byte[] extensionValue2 = x509crl.getExtensionValue(ISSUING_DISTRIBUTION_POINT);
                    x509CRLSelector.setMinCRLNumber(positiveValue == null ? null : positiveValue.add(BigInteger.valueOf(1L)));
                    PKIXCRLStoreSelector.Builder builder = new PKIXCRLStoreSelector.Builder(x509CRLSelector);
                    builder.setIssuingDistributionPoint(extensionValue2);
                    builder.setIssuingDistributionPointEnabled(true);
                    builder.setMaxBaseCRLNumber(positiveValue);
                    Set<X509CRL> setFindCRLs = PKIXCRLUtil.findCRLs(builder.build(), date, list, list2);
                    HashSet hashSet = new HashSet();
                    for (X509CRL x509crl2 : setFindCRLs) {
                        if (isDeltaCRL(x509crl2)) {
                            hashSet.add(x509crl2);
                        }
                    }
                    return hashSet;
                } catch (Exception e) {
                    throw new AnnotatedException("issuing distribution point extension value could not be read", e);
                }
            } catch (Exception e2) {
                throw new AnnotatedException("cannot extract CRL number extension from CRL", e2);
            }
        } catch (IOException e3) {
            throw new AnnotatedException("cannot extract issuer from CRL.", e3);
        }
    }

    private static boolean isDeltaCRL(X509CRL x509crl) {
        Set<String> criticalExtensionOIDs = x509crl.getCriticalExtensionOIDs();
        if (null == criticalExtensionOIDs) {
            return false;
        }
        return criticalExtensionOIDs.contains(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
    }

    protected static Set getCompleteCRLs(DistributionPoint distributionPoint, Object obj, Date date, List list, List list2) throws AnnotatedException, CRLNotFoundException {
        X509CRLSelector x509CRLSelector = new X509CRLSelector();
        try {
            HashSet hashSet = new HashSet();
            hashSet.add(getIssuer((X509Certificate) obj));
            getCRLIssuersFromDistributionPoint(distributionPoint, hashSet, x509CRLSelector);
            if (obj instanceof X509Certificate) {
                x509CRLSelector.setCertificateChecking((X509Certificate) obj);
            }
            Set setFindCRLs = PKIXCRLUtil.findCRLs(new PKIXCRLStoreSelector.Builder(x509CRLSelector).setCompleteCRLEnabled(true).build(), date, list, list2);
            checkCRLsNotEmpty(setFindCRLs, obj);
            return setFindCRLs;
        } catch (AnnotatedException e) {
            throw new AnnotatedException("Could not get issuer information from distribution point.", e);
        }
    }

    protected static PublicKey getNextWorkingKey(List list, int i, JcaJceHelper jcaJceHelper) throws CertPathValidatorException {
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
                    return jcaJceHelper.createKeyFactory("DSA").generatePublic(new DSAPublicKeySpec(dSAPublicKey.getY(), params.getP(), params.getQ(), params.getG()));
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        throw new CertPathValidatorException("DSA parameters cannot be inherited from previous certificate.");
    }

    static void checkCRLsNotEmpty(Set set, Object obj) throws CRLNotFoundException {
        if (set.isEmpty()) {
            throw new CRLNotFoundException("No CRLs found for issuer \"" + RFC4519Style.INSTANCE.toString(getIssuer((X509Certificate) obj)) + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
        }
    }

    public static boolean isIndirectCRL(X509CRL x509crl) throws CRLException {
        try {
            byte[] extensionValue = x509crl.getExtensionValue(Extension.issuingDistributionPoint.getId());
            return extensionValue != null && IssuingDistributionPoint.getInstance(ASN1OctetString.getInstance(extensionValue).getOctets()).isIndirectCRL();
        } catch (Exception e) {
            throw new CRLException("exception reading IssuingDistributionPoint", e);
        }
    }

    private static X500Name getIssuer(X509Certificate x509Certificate) {
        return getX500Name(x509Certificate.getIssuerX500Principal());
    }

    private static X500Name getIssuer(X509CRL x509crl) {
        return getX500Name(x509crl.getIssuerX500Principal());
    }

    private static X500Name getX500Name(X500Principal x500Principal) {
        return X500Name.getInstance(x500Principal.getEncoded());
    }
}
