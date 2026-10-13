package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Principal;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathBuilderSpi;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidator;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jce.exception.ExtCertPathBuilderException;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;
import org.bouncycastle.x509.ExtendedPKIXBuilderParameters;
import org.bouncycastle.x509.ExtendedPKIXParameters;
import org.bouncycastle.x509.X509AttributeCertStoreSelector;
import org.bouncycastle.x509.X509AttributeCertificate;
import org.bouncycastle.x509.X509CertStoreSelector;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/provider/PKIXAttrCertPathBuilderSpi.class */
public class PKIXAttrCertPathBuilderSpi extends CertPathBuilderSpi {
    private Exception certPathException;

    @Override // java.security.cert.CertPathBuilderSpi
    public CertPathBuilderResult engineBuild(CertPathParameters certPathParameters) throws CertPathBuilderException, InvalidAlgorithmParameterException {
        PKIXExtendedBuilderParameters pKIXExtendedBuilderParametersBuild;
        if (!(certPathParameters instanceof PKIXBuilderParameters) && !(certPathParameters instanceof ExtendedPKIXBuilderParameters) && !(certPathParameters instanceof PKIXExtendedBuilderParameters)) {
            throw new InvalidAlgorithmParameterException("Parameters must be an instance of " + PKIXBuilderParameters.class.getName() + " or " + PKIXExtendedBuilderParameters.class.getName() + ".");
        }
        List arrayList = new ArrayList();
        if (certPathParameters instanceof PKIXBuilderParameters) {
            PKIXExtendedBuilderParameters.Builder builder = new PKIXExtendedBuilderParameters.Builder((PKIXBuilderParameters) certPathParameters);
            if (certPathParameters instanceof ExtendedPKIXParameters) {
                ExtendedPKIXBuilderParameters extendedPKIXBuilderParameters = (ExtendedPKIXBuilderParameters) certPathParameters;
                builder.addExcludedCerts(extendedPKIXBuilderParameters.getExcludedCerts());
                builder.setMaxPathLength(extendedPKIXBuilderParameters.getMaxPathLength());
                arrayList = extendedPKIXBuilderParameters.getStores();
            }
            pKIXExtendedBuilderParametersBuild = builder.build();
        } else {
            pKIXExtendedBuilderParametersBuild = (PKIXExtendedBuilderParameters) certPathParameters;
        }
        ArrayList arrayList2 = new ArrayList();
        PKIXExtendedParameters baseParameters = pKIXExtendedBuilderParametersBuild.getBaseParameters();
        Selector targetConstraints = baseParameters.getTargetConstraints();
        if (!(targetConstraints instanceof X509AttributeCertStoreSelector)) {
            throw new CertPathBuilderException("TargetConstraints must be an instance of " + X509AttributeCertStoreSelector.class.getName() + " for " + getClass().getName() + " class.");
        }
        try {
            Collection collectionFindCertificates = findCertificates((X509AttributeCertStoreSelector) targetConstraints, arrayList);
            if (collectionFindCertificates.isEmpty()) {
                throw new CertPathBuilderException("No attribute certificate found matching targetConstraints.");
            }
            CertPathBuilderResult certPathBuilderResultBuild = null;
            Iterator it = collectionFindCertificates.iterator();
            while (it.hasNext() && certPathBuilderResultBuild == null) {
                X509AttributeCertificate x509AttributeCertificate = (X509AttributeCertificate) it.next();
                X509CertStoreSelector x509CertStoreSelector = new X509CertStoreSelector();
                Principal[] principals = x509AttributeCertificate.getIssuer().getPrincipals();
                LinkedHashSet linkedHashSet = new LinkedHashSet();
                for (int i = 0; i < principals.length; i++) {
                    try {
                        if (principals[i] instanceof X500Principal) {
                            x509CertStoreSelector.setSubject(((X500Principal) principals[i]).getEncoded());
                        }
                        PKIXCertStoreSelector<? extends Certificate> pKIXCertStoreSelectorBuild = new PKIXCertStoreSelector.Builder(x509CertStoreSelector).build();
                        CertPathValidatorUtilities.findCertificates(linkedHashSet, pKIXCertStoreSelectorBuild, baseParameters.getCertStores());
                        CertPathValidatorUtilities.findCertificates(linkedHashSet, pKIXCertStoreSelectorBuild, baseParameters.getCertificateStores());
                    } catch (IOException e) {
                        throw new ExtCertPathBuilderException("cannot encode X500Principal.", e);
                    } catch (AnnotatedException e2) {
                        throw new ExtCertPathBuilderException("Public key certificate for attribute certificate cannot be searched.", e2);
                    }
                }
                if (linkedHashSet.isEmpty()) {
                    throw new CertPathBuilderException("Public key certificate for attribute certificate cannot be found.");
                }
                Iterator it2 = linkedHashSet.iterator();
                while (it2.hasNext() && certPathBuilderResultBuild == null) {
                    certPathBuilderResultBuild = build(x509AttributeCertificate, (X509Certificate) it2.next(), pKIXExtendedBuilderParametersBuild, arrayList2);
                }
            }
            if (certPathBuilderResultBuild == null && this.certPathException != null) {
                throw new ExtCertPathBuilderException("Possible certificate chain could not be validated.", this.certPathException);
            }
            if (certPathBuilderResultBuild == null && this.certPathException == null) {
                throw new CertPathBuilderException("Unable to find certificate chain.");
            }
            return certPathBuilderResultBuild;
        } catch (AnnotatedException e3) {
            throw new ExtCertPathBuilderException("Error finding target attribute certificate.", e3);
        }
    }

    /* JADX WARN: Code duplicated, block: B:61:0x01b2  */
    private CertPathBuilderResult build(X509AttributeCertificate x509AttributeCertificate, X509Certificate x509Certificate, PKIXExtendedBuilderParameters pKIXExtendedBuilderParameters, List list) {
        if (list.contains(x509Certificate) || pKIXExtendedBuilderParameters.getExcludedCerts().contains(x509Certificate)) {
            return null;
        }
        if (pKIXExtendedBuilderParameters.getMaxPathLength() != -1 && list.size() - 1 > pKIXExtendedBuilderParameters.getMaxPathLength()) {
            return null;
        }
        list.add(x509Certificate);
        CertPathBuilderResult certPathBuilderResultBuild = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID, BouncyCastleProvider.PROVIDER_NAME);
            CertPathValidator certPathValidator = CertPathValidator.getInstance("RFC3281", BouncyCastleProvider.PROVIDER_NAME);
            try {
                PKIXExtendedParameters baseParameters = pKIXExtendedBuilderParameters.getBaseParameters();
                if (CertPathValidatorUtilities.isIssuerTrustAnchor(x509Certificate, baseParameters.getTrustAnchors(), baseParameters.getSigProvider())) {
                    try {
                        CertPath certPathGenerateCertPath = certificateFactory.generateCertPath((List<? extends Certificate>) list);
                        try {
                            PKIXCertPathValidatorResult pKIXCertPathValidatorResult = (PKIXCertPathValidatorResult) certPathValidator.validate(certPathGenerateCertPath, pKIXExtendedBuilderParameters);
                            return new PKIXCertPathBuilderResult(certPathGenerateCertPath, pKIXCertPathValidatorResult.getTrustAnchor(), pKIXCertPathValidatorResult.getPolicyTree(), pKIXCertPathValidatorResult.getPublicKey());
                        } catch (Exception e) {
                            throw new AnnotatedException("Certification path could not be validated.", e);
                        }
                    } catch (Exception e2) {
                        throw new AnnotatedException("Certification path could not be constructed from certificate list.", e2);
                    }
                }
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(baseParameters.getCertificateStores());
                try {
                    arrayList.addAll(CertPathValidatorUtilities.getAdditionalStoresFromAltNames(x509Certificate.getExtensionValue(Extension.issuerAlternativeName.getId()), baseParameters.getNamedCertificateStoreMap()));
                    HashSet hashSet = new HashSet();
                    try {
                        hashSet.addAll(CertPathValidatorUtilities.findIssuerCerts(x509Certificate, baseParameters.getCertStores(), arrayList));
                        if (hashSet.isEmpty()) {
                            throw new AnnotatedException("No issuer certificate for certificate in certification path found.");
                        }
                        Iterator it = hashSet.iterator();
                        while (it.hasNext() && certPathBuilderResultBuild == null) {
                            X509Certificate x509Certificate2 = (X509Certificate) it.next();
                            if (!x509Certificate2.getIssuerX500Principal().equals(x509Certificate2.getSubjectX500Principal())) {
                                certPathBuilderResultBuild = build(x509AttributeCertificate, x509Certificate2, pKIXExtendedBuilderParameters, list);
                            }
                        }
                        if (certPathBuilderResultBuild == null) {
                            list.remove(x509Certificate);
                        }
                        return certPathBuilderResultBuild;
                    } catch (AnnotatedException e3) {
                        throw new AnnotatedException("Cannot find issuer certificate for certificate in certification path.", e3);
                    }
                } catch (CertificateParsingException e4) {
                    throw new AnnotatedException("No additional X.509 stores can be added from certificate locations.", e4);
                }
            } catch (AnnotatedException e5) {
                this.certPathException = new AnnotatedException("No valid certification path could be build.", e5);
            }
            this.certPathException = new AnnotatedException("No valid certification path could be build.", e5);
            if (certPathBuilderResultBuild == null) {
                list.remove(x509Certificate);
            }
            return certPathBuilderResultBuild;
        } catch (Exception e6) {
            throw new RuntimeException("Exception creating support classes.");
        }
    }

    protected static Collection findCertificates(X509AttributeCertStoreSelector x509AttributeCertStoreSelector, List list) throws AnnotatedException {
        HashSet hashSet = new HashSet();
        for (Object obj : list) {
            if (obj instanceof Store) {
                try {
                    hashSet.addAll(((Store) obj).getMatches(x509AttributeCertStoreSelector));
                } catch (StoreException e) {
                    throw new AnnotatedException("Problem while picking certificates from X.509 store.", e);
                }
            }
        }
        return hashSet;
    }
}
