package org.spongycastle.jce.provider;

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
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.jcajce.PKIXCertStoreSelector;
import org.spongycastle.jcajce.PKIXExtendedBuilderParameters;
import org.spongycastle.jce.exception.ExtCertPathBuilderException;
import org.spongycastle.util.Selector;
import org.spongycastle.util.Store;
import org.spongycastle.util.StoreException;
import org.spongycastle.x509.ExtendedPKIXBuilderParameters;
import org.spongycastle.x509.ExtendedPKIXParameters;
import org.spongycastle.x509.X509AttributeCertStoreSelector;
import org.spongycastle.x509.X509AttributeCertificate;
import org.spongycastle.x509.X509CertStoreSelector;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/provider/PKIXAttrCertPathBuilderSpi.class */
public class PKIXAttrCertPathBuilderSpi extends CertPathBuilderSpi {
    private Exception certPathException;

    @Override // java.security.cert.CertPathBuilderSpi
    public CertPathBuilderResult engineBuild(CertPathParameters params) throws CertPathBuilderException, InvalidAlgorithmParameterException {
        PKIXExtendedBuilderParameters paramsPKIX;
        if (!(params instanceof PKIXBuilderParameters) && !(params instanceof ExtendedPKIXBuilderParameters) && !(params instanceof PKIXExtendedBuilderParameters)) {
            throw new InvalidAlgorithmParameterException("Parameters must be an instance of " + PKIXBuilderParameters.class.getName() + " or " + PKIXExtendedBuilderParameters.class.getName() + ".");
        }
        List targetStores = new ArrayList();
        if (params instanceof PKIXBuilderParameters) {
            PKIXExtendedBuilderParameters.Builder paramsPKIXBldr = new PKIXExtendedBuilderParameters.Builder((PKIXBuilderParameters) params);
            if (params instanceof ExtendedPKIXParameters) {
                ExtendedPKIXBuilderParameters extendedPKIXBuilderParameters = (ExtendedPKIXBuilderParameters) params;
                paramsPKIXBldr.addExcludedCerts(extendedPKIXBuilderParameters.getExcludedCerts());
                paramsPKIXBldr.setMaxPathLength(extendedPKIXBuilderParameters.getMaxPathLength());
                targetStores = extendedPKIXBuilderParameters.getStores();
            }
            paramsPKIX = paramsPKIXBldr.build();
        } else {
            paramsPKIX = (PKIXExtendedBuilderParameters) params;
        }
        List certPathList = new ArrayList();
        Selector certSelect = paramsPKIX.getBaseParameters().getTargetConstraints();
        if (!(certSelect instanceof X509AttributeCertStoreSelector)) {
            throw new CertPathBuilderException("TargetConstraints must be an instance of " + X509AttributeCertStoreSelector.class.getName() + " for " + getClass().getName() + " class.");
        }
        try {
            Collection targets = findCertificates((X509AttributeCertStoreSelector) certSelect, targetStores);
            if (targets.isEmpty()) {
                throw new CertPathBuilderException("No attribute certificate found matching targetContraints.");
            }
            CertPathBuilderResult result = null;
            Iterator targetIter = targets.iterator();
            while (targetIter.hasNext() && result == null) {
                X509AttributeCertificate cert = (X509AttributeCertificate) targetIter.next();
                X509CertStoreSelector selector = new X509CertStoreSelector();
                Principal[] principals = cert.getIssuer().getPrincipals();
                Set issuers = new HashSet();
                for (int i = 0; i < principals.length; i++) {
                    try {
                        if (principals[i] instanceof X500Principal) {
                            selector.setSubject(((X500Principal) principals[i]).getEncoded());
                        }
                        PKIXCertStoreSelector<? extends Certificate> pKIXCertStoreSelectorBuild = new PKIXCertStoreSelector.Builder(selector).build();
                        issuers.addAll(CertPathValidatorUtilities.findCertificates(pKIXCertStoreSelectorBuild, paramsPKIX.getBaseParameters().getCertStores()));
                        issuers.addAll(CertPathValidatorUtilities.findCertificates(pKIXCertStoreSelectorBuild, paramsPKIX.getBaseParameters().getCertificateStores()));
                    } catch (IOException e) {
                        throw new ExtCertPathBuilderException("cannot encode X500Principal.", e);
                    } catch (AnnotatedException e2) {
                        throw new ExtCertPathBuilderException("Public key certificate for attribute certificate cannot be searched.", e2);
                    }
                }
                if (issuers.isEmpty()) {
                    throw new CertPathBuilderException("Public key certificate for attribute certificate cannot be found.");
                }
                Iterator it = issuers.iterator();
                while (it.hasNext() && result == null) {
                    result = build(cert, (X509Certificate) it.next(), paramsPKIX, certPathList);
                }
            }
            if (result == null && this.certPathException != null) {
                throw new ExtCertPathBuilderException("Possible certificate chain could not be validated.", this.certPathException);
            }
            if (result == null && this.certPathException == null) {
                throw new CertPathBuilderException("Unable to find certificate chain.");
            }
            return result;
        } catch (AnnotatedException e3) {
            throw new ExtCertPathBuilderException("Error finding target attribute certificate.", e3);
        }
    }

    /* JADX WARN: Code duplicated, block: B:61:0x01b0  */
    private CertPathBuilderResult build(X509AttributeCertificate attrCert, X509Certificate tbvCert, PKIXExtendedBuilderParameters pkixParams, List tbvPath) {
        if (tbvPath.contains(tbvCert) || pkixParams.getExcludedCerts().contains(tbvCert)) {
            return null;
        }
        if (pkixParams.getMaxPathLength() != -1 && tbvPath.size() - 1 > pkixParams.getMaxPathLength()) {
            return null;
        }
        tbvPath.add(tbvCert);
        CertPathBuilderResult builderResult = null;
        try {
            CertificateFactory cFact = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID, BouncyCastleProvider.PROVIDER_NAME);
            CertPathValidator validator = CertPathValidator.getInstance("RFC3281", BouncyCastleProvider.PROVIDER_NAME);
            try {
                if (CertPathValidatorUtilities.isIssuerTrustAnchor(tbvCert, pkixParams.getBaseParameters().getTrustAnchors(), pkixParams.getBaseParameters().getSigProvider())) {
                    try {
                        CertPath certPath = cFact.generateCertPath((List<? extends Certificate>) tbvPath);
                        try {
                            PKIXCertPathValidatorResult result = (PKIXCertPathValidatorResult) validator.validate(certPath, pkixParams);
                            return new PKIXCertPathBuilderResult(certPath, result.getTrustAnchor(), result.getPolicyTree(), result.getPublicKey());
                        } catch (Exception e) {
                            throw new AnnotatedException("Certification path could not be validated.", e);
                        }
                    } catch (Exception e2) {
                        throw new AnnotatedException("Certification path could not be constructed from certificate list.", e2);
                    }
                }
                List stores = new ArrayList();
                stores.addAll(pkixParams.getBaseParameters().getCertificateStores());
                try {
                    stores.addAll(CertPathValidatorUtilities.getAdditionalStoresFromAltNames(tbvCert.getExtensionValue(Extension.issuerAlternativeName.getId()), pkixParams.getBaseParameters().getNamedCertificateStoreMap()));
                    Collection issuers = new HashSet();
                    try {
                        issuers.addAll(CertPathValidatorUtilities.findIssuerCerts(tbvCert, pkixParams.getBaseParameters().getCertStores(), stores));
                        if (issuers.isEmpty()) {
                            throw new AnnotatedException("No issuer certificate for certificate in certification path found.");
                        }
                        Iterator it = issuers.iterator();
                        while (it.hasNext() && builderResult == null) {
                            X509Certificate issuer = (X509Certificate) it.next();
                            if (!issuer.getIssuerX500Principal().equals(issuer.getSubjectX500Principal())) {
                                builderResult = build(attrCert, issuer, pkixParams, tbvPath);
                            }
                        }
                        if (builderResult == null) {
                            tbvPath.remove(tbvCert);
                        }
                        return builderResult;
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
            if (builderResult == null) {
                tbvPath.remove(tbvCert);
            }
            return builderResult;
        } catch (Exception e6) {
            throw new RuntimeException("Exception creating support classes.");
        }
    }

    protected static Collection findCertificates(X509AttributeCertStoreSelector certSelect, List certStores) throws AnnotatedException {
        Set certs = new HashSet();
        for (Object obj : certStores) {
            if (obj instanceof Store) {
                Store certStore = (Store) obj;
                try {
                    certs.addAll(certStore.getMatches(certSelect));
                } catch (StoreException e) {
                    throw new AnnotatedException("Problem while picking certificates from X.509 store.", e);
                }
            }
        }
        return certs;
    }
}
