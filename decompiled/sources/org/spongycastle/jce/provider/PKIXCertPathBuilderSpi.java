package org.spongycastle.jce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathBuilderSpi;
import java.security.cert.CertPathParameters;
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
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.jcajce.PKIXCertStore;
import org.spongycastle.jcajce.PKIXCertStoreSelector;
import org.spongycastle.jcajce.PKIXExtendedBuilderParameters;
import org.spongycastle.jcajce.PKIXExtendedParameters;
import org.spongycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.spongycastle.jce.exception.ExtCertPathBuilderException;
import org.spongycastle.x509.ExtendedPKIXBuilderParameters;
import org.spongycastle.x509.ExtendedPKIXParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/provider/PKIXCertPathBuilderSpi.class */
public class PKIXCertPathBuilderSpi extends CertPathBuilderSpi {
    private Exception certPathException;

    @Override // java.security.cert.CertPathBuilderSpi
    public CertPathBuilderResult engineBuild(CertPathParameters params) throws CertPathBuilderException, InvalidAlgorithmParameterException {
        PKIXExtendedBuilderParameters paramsPKIX;
        PKIXExtendedBuilderParameters.Builder paramsBldrPKIXBldr;
        if (params instanceof PKIXBuilderParameters) {
            PKIXExtendedParameters.Builder paramsPKIXBldr = new PKIXExtendedParameters.Builder((PKIXBuilderParameters) params);
            if (params instanceof ExtendedPKIXParameters) {
                ExtendedPKIXBuilderParameters extendedPKIXBuilderParameters = (ExtendedPKIXBuilderParameters) params;
                Iterator it = extendedPKIXBuilderParameters.getAdditionalStores().iterator();
                while (it.hasNext()) {
                    paramsPKIXBldr.addCertificateStore((PKIXCertStore) it.next());
                }
                paramsBldrPKIXBldr = new PKIXExtendedBuilderParameters.Builder(paramsPKIXBldr.build());
                paramsBldrPKIXBldr.addExcludedCerts(extendedPKIXBuilderParameters.getExcludedCerts());
                paramsBldrPKIXBldr.setMaxPathLength(extendedPKIXBuilderParameters.getMaxPathLength());
            } else {
                paramsBldrPKIXBldr = new PKIXExtendedBuilderParameters.Builder((PKIXBuilderParameters) params);
            }
            paramsPKIX = paramsBldrPKIXBldr.build();
        } else if (params instanceof PKIXExtendedBuilderParameters) {
            paramsPKIX = (PKIXExtendedBuilderParameters) params;
        } else {
            throw new InvalidAlgorithmParameterException("Parameters must be an instance of " + PKIXBuilderParameters.class.getName() + " or " + PKIXExtendedBuilderParameters.class.getName() + ".");
        }
        List certPathList = new ArrayList();
        PKIXCertStoreSelector certSelect = paramsPKIX.getBaseParameters().getTargetConstraints();
        try {
            Collection targets = CertPathValidatorUtilities.findCertificates(certSelect, paramsPKIX.getBaseParameters().getCertificateStores());
            targets.addAll(CertPathValidatorUtilities.findCertificates(certSelect, paramsPKIX.getBaseParameters().getCertStores()));
            if (targets.isEmpty()) {
                throw new CertPathBuilderException("No certificate found matching targetContraints.");
            }
            CertPathBuilderResult result = null;
            Iterator targetIter = targets.iterator();
            while (targetIter.hasNext() && result == null) {
                X509Certificate cert = (X509Certificate) targetIter.next();
                result = build(cert, paramsPKIX, certPathList);
            }
            if (result == null && this.certPathException != null) {
                if (this.certPathException instanceof AnnotatedException) {
                    throw new CertPathBuilderException(this.certPathException.getMessage(), this.certPathException.getCause());
                }
                throw new CertPathBuilderException("Possible certificate chain could not be validated.", this.certPathException);
            }
            if (result == null && this.certPathException == null) {
                throw new CertPathBuilderException("Unable to find certificate chain.");
            }
            return result;
        } catch (AnnotatedException e) {
            throw new ExtCertPathBuilderException("Error finding target certificate.", e);
        }
    }

    /* JADX WARN: Code duplicated, block: B:59:0x0194  */
    protected CertPathBuilderResult build(X509Certificate tbvCert, PKIXExtendedBuilderParameters pkixParams, List tbvPath) {
        if (tbvPath.contains(tbvCert) || pkixParams.getExcludedCerts().contains(tbvCert)) {
            return null;
        }
        if (pkixParams.getMaxPathLength() != -1 && tbvPath.size() - 1 > pkixParams.getMaxPathLength()) {
            return null;
        }
        tbvPath.add(tbvCert);
        CertPathBuilderResult builderResult = null;
        try {
            CertificateFactory cFact = new CertificateFactory();
            PKIXCertPathValidatorSpi validator = new PKIXCertPathValidatorSpi();
            try {
                if (CertPathValidatorUtilities.isIssuerTrustAnchor(tbvCert, pkixParams.getBaseParameters().getTrustAnchors(), pkixParams.getBaseParameters().getSigProvider())) {
                    try {
                        CertPath certPath = cFact.engineGenerateCertPath(tbvPath);
                        try {
                            PKIXCertPathValidatorResult result = (PKIXCertPathValidatorResult) validator.engineValidate(certPath, pkixParams);
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
                            builderResult = build(issuer, pkixParams, tbvPath);
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
                this.certPathException = e5;
            }
            this.certPathException = e5;
            if (builderResult == null) {
                tbvPath.remove(tbvCert);
            }
            return builderResult;
        } catch (Exception e6) {
            throw new RuntimeException("Exception creating support classes.");
        }
    }
}
