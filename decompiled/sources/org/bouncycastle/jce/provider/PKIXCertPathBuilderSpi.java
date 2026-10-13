package org.bouncycastle.jce.provider;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.jcajce.PKIXCertStore;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.bouncycastle.x509.ExtendedPKIXBuilderParameters;
import org.bouncycastle.x509.ExtendedPKIXParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/provider/PKIXCertPathBuilderSpi.class */
public class PKIXCertPathBuilderSpi extends CertPathBuilderSpi {
    private final boolean isForCRLCheck;
    private Exception certPathException;

    public PKIXCertPathBuilderSpi() {
        this(false);
    }

    PKIXCertPathBuilderSpi(boolean z) {
        this.isForCRLCheck = z;
    }

    @Override // java.security.cert.CertPathBuilderSpi
    public CertPathBuilderResult engineBuild(CertPathParameters certPathParameters) throws CertPathBuilderException, InvalidAlgorithmParameterException {
        PKIXExtendedBuilderParameters pKIXExtendedBuilderParametersBuild;
        PKIXExtendedBuilderParameters.Builder builder;
        if (certPathParameters instanceof PKIXBuilderParameters) {
            PKIXExtendedParameters.Builder builder2 = new PKIXExtendedParameters.Builder((PKIXBuilderParameters) certPathParameters);
            if (certPathParameters instanceof ExtendedPKIXParameters) {
                ExtendedPKIXBuilderParameters extendedPKIXBuilderParameters = (ExtendedPKIXBuilderParameters) certPathParameters;
                Iterator it = extendedPKIXBuilderParameters.getAdditionalStores().iterator();
                while (it.hasNext()) {
                    builder2.addCertificateStore((PKIXCertStore) it.next());
                }
                builder = new PKIXExtendedBuilderParameters.Builder(builder2.build());
                builder.addExcludedCerts(extendedPKIXBuilderParameters.getExcludedCerts());
                builder.setMaxPathLength(extendedPKIXBuilderParameters.getMaxPathLength());
            } else {
                builder = new PKIXExtendedBuilderParameters.Builder((PKIXBuilderParameters) certPathParameters);
            }
            pKIXExtendedBuilderParametersBuild = builder.build();
        } else {
            if (!(certPathParameters instanceof PKIXExtendedBuilderParameters)) {
                throw new InvalidAlgorithmParameterException("Parameters must be an instance of " + PKIXBuilderParameters.class.getName() + " or " + PKIXExtendedBuilderParameters.class.getName() + ".");
            }
            pKIXExtendedBuilderParametersBuild = (PKIXExtendedBuilderParameters) certPathParameters;
        }
        ArrayList arrayList = new ArrayList();
        CertPathBuilderResult certPathBuilderResultBuild = null;
        Iterator it2 = CertPathValidatorUtilities.findTargets(pKIXExtendedBuilderParametersBuild).iterator();
        while (it2.hasNext() && certPathBuilderResultBuild == null) {
            certPathBuilderResultBuild = build((X509Certificate) it2.next(), pKIXExtendedBuilderParametersBuild, arrayList);
        }
        if (certPathBuilderResultBuild == null && this.certPathException != null) {
            if (this.certPathException instanceof AnnotatedException) {
                throw new CertPathBuilderException(this.certPathException.getMessage(), this.certPathException.getCause());
            }
            throw new CertPathBuilderException("Possible certificate chain could not be validated.", this.certPathException);
        }
        if (certPathBuilderResultBuild == null && this.certPathException == null) {
            throw new CertPathBuilderException("Unable to find certificate chain.");
        }
        return certPathBuilderResultBuild;
    }

    /* JADX WARN: Code duplicated, block: B:59:0x019b  */
    protected CertPathBuilderResult build(X509Certificate x509Certificate, PKIXExtendedBuilderParameters pKIXExtendedBuilderParameters, List list) {
        if (list.contains(x509Certificate) || pKIXExtendedBuilderParameters.getExcludedCerts().contains(x509Certificate)) {
            return null;
        }
        if (pKIXExtendedBuilderParameters.getMaxPathLength() != -1 && list.size() - 1 > pKIXExtendedBuilderParameters.getMaxPathLength()) {
            return null;
        }
        list.add(x509Certificate);
        CertPathBuilderResult certPathBuilderResultBuild = null;
        try {
            CertificateFactory certificateFactory = new CertificateFactory();
            PKIXCertPathValidatorSpi pKIXCertPathValidatorSpi = new PKIXCertPathValidatorSpi(this.isForCRLCheck);
            try {
                if (CertPathValidatorUtilities.isIssuerTrustAnchor(x509Certificate, pKIXExtendedBuilderParameters.getBaseParameters().getTrustAnchors(), pKIXExtendedBuilderParameters.getBaseParameters().getSigProvider())) {
                    try {
                        CertPath certPathEngineGenerateCertPath = certificateFactory.engineGenerateCertPath(list);
                        try {
                            PKIXCertPathValidatorResult pKIXCertPathValidatorResult = (PKIXCertPathValidatorResult) pKIXCertPathValidatorSpi.engineValidate(certPathEngineGenerateCertPath, pKIXExtendedBuilderParameters);
                            return new PKIXCertPathBuilderResult(certPathEngineGenerateCertPath, pKIXCertPathValidatorResult.getTrustAnchor(), pKIXCertPathValidatorResult.getPolicyTree(), pKIXCertPathValidatorResult.getPublicKey());
                        } catch (Exception e) {
                            throw new AnnotatedException("Certification path could not be validated.", e);
                        }
                    } catch (Exception e2) {
                        throw new AnnotatedException("Certification path could not be constructed from certificate list.", e2);
                    }
                }
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(pKIXExtendedBuilderParameters.getBaseParameters().getCertificateStores());
                try {
                    arrayList.addAll(CertPathValidatorUtilities.getAdditionalStoresFromAltNames(x509Certificate.getExtensionValue(Extension.issuerAlternativeName.getId()), pKIXExtendedBuilderParameters.getBaseParameters().getNamedCertificateStoreMap()));
                    HashSet hashSet = new HashSet();
                    try {
                        hashSet.addAll(CertPathValidatorUtilities.findIssuerCerts(x509Certificate, pKIXExtendedBuilderParameters.getBaseParameters().getCertStores(), arrayList));
                        if (hashSet.isEmpty()) {
                            throw new AnnotatedException("No issuer certificate for certificate in certification path found.");
                        }
                        Iterator it = hashSet.iterator();
                        while (it.hasNext() && certPathBuilderResultBuild == null) {
                            certPathBuilderResultBuild = build((X509Certificate) it.next(), pKIXExtendedBuilderParameters, list);
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
                this.certPathException = e5;
            }
            this.certPathException = e5;
            if (certPathBuilderResultBuild == null) {
                list.remove(x509Certificate);
            }
            return certPathBuilderResultBuild;
        } catch (Exception e6) {
            throw new RuntimeException("Exception creating support classes.");
        }
    }
}
