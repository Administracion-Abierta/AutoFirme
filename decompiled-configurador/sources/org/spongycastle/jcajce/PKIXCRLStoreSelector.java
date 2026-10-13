package org.spongycastle.jcajce;

import java.math.BigInteger;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509Certificate;
import java.util.Collection;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Selector;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/PKIXCRLStoreSelector.class */
public class PKIXCRLStoreSelector<T extends CRL> implements Selector<T> {
    private final CRLSelector baseSelector;
    private final boolean deltaCRLIndicator;
    private final boolean completeCRLEnabled;
    private final BigInteger maxBaseCRLNumber;
    private final byte[] issuingDistributionPoint;
    private final boolean issuingDistributionPointEnabled;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/PKIXCRLStoreSelector$Builder.class */
    public static class Builder {
        private final CRLSelector baseSelector;
        private boolean deltaCRLIndicator = false;
        private boolean completeCRLEnabled = false;
        private BigInteger maxBaseCRLNumber = null;
        private byte[] issuingDistributionPoint = null;
        private boolean issuingDistributionPointEnabled = false;

        public Builder(CRLSelector crlSelector) {
            this.baseSelector = (CRLSelector) crlSelector.clone();
        }

        public Builder setCompleteCRLEnabled(boolean completeCRLEnabled) {
            this.completeCRLEnabled = completeCRLEnabled;
            return this;
        }

        public Builder setDeltaCRLIndicatorEnabled(boolean deltaCRLIndicator) {
            this.deltaCRLIndicator = deltaCRLIndicator;
            return this;
        }

        public void setMaxBaseCRLNumber(BigInteger maxBaseCRLNumber) {
            this.maxBaseCRLNumber = maxBaseCRLNumber;
        }

        public void setIssuingDistributionPointEnabled(boolean issuingDistributionPointEnabled) {
            this.issuingDistributionPointEnabled = issuingDistributionPointEnabled;
        }

        public void setIssuingDistributionPoint(byte[] issuingDistributionPoint) {
            this.issuingDistributionPoint = Arrays.clone(issuingDistributionPoint);
        }

        public PKIXCRLStoreSelector<? extends CRL> build() {
            return new PKIXCRLStoreSelector<>(this);
        }
    }

    private PKIXCRLStoreSelector(Builder baseBuilder) {
        this.baseSelector = baseBuilder.baseSelector;
        this.deltaCRLIndicator = baseBuilder.deltaCRLIndicator;
        this.completeCRLEnabled = baseBuilder.completeCRLEnabled;
        this.maxBaseCRLNumber = baseBuilder.maxBaseCRLNumber;
        this.issuingDistributionPoint = baseBuilder.issuingDistributionPoint;
        this.issuingDistributionPointEnabled = baseBuilder.issuingDistributionPointEnabled;
    }

    public boolean isIssuingDistributionPointEnabled() {
        return this.issuingDistributionPointEnabled;
    }

    @Override // org.spongycastle.util.Selector
    public boolean match(CRL obj) {
        if (!(obj instanceof X509CRL)) {
            return this.baseSelector.match(obj);
        }
        X509CRL crl = (X509CRL) obj;
        ASN1Integer dci = null;
        try {
            byte[] bytes = crl.getExtensionValue(Extension.deltaCRLIndicator.getId());
            if (bytes != null) {
                dci = ASN1Integer.getInstance(ASN1OctetString.getInstance(bytes).getOctets());
            }
            if (isDeltaCRLIndicatorEnabled() && dci == null) {
                return false;
            }
            if (isCompleteCRLEnabled() && dci != null) {
                return false;
            }
            if (dci != null && this.maxBaseCRLNumber != null && dci.getPositiveValue().compareTo(this.maxBaseCRLNumber) == 1) {
                return false;
            }
            if (this.issuingDistributionPointEnabled) {
                byte[] idp = crl.getExtensionValue(Extension.issuingDistributionPoint.getId());
                if (this.issuingDistributionPoint == null) {
                    if (idp != null) {
                        return false;
                    }
                } else if (!Arrays.areEqual(idp, this.issuingDistributionPoint)) {
                    return false;
                }
            }
            return this.baseSelector.match(obj);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDeltaCRLIndicatorEnabled() {
        return this.deltaCRLIndicator;
    }

    @Override // org.spongycastle.util.Selector
    public Object clone() {
        return this;
    }

    public boolean isCompleteCRLEnabled() {
        return this.completeCRLEnabled;
    }

    public BigInteger getMaxBaseCRLNumber() {
        return this.maxBaseCRLNumber;
    }

    public byte[] getIssuingDistributionPoint() {
        return Arrays.clone(this.issuingDistributionPoint);
    }

    public X509Certificate getCertificateChecking() {
        if (this.baseSelector instanceof X509CRLSelector) {
            return ((X509CRLSelector) this.baseSelector).getCertificateChecking();
        }
        return null;
    }

    public static Collection<? extends CRL> getCRLs(PKIXCRLStoreSelector selector, CertStore certStore) throws CertStoreException {
        return certStore.getCRLs(new SelectorClone(selector));
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/PKIXCRLStoreSelector$SelectorClone.class */
    private static class SelectorClone extends X509CRLSelector {
        private final PKIXCRLStoreSelector selector;

        SelectorClone(PKIXCRLStoreSelector selector) {
            this.selector = selector;
            if (selector.baseSelector instanceof X509CRLSelector) {
                X509CRLSelector baseSelector = (X509CRLSelector) selector.baseSelector;
                setCertificateChecking(baseSelector.getCertificateChecking());
                setDateAndTime(baseSelector.getDateAndTime());
                setIssuers(baseSelector.getIssuers());
                setMinCRLNumber(baseSelector.getMinCRL());
                setMaxCRLNumber(baseSelector.getMaxCRL());
            }
        }

        @Override // java.security.cert.X509CRLSelector, java.security.cert.CRLSelector
        public boolean match(CRL crl) {
            if (this.selector == null) {
                return crl != null;
            }
            return this.selector.match(crl);
        }
    }
}
