package org.spongycastle.jce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.spongycastle.jce.MultiCertStoreParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/provider/MultiCertStoreSpi.class */
public class MultiCertStoreSpi extends CertStoreSpi {
    private MultiCertStoreParameters params;

    public MultiCertStoreSpi(CertStoreParameters params) throws InvalidAlgorithmParameterException {
        super(params);
        if (!(params instanceof MultiCertStoreParameters)) {
            throw new InvalidAlgorithmParameterException("org.spongycastle.jce.provider.MultiCertStoreSpi: parameter must be a MultiCertStoreParameters object\n" + params.toString());
        }
        this.params = (MultiCertStoreParameters) params;
    }

    @Override // java.security.cert.CertStoreSpi
    public Collection engineGetCertificates(CertSelector certSelector) throws CertStoreException {
        boolean searchAllStores = this.params.getSearchAllStores();
        List allCerts = searchAllStores ? new ArrayList() : Collections.EMPTY_LIST;
        for (CertStore store : this.params.getCertStores()) {
            Collection<? extends Certificate> certificates = store.getCertificates(certSelector);
            if (searchAllStores) {
                allCerts.addAll(certificates);
            } else if (!certificates.isEmpty()) {
                return certificates;
            }
        }
        return allCerts;
    }

    @Override // java.security.cert.CertStoreSpi
    public Collection engineGetCRLs(CRLSelector crlSelector) throws CertStoreException {
        boolean searchAllStores = this.params.getSearchAllStores();
        List allCRLs = searchAllStores ? new ArrayList() : Collections.EMPTY_LIST;
        for (CertStore store : this.params.getCertStores()) {
            Collection<? extends CRL> cRLs = store.getCRLs(crlSelector);
            if (searchAllStores) {
                allCRLs.addAll(cRLs);
            } else if (!cRLs.isEmpty()) {
                return cRLs;
            }
        }
        return allCRLs;
    }
}
