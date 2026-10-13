package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/OriginatorInfoGenerator.class */
public class OriginatorInfoGenerator {
    private final List origCerts;
    private final List origCRLs;

    public OriginatorInfoGenerator(X509CertificateHolder x509CertificateHolder) {
        this.origCerts = new ArrayList(1);
        this.origCRLs = null;
        this.origCerts.add(x509CertificateHolder.toASN1Structure());
    }

    public OriginatorInfoGenerator(Store store) throws CMSException {
        this(store, null);
    }

    public OriginatorInfoGenerator(Store store, Store store2) throws CMSException {
        if (store != null) {
            this.origCerts = CMSUtils.getCertificatesFromStore(store);
        } else {
            this.origCerts = null;
        }
        if (store2 != null) {
            this.origCRLs = CMSUtils.getCRLsFromStore(store2);
        } else {
            this.origCRLs = null;
        }
    }

    public OriginatorInformation generate() {
        return new OriginatorInformation(new OriginatorInfo(this.origCerts == null ? null : CMSUtils.createDerSetFromList(this.origCerts), this.origCRLs == null ? null : CMSUtils.createDerSetFromList(this.origCRLs)));
    }
}
