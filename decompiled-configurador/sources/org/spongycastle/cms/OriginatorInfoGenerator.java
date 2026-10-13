package org.spongycastle.cms;

import java.util.ArrayList;
import java.util.List;
import org.spongycastle.asn1.cms.OriginatorInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.util.Store;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/OriginatorInfoGenerator.class */
public class OriginatorInfoGenerator {
    private final List origCerts;
    private final List origCRLs;

    public OriginatorInfoGenerator(X509CertificateHolder origCert) {
        this.origCerts = new ArrayList(1);
        this.origCRLs = null;
        this.origCerts.add(origCert.toASN1Structure());
    }

    public OriginatorInfoGenerator(Store origCerts) throws CMSException {
        this(origCerts, null);
    }

    public OriginatorInfoGenerator(Store origCerts, Store origCRLs) throws CMSException {
        this.origCerts = CMSUtils.getCertificatesFromStore(origCerts);
        if (origCRLs != null) {
            this.origCRLs = CMSUtils.getCRLsFromStore(origCRLs);
        } else {
            this.origCRLs = null;
        }
    }

    public OriginatorInformation generate() {
        if (this.origCRLs != null) {
            return new OriginatorInformation(new OriginatorInfo(CMSUtils.createDerSetFromList(this.origCerts), CMSUtils.createDerSetFromList(this.origCRLs)));
        }
        return new OriginatorInformation(new OriginatorInfo(CMSUtils.createDerSetFromList(this.origCerts), null));
    }
}
