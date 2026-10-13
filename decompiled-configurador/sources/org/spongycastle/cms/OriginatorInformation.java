package org.spongycastle.cms;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.cms.OriginatorInfo;
import org.spongycastle.asn1.x509.Certificate;
import org.spongycastle.asn1.x509.CertificateList;
import org.spongycastle.cert.X509CRLHolder;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.util.CollectionStore;
import org.spongycastle.util.Store;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/OriginatorInformation.class */
public class OriginatorInformation {
    private OriginatorInfo originatorInfo;

    OriginatorInformation(OriginatorInfo originatorInfo) {
        this.originatorInfo = originatorInfo;
    }

    public Store getCertificates() {
        ASN1Set certSet = this.originatorInfo.getCertificates();
        if (certSet != null) {
            List certList = new ArrayList(certSet.size());
            Enumeration en = certSet.getObjects();
            while (en.hasMoreElements()) {
                ASN1Primitive obj = ((ASN1Encodable) en.nextElement()).toASN1Primitive();
                if (obj instanceof ASN1Sequence) {
                    certList.add(new X509CertificateHolder(Certificate.getInstance(obj)));
                }
            }
            return new CollectionStore(certList);
        }
        return new CollectionStore(new ArrayList());
    }

    public Store getCRLs() {
        ASN1Set crlSet = this.originatorInfo.getCRLs();
        if (crlSet != null) {
            List crlList = new ArrayList(crlSet.size());
            Enumeration en = crlSet.getObjects();
            while (en.hasMoreElements()) {
                ASN1Primitive obj = ((ASN1Encodable) en.nextElement()).toASN1Primitive();
                if (obj instanceof ASN1Sequence) {
                    crlList.add(new X509CRLHolder(CertificateList.getInstance(obj)));
                }
            }
            return new CollectionStore(crlList);
        }
        return new CollectionStore(new ArrayList());
    }

    public OriginatorInfo toASN1Structure() {
        return this.originatorInfo;
    }
}
