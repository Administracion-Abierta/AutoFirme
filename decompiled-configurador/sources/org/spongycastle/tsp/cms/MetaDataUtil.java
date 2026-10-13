package org.spongycastle.tsp.cms;

import java.io.IOException;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1String;
import org.spongycastle.asn1.cms.Attributes;
import org.spongycastle.asn1.cms.MetaData;
import org.spongycastle.cms.CMSException;
import org.spongycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/tsp/cms/MetaDataUtil.class */
class MetaDataUtil {
    private final MetaData metaData;

    MetaDataUtil(MetaData metaData) {
        this.metaData = metaData;
    }

    void initialiseMessageImprintDigestCalculator(DigestCalculator calculator) throws CMSException {
        if (this.metaData != null && this.metaData.isHashProtected()) {
            try {
                calculator.getOutputStream().write(this.metaData.getEncoded(ASN1Encoding.DER));
            } catch (IOException e) {
                throw new CMSException("unable to initialise calculator from metaData: " + e.getMessage(), e);
            }
        }
    }

    String getFileName() {
        if (this.metaData != null) {
            return convertString(this.metaData.getFileName());
        }
        return null;
    }

    String getMediaType() {
        if (this.metaData != null) {
            return convertString(this.metaData.getMediaType());
        }
        return null;
    }

    Attributes getOtherMetaData() {
        if (this.metaData != null) {
            return this.metaData.getOtherMetaData();
        }
        return null;
    }

    private String convertString(ASN1String s) {
        if (s != null) {
            return s.toString();
        }
        return null;
    }
}
