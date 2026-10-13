package org.bouncycastle.tsp.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.cms.Attributes;
import org.bouncycastle.asn1.cms.MetaData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/cms/MetaDataUtil.class */
class MetaDataUtil {
    private final MetaData metaData;

    MetaDataUtil(MetaData metaData) {
        this.metaData = metaData;
    }

    void initialiseMessageImprintDigestCalculator(DigestCalculator digestCalculator) throws CMSException {
        if (this.metaData == null || !this.metaData.isHashProtected()) {
            return;
        }
        try {
            digestCalculator.getOutputStream().write(this.metaData.getEncoded("DER"));
        } catch (IOException e) {
            throw new CMSException("unable to initialise calculator from metaData: " + e.getMessage(), e);
        }
    }

    String getFileName() {
        if (this.metaData != null) {
            return convertString(this.metaData.getFileNameUTF8());
        }
        return null;
    }

    String getMediaType() {
        if (this.metaData != null) {
            return convertString(this.metaData.getMediaTypeIA5());
        }
        return null;
    }

    Attributes getOtherMetaData() {
        if (this.metaData != null) {
            return this.metaData.getOtherMetaData();
        }
        return null;
    }

    private String convertString(ASN1String aSN1String) {
        if (aSN1String != null) {
            return aSN1String.toString();
        }
        return null;
    }
}
