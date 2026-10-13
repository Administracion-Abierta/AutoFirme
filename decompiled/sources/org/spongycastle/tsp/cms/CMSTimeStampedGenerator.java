package org.spongycastle.tsp.cms;

import java.net.URI;
import org.spongycastle.asn1.ASN1Boolean;
import org.spongycastle.asn1.DERIA5String;
import org.spongycastle.asn1.DERUTF8String;
import org.spongycastle.asn1.cms.Attributes;
import org.spongycastle.asn1.cms.MetaData;
import org.spongycastle.cms.CMSException;
import org.spongycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/tsp/cms/CMSTimeStampedGenerator.class */
public class CMSTimeStampedGenerator {
    protected MetaData metaData;
    protected URI dataUri;

    public void setDataUri(URI dataUri) {
        this.dataUri = dataUri;
    }

    public void setMetaData(boolean hashProtected, String fileName, String mediaType) {
        setMetaData(hashProtected, fileName, mediaType, (Attributes) null);
    }

    public void setMetaData(boolean hashProtected, String fileName, String mediaType, Attributes attributes) {
        DERUTF8String asn1FileName = null;
        if (fileName != null) {
            asn1FileName = new DERUTF8String(fileName);
        }
        DERIA5String asn1MediaType = null;
        if (mediaType != null) {
            asn1MediaType = new DERIA5String(mediaType);
        }
        setMetaData(hashProtected, asn1FileName, asn1MediaType, attributes);
    }

    private void setMetaData(boolean hashProtected, DERUTF8String fileName, DERIA5String mediaType, Attributes attributes) {
        this.metaData = new MetaData(ASN1Boolean.getInstance(hashProtected), fileName, mediaType, attributes);
    }

    public void initialiseMessageImprintDigestCalculator(DigestCalculator calculator) throws CMSException {
        MetaDataUtil util = new MetaDataUtil(this.metaData);
        util.initialiseMessageImprintDigestCalculator(calculator);
    }
}
