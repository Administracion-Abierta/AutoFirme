package org.spongycastle.dvcs;

import java.io.IOException;
import java.util.Date;
import org.spongycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.spongycastle.asn1.dvcs.DVCSTime;
import org.spongycastle.asn1.dvcs.Data;
import org.spongycastle.asn1.dvcs.ServiceType;
import org.spongycastle.cms.CMSSignedData;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/dvcs/VSDRequestBuilder.class */
public class VSDRequestBuilder extends DVCSRequestBuilder {
    public VSDRequestBuilder() {
        super(new DVCSRequestInformationBuilder(ServiceType.VSD));
    }

    public void setRequestTime(Date requestTime) {
        this.requestInformationBuilder.setRequestTime(new DVCSTime(requestTime));
    }

    public DVCSRequest build(CMSSignedData document) throws DVCSException {
        try {
            Data data = new Data(document.getEncoded());
            return createDVCRequest(data);
        } catch (IOException e) {
            throw new DVCSException("Failed to encode CMS signed data", e);
        }
    }
}
