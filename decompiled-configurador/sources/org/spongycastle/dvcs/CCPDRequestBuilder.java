package org.spongycastle.dvcs;

import org.spongycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.spongycastle.asn1.dvcs.Data;
import org.spongycastle.asn1.dvcs.ServiceType;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/dvcs/CCPDRequestBuilder.class */
public class CCPDRequestBuilder extends DVCSRequestBuilder {
    public CCPDRequestBuilder() {
        super(new DVCSRequestInformationBuilder(ServiceType.CCPD));
    }

    public DVCSRequest build(MessageImprint messageImprint) throws DVCSException {
        Data data = new Data(messageImprint.toASN1Structure());
        return createDVCRequest(data);
    }
}
